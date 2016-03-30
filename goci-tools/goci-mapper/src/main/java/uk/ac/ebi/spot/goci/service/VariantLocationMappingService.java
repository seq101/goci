package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 07/07/2015.
 * <p>
 * Service that processes data returned from mapping pipeline. This specifically focuses on location information and
 * saves it to the database.
 */
@Service
public class VariantLocationMappingService {

    // Repositories
    private LocationRepository locationRepository;
    private VariantRepository variantRepository;
    private GenomicContextRepository genomicContextRepository;

    // Services
    private VariantQueryService variantQueryService;
    private LocationCreationService locationCreationService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    //Constructor
    @Autowired
    public VariantLocationMappingService(LocationRepository locationRepository,
                                         VariantRepository variantRepository,
                                         GenomicContextRepository genomicContextRepository,
                                         VariantQueryService variantQueryService,
                                         LocationCreationService locationCreationService) {
        this.locationRepository = locationRepository;
        this.variantRepository = variantRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.variantQueryService = variantQueryService;
        this.locationCreationService = locationCreationService;
    }

    public void storeVariantLocation(Map<String, Set<Location>> variantToLocations) {

        // Go through each rs_id and its associated locations returned from the mapping pipeline
        for (String variantExternalId : variantToLocations.keySet()) {

            Set<Location> variantLocationsFromMapping = variantToLocations.get(variantExternalId);

            // Check if the SNP exists
            Variant variantInDatabase =
                    variantQueryService.findByExternalIdIgnoreCase(variantExternalId);

            if (variantInDatabase != null) {

                // Store all new location objects
                Collection<Location> newVariantLocations = new ArrayList<>();

                for (Location variantLocationFromMapping : variantLocationsFromMapping) {

                    String chromosomeNameFromMapping = variantLocationFromMapping.getChromosomeName();
                    if (chromosomeNameFromMapping != null) {
                        chromosomeNameFromMapping = chromosomeNameFromMapping.trim();
                    }

                    String chromosomePositionFromMapping = variantLocationFromMapping.getChromosomePosition();
                    if (chromosomePositionFromMapping != null) {
                        chromosomePositionFromMapping = chromosomePositionFromMapping.trim();
                    }

                    Region regionFromMapping = variantLocationFromMapping.getRegion();
                    String regionNameFromMapping = null;
                    if (regionFromMapping != null) {
                        if (regionFromMapping.getName() != null) {
                            regionNameFromMapping = regionFromMapping.getName().trim();
                        }
                    }

                    // Check if location already exists
                    Location existingLocation =
                            locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                    chromosomeNameFromMapping,
                                    chromosomePositionFromMapping,
                                    regionNameFromMapping);


                    if (existingLocation != null) {
                        newVariantLocations.add(existingLocation);
                    }
                    // Create new location
                    else {
                        Location newLocation = locationCreationService.createLocation(chromosomeNameFromMapping,
                                                                                      chromosomePositionFromMapping,
                                                                                      regionNameFromMapping);

                        newVariantLocations.add(newLocation);
                    }
                }

                // If we have new locations then link to snp and save
                if (newVariantLocations.size() > 0) {

                    // Set new location details
                    variantInDatabase.setLocations(newVariantLocations);
                    // Update the last update date
                    variantInDatabase.setLastUpdateDate(new Date());
                    variantRepository.save(variantInDatabase);
                }
                else {getLog().warn("No new locations to add to " + variantExternalId);}

            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                getLog().error("Adding location for Variant not found in database, VARIANT_ID:" + variantExternalId);
                throw new RuntimeException("Adding location for Variant not found in database, VARIANT_ID: " + variantExternalId);

            }

        }
    }

    /**
     * Method to remove the existing locations linked to a SNP
     *
     * @param variant SNP from which to remove the associated locations
     */
    public void removeExistingVariantLocations(Variant variant) {

        // Get a list of locations currently linked to SNP
        Collection<Location> oldVariantLocations = variant.getLocations();

        if (oldVariantLocations != null && !oldVariantLocations.isEmpty()) {
            Set<Long> oldVariantLocationIds = new HashSet<>();
            for (Location oldVariantLocation : oldVariantLocations) {
                oldVariantLocationIds.add(oldVariantLocation.getId());
            }

            // Remove old locations
            variant.setLocations(new ArrayList<>());
            variantRepository.save(variant);

            // Clean-up old locations that were linked to SNP
            if (oldVariantLocationIds.size() > 0) {
                for (Long oldVariantLocationId : oldVariantLocationIds) {
                    cleanUpLocations(oldVariantLocationId);
                }
            }
        }
    }

    /**
     * Method to remove any old locations that no longer have snps or genomic contexts linked to them
     *
     * @param id Id of location object
     */
    private void cleanUpLocations(Long id) {
        List<Variant> variants =
                variantRepository.findByLocationsId(id);
        List<GenomicContext> genomicContexts = genomicContextRepository.findByLocationId(id);

        if (variants.size() == 0 && genomicContexts.size() == 0) {
            locationRepository.delete(id);
        }
    }

}
