package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.component.EnsemblMappingPipeline;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EnsemblMappingResult;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 13/08/2015.
 *
 * @author emma
 *         <p>
 *         Service that runs mapping pipeline over all or a selection of associations.
 */
@Service
public class MappingService {

    private VariantRepository variantRepository;

    // Services
    private VariantLocationMappingService variantLocationMappingService;
    private VariantGenomicContextMappingService variantGenomicContextMappingService;
    private AssociationReportService associationReportService;
    private MappingRecordService mappingRecordService;
    private VariantQueryService variantQueryService;
    private EnsemblMappingPipeline ensemblMappingPipeline;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MappingService(VariantRepository variantRepository,
                          VariantLocationMappingService variantLocationMappingService,
                          VariantGenomicContextMappingService variantGenomicContextMappingService,
                          AssociationReportService associationReportService,
                          MappingRecordService mappingRecordService,
                          VariantQueryService variantQueryService,
                          EnsemblMappingPipeline ensemblMappingPipeline) {
        this.variantRepository = variantRepository;
        this.variantLocationMappingService = variantLocationMappingService;
        this.variantGenomicContextMappingService = variantGenomicContextMappingService;
        this.associationReportService = associationReportService;
        this.mappingRecordService = mappingRecordService;
        this.variantQueryService = variantQueryService;
        this.ensemblMappingPipeline = ensemblMappingPipeline;
    }

    /**
     * Perform validation and mapping of supplied associations
     *
     * @param associations Collection of associations to map
     * @param performer    name of curator/job carrying out the mapping
     */
    @Transactional(rollbackFor = EnsemblMappingException.class)
    public void validateAndMapAssociations(Collection<Association> associations, String performer)
            throws EnsemblMappingException {

        try {
            for (Association association : associations) {
                doMapping(association);

                // Once mapping is complete, update mapping record
                getLog().debug("Update mapping record");
                mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);
            }
        }
        catch (EnsemblMappingException e) {
            throw new EnsemblMappingException("Attempt to map supplied associations failed", e);
        }
    }


    /**
     * Perform validation and mapping of all database associations
     *
     * @param associations Collection of associations to map
     * @param performer    name of curator/job carrying out the mapping
     */
    public void validateAndMapAllAssociations(Collection<Association> associations, String performer)
            throws EnsemblMappingException {

        try {
            for (Association association : associations) {
                doMapping(association);

                // Once mapping is complete, update mapping record
                getLog().debug("Update mapping record");
                mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);
            }
        }
        catch (EnsemblMappingException e) {
            throw new EnsemblMappingException("Attempt to map all associations failed", e);
        }
    }

    private void doMapping(Association association) throws EnsemblMappingException {

        getLog().info("Mapping association: " + association.getId());

        // Map to store returned location data, this is used in
        // variantLocationMappingService to process all locations linked
        // to a single snp in one go
        Map<String, Set<Location>> variantToLocationsMap = new HashMap<>();

        // Collection to store all genomic contexts
        Collection<GenomicContext> allGenomicContexts = new ArrayList<>();

        // Collection to store all errors for one association
        Collection<String> associationPipelineErrors = new ArrayList<>();

        // For each loci get the SNP and author reported genes
        Collection<Locus> studyAssociationLoci = association.getLoci();
        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();

            Collection<Variant> variantsLinkedToLocus =
                    variantQueryService.findByEffectAllelesLociId(locusId);

            Collection<Gene> authorReportedGenesLinkedToVariant = associationLocus.getAuthorReportedGenes();

            // Get gene names
            Collection<String> authorReportedGeneNamesLinkedToVariant = new ArrayList<>();
            for (Gene authorReportedGeneLinkedToVariant : authorReportedGenesLinkedToVariant) {
                authorReportedGeneNamesLinkedToVariant.add(authorReportedGeneLinkedToVariant.getGeneName().trim());
            }

            // Pass rs_id and author reported genes to mapping component
            for (Variant variantLinkedToLocus : variantsLinkedToLocus) {

                String variantExternalId = variantLinkedToLocus.getExternalId();
                EnsemblMappingResult ensemblMappingResult = new EnsemblMappingResult();

                // Try to map supplied data
                try {
                    getLog().debug("Running mapping....");
                    ensemblMappingResult =
                            ensemblMappingPipeline.run_pipeline(variantExternalId, authorReportedGeneNamesLinkedToVariant);
                }
                catch (Exception e) {
                    getLog().error("Encountered a " + e.getClass().getSimpleName() +
                                           " whilst trying to run mapping of Variant " + variantExternalId, e);
                    throw new EnsemblMappingException();
                }

                getLog().debug("Mapping complete");
                // First remove old locations and genomic contexts
                variantLocationMappingService.removeExistingVariantLocations(variantLinkedToLocus);
                variantGenomicContextMappingService.removeExistingGenomicContexts(variantLinkedToLocus);

                Collection<Location> locations = ensemblMappingResult.getLocations();
                Collection<GenomicContext> variantGenomicContexts = ensemblMappingResult.getGenomicContexts();
                ArrayList<String> pipelineErrors = ensemblMappingResult.getPipelineErrors();

                // Update functional class
                variantLinkedToLocus.setFunctionalClass(ensemblMappingResult.getFunctionalClass());
                variantLinkedToLocus.setLastUpdateDate(new Date());
                variantRepository.save(variantLinkedToLocus);

                // Store location information for SNP
                if (!locations.isEmpty()) {
                    for (Location location : locations) {

                        // Next time we see SNP, add location to set
                        // This would only occur is SNP has multiple locations
                        if (variantToLocationsMap.containsKey(variantExternalId)) {
                            variantToLocationsMap.get(variantExternalId).add(location);
                        }

                        // First time we see a SNP store the location
                        else {
                            Set<Location> variantLocation = new HashSet<>();
                            variantLocation.add(location);
                            variantToLocationsMap.put(variantExternalId, variantLocation);
                        }
                    }
                }
                else {
                    getLog().warn("Attempt to map Variant: " + variantExternalId + " returned no location details");
                    pipelineErrors.add("Attempt to map Variant: " + variantExternalId + " returned no location details");
                }

                // Store genomic context data for snp
                if (!variantGenomicContexts.isEmpty()) {
                    allGenomicContexts.addAll(variantGenomicContexts);
                }
                else {
                    getLog().warn("Attempt to map Variant: " + variantExternalId + " returned no mapped genes");
                    pipelineErrors.add("Attempt to map Variant: " + variantExternalId + " returned no mapped genes");
                }

                if (!pipelineErrors.isEmpty()) {
                    associationPipelineErrors.addAll(pipelineErrors);
                }
            }
        }

        // Create association report based on whether there is errors or not
        if (!associationPipelineErrors.isEmpty()) {
            associationReportService.processAssociationErrors(association, associationPipelineErrors);
        }
        else {
            associationReportService.updateAssociationReportDetails(association);
        }

        // Save data
        if (!variantToLocationsMap.isEmpty()) {
            getLog().debug("Updating location details ...");
            variantLocationMappingService.storeVariantLocation(variantToLocationsMap);
            getLog().debug("Updating location details complete");
        }
        if (!allGenomicContexts.isEmpty()) {
            getLog().debug("Updating genomic context details ...");
            variantGenomicContextMappingService.processGenomicContext(allGenomicContexts);
            getLog().debug("Updating genomic context details complete");
        }
    }
}
