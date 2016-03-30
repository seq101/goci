package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.EnsemblGeneRepository;
import uk.ac.ebi.spot.goci.repository.EntrezGeneRepository;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by emma on 10/07/2015.
 *
 * @author emma
 *         <p>
 *         Service class to store genomic context information returned from mapping pipeline. Begins by storing gene
 *         information and then creating genomic context information. The information from the current run of the
 *         pipeline is always considered most up-to-date therefore in most cases previous information is deleted.
 */
@Service
public class VariantGenomicContextMappingService {

    private VariantRepository variantRepository;
    private GeneRepository geneRepository;
    private GenomicContextRepository genomicContextRepository;
    private EnsemblGeneRepository ensemblGeneRepository;
    private EntrezGeneRepository entrezGeneRepository;
    private LocationRepository locationRepository;

    // Service
    private GeneQueryService geneQueryService;
    private EnsemblGeneQueryService ensemblGeneQueryService;
    private EntrezGeneQueryService entrezGeneQueryService;
    private VariantQueryService variantQueryService;
    private LocationCreationService locationCreationService;
    private GenomicContextCreationService genomicContextCreationService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    //Constructor
    @Autowired
    public VariantGenomicContextMappingService(VariantRepository variantRepository,
                                               GeneRepository geneRepository,
                                               GenomicContextRepository genomicContextRepository,
                                               EnsemblGeneRepository ensemblGeneRepository,
                                               EntrezGeneRepository entrezGeneRepository,
                                               LocationRepository locationRepository,
                                               GeneQueryService geneQueryService,
                                               EnsemblGeneQueryService ensemblGeneQueryService,
                                               EntrezGeneQueryService entrezGeneQueryService,
                                               VariantQueryService variantQueryService,
                                               LocationCreationService locationCreationService,
                                               GenomicContextCreationService genomicContextCreationService) {
        this.variantRepository = variantRepository;
        this.geneRepository = geneRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.ensemblGeneRepository = ensemblGeneRepository;
        this.entrezGeneRepository = entrezGeneRepository;
        this.locationRepository = locationRepository;
        this.geneQueryService = geneQueryService;
        this.ensemblGeneQueryService = ensemblGeneQueryService;
        this.entrezGeneQueryService = entrezGeneQueryService;
        this.variantQueryService = variantQueryService;
        this.locationCreationService = locationCreationService;
        this.genomicContextCreationService = genomicContextCreationService;
    }

    /**
     * Takes genomic context information returned by mapping pipeline and creates a structure that links an rs_id to all
     * its genomic context objects. This ensures we can do a single update based on latest mapping information.
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
    public void processGenomicContext(Collection<GenomicContext> genomicContexts) {

        // Process the gene information first so all IDs can be updated and any new genes created
        processGenes(genomicContexts);

        // Need flatten down genomic context information
        // and create structure linking each RS_ID to its complete set of new mapped data
        getLog().debug("Collate all new genomic context information...");
        Map<String, Set<GenomicContext>> variantToGenomicContextMap = new HashMap<>();

        for (GenomicContext genomicContext : genomicContexts) {
            String externalIdInGenomicContext = genomicContext.getVariant().getExternalId();

            // Next time we see SNP, add genomic context to set
            if (variantToGenomicContextMap.containsKey(externalIdInGenomicContext)) {
                variantToGenomicContextMap.get(externalIdInGenomicContext).add(genomicContext);
            }

            // First time we see a SNP store the genomic context
            else {
                Set<GenomicContext> variantGenomicContext = new HashSet<>();
                variantGenomicContext.add(genomicContext);
                variantToGenomicContextMap.put(externalIdInGenomicContext, variantGenomicContext);
            }
        }

        // Store genomic context information
        getLog().debug("Storing new genomic context information...");
        storeVariantGenomicContext(variantToGenomicContextMap);
    }


    /**
     * Extract gene information from genomic contexts returned from mapping pipeline
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
    private void processGenes(Collection<GenomicContext> genomicContexts) {

        getLog().debug("Processing genes...");

        // Need to flatten down genomic context gene information
        // and create structure linking each gene symbol to its
        // complete set of current Ensembl and Entrez IDs
        Map<String, Set<String>> geneToEnsemblIdMap = new HashMap<>();
        Map<String, Set<String>> geneToEntrezIdMap = new HashMap<>();

        // Loop over each genomic context and store information on external IDs linked to gene symbol
        for (GenomicContext genomicContext : genomicContexts) {

            // Check gene exists
            String geneName = genomicContext.getGene().getGeneName().trim();

            if (!geneName.equalsIgnoreCase("undefined")) {

                // Retrieve the latest Ensembl/Entrez IDs for the named gene from the latest mapping run
                Collection<EnsemblGene> ensemblGeneIds = genomicContext.getGene().getEnsemblGeneIds();
                for (EnsemblGene ensemblGene : ensemblGeneIds) {

                    String ensemblId = ensemblGene.getEnsemblGeneId();
                    if (ensemblId != null) {
                        if (geneToEnsemblIdMap.containsKey(geneName)) {
                            geneToEnsemblIdMap.get(geneName).add(ensemblId);
                        }

                        else {
                            Set<String> ensemblGeneIdsSet = new HashSet<>();
                            ensemblGeneIdsSet.add(ensemblId);
                            geneToEnsemblIdMap.put(geneName, ensemblGeneIdsSet);
                        }
                    }
                }

                Collection<EntrezGene> entrezGeneIds = genomicContext.getGene().getEntrezGeneIds();
                for (EntrezGene entrezGene : entrezGeneIds) {

                    String entrezId = entrezGene.getEntrezGeneId();
                    if (entrezId != null) {
                        if (geneToEntrezIdMap.containsKey(geneName)) {
                            geneToEntrezIdMap.get(geneName).add(entrezId);
                        }

                        else {
                            Set<String> entrezGeneIdsSet = new HashSet<>();
                            entrezGeneIdsSet.add(entrezId);
                            geneToEntrezIdMap.put(geneName, entrezGeneIdsSet);
                        }
                    }
                }
            }
        }

        // Store genes, source is required so we know what table to add them to
        if (geneToEnsemblIdMap.size() > 0) {
            storeGenes(geneToEnsemblIdMap, "Ensembl");
        }

        if (geneToEntrezIdMap.size() > 0) {
            storeGenes(geneToEntrezIdMap, "Entrez");
        }
    }

    /**
     * Create/update genes with latest mapping information
     *
     * @param geneToExternalIdMap map of a gene name and all external database IDs from current mapping run
     * @param source              the source of mapping, either Ensembl or Entrez
     */
    private void storeGenes(Map<String, Set<String>> geneToExternalIdMap, String source) {

        for (String geneName : geneToExternalIdMap.keySet()) {

            Set<String> externalIds = geneToExternalIdMap.get(geneName);

            // Find any existing database genes that match the gene name
            // IgnoreCase query is not used here as we want
            // the exact gene name returned from mapping
            Gene existingGeneInDatabase = geneQueryService.findByGeneName(geneName);

            // If gene is not found in database then create one
            if (existingGeneInDatabase == null) {
                createGene(geneName, externalIds, source);
            }

            // Update gene
            else {

                if (source.equalsIgnoreCase("Ensembl")) {

                    // Get a list of current Ensembl IDs linked to existing gene
                    Collection<EnsemblGene> oldEnsemblGenesLinkedToGene =
                            existingGeneInDatabase.getEnsemblGeneIds();
                    Collection<Long> oldEnsemblIdsLinkedToGene = new ArrayList<>();

                    for (EnsemblGene oldEnsemblGeneLinkedToGene : oldEnsemblGenesLinkedToGene) {
                        oldEnsemblIdsLinkedToGene.add(oldEnsemblGeneLinkedToGene.getId());
                    }

                    Collection<EnsemblGene> newEnsemblGenes = new ArrayList<>();
                    for (String id : externalIds) {
                        EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                        newEnsemblGenes.add(ensemblGene);
                    }

                    // Set latest IDs from mapping run
                    existingGeneInDatabase.setEnsemblGeneIds(newEnsemblGenes);

                    // Save changes
                    geneRepository.save(existingGeneInDatabase);

                    // Clean-up any Ensembl IDs that may now be left without a gene linked
                    for (Long oldEnsemblIdLinkedToGene : oldEnsemblIdsLinkedToGene) {
                        cleanUpEnsemblGenes(oldEnsemblIdLinkedToGene);
                    }

                }

                if (source.equalsIgnoreCase("Entrez")) {

                    // Get a list of of current Entrez IDs linked to existing gene
                    Collection<EntrezGene> oldEntrezGenesLinkedToGene = existingGeneInDatabase.getEntrezGeneIds();
                    Collection<Long> oldEntrezGenesIdsLinkedToGene = new ArrayList<>();

                    for (EntrezGene oldEntrezGeneLinkedToGene : oldEntrezGenesLinkedToGene) {
                        oldEntrezGenesIdsLinkedToGene.add(oldEntrezGeneLinkedToGene.getId());
                    }

                    Collection<EntrezGene> newEntrezGenes = new ArrayList<>();
                    for (String id : externalIds) {
                        EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                        newEntrezGenes.add(entrezGene);
                    }

                    // Set latest IDs from mapping run
                    existingGeneInDatabase.setEntrezGeneIds(newEntrezGenes);

                    // Save changes
                    geneRepository.save(existingGeneInDatabase);

                    // Clean-up any Entrez IDs that may now be left without a gene linked
                    for (Long oldEntrezGenesIdLinkedToGene : oldEntrezGenesIdsLinkedToGene) {
                        cleanUpEntrezGenes(oldEntrezGenesIdLinkedToGene);
                    }

                }
            }
        }

    }


    /**
     * Saves genomic context information to database
     *
     * @param variantToGenomicContextMap map of rs_id and all genomic context details returned from current mapping run
     */
    private void storeVariantGenomicContext(Map<String, Set<GenomicContext>> variantToGenomicContextMap) {

        // Go through each rs_id and its associated genomic contexts returned from the mapping pipeline
        for (String variantExternalId : variantToGenomicContextMap.keySet()) {

            getLog().debug("Storing genomic context for " + variantExternalId);

            Set<GenomicContext> genomicContextsFromMapping = variantToGenomicContextMap.get(variantExternalId);

            // Check if the SNP exists
            Variant variantInDatabase =
                    variantQueryService.findByExternalIdIgnoreCase(variantExternalId);

            if (variantInDatabase != null) {

                Collection<GenomicContext> newVariantGenomicContexts = new ArrayList<>();

                for (GenomicContext genomicContextFromMapping : genomicContextsFromMapping) {

                    // Gene should already have been created
                    String geneName = genomicContextFromMapping.getGene().getGeneName().trim();

                    if (!geneName.equalsIgnoreCase("undefined")) {

                        // Create new genomic context
                        Boolean isIntergenic = genomicContextFromMapping.getIsIntergenic();
                        Boolean isUpstream = genomicContextFromMapping.getIsUpstream();
                        Boolean isDownstream = genomicContextFromMapping.getIsDownstream();
                        Long distance = genomicContextFromMapping.getDistance();
                        String source = genomicContextFromMapping.getSource();
                        String mappingMethod = genomicContextFromMapping.getMappingMethod();
                        Boolean isClosestGene = genomicContextFromMapping.getIsClosestGene();

                        // Location details
                        String chromosomeName = genomicContextFromMapping.getLocation().getChromosomeName();
                        String chromosomePosition = genomicContextFromMapping.getLocation().getChromosomePosition();
                        Region regionFromMapping = genomicContextFromMapping.getLocation().getRegion();
                        String regionName = null;

                        if (regionFromMapping.getName() != null) {
                            regionName = regionFromMapping.getName().trim();
                        }

                        // Check if location already exists
                        Location location =
                                locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                        chromosomeName,
                                        chromosomePosition,
                                        regionName);

                        if (location == null) {
                            location = locationCreationService.createLocation(chromosomeName,
                                                                              chromosomePosition,
                                                                              regionName);
                        }

                        GenomicContext genomicContext = genomicContextCreationService.createGenomicContext(isIntergenic,
                                                                                                           isUpstream,
                                                                                                           isDownstream,
                                                                                                           distance,
                                                                                                           source,
                                                                                                           mappingMethod,
                                                                                                           geneName,
                                                                                                           variantInDatabase,
                                                                                                           isClosestGene,
                                                                                                           location);

                        newVariantGenomicContexts.add(genomicContext);
                    }

                    else {
                        getLog().warn("Gene name returned from mapping pipeline is 'undefined' for Variant" +
                                              variantInDatabase.getExternalId());
                    }
                }

                // Save latest mapped information
                variantInDatabase.setGenomicContexts(newVariantGenomicContexts);
                // Update the last update date
                variantInDatabase.setLastUpdateDate(new Date());
                variantRepository.save(variantInDatabase);

            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                getLog().error("Adding genomic context for Variant not found in database, VARIANT_ID:" + variantExternalId);
                throw new RuntimeException(
                        "Adding genomic context for Variant not found in database, VARIANT_ID: " + variantExternalId);
            }
        }
    }

    /**
     * Method to create a gene
     *
     * @param geneName    gene symbol or name
     * @param externalIds external gene IDs
     * @param source      the source of mapping, either Ensembl or Entrez
     */
    private void createGene(String geneName, Set<String> externalIds, String source) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);

        if (source.equalsIgnoreCase("Ensembl")) {
            // Set Ensembl Ids for new gene
            Collection<EnsemblGene> ensemblGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                ensemblGeneIds.add(ensemblGene);
            }
            newGene.setEnsemblGeneIds(ensemblGeneIds);
        }

        if (source.equalsIgnoreCase("Entrez")) {
            // Set Entrez Ids for new gene
            Collection<EntrezGene> entrezGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                entrezGeneIds.add(entrezGene);
            }
            newGene.setEntrezGeneIds(entrezGeneIds);
        }

        // Save gene
        getLog().debug("Creating " + source + " gene, with name " + geneName);
        geneRepository.save(newGene);
    }

    /**
     * Method to create an Ensembl gene, this database table holds ensembl gene IDs
     *
     * @param id       Ensembl gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    private EnsemblGene createOrRetrieveEnsemblExternalId(String id, String geneName) {
        EnsemblGene ensemblGene = ensemblGeneQueryService.findByEnsemblGeneId(id);

        // Create new entry in ENSEMBL_GENE table for this ID
        if (ensemblGene == null) {
            ensemblGene = new EnsemblGene();
            ensemblGene.setEnsemblGeneId(id);
            ensemblGeneRepository.save(ensemblGene);
        }

        // Check this ID is not linked to a gene with a different name
        else {
            Gene existingGeneLinkedToId = ensemblGene.getGene();

            if (existingGeneLinkedToId != null) {
                if (!Objects.equals(existingGeneLinkedToId.getGeneName(), geneName)) {
                    getLog().warn(
                            "Ensembl ID: " + id + ", is already used in database by a different gene(s): " +
                                    existingGeneLinkedToId.getGeneName() + ". Will update so links to " + geneName);

                    // For gene already linked to this ensembl ID remove the ensembl ID
                    existingGeneLinkedToId.getEnsemblGeneIds().remove(ensemblGene);
                    geneRepository.save(existingGeneLinkedToId);
                }
            }
        }
        return ensemblGene;
    }

    /**
     * Method to create an Entrez gene, this database table holds entrez gene IDs
     *
     * @param id       Entrez gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    private EntrezGene createOrRetrieveEntrezExternalId(String id, String geneName) {
        EntrezGene entrezGene = entrezGeneQueryService.findByEntrezGeneId(id);

        // Create new entry in ENTREZ_GENE table for this ID
        if (entrezGene == null) {
            entrezGene = new EntrezGene();
            entrezGene.setEntrezGeneId(id);
            entrezGeneRepository.save(entrezGene);
        }

        // Check this ID is not linked to a gene with a different name
        else {
            Gene existingGeneLinkedToId = entrezGene.getGene();

            if (existingGeneLinkedToId != null) {
                if (!Objects.equals(existingGeneLinkedToId.getGeneName(), geneName)) {
                    getLog().warn(
                            "Entrez ID: " + id + ", is already used in database by a different gene(s): " +
                                    existingGeneLinkedToId.getGeneName() + ". Will update so links to " + geneName);

                    // For gene already linked to this entrez ID remove the entrez ID
                    existingGeneLinkedToId.getEntrezGeneIds().remove(entrezGene);
                    geneRepository.save(existingGeneLinkedToId);
                }
            }
        }
        return entrezGene;
    }

    /**
     * Method to clean-up an Ensembl gene ID in database that has no linked gene
     *
     * @param id Ensembl gene ID to delete
     */

    private void cleanUpEnsemblGenes(Long id) {

        // Find any genes with this Ensembl ID
        Gene geneWithEnsemblId = geneRepository.findByEnsemblGeneIdsId(id);

        // If this ID is not linked to a gene then delete it
        if (geneWithEnsemblId == null) {
            ensemblGeneRepository.delete(id);
        }
    }

    /**
     * Method to clean-up an Entrez gene ID in database that has no linked gene
     *
     * @param id Entrez gene ID to delete
     */
    private void cleanUpEntrezGenes(Long id) {

        // Find any genes with this Entrez ID
        Gene geneWithEntrezIds = geneRepository.findByEntrezGeneIdsId(id);

        // If this ID is not linked to a gene then delete it
        if (geneWithEntrezIds == null) {
            entrezGeneRepository.delete(id);
        }
    }

    /**
     * Method to remove the existing genomic contexts linked to a SNP
     *
     * @param variant SNP from which to remove the associated genomic contexts
     */
    public void removeExistingGenomicContexts(Variant variant) {

        // Get a list of locations currently genomic context
        Collection<GenomicContext> variantGenomicContexts = variant.getGenomicContexts();

        if (variantGenomicContexts != null && !variantGenomicContexts.isEmpty()) {
            // Remove old genomic contexts, as these will be updated with latest mapping
            variant.setGenomicContexts(new ArrayList<>());
            variantRepository.save(variant);
            Set<Long> oldVariantLocationIds = new HashSet<>();

            for (GenomicContext variantGenomicContext : variantGenomicContexts) {
                if (variantGenomicContext.getLocation() != null) {
                    oldVariantLocationIds.add(variantGenomicContext.getLocation().getId());
                }
                genomicContextRepository.delete(variantGenomicContext);
            }

            for (Long oldVariantLocationId : oldVariantLocationIds) {
                cleanUpLocations(oldVariantLocationId);
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
