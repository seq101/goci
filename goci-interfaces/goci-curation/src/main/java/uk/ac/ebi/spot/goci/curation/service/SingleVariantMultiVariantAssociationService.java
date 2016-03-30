package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.VariantFormRow;
import uk.ac.ebi.spot.goci.curation.model.VariantMappingForm;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 13/04/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates an association or returns a view of an association, used by AssociationController.
 *         Assumes we only have one locus for associations of type: single and multi-snp haplotypes
 */
@Service
public class SingleVariantMultiVariantAssociationService {

    // Repositories
    private AssociationRepository associationRepository;
    private LocusRepository locusRepository;
    private GenomicContextRepository genomicContextRepository;

    // Services
    private LociAttributesService lociAttributesService;

    @Autowired
    public SingleVariantMultiVariantAssociationService(AssociationRepository associationRepository,
                                                       LocusRepository locusRepository,
                                                       GenomicContextRepository genomicContextRepository,
                                                       LociAttributesService lociAttributesService) {
        this.associationRepository = associationRepository;
        this.locusRepository = locusRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.lociAttributesService = lociAttributesService;
    }


    public Association createAssociation(VariantAssociationForm variantAssociationForm) {

        Association association = new Association();

        // Set simple string, boolean and float association attributes
        association.setPvalueText(variantAssociationForm.getPvalueText());
        association.setOrType(variantAssociationForm.getOrType());
        association.setVariantType(variantAssociationForm.getVariantType());
        association.setMultiVariantHaplotype(variantAssociationForm.getMultiVariantHaplotype());
        association.setVariantApproved(variantAssociationForm.getVariantApproved());
        association.setOrPerCopyNum(variantAssociationForm.getOrPerCopyNum());
        association.setOrPerCopyRecip(variantAssociationForm.getOrPerCopyRecip());
        association.setOrPerCopyRange(variantAssociationForm.getOrPerCopyRange());
        association.setOrPerCopyRecipRange(variantAssociationForm.getOrPerCopyRecipRange());
        association.setOrPerCopyStdError(variantAssociationForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(variantAssociationForm.getOrPerCopyUnitDescr());

        // Set risk frequency
        association.setEffectAlleleFrequency(variantAssociationForm.getEffectAlleleFrequency());

        // Set value by default to false
        association.setVariantInteraction(false);

        // Add collection of EFO traits
        association.setEfoTraits(variantAssociationForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(variantAssociationForm.getPvalueMantissa());
        association.setPvalueExponent(variantAssociationForm.getPvalueExponent());

        // Check for existing loci, when editing delete any existing loci and risk alleles
        // They will be recreated in next for loop
        if (variantAssociationForm.getAssociationId() != null) {
            Association associationUserIsEditing = associationRepository.findOne(variantAssociationForm.getAssociationId());
            Collection<Locus> associationLoci = associationUserIsEditing.getLoci();
            Collection<EffectAllele> existingEffectAlleles = new ArrayList<>();

            if (associationLoci != null) {
                for (Locus locus : associationLoci) {
                    existingEffectAlleles.addAll(locus.getStrongestEffectAlleles());
                }
                for (Locus locus : associationLoci) {
                    lociAttributesService.deleteLocus(locus);
                }
                for (EffectAllele existingEffectAllele : existingEffectAlleles) {
                    lociAttributesService.deleteEffectAllele(existingEffectAllele);
                }
            }

        }

        // Add loci to association, for multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();

        // Set locus description and haplotype count
        // Set this number to the number of rows entered by curator
        Integer numberOfRows = variantAssociationForm.getVariantFormRows().size();
        if (numberOfRows > 1) {
            locus.setHaplotypeVariantCount(numberOfRows);
        }

        locus.setDescription(variantAssociationForm.getMultiVariantHaplotypeDescr());

        // Create gene from each string entered, may sure to check pre-existence
        Collection<String> authorReportedGenes = variantAssociationForm.getAuthorReportedGenes();
        Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);

        // Set locus genes
        locus.setAuthorReportedGenes(locusGenes);

        // Handle rows entered for haplotype by curator
        Collection<VariantFormRow> rows = variantAssociationForm.getVariantFormRows();
        Collection<EffectAllele> locusEffectAlleles = new ArrayList<>();

        for (VariantFormRow row : rows) {

            // Create snps from row information
            String curatorEnteredVARIANT = row.getVariant();
            Variant variant = lociAttributesService.createVariant(curatorEnteredVARIANT);

            // Get the curator entered risk allele
            String curatorEnteredEffectAllele = row.getStrongestEffectAllele();

            // Create a new risk allele and assign newly created snp
            EffectAllele effectAllele = lociAttributesService.createEffectAllele(curatorEnteredEffectAllele, variant);

            // If its not a multi-snp haplotype save frequency to risk allele
            if (!variantAssociationForm.getMultiVariantHaplotype()) {
                effectAllele.setEffectFrequency(variantAssociationForm.getEffectAlleleFrequency());
            }

            // Check for proxies and if we have one create a proxy snps
            if (row.getProxyVariants() != null && !row.getProxyVariants().isEmpty()) {
                Collection<Variant> effectAlleleProxyVariants = new ArrayList<>();

                for (String curatorEnteredProxyVariant : row.getProxyVariants()) {
                    Variant proxyVariant = lociAttributesService.createVariant(curatorEnteredProxyVariant);
                    effectAlleleProxyVariants.add(proxyVariant);
                }

                effectAllele.setProxyVariants(effectAlleleProxyVariants);
            }

            // Get the merged information
            if (row.getMerged() != null) {
                variant.setMerged(row.getMerged());
            }

            locusEffectAlleles.add(effectAllele);
        }

        // Assign all created risk alleles to locus
        locus.setStrongestEffectAlleles(locusEffectAlleles);

        // Save our newly created locus
        locusRepository.save(locus);

        // Add locus to collection and link to our association
        loci.add(locus);
        association.setLoci(loci);
        return association;

    }


    // Creates form which we can then return to view for editing etc.
    public VariantAssociationForm createVariantAssociationForm(Association association) {

        VariantAssociationForm variantAssociationForm = new VariantAssociationForm();

        // Set association ID
        variantAssociationForm.setAssociationId(association.getId());

        // Set simple string and float association attributes
        variantAssociationForm.setEffectAlleleFrequency(association.getEffectAlleleFrequency());
        variantAssociationForm.setPvalueText(association.getPvalueText());
        variantAssociationForm.setOrPerCopyNum(association.getOrPerCopyNum());
        variantAssociationForm.setOrType(association.getOrType());
        variantAssociationForm.setVariantType(association.getVariantType());
        variantAssociationForm.setMultiVariantHaplotype(association.getMultiVariantHaplotype());
        variantAssociationForm.setVariantApproved(association.getVariantApproved());
        variantAssociationForm.setPvalueMantissa(association.getPvalueMantissa());
        variantAssociationForm.setPvalueExponent(association.getPvalueExponent());
        variantAssociationForm.setOrPerCopyRecip(association.getOrPerCopyRecip());
        variantAssociationForm.setOrPerCopyStdError(association.getOrPerCopyStdError());
        variantAssociationForm.setOrPerCopyRange(association.getOrPerCopyRange());
        variantAssociationForm.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        variantAssociationForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());


        // Add collection of Efo traits
        variantAssociationForm.setEfoTraits(association.getEfoTraits());

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();
        Collection<Gene> locusGenes = new ArrayList<>();
        Collection<EffectAllele> locusEffectAlleles = new ArrayList<EffectAllele>();

        // For multi-snp and standard snps we assume their is only one locus
        for (Locus locus : loci) {
            locusGenes.addAll(locus.getAuthorReportedGenes());
            locusEffectAlleles.addAll(locus.getStrongestEffectAlleles());

            // There should only be one locus thus should be safe to set these here
            variantAssociationForm.setMultiVariantHaplotypeNum(locus.getHaplotypeVariantCount());
            variantAssociationForm.setMultiVariantHaplotypeDescr(locus.getDescription());
        }


        // Get name of gene and add to form
        Collection<String> authorReportedGenes = new ArrayList<>();
        for (Gene locusGene : locusGenes) {
            authorReportedGenes.add(locusGene.getGeneName());
        }
        variantAssociationForm.setAuthorReportedGenes(authorReportedGenes);

        // Handle snp rows
        Collection<GenomicContext> variantGenomicContexts = new ArrayList<GenomicContext>();
        List<VariantFormRow> variantFormRows = new ArrayList<VariantFormRow>();
        List<VariantMappingForm> variantMappingForms = new ArrayList<VariantMappingForm>();
        for (EffectAllele effectAllele : locusEffectAlleles) {
            VariantFormRow variantFormRow = new VariantFormRow();
            variantFormRow.setStrongestEffectAllele(effectAllele.getEffectAlleleName());

            Variant variant = effectAllele.getVariant();
            String externalId = variant.getExternalId();
            variantFormRow.setVariant(externalId);
            variantFormRow.setMerged(variant.getMerged());

            Collection<Location> locations = variant.getLocations();
            for (Location location : locations) {
                VariantMappingForm variantMappingForm = new VariantMappingForm(externalId, location);
                variantMappingForms.add(variantMappingForm);
            }

            // Set proxy if one is present
            Collection<String> proxyVariants = new ArrayList<>();
            if (effectAllele.getProxyVariants() != null) {
                for (Variant effectAlleleProxyVariant : effectAllele.getProxyVariants()) {
                    proxyVariants.add(effectAlleleProxyVariant.getExternalId());
                }
            }
            variantFormRow.setProxyVariants(proxyVariants);

            variantGenomicContexts.addAll(genomicContextRepository.findByVariantId(variant.getId()));
            variantFormRows.add(variantFormRow);
        }

        variantAssociationForm.setVariantMappingForms(variantMappingForms);
        variantAssociationForm.setGenomicContexts(variantGenomicContexts);
        variantAssociationForm.setVariantFormRows(variantFormRows);
        return variantAssociationForm;
    }
}
