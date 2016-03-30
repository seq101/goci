package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.VariantFormColumn;
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
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates an association or returns a view of an association, used by AssociationController.
 *         Used only for SNP X SNP interaction associations
 */
@Service
public class VariantInteractionAssociationService {

    // Repositories
    private LocusRepository locusRepository;
    private AssociationRepository associationRepository;
    private GenomicContextRepository genomicContextRepository;

    // Services
    private LociAttributesService lociAttributesService;

    @Autowired
    public VariantInteractionAssociationService(LocusRepository locusRepository,
                                                AssociationRepository associationRepository,
                                                GenomicContextRepository genomicContextRepository,
                                                LociAttributesService lociAttributesService) {
        this.locusRepository = locusRepository;
        this.associationRepository = associationRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.lociAttributesService = lociAttributesService;
    }

    public Association createAssociation(VariantAssociationInteractionForm variantAssociationInteractionForm) {

        Association association = new Association();

        // Set simple string, boolean and float association attributes
        association.setPvalueText(variantAssociationInteractionForm.getPvalueText());
        association.setOrType(variantAssociationInteractionForm.getOrType());
        association.setVariantType(variantAssociationInteractionForm.getVariantType());
        association.setVariantApproved(variantAssociationInteractionForm.getVariantApproved());
        association.setOrPerCopyNum(variantAssociationInteractionForm.getOrPerCopyNum());
        association.setOrPerCopyRecip(variantAssociationInteractionForm.getOrPerCopyRecip());
        association.setOrPerCopyRange(variantAssociationInteractionForm.getOrPerCopyRange());
        association.setOrPerCopyRecipRange(variantAssociationInteractionForm.getOrPerCopyRecipRange());
        association.setOrPerCopyStdError(variantAssociationInteractionForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(variantAssociationInteractionForm.getOrPerCopyUnitDescr());
        association.setEffectAlleleFrequency(variantAssociationInteractionForm.getEffectAlleleFrequency());

        // Set multi-snp and snp interaction checkboxes
        association.setMultiVariantHaplotype(false);
        association.setVariantInteraction(true);

        // Add collection of EFO traits
        association.setEfoTraits(variantAssociationInteractionForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(variantAssociationInteractionForm.getPvalueMantissa());
        association.setPvalueExponent(variantAssociationInteractionForm.getPvalueExponent());

        // Check for existing loci, when editing delete any existing loci and risk alleles
        // They will be recreated in next for loop
        if (variantAssociationInteractionForm.getAssociationId() != null) {

            Association associationUserIsEditing =
                    associationRepository.findOne(variantAssociationInteractionForm.getAssociationId());
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

        // For each column create a loci
        Collection<Locus> loci = new ArrayList<>();
        for (VariantFormColumn col : variantAssociationInteractionForm.getVariantFormColumns()) {

            Locus locus = new Locus();
            locus.setDescription("Variant x Variant interaction");

            // Set locus genes
            Collection<String> authorReportedGenes = col.getAuthorReportedGenes();
            Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);
            locus.setAuthorReportedGenes(locusGenes);

            // Create SNP
            String curatorEnteredVariant = col.getVariant();
            Variant variant = lociAttributesService.createVariant(curatorEnteredVariant);

            // One risk allele per locus
            String curatorEnteredEffectAllele = col.getStrongestEffectAllele();
            EffectAllele effectAllele = lociAttributesService.createEffectAllele(curatorEnteredEffectAllele, variant);
            Collection<EffectAllele> locusEffectAlleles = new ArrayList<>();

            // Set risk allele attributes
            effectAllele.setGenomeWide(col.getGenomeWide());
            effectAllele.setLimitedList(col.getLimitedList());
            effectAllele.setEffectFrequency(col.getEffectFrequency());

            // Check for a proxy and if we have one create a proxy variant
            Collection<String> curatorEnteredProxyVariants = col.getProxyVariants();
            if (curatorEnteredProxyVariants != null && !curatorEnteredProxyVariants.isEmpty()) {

                Collection<Variant> effectAlleleProxyVariants = new ArrayList<>();

                for (String curatorEnteredProxyVariant : curatorEnteredProxyVariants) {
                    Variant proxyVariant = lociAttributesService.createVariant(curatorEnteredProxyVariant);
                    effectAlleleProxyVariants.add(proxyVariant);
                }

                effectAllele.setProxyVariants(effectAlleleProxyVariants);
            }

            // Link risk allele to locus
            locusEffectAlleles.add(effectAllele);
            locus.setStrongestEffectAlleles(locusEffectAlleles);

            // Save our newly created locus
            locusRepository.save(locus);

            // Add locus to collection and link to our association
            loci.add(locus);

        }
        association.setLoci(loci);
        return association;
    }

    // Create a form to return to view from Association model object
    public VariantAssociationInteractionForm createVariantAssociationInteractionForm(Association association) {

        // Create form
        VariantAssociationInteractionForm variantAssociationInteractionForm = new VariantAssociationInteractionForm();

        // Set simple string and boolean values
        variantAssociationInteractionForm.setAssociationId(association.getId());
        variantAssociationInteractionForm.setPvalueText(association.getPvalueText());
        variantAssociationInteractionForm.setOrPerCopyNum(association.getOrPerCopyNum());
        variantAssociationInteractionForm.setVariantType(association.getVariantType());
        variantAssociationInteractionForm.setVariantApproved(association.getVariantApproved());
        variantAssociationInteractionForm.setOrType(association.getOrType());
        variantAssociationInteractionForm.setPvalueMantissa(association.getPvalueMantissa());
        variantAssociationInteractionForm.setPvalueExponent(association.getPvalueExponent());
        variantAssociationInteractionForm.setOrPerCopyRecip(association.getOrPerCopyRecip());
        variantAssociationInteractionForm.setOrPerCopyStdError(association.getOrPerCopyStdError());
        variantAssociationInteractionForm.setOrPerCopyRange(association.getOrPerCopyRange());
        variantAssociationInteractionForm.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        variantAssociationInteractionForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());
        variantAssociationInteractionForm.setEffectAlleleFrequency(association.getEffectAlleleFrequency());

        // Add collection of Efo traits
        variantAssociationInteractionForm.setEfoTraits(association.getEfoTraits());

        // Create form columns
        List<VariantFormColumn> variantFormColumns = new ArrayList<>();

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();

        Collection<GenomicContext> variantGenomicContexts = new ArrayList<GenomicContext>();
        List<VariantMappingForm> variantMappingForms = new ArrayList<VariantMappingForm>();

        // Create a column per locus
        if (loci != null && !loci.isEmpty()) {
            for (Locus locus : loci) {

                VariantFormColumn variantFormColumn = new VariantFormColumn();

                // Set genes
                Collection<String> authorReportedGenes = new ArrayList<>();
                for (Gene gene : locus.getAuthorReportedGenes()) {
                    authorReportedGenes.add(gene.getGeneName());
                }
                variantFormColumn.setAuthorReportedGenes(authorReportedGenes);

                // Set risk allele
                Collection<EffectAllele> locusEffectAlleles = locus.getStrongestEffectAlleles();
                String strongestEffectAllele = null;
                String variant = null;
                Collection<String> proxyVariants = new ArrayList<>();
                Boolean genomeWide = false;
                Boolean limitedList = false;
                String effectFrequency = null;

                // For variant x variant interaction studies should only have one risk allele per locus
                if (locusEffectAlleles != null && locusEffectAlleles.size() == 1) {
                    for (EffectAllele effectAllele : locusEffectAlleles) {
                        strongestEffectAllele = effectAllele.getEffectAlleleName();
                        variant = effectAllele.getVariant().getExternalId();

                        Variant variantObj = effectAllele.getVariant();
                        Collection<Location> locations = variantObj.getLocations();
                        for (Location location : locations) {
                            VariantMappingForm variantMappingForm = new VariantMappingForm(variant, location);
                            variantMappingForms.add(variantMappingForm);
                        }
                        variantGenomicContexts.addAll(genomicContextRepository.findByVariantId(variantObj.getId()));

                        // Set proxy
                        if (effectAllele.getProxyVariants() != null) {
                            for (Variant effectAlleleProxyVariant : effectAllele.getProxyVariants()) {
                                proxyVariants.add(effectAlleleProxyVariant.getExternalId());
                            }
                        }

                        if (effectAllele.getGenomeWide() != null && effectAllele.getGenomeWide()) {
                            genomeWide = true;
                        }

                        if (effectAllele.getLimitedList() != null && effectAllele.getLimitedList()) {
                            limitedList = true;
                        }

                        effectFrequency = effectAllele.getEffectFrequency();
                    }
                }

                else {
                    throw new RuntimeException(
                            "More than one effect allele found for locus " + locus.getId() +
                                    ", this is not supported yet for Variant interaction associations"
                    );
                }

                // Set column attributes
                variantFormColumn.setStrongestEffectAllele(strongestEffectAllele);
                variantFormColumn.setVariant(variant);
                variantFormColumn.setProxyVariants(proxyVariants);
                variantFormColumn.setGenomeWide(genomeWide);
                variantFormColumn.setLimitedList(limitedList);
                variantFormColumn.setEffectFrequency(effectFrequency);


                variantFormColumns.add(variantFormColumn);
            }
        }
        variantAssociationInteractionForm.setVariantMappingForms(variantMappingForms);
        variantAssociationInteractionForm.setGenomicContexts(variantGenomicContexts);
        variantAssociationInteractionForm.setVariantFormColumns(variantFormColumns);
        variantAssociationInteractionForm.setNumOfInteractions(variantFormColumns.size());
        return variantAssociationInteractionForm;
    }


}
