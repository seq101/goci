package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationTableView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Variant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates table view of a study's associations
 */
@Service
public class AssociationViewService {

    private AssociationMappingErrorService associationMappingErrorService;
    private AssociationComponentsSyntaxChecks associationComponentsSyntaxChecks;

    @Autowired
    public AssociationViewService(AssociationMappingErrorService associationMappingErrorService,
                                  AssociationComponentsSyntaxChecks associationComponentsSyntaxChecks) {
        this.associationMappingErrorService = associationMappingErrorService;
        this.associationComponentsSyntaxChecks = associationComponentsSyntaxChecks;
    }

    /**
     * Create object, from Association, that will be returned to view
     *
     * @param association Association object
     */
    public VariantAssociationTableView createVariantAssociationTableView(Association association) {
        VariantAssociationTableView variantAssociationTableView = new VariantAssociationTableView();

        // For SNP interaction studies snp, proxy snps, risk alleles etc
        // should be separated by an 'x'
        String delimiter = "; ";
        if (association.getVariantInteraction()) {
            delimiter = " x ";
        }

        variantAssociationTableView.setAssociationId(association.getId());

        // For each locus relevant attributes
        Collection<Locus> loci = association.getLoci();
        Collection<String> allLociGenes = new ArrayList<>();
        Collection<String> allLociEffectAlleles = new ArrayList<String>();
        Collection<String> allLociVariants = new ArrayList<String>();
        Collection<String> allLociProxyVariants = new ArrayList<String>();
        Collection<String> allLociEffectAlleleFrequencies = new ArrayList<String>();
        Collection<String> allLociVariantStatuses = new ArrayList<String>();

        // By looking at each locus in turn we can keep order in view
        String syntaxError = ""; // store any syntax errors
        for (Locus locus : loci) {

            // Store gene names, a locus can have a number of genes attached.
            // Per locus create a comma separated list and add to an array.
            // Further processing will then delimit this list
            // either by ; or 'x' depending on association type
            Collection<String> currentlocusGenes = new ArrayList<>();
            String commaSeparatedGenes = "";
            for (Gene gene : locus.getAuthorReportedGenes()) {
                currentlocusGenes.add(gene.getGeneName());
            }
            if (!currentlocusGenes.isEmpty()) {
                commaSeparatedGenes = String.join(", ", currentlocusGenes);
                allLociGenes.add(commaSeparatedGenes);
            }
            else { allLociGenes.add("NR"); }

            for (EffectAllele effectAllele : locus.getStrongestEffectAlleles()) {
                allLociEffectAlleles.add(effectAllele.getEffectAlleleName());

                // Check for any potential errors
                syntaxError = syntaxError + associationComponentsSyntaxChecks.checkEffectAllele(effectAllele.getEffectAlleleName());

                // For standard association set the risk allele frequency
                // Based on assumption we only have one locus with a single risk allele attached
                if (!association.getMultiVariantHaplotype() && !association.getVariantInteraction()) {
                    if (effectAllele.getEffectFrequency() != null && !effectAllele.getEffectFrequency().isEmpty()) {
                        allLociEffectAlleleFrequencies.add(effectAllele.getEffectFrequency());
                    }
                }

                // SNPs attached to risk allele
                Variant variant = effectAllele.getVariant();
                allLociVariants.add(variant.getExternalId());

                // Check for any potential errors
                syntaxError = syntaxError + associationComponentsSyntaxChecks.checkVariant(variant.getExternalId());

                // Set proxies if present
                Collection<String> currentLocusProxies = new ArrayList<>();
                String commaSeparatedProxies = "";
                if (effectAllele.getProxyVariants() != null) {
                    for (Variant proxyVariant : effectAllele.getProxyVariants()) {
                        currentLocusProxies.add(proxyVariant.getExternalId());

                        // Check for any potential errors
                        syntaxError = syntaxError + associationComponentsSyntaxChecks.checkProxy(proxyVariant.getExternalId());
                    }
                }

                // Comma separate proxies in view
                if (!currentLocusProxies.isEmpty()) {
                    commaSeparatedProxies = String.join(", ", currentLocusProxies);
                    allLociProxyVariants.add(commaSeparatedProxies);
                }

                else { allLociProxyVariants.add("NR");}

                // Only required for SNP interaction studies
                if (association.getVariantInteraction() != null) {
                    if (association.getVariantInteraction()) {

                        // Genome wide Vs Limited List
                        Collection<String> variantStatus = new ArrayList<>();
                        String commaSeparatedVariantStatus = "";
                        if (effectAllele.getLimitedList() != null) {
                            if (effectAllele.getLimitedList()) {
                                variantStatus.add("LL");
                            }
                        }
                        if (effectAllele.getGenomeWide() != null) {
                            if (effectAllele.getGenomeWide()) {
                                variantStatus.add("GW");
                            }
                        }
                        if (!variantStatus.isEmpty()) {
                            commaSeparatedVariantStatus = String.join(", ", variantStatus);
                            allLociVariantStatuses.add(commaSeparatedVariantStatus);
                        }
                        else { allLociVariantStatuses.add("NR");}


                        // Allele risk frequency
                        if (effectAllele.getEffectFrequency() != null && !effectAllele.getEffectFrequency().isEmpty()) {
                            allLociEffectAlleleFrequencies.add(effectAllele.getEffectFrequency());
                        }
                        else {
                            allLociEffectAlleleFrequencies.add("NR");
                        }
                    }
                }
            }
        }

        // Create delimited strings for view
        String authorReportedGenes = null;
        if (allLociGenes.size() > 1) {
            authorReportedGenes = String.join(delimiter, allLociGenes);
        }
        else {
            authorReportedGenes = String.join("", allLociGenes);
        }
        variantAssociationTableView.setAuthorReportedGenes(authorReportedGenes);

        String strongestEffectAlleles = null;
        if (allLociEffectAlleles.size() > 1) {
            strongestEffectAlleles = String.join(delimiter, allLociEffectAlleles);
        }
        else {
            strongestEffectAlleles = String.join("", allLociEffectAlleles);
        }
        variantAssociationTableView.setStrongestEffectAlleles(strongestEffectAlleles);

        String associationVariants = null;
        if (allLociVariants.size() > 1) {
            associationVariants = String.join(delimiter, allLociVariants);
        }
        else {
            associationVariants = String.join("", allLociVariants);
        }
        variantAssociationTableView.setVariants(associationVariants);

        String associationProxies = null;
        if (allLociProxyVariants.size() > 1) {
            associationProxies = String.join(delimiter, allLociProxyVariants);
        }
        else {
            associationProxies = String.join("", allLociProxyVariants);
        }
        variantAssociationTableView.setProxyVariants(associationProxies);

        // Set both risk frequencies
        String associationEffectAlleleFrequencies = null;
        if (allLociEffectAlleleFrequencies.size() > 1) {
            associationEffectAlleleFrequencies = String.join(delimiter, allLociEffectAlleleFrequencies);
        }
        else {
            associationEffectAlleleFrequencies = String.join("", allLociEffectAlleleFrequencies);
        }
        variantAssociationTableView.setEffectAlleleFrequencies(associationEffectAlleleFrequencies);
        variantAssociationTableView.setAssociationEffectFrequency(association.getEffectAlleleFrequency());

        String associationVariantStatuses = null;
        if (allLociVariantStatuses.size() > 1) {
            associationVariantStatuses = String.join(delimiter, allLociVariantStatuses);
        }
        else {
            associationVariantStatuses = String.join("", allLociVariantStatuses);
        }
        variantAssociationTableView.setVariantStatuses(associationVariantStatuses);

        variantAssociationTableView.setPvalueMantissa(association.getPvalueMantissa());
        variantAssociationTableView.setPvalueExponent(association.getPvalueExponent());
        variantAssociationTableView.setPvalueText(association.getPvalueText());


        Collection<String> efoTraits = new ArrayList<>();
        for (EfoTrait efoTrait : association.getEfoTraits()) {
            efoTraits.add(efoTrait.getTrait());

        }
        String associationEfoTraits = null;
        associationEfoTraits = String.join(", ", efoTraits);
        variantAssociationTableView.setEfoTraits(associationEfoTraits);

        variantAssociationTableView.setOrPerCopyNum(association.getOrPerCopyNum());
        variantAssociationTableView.setOrPerCopyRecip(association.getOrPerCopyRecip());

        if (association.getOrType() != null) {
            if (association.getOrType()) {
                variantAssociationTableView.setOrType("Yes");
            }

            if (!association.getOrType()) {
                variantAssociationTableView.setOrType("No");
            }
        }

        variantAssociationTableView.setOrPerCopyRange(association.getOrPerCopyRange());
        variantAssociationTableView.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        variantAssociationTableView.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());
        variantAssociationTableView.setOrPerCopyStdError(association.getOrPerCopyStdError());
        variantAssociationTableView.setAssociationType(association.getVariantType());


        if (association.getMultiVariantHaplotype() != null) {
            if (association.getMultiVariantHaplotype()) {
                variantAssociationTableView.setMultiVariantHaplotype("Yes");
            }

            if (!association.getMultiVariantHaplotype()) {
                variantAssociationTableView.setMultiVariantHaplotype("No");
            }
        }

        if (association.getVariantInteraction() != null) {
            if (association.getVariantInteraction()) {
                variantAssociationTableView.setVariantInteraction("Yes");
            }

            if (!association.getVariantInteraction()) {
                variantAssociationTableView.setVariantInteraction("No");
            }
        }

        if (association.getVariantApproved() != null) {
            if (association.getVariantApproved()) {
                variantAssociationTableView.setVariantApproved("Yes");
            }


            if (!association.getVariantApproved()) {
                variantAssociationTableView.setVariantApproved("No");
            }
        }

        // Check if the errors in the association report have been checked by a curator
        if (association.getAssociationReport() != null) {
            if (association.getAssociationReport().getErrorCheckedByCurator() != null) {
                if (association.getAssociationReport().getErrorCheckedByCurator()) {
                    variantAssociationTableView.setAssociationErrorsChecked("Yes");
                }

                if (!association.getAssociationReport().getErrorCheckedByCurator()) {
                    variantAssociationTableView.setAssociationErrorsChecked("No");
                }
            }
        }

        // Set error map
        variantAssociationTableView.setAssociationErrorMap(associationMappingErrorService.createAssociationErrorMap(
                association.getAssociationReport()));

        // Set syntax errors
        if (!syntaxError.isEmpty()) {
            variantAssociationTableView.setSyntaxErrorsFound("Yes");
        }
        else {variantAssociationTableView.setSyntaxErrorsFound("No");}

        // Get mapping details
        if (association.getLastMappingPerformedBy() != null) {
            variantAssociationTableView.setLastMappingPerformedBy(association.getLastMappingPerformedBy());
        }

        if (association.getLastMappingDate() != null) {
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateOfLastMapping = df.format(association.getLastMappingDate());
            variantAssociationTableView.setLastMappingDate(dateOfLastMapping);
        }

        return variantAssociationTableView;
    }
}
