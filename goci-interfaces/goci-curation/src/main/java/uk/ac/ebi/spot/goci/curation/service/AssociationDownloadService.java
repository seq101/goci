package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Variant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwelter on 09/04/15.
 * <p>
 * This is a service class to process a set of associations for a given study and output the result to a tsv file
 */


@Service
public class AssociationDownloadService {

    public AssociationDownloadService() {

    }


    public void createDownloadFile(OutputStream outputStream, Collection<Association> associations)
            throws IOException {

        String file = processAssociations(associations);

        // Write file
        outputStream.write(file.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

    private String processAssociations(Collection<Association> associations) {

        String header =
                "Gene\tStrongest Variant-Effect Allele\tVariant\tProxy Variant\tIndependent Variant effect allele frequency in controls\tEffect element (allele, haplotype or VariantxVariant interaction) frequency in controls\tP-value mantissa\tP-value exponent\tP-value (Text)\tOR per copy or beta (Num)\tOR entered (reciprocal)\tOR-type? (Y/N)\tMulti-Variant Haplotype?\tVariant:Variant interaction?\tConfidence Interval\tReciprocal confidence interval\tBeta unit and direction\tStandard Error\tVariant type (novel/known)\tVariant Status\tEFO traits\r\n";


        StringBuilder output = new StringBuilder();
        output.append(header);


        for (Association association : associations) {
            StringBuilder line = new StringBuilder();

            extractGeneticData(association, line);

            if (association.getEffectAlleleFrequency() == null) {
                line.append("");
            }
            else {
                line.append(association.getEffectAlleleFrequency());

            }
            line.append("\t");

            if (association.getPvalueMantissa() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueMantissa());

            }
            line.append("\t");

            if (association.getPvalueExponent() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueExponent());
            }
            line.append("\t");

            if (association.getPvalueText() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueText());
            }
            line.append("\t");

            if (association.getOrPerCopyNum() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyNum());
            }
            line.append("\t");

            if (association.getOrPerCopyRecip() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRecip());
            }
            line.append("\t");

            if (association.getOrType() == null) {
                line.append("");

            }
            else {
                if (association.getOrType()) {
                    line.append("Y");
                }
                else {
                    line.append("N");
                }
            }
            line.append("\t");

            if (association.getMultiVariantHaplotype() == null) {
                line.append("");
            }
            else {
                if (association.getMultiVariantHaplotype()) {
                    line.append("Y");
                }
                else {
                    line.append("N");
                }
            }
            line.append("\t");

            if (association.getVariantInteraction() == null) {
                line.append("");
            }
            else {
                if (association.getVariantInteraction()) {
                    line.append("Y");
                }
                else {
                    line.append("N");
                }
            }
            line.append("\t");

            if (association.getOrPerCopyRange() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRange());
            }
            line.append("\t");

            if (association.getOrPerCopyRecipRange() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRecipRange());
            }
            line.append("\t");


            if (association.getOrPerCopyUnitDescr() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyUnitDescr());
            }

            line.append("\t");

            if (association.getOrPerCopyStdError() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyStdError());
            }

            line.append("\t");

            if (association.getVariantType() == null) {
                line.append("");
            }
            else {
                line.append(association.getVariantType());
            }
            line.append("\t");

            // Variant Status
            extractVariantStatus(association, line);

            if (association.getEfoTraits() == null) {
                line.append("");
            }
            else {
                extractEfoTraits(association.getEfoTraits(), line);
            }
            line.append("\r\n");

            output.append(line.toString());

        }

        return output.toString();
    }

    private void extractVariantStatus(Association association, StringBuilder line) {

        final StringBuilder variantStatuses = new StringBuilder();

        // Only applies to Variant interaction studies, delimiter used is 'x'
        if (association.getVariantInteraction() != null && association.getVariantInteraction()) {
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestEffectAlleles().forEach(
                                effectAllele -> {

                                    // Genome wide Vs Limited List,
                                    // create a comma separated list per
                                    // effect allele
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
                                    }
                                    else { commaSeparatedVariantStatus = "NR";}

                                    setOrAppend(variantStatuses, commaSeparatedVariantStatus, " x ");
                                }
                        );
                    }
            );
        }

        line.append(variantStatuses.toString());
        line.append("\t");
    }

    private void extractEfoTraits(Collection<EfoTrait> efoTraits, StringBuilder line) {
        StringBuilder traits = new StringBuilder();
        for (EfoTrait efoTrait : efoTraits) {
            String uri = efoTrait.getUri();
            String[] elements = uri.split("/");

            String id = elements[elements.length - 1];
            setOrAppend(traits, id.trim(), ",");
        }

        line.append(traits.toString());
    }

    private void extractGeneticData(Association association, StringBuilder line) {
        final StringBuilder strongestAllele = new StringBuilder();
        final StringBuilder reportedGenes = new StringBuilder();
        final StringBuilder externalId = new StringBuilder();
        final StringBuilder proxyVariantsExternalIds = new StringBuilder();
        final StringBuilder effectAlleleFrequency = new StringBuilder();

        // Set our delimiter for download spreadsheet
        final String delimiter;
        if (association.getVariantInteraction() != null && association.getVariantInteraction()) {
            delimiter = " x ";
        }
        else {
            delimiter = "; ";
        }

        // Interaction specific values
        if (association.getVariantInteraction() != null && association.getVariantInteraction()) {

            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestEffectAlleles().forEach(
                                effectAllele -> {

                                    // Set Effect allele frequency
                                    if (effectAllele.getEffectFrequency() != null &&
                                            !effectAllele.getEffectFrequency().isEmpty()) {
                                        String frequency = effectAllele.getEffectFrequency();
                                        setOrAppend(effectAlleleFrequency, frequency, delimiter);
                                    }
                                    else {
                                        setOrAppend(effectAlleleFrequency, "NR", delimiter);
                                    }

                                }
                        );

                        // Handle locus genes for Variant interaction studies.
                        // This is so it clear in the download which group
                        // of genes belong to which interaction
                        Collection<String> currentLocusGenes = new ArrayList<>();
                        String commaSeparatedGenes = "";
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            currentLocusGenes.add(gene.getGeneName().trim());
                        });
                        if (!currentLocusGenes.isEmpty()) {
                            commaSeparatedGenes = String.join(", ", currentLocusGenes);
                            setOrAppend(reportedGenes, commaSeparatedGenes, delimiter);
                        }
                        else {
                            setOrAppend(reportedGenes, "NR", delimiter);
                        }
                    }
            );
        }
        else {
            // Single study or a haplotype
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestEffectAlleles().forEach(
                                effectAllele -> {

                                    // Set Effect allele frequency to blank as its not recorded by curators
                                    // for standard or multi-Variant haplotypes
                                    setOrAppend(effectAlleleFrequency, "", "");

                                }
                        );
                        // For a haplotype all genes are separated by a comma
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            setOrAppend(reportedGenes, gene.getGeneName().trim(), ", ");
                        });
                    }
            );
        }

        // Set attributes common to all associations
        association.getLoci().forEach(
                locus -> {
                    locus.getStrongestEffectAlleles().forEach(
                            effectAllele -> {
                                setOrAppend(strongestAllele, effectAllele.getEffectAlleleName(), delimiter);

                                Variant variant = effectAllele.getVariant();
                                setOrAppend(externalId, variant.getExternalId(), delimiter);

                                // Set proxies or 'NR' if non available
                                Collection<String> currentLocusProxies = new ArrayList<>();
                                String colonSeparatedProxies = "";
                                if (effectAllele.getProxyVariants() != null) {
                                    for (Variant proxyVariant : effectAllele.getProxyVariants()) {
                                        currentLocusProxies.add(proxyVariant.getExternalId());
                                    }
                                }

                                // Separate multiple proxies linked by comma
                                if (!currentLocusProxies.isEmpty()) {
                                    colonSeparatedProxies = String.join(", ", currentLocusProxies);
                                    setOrAppend(proxyVariantsExternalIds, colonSeparatedProxies, delimiter);

                                }
                                else {
                                    setOrAppend(proxyVariantsExternalIds, "NR", delimiter);
                                }


                            }
                    );

                }
        );


        line.append(reportedGenes.toString());
        line.append("\t");
        line.append(strongestAllele.toString());
        line.append("\t");
        line.append(externalId.toString());
        line.append("\t");
        line.append(proxyVariantsExternalIds.toString());
        line.append("\t");
        line.append(effectAlleleFrequency.toString());
        line.append("\t");

    }


    private void setOrAppend(StringBuilder current, String toAppend, String delim) {
        if (toAppend != null && !toAppend.isEmpty()) {
            if (current.length() == 0) {
                current.append(toAppend);
            }
            else {
                current.append(delim).append(toAppend);
            }
        }
    }
}