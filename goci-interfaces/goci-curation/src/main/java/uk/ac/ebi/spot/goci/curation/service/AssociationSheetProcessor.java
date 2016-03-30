package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all the association records For each Variant, an
 *         VariantAssociationForm object is created and passed back to the controller for further processing
 *         <p>
 *         Created from code originally written by Dani/Tony. Adapted to fit with new curation system.
 */
@Service
public class AssociationSheetProcessor {

    // Services
    private AssociationCalculationService associationCalculationService;
    private LociAttributesService lociAttributesService;

    // Repository
    private EfoTraitRepository efoTraitRepository;
    private LocusRepository locusRepository;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());
    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationSheetProcessor(AssociationCalculationService associationCalculationService,
                                     LociAttributesService lociAttributesService,
                                     EfoTraitRepository efoTraitRepository,
                                     LocusRepository locusRepository) {

        this.associationCalculationService = associationCalculationService;
        this.lociAttributesService = lociAttributesService;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
    }


    // Read and parse uploaded spreadsheet
    public Collection<Association> readVariantAssociations(XSSFSheet sheet) {

        // Create collection to store all newly created associations
        Collection<Association> newAssociations = new ArrayList<>();

        boolean done = false;
        int rowNum = 1;

        while (!done) {
            XSSFRow row = sheet.getRow(rowNum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
                logMessage = "All spreadsheet data processed successfully";
            }
            else {

                // Get gene values
                String authorReportedGene = null;
                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";
                }

                // Get Strongest VARIANT-Effect Allele
                String strongestAllele = null;
                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Effect allele' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("Effect allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Effect allele' in row " + rowNum + 1 + "\n";
                }


                // Get VARIANT
                String variant = null;
                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    variant = row.getCell(2).getRichStringCellValue().getString();
                    logMessage = "Error in field 'VARIANT' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("VARIANT is null in row " + row.getRowNum());
                    logMessage = "Error in field 'VARIANT' in row " + rowNum + 1 + "\n";

                }

                // Get Proxy VARIANT
                String proxy = null;
                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    proxy = row.getCell(3).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Proxy VARIANT' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("VARIANT is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Proxy VARIANT' in row " + rowNum + 1 + "\n";

                }

                // Get Effect Allele Frequency, will contain multiple values for haplotype or interaction
                String effectFrequency = null;
                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell effect = row.getCell(4);
                    switch (effect.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            effectFrequency = effect.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Effect Frequency' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            effectFrequency = Double.toString(effect.getNumericCellValue());
                            logMessage =
                                    "Error in field 'Effect Allele Frequency in Controls' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Effect Allele Frequency in Controls' in row " + rowNum + 1 + "\n";
                }

                // Will be a single value that applies to association
                String associationEffectAlleleFrequency = null;
                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell effect = row.getCell(5);
                    switch (effect.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            associationEffectAlleleFrequency = effect.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Interacting VARIANTs combined effect allele frequency' in row " +
                                    rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            associationEffectAlleleFrequency = Double.toString(effect.getNumericCellValue());
                            logMessage = "Error in field 'Interacting VARIANTs combined effect allele frequency' in row " +
                                    rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Effect Frequency' in row " + rowNum + 1 + "\n";
                }

                // Get P-value mantissa	and P-value exponent
                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(6);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                }

                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(7);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";
                }

                // Get P-value (Text)
                String pvalueText;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = row.getCell(8).getRichStringCellValue().getString();
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";

                }
                else {
                    pvalueText = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";
                }

                // Get OR per copy or beta (Num)
                Float orPerCopyNum = null;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(9);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = (float) or.getNumericCellValue();
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                }

                // Get OR entered (reciprocal)
                Float orPerCopyRecip = null;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(10);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = (float) recip.getNumericCellValue();
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";

                }


                String orType;
                if (row.getCell(11, row.RETURN_BLANK_AS_NULL) != null) {
                    orType = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }
                else {
                    orType = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }

                // Get Multi-VARIANT Haplotype value
                String multiVariantHaplotype;
                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    multiVariantHaplotype = row.getCell(12).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Multi-VARIANT Haplotype' in row " + rowNum + 1 + "\n";
                }
                else {
                    multiVariantHaplotype = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Multi-VARIANT Haplotype' in row " + rowNum + 1 + "\n";
                }

                // Get VARIANT interaction value
                String variantInteraction;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    variantInteraction = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'VARIANT:VARIANT interaction' in row " + rowNum + 1 + "\n";
                }
                else {
                    variantInteraction = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'VARIANT:VARIANT interaction' in row " + rowNum + 1 + "\n";
                }

                // Get Confidence Interval/Range
                String orPerCopyRange;
                if (row.getCell(14, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRange = row.getCell(14).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }

                String orPerCopyRecipRange;
                if (row.getCell(15, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRecipRange = row.getCell(15).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Reciprocal CI' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyRecipRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Reciprocal CI' in row " + rowNum + 1 + "\n";
                }

                // Get Beta unit and direction/description
                String orPerCopyUnitDescr;
                if (row.getCell(16) != null) {
                    orPerCopyUnitDescr = row.getCell(16).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }

                // Get standard error
                Float orPerCopyStdError = null;
                if (row.getCell(17, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(17);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyStdError = null;
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyStdError = (float) std.getNumericCellValue();
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyStdError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                }

                // Get VARIANT type (novel / known)
                String variantType;
                if (row.getCell(18, row.RETURN_BLANK_AS_NULL) != null) {
                    variantType = row.getCell(18).getRichStringCellValue().getString().toLowerCase();
                    logMessage = "Error in field 'VARIANT type' in row " + rowNum + 1 + "\n";
                }
                else {
                    variantType = null;
                    getLog().debug("VARIANT type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'VARIANT type' in row " + rowNum + 1 + "\n";
                }

                // Get SNVARIANTP Status
                String variantStatus;
                if (row.getCell(19, row.RETURN_BLANK_AS_NULL) != null) {
                    variantStatus = row.getCell(19).getRichStringCellValue().getString().toLowerCase();
                    logMessage = "Error in field 'VARIANT type' in row " + rowNum + 1 + "\n";
                }
                else {
                    variantStatus = null;
                    getLog().debug("VARIANT type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'VARIANT type' in row " + rowNum + 1 + "\n";
                }

                String efoTrait;
                if (row.getCell(20, row.RETURN_BLANK_AS_NULL) != null) {
                    efoTrait = row.getCell(20).getRichStringCellValue().getString();
                    logMessage = "Error in field 'EFO traits' in row " + rowNum + 1 + "\n";
                }
                else {
                    efoTrait = null;
                    getLog().debug("EFO trait is null in row " + row.getRowNum());
                    logMessage = "Error in field 'EFO trait' in row " + rowNum + 1 + "\n";
                }


                // Once we have all the values entered in file process them
                if (authorReportedGene == null && strongestAllele == null && variant == null && proxy == null &&
                        effectFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                }
                else {

                    Association newAssociation = new Association();

                    // Set EFO traits
                    if (efoTrait != null) {
                        String[] uris = efoTrait.split(",");
                        Collection<String> efoUris = new ArrayList<>();

                        for (String uri : uris) {
                            String trimmedUri = uri.trim();
                            efoUris.add(trimmedUri);
                        }

                        Collection<EfoTrait> efoTraits = getEfoTraitsFromRepository(efoUris);

                        newAssociation.setEfoTraits(efoTraits);
                    }

                    // Set values common to all association types
                    newAssociation.setEffectAlleleFrequency(associationEffectAlleleFrequency);
                    newAssociation.setPvalueMantissa(pvalueMantissa);
                    newAssociation.setPvalueExponent(pvalueExponent);
                    newAssociation.setPvalueText(pvalueText);
                    newAssociation.setOrPerCopyRecip(orPerCopyRecip);
                    newAssociation.setOrPerCopyStdError(orPerCopyStdError);
                    newAssociation.setOrPerCopyRecipRange(orPerCopyRecipRange);
                    newAssociation.setOrPerCopyUnitDescr(orPerCopyUnitDescr);
                    newAssociation.setVariantType(variantType);

                    boolean recipReverse = false;
                    // Calculate OR per copy num
                    if ((orPerCopyRecip != null) && (orPerCopyNum == null)) {
                        orPerCopyNum = ((100 / orPerCopyRecip) / 100);
                        newAssociation.setOrPerCopyNum(orPerCopyNum);
                        recipReverse = true;
                    }
                    // Otherwise set to whatever is in upload
                    else {
                        newAssociation.setOrPerCopyNum(orPerCopyNum);
                    }

                    // This logic is retained from Dani's original code
                    if ((orPerCopyRecipRange != null) && recipReverse) {
                        orPerCopyRange = associationCalculationService.reverseCI(orPerCopyRecipRange);
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
                    }
                    else if ((orPerCopyRange == null) && (orPerCopyStdError != null)) {
                        orPerCopyRange = associationCalculationService.setRange(orPerCopyStdError, orPerCopyNum);
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
                    }
                    else {
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
                    }

                    if (orType.equalsIgnoreCase("Y")) {
                        newAssociation.setOrType(true);
                    }
                    else {
                        newAssociation.setOrType(false);
                    }

                    if (multiVariantHaplotype.equalsIgnoreCase("Y")) {
                        newAssociation.setMultiVariantHaplotype(true);
                    }
                    else {
                        newAssociation.setMultiVariantHaplotype(false);
                    }

                    if (variantInteraction.equalsIgnoreCase("Y")) {
                        newAssociation.setVariantInteraction(true);
                    }
                    else {
                        newAssociation.setVariantInteraction(false);
                    }

                    String delimiter;
                    Collection<Locus> loci = new ArrayList<>();

                    if (newAssociation.getVariantInteraction()) {
                        delimiter = "x";

                        // For SNP interaction studies we need to create a locus per effect allele
                        // Handle curator entered effect allele
                        Collection<EffectAllele> locusEffectAlleles =
                                createLocusEffectAlleles(strongestAllele,
                                                         variant,
                                                         proxy,
                                                         effectFrequency,
                                                         variantStatus,
                                                         delimiter);

                        // Add genes to relevant loci, split by 'x' delimiter first
                        Collection<Locus> lociWithAddedGenes = new ArrayList<>();

                        // Deal with genes for each interaction which should be
                        // separated by 'x'
                        String[] separatedGenes = authorReportedGene.split(delimiter);
                        int geneIndex = 0;

                        for (EffectAllele effectAllele : locusEffectAlleles) {
                            Locus locus = new Locus();

                            // Set effect alleles, assume one locus per effect allele
                            Collection<EffectAllele> currentLocusEffectAlleles = new ArrayList<>();
                            currentLocusEffectAlleles.add(effectAllele);
                            locus.setStrongestEffectAlleles(currentLocusEffectAlleles);

                            // Set gene
                            String interactionGene = separatedGenes[geneIndex];
                            Collection<Gene> locusGenes = createLocusGenes(interactionGene, ",");
                            locus.setAuthorReportedGenes(locusGenes);
                            geneIndex++;

                            // Set description
                            locus.setDescription("VARIANT x VARIANT interaction");

                            // Save our newly created locus
                            locusRepository.save(locus);
                            loci.add(locus);
                        }
                    }

                    // Handle multi-variant and standard variant
                    else {
                        delimiter = ";";

                        // For multi-variant and standard variants we assume their is only one locus
                        Locus locus = new Locus();

                        // Handle curator entered genes, for haplotype they are separated by a comma
                        Collection<Gene> locusGenes = createLocusGenes(authorReportedGene, ",");
                        locus.setAuthorReportedGenes(locusGenes);

                        // Handle curator entered effect allele
                        Collection<EffectAllele> locusEffectAlleles =
                                createLocusEffectAlleles(strongestAllele,
                                                         variant,
                                                         proxy,
                                                         effectFrequency,
                                                         variantStatus,
                                                         delimiter);


                        // For standard associations set the effect allele frequency to the
                        // same value as the overall association frequency
                        Collection<EffectAllele> locusEffectAllelesWithEffectFrequencyValues = new ArrayList<>();
                        if (!newAssociation.getMultiVariantHaplotype()) {
                            for (EffectAllele effectAllele : locusEffectAlleles) {
                                effectAllele.setEffectFrequency(associationEffectAlleleFrequency);
                                locusEffectAllelesWithEffectFrequencyValues.add(effectAllele);
                            }
                            locus.setStrongestEffectAlleles(locusEffectAllelesWithEffectFrequencyValues);
                        }

                        else {
                            locus.setStrongestEffectAlleles(locusEffectAlleles);
                        }

                        // Set locus attributes
                        Integer haplotypeCount = locusEffectAlleles.size();
                        if (haplotypeCount > 1) {
                            locus.setHaplotypeVariantCount(haplotypeCount);
                            locus.setDescription(String.valueOf(haplotypeCount) + "-variant haplotype");
                        }

                        else {
                            locus.setDescription("Single variant");
                        }

                        // Save our newly created locus
                        locusRepository.save(locus);
                        loci.add(locus);
                    }

                    newAssociation.setLoci(loci);

                    // Add all newly created associations to collection
                    newAssociations.add(newAssociation);
                }
            }
            rowNum++;
        }

        return newAssociations;
    }

    private Collection<EffectAllele> createLocusEffectAlleles(String strongestAllele,
                                                              String variant,
                                                              String proxy,
                                                              String effectFrequency,
                                                              String variantStatus,
                                                              String delimiter) {


        Collection<EffectAllele> locusEffectAlleles = new ArrayList<>();
        // For our list of variants, proxies and effect alleles separate by delimiter
        List<String> variants = new ArrayList<>();
        String[] separatedVariants = variant.split(delimiter);
        for (String separatedVariant : separatedVariants) {
            variants.add(separatedVariant.trim());
        }

        List<String> effectAlleles = new ArrayList<>();
        String[] separatedEffectAlleles = strongestAllele.split(delimiter);
        for (String separatedEffectAllele : separatedEffectAlleles) {
            effectAlleles.add(separatedEffectAllele.trim());
        }

        List<String> proxies = new ArrayList<>();
        String[] separatedProxies = proxy.split(delimiter);
        for (String separatedProxy : separatedProxies) {
            proxies.add(separatedProxy.trim());
        }

        // Value is only recorded for VARIANT interaction associations
        List<String> effectFrequencies = new ArrayList<>();
        Iterator<String> effectFrequencyIterator = null;
        if (effectFrequency != null) {
            String[] separatedVariantFrequencies = effectFrequency.split(delimiter);
            for (String separatedVariantFrequency : separatedVariantFrequencies) {
                effectFrequencies.add(separatedVariantFrequency.trim());
            }
            effectFrequencyIterator = effectFrequencies.iterator();
        }

        // Variant status
        List<String> variantStatuses = new ArrayList<>();
        Iterator<String> variantStatusIterator = null;
        if (variantStatus != null) {
            String[] separatedVariantStatuses = variantStatus.split(delimiter);
            for (String separatedVariantStatus : separatedVariantStatuses) {
                variantStatuses.add(separatedVariantStatus.trim());
            }
            variantStatusIterator = variantStatuses.iterator();
        }

        Iterator<String> variantAlleleIterator = effectAlleles.iterator();
        Iterator<String> variantIterator = variants.iterator();
        Iterator<String> proxyIterator = proxies.iterator();

        // Loop through our effect alleles
        if (effectAlleles.size() == variants.size()) {

            while (variantAlleleIterator.hasNext()) {

                String variantValue = variantIterator.next().trim();
                String effectAlleleValue = variantAlleleIterator.next().trim();
                String proxyValue = proxyIterator.next().trim();

                Variant newVariant = lociAttributesService.createVariant(variantValue);

                // Create a new effect allele and assign newly created variant
                EffectAllele newEffectAllele = lociAttributesService.createEffectAllele(effectAlleleValue, newVariant);

                // Check for proxies and if we have one create a proxy variant
                Collection<Variant> newEffectAlleleProxies = new ArrayList<>();
                if (proxyValue.contains(":")) {
                    String[] splitProxyValues = proxyValue.split(":");

                    for (String splitProxyValue : splitProxyValues) {
                        Variant proxyVariant = lociAttributesService.createVariant(splitProxyValue.trim());
                        newEffectAlleleProxies.add(proxyVariant);
                    }
                }

                else if (proxyValue.contains(",")) {
                    String[] splitProxyValues = proxyValue.split(",");

                    for (String splitProxyValue : splitProxyValues) {
                        Variant proxyVariant = lociAttributesService.createVariant(splitProxyValue.trim());
                        newEffectAlleleProxies.add(proxyVariant);
                    }
                }

                else {
                    Variant proxyVariant = lociAttributesService.createVariant(proxyValue);
                    newEffectAlleleProxies.add(proxyVariant);
                }
                newEffectAllele.setProxyVariants(newEffectAlleleProxies);

                // If there is no curator entered value for effect allele frequency don't save
                String effectFrequencyValue = null;
                if (effectFrequencyIterator != null) {
                    effectFrequencyValue = effectFrequencyIterator.next().trim();
                }
                if (effectFrequencyValue != null) {
                    newEffectAllele.setEffectFrequency(effectFrequencyValue);
                }

                // Handle variant statuses, these should only apply to VARIANT interaction associations
                String variantStatusValue = null;
                if (variantStatusIterator != null) {
                    variantStatusValue = variantStatusIterator.next().trim();
                }

                if (variantStatus != null && !variantStatus.equalsIgnoreCase("NR")) {
                    if (variantStatusValue.contains("GW") || variantStatusValue.contains("gw")) {
                        newEffectAllele.setGenomeWide(true);
                    }
                    if (variantStatusValue.contains("LL") || variantStatusValue.contains("ll")) {
                        newEffectAllele.setLimitedList(true);
                    }
                }

                locusEffectAlleles.add(newEffectAllele);
            }
        }
        else {
            getLog().error("Mismatched number of variants and effect alleles");
        }

        return locusEffectAlleles;
    }

    private Collection<Gene> createLocusGenes(String authorReportedGene, String delimiter) {

        String[] genes = authorReportedGene.split(delimiter);
        Collection<String> genesToCreate = new ArrayList<>();

        for (String gene : genes) {
            String trimmedGene = gene.trim();
            genesToCreate.add(trimmedGene);
        }

        return lociAttributesService.createGene(genesToCreate);
    }

    private Collection<EfoTrait> getEfoTraitsFromRepository(Collection<String> efoUris) {
        Collection<EfoTrait> efoTraits = new ArrayList<>();
        for (String uri : efoUris) {
            String fullUri;
            if (uri.contains("EFO")) {
                fullUri = "http://www.ebi.ac.uk/efo/".concat(uri);
            }
            else if (uri.contains("Orphanet")) {
                fullUri = "http://www.orpha.net/ORDO/".concat(uri);
            }
            else {
                fullUri = "http://purl.obolibrary.org/obo/".concat(uri);
            }

            Collection<EfoTrait> traits = efoTraitRepository.findByUri(fullUri);

            for (EfoTrait trait : traits) {
                efoTraits.add(trait);
            }
        }
        return efoTraits;
    }


    public String getLogMessage() {
        return logMessage;
    }


}

