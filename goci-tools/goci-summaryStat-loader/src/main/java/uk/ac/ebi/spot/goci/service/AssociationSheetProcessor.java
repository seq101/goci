package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by catherineleroy on 12/04/2016.
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

            if (row == null || (row.getCell(0, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(1, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(2, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(3, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(4, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(5, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(6, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(7, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(8, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(9, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(10, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(11, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(12, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(13, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(14, row.RETURN_BLANK_AS_NULL) == null &&
                    row.getCell(15, row.RETURN_BLANK_AS_NULL) == null
            )) {
                done = true;
                getLog().debug("Last row read");
                logMessage = "All spreadsheet data processed successfully";
            }
            else {

                DataFormatter dataFormatter = new DataFormatter();

                //To store in locus table (chromosome_name)
                String chromosome = null;
                String columnName = "chromosome";
                int colNum = 0;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    chromosome = dataFormatter.formatCellValue(row.getCell(colNum));//
                    // .getStringCellValue();
                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            chromosome = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            chromosome = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }

                    //                    chromosome = row.getCell(colNum).getRichStringCellValue().getString();
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("\n\nchromosome = " + chromosome);

                //To store in locus table (chromosome_position)
                String position = null;
                columnName = "position";
                colNum = 1;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    position = row.getCell(colNum).getStringCellValue();

                    position = dataFormatter.formatCellValue(row.getCell(colNum));//

                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            position = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            position = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("position = " + position);


                //variant id, rsId
                String variantExternalId = null;
                columnName = "variantExternalId";
                colNum = 2;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    variantExternalId = row.getCell(colNum).getStringCellValue();

                    variantExternalId = dataFormatter.formatCellValue(row.getCell(colNum));//


                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            variantExternalId = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            variantExternalId = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("variantExternalId = " + variantExternalId);

                String effectAllele = null;
                columnName = "effectAllele";
                colNum = 3;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    //                    effectAllele = row.getCell(colNum).getStringCellValue();
                    effectAllele = dataFormatter.formatCellValue(row.getCell(colNum));//

                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("effectAllele = " + effectAllele);


                String otherAllele = null;
                columnName = "otherAllele";
                colNum = 4;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    otherAllele = row.getCell(colNum).getStringCellValue();
                    otherAllele = dataFormatter.formatCellValue(row.getCell(colNum));//

                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("otherAllele = " + otherAllele);


                String associationEffectAlleleFrequency = null;
                columnName = "associationEffectAlleleFrequency";
                colNum = 5;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {

                    associationEffectAlleleFrequency = dataFormatter.formatCellValue(row.getCell(colNum));//


                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    associationEffectAlleleFrequency = row.getCell(colNum).getStringCellValue();

                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            associationEffectAlleleFrequency = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            associationEffectAlleleFrequency = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("associationEffectAlleleFrequency = " + associationEffectAlleleFrequency);


                String associationEffectAlleleFrequencyCases = null;
                columnName = "associationEffectAlleleFrequencyCases";
                colNum = 6;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {

                    associationEffectAlleleFrequencyCases = dataFormatter.formatCellValue(row.getCell(colNum));//


                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    associationEffectAlleleFrequencyCases = row.getCell(colNum).getStringCellValue();

                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            associationEffectAlleleFrequencyCases = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            associationEffectAlleleFrequencyCases = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("associationEffectAlleleFrequencyCases = " + associationEffectAlleleFrequencyCases);


                String associationEffectAlleleFrequencyControls = null;
                colNum = 7;
                columnName = "associationEffectAlleleFrequencyControls";
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    associationEffectAlleleFrequencyControls = dataFormatter.formatCellValue(row.getCell(colNum));//

                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    associationEffectAlleleFrequencyControls = row.getCell(colNum).getStringCellValue();

                    //                    switch( row.getCell(colNum).getCellType()) {
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    //                            associationEffectAlleleFrequencyControls = row.getCell(colNum).getRichStringCellValue().toString();
                    //                            break;
                    //                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    //                            associationEffectAlleleFrequencyControls = Integer.toString((int) row.getCell(colNum).getNumericCellValue());
                    //                            break;
                    //                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("associationEffectAlleleFrequencyControls = " + associationEffectAlleleFrequencyControls);


                // also called INFO
                BigDecimal imputationQualityScore = null;
                colNum = 8;
                columnName = "imputationQualityScore";
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(colNum);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1 + " not a numerical value. ";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            imputationQualityScore = BigDecimal.valueOf (mant.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("imputationQualityScore = " + imputationQualityScore);


                BigDecimal standardError = null;
                columnName = "standardError";
                colNum = 9;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {

                    XSSFCell mant = row.getCell(colNum);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1 + " not a numerical value. ";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            standardError = BigDecimal.valueOf (mant.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("standardError = " + standardError);


                String pvalueText = null;
                columnName = "pvalueText";
                Integer pvalueExponent = null;
                Integer pvalueMantissa = null;
                colNum = 10;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = dataFormatter.formatCellValue(row.getCell(colNum));//

                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    pvalueText = row.getCell(colNum).getStringCellValue();

                    String[] pvalueBreakout = pvalueText.split("e");
                    if (pvalueBreakout.length == 2) {
                        pvalueExponent = Integer.parseInt(pvalueBreakout[1]);
                        pvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                    }
                    else {
                        getLog().debug(columnName + " with format problem in row " + row.getRowNum() + ". We were expecting a scientific notation (eg. : 1e-14)");
                        logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                    }
                } else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("pvalueText = " + pvalueText);
                getLog().error("pvalueExponent = " + pvalueExponent);
                getLog().error("pvalueMantissa = " + pvalueMantissa);


                BigDecimal beta = null;
                columnName = "beta";
                colNum = 11;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(colNum);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1 + " not a numerical value. ";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            beta = BigDecimal.valueOf (mant.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("beta = " + beta);


                String metaDirection = null;
                columnName = "metaDirection";
                colNum = 12;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    metaDirection = dataFormatter.formatCellValue(row.getCell(colNum));//

                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    metaDirection = row.getCell(colNum).getStringCellValue();
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("metaDirection = " + metaDirection);


                BigDecimal heterogeneityScore = null;
                columnName = "heterogeneityScore";
                colNum = 13;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(colNum);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1 + " not a numerical value. ";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            heterogeneityScore = BigDecimal.valueOf (mant.getNumericCellValue());

                            break;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("heterogeneityScore = " + heterogeneityScore);


                String heterogeneityPvalue = null;
                Integer heterogeneityPvalueMantissa = null;
                Integer heterogeneityPvalueExponent = null;
                columnName = "heterogeneityPvalue";
                colNum = 14;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {

                    heterogeneityPvalue = dataFormatter.formatCellValue(row.getCell(colNum));//


                    //                    row.getCell(colNum).setCellType(XSSFCell.CELL_TYPE_STRING);
                    //                    heterogeneityPvalue = row.getCell(colNum).getStringCellValue();

                    String[] pvalueBreakout = pvalueText.split("e");
                    if (pvalueBreakout.length == 2) {
                        heterogeneityPvalueExponent =Integer.parseInt(pvalueBreakout[1]);
                        heterogeneityPvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                    }
                    else {
                        getLog().debug(columnName + " with format problem in row " + row.getRowNum() + ". We were expecting a scientific notation (eg. : 1e-14)");
                        logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("heterogeneityPvalue = " + heterogeneityPvalue);
                getLog().error("heterogeneityPvalueMantissa = " + heterogeneityPvalueMantissa);
                getLog().error("heterogeneityPvalueExponent = " + heterogeneityPvalueExponent);




                BigDecimal bayesFactorLog10 = null;
                columnName = "bayesFactorLog10";
                colNum = 15;
                if (row.getCell(colNum, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(colNum);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1 + " not a numerical value. ";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            bayesFactorLog10 = BigDecimal.valueOf (mant.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug(columnName + " is null in row " + row.getRowNum());
                    logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
                getLog().error("bayesFactorLog10 = " + bayesFactorLog10);


                Association newAssociation = new Association();

                if("NR".equals(associationEffectAlleleFrequency) && ("NR".equals(associationEffectAlleleFrequencyCases)  || "NR".equals(associationEffectAlleleFrequencyControls))){
                    getLog().debug("The association effectAlleleFrequencyCases and controls can't be null if the association alleleFrequency is. Row num = " + rowNum + 1);
                    logMessage = "The association effectAlleleFrequencyCases and controls can't be null if the association alleleFrequency is. Row num = " + rowNum + 1;
                }
                if(!"NR".equals(associationEffectAlleleFrequency) && associationEffectAlleleFrequency != null){
                    newAssociation.setEffectAlleleFrequency(associationEffectAlleleFrequency);
                }
                if(!"NR".equals(associationEffectAlleleFrequencyCases)  && associationEffectAlleleFrequencyCases != null){
                    newAssociation.setEffectAlleleFrequencyCases(associationEffectAlleleFrequencyCases);
                }
                if(!"NR".equals(associationEffectAlleleFrequencyControls) && associationEffectAlleleFrequencyControls != null){
                    newAssociation.setEffectAlleleFreqControls(associationEffectAlleleFrequencyControls);
                }

                if( ! "NR".equals(imputationQualityScore) && imputationQualityScore != null) {
                    newAssociation.setImputationQualityScore(imputationQualityScore);
                }

                newAssociation.setStandardError(standardError);
                newAssociation.setPvalueText(pvalueText);
                newAssociation.setPvalueExponent(pvalueExponent);
                newAssociation.setPvalueMantissa(pvalueMantissa);

                if( ! "NR".equals(heterogeneityPvalue) && heterogeneityPvalue != null){
                    newAssociation.setHeterogeneityPvalueExponent(heterogeneityPvalueExponent);
                    newAssociation.setHeterogeneityPvalueMantissa(heterogeneityPvalueMantissa);
                    newAssociation.setHeterogeneityPvalueText(heterogeneityPvalue);
                }

                if( beta != null ){
                    newAssociation.setBeta(beta);
                }

                if( ! "NR".equals(metaDirection) && metaDirection != null){
                    newAssociation.setMetaDirection(metaDirection);
                }

                if(heterogeneityScore != null){
                    newAssociation.setHeterogeneityScore(heterogeneityScore);
                }

                if( bayesFactorLog10 != null){
                    newAssociation.setBayesFactorLog10(bayesFactorLog10);
                }







                // For SNP interaction studies we need to create a locus per effect allele
                // Handle curator entered effect allele
                EffectAllele locusEffectAllele =
                        createLocusEffectAllele(effectAllele,
                                                otherAllele,
                                                variantExternalId,
                                                chromosome,
                                                position
                        );




                Locus locus = new Locus();

                // Set effect alleles, assume one locus per effect allele
                Collection<EffectAllele> currentLocusEffectAlleles = new ArrayList<>();
                currentLocusEffectAlleles.add(locusEffectAllele);
                locus.setStrongestEffectAlleles(currentLocusEffectAlleles);

                //                            locus.setChromosomeName(chromosome);
                //                locus.setChromosomePosition(position);

                // Save our newly created locus
                locusRepository.save(locus);

                Collection<Locus> loci = new ArrayList<>();
                loci.add(locus);


                newAssociation.setLoci(loci);

                // Add all newly created associations to collection
                newAssociations.add(newAssociation);

            }
            rowNum++;
        }

        return newAssociations;
    }

    private EffectAllele createLocusEffectAllele(String effectAllele,
                                                 String otherAllele,
                                                 String variantExternalId,
                                                 String chromosome,
                                                 String position
    ) {

        Variant newVariant = lociAttributesService.createVariant(variantExternalId, chromosome, position);
        EffectAllele newEffectAllele = lociAttributesService.createEffectAllele(effectAllele, otherAllele, newVariant);

        return newEffectAllele;
    }



    public String getLogMessage() {
        return logMessage;
    }


}
