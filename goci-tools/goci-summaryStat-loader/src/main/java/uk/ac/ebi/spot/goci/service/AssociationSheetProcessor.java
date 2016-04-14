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
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by catherineleroy on 12/04/2016.
 */
@Service
public class AssociationSheetProcessor {
    // Services
    private LociAttributesService lociAttributesService;

    // Repository
    private LocusRepository locusRepository;


    private AssociationRepository associationRepository;


    private StudyRepository studyRepository;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());
//    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationSheetProcessor(LociAttributesService lociAttributesService,
                                     LocusRepository locusRepository,
                                     AssociationRepository associationRepository,
                                     StudyRepository studyRepository) {

        this.lociAttributesService = lociAttributesService;
        this.locusRepository = locusRepository;
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
    }


    // Read and parse uploaded spreadsheet
    public void readVariantAssociations(String file, String studyId) throws IOException {

        Study study = studyRepository.findOne(Long.parseLong(studyId));


        // Create collection to store all newly created associations
        Collection<Association> newAssociations = new ArrayList<>();

        String line;

        BufferedReader br = new BufferedReader(new FileReader(file));

        int rowNum = 0;
        startChrono();
        while ((line = br.readLine()) != null) {

            if((rowNum % 1000) == 0){
                stopChrono();
                System.out.println(rowNum);
                startChrono();
            }


////            System.out.println(line);
//            if(line.startsWith("#")){
//                continue;
//            }
            String[] cells = line.split("\t");

//            if(cells.length == 0){
//                continue;
//            }

            //To store in locus table (chromosome_name)
            String columnName = "chromosome";
            int colNum = 0;
            String chromosome = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                chromosome = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
            //                getLog().error("\n\nchromosome = " + chromosome);

            //To store in locus table (chromosome_position)
            columnName = "position";
            colNum = 1;
            String position = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                position = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("position = " + position);


            //variant id, rsId
            columnName = "variantExternalId";
            colNum = 2;
            String variantExternalId = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if ( !cells[colNum].isEmpty()) {
                variantExternalId = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("variantExternalId = " + variantExternalId);

            columnName = "effectAllele";
            colNum = 3;
            String effectAllele = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                effectAllele = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("effectAllele = " + effectAllele);


            columnName = "otherAllele";
            colNum = 4;
            String otherAllele = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                otherAllele = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("otherAllele = " + otherAllele);


            columnName = "associationEffectAlleleFrequency";
            colNum = 5;
            String associationEffectAlleleFrequency = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                associationEffectAlleleFrequency = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("associationEffectAlleleFrequency = " + associationEffectAlleleFrequency);


            columnName = "associationEffectAlleleFrequencyCases";
            colNum = 6;
            String associationEffectAlleleFrequencyCases = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                associationEffectAlleleFrequencyCases = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("associationEffectAlleleFrequencyCases = " + associationEffectAlleleFrequencyCases);


            colNum = 7;
            columnName = "associationEffectAlleleFrequencyControls";
            String associationEffectAlleleFrequencyControls = null;
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                associationEffectAlleleFrequencyControls = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("associationEffectAlleleFrequencyControls = " + associationEffectAlleleFrequencyControls);


            // also called INFO
            colNum = 8;
            columnName = "imputationQualityScore";
            BigDecimal imputationQualityScore =  null;
//            if (cells[colNum] != null  && !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                imputationQualityScore = new BigDecimal(cells[colNum]);
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("imputationQualityScore = " + imputationQualityScore);


            columnName = "standardError";
            colNum = 9;
            BigDecimal standardError =  new BigDecimal(cells[colNum]);
//            if (cells[colNum] != null  && !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                standardError = new BigDecimal(cells[colNum]);
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("standardError = " + standardError);


            columnName = "pvalueText";
            Integer pvalueExponent = null;
            Integer pvalueMantissa = null;
            colNum = 10;
            String pvalueText = null;
//            if (cells[colNum] == null || cells[colNum].isEmpty()) {
            if (cells[colNum].isEmpty()) {
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }else{
                pvalueText = cells[colNum];
                String[] pvalueBreakout = pvalueText.split("e");
                if (pvalueBreakout.length == 2) {
                    pvalueExponent = Integer.parseInt(pvalueBreakout[1]);

                    if(!pvalueBreakout[0].contains(".")){
                        pvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                    }else{
                        Double d = new Double(pvalueBreakout[0]);
                        pvalueMantissa = (int) Math.round(d);
                    }

                }
                else {
                    pvalueBreakout = pvalueText.split("E");
                    if (pvalueBreakout.length == 2) {
                        pvalueExponent = Integer.parseInt(pvalueBreakout[1]);
                        if(!pvalueBreakout[0].contains(".")){
                            pvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                        }else{
                            Double d = new Double(pvalueBreakout[0]);
                            pvalueMantissa = (int) Math.round(d);
                        }
                    }else {
                        Double d = Double.parseDouble(pvalueText);

                        NumberFormat formatter = new DecimalFormat();
                        formatter = new DecimalFormat("0E0000");
                        String scientificNotation = formatter.format(d);

                        String[] split = scientificNotation.split("E");

                        pvalueMantissa = Integer.parseInt(split[0]);

                        pvalueExponent = Integer.parseInt(split[1]);

                        pvalueText = pvalueMantissa + "e" + pvalueExponent;

                    }
                    //                        getLog().debug(columnName + " with format problem in row " + row.getRowNum() + ". We were expecting a scientific notation (eg. : 1e-14)");
                    //                        logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
            }
//            getLog().error("pvalueText = " + pvalueText);
//            getLog().error("pvalueExponent = " + pvalueExponent);
//            getLog().error("pvalueMantissa = " + pvalueMantissa);


            columnName = "beta";
            colNum = 11;
            BigDecimal beta =  null;
//            if (cells[colNum] != null && !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                System.out.println(cells[colNum]);
                beta = new BigDecimal(cells[colNum]);
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("beta = " + beta);


            columnName = "metaDirection";
            colNum = 12;
            String metaDirection = cells[colNum];
//            if (cells[colNum] != null || !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                metaDirection = cells[colNum];
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("metaDirection = " + metaDirection);


            columnName = "heterogeneityScore";
            colNum = 13;
            BigDecimal heterogeneityScore =  null;
//            if (cells[colNum] != null  && !cells[colNum].isEmpty()) {
            if (!cells[colNum].isEmpty()) {
                heterogeneityScore = new BigDecimal(cells[colNum]);
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("heterogeneityScore = " + heterogeneityScore);


            Integer heterogeneityPvalueMantissa = null;
            Integer heterogeneityPvalueExponent = null;
            columnName = "heterogeneityPvalue";
            colNum = 14;
            String heterogeneityPvalue = null;
//            if (cells[colNum] == null || cells[colNum].isEmpty()) {
            if (cells[colNum].isEmpty()) {
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }else{
                heterogeneityPvalue = cells[colNum];
                String[] pvalueBreakout = heterogeneityPvalue.split("e");
                if (pvalueBreakout.length == 2) {
                    heterogeneityPvalueExponent = Integer.parseInt(pvalueBreakout[1]);
//                    heterogeneityPvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                    if(!pvalueBreakout[0].contains(".")){
                        heterogeneityPvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                    }else{
                        Double d = new Double(pvalueBreakout[0]);
                        heterogeneityPvalueMantissa = (int) Math.round(d);
                    }
                }
                else {
                    pvalueBreakout = heterogeneityPvalue.split("E");
                    if (pvalueBreakout.length == 2) {
                        heterogeneityPvalueExponent = Integer.parseInt(pvalueBreakout[1]);
//                        heterogeneityPvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                        if(!pvalueBreakout[0].contains(".")){
                            heterogeneityPvalueMantissa = Integer.parseInt(pvalueBreakout[0]);
                        }else{
                            Double d = new Double(pvalueBreakout[0]);
                            heterogeneityPvalueMantissa = (int) Math.round(d);
                        }
                    }else {
                        Double d = Double.parseDouble(heterogeneityPvalue);

                        NumberFormat formatter = new DecimalFormat();
                        formatter = new DecimalFormat("0E0000");
                        String scientificNotation = formatter.format(d);

                        String[] split = scientificNotation.split("E");

                        heterogeneityPvalueMantissa = Integer.parseInt(split[0]);

                        heterogeneityPvalueExponent = Integer.parseInt(split[1]);

                        pvalueText = heterogeneityPvalueMantissa + "e" + heterogeneityPvalueExponent;

                    }
                    //                        getLog().debug(columnName + " with format problem in row " + row.getRowNum() + ". We were expecting a scientific notation (eg. : 1e-14)");
                    //                        logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
                }
            }
//            getLog().error("heterogeneityPvalue = " + heterogeneityPvalue);
//            getLog().error("heterogeneityPvalueMantissa = " + heterogeneityPvalueMantissa);
//            getLog().error("heterogeneityPvalueExponent = " + heterogeneityPvalueExponent);




            columnName = "bayesFactorLog10";
            colNum = 15;
            BigDecimal bayesFactorLog10 =  null;
            if (cells[colNum] != null  && !cells[colNum].isEmpty() && cells[colNum].replaceAll(" ", "").length() != 0) {
                System.out.println("bayes="+cells[colNum]+"=");
                System.out.println("bayes length="+cells[colNum].replaceAll(" ", "").length()+"=");
                System.out.println("bayes no blank="+cells[colNum].replaceAll(" ", "")+"=");
                bayesFactorLog10 = new BigDecimal(cells[colNum]);
            }else{
                getLog().debug(columnName + " is null in row " + rowNum);
//                logMessage = "Error in field " + columnName +  " with column number " + colNum + " row number " + rowNum + 1;
            }
//            getLog().error("bayesFactorLog10 = " + bayesFactorLog10);


            Association newAssociation = new Association();

            if("NR".equals(associationEffectAlleleFrequency) && ("NR".equals(associationEffectAlleleFrequencyCases)  || "NR".equals(associationEffectAlleleFrequencyControls))){
                getLog().debug("The association effectAlleleFrequencyCases and controls can't be null if the association alleleFrequency is. Row num = " + rowNum + 1);
//                logMessage = "The association effectAlleleFrequencyCases and controls can't be null if the association alleleFrequency is. Row num = " + rowNum + 1;
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


            newAssociation.setStudy(study);

            newAssociations.add(newAssociation);

            if(newAssociations.size() == 100) {
                saveAssociation(newAssociations, studyId);
                newAssociations = new ArrayList<>();
            }



            // Add all newly created associations to collection
//            newAssociations.add(newAssociation);

            //            }
            rowNum++;
        }

//        return newAssociations;
    }


    public void saveAssociation(Collection<Association> associations, String studyId){
//        Study study = studyRepository.findOne(Long.parseLong(studyId));
        Study study = new Study();
        study.setId(Long.parseLong(studyId));

        associationRepository.save(associations);
//        for(Association association : associations) {
//            // Set the study ID for our association
//            association.setStudy(study);
//
//            // Save our association information
//            association.setLastUpdateDate(new Date());
//            associationRepository.save(association);
//
//            // Map RS_ID in association
//
//            //            Curator curator = study.getHousekeeping().getCurator();
//            //            String mappedBy = curator.getLastName();
//            //            try {
//            //                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
//            //            }
//            //            catch (EnsemblMappingException e) {
//            //                model.addAttribute("study", study);
//            //                return "ensembl_mapping_failure";
//            //            }
//        }


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


    static long chrono = 0 ;

    // Lancement du chrono
    static void startChrono() {
        chrono = java.lang.System.currentTimeMillis() ;
    }

    // Arret du chrono
    static void stopChrono() {
        long chrono2 = java.lang.System.currentTimeMillis() ;
        long temps = chrono2 - chrono ;
        System.out.println("Temps ecoule = " + temps + " ms") ;
    }

    //    public String getLogMessage() {
//        return logMessage;
//    }


}
