package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.AssociationSheetProcessor;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import uk.ac.ebi.spot.goci.service.StudyService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by catherineleroy on 16/02/2016.
 */
@SpringBootApplication
public class AssociationImporter {

    @Autowired
    private AssociationSheetProcessor associationSheetProcessor;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private StudyRepository studyRepository;

    private static String inputFilePath = "";
    private static String studyId = "";

    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();

            int parseArgs = parseArguments(strings);

            if (parseArgs == 0) {
                // execute publisher
                this.getAndSave(inputFilePath);
            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }

            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Got variant in " + time + " s. - application will now exit");
        };
    }


    public static void main(String[] args) {
        System.out.println("Starting Goci variant list builder...");
        ApplicationContext ctx = SpringApplication.run(AssociationImporter.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }


    private static int parseArguments(String[] args) {
        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.hasOption("")) {
                // print out mode help
                help.printHelp("publish", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)

                if (cl.hasOption("i")) {
                    inputFilePath = cl.getOptionValue("i");
                }
                else {
                    System.err.println("-i association file is required");
                    help.printHelp("import", options, true);
                    parseArgs += 2;
                }

                if (cl.hasOption("s")) {
                    studyId = cl.getOptionValue("s");
                }
                else {
                    System.err.println("-s study id is required");
                    help.printHelp("import", options, true);
                    parseArgs += 3;
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("import", options, true);
            parseArgs += 4;
        }
        return parseArgs;
    }


    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        Option studyIdOption = new Option("s",
                                          "studyId",
                                          true,
                                          "The id of the study those association to be loaded should be linked to.");
        studyIdOption.setRequired(true);
        studyIdOption.setArgName("studyId");
        options.addOption(studyIdOption);

        Option inputFileOption = new Option("i", "input", true,
                                            "The input file mapping variant variantId to ensembl gene Id");
        inputFileOption.setArgName("inputFile");
        inputFileOption.setRequired(true);
        options.addOption(inputFileOption);


        return options;
    }


    public void getAndSave(String inputFilePath) throws IOException, InvalidFormatException {


        // Open and parse our spreadsheet file
        XSSFSheet sheet;
        OPCPackage pkg = OPCPackage.open(inputFilePath);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        sheet = current.getSheetAt(0);
        Collection<Association> associations = associationSheetProcessor.readVariantAssociations(sheet);
        pkg.close();




        Study study = studyRepository.findOne(Long.parseLong(studyId));

        // Create our associations
        if (!associations.isEmpty()) {
            Collection<Association> associationsToMap = new ArrayList<>();
            for (Association newAssociation : associations) {

                // Set the study ID for our association
                newAssociation.setStudy(study);

                // Save our association information
                newAssociation.setLastUpdateDate(new Date());
                associationRepository.save(newAssociation);

                // Map RS_ID in association
                associationsToMap.add(newAssociation);
            }

            //            Curator curator = study.getHousekeeping().getCurator();
            //            String mappedBy = curator.getLastName();
            //            try {
            //                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            //            }
            //            catch (EnsemblMappingException e) {
            //                model.addAttribute("study", study);
            //                return "ensembl_mapping_failure";
            //            }
        }

    }


}