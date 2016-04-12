package uk.ac.ebi.spot.goci.curation.controller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.DataIntegrityException;
import uk.ac.ebi.spot.goci.curation.model.AssociationFormErrorView;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.VariantAssociationTableView;
import uk.ac.ebi.spot.goci.curation.model.VariantFormColumn;
import uk.ac.ebi.spot.goci.curation.model.VariantFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationBatchLoaderService;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationFormErrorViewService;
import uk.ac.ebi.spot.goci.curation.service.AssociationViewService;
import uk.ac.ebi.spot.goci.curation.service.CheckEfoTermAssignment;
import uk.ac.ebi.spot.goci.curation.service.LociAttributesService;
import uk.ac.ebi.spot.goci.curation.service.SingleVariantMultiVariantAssociationService;
import uk.ac.ebi.spot.goci.curation.service.VariantInteractionAssociationService;
import uk.ac.ebi.spot.goci.curation.validator.VariantFormColumnValidator;
import uk.ac.ebi.spot.goci.curation.validator.VariantFormRowValidator;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.VariantRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.MappingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 06/01/15.
 *
 * @author emma
 *         <p>
 *         Association controller, interpret user input and transform it into a variant/association model that is
 *         represented to the user by the associated HTML page. Used to view, add and edit existing variant/assocaition
 *         information.
 */

@Controller
public class AssociationController {

    // Repositories
    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EfoTraitRepository efoTraitRepository;
    private LocusRepository locusRepository;
    private AssociationReportRepository associationReportRepository;

    // Services
    private AssociationBatchLoaderService associationBatchLoaderService;
    private AssociationDownloadService associationDownloadService;
    private AssociationViewService associationViewService;
    private SingleVariantMultiVariantAssociationService singleVariantMultiVariantAssociationService;
    private VariantInteractionAssociationService variantInteractionAssociationService;
    private LociAttributesService lociAttributesService;
    private AssociationFormErrorViewService associationFormErrorViewService;
    private CheckEfoTermAssignment checkEfoTermAssignment;

    // Validators
    private VariantFormRowValidator variantFormRowValidator;
    private VariantFormColumnValidator variantFormColumnValidator;

    // Uses goci-mapper service to run mapping
    private MappingService mappingService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationController(AssociationRepository associationRepository,
                                 StudyRepository studyRepository,
                                 EfoTraitRepository efoTraitRepository,
                                 LocusRepository locusRepository,
                                 VariantRepository variantRepository,
                                 AssociationReportRepository associationReportRepository,
                                 AssociationBatchLoaderService associationBatchLoaderService,
                                 AssociationDownloadService associationDownloadService,
                                 AssociationViewService associationViewService,
                                 SingleVariantMultiVariantAssociationService singleVariantMultiVariantAssociationService,
                                 VariantInteractionAssociationService variantInteractionAssociationService,
                                 LociAttributesService lociAttributesService,
                                 AssociationFormErrorViewService associationFormErrorViewService,
                                 VariantFormRowValidator variantFormRowValidator,
                                 VariantFormColumnValidator variantFormColumnValidator,
                                 MappingService mappingService, CheckEfoTermAssignment checkEfoTermAssignment) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
        this.associationReportRepository = associationReportRepository;
        this.associationBatchLoaderService = associationBatchLoaderService;
        this.associationDownloadService = associationDownloadService;
        this.associationViewService = associationViewService;
        this.singleVariantMultiVariantAssociationService = singleVariantMultiVariantAssociationService;
        this.variantInteractionAssociationService = variantInteractionAssociationService;
        this.lociAttributesService = lociAttributesService;
        this.associationFormErrorViewService = associationFormErrorViewService;
        this.variantFormRowValidator = variantFormRowValidator;
        this.variantFormColumnValidator = variantFormColumnValidator;
        this.mappingService = mappingService;
        this.checkEfoTermAssignment = checkEfoTermAssignment;
    }

    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudyVariants(Model model,
                                @PathVariable Long studyId,
                                @RequestParam(required = false) Long associationId) {

        // Get all associations for a study
        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));

        // For our associations create a table view object and return
        Collection<VariantAssociationTableView> variantAssociationTableViews = new ArrayList<VariantAssociationTableView>();
        for (Association association : associations) {
            VariantAssociationTableView variantAssociationTableView =
                    associationViewService.createVariantAssociationTableView(association);
            variantAssociationTableViews.add(variantAssociationTableView);
        }
        model.addAttribute("variantAssociationTableViews", variantAssociationTableViews);

        // Determine last viewed association
        LastViewedAssociation lastViewedAssociation = getLastViewedAssociation(associationId);
        model.addAttribute("lastViewedAssociation", lastViewedAssociation);

        // Pass back count of associations
        Integer totalAssociations = associations.size();
        model.addAttribute("totalAssociations", totalAssociations);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }

    @RequestMapping(value = "/studies/{studyId}/associations/sortpvalue",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String sortStudyVariantsByPvalue(Model model,
                                        @PathVariable Long studyId,
                                        @RequestParam(required = true) String direction,
                                        @RequestParam(required = false) Long associationId) {

        // Get all associations for a study and perform relevant sorting
        Collection<Association> associations = new ArrayList<>();
        switch (direction) {
            case "asc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByPvalueExponentAndMantissaAsc()));
                break;
            case "desc":
                associations.addAll(associationRepository.findByStudyId(studyId,
                                                                        sortByPvalueExponentAndMantissaDesc()));
                break;
            default:
                associations.addAll(associationRepository.findByStudyId(studyId));
                break;
        }

        // For our associations create a table view object and return
        Collection<VariantAssociationTableView> variantAssociationTableViews = new ArrayList<VariantAssociationTableView>();
        for (Association association : associations) {
            VariantAssociationTableView variantAssociationTableView = associationViewService.createVariantAssociationTableView(
                    association);
            variantAssociationTableViews.add(variantAssociationTableView);
        }
        model.addAttribute("variantAssociationTableViews", variantAssociationTableViews);

        // Determine last viewed association
        LastViewedAssociation lastViewedAssociation = getLastViewedAssociation(associationId);
        model.addAttribute("lastViewedAssociation", lastViewedAssociation);

        // Pass back count of associations
        Integer totalAssociations = associations.size();
        model.addAttribute("totalAssociations", totalAssociations);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }


    @RequestMapping(value = "/studies/{studyId}/associations/sortexternalid",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String sortStudyVariantsByExternalId(Model model,
                                      @PathVariable Long studyId,
                                      @RequestParam(required = true) String direction,
                                      @RequestParam(required = false) Long associationId) {

        // Get all associations for a study and perform relevant sorting
        Collection<Association> associations = new ArrayList<>();

        // Sorting will not work for multi-variant haplotype or variant interactions so need to check for that
        Boolean sortValues = true;

        switch (direction) {
            case "asc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByExternalIdAsc()));
                break;
            case "desc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByExternalIdDesc()));
                break;
            default:
                associations.addAll(associationRepository.findByStudyId(studyId));
                break;
        }

        // Pass back count of associations
        Integer totalAssociations = associations.size();
        model.addAttribute("totalAssociations", totalAssociations);

        // For our associations create a table view object and return
        Collection<VariantAssociationTableView> variantAssociationTableViews = new ArrayList<VariantAssociationTableView>();
        for (Association association : associations) {
            VariantAssociationTableView variantAssociationTableView = associationViewService.createVariantAssociationTableView(
                    association);

            // Cannot sort multi field values
            if (variantAssociationTableView.getMultiVariantHaplotype() != null) {
                if (variantAssociationTableView.getMultiVariantHaplotype().equalsIgnoreCase("Yes")) {
                    sortValues = false;
                }
            }

            if (variantAssociationTableView.getVariantInteraction() != null) {
                if (variantAssociationTableView.getVariantInteraction().equalsIgnoreCase("Yes")) {
                    sortValues = false;
                }
            }

            variantAssociationTableViews.add(variantAssociationTableView);
        }

        // Only return sorted results if its not a multi-variant haplotype or variant interaction
        if (sortValues) {

            model.addAttribute("variantAssociationTableViews", variantAssociationTableViews);

            // Determine last viewed association
            LastViewedAssociation lastViewedAssociation = getLastViewedAssociation(associationId);
            model.addAttribute("lastViewedAssociation", lastViewedAssociation);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "study_association";

        }

        else {
            return "redirect:/studies/" + studyId + "/associations";
        }

    }


    // Upload a spreadsheet of variant association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String uploadStudyVariants(@RequestParam("file") MultipartFile file, @PathVariable Long studyId, Model model)
            throws EnsemblMappingException {

        // Establish our study object
        Study study = studyRepository.findOne(studyId);

        if (!file.isEmpty()) {
            // Save the uploaded file received in a multipart request as a file in the upload directory
            // The default temporary-file directory is specified by the system property java.io.tmpdir.

            String uploadDir =
                    System.getProperty("java.io.tmpdir") + File.separator + "gwas_batch_upload" + File.separator;

            // Create file
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            uploadedFile.getParentFile().mkdirs();

            // Copy contents of multipart request to newly created file
            try {
                file.transferTo(uploadedFile);
            }
            catch (IOException e) {
                throw new RuntimeException(
                        "Unable to to upload file ", e);
            }

            String uploadedFilePath = uploadedFile.getAbsolutePath();

            // Set permissions
            uploadedFile.setExecutable(true, false);
            uploadedFile.setReadable(true, false);
            uploadedFile.setWritable(true, false);


            // Send file, including path, to SNP batch loader process
            Collection<Association> newAssociations = new ArrayList<>();
            try {
                newAssociations = associationBatchLoaderService.processData(uploadedFilePath);
            }
            catch (InvalidOperationException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (InvalidFormatException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (RuntimeException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "data_upload_problem";

            }

            // Create our associations
            if (!newAssociations.isEmpty()) {
                Collection<Association> associationsToMap = new ArrayList<>();
                for (Association newAssociation : newAssociations) {

                    // Set the study ID for our association
                    newAssociation.setStudy(study);

                    // Save our association information
                    newAssociation.setLastUpdateDate(new Date());
                    getLog().error("newAssociation.getEffectAlleleFreqControls()" + newAssociation.getEffectAlleleFreqControls());
                    associationRepository.save(newAssociation);

                    // Map RS_ID in association
                    associationsToMap.add(newAssociation);
                }

                Curator curator = study.getHousekeeping().getCurator();
                String mappedBy = curator.getLastName();
                try {
                    mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
                }
                catch (EnsemblMappingException e) {
                    model.addAttribute("study", study);
                    return "ensembl_mapping_failure";
                }
            }
            return "redirect:/studies/" + studyId + "/associations";

        }
        else {
            // File is empty so let user know
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "empty_variantfile_upload_warning";
        }

    }

    // Generate a empty form page to add standard variant
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addStandardVariants(Model model, @PathVariable Long studyId) {

        // Return form object
        VariantAssociationForm emptyForm = new VariantAssociationForm();

        // Add one row by default and set description
        emptyForm.getVariantFormRows().add(new VariantFormRow());
        emptyForm.setMultiVariantHaplotypeDescr("Single variant");

        model.addAttribute("variantAssociationForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_standard_variant_association";
    }


    // Generate a empty form page to add multi-variant haplotype
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addMultiVariants(Model model, @PathVariable Long studyId) {

        // Return form object
        VariantAssociationForm emptyForm = new VariantAssociationForm();
        emptyForm.setMultiVariantHaplotype(true);

        model.addAttribute("variantAssociationForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_multi_variant_association";
    }

    // Generate a empty form page to add a interaction association
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addVariantInteraction(Model model, @PathVariable Long studyId) {

        // Return form object
        VariantAssociationInteractionForm emptyForm = new VariantAssociationInteractionForm();
        model.addAttribute("variantAssociationInteractionForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_variant_interaction_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRows"})
    public String addRows(VariantAssociationForm variantAssociationForm, Model model, @PathVariable Long studyId) {
        Integer numberOfRows = variantAssociationForm.getMultiVariantHaplotypeNum();

        // Add number of rows curator selected
        while (numberOfRows != 0) {
            variantAssociationForm.getVariantFormRows().add(new VariantFormRow());
            numberOfRows--;
        }

        // Pass back updated form
        model.addAttribute("variantAssociationForm", variantAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_variant_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCols"})
    public String addRows(VariantAssociationInteractionForm variantAssociationInteractionForm,
                          Model model,
                          @PathVariable Long studyId) {
        Integer numberOfCols = variantAssociationInteractionForm.getNumOfInteractions();

        // Add number of cols curator selected
        while (numberOfCols != 0) {
            variantAssociationInteractionForm.getVariantFormColumns().add(new VariantFormColumn());
            numberOfCols--;
        }

        // Pass back updated form
        model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_variant_interaction_association";
    }

    // Add single row to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRow"})
    public String addRow(VariantAssociationForm variantAssociationForm, Model model, @PathVariable Long studyId) {
        variantAssociationForm.getVariantFormRows().add(new VariantFormRow());

        // Pass back updated form
        model.addAttribute("variantAssociationForm", variantAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_variant_association";
    }

    // Add single column to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCol"})
    public String addCol(VariantAssociationInteractionForm variantAssociationInteractionForm,
                         Model model,
                         @PathVariable Long studyId) {
        variantAssociationInteractionForm.getVariantFormColumns().add(new VariantFormColumn());

        // Pass back updated form
        model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_variant_interaction_association";
    }

    // Remove row from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"removeRow"})
    public String removeRow(VariantAssociationForm variantAssociationForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        variantAssociationForm.getVariantFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("variantAssociationForm", variantAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_variant_association";
    }

    // Remove column from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"removeCol"})
    public String removeCol(VariantAssociationInteractionForm variantAssociationInteractionForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId) {

        //Index of value to remove
        final Integer colId = Integer.valueOf(req.getParameter("removeCol"));

        // Remove col
        variantAssociationInteractionForm.getVariantFormColumns().remove(colId.intValue());

        // Pass back updated form
        model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_variant_interaction_association";
    }


    // Add new standard association/variant information to a study
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStandardVariants(@ModelAttribute VariantAssociationForm variantAssociationForm,
                                  @PathVariable Long studyId,
                                  BindingResult result,
                                  Model model) throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors = checkVariantAssociationFormErrors(result, variantAssociationForm);

        if (hasErrors) {
            model.addAttribute("variantAssociationForm", variantAssociationForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_standard_variant_association";
        }
        else {
            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation = singleVariantMultiVariantAssociationService.createAssociation(variantAssociationForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addMultiVariants(@ModelAttribute VariantAssociationForm variantAssociationForm,
                               @PathVariable Long studyId,
                               BindingResult result,
                               Model model) throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors = checkVariantAssociationFormErrors(result, variantAssociationForm);

        if (hasErrors) {
            model.addAttribute("variantAssociationForm", variantAssociationForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_multi_variant_association";
        }
        else {

            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation = singleVariantMultiVariantAssociationService.createAssociation(variantAssociationForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addVariantInteraction(@ModelAttribute VariantAssociationInteractionForm variantAssociationInteractionForm,
                                    @PathVariable Long studyId, BindingResult result, Model model)
            throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors = checkVariantAssociationInteractionFormErrors(result, variantAssociationInteractionForm);

        if (hasErrors) {
            model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_variant_interaction_association";
        }
        else {
            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation =
                    variantInteractionAssociationService.createAssociation(variantAssociationInteractionForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);

        // Get mapping details
        MappingDetails mappingDetails = createMappingDetails(associationToView);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        AssociationFormErrorView associationFormErrorView = associationFormErrorViewService.checkAssociationForErrors(
                associationToView);
        model.addAttribute("errors", associationFormErrorView);

        // Establish study
        Long studyId = associationToView.getStudy().getId();

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        if (associationToView.getVariantInteraction() != null && associationToView.getVariantInteraction()) {
            VariantAssociationInteractionForm variantAssociationInteractionForm =
                    variantInteractionAssociationService.createVariantAssociationInteractionForm(associationToView);
            model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);
            return "edit_variant_interaction_association";
        }

        else if (associationToView.getMultiVariantHaplotype() != null && associationToView.getMultiVariantHaplotype()) {
            // Create form and return to user
            VariantAssociationForm variantAssociationForm = singleVariantMultiVariantAssociationService.createVariantAssociationForm(
                    associationToView);
            model.addAttribute("variantAssociationForm", variantAssociationForm);
            return "edit_multi_variant_association";
        }

        // If attributes haven't been set determine based on locus count and risk allele count
        else {
            Integer locusCount = associationToView.getLoci().size();

            List<EffectAllele> effectAlleles = new ArrayList<>();
            for (Locus locus : associationToView.getLoci()) {
                for (EffectAllele effectAllele : locus.getStrongestEffectAlleles()) {
                    effectAlleles.add(effectAllele);
                }
            }

            // Case where we have SNP interaction
            if (locusCount > 1) {
                VariantAssociationInteractionForm variantAssociationInteractionForm =
                        variantInteractionAssociationService.createVariantAssociationInteractionForm(associationToView);
                model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);
                return "edit_variant_interaction_association";
            }
            else {

                // Create form and return to user
                VariantAssociationForm variantAssociationForm = singleVariantMultiVariantAssociationService.createVariantAssociationForm(
                        associationToView);
                model.addAttribute("variantAssociationForm", variantAssociationForm);


                // If editing multi-variant haplotype
                if (effectAlleles.size() > 1) {
                    return "edit_multi_variant_association";
                }
                else {
                    return "edit_standard_variant_association";
                }
            }
        }
    }

    //Edit existing association
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute VariantAssociationForm variantAssociationForm,
                                  BindingResult variantAssociationFormBindingResult,
                                  @ModelAttribute VariantAssociationInteractionForm variantAssociationInteractionForm,
                                  BindingResult variantAssociationInteractionFormBindingResult,
                                  @PathVariable Long associationId,
                                  @RequestParam(value = "associationtype", required = true) String associationType,
                                  Model model) throws EnsemblMappingException {


        // Validate returned form depending on association type
        Boolean hasErrors;
        if (associationType.equalsIgnoreCase("interaction")) {
            hasErrors = checkVariantAssociationInteractionFormErrors(variantAssociationInteractionFormBindingResult,
                                                                     variantAssociationInteractionForm);
        }
        else {
            hasErrors = checkVariantAssociationFormErrors(variantAssociationFormBindingResult, variantAssociationForm);
        }

        // If errors found then return the edit form with all information entered by curator preserved
        if (hasErrors) {

            // Return association with that ID
            Association associationToEdit = associationRepository.findOne(associationId);

            // Get mapping details
            MappingDetails mappingDetails = createMappingDetails(associationToEdit);
            model.addAttribute("mappingDetails", mappingDetails);

            // Return any association errors
            AssociationFormErrorView associationFormErrorView =
                    associationFormErrorViewService.checkAssociationForErrors(
                            associationToEdit);
            model.addAttribute("errors", associationFormErrorView);

            // Establish study
            Long studyId = associationToEdit.getStudy().getId();

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));

            if (associationType.equalsIgnoreCase("interaction")) {
                model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);
                return "edit_variant_interaction_association";
            }

            else {
                Integer locusCount = associationToEdit.getLoci().size();

                List<EffectAllele> effectAlleles = new ArrayList<>();
                for (Locus locus : associationToEdit.getLoci()) {
                    for (EffectAllele effectAllele : locus.getStrongestEffectAlleles()) {
                        effectAlleles.add(effectAllele);
                    }
                }

                model.addAttribute("variantAssociationForm", variantAssociationForm);

                // Determine html view to display
                if (effectAlleles.size() > 1) {
                    return "edit_multi_variant_association";
                }
                else {
                    return "edit_standard_variant_association";
                }
            }
        }
        else {
            //Create association
            Association editedAssociation;

            // Request parameter determines how to process form and also which form to process
            if (associationType.equalsIgnoreCase("interaction")) {
                editedAssociation = variantInteractionAssociationService.createAssociation(variantAssociationInteractionForm);
            }

            else if (associationType.equalsIgnoreCase("standardormulti")) {
                editedAssociation = singleVariantMultiVariantAssociationService.createAssociation(variantAssociationForm);
            }

            // default to standard view
            else {
                editedAssociation = singleVariantMultiVariantAssociationService.createAssociation(variantAssociationForm);
            }

            // Set ID of new  association to the ID of the association we're currently editing
            editedAssociation.setId(associationId);

            // Set study to one currently linked to association
            Association currentAssociation = associationRepository.findOne(associationId);
            Study associationStudy = currentAssociation.getStudy();
            editedAssociation.setStudy(associationStudy);

            // Save our association information
            editedAssociation.setLastUpdateDate(new Date());
            associationRepository.save(editedAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(editedAssociation);
            Curator curator = associationStudy.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", associationStudy);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + associationId;
        }
    }

    // Add single row to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addRow"})
    public String addRowEditMode(VariantAssociationForm variantAssociationForm, Model model, @PathVariable Long associationId) {
        variantAssociationForm.getVariantFormRows().add(new VariantFormRow());

        // Pass back updated form
        model.addAttribute("variantAssociationForm", variantAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Get mapping details
        MappingDetails mappingDetails = createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        AssociationFormErrorView associationFormErrorView = associationFormErrorViewService.checkAssociationForErrors(
                currentAssociation);
        model.addAttribute("errors", associationFormErrorView);

        return "edit_multi_variant_association";
    }

    // Add single column to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addCol"})
    public String addColEditMode(VariantAssociationInteractionForm variantAssociationInteractionForm,
                                 Model model, @PathVariable Long associationId) {

        variantAssociationInteractionForm.getVariantFormColumns().add(new VariantFormColumn());

        // Pass back updated form
        model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Get mapping details
        MappingDetails mappingDetails = createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        AssociationFormErrorView associationFormErrorView = associationFormErrorViewService.checkAssociationForErrors(
                currentAssociation);
        model.addAttribute("errors", associationFormErrorView);

        return "edit_variant_interaction_association";
    }

    // Remove row from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeRow"})
    public String removeRowEditMode(VariantAssociationForm variantAssociationForm,
                                    HttpServletRequest req,
                                    Model model,
                                    @PathVariable Long associationId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        variantAssociationForm.getVariantFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("variantAssociationForm", variantAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Get mapping details
        MappingDetails mappingDetails = createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        AssociationFormErrorView associationFormErrorView = associationFormErrorViewService.checkAssociationForErrors(
                currentAssociation);
        model.addAttribute("errors", associationFormErrorView);

        return "edit_multi_variant_association";
    }

    // Remove column from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeCol"})
    public String removeColEditMode(VariantAssociationInteractionForm variantAssociationInteractionForm,
                                    HttpServletRequest req,
                                    Model model,
                                    @PathVariable Long associationId) {

        //Index of value to remove
        final Integer colId = Integer.valueOf(req.getParameter("removeCol"));

        // Remove col
        variantAssociationInteractionForm.getVariantFormColumns().remove(colId.intValue());

        // Pass back updated form
        model.addAttribute("variantAssociationInteractionForm", variantAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Get mapping details
        MappingDetails mappingDetails = createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        AssociationFormErrorView associationFormErrorView = associationFormErrorViewService.checkAssociationForErrors(
                currentAssociation);
        model.addAttribute("errors", associationFormErrorView);

        return "edit_variant_interaction_association";
    }


    // Delete all associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations/delete_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String deleteAllAssociations(Model model, @PathVariable Long studyId) {

        // Get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);

        // For each association get the loci
        Collection<Locus> loci = new ArrayList<Locus>();
        for (Association association : studyAssociations) {
            loci.addAll(association.getLoci());
        }

        // Delete each locus and risk allele, which in turn deletes link to genes via author_reported_gene table,
        // Variants are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            Collection<EffectAllele> locusEffectAlleles = locus.getStrongestEffectAlleles();
            locus.setStrongestEffectAlleles(new ArrayList<>());
            for (EffectAllele effectAllele : locusEffectAlleles) {
                lociAttributesService.deleteEffectAllele(effectAllele);
            }
            locusRepository.delete(locus);
        }
        // Delete associations
        for (Association association : studyAssociations) {
            associationRepository.delete(association);
        }

        return "redirect:/studies/" + studyId + "/associations";
    }

    // Delete checked SNP associations
    @RequestMapping(value = "/studies/{studyId}/associations/delete_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> deleteChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        Collection<Locus> loci = new ArrayList<Locus>();
        Collection<Association> studyAssociations = new ArrayList<Association>();

        // For each association get the loci attached
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            loci.addAll(association.getLoci());
            studyAssociations.add(association);
            count++;
        }

        // Delete each locus and risk allele, which in turn deletes link to genes via author_reported_gene table,
        // Variants are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            Collection<EffectAllele> locusEffectAlleles = locus.getStrongestEffectAlleles();
            locus.setStrongestEffectAlleles(new ArrayList<>());
            for (EffectAllele effectAllele : locusEffectAlleles) {
                lociAttributesService.deleteEffectAllele(effectAllele);
            }
            locusRepository.delete(locus);
        }

        // Delete associations
        for (Association association : studyAssociations) {
            associationRepository.delete(association);
        }

        message = "Successfully deleted " + count + " associations";

        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;

    }


    // Approve a single SNP association
    @RequestMapping(value = "associations/{associationId}/approve",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String approveVariantAssociation(@PathVariable Long associationId, RedirectAttributes redirectAttributes) {

        Association association = associationRepository.findOne(associationId);

        // Check if association has an EFO trait
        Boolean associationEfoTermsAssigned = checkEfoTermAssignment.checkAssociationEfoAssignment(association);

        if (!associationEfoTermsAssigned) {
            String message = "Cannot approve association as no EFO trait assigned";
            redirectAttributes.addFlashAttribute("efoMessage", message);
        }
        else {
            // Mark errors as checked
            associationErrorsChecked(association);

            // Set variantChecked attribute to true
            association.setVariantApproved(true);
            association.setLastUpdateDate(new Date());
            associationRepository.save(association);
        }

        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }


    // Un-approve a single SNP association
    @RequestMapping(value = "associations/{associationId}/unapprove",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String unapproveVariantAssociation(@PathVariable Long associationId) {

        Association association = associationRepository.findOne(associationId);

        // Mark errors as unchecked
        associationErrorsUnchecked(association);

        // Set variantChecked attribute to true
        association.setVariantApproved(false);
        association.setLastUpdateDate(new Date());
        associationRepository.save(association);

        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }

    // Approve checked SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> approveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        // Create a collection of all association objects and check EFO term assignment
        Collection<Association> allAssociations = new ArrayList<>();
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            allAssociations.add(association);
        }
        Boolean associationsEfoTermsAssigned = checkEfoTermAssignment.checkAssociationsEfoAssignment(allAssociations);

        if (!associationsEfoTermsAssigned) {
            message = "Cannot approve association(s) as no EFO trait assigned";
        }

        else {
            // For each one set variantChecked attribute to true
            for (String associationId : associationsIds) {
                Association association = associationRepository.findOne(Long.valueOf(associationId));

                // Mark errors as checked
                associationErrorsChecked(association);

                association.setVariantApproved(true);
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
                count++;
            }
            message = "Successfully updated " + count + " associations";
        }
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    // Un-approve checked SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/unapprove_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> unapproveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        // For each one set variantChecked attribute to true
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));

            // Mark errors as checked
            associationErrorsUnchecked(association);

            association.setVariantApproved(false);
            association.setLastUpdateDate(new Date());
            associationRepository.save(association);
            count++;
        }
        message = "Successfully updated " + count + " associations";

        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }


    // Approve all SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String approveAll(@PathVariable Long studyId, RedirectAttributes redirectAttributes) {

        // Get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);
        Boolean associationEfoTermsAssigned = checkEfoTermAssignment.checkAssociationsEfoAssignment(studyAssociations);

        if (!associationEfoTermsAssigned) {
            String message = "Cannot approve all associations as no EFO trait assigned";
            redirectAttributes.addFlashAttribute("efoMessage", message);
        }

        else {
            // For each one set variantChecked attribute to true
            for (Association association : studyAssociations) {
                // Mark errors as checked
                associationErrorsChecked(association);

                association.setVariantApproved(true);
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
            }
        }
        return "redirect:/studies/" + studyId + "/associations";
    }

    /**
     * Run mapping pipeline on all SNPs in a study
     *
     * @param studyId            Study ID in database
     * @param redirectAttributes attributes for a redirect scenario
     */
    @RequestMapping(value = "/studies/{studyId}/associations/validate_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String validateAll(@PathVariable Long studyId, RedirectAttributes redirectAttributes, Model model)
            throws EnsemblMappingException {

        // For the study get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);

        Study study = studyRepository.findOne(studyId);
        Curator curator = study.getHousekeeping().getCurator();
        String mappedBy = curator.getLastName();
        try {
            mappingService.validateAndMapAssociations(studyAssociations, mappedBy);
        }
        catch (EnsemblMappingException e) {
            model.addAttribute("study", study);
            return "ensembl_mapping_failure";
        }

        String message = "Mapping complete, please check for any errors displayed in the 'Errors' column";
        redirectAttributes.addFlashAttribute("mappingComplete", message);
        return "redirect:/studies/" + studyId + "/associations";
    }


    @RequestMapping(value = "/studies/{studyId}/associations/download",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String downloadStudyVariants(HttpServletResponse response, Model model, @PathVariable Long studyId)
            throws IOException {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        Study study = studyRepository.findOne((studyId));

        if (associations.size() == 0) {
            model.addAttribute("study", study);
            return "no_association_download_warning";
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName =
                    study.getAuthor().concat("-").concat(study.getPubmedId()).concat("-").concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            associationDownloadService.createDownloadFile(response.getOutputStream(), associations);

            return "redirect:/studies/" + studyId + "/associations";
        }
    }


    @RequestMapping(value = "/studies/{studyId}/associations/applyefotraits",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String applyStudyEFOtraitToVariants(Model model, @PathVariable Long studyId,
                                           @RequestParam(value = "e",
                                                         required = false,
                                                         defaultValue = "false") boolean existing,
                                           @RequestParam(value = "o",
                                                         required = false,
                                                         defaultValue = "true") boolean overwrite)
            throws IOException {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        Study study = studyRepository.findOne((studyId));
        Collection<EfoTrait> efoTraits = study.getEfoTraits();


        if (associations.size() == 0 || efoTraits.size() == 0) {
            model.addAttribute("study", study);
            return "no_association_efo_trait_warning";
        }
        else {
            if (!existing) {
                for (Association association : associations) {
                    if (association.getEfoTraits().size() != 0) {
                        model.addAttribute("study", study);
                        return "existing_efo_traits_warning";
                    }
                }
            }
            Collection<EfoTrait> associationTraits = new ArrayList<EfoTrait>();

            for (EfoTrait efoTrait : efoTraits) {
                associationTraits.add(efoTrait);
            }

            for (Association association : associations) {
                if (association.getEfoTraits().size() != 0 && !overwrite) {
                    for (EfoTrait trait : associationTraits) {
                        if (!association.getEfoTraits().contains(trait)) {
                            association.addEfoTrait(trait);
                        }
                    }
                }
                else {
                    association.setEfoTraits(associationTraits);
                }
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
            }

            return "redirect:/studies/" + studyId + "/associations";
        }
    }

    /* Exception handling */
    @ExceptionHandler(DataIntegrityException.class)
    public String handleDataIntegrityException(DataIntegrityException dataIntegrityException, Model model) {
        return dataIntegrityException.getMessage();
    }


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEfoTraits() {
        return efoTraitRepository.findAll(sortByTraitAsc());
    }

    // Sort options
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

    private Sort sortByPvalueExponentAndMantissaAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "pvalueExponent"),
                        new Sort.Order(Sort.Direction.ASC, "pvalueMantissa"));
    }

    private Sort sortByPvalueExponentAndMantissaDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "pvalueExponent"),
                        new Sort.Order(Sort.Direction.DESC, "pvalueMantissa"));
    }

    private Sort sortByExternalIdAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "loci.strongestEffectAlleles.variant.externalId"));
    }

    private Sort sortByExternalIdDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "loci.strongestEffectAlleles.variant.externalId"));
    }

    /**
     * Gather mapping details for an association
     *
     * @param association Association with mapping details
     */
    private MappingDetails createMappingDetails(Association association) {
        MappingDetails mappingDetails = new MappingDetails();
        mappingDetails.setPerformer(association.getLastMappingPerformedBy());
        mappingDetails.setMappingDate(association.getLastMappingDate());
        return mappingDetails;
    }


    /**
     * Mark errors for a particular association as checked, this involves updating the linked association report
     *
     * @param association Association to mark as errors checked
     */
    public void associationErrorsChecked(Association association) {
        AssociationReport associationReport = association.getAssociationReport();
        associationReport.setErrorCheckedByCurator(true);
        associationReport.setLastUpdateDate(new Date());
        associationReportRepository.save(associationReport);
    }


    /**
     * Mark errors for a particular association as unchecked, this involves updating the linked association report
     *
     * @param association Association to mark as errors unchecked
     */
    public void associationErrorsUnchecked(Association association) {
        AssociationReport associationReport = association.getAssociationReport();
        associationReport.setErrorCheckedByCurator(false);
        associationReport.setLastUpdateDate(new Date());
        associationReportRepository.save(associationReport);
    }


    /**
     * Determine last viewed association
     *
     * @param associationId ID of association last viewed
     */
    private LastViewedAssociation getLastViewedAssociation(Long associationId) {

        LastViewedAssociation lastViewedAssociation = new LastViewedAssociation();
        if (associationId != null) {
            lastViewedAssociation.setId(associationId);
        }
        return lastViewedAssociation;
    }

    /**
     * Check a standard SNP association form for errors
     *
     * @param result Binding result from edit form
     * @param form   The form to validate
     */

    private Boolean checkVariantAssociationFormErrors(BindingResult result,
                                                  VariantAssociationForm form) {
        for (VariantFormRow row : form.getVariantFormRows()) {
            variantFormRowValidator.validate(row, result);
        }

        return result.hasErrors();
    }

    /**
     * Check a SNP association interaction form for errors
     *
     * @param result Binding result from edit form
     * @param form   The form to validate
     */
    private Boolean checkVariantAssociationInteractionFormErrors(BindingResult result,
                                                             VariantAssociationInteractionForm form) {

        for (VariantFormColumn column : form.getVariantFormColumns()) {
            variantFormColumnValidator.validate(column, result);
        }

        return result.hasErrors();
    }
}

