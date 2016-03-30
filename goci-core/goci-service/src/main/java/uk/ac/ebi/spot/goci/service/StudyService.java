package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class StudyService {
    private StudyRepository studyRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.StudyRepository} that retrieves all studies, and
     * then within the same datasource transaction additionally loads other objects referenced by this study (traits,
     * associations, housekeeping).
     * <p>
     * Use this when you know you will need deep information about a study and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> findAll() {
        List<Study> allStudies = studyRepository.findAll();
        allStudies.forEach(this::loadAssociatedData);
        return allStudies;
    }

    /**
     * Get in one transaction all the studies, plus associated Associations, plus associated SNPs and their regions,
     * plus the studies publish date.
     *
     * @return a List of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> deepFindAll() {
        List<Study> allStudies = studyRepository.findAll();
        allStudies.forEach(this::deepLoadAssociatedData);
        return allStudies;
    }


    @Transactional(readOnly = true)
    public List<Study> findAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> deepFindAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.StudyRepository} that retrieves all studies, and
     * then within the same datasource transaction additionally loads other objects referenced by this study (traits,
     * associations, housekeeping).
     * <p>
     * Use this when you know you will need deep information about a study and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies() {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindPublishedStudies() {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }


    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies(Sort sort) {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findPublishedStudies(Pageable pageable) {
        Page<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        pageable);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Study fetchOne(Study study) {
        loadAssociatedData(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Collection<Study> fetchAll(Collection<Study> studies) {
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByVariantId(Long variantId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsLociStrongestEffectAllelesVariantIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        variantId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByAssociationId(Long associationId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        associationId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Study> studies =
                studyRepository.findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        diseaseTraitId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    public void loadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        int ethnicityCount = study.getEthnicities().size();
        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
        if (publishDate != null) {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + ethnicityCount +
                            " ancestry entries and was published on " + publishDate.toString());
        }
        else {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + ethnicityCount +
                            " ancestry entries and is not yet published");
        }
    }

    public void deepLoadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        int variantCount = study.getVariants().size();
        //        System.out.println("BONJOUR");
        //        getLog().error("BONJOUR");
        for (Variant variant : study.getVariants()) {
            int locationCount = variant.getLocations().size();
            getLog().trace("Variant '" + variant.getId() + "' is linked to " + locationCount + " regions.");
            //            for(Region region : variant.getRegions()){
            //                region.getId();
            //            }
            int ethnicityCount = study.getEthnicities().size();

            for (Association association : study.getAssociations()) {
                int lociCount = association.getLoci().size();
                int associationEfoTraitCount = association.getEfoTraits().size();
                getLog().trace("Association '" + association.getId() + "' is linked to " + lociCount + " loci and " +
                                       associationEfoTraitCount + "efo traits.");
                for (Locus locus : association.getLoci()) {
                    int effectAlleleCount = locus.getStrongestEffectAlleles().size();
                    getLog().trace("Locus '" + locus.getId() + "' is linked to " + effectAlleleCount + " effect alleles.");
                    for (EffectAllele effectAllele : locus.getStrongestEffectAlleles()) {
                        Variant effectAlleleVariant = effectAllele.getVariant();
                        int effectAlleleVariantRegionCount = effectAlleleVariant.getLocations().size();
                        getLog().trace("Variant '" + effectAlleleVariant.getId() + "' is linked to " + effectAlleleVariantRegionCount +
                                               " regions.");
                    }
                }
            }


            Date publishDate = study.getHousekeeping().getCatalogPublishDate();
            if (publishDate != null) {
                getLog().trace(
                        "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                                "has " + associationCount + " associations, " + variantCount + " Variants, " + ethnicityCount +
                                " ancestry entries and was published on " +
                                publishDate.toString());
            }
            else {
                getLog().trace(
                        "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                                "has " + associationCount + " associations, " + variantCount + " Variants, , " +
                                ethnicityCount + " ancestry entries and is not yet published");
            }
        }
    }
}
