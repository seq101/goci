package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.Collection;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
@Service
public class VariantService {
    private VariantRepository variantRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public VariantService(VariantRepository variantRepository) {
        this.variantRepository = variantRepository;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * A facade service around a {@link VariantRepository} that
     * retrieves all SNPs, and then within the same datasource transaction additionally loads other objects referenced
     * by this SNP (so Genes and Regions).
     * <p>
     * Use this when you know you will need deep information about a SNP and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of Variants
     */
    @Transactional(readOnly = true)
    public List<Variant> findAll() {
        List<Variant> allVariants = variantRepository.findAll();
        allVariants.forEach(this::loadAssociatedData);
        return allVariants;
    }

    @Transactional(readOnly = true)
    public List<Variant> findAll(Sort sort) {
        List<Variant> allVariants = variantRepository.findAll(sort);
        allVariants.forEach(this::loadAssociatedData);
        return allVariants;
    }

    @Transactional(readOnly = true)
    public Page<Variant> findAll(Pageable pageable) {
        Page<Variant> allVariants = variantRepository.findAll(pageable);
        allVariants.forEach(this::loadAssociatedData);
        return allVariants;
    }

    @Transactional(readOnly = true)
    public Variant fetchOne(Variant variant) {
        loadAssociatedData(variant);
        return variant;
    }

    @Transactional(readOnly = true)
    public Collection<Variant> fetchAll(Collection<Variant> variants) {
        variants.forEach(this::loadAssociatedData);
        return variants;
    }

    @Transactional(readOnly = true)
    public Collection<Variant> findByStudyId(Long studyId) {
        Collection<Variant> variants =
                variantRepository.findByEffectAllelesLociAssociationStudyId(studyId);
        variants.forEach(this::loadAssociatedData);
        return variants;
    }

    public Collection<Variant> findByAssociationId(Long associationId) {
        Collection<Variant> variants =
                variantRepository.findByEffectAllelesLociAssociationId(associationId);
        variants.forEach(this::loadAssociatedData);
        return variants;
    }

    public Collection<Variant> findByDiseaseTraitId(Long traitId) {
        Collection<Variant> variants =
                variantRepository.findByEffectAllelesLociAssociationStudyDiseaseTraitId(traitId);
        variants.forEach(this::loadAssociatedData);
        return variants;
    }

    public void loadAssociatedData(Variant variant) {
        int locationCount = variant.getLocations().size();
        int geneCount = variant.getGenomicContexts().size();
        getLog().trace("Variant '" + variant.getExternalId() + "' is mapped to " + locationCount + " locations " +
                               "and " + geneCount + " genes");
    }

    @Transactional(readOnly = true)
    public Collection<Variant> deepFindByStudyId(Long studyId) {
        Collection<Variant> variants =
                variantRepository.findByEffectAllelesLociAssociationStudyId(studyId);
        variants.forEach(this::loadAssociatedData);
        return variants;
    }
}
