package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.Collection;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link VariantRepository} that
 *         retrieves all associations, and then within the same datasource transaction additionally loads other objects
 *         referenced by this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class VariantQueryService {

    // Repositories
    private VariantRepository variantRepository;

    @Autowired
    public VariantQueryService(VariantRepository variantRepository) {
        this.variantRepository = variantRepository;
    }

    @Transactional(readOnly = true)
    public Variant findByExternalIdIgnoreCase(String externalId) {
        Variant variant =
                variantRepository.findByExternalIdIgnoreCase(externalId);
        loadAssociatedData(variant);
        return variant;
    }

    @Transactional(readOnly = true)
    public Collection<Variant> findByEffectAllelesLociId(Long locusId) {
        Collection<Variant> variantsLinkedToLocus =
                variantRepository.findByEffectAllelesLociId(locusId);
        variantsLinkedToLocus.forEach(this::loadAssociatedData);
        return variantsLinkedToLocus;
    }

    public void loadAssociatedData(Variant variant) {

        if (variant.getLocations() != null) {
            variant.getLocations().size();
        }
        if (variant.getGenomicContexts() != null) {
            variant.getGenomicContexts().size();
        }
    }
}
