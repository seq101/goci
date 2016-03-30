package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Variant;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Single Nucloetide Polymorphism entity objectls
 */

@RepositoryRestResource
public interface VariantRepository extends JpaRepository<Variant, Long> {
    Variant findByExternalId(String externalId);

    Variant findByExternalIdIgnoreCase(String externalId);

    Collection<Variant> findByEffectAllelesLociAssociationStudyId(Long studyId);

    Collection<Variant> findByEffectAllelesLociAssociationId(Long associationId);

    Collection<Variant> findByEffectAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    List<Variant> findByLocationsId(Long locationId);

    Collection<Variant> findByEffectAllelesLociId(Long locusId);
}

