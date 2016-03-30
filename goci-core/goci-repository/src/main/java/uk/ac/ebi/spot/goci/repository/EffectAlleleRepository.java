package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EffectAllele;

import java.util.List;

/**
 * Created by emma on 27/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing EffectAllele entity object
 */
@RepositoryRestResource
public interface EffectAlleleRepository extends JpaRepository<EffectAllele, Long> {
    List<EffectAllele> findByEffectAlleleName(String effectAlleleName);
}
