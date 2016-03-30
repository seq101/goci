package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Gene;

import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Gene entity object
 */
@RepositoryRestResource
public interface GeneRepository extends JpaRepository<Gene, Long> {
    Gene findByGeneNameIgnoreCase(String geneName);

    Collection<Gene> findByAuthorReportedFromLociAssociationStudyId(Long studyId);

    Collection<Gene> findByGenomicContextsVariantEffectAllelesLociAssociationStudyId(Long studyId);

    Collection<Gene> findByAuthorReportedFromLociStrongestEffectAllelesVariantId(Long variantId);

    Collection<Gene> findByGenomicContextsVariantId(Long variantId);

    Collection<Gene> findByAuthorReportedFromLociAssociationId(Long associationId);

    Collection<Gene> findByGenomicContextsVariantEffectAllelesLociAssociationId(Long associationId);

    Collection<Gene> findByGenomicContextsVariantEffectAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    Collection<Gene> findByGenomicContextsVariantEffectAllelesLociAssociationEfoTraitsId(Long traitId);

    Gene findByEnsemblGeneIdsId(Long id);

    Gene findByEntrezGeneIdsId(Long id);

    Gene findByGeneName(String geneName);
}
