package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Association;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 26/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Association entity object
 */
@RepositoryRestResource
public interface AssociationRepository extends JpaRepository<Association, Long> {
    Collection<Association> findByStudyId(long studyId);

    Collection<Association> findByStudyId(long studyId, Sort sort);

    List<Association> findByStudyIdAndLastUpdateDateIsNotNullOrderByLastUpdateDateDesc(Long studyId);

    Collection<Association> findByLociStrongestEffectAllelesVariantId(long variantId);

    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull();

    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort);

    Page<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable);

    List<Association> findByLociStrongestEffectAllelesVariantIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long variantId);

    List<Association> findByLociStrongestEffectAllelesVariantIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long variantId);

    Page<Association> findByLociStrongestEffectAllelesVariantIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long variantId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long diseaseTraitId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long diseaseTraitId);

    Page<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long diseaseTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long efoTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long efoTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long efoTraitId);
}
