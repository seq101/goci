package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by Dani on 27/11/2014.
 */
@RepositoryRestResource
public interface StudyRepository  extends JpaRepository<Study, Long> {
    Study findByPubmedId(String pubmedId);

  //  Object findAll();

//    Object findOne(long l);

 //   Study save(Study study);
}
