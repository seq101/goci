package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by catherineleroy on 23/03/2016.
 */
@Entity
public class StudyTypeDictionnary {

    @Id
    @GeneratedValue
    private long id;

    private String typeName;

    private String definition;

    @OneToMany(mappedBy = "studyTypeDictionnary")
    Collection<Study> studies;

    public StudyTypeDictionnary(){}

    public StudyTypeDictionnary(String typeName, String definition, Collection<Study> studies){
        this.typeName = typeName;
        this.definition = definition;
        this.studies = studies;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
}
