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
public class ImputPlatformDictionnary {

    @Id
    @GeneratedValue
    private long id;

    private String platformName;

    @OneToMany(mappedBy = "imputPlatformDictionnary")
    Collection<Study> studies;

    public ImputPlatformDictionnary(){}

    public ImputPlatformDictionnary(String platformName, Collection<Study> studies){
        this.platformName = platformName;
        this.studies = studies;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
}
