package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by catherineleroy on 22/03/2016.
 */
@Entity
public class PlatformDictionnary {
    @Id
    @GeneratedValue
    private Long id;

    private String platformName;

    @OneToMany(mappedBy = "platformDictionnary")
    private Collection<ArrayDictionnary> arrayDictionnaries;

    public PlatformDictionnary(){
    }

    public PlatformDictionnary(Collection<ArrayDictionnary> arrayDictionnaries, String platformName){
        this.arrayDictionnaries = arrayDictionnaries;
        this.platformName = platformName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Collection<ArrayDictionnary> getArrayDictionnaries() {
        return arrayDictionnaries;
    }

    public void setArrayDictionnaries(Collection<ArrayDictionnary> arrayDictionnaries) {
        this.arrayDictionnaries = arrayDictionnaries;
    }
}
