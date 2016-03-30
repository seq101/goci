package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

/**
 * Created by catherineleroy on 22/03/2016.
 */
@Entity
public class ArrayDictionnary {

    @Id
    @GeneratedValue
    private long id;

    private String arrayName;

    @ManyToMany(mappedBy = "arrayDictionnaries")
    Collection<Ethnicity> ethnicities;

    @ManyToOne
    @JoinColumn(name="platform_dictionnary_id")
    private PlatformDictionnary platformDictionnary;

    public ArrayDictionnary(){}

    public ArrayDictionnary(Collection<Ethnicity> ethnicities, PlatformDictionnary platformDictionnary){
        this.ethnicities = ethnicities;
        this.platformDictionnary = platformDictionnary;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public Collection<Ethnicity> getEthnicities() {
        return ethnicities;
    }

    public void setEthnicities(Collection<Ethnicity> ethnicities) {
        this.ethnicities = ethnicities;
    }

    public PlatformDictionnary getPlatformDictionnary() {
        return platformDictionnary;
    }

    public void setPlatformDictionnary(PlatformDictionnary platformDictionnary) {
        this.platformDictionnary = platformDictionnary;
    }
}
