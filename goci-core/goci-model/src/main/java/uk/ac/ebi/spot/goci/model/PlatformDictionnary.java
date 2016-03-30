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

    private String plaformName;

    @OneToMany(mappedBy = "platformDictionnary")
    private Collection<ArrayDictionnary> arrayDictionnaries;

    public PlatformDictionnary(){
    }

    public PlatformDictionnary(Collection<ArrayDictionnary> arrayDictionnaries, String plaformName){
        this.arrayDictionnaries = arrayDictionnaries;
        this.plaformName = plaformName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaformName() {
        return plaformName;
    }

    public void setPlaformName(String plaformName) {
        this.plaformName = plaformName;
    }

    public Collection<ArrayDictionnary> getArrayDictionnaries() {
        return arrayDictionnaries;
    }

    public void setArrayDictionnaries(Collection<ArrayDictionnary> arrayDictionnaries) {
        this.arrayDictionnaries = arrayDictionnaries;
    }
}
