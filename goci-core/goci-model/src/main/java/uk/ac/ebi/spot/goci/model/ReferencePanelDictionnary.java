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
public class ReferencePanelDictionnary {

    @Id
    @GeneratedValue
    private long id;

    private String referencePanelName;

    @OneToMany(mappedBy = "referencePanelDictionnary")
    private Collection<Study> studies;

    public ReferencePanelDictionnary(){}

    public ReferencePanelDictionnary(String referencePanelName, Collection<Study> studies){
        this.referencePanelName = referencePanelName;
        this.studies = studies;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReferencePanelName() {
        return referencePanelName;
    }

    public void setReferencePanelName(String referencePanelName) {
        this.referencePanelName = referencePanelName;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
}
