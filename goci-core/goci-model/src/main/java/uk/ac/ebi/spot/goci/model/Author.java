package uk.ac.ebi.spot.goci.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Created by cinzia on 22/09/2017.
 */
@Entity
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Fullname is missing")
    private String fullname;

    private String orcid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", updatable = false)
    private Date createdAt;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }


    @ManyToOne
    @JoinTable(name = "PUBLICATION_AUTHORS",
            joinColumns = @JoinColumn(name = "AUTHOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "PUBLICATION_ID"))
    private Publication publication;

    //JPA Constructor
    public Author() {}

    public Author(String fullname, String orcid) {
        this.fullname = fullname;
        this.orcid = orcid;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Publication getPublication() { return publication; }

    public void setPublication(Publication publication) { this.publication = publication; }


    // Use Optional for Default value.

    public String getFullnameShort(int  lenghtFullname) {
        return fullname.substring(0, Math.min(fullname.length(), lenghtFullname));
    }

}