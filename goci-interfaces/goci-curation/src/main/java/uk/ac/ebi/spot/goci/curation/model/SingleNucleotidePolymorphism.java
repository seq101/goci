package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         Model object representing a single nucleotide polymorphisms and its attributes
 */

@Entity
@Table(name = "GWASSNP")
public class SingleNucleotidePolymorphism {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "SNP")
    private String rsID;

    // TODO ADD THESE ONCE TABLE IS ACTIVE
    @Column(name = "CHROMOSOME_NAME")
    private String chromosomeName;

    @Column(name = "CHROMOSOME_POS")
    private String chromosomePosition;

    @Column(name = "LASTUPDATEDATE")
    private Timestamp lastUpdateDate;

    // TODO HOW DO WE DEFINE RELATIONSHIP WITH GENE AND REGION
    // Associated region
  /*  @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "GWASREGIONXREF",
            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "REGIONID", referencedColumnName = "ID")}
    )
    private Region region;*/

    // Associated genes
    @ManyToMany
    @JoinTable(
            name = "GWASGENEXREF",
            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "GENEID", referencedColumnName = "ID")}
    )
    private Collection<Gene> genes;

    // JPA no-args constructor
    public SingleNucleotidePolymorphism() {
    }

    public SingleNucleotidePolymorphism(String rsID, String chromosomeName, String chromosomePosition, Timestamp lastUpdateDate) {
        this.rsID = rsID;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRsID() {
        return rsID;
    }

    public void setRsID(String rsID) {
        this.rsID = rsID;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public void setChromosomePosition(String chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString() {
        return "SingleNucleotidePolymorphism{" +
                "id=" + id +
                ", rsID='" + rsID + '\'' +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}
