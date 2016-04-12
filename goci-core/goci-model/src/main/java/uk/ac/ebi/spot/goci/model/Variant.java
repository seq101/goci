package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing a single nucleotide polymorphisms and its attributes
 */

@Entity
public class Variant {
    @Id
    @GeneratedValue
    private Long id;

    //Can be rsId or some other id in case of data from immunochip or indels.
    private String externalId;

    private Long merged;

    private String functionalClass;

    private Date lastUpdateDate;

    private String chromosomeName;

    private String chromosomePosition;

    @ManyToMany
    @JoinTable(name = "VARIANT_LOCATION",
               joinColumns = @JoinColumn(name = "VARIANT_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCATION_ID"))
    private Collection<Location> locations;

    @OneToMany(mappedBy = "variant")
    private Collection<GenomicContext> genomicContexts;

    @OneToMany(mappedBy = "variant")
    private Collection<EffectAllele> effectAlleles;

    // JPA no-args constructor
    public Variant() {
    }

    public Variant(String externalId,
                   Long merged,
                   String functionalClass,
                   String chromosomeName,
                   String chromosomePosition,
                   Date lastUpdateDate,
                   Collection<Location> locations,
                   Collection<GenomicContext> genomicContexts,
                   Collection<EffectAllele> effectAlleles) {
        this.externalId = externalId;
        this.merged = merged;
        this.functionalClass = functionalClass;
        this.lastUpdateDate = lastUpdateDate;
        this.locations = locations;
        this.genomicContexts = genomicContexts;
        this.effectAlleles = effectAlleles;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
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

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getMerged() {
        return merged;
    }

    public void setMerged(Long merged) {
        this.merged = merged;
    }

    public String getFunctionalClass() {
        return functionalClass;
    }

    public void setFunctionalClass(String functionalClass) {
        this.functionalClass = functionalClass;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    public void setLocations(Collection<Location> locations) {
        this.locations = locations;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public Collection<EffectAllele> getEffectAlleles() {
        return effectAlleles;
    }

    public void setEffectAlleles(Collection<EffectAllele> effectAlleles) {
        this.effectAlleles = effectAlleles;
    }


}
