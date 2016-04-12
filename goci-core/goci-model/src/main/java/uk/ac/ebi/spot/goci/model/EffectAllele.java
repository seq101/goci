package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 */
@Entity
public class EffectAllele {

    @Id
    @GeneratedValue
    private Long id;

    private String effectAlleleName;
    private String otherAlleleName;

    // Values required for SNP Interaction associations
    private String effectFrequency;

    private Boolean genomeWide = false;

    private Boolean limitedList = false;

    @ManyToOne
    @JoinTable(name = "EFFECT_ALLELE_VARIANT",
               joinColumns = @JoinColumn(name = "EFFECT_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "VARIANT_ID"))
    private Variant variant;

    @ManyToMany
    @JoinTable(name = "EFFECT_ALLELE_PROXY_VARIANT",
               joinColumns = @JoinColumn(name = "EFFECT_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "VARIANT_ID"))
    private Collection<Variant> proxyVariants;


    @ManyToMany(mappedBy = "strongestEffectAlleles")
    private Collection<Locus> loci;


    // JPA no-args constructor
    public EffectAllele() {
    }

    public EffectAllele(String effectAlleleName,
                        String otherAlleleName,
                        String effectFrequency,
                        Boolean genomeWide,
                        Boolean limitedList,
                        Variant variant,
                        Collection<Variant> proxyVariants,
                        Collection<Locus> loci) {
        this.effectAlleleName = effectAlleleName;
        this.effectFrequency = effectFrequency;
        this.genomeWide = genomeWide;
        this.limitedList = limitedList;
        this.variant = variant;
        this.proxyVariants = proxyVariants;
        this.otherAlleleName = otherAlleleName;
        this.loci = loci;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEffectAlleleName() {
        return effectAlleleName;
    }

    public void setEffectAlleleName(String effectAlleleName) {
        this.effectAlleleName = effectAlleleName;
    }

    public String getOtherAlleleName() {
        return otherAlleleName;
    }

    public void setOtherAlleleName(String otherAlleleName) {
        this.otherAlleleName = otherAlleleName;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }


    public Collection<Variant> getProxyVariants() {
        return proxyVariants;
    }

    public void setProxyVariants(Collection<Variant> proxyVariants) {
        this.proxyVariants = proxyVariants;
    }

    public Collection<Locus> getLoci() {
        return loci;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    public void setEffectFrequency(String effectFrequency) {
        this.effectFrequency = effectFrequency;
    }

    public void setGenomeWide(Boolean genomeWide) {
        this.genomeWide = genomeWide;
    }

    public void setLimitedList(Boolean limitedList) {
        this.limitedList = limitedList;
    }

    public String getEffectFrequency() {
        return effectFrequency;
    }

    public Boolean getGenomeWide() {
        return genomeWide;
    }

    public Boolean getLimitedList() {
        return limitedList;
    }

    @Override public String toString() {
        return "EffectAllele{" +
                "id=" + id +
                ", effectAlleleName='" + effectAlleleName + '\'' +
                ", effectFrequency='" + effectFrequency + '\'' +
                ", genomeWide=" + genomeWide +
                ", limitedList=" + limitedList +
                ", variant=" + variant +
                ", proxyVariants=" + proxyVariants +
                ", loci=" + loci +
                '}';
    }
}
