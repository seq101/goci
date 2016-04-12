package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 *
 * @author emma
 *         <p>
 *         Locus object holds links to associated effect alleles and author reported genes
 */
@Entity
public class Locus {
    @Id
    @GeneratedValue
    private Long id;

    private Integer haplotypeVariantCount;

//    private String chromosomeName;
//
//    private String chromosomePosition;

    private String description;

    @ManyToMany
    @JoinTable(name = "LOCUS_EFFECT_ALLELE",
               joinColumns = @JoinColumn(name = "LOCUS_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFFECT_ALLELE_ID"))
    private Collection<EffectAllele> strongestEffectAlleles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "AUTHOR_REPORTED_GENE",
               joinColumns = @JoinColumn(name = "LOCUS_ID"),
               inverseJoinColumns = @JoinColumn(name = "REPORTED_GENE_ID"))
    private Collection<Gene> authorReportedGenes = new ArrayList<>();

    @ManyToOne
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "LOCUS_ID"),
               inverseJoinColumns = @JoinColumn(name = "ASSOCIATION_ID"))
    private Association association;

    // JPA no-args constructor
    public Locus() {
    }

    public Locus(Integer haplotypeVariantCount,
                 String description,
                 Collection<EffectAllele> strongestEffectAlleles,
                 Collection<Gene> authorReportedGenes) {
        this.haplotypeVariantCount = haplotypeVariantCount;
        this.description = description;
        this.strongestEffectAlleles = strongestEffectAlleles;
        this.authorReportedGenes = authorReportedGenes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHaplotypeVariantCount() {
        return haplotypeVariantCount;
    }

    public void setHaplotypeVariantCount(Integer haplotypeVariantCount) {
        this.haplotypeVariantCount = haplotypeVariantCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<EffectAllele> getStrongestEffectAlleles() {
        return strongestEffectAlleles;
    }

    public void setStrongestEffectAlleles(Collection<EffectAllele> strongestEffectAlleles) {
        this.strongestEffectAlleles = strongestEffectAlleles;
    }

    public Collection<Gene> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<Gene> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

//    public String getChromosomeName() {
//        return chromosomeName;
//    }
//
//    public void setChromosomeName(String chromosomeName) {
//        this.chromosomeName = chromosomeName;
//    }
//
//    public String getChromosomePosition() {
//        return chromosomePosition;
//    }
//
//    public void setChromosomePosition(String chromosomePosition) {
//        this.chromosomePosition = chromosomePosition;
//    }

    @Override public String toString() {
        return "Locus{" +
                "id=" + id +
                ", haplotypeVariantCount=" + haplotypeVariantCount +
                ", description='" + description + '\'' +
                '}';
    }
}
