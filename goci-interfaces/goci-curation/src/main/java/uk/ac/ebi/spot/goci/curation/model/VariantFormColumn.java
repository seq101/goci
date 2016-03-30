package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 12/02/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with columns used by curators to enter variant/association details for interaction studies
 */
public class VariantFormColumn {

    private String variant;

    private String strongestEffectAllele;

    private String effectFrequency;

    private Collection<String> authorReportedGenes;

    private Collection<String> proxyVariants;

    private Boolean genomeWide = false;

    private Boolean limitedList = false;

    // Constructors
    public VariantFormColumn() {
    }

    public VariantFormColumn(String variant,
                             String strongestEffectAllele,
                             String effectFrequency,
                             Collection<String> authorReportedGenes,
                             Collection<String> proxyVariants,
                             Boolean genomeWide,
                             Boolean limitedList) {
        this.variant = variant;
        this.strongestEffectAllele = strongestEffectAllele;
        this.effectFrequency = effectFrequency;
        this.authorReportedGenes = authorReportedGenes;
        this.proxyVariants = proxyVariants;
        this.genomeWide = genomeWide;
        this.limitedList = limitedList;
    }

    public String getVariant() {
        return variant;
    }

    public VariantFormColumn setVariant(String variant) {
        this.variant = variant;
        return this;
    }

    public String getStrongestEffectAllele() {
        return strongestEffectAllele;
    }

    public VariantFormColumn setStrongestEffectAllele(String strongestEffectAllele) {
        this.strongestEffectAllele = strongestEffectAllele;
        return this;
    }

    public String getEffectFrequency() {
        return effectFrequency;
    }

    public VariantFormColumn setEffectFrequency(String effectFrequency) {
        this.effectFrequency = effectFrequency;
        return this;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public VariantFormColumn setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
        return this;
    }


    public Collection<String> getProxyVariants() {
        return proxyVariants;
    }

    public void setProxyVariants(Collection<String> proxyVariants) {
        this.proxyVariants = proxyVariants;
    }

    public Boolean getGenomeWide() {
        return genomeWide;
    }

    public void setGenomeWide(Boolean genomeWide) {
        this.genomeWide = genomeWide;
    }

    public Boolean getLimitedList() {
        return limitedList;
    }

    public void setLimitedList(Boolean limitedList) {
        this.limitedList = limitedList;
    }
}
