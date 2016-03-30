package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 *
 * @author emma
 *         <p>
 *         Service class that deals with rows on SNP association form
 */
public class VariantFormRow {

    private String variant;

    private String strongestEffectAllele;

    private Collection<String> proxyVariants;

    private Long merged;

    public VariantFormRow() {
    }

    public VariantFormRow(String variant, String strongestEffectAllele, Collection<String> proxyVariants) {
        this.variant = variant;
        this.strongestEffectAllele = strongestEffectAllele;
        this.proxyVariants = proxyVariants;
        this.merged = merged;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getStrongestEffectAllele() {
        return strongestEffectAllele;
    }

    public void setStrongestEffectAllele(String strongestEffectAllele) {
        this.strongestEffectAllele = strongestEffectAllele;
    }

    public Collection<String> getProxyVariants() {
        return proxyVariants;
    }

    public void setProxyVariants(Collection<String> proxyVariants) {
        this.proxyVariants = proxyVariants;
    }

    public Long getMerged() { return merged; }

    public void setMerged(Long merged) {
        this.merged = merged;
    }
}
