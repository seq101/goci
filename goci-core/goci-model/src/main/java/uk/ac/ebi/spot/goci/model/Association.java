package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an association
 */


@Entity
public class Association {
    @Id
    @GeneratedValue
    private Long id;


    private String effectAlleleFrequency;
    private String effectAlleleFrequencyCases;
    private String effectAlleleFrequencyControls;

    private String pvalueText;

    private Float orPerCopyNum;

    private Boolean orType = false;

    private String variantType;

    private Boolean multiVariantHaplotype = false;

    private Boolean variantInteraction = false;

    private Boolean variantApproved = false;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    /**
     * The variant direction in the different studies of the mega analysis. <br>
     * eg. : +-? means that the direction of that variant for the first study was +, of the second study was -, and the variant
     * was not found in the last study. + means that this variant makes the occurence of the trait more likely, - means
     * it makes the occurence of the trait less likely.
     */
    private String metaDirection;

    /**
     * The heterogeniety score (for Meta analysis) also called sometimes hetlsqt.
     */
    private BigDecimal heterogeneityScore;

    /**
     * The mantissa of the heterogeneity pvalue (for Meta analysis)
     */
    private Integer heterogeneityPvalueMantissa;

    /**
     * The exponent of the heterogeneity pvalue (for Meta analysis)
     */
    private Integer heterogeneityPvalueExponent;

    // The variant imputation quality score.
    private BigDecimal imputationQualityScore;

    private BigDecimal bayesFactorLog10;

    private BigDecimal standardError;

    /**
     * The association beta. This will be greater then 0 if the variant makes the trait more likely and inferior to 0 if
     * the variant makes the trait less likely.
     */
    private BigDecimal beta;

    @ManyToOne
    private Study study;

    // Association can have a number of loci attached depending on whether its a multi-variant haplotype
    // or VARIANT:VARIANT interaction
    @OneToMany
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCUS_ID"))
    private Collection<Locus> loci = new ArrayList<>();

    // To avoid null values collections are by default initialized to an empty array list
    @ManyToMany
    @JoinTable(name = "ASSOCIATION_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    @OneToOne(mappedBy = "association", cascade = CascadeType.REMOVE)
    private AssociationReport associationReport;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    private String lastMappingPerformedBy;



    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    // JPA no-args constructor
    public Association() {
    }

    public Association(String effectAlleleFrequency,
                       String effectAlleleFrequencyCases,
                       String effectAlleleFrequencyControls,
                       String pvalueText,
                       Float orPerCopyNum,
                       Boolean orType,
                       String variantType,
                       Boolean multiVariantHaplotype,
                       Boolean variantInteraction,
                       Boolean variantApproved,
                       Integer pvalueMantissa,
                       Integer pvalueExponent,
                       Float orPerCopyRecip,
                       Float orPerCopyStdError,
                       String orPerCopyRange,
                       String orPerCopyRecipRange,
                       String orPerCopyUnitDescr,
                       Study study,
                       Collection<Locus> loci,
                       Collection<EfoTrait> efoTraits,
                       AssociationReport associationReport,
                       Date lastMappingDate,
                       String lastMappingPerformedBy,
                       Date lastUpdateDate,
                       BigDecimal imputationQualityScore,
                       BigDecimal bayesFactorLog10,
                       String metaDirection,
                       BigDecimal heterogeneityScore,
                       Integer heterogeneityPvalueMantissa,
                       Integer heterogeneityPvalueExponent,
                       BigDecimal beta,
                       BigDecimal standardError) {
        this.effectAlleleFrequency = effectAlleleFrequency;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.orType = orType;
        this.variantType = variantType;
        this.multiVariantHaplotype = multiVariantHaplotype;
        this.variantInteraction = variantInteraction;
        this.variantApproved = variantApproved;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.study = study;
        this.loci = loci;
        this.efoTraits = efoTraits;
        this.associationReport = associationReport;
        this.lastMappingDate = lastMappingDate;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastUpdateDate = lastUpdateDate;
        this.imputationQualityScore = imputationQualityScore;
        this.bayesFactorLog10 = bayesFactorLog10;
        this.metaDirection = metaDirection;
        this.heterogeneityScore = heterogeneityScore;
        this.heterogeneityPvalueMantissa = heterogeneityPvalueMantissa;
        this.heterogeneityPvalueExponent = heterogeneityPvalueExponent;
        this.beta = beta;
        this.standardError = standardError;
        this.effectAlleleFrequencyCases = effectAlleleFrequencyCases;
        this.effectAlleleFrequencyControls = effectAlleleFrequencyControls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEffectAlleleFrequency() {
        return effectAlleleFrequency;
    }

    public void setEffectAlleleFrequency(String effectAlleleFrequency) {
        this.effectAlleleFrequency = effectAlleleFrequency;
    }

    public String getPvalueText() {
        return pvalueText;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public Boolean getOrType() {
        return orType;
    }

    public void setOrType(Boolean orType) {
        this.orType = orType;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public Boolean getMultiVariantHaplotype() {
        return multiVariantHaplotype;
    }

    public void setMultiVariantHaplotype(Boolean multiVariantHaplotype) {
        this.multiVariantHaplotype = multiVariantHaplotype;
    }

    public Boolean getVariantInteraction() {
        return variantInteraction;
    }

    public void setVariantInteraction(Boolean variantInteraction) {
        this.variantInteraction = variantInteraction;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public Float getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public void setOrPerCopyStdError(Float orPerCopyStdError) {
        this.orPerCopyStdError = orPerCopyStdError;
    }

    public String getOrPerCopyRange() {
        return orPerCopyRange;
    }

    public void setOrPerCopyRange(String orPerCopyRange) {
        this.orPerCopyRange = orPerCopyRange;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public void setOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<Locus> getLoci() {
        return loci;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public void addEfoTrait(EfoTrait efoTrait) {
        efoTraits.add(efoTrait);
    }

    public Boolean getVariantApproved() {
        return variantApproved;
    }

    public void setVariantApproved(Boolean variantApproved) {
        this.variantApproved = variantApproved;
    }

    public AssociationReport getAssociationReport() {
        return associationReport;
    }

    public void setAssociationReport(AssociationReport associationReport) {
        this.associationReport = associationReport;
    }

    public Date getLastMappingDate() {
        return lastMappingDate;
    }

    public void setLastMappingDate(Date lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public double getPvalue() {
        return (pvalueMantissa * Math.pow(10, pvalueExponent));
    }

    public BigDecimal getImputationQualityScore() {
        return imputationQualityScore;
    }

    public void setImputationQualityScore(BigDecimal imputationQualityScore) {
        this.imputationQualityScore = imputationQualityScore;
    }

    public BigDecimal getBayesFactorLog10() {
        return bayesFactorLog10;
    }

    public void setBayesFactorLog10(BigDecimal bayesFactorLog10) {
        this.bayesFactorLog10 = bayesFactorLog10;
    }

    public String getMetaDirection() {
        return metaDirection;
    }

    public void setMetaDirection(String metaDirection) {
        this.metaDirection = metaDirection;
    }

    public BigDecimal getHeterogeneityScore() {
        return heterogeneityScore;
    }

    public void setHeterogeneityScore(BigDecimal heterogeneityScore) {
        this.heterogeneityScore = heterogeneityScore;
    }

    public Integer getHeterogeneityPvalueExponent() {
        return heterogeneityPvalueExponent;
    }

    public void setHeterogeneityPvalueExponent(Integer heterogeneityPvalueExponent) {
        this.heterogeneityPvalueExponent = heterogeneityPvalueExponent;
    }

    public Integer getHeterogeneityPvalueMantissa() {
        return heterogeneityPvalueMantissa;
    }

    public void setHeterogeneityPvalueMantissa(Integer heterogeneityPvalueMantissa) {
        this.heterogeneityPvalueMantissa = heterogeneityPvalueMantissa;
    }

    public BigDecimal getStandardError() {
        return standardError;
    }

    public void setStandardError(BigDecimal standardError) {
        this.standardError = standardError;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public String getEffectAlleleFrequencyCases() {
        return effectAlleleFrequencyCases;
    }

    public void setEffectAlleleFrequencyCases(String effectAlleleFrequencyCases) {
        this.effectAlleleFrequencyCases = effectAlleleFrequencyCases;
    }

    public String getEffectAlleleFrequencyControls() {
        return effectAlleleFrequencyControls;
    }

    public void setEffectAlleleFrequencyControls(String effectAlleleFrequencyControls) {
        this.effectAlleleFrequencyControls = effectAlleleFrequencyControls;
    }
}
