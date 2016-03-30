package uk.ac.ebi.spot.goci.curation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Model object that creates a view of a studies associations that can easily be rendered in a HTML table.
 */
public class VariantAssociationTableView {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String authorReportedGenes;

    private String strongestEffectAlleles;

    private String variants;

    private String proxyVariants;

    // Two different frequencies, one for overall association and
    // one for each effect allele
    private String associationEffectFrequency;

    private String effectAlleleFrequencies;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private String pvalueText;

    private String efoTraits;

    private Float orPerCopyNum;

    private Float orPerCopyRecip;

    private String orType;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private Float orPerCopyStdError;

    private String associationType;

    private String multiVariantHaplotype;

    private String variantInteraction;

    private String variantApproved;

    private String variantStatuses;

    private Map<String, String> associationErrorMap = new HashMap<>();

    private String associationErrorsChecked;

    private String lastMappingPerformedBy;

    private String lastMappingDate;

    private String syntaxErrorsFound;

    // Constructors
    public VariantAssociationTableView() {
    }

    public VariantAssociationTableView(Long associationId,
                                       String authorReportedGenes,
                                       String strongestEffectAlleles,
                                       String variants,
                                       String proxyVariants,
                                       String associationEffectFrequency,
                                       String effectAlleleFrequencies,
                                       Integer pvalueMantissa,
                                       Integer pvalueExponent,
                                       String pvalueText,
                                       String efoTraits,
                                       Float orPerCopyNum,
                                       Float orPerCopyRecip,
                                       String orType,
                                       String orPerCopyRange,
                                       String orPerCopyRecipRange,
                                       String orPerCopyUnitDescr,
                                       Float orPerCopyStdError,
                                       String associationType,
                                       String multiVariantHaplotype,
                                       String variantInteraction,
                                       String variantApproved,
                                       String variantStatuses,
                                       Map<String, String> associationErrorMap,
                                       String associationErrorsChecked,
                                       String lastMappingPerformedBy,
                                       String lastMappingDate,
                                       String syntaxErrorsFound) {
        this.associationId = associationId;
        this.authorReportedGenes = authorReportedGenes;
        this.strongestEffectAlleles = strongestEffectAlleles;
        this.variants = variants;
        this.proxyVariants = proxyVariants;
        this.associationEffectFrequency = associationEffectFrequency;
        this.effectAlleleFrequencies = effectAlleleFrequencies;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.pvalueText = pvalueText;
        this.efoTraits = efoTraits;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orType = orType;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.orPerCopyStdError = orPerCopyStdError;
        this.associationType = associationType;
        this.multiVariantHaplotype = multiVariantHaplotype;
        this.variantInteraction = variantInteraction;
        this.variantApproved = variantApproved;
        this.variantStatuses = variantStatuses;
        this.associationErrorMap = associationErrorMap;
        this.associationErrorsChecked = associationErrorsChecked;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastMappingDate = lastMappingDate;
        this.syntaxErrorsFound = syntaxErrorsFound;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(String authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public String getStrongestEffectAlleles() {
        return strongestEffectAlleles;
    }

    public void setStrongestEffectAlleles(String strongestEffectAlleles) {
        this.strongestEffectAlleles = strongestEffectAlleles;
    }

    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }

    public String getProxyVariants() {
        return proxyVariants;
    }

    public void setProxyVariants(String proxyVariants) {
        this.proxyVariants = proxyVariants;
    }

    public String getAssociationEffectFrequency() {
        return associationEffectFrequency;
    }

    public void setAssociationEffectFrequency(String associationEffectFrequency) {
        this.associationEffectFrequency = associationEffectFrequency;
    }

    public String getEffectAlleleFrequencies() {
        return effectAlleleFrequencies;
    }

    public void setEffectAlleleFrequencies(String effectAlleleFrequencies) {
        this.effectAlleleFrequencies = effectAlleleFrequencies;
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

    public String getPvalueText() {
        return pvalueText;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public String getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(String efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public String getOrType() {
        return orType;
    }

    public void setOrType(String orType) {
        this.orType = orType;
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

    public Float getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public void setOrPerCopyStdError(Float orPerCopyStdError) {
        this.orPerCopyStdError = orPerCopyStdError;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    public String getMultiVariantHaplotype() {
        return multiVariantHaplotype;
    }

    public void setMultiVariantHaplotype(String multiVariantHaplotype) {
        this.multiVariantHaplotype = multiVariantHaplotype;
    }

    public String getVariantInteraction() {
        return variantInteraction;
    }

    public void setVariantInteraction(String variantInteraction) {
        this.variantInteraction = variantInteraction;
    }

    public String getVariantApproved() {
        return variantApproved;
    }

    public void setVariantApproved(String variantApproved) {
        this.variantApproved = variantApproved;
    }

    public String getVariantStatuses() {
        return variantStatuses;
    }

    public void setVariantStatuses(String variantStatuses) {
        this.variantStatuses = variantStatuses;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

    public String getAssociationErrorsChecked() {
        return associationErrorsChecked;
    }

    public void setAssociationErrorsChecked(String associationErrorsChecked) {
        this.associationErrorsChecked = associationErrorsChecked;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public String getLastMappingDate() {
        return lastMappingDate;
    }

    public void setLastMappingDate(String lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public String getSyntaxErrorsFound() {
        return syntaxErrorsFound;
    }

    public void setSyntaxErrorsFound(String syntaxErrorsFound) {
        this.syntaxErrorsFound = syntaxErrorsFound;
    }
}
