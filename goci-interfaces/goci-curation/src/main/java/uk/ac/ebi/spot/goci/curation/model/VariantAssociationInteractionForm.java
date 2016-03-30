package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 12/02/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with form used by curators to enter snp/association details for interaction studies
 */
public class VariantAssociationInteractionForm {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String pvalueText;

    private Float orPerCopyNum;

    private String variantType;

    private Boolean variantApproved = false;

    private Boolean orType;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private List<VariantFormColumn> variantFormColumns = new ArrayList<>();

    private List<VariantMappingForm> variantMappingForms = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Integer numOfInteractions;

    private String effectAlleleFrequency;


    // Constructors
    public VariantAssociationInteractionForm() {
    }

    public VariantAssociationInteractionForm(Long associationId,
                                             String pvalueText,
                                             Float orPerCopyNum,
                                             String variantType,
                                             Boolean variantApproved,
                                             Boolean orType,
                                             Integer pvalueMantissa,
                                             Integer pvalueExponent,
                                             Float orPerCopyRecip,
                                             Float orPerCopyStdError,
                                             String orPerCopyRange,
                                             String orPerCopyRecipRange,
                                             String orPerCopyUnitDescr,
                                             List<VariantFormColumn> variantFormColumns,
                                             List<VariantMappingForm> variantMappingForms,
                                             Collection<GenomicContext> genomicContexts,
                                             Collection<EfoTrait> efoTraits,
                                             Integer numOfInteractions,
                                             String effectAlleleFrequency) {
        this.associationId = associationId;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.variantType = variantType;
        this.variantApproved = variantApproved;
        this.orType = orType;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.variantFormColumns = variantFormColumns;
        this.variantMappingForms = variantMappingForms;
        this.genomicContexts = genomicContexts;
        this.efoTraits = efoTraits;
        this.numOfInteractions = numOfInteractions;
        this.effectAlleleFrequency = effectAlleleFrequency;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
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

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
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

    public List<VariantFormColumn> getVariantFormColumns() {
        return variantFormColumns;
    }

    public void setVariantFormColumns(List<VariantFormColumn> variantFormColumns) {
        this.variantFormColumns = variantFormColumns;
    }

    public List<VariantMappingForm> getVariantMappingForms() {
        return variantMappingForms;
    }

    public void setVariantMappingForms(List<VariantMappingForm> variantMappingForms) {
        this.variantMappingForms = variantMappingForms;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Integer getNumOfInteractions() {
        return numOfInteractions;
    }

    public void setNumOfInteractions(Integer numOfInteractions) {
        this.numOfInteractions = numOfInteractions;
    }

    public Boolean getVariantApproved() {
        return variantApproved;
    }

    public void setVariantApproved(Boolean variantApproved) {
        this.variantApproved = variantApproved;
    }

    public Boolean getOrType() {
        return orType;
    }

    public void setOrType(Boolean orType) {
        this.orType = orType;
    }

    public String getEffectAlleleFrequency() {
        return effectAlleleFrequency;
    }

    public void setEffectAlleleFrequency(String effectAlleleFrequency) {
        this.effectAlleleFrequency = effectAlleleFrequency;
    }

}
