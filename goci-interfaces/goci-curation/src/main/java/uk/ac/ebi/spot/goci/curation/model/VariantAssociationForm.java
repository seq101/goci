package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 28/01/15.
 *
 * @author emma
 *         <p>
 *         New service class to deal with form used by curators to enter snp/association details
 */

public class VariantAssociationForm {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String effectAlleleFrequency;

    private String pvalueText;

    private Float orPerCopyNum;

    private Boolean orType = false;

    private String variantType;

    private Boolean multiVariantHaplotype = false;

    private Boolean variantInteraction = false;

    private Boolean variantApproved = false;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float pvalueFloat;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private List<VariantFormRow> variantFormRows = new ArrayList<>();

    private List<VariantMappingForm> variantMappingForms = new ArrayList<>();

    private Collection<String> authorReportedGenes;

    // These attributes store locus attributes
    private String multiVariantHaplotypeDescr;

    private Integer multiVariantHaplotypeNum;

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private Map<String, String> associationErrorMap = new HashMap<>();

    // Constructors
    public VariantAssociationForm() {
    }

    public VariantAssociationForm(Long associationId,
                                  String effectAlleleFrequency,
                                  String pvalueText,
                                  Float orPerCopyNum,
                                  Boolean orType,
                                  String variantType,
                                  Boolean multiVariantHaplotype,
                                  Boolean variantInteraction,
                                  Boolean variantApproved,
                                  Integer pvalueMantissa,
                                  Integer pvalueExponent,
                                  Float pvalueFloat,
                                  Float orPerCopyRecip,
                                  Float orPerCopyStdError,
                                  String orPerCopyRange,
                                  String orPerCopyRecipRange,
                                  String orPerCopyUnitDescr,
                                  List<VariantFormRow> variantFormRows,
                                  List<VariantMappingForm> variantMappingForms,
                                  Collection<String> authorReportedGenes,
                                  String multiVariantHaplotypeDescr,
                                  Integer multiVariantHaplotypeNum,
                                  Collection<EfoTrait> efoTraits,
                                  Collection<GenomicContext> genomicContexts,
                                  Map<String, String> associationErrorMap) {
        this.associationId = associationId;
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
        this.pvalueFloat = pvalueFloat;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.variantFormRows = variantFormRows;
        this.variantMappingForms = variantMappingForms;
        this.authorReportedGenes = authorReportedGenes;
        this.multiVariantHaplotypeDescr = multiVariantHaplotypeDescr;
        this.multiVariantHaplotypeNum = multiVariantHaplotypeNum;
        this.efoTraits = efoTraits;
        this.genomicContexts = genomicContexts;
        this.associationErrorMap = associationErrorMap;
    }

    // Getters/setters
    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
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

    public String getMultiVariantHaplotypeDescr() {
        return multiVariantHaplotypeDescr;
    }

    public void setMultiVariantHaplotypeDescr(String multiVariantHaplotypeDescr) {
        this.multiVariantHaplotypeDescr = multiVariantHaplotypeDescr;
    }

    public Integer getMultiVariantHaplotypeNum() {
        return multiVariantHaplotypeNum;
    }

    public void setMultiVariantHaplotypeNum(Integer multiVariantHaplotypeNum) {
        this.multiVariantHaplotypeNum = multiVariantHaplotypeNum;
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

    public Float getPvalueFloat() {
        return pvalueFloat;
    }

    public void setPvalueFloat(Float pvalueFloat) {
        this.pvalueFloat = pvalueFloat;
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

    public List<VariantFormRow> getVariantFormRows() {
        return variantFormRows;
    }

    public void setVariantFormRows(List<VariantFormRow> variantFormRows) {
        this.variantFormRows = variantFormRows;
    }

    public List<VariantMappingForm> getVariantMappingForms() {
        return variantMappingForms;
    }

    public void setVariantMappingForms(List<VariantMappingForm> variantMappingForms) {
        this.variantMappingForms = variantMappingForms;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Boolean getVariantApproved() {
        return variantApproved;
    }

    public void setVariantApproved(Boolean variantApproved) {
        this.variantApproved = variantApproved;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

}

