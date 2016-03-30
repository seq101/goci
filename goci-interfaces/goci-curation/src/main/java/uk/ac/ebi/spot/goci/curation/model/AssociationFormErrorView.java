package uk.ac.ebi.spot.goci.curation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 29/05/2015.
 *
 * @author emma
 *         <p>
 *         Model object used to store errors in an assoication
 */
public class AssociationFormErrorView {

    private String variantErrors;

    private String effectAlleleErrors;

    private String proxyErrors;

    private Map<String, String> associationErrorMap = new HashMap<>();

    // Constructor
    public AssociationFormErrorView() {

    }

    public String getVariantErrors() {
        return variantErrors;
    }

    public void setVariantErrors(String variantErrors) {
        this.variantErrors = variantErrors;
    }

    public String getEffectAlleleErrors() {
        return effectAlleleErrors;
    }

    public void setEffectAlleleErrors(String effectAlleleErrors) {
        this.effectAlleleErrors = effectAlleleErrors;
    }

    public String getProxyErrors() {
        return proxyErrors;
    }

    public void setProxyErrors(String proxyErrors) {
        this.proxyErrors = proxyErrors;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }
}
