package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;

/**
 * Created by emma on 14/12/2015.
 *
 * @author emma
 *         <p>
 *         Performs basic syntax checking of main association components
 */
@Service
public class AssociationComponentsSyntaxChecks {

    public AssociationComponentsSyntaxChecks() {
    }

    /**
     * Check value for common syntax errors
     *
     * @param variantValue SNP RS_ID
     */
    public String checkVariant(String variantValue) {

        String error = "";
        error = runCommonChecks(variantValue, "VARIANT");

        if (variantValue.contains("-")) {
            error = error + "VARIANT " + variantValue + " contains a '-' character. ";
        }
        if (!variantValue.startsWith("rs")) {
            error = error + "VARIANT " + variantValue + " does not start with rs. ";
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param variantValue Proxy SNP RS_ID
     */
    public String checkProxy(String variantValue) {

        String error = "";
        error = runCommonChecks(variantValue, "Proxy Variant");

        if (variantValue.contains("-")) {
            error = error + "Variant " + variantValue + " contains a '-' character. ";
        }
        if (!variantValue.equals("NR")) {
            if (!variantValue.startsWith("rs")) {
                error = error + "Variant " + variantValue + " does not start with rs. ";
            }
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param effectAllele Risk allele name
     */
    public String checkEffectAllele(String effectAllele) {

        String error = "";
        error = runCommonChecks(effectAllele, "Risk Allele");

        if (!effectAllele.startsWith("rs")) {
            error = error + "Risk Allele " + effectAllele + " does not start with rs. ";
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param value     value to check
     * @param valueType Type of value i.e. SNP, Proxy SNP, Risk Allele
     */

    public String runCommonChecks(String value, String valueType) {

        String error = "";
        if (value.contains(",")) {
            error = valueType + " " + value + " contains a ',' character. ";
        }
        if (value.contains("x")) {
            error = error + valueType + " " + value + " contains an 'x' character. ";
        }
        if (value.contains("X")) {
            error = error + valueType + " " + value + " contains an 'X' character. ";
        }
        if (value.contains(":")) {
            error = error + valueType + " " + value + " contains a ':' character. ";
        }
        if (value.contains(";")) {
            error = error + valueType + " " + value + " contains a ';' character. ";
        }

        return error;
    }

}
