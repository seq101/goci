package uk.ac.ebi.spot.goci.curation.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.ac.ebi.spot.goci.curation.model.VariantFormRow;

/**
 * Created by emma on 25/11/2015.
 *
 * @author emma
 *         <p>
 *         Custom validator for SnpFormRow object, that checks for fields left blank by curators
 */
@Service
public class VariantFormRowValidator implements Validator {

    public boolean supports(Class clazz) {
        return VariantFormRow.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        VariantFormRow row = (VariantFormRow) obj;
        if (row.getVariant().isEmpty()) {
            e.rejectValue("variantFormRows", "No Variant in row", "Please do not leave Variant field blank");
        }

        if (row.getStrongestEffectAllele().isEmpty()) {
            e.rejectValue("variantFormRows",
                          "No Strongest Variant-Risk Allele in row",
                          "Please do not leave Strongest Variant-Risk Allele field blank");
        }
    }
}
