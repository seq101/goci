package uk.ac.ebi.spot.goci.curation.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.ac.ebi.spot.goci.curation.model.VariantFormColumn;

/**
 * Created by emma on 25/11/2015.
 *
 * @author emma
 *         <p>
 *         Custom validator for SnpFormColumn object, that checks for fields left blank by curators
 */
@Service
public class VariantFormColumnValidator implements Validator {
    @Override public boolean supports(Class clazz) {
        return VariantFormColumn.class.equals(clazz);
    }

    @Override public void validate(Object obj, Errors e) {
        VariantFormColumn col = (VariantFormColumn) obj;
        if (col.getVariant().isEmpty()) {
            e.rejectValue("variantFormColumns", "No Variant in column", "Please do not leave Variant field blank");
        }

        if (col.getStrongestEffectAllele().isEmpty()) {
            e.rejectValue("variantFormColumns",
                          "No Strongest Variant-Risk Allele in column",
                          "Please do not leave Strongest Variant-Risk Allele field blank");
        }
    }
}
