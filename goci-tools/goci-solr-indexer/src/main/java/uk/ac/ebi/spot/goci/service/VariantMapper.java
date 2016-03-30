package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.VariantIndex;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.model.VariantDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class VariantMapper
        extends ObjectDocumentMapper<Variant, VariantDocument> {
    @Autowired
    public VariantMapper(ObjectConverter objectConverter, VariantIndex variantIndex) {
        super(VariantDocument.class, objectConverter, variantIndex);
    }
}
