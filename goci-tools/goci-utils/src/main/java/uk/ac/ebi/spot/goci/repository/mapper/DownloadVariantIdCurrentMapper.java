package uk.ac.ebi.spot.goci.repository.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dwelter on 23/03/15.
 */
@Component
public class DownloadVariantIdCurrentMapper implements CatalogDataMapper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Collections.singletonList(CatalogHeaderBinding.VARIANT_EXTERNALID_FOR_ID);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_VARIANT_ID;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String output;

        String externalId = databaseValues.get(CatalogHeaderBinding.VARIANT_EXTERNALID_FOR_ID);

        if (!externalId.isEmpty() && externalId.contains("rs")) {
            if (externalId.contains("x")) {
                String front = externalId.split("x")[0].trim();
                output = front.substring(2);
            }
            else {
                output = externalId.substring(2);
            }
        }
        else {
            output = "";
        }

        return output;
    }
}
