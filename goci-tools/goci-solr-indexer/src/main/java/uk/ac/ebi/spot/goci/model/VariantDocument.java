package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class VariantDocument extends EmbeddableDocument<Variant> {
    // basic snp information
    @Field private String externalId;
    @Field private String context;
    @Field private String last_modified;

    public VariantDocument(Variant variant) {
        super(variant);
        this.externalId = variant.getExternalId();

        this.context = variant.getFunctionalClass();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (variant.getLastUpdateDate() != null) {
            this.last_modified = df.format(variant.getLastUpdateDate());
        }
    }

    public String getExternalId() {
        return externalId;
    }

    public String getContext() {
        return context;
    }

    public String getLast_modified() {
        return last_modified;
    }

    @Override
    public String toString() {
        return "VariantDocument{" +
                "id=" + getId() +
                ", externalId='" + externalId + '\'' +
                '}';
    }

}
