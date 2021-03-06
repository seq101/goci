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
public class SnpDocument extends EmbeddableDocument<SingleNucleotidePolymorphism> {
    // basic snp information
    @Field private String rsId;
    @Field private String context;
    @Field private String last_modified;

    public SnpDocument(SingleNucleotidePolymorphism snp) {
        super(snp);
        this.rsId = snp.getRsId();

        this.context = snp.getFunctionalClass();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (snp.getLastUpdateDate() != null) {
            this.last_modified = df.format(snp.getLastUpdateDate());
        }
    }

    public String getRsId() {
        return rsId;
    }

    public String getContext() {
        return context;
    }

    public String getLast_modified() {
        return last_modified;
    }

    @Override
    public String toString() {
        return "SnpDocument{" +
                "id=" + getId() +
                ", rsId='" + rsId + '\'' +
                '}';
    }

}
