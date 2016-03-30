package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 07/01/15.
 *
 * @author emma
 *         <p>
 *         Service class used to deal with curator reported SNPs, which are entered as separate tags
 */
public class CuratorReportedVariant {

    private Collection<String> reportedVariantValue;

    public CuratorReportedVariant(Collection<String> reportedVariantValue) {
        this.reportedVariantValue = reportedVariantValue;
    }

    public CuratorReportedVariant() {

    }

    public Collection<String> getReportedVariantValue() {
        return reportedVariantValue;
    }

    public void setReportedVariantValue(Collection<String> reportedVariantValue) {
        this.reportedVariantValue = reportedVariantValue;
    }
}
