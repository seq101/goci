package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.Location;

/**
 * Created by Laurent on 21/05/15.
 */
public class VariantMappingForm {

    private String variant;

    private Location location;

    public VariantMappingForm() {
    }

    public VariantMappingForm(String variant, Location location) {
        this.variant = variant;
        this.location = location;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
