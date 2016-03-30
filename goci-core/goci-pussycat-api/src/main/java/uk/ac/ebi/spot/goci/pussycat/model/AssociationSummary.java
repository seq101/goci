package uk.ac.ebi.spot.goci.pussycat.model;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 01/09/14
 */
public interface AssociationSummary {
    String getPubMedID();

    String getFirstAuthor();

    String getPublicationDate();

    String getVariant();

    String getPvalue();

    String getGWASTraitName();

    String getEFOTraitLabel();

    URI getEFOTraitURI();
}
