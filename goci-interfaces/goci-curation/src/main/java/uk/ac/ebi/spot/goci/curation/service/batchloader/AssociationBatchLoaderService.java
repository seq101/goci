package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import uk.ac.ebi.spot.goci.curation.model.BatchUploadError;
import uk.ac.ebi.spot.goci.model.Association;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Upload, error check and create persistance obejects that can be saved in DB from a batch of SNPs from a .xlsx
 *         spreadsheet. Note that the spreadsheet must be of .xlsx format!
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 */
public interface AssociationBatchLoaderService {

    Collection<BatchUploadError> checkUploadForErrors(String fileName) throws InvalidFormatException, IOException;

    Collection<Association> processData(String fileName) throws InvalidFormatException, IOException;

}
