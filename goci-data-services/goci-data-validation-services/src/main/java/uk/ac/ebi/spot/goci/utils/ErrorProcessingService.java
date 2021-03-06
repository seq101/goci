package uk.ac.ebi.spot.goci.utils;

import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by emma on 14/04/2016.
 *
 * @author emma
 *         <p>
 *         Utility service to format errors for return to user
 */
public class ErrorProcessingService {
    /**
     * Create error object
     *
     * @param message       Error message
     * @param columnChecked Name of the column checked
     */
    public static ValidationError createError(String message, String columnChecked, Boolean warning) {
        ValidationError error = new ValidationError();

        // If there is an error create a fully formed object
        if (message != null) {
            error.setField(columnChecked);
            error.setError(message);
            error.setWarning(warning);
        }
        return error;
    }

    /**
     * Check error objects created to ensure we only return those with an actual message and location
     *
     * @param errors Errors to be checked
     * @return list of errors with message and field
     */
    public static Collection<ValidationError> checkForValidErrors(Collection<ValidationError> errors) {
        return errors.stream()
                .filter(validationError -> validationError.getError() != null).collect(Collectors.toList());
    }
}