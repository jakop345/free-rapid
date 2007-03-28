package net.wordrider.files;

import net.wordrider.core.Lng;
import net.wordrider.files.ti68kformat.TIFileException;

/**
 * @author Vity
 */
public final class InvalidDataTypeException extends TIFileException {
    private static final String errorMessage = Lng.getLabel("message.exception.badDatatype");

    public InvalidDataTypeException() {
    }

    public final String getMessage() {
        return errorMessage;
    }
}
