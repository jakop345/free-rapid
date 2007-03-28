package net.wordrider.files;

import net.wordrider.core.Lng;
import net.wordrider.files.ti68kformat.TIFileException;

/**
 * @author Vity
 */
public final class NotSupportedFileException extends TIFileException {
    private static final String errorMessage = Lng.getLabel("message.exception.badheader");
    private final String notSupportedfileType;

    public NotSupportedFileException(final String file) {
        this.notSupportedfileType = file;
    }

    public final String getMessage() {
        return errorMessage + notSupportedfileType;
    }
}
