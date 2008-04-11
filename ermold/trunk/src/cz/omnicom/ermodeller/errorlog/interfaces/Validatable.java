package cz.omnicom.ermodeller.errorlog.interfaces;

import cz.omnicom.ermodeller.errorlog.ErrorLogList;
import cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException;

/**
 * Interface implementing validatable object (which can be checked).
 */
public interface Validatable {
    /**
     * After validating retunrs list of errors.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
    ErrorLogList validate() throws CheckNameDuplicityValidationException;
}
