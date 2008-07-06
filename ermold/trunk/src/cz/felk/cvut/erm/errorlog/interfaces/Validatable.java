package cz.felk.cvut.erm.errorlog.interfaces;

import cz.felk.cvut.erm.errorlog.ErrorLogList;
import cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException;

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
