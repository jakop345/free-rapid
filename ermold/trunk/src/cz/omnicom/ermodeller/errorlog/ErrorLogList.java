package cz.omnicom.ermodeller.errorlog;

import java.util.Enumeration;
import java.util.Vector;

/**
 * ErrorLogList is a <code>Vector</code> which can concatenate with another vector.
 *
 * @see java.util.Vector
 */
public class ErrorLogList extends Vector<ValidationError> {
    /**
     * Adds all errors from <code>aErrorLogList</code> to this list.
     *
     * @param aErrorLogList cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
    public synchronized void concatErrorLogList(ErrorLogList aErrorLogList) {
        ErrorLogList copyErrorLogList;
        synchronized (aErrorLogList) {
            copyErrorLogList = (ErrorLogList) aErrorLogList.clone();
        }
        for (Enumeration newErrors = copyErrorLogList.elements(); newErrors.hasMoreElements();) {
            this.addElement((ValidationError) newErrors.nextElement());
        }
    }
}
