/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vity.freerapid.plugins.services.rapidshare_premium;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;

/**
 * @author Tomáš Procházka &lt;<a href="mailto:tomas.prochazka@atomsoft.cz">tomas.prochazka@atomsoft.cz</a>&gt;
 * @version $Revision$ ($Date$)
 */
class BadLoginException extends ErrorDuringDownloadingException {

    /** Constructor */
    public BadLoginException() {
    }

    public BadLoginException(String message) {
        super(message);
    }
}

