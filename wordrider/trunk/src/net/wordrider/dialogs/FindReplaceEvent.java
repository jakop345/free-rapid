package net.wordrider.dialogs;

import java.util.EventObject;

/**
 * This is simply overriding EventObject by storing a FindReplaceDialog into the source field of the superclass.
 * @author Ulrich Hilger
 * @author CalCom
 * @author <a href="http://www.calcom.de">http://www.calcom.de</a>
 * @author <a href="mailto:info@calcom.de">info@calcom.de</a>
 * @version 1.1, April 13, 2002
 */
final class FindReplaceEvent extends EventObject {
    FindReplaceEvent(final FindReplaceDialog source) {
        super(source);
    }
}