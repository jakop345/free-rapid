package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2obj.interfaces.SubObjProducer;
import cz.omnicom.ermodeller.conc2obj.interfaces.SubTreeProducerObj;


/**
 * Superclass of SQL commands.
 */
public abstract class CommandObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
