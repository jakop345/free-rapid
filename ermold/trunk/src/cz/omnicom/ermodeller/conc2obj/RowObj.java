package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2obj.interfaces.SubObjProducer;
import cz.omnicom.ermodeller.conc2obj.interfaces.SubTreeProducerObj;


/**
 * Row is element of command. Commands contain only rows.
 *
 * @see cz.omnicom.ermodeller.sql.CommandSQL
 */
public abstract class RowObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
