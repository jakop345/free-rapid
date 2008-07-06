package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2obj.interfaces.SubObjProducer;
import cz.felk.cvut.erm.conc2obj.interfaces.SubTreeProducerObj;


/**
 * Row is element of command. Commands contain only rows.
 *
 * @see cz.felk.cvut.erm.sql.CommandSQL
 */
public abstract class RowObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
