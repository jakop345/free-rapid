package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2obj.interfaces.SubObjProducer;
import cz.felk.cvut.erm.conc2obj.interfaces.SubTreeProducerObj;


/**
 * Superclass of SQL commands.
 */
abstract class CommandObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
