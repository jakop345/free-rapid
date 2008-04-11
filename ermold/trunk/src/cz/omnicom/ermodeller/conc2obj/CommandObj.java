package cz.omnicom.ermodeller.conc2obj;


/**
 * Superclass of SQL commands.
 */
public abstract class CommandObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
