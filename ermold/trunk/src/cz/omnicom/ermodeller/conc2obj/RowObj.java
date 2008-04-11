package cz.omnicom.ermodeller.conc2obj;


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
