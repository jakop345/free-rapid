package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2obj.interfaces.SubObjProducer;
import cz.omnicom.ermodeller.conc2obj.interfaces.SubTreeProducerObj;


/**
 * PartOfCheck represents any condition closed in brackets in check.
 */
public abstract class PartOfCheckObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
