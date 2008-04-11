package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.icontree.IconNode;

/**
 * Produce subtree of icon nodes.
 *
 * @see cz.omnicom.ermodeller.errorlog.icontree.IconNode
 */
public interface SubTreeProducerObj {
    /**
     * Retunrs subtree.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree();
}
