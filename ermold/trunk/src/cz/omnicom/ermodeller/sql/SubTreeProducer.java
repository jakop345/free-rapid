package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.icontree.IconNode;

/**
 * Produce subtree of icon nodes.
 *
 * @see cz.omnicom.ermodeller.errorlog.icontree.IconNode
 */
public interface SubTreeProducer {
    /**
     * Retunrs subtree.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree();
}
