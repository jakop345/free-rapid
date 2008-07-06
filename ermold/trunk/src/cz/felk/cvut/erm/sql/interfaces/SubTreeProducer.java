package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.icontree.IconNode;

/**
 * Produce subtree of icon nodes.
 *
 * @see cz.felk.cvut.erm.errorlog.icontree.IconNode
 */
public interface SubTreeProducer {
    /**
     * Retunrs subtree.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree();
}
