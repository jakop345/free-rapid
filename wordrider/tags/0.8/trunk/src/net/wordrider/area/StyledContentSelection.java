package net.wordrider.area;

import java.awt.datatransfer.*;

//import java.io.StringReader;

/**
 * @author Vity
 */
final class StyledContentSelection implements Transferable, ClipboardOwner {
    private static final int STYLED_TEXT = 0;
    private static final int STRING = 1;

    /**
     * the data to transfer
     */
    private final StyledContent data;

    /**
     * the data flavor of this transferable
     */
    private static final DataFlavor[] flavors = {
            new DataFlavor(StyledContent.class, "StyledContent"),
            DataFlavor.stringFlavor
    };

    /**
     * construct a <code>StyledContentSelection</code> with a chunk of styled text.
     * @param data - a StyledContent object
     */
    public StyledContentSelection(final StyledContent data) {
        this.data = data;
    }

    /* ---- start of Transferable implementation ----------------------------*/

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in.  The array should be
     * ordered according to preference for providing the data (from most richly descriptive to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public final DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    /**
     * Returns whether or not the specified data flavor is supported for this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating wjether or not the data flavor is supported
     */
    public final boolean isDataFlavorSupported(final DataFlavor flavor) {
        for (DataFlavor flavor1 : flavors)
            if (flavor1.equals(flavor))
                return true;
        return false;
    }

    /**
     * Returns an object which represents the data to be transferred.  The class of the object returned is defined by
     * the representation class of the flavor.
     * @param flavor the requested flavor for the data
     * @throws UnsupportedFlavorException if the requested data flavor is not supported.
     */
    public final Object getTransferData(final DataFlavor flavor) throws
            UnsupportedFlavorException {
        if (flavor.equals(flavors[STYLED_TEXT])) {
            return data;
        } else if (flavor.equals(flavors[STRING])) {
            return data.toString();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /* ----------- end of Transferable implementation ------------------- */

    /* ----------- start of ClipboardOwner implementation --------------- */

    /**
     * Notifies this object that it is no longer the owner of the contents of the clipboard.
     * @param clipboard the clipboard that is no longer owned
     * @param contents  the contents which this owner had placed on the clipboard
     */
    public final void lostOwnership(final Clipboard clipboard, final Transferable contents) {
    }

    /* ------------ end of ClipboardOwner implementation ---------------- */
}