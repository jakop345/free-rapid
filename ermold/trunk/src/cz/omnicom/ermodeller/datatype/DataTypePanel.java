package cz.omnicom.ermodeller.datatype;

import javax.swing.*;
//import java.beans.*;

/**
 * Superclass of panels customizing the datatype. Is part of <code>DataTypeEditor</code>.
 *
 * @see cz.omnicom.ermodeller.datatype.DataTypeEditor
 */
public abstract class DataTypePanel extends JPanel {
    /**
     * Editor in which the panel is added.
     */

    /*misto DataTypeEditor je JPanel*/
    private JPanel dataTypeEditor = null;

    protected JPanel getDataTypeEditor() {
        return dataTypeEditor;
    }

    public void setDataTypeEditor(JPanel aDataTypeEditor) {
        dataTypeEditor = aDataTypeEditor;
    }
}