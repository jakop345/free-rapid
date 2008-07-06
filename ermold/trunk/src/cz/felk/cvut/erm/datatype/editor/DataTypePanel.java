package cz.felk.cvut.erm.datatype.editor;

import javax.swing.*;
//import java.beans.*;

/**
 * Superclass of panels customizing the datatype. Is part of <code>DataTypeEditor</code>.
 *
 * @see DataTypeEditor
 */
public abstract class DataTypePanel extends JPanel {
    /**
     * Editor in which the panel is added.
     */

    /*misto DataTypeEditor je JPanel*/
    private JPanel dataTypeEditor = null;

    public JPanel getDataTypeEditor() {
        return dataTypeEditor;
    }

    public void setDataTypeEditor(JPanel aDataTypeEditor) {
        dataTypeEditor = aDataTypeEditor;
    }
}