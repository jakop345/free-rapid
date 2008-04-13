package cz.green.ermodeller.dialogs;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import cz.omnicom.ermodeller.datatype.DataType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DataTypePropertyEditor
 *
 * @author Ladislav Vitasek
 */
public class DataTypePropertyEditor extends AbstractPropertyEditor {

    private DefaultCellRenderer label;
    private DataType dataType;

    public DataTypePropertyEditor() {
        editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
        label = new DefaultCellRenderer();
        ((JPanel) editor).add("*", label);
        label.setOpaque(false);
        //JButton button = ComponentFactory.Helper.getFactory().createMiniButton();
        JButton button = new JButton("");
        ((JPanel) editor).add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectDataType();
            }
        });
        ((JPanel) editor).setOpaque(false);
    }

    public Object getValue() {
        return dataType;
    }

    public void setValue(Object value) {
        dataType = (DataType) value;
        label.setValue(dataType.toDescriptionString());
    }

    protected void selectDataType() {
        //ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
        JOptionPane.showMessageDialog(editor, "BLBLLBLLL");
        //Color selectedColor = JColorChooser.showDialog(editor, title, dataType);

//        if (selectedColor != null) {
//            DataType oldDataType = dataType;
//            DataType newColor = selectedColor;
//            label.setValue(newColor);
//            dataType = newColor;
//            firePropertyChange(oldDataType, newColor);
//        }
    }

}