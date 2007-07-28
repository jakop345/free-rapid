package cz.cvut.felk.timejuggler.swing;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.text.DateFormat;

/**
 * Trida slouzici k instanciovani upravenych zakladnich komponent
 * @author Vity
 */

public class ComponentFactory {

    private ComponentFactory() {
    }

    public static JSpinner getTimeSpinner() {
        final JSpinner spinner = new JSpinner(new SpinnerDateModel());
        ((JSpinner.DateEditor) spinner.getEditor()).getTextField().setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(DateFormat.getTimeInstance(DateFormat.SHORT))));
        return spinner;
    }

    public static JComboBox getComboBox() {
        JComboBox combo = new JComboBox(new NaiiveComboModel());
        combo.setRenderer(new ComboBoxRenderer());
        return combo;
    }


    public static JEditorPane getEmailsEditorPane() {
        return new EditorPaneLinkDetector();
    }

    private static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        static final String SEPARATOR = "-";
        JSeparator separator;

        public ComboBoxRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            separator = new JSeparator(JSeparator.HORIZONTAL);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (SEPARATOR.equals(value)) {
                return separator;
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            String str = (value == null) ? "" : value.toString();
            setFont(list.getFont());
            setText(str);
            return this;
        }
    }

    public static class NaiiveComboModel extends DefaultComboBoxModel {
        public NaiiveComboModel() {
            super();
        }

        public NaiiveComboModel(Object items[]) {
            super(items);
        }

        public void setSelectedItem(Object o) {
            //Object currentItem = getSelectedItem();
            if (!ComboBoxRenderer.SEPARATOR.equals(o)) {
                super.setSelectedItem(o);
            }
        }
    }

}
