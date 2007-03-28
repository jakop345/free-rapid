package net.wordrider.dialogs;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Vity
 */
class CheckRenderer extends JPanel implements ListCellRenderer {
    final JCheckBox check;
    private final ListLabel label;
    private final Icon commonIcon;
    private static Border noFocusBorder;


    CheckRenderer() {
        setLayout(null);
        add(check = new JCheckBox());
        add(label = new ListLabel());
        check.setBackground(null);
        if (noFocusBorder == null)
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        setBorder(noFocusBorder);
        check.setBorder(noFocusBorder);
        commonIcon = UIManager.getIcon("Tree.leafIcon");
    }

    public final Dimension getPreferredSize() {
        final Dimension d_check = check.getPreferredSize();
        final Dimension d_label = label.getPreferredSize();
        return new Dimension(d_check.width + d_label.width,
                (d_check.height < d_label.height ?
                        d_label.height : d_check.height));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return this.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus, null);
    }

    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus, Icon icon) {
        setComponentOrientation(list.getComponentOrientation());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());

        label.setFont(list.getFont());
        label.setText(value.toString());
        if (icon == null)
            icon = commonIcon;
        label.setIcon(icon);
        setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        return this;
    }


    public final void doLayout() {
        final Dimension d_check = check.getPreferredSize();
        final Dimension d_label = label.getPreferredSize();
        int y_check = 0;
        int y_label = 0;
        if (d_check.height < d_label.height) {
            y_check = (d_label.height - d_check.height) / 2;
        } else {
            y_label = (d_check.height - d_label.height) / 2;
        }
        check.setLocation(4, y_check);
        check.setBounds(4, y_check, d_check.width, d_check.height);
        label.setLocation(d_check.width + 4, y_label);
        label.setBounds(d_check.width + 4, y_label, d_label.width, d_label.height);
    }

    public static final class ListLabel extends JLabel {
        public final Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {
                retDimension = new Dimension(retDimension.width + 10,
                        retDimension.height);
            }
            return retDimension;
        }

    }
}
