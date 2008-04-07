package cz.cvut.felk.erm.swing.renderers;

import cz.cvut.felk.erm.swing.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public class ColorTableCellRenderer extends JLabel implements TableCellRenderer {
    private final String noneText;

    public ColorTableCellRenderer() {
        super();
        this.setOpaque(true);
        noneText = Swinger.getResourceMap().getString("tableColorNone");
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Color fg = null;

        Color newColor = (Color) value;
        if (newColor == null) {
            this.setHorizontalAlignment(CENTER);
            setText(noneText);
        } else setText("");

        Color bg = newColor;

        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column) {

            fg = UIManager.getColor("Table.dropCellForeground");
            bg = UIManager.getColor("Table.dropCellBackground");

            isSelected = true;
        }

        if (isSelected) {
            super.setForeground(fg == null ? table.getSelectionForeground()
                    : fg);
//            super.setBackground(bg == null ? table.getSelectionBackground()
//                    : bg);
        }

        if (bg == null) {
            bg = table.getBackground();
        }
        this.setBackground(bg);
        setFont(table.getFont());

        if (hasFocus) {
            Border border = null;
            if (isSelected) {
                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            setBorder(border);

            if (!isSelected && table.isCellEditable(row, column)) {
                Color col;
                col = UIManager.getColor("Table.focusCellForeground");
                if (col != null) {
                    super.setForeground(col);
                }
                col = UIManager.getColor("Table.focusCellBackground");
                if (col != null) {
                    //        super.setBackground(col);
                }
            }
        } else setBorder(null);

        return this;

    }
}