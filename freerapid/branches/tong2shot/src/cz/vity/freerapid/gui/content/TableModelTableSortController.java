package cz.vity.freerapid.gui.content;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.table.TableModel;
import java.util.Comparator;

/**
* @author VitasekL
*/
class TableModelTableSortController extends TableSortController<TableModel> {
    private JXTable parentTable;

    public TableModelTableSortController(final JXTable parentTable) {
        super(parentTable.getModel());
        this.parentTable = parentTable;
    }

    @Override
    public Comparator<?> getComparator(int column) {
        return ((TableColumnExt) parentTable.getColumn(parentTable.convertColumnIndexToView(column))).getComparator();
    }

    @Override
    protected boolean useToString(int column) {
        return false;
    }
}
