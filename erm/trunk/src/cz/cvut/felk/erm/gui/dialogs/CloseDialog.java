package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.gui.MyPresentationModel;
import cz.cvut.felk.erm.gui.managers.FileInstance;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.swing.renderers.CheckRenderer;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
@SuppressWarnings({"unchecked"})
public class CloseDialog<C extends FileInstance> extends AppDialog {
    private final static Logger logger = Logger.getLogger(CloseDialog.class.getName());

    private JList list;
    private JButton btnOk;
    private JButton btnCancel;

    private MyPresentationModel model;
    private JCheckBox checkSort;
    private JButton btnSelectAll;
    private JButton btnSelectNone;
    private java.util.List<ContentData> listData;
    private final C active;
    private boolean anyChecked;
    private static final String OK_BTN_ENABLED_PROPERTY = "btnEnabledProperty";

    public CloseDialog(final Frame owner, final Collection<C> list) throws Exception {
        this(owner, list, null);
    }

    public CloseDialog(Frame owner, Collection<C> list, final C active) throws Exception {
        super(owner, true);
        this.active = active;

        this.setName("CloseDialog");
        try {
            initComponents();
            prepareData(list);
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
        }
    }

    private void prepareData(final Collection<C> list) {
        listData = new ArrayList<ContentData>(list.size());
        boolean checked;
        for (C item : list) {
            checked = item.equals(active);
            final ContentData contentData = new ContentData(item, checked);
            listData.add(contentData);
        }
    }

    public boolean isBtnEnabledProperty() {
        return isAnyChecked();
    }

    @Override
    public void doClose() {

        logger.log(Level.FINE, "Closing CloseDialog");
        try {
            if (model != null) {
                model.triggerCommit();
                model.release();
            }

        } finally {
            super.doClose();
        }
    }


    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOk;
    }

    private void build() {
        inject();
        buildGUI();
        buildModels();

        final ActionMap actionMap = getActionMap();

        btnOk.setAction(actionMap.get("okBtnAction"));
        btnCancel.setAction(actionMap.get("cancelBtnAction"));
        btnSelectAll.setAction(actionMap.get("btnSelectAllAction"));
        btnSelectNone.setAction(actionMap.get("btnSelectNoneAction"));
        checkSort.setAction(actionMap.get("checkSortAction"));
        setDefaultValues();

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
        Swinger.inputFocus(this.list);

    }

    private void setDefaultValues() {
        setBtnEnabledProperty(isAnyChecked());
        bind(checkSort, UserProp.SORT_FILES_CLOSEDIALOG, false);
        checkAll(true);
    }

    private void buildModels() {
        model = new MyPresentationModel(null, new Trigger());

        reSort(AppPrefs.getProperty(UserProp.SORT_FILES_CLOSEDIALOG, false));
        this.list.setSelectedIndex(0);
    }

    private void buildGUI() {
        list.addKeyListener(new MyKeyAdapter());
        list.setCellRenderer(new CheckListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(new EmptyBorder(2, 4, 0, 0));
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                toggleChecked(list.locationToIndex(e.getPoint()));
            }
        });

    }

    @Action(enabledProperty = OK_BTN_ENABLED_PROPERTY)
    public void okBtnAction() {

        doClose();
    }

    @Action
    public void cancelBtnAction() {
        doClose();
    }

    @Action
    public void btnSelectAllAction() {
        checkAll(true);
        list.repaint();
    }

    @Action
    public void btnSelectNoneAction() {
        checkAll(false);
        list.repaint();
    }

    @Action
    public void checkSortAction() {
        reSort(checkSort.isSelected());
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }


    private void bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        Bindings.bind(checkBox, model.getBufferedPreferences(key, defaultValue));
    }


    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        list = new JList();
        checkSort = new JCheckBox();
        JPanel buttonBar = new JPanel();
        btnSelectAll = new JButton();
        btnSelectNone = new JButton();
        btnOk = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== CloseDialog ========
        {

            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setLayout(new BorderLayout());

                //======== contentPanel ========
                {
                    contentPanel.setLayout(new FormLayout(
                            new ColumnSpec[]{
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(121), FormSpec.DEFAULT_GROW)
                            },
                            new RowSpec[]{
                                    new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormFactory.LINE_GAP_ROWSPEC,
                                    FormFactory.DEFAULT_ROWSPEC
                            }));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(list);
                    }
                    contentPanel.add(scrollPane1, cc.xywh(1, 1, 3, 1));

                    //---- checkSort ----
                    checkSort.setName("checkSort");
                    contentPanel.add(checkSort, cc.xywh(1, 3, 3, 1));
                }
                dialogPane.add(contentPanel, BorderLayout.CENTER);

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setLayout(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC,
                                    new ColumnSpec("max(min;30dlu):grow"),
                                    FormFactory.BUTTON_COLSPEC,
                                    FormFactory.RELATED_GAP_COLSPEC,
                                    FormFactory.BUTTON_COLSPEC
                            },
                            RowSpec.decodeSpecs("pref")));
                    ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}, {6, 8}});

                    //---- btnSelectAll ----
                    btnSelectAll.setName("btnSelectAll");
                    buttonBar.add(btnSelectAll, cc.xy(2, 1));

                    //---- btnSelectNone ----
                    btnSelectNone.setName("btnSelectNone");
                    buttonBar.add(btnSelectNone, cc.xy(4, 1));

                    //---- btnOk ----
                    btnOk.setName("btnOk");
                    buttonBar.add(btnOk, cc.xy(6, 1));

                    //---- btnCancel ----
                    btnCancel.setName("btnCancel");
                    buttonBar.add(btnCancel, cc.xy(8, 1));
                }
                dialogPane.add(buttonBar, BorderLayout.SOUTH);
            }
            contentPane.add(dialogPane, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(this.getOwner());
        }
    }


    private final class CheckListRenderer extends CheckRenderer {
        public final Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final ContentData contentData = ((ContentData) value);
            check.setSelected(contentData.checked);
            final File file = contentData.item.getFile();
            if (file != null)
                this.setToolTipText(file.getPath());
            return super.getListCellRendererComponent(list, contentData.item, index, isSelected, cellHasFocus, contentData.item.getIcon());    //call to super
        }

    }


    private final class MyKeyAdapter extends KeyAdapter {
        public final void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                toggleChecked(list.getSelectedIndex());
            }
        }
    }

    private void checkAll(final boolean value) {
        for (ContentData contentData : listData) {
            contentData.checked = value;
        }
        setBtnEnabledProperty(isAnyChecked());
    }

    public void setBtnEnabledProperty(final boolean newValue) {
        final boolean oldValue = anyChecked;
        this.anyChecked = newValue;
        firePropertyChange(OK_BTN_ENABLED_PROPERTY, oldValue, newValue);
    }


    private void toggleChecked(final int index) {
        if (index < 0)
            return;
        final ContentData data = (ContentData) list.getModel().getElementAt(index);
        data.checked = !data.checked;
        final Rectangle rect = list.getCellBounds(index, index);
        list.repaint(rect);
        setBtnEnabledProperty(isAnyChecked());
    }


    private boolean isAnyChecked() {
        for (ContentData contentData : listData) {
            if (contentData.checked)
                return true;
        }
        return false;
    }

    private class NameComparator implements Comparator<ContentData> {

        public int compare(ContentData o1, ContentData o2) {
            return o1.item.getName().toLowerCase().compareTo(o2.item.getName().toLowerCase());
        }

    }

    private class ContentData implements Comparable {
        private C item;
        private boolean checked;

        public ContentData(C item, boolean checked) {
            this.item = item;
            this.checked = checked;
        }

        public int compareTo(Object o) {
            return item.compareTo(((ContentData) o).item);
        }
    }

    private void reSort(final boolean sort) {
        final Object[] objects;
        final TreeSet<ContentData> treeSet;
        if (sort) {
            treeSet = new TreeSet<ContentData>(new NameComparator());
            treeSet.addAll(listData);
        } else {
            treeSet = new TreeSet<ContentData>(listData);
        }
        objects = treeSet.toArray();
        final AbstractListModel listModel = new AbstractListModel() {
            public int getSize() {
                return objects.length;
            }

            public Object getElementAt(int index) {
                return objects[index];
            }
        };
        list.setModel(listModel);
    }

    public final Collection<C> getReturnList() {
        final Collection<C> resultList = new ArrayList<C>();
        if (result == RESULT_CANCEL)
            return resultList;
        for (ContentData contentData : listData) {
            if (contentData.checked)
                resultList.add(contentData.item);
        }
        return resultList;
    }
}
