package net.wordrider.dialogs;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
@SuppressWarnings({"unchecked"})
public final class CloseDialog<C extends FileInstance> extends AppDialog {
    private final static Logger logger = Logger.getLogger(CloseDialog.class.getName());
    private JButton btnCancel;
    private JButton btnSave;
    private JButton selectAll;
    private JList list;

    //private Frame frame;
    private List<ContentData> listData;
    private final boolean sendFiles;
    private final C active;
    private final JCheckBox checkSort = Swinger.getCheckBox("dialog.close.checkSort");
    private final JCheckBox checkSendWithPictures = Swinger.getCheckBox("dialog.send.checkSendWithPictures", AppPrefs.SEND_WITH_PICTURES, true);

    public CloseDialog(final Frame owner, final Collection<C> list) {
        this(owner, list, null);
    }

    public CloseDialog(final Frame owner, final Collection<C> list, final C active) {
        super(owner, true);
        this.active = active;
        this.sendFiles = active != null;
        //this.frame = owner;
        try {
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        prepareData(list);
        final boolean sort = AppPrefs.getProperty(AppPrefs.LIST_SORT, false);
        checkSort.setSelected(sort);
        reSort(sort);
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setTitle((sendFiles) ? Lng.getLabel("dialog.send.title") : Lng.getLabel("dialog.close.title"));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.list.setSelectedIndex(0);
        Swinger.inputFocus(this.list);
        this.setVisible(true);
    }

    private void prepareData(final Collection<C> list) {
        listData = new ArrayList<ContentData>(list.size());
        boolean checked;
        for (C item : list) {
            checked = !sendFiles || item.equals(active);
            final ContentData contentData = new ContentData(item, checked);
            listData.add(contentData);
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

    protected AbstractButton getOkButton() {
        return btnSave;
    }

    protected AbstractButton getCancelButton() {
        return btnCancel;
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

    private final class ActionButtonsAdapter implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final Object source = e.getSource();
            if (source.equals(btnSave)) {
                setResult(RESULT_OK);                
                AppPrefs.storeProperty(AppPrefs.SEND_WITH_PICTURES, checkSendWithPictures.isSelected());
                AppPrefs.storeProperty(AppPrefs.LIST_SORT, checkSort.isSelected());
                doClose();
            } else if (source.equals(btnCancel)) {
                doClose();
            } else if (source.equals(checkSort)) {
                reSort(checkSort.isSelected());
            }
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
    }

    private final class SelectAction implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            checkAll(e.getSource().equals(selectAll));
            list.repaint();
        }
    }

    private void toggleChecked(final int index) {
        if (index < 0)
            return;
        final ContentData data = (ContentData) list.getModel().getElementAt(index);
        data.checked = !data.checked;
        final Rectangle rect = list.getCellBounds(index, index);
        list.repaint(rect);
        if (sendFiles)
            updateSaveBtnEnabled();
    }

    private void updateSaveBtnEnabled() {
        btnSave.setEnabled(isAnyChecked());
    }

    private boolean isAnyChecked() {
        for (ContentData contentData : listData) {
            if (contentData.checked)
                return true;
        }
        return false;
    }

    private void init() {

        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{f}, new double[]{f, p, p});
        mgr.setVGap(2);

        final Container mainPanel = this.getContentPane();
        mainPanel.setLayout(mgr);
        final JPanel btnPanel = new JPanel(new BorderLayout());
        final JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(checkSort, BorderLayout.WEST);
        if (sendFiles)
            optionsPanel.add(checkSendWithPictures, BorderLayout.EAST);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(btnPanel.getBorder(), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        final JPanel leftBtnPanel = new JPanel(new EqualsLayout(EqualsLayout.LEFT, 5));
        final JPanel rightBtnPanel = new JPanel(new EqualsLayout(EqualsLayout.RIGHT, 5));
        btnPanel.add(leftBtnPanel, BorderLayout.WEST);
        btnPanel.add(rightBtnPanel, BorderLayout.CENTER);
        final JScrollPane scrollPane;
        mainPanel.add(scrollPane = new JScrollPane(list = new JList()), new CustomLayoutConstraints(0, 0));
        mainPanel.add(optionsPanel, new CustomLayoutConstraints(0, 1));

        list.addKeyListener(new MyKeyAdapter());
        list.setCellRenderer(new CheckListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(new EmptyBorder(2, 4, 0, 0));
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                toggleChecked(list.locationToIndex(e.getPoint()));
            }
        });
        mainPanel.add(btnPanel, new CustomLayoutConstraints(0, 2));
        final Insets insets = btnPanel.getBorder().getBorderInsets(btnPanel);
        final Dimension buttonSize = new Dimension(80, 25);
        final ActionListener selectAction = new SelectAction();
        selectAll = Swinger.getButton("dialog.close.btnSelectAll");
        selectAll.addActionListener(selectAction);
        final JButton diselectAll = Swinger.getButton("dialog.close.btnNone");
        diselectAll.addActionListener(selectAction);
        btnCancel = Swinger.getButton("dialog.close.btnCancel");
        btnCancel.setMinimumSize(buttonSize);
        btnSave = Swinger.getButton(sendFiles ? "dialog.send.btnSend" : "dialog.close.btnOK");
        btnSave.setMinimumSize(buttonSize);

        final ActionListener actionButtonListener = new ActionButtonsAdapter();
        btnSave.addActionListener(actionButtonListener);
        btnCancel.addActionListener(actionButtonListener);
        checkSort.addActionListener(actionButtonListener);
        leftBtnPanel.add(selectAll);
        leftBtnPanel.add(diselectAll);
        rightBtnPanel.add(btnSave);
        rightBtnPanel.add(btnCancel);
        btnPanel.setPreferredSize(new Dimension(selectAll.getPreferredSize().width + diselectAll.getPreferredSize().width + btnCancel.getPreferredSize().width * 2 + 70, buttonSize.height + insets.top + insets.bottom + 5));
        scrollPane.setPreferredSize(new Dimension(360, 150));
        this.pack();
    }


    private class NameComparator implements Comparator<ContentData> {

        public int compare(ContentData o1, ContentData o2) {
            return o1.item.getName().toLowerCase().compareTo(o2.item.getName().toLowerCase());
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

}
