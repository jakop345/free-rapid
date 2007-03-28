package net.wordrider.core.managers;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.interfaces.*;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class StatusbarManager implements IRiderManager, CaretListener, IAreaChangeListener, PropertyChangeListener, IHidAble, InstanceListener {

    private final JPanel statusPanel;
    private static final String LINE_PROPERTY = "linePosition";

    private static final String OVERTYPE_PROPERTY = "overtype";

    private JTextComponent editor;
    private final Map<String, PanelItem> subSections = new LinkedHashMap<String, PanelItem>(9);
    private static final Font font = new Font("SansSerif", Font.PLAIN, 11);
    private final static Logger logger = Logger.getLogger(StatusbarManager.class.getName());

    public StatusbarManager() {
        statusPanel = new JPanel();
        statusPanel.setSize(new Dimension(10, 16));
        statusPanel.setPreferredSize(new Dimension(10, 16));
        statusPanel.setVisible(AppPrefs.getProperty(AppPrefs.SHOW_STATUSBAR, true));
        init();
    }

    private void init() {
        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{4, 300, 70, 70, f, 60, 60, f, 35, 30, 10}, new double[]{p});
        mgr.setHGap(4);
        mgr.setVGap(2);
        statusPanel.setLayout(mgr);

        addSection("fileName", "", "");
        addSection("line", Lng.getLabel("statusbar.linenumber"), Lng.getLabel("statusbar.hint.linePosition"));
        addSection("column", Lng.getLabel("statusbar.column"), Lng.getLabel("statusbar.hint.columnPosition"));
        addSeparator("Separator1");
        addSection(OVERTYPE_PROPERTY, "", Lng.getLabel("statusbar.hint.overtype"));
        addSection("modified", "", Lng.getLabel("statusbar.hint.modified"));
        addSeparator("Separator2");
        addSection("capslock", "", Lng.getLabel("statusbar.hint.capslock"));
        addSection("numlock", "", Lng.getLabel("statusbar.hint.numlock"));
    }

    private void addSeparator(final String sectionName) {
        addSection(sectionName, "", null);
        //repaintStatusPanel();
    }

    private void addSection(final String sectionName, final String staticLabel, final String hint) {
//        removeSection(sectionName);
        final PanelItem item = new PanelItem(staticLabel, hint, false);
        subSections.put(sectionName, item);
        item.getComponent().setVisible(false);
        this.statusPanel.add(item.getComponent(), new CustomLayoutConstraints(subSections.size(), 0));
        //repaintStatusPanel();
    }

    private void display(final String sectionName, final String value) {
        final PanelItem item = subSections.get(sectionName);
        item.setOptionalLabel(value);
    }

    private void display(final String sectionName, final String value, final String hint) {
        final PanelItem item = subSections.get(sectionName);
        item.setOptionalLabel(value, hint);
    }


    private void clear(final boolean enable) {
        final Collection<PanelItem> panelItems = subSections.values();
        for (PanelItem panelItem : panelItems) {
            panelItem.setVisible(enable);
        }
    }


    private void displayFilePath(final IFileInstance instance) {
        String value = (!instance.hasAssignedFile()) ? Lng.getLabel("statusbar.notsaved") : instance.getFile().getAbsolutePath();
        display("fileName", Utils.shortenFileName(value, 50), value);
    }

    private void displayOvertype(final boolean value) {
        display(OVERTYPE_PROPERTY, (value) ? Lng.getLabel("statusbar.rewrite") : Lng.getLabel("statusbar.insert"));
    }

    public void instanceModifiedStatusChanged(InstanceEvent e) {
        displayModified(e.getInstance().isModified());
    }

    public void fileAssigned(InstanceEvent e) {
        displayFilePath(e.getInstance());
    }

    private static final class PanelItem {
        private String staticLabel = "";
        private String optionalLabel = "";
        private Component component;
        private boolean separator = false;

        private PanelItem() {
            super();
        }

        public PanelItem(final String staticLabel, final String hint, final boolean isSeparator) {
            this();
            this.separator = isSeparator;
            this.staticLabel = staticLabel;
            final JLabel label = new JLabel(getComposedText());
            label.setToolTipText(hint);
            this.component = label;
            label.setFont(font);
        }

        public final Component getComponent() {
            return component;
        }

        public final String getStaticLabel() {
            return staticLabel;
        }

        public final String getOptionalLabel() {
            return optionalLabel;
        }

        public final boolean isSeparator() {
            return separator;
        }

        public final void setOptionalLabel(final String optionalLabel, final String hint) {
            this.optionalLabel = optionalLabel;
            final JLabel label = (JLabel) component;
            label.setText(getComposedText());
            label.setToolTipText(hint);

        }

        public final void setOptionalLabel(final String optionalLabel) {
            this.optionalLabel = optionalLabel;
            ((JLabel) component).setText(getComposedText());
        }

        private String getComposedText() {
            return this.getStaticLabel() + " " + this.getOptionalLabel();
        }

        public void setVisible(boolean visible) {
            this.component.setVisible(visible);
        }
    }

    public final Component getManagerComponent() {
        return statusPanel;
    }

    private void setCaretColumn(final int caretPosition) {
        int currentColumn = 0;
        if (caretPosition != 0) {
            try {
                currentColumn = caretPosition - Utilities.getRowStart(editor, caretPosition);
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
            }
        }
        display("column", String.valueOf(currentColumn + 1));
    }

    public final void caretUpdate(final CaretEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (e.getSource().equals(editor))
                    //setCaretColumn(e.getDot());
                    setCaretColumn(editor.getCaretPosition());
            }
        });
    }

    private void displayLineNumber(final String value) {
        display("line", value);
    }

    private void displayModified(final boolean value) {
        display("modified", (value) ? Lng.getLabel("statusbar.modified") : "");
    }

    public final void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(LINE_PROPERTY))
            displayLineNumber(evt.getNewValue().toString());

        else if (evt.getPropertyName().equals(OVERTYPE_PROPERTY))
            displayOvertype((Boolean) evt.getNewValue());
    }

    public final void specialKeyStatusChanged() {
        display("capslock", (getKeyStatus(KeyEvent.VK_CAPS_LOCK) ? Lng.getLabel("statusbar.caps") : ""));
        display("numlock", (getKeyStatus(KeyEvent.VK_NUM_LOCK) ? Lng.getLabel("statusbar.num") : ""));
    }

    private static boolean getKeyStatus(final int key) {
        try {
            return Toolkit.getDefaultToolkit().getLockingKeyState(key);
        } catch (UnsupportedOperationException e) { //MacOS or other systems
            return false;
        }
    }


    public void areaActivated(AreaChangeEvent event) {
        if (!isVisible())
            return;
        if (((AreaManager) event.getSource()).getOpenedInstanceCount() == 1)
            clear(true);
        final IFileInstance instance = event.getFileInstance();
        displayFilePath(instance);

        instance.addInstanceListener(this);
        editor = instance.getRiderArea();
        editor.addCaretListener(this);
        editor.addPropertyChangeListener(LINE_PROPERTY, this);
        editor.addPropertyChangeListener(OVERTYPE_PROPERTY, this);
        //System.out.println(instance.getName());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (editor != null)
                    setCaretColumn(editor.getCaretPosition());
            }
        }
        );
        displayLineNumber("1");
        displayModified(instance.isModified());
        displayOvertype((Boolean) editor.getClientProperty(OVERTYPE_PROPERTY));

        specialKeyStatusChanged();
    }

    public void areaDeactivated(AreaChangeEvent event) {
        if (((AreaManager) event.getSource()).getOpenedInstanceCount() == 1)
            clear(false);
        event.getFileInstance().removeInstanceListener(this);
        assert editor != null;

        editor.removeCaretListener(this);
        editor.removePropertyChangeListener(LINE_PROPERTY, this);
        editor.removePropertyChangeListener(OVERTYPE_PROPERTY, this);
        editor = null;

    }


    public final boolean isVisible() {
        return statusPanel.isVisible();
    }

    public final void setVisible(final boolean value) {
        statusPanel.setVisible(value);
        final AreaManager areaManager = AreaManager.getInstance();
        final FileInstance activeInstance = areaManager.getActiveInstance();
        if (value) {
            if (activeInstance != null)
                areaDeactivated(new AreaChangeEvent(areaManager, activeInstance));
        } else {
            areaDeactivated(new AreaChangeEvent(areaManager, activeInstance));
        }

        AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, value);
    }
}
