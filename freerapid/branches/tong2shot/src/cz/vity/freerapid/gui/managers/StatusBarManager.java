package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.BoundedRangeAdapter;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.model.bean.DownloadFile;
import cz.vity.freerapid.model.bean.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.swing.binding.BindUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Sprava a vytvoreni Statusbaru
 *
 * @author Vity
 */
public class StatusBarManager implements PropertyChangeListener, ListDataListener, ListSelectionListener {
    private JXStatusBar statusbar;
    private JLabel infoLabel;
    private final ManagerDirector director;
    private final ApplicationContext context;
    private JProgressBar progress;
    private MainApp app;
    private ResourceMap resourceMap;

    private DataManager dataManager;
    private Image defaultIconImage;
    private Image downloadingIconImage;
    private TrayIconSupport trayIconSupport;

    //private MemoryIndicator indicator;
    private PropertyChangeListener taskPCL;

    private Task activeTask = null;
    private JSlider slider;
    private static final int BAR_HEIGHT = 22;
    private JSpinner spinnerMaxConcurrentDownloads;
    private JSpinner spinnerPluginMaxDownloads;

    /**
     * Konstruktor
     *
     * @param director spravce manazeru
     * @param context  aplikacni kontext
     */
    public StatusBarManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        resourceMap = context.getResourceMap();
        dataManager = director.getDataManager();
        app = (MainApp) context.getApplication();
    }


    public JXStatusBar getStatusBar() {
        if (statusbar == null) {
            statusbar = new JXStatusBar();

            trayIconSupport = app.getTrayIconSupport();
            defaultIconImage = (Utils.isWindows()) ? resourceMap.getImageIcon("trayIconImageWin").getImage() : resourceMap.getImageIcon("trayIconImage").getImage();
            downloadingIconImage = resourceMap.getImageIcon("downloadingIconImage").getImage();

            final Action action = context.getActionMap().get("showStatusBar");
            final ValueModel valueModel = BindUtils.getPrefsValueModel(UserProp.SHOW_STATUSBAR, UserProp.SHOW_STATUSBAR_DEFAULT);
            action.putValue(Action.SELECTED_KEY, valueModel.getValue());
            PropertyConnector.connectAndUpdate(valueModel, getStatusBar(), "visible");


            JLabel clipboardMonitoring = new JLabel();
            clipboardMonitoring.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AppPrefs.negateProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT);
                }
            });
            JLabel quietMode = new JLabel();
            quietMode.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AppPrefs.negateProperty(UserProp.QUIET_MODE_ENABLED, UserProp.QUIET_MODE_ENABLED_DEFAULT);
                }
            });

            taskPCL = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if ("progress".equals(e.getPropertyName())) {
                        progress.setIndeterminate(false);
                        progress.setValue((Integer) e.getNewValue());
                    } else if ("message".equals(e.getPropertyName())) {
                        progress.setIndeterminate(true);
                        progress.setStringPainted(true);
                        final String s = (String) e.getNewValue();
                        progress.setString(s);
                        progress.setToolTipText(s);
                    }
                }
            };


            clipboardMonitoring.setName("labelClipboardMonitoring");
            resourceMap.injectComponent(clipboardMonitoring);
            quietMode.setName("labelQuietMode");
            resourceMap.injectComponent(quietMode);


            statusbar.setName("statusbarPanel");
            infoLabel = new JLabel();
            progress = new JProgressBar();

            spinnerMaxConcurrentDownloads = new JSpinner();
            ValueModel maxConcurrentDownloadsAdapter = BindUtils.getReadOnlyPrefsValueModel(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
            bind(spinnerMaxConcurrentDownloads, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, 1, 1000000, 1, maxConcurrentDownloadsAdapter);
            spinnerMaxConcurrentDownloads.setToolTipText("Max concurrent downloads at a time"); //TODO: internationalize

            spinnerPluginMaxDownloads = new JSpinner();
            spinnerPluginMaxDownloads.setToolTipText("Plugin's max allowed downloads");

            //  progress.setStringPainted(false);
            //indicator = new MemoryIndicator();
            //indicator.setPreferredSize(new Dimension(100, BAR_HEIGHT));
            infoLabel.setPreferredSize(new Dimension(420, BAR_HEIGHT));
            clipboardMonitoring.setPreferredSize(new Dimension(17, BAR_HEIGHT));
            quietMode.setPreferredSize(new Dimension(17, BAR_HEIGHT));
            spinnerMaxConcurrentDownloads.setPreferredSize(new Dimension(56, BAR_HEIGHT));
            spinnerPluginMaxDownloads.setPreferredSize(new Dimension(56, BAR_HEIGHT));
            spinnerPluginMaxDownloads.setEnabled(false);
            spinnerPluginMaxDownloads.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    director.getPluginsManager().updatePluginSettings();
                }
            });
            progress.setPreferredSize(new Dimension(progress.getPreferredSize().width + 35, BAR_HEIGHT));
            progress.setVisible(false);
            director.getMenuManager().getMenuBar().addPropertyChangeListener("selectedText", this);
            statusbar.add(infoLabel, JXStatusBar.Constraint.ResizeBehavior.FIXED);

            final JPanel speedBarPanel = new JPanel();
            speedBarPanel.setLayout(new BoxLayout(speedBarPanel, BoxLayout.LINE_AXIS));

            initSpeedBar(speedBarPanel, resourceMap);

            PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT), clipboardMonitoring, "enabled");
            PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.QUIET_MODE_ENABLED, UserProp.QUIET_MODE_ENABLED_DEFAULT), quietMode, "enabled");

            statusbar.add(clipboardMonitoring, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(quietMode, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(spinnerMaxConcurrentDownloads, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(spinnerPluginMaxDownloads, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(speedBarPanel, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(progress, JXStatusBar.Constraint.ResizeBehavior.FIXED);

            statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);

            context.getTaskMonitor().addPropertyChangeListener(this);

            dataManager.getProcessManager().addPropertyChangeListener("downloading", this);

            director.getSpeedRegulator().addPropertyChangeListener("speed", this);

            dataManager.addPropertyChangeListener("completed", this);
            dataManager.addPropertyChangeListener("state", this);

            director.getContentManager().getContentPanel().addListSelectionListener(this);

            AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
                public void preferenceChange(final PreferenceChangeEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            checkPropertyChange(evt);
                        }
                    });
                }
            });
            //final ContentPanel speedBarPanel = director.getDockingManager().getContentPanel();
            updateInfoStatus();
            //updateMemoryIndicator();
        }
        return statusbar;
    }

    private void initSpeedBar(JPanel panel, ResourceMap resourceMap) {
        final JXCollapsiblePane cp = new JXCollapsiblePane();
        cp.setDirection(JXCollapsiblePane.Direction.RIGHT);
        cp.setBorder(null);
        cp.setAnimated(true);

        cp.setPreferredSize(new Dimension(160, BAR_HEIGHT));
        //cp.setSize(80, 16);

        cp.setBorder(null);


        final JLabel labelSpeedBtn = new JLabel();
        labelSpeedBtn.setName("labelSpeedBtn");
        resourceMap.injectComponent(labelSpeedBtn);
        labelSpeedBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // cp.setCollapsed(!cp.isCollapsed());
                AppPrefs.negateProperty(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
            }
        });

        panel.add(labelSpeedBtn);
        panel.add(cp);

        labelSpeedBtn.setBackground(null);

        final ValueModel valueModel = BindUtils.getPrefsValueModel(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
        PropertyConnector.connectAndUpdate(ConverterFactory.createBooleanNegator(valueModel), cp, "collapsed");

        final ValueModel speedAdapter = BindUtils.getReadOnlyPrefsValueModel(UserProp.SPEED_LIMIT, UserProp.SPEED_LIMIT_DEFAULT);
        slider = new JSlider(new BoundedRangeAdapter(speedAdapter, 0, 0, Integer.MAX_VALUE));
        slider.setName("speedSlider");
        slider.setPreferredSize(new Dimension(100, BAR_HEIGHT));
        slider.setMaximumSize(new Dimension(100, BAR_HEIGHT));
        slider.setSize(45, 12);
        final Font font = slider.getFont().deriveFont(6F);
        slider.setFont(font);

        slider.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        slider.setSnapToTicks(true);

        bindSpeedSlider(slider);

        final JLabel labelSpeed = new JLabel();
        labelSpeed.setName("labelSpeed");
        labelSpeed.setLabelFor(slider);
        Bindings.bind(labelSpeed, ConverterFactory.createStringConverter(speedAdapter, NumberFormat.getIntegerInstance()));
        labelSpeed.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        labelSpeed.setPreferredSize(new Dimension(32, BAR_HEIGHT));
        labelSpeed.setBackground(Color.green);


        final JLabel labelSpeedUnit = new JLabel();
        labelSpeedUnit.setLabelFor(slider);
        labelSpeedUnit.setName("labelSpeedUnit");


        PropertyConnector.connectAndUpdate(valueModel, slider, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, labelSpeed, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, labelSpeedUnit, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, labelSpeedBtn, "enabled");

        CellConstraints cc = new CellConstraints();
        PanelBuilder panelBuilder = new PanelBuilder(new FormLayout(new ColumnSpec[]{FormSpecs.PREF_COLSPEC, FormSpecs.PREF_COLSPEC, FormSpecs.PREF_COLSPEC}, new RowSpec[]{FormSpecs.PREF_ROWSPEC}), cp);

        panelBuilder.add(slider, cc.xy(1, 1));
        panelBuilder.add(labelSpeed, cc.xy(2, 1));
        panelBuilder.add(labelSpeedUnit, cc.xy(3, 1));

    }

    private void checkPropertyChange(PreferenceChangeEvent evt) {
        final String key = evt.getKey();
        if (UserProp.SHOWINFO_IN_TITLE.equals(key)) {
            updateInfoStatus();
        } else if (UserProp.ANIMATE_ICON.equals(key)) {
            if (!AppPrefs.getProperty(UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT))
                trayIconSupport.setImage(defaultIconImage);
            else
                updateIconAnimation();
        } else if (UserProp.SHOW_MEMORY_INDICATOR.equals(key)) {
            //updateMemoryIndicator();
        } else if (UserProp.SPEED_LIMIT_ENABLED.equals(key)) {
            if (Boolean.TRUE.equals(Boolean.valueOf(evt.getNewValue())))
                Swinger.inputFocus(slider);
        } else if (UserProp.GLOBAL_SPEED_SLIDER_MAX.equals(key) || UserProp.GLOBAL_SPEED_SLIDER_MAX.equals(key) || UserProp.GLOBAL_SPEED_SLIDER_STEP.equals(key)) {
            bindSpeedSlider(slider);
        } else if (UserProp.MAX_DOWNLOADS_AT_A_TIME.equals(key)) {
            spinnerMaxConcurrentDownloads.setValue(AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT));
        }
    }

//    private void updateMemoryIndicator() {
//        final boolean memoryIndicator = AppPrefs.getProperty(UserProp.SHOW_MEMORY_INDICATOR, UserProp.SHOW_MEMORY_INDICATOR_DEFAULT);
//        indicator.setVisible(memoryIndicator);
//        if (memoryIndicator)
//            statusbar.add(indicator, JXStatusBar.Constraint.ResizeBehavior.FIXED);
//        else
//            statusbar.remove(indicator);
//    }

//    private void setStatusBarVisible(boolean visible) {
//        getStatusBar().setVisible(visible);
//    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("speed".equals(propertyName) || "completed".equals(propertyName)) {
            updateInfoStatus();
        } else if ("started".equals(propertyName) || "done".equals(propertyName) || "message".equals(propertyName)) {
            //final Task task = (Task) evt.getSource();
            if (!(evt.getSource() instanceof DownloadTask))
                updateProgress(evt);
        } else if ("selectedText".equals(propertyName)) {
            final String s = (String) evt.getNewValue();
            if ("cancel".equals(s)) {
                updateInfoStatus();
            } else
                infoLabel.setText(s);
        } else if ("downloading".equals(propertyName)) {
            if (AppPrefs.getProperty(UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT))
                updateIconAnimation();
        }
    }

    private void updateIconAnimation() {
        final int downloading = dataManager.getDownloading();
        if (downloading == 0) {
            trayIconSupport.setImage(defaultIconImage);
        } else {
            trayIconSupport.setImage(downloadingIconImage);
        }
    }

    private void updateProgress(PropertyChangeEvent evt) {
        final Task task = (Task) evt.getSource();
        final String propertyName = evt.getPropertyName();
        if ("done".equals(propertyName)) {
            progress.setVisible(false);
            task.removePropertyChangeListener(taskPCL);
            activeTask = null;
        } else if ("started".equals(propertyName)) {
            if (activeTask != null)
                task.removePropertyChangeListener(taskPCL);
            activeTask = task;
            progress.setStringPainted(false);
            progress.setVisible(true);
            progress.setToolTipText(null);
            progress.setIndeterminate(!task.isProgressPropertyValid());
            task.addPropertyChangeListener(taskPCL);
        }
    }


    public void updateInfoStatus() {
        final int completed = dataManager.getCompleted();
        final int size = dataManager.getDownloadFiles().size();
        final long speed = director.getSpeedRegulator().getSpeed();
        final TrayIconSupport trayIconSupport = app.getTrayIconSupport();
        final boolean showInFrameTitle = AppPrefs.getProperty(UserProp.SHOWINFO_IN_TITLE, UserProp.SHOWINFO_IN_TITLE_DEFAULT);
        final String speedFormatted = ContentPanel.bytesToAnother(speed);
        int downloading = dataManager.getDownloading();
        if (showInFrameTitle) {
            final String s;
            if (downloading == 0) {
                s = resourceMap.getString("frameTitleInfoNoDownloads", completed, size);
            } else {
                if (speed == 0) {
                    s = resourceMap.getString("frameTitleInfo0Speed", completed, size, speedFormatted);
                } else
                    s = resourceMap.getString("frameTitleInfo", completed, size, speedFormatted);
            }

            app.getMainFrame().setTitle(s);
        } else {
            app.getMainFrame().setTitle(resourceMap.getString("Application.title"));
        }

        if (size >= 0) {
            trayIconSupport.setToolTip(resourceMap.getString("tooltipTrayInfo", completed, size, speedFormatted));
            infoLabel.setText(resourceMap.getString("statusBarInfo", completed, size, speedFormatted));
        } else {
            trayIconSupport.setToolTip(resourceMap.getString("Application.title"));
            infoLabel.setText(resourceMap.getString("statusBarInfoIdle"));
        }

    }

    public void intervalAdded(ListDataEvent e) {
        updateInfoStatus();
    }

    public void intervalRemoved(ListDataEvent e) {
        updateInfoStatus();
    }

    public void contentsChanged(ListDataEvent e) {

    }

    private void bindSpeedSlider(JSlider slider) {
        int minimum = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_MIN, UserProp.GLOBAL_SPEED_SLIDER_MIN_DEFAULT);
        int maximum = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_MAX, UserProp.GLOBAL_SPEED_SLIDER_MAX_DEFAULT);
        int step = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_STEP, UserProp.GLOBAL_SPEED_SLIDER_STEP_DEFAULT);
//        minimum = Math.min(minimum, maximum);
//        maximum = Math.max(minimum, maximum);
//        if (step > maximum - minimum) {
//            step = Math.max(maximum - minimum / 10, 1);
//        }
        slider.setMinimum(minimum);
        slider.setMaximum(maximum);
        slider.setMinorTickSpacing(step);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final int index = director.getContentManager().getContentPanel().getSelectedRows()[0];
                spinnerPluginMaxDownloads.setEnabled(false);
                if (index != -1) {
                    final DownloadFile httpFile = dataManager.getDownloadFiles().get(index);
                    if (httpFile.getState() == DownloadState.COMPLETED || httpFile.getState() == DownloadState.DELETED)
                        return;
                    final String pluginID = httpFile.getPluginID();
                    final PluginMetaData pluginMetaData;
                    try {
                        pluginMetaData = director.getPluginsManager().getPluginMetadata(pluginID);
                    } catch (Exception ex) {
                        return; //direct download
                    }

                    final int max = pluginMetaData.getMaxParallelDownloads();
                    final BeanAdapter<PluginMetaData> beanModel = new BeanAdapter<PluginMetaData>(pluginMetaData, true);
                    bind(spinnerPluginMaxDownloads, 1, 1, max, 1, beanModel.getValueModel("maxAllowedDownloads"));
                    spinnerPluginMaxDownloads.setEnabled(max > 1);
                }
            }
        });
    }

    private void bind(JSpinner spinner, int defaultValue, int minValue, int maxValue, int step, final ValueModel valueModel) {
        spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
                valueModel,
                defaultValue,
                minValue,
                maxValue,
                step));
        final JComponent editor = spinner.getEditor();
        if (editor instanceof JFormattedTextField) {
            final JFormattedTextField field = (JFormattedTextField) editor;
            field.setFocusLostBehavior(JFormattedTextField.COMMIT);
        }
    }

//    private ValueModel bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
//        final ValueModel valueModel = new MyPreferencesAdapter(key, defaultValue);
//        Bindings.bind(checkBox, valueModel);
//        return valueModel;
//    }


}
