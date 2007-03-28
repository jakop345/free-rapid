package net.wordrider.core;

import net.wordrider.area.RiderStyles;
import net.wordrider.core.actions.ExitAction;
import net.wordrider.core.actions.OpenFileAction;
import net.wordrider.core.managers.ManagerDirector;
import net.wordrider.core.managers.PluginToolsManager;
import net.wordrider.core.swing.TextComponentContextMenuListener;
import net.wordrider.gui.LookAndFeels;
import net.wordrider.plugintools.BreakpointList;
import net.wordrider.plugintools.CharacterList;
import net.wordrider.plugintools.FindAll;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class MainAppFrame extends JFrame {
    private JPanel rootPanel;
    private ManagerDirector mainPanelManager;
    private CharacterList characterList = null;
    private final static Logger logger = Logger.getLogger(MainAppFrame.class.getName());

    public MainAppFrame() throws HeadlessException {
        super();    //call to super
        //  JFrame.setDefaultLookAndFeelDecorated(true);
        LookAndFeels.getInstance().loadLookAndFeelSettings();
        final Image icon = Swinger.getIconImage("011.png");

        if (icon != null)
            this.setIconImage(icon);

        if (AppPrefs.getProperty(AppPrefs.DECORATED_FRAMES, false)) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            LookAndFeels.updateWindowUI(this);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        final DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        final int defaultWidth = mode.getWidth();
        final int defaultHeight = mode.getHeight();

        final boolean maximized;
        final int width, height;
        if (AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION, false) && !AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION_MAXIMIZED, false)) {
            maximized = false;
            this.setLocation(AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION_X, 0), AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION_Y, 0));
            width = AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION_WIDTH, defaultWidth);
            height = AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION_HEIGHT, defaultHeight);
        } else {
            maximized = true;
            width = defaultWidth;
            height = defaultHeight;
        }

        this.setSize(width, height);
        this.setTitle(Consts.APPVERSION + (logger.isLoggable(Level.INFO) ? " debug" : ""));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getMainPanel());
        if (maximized)
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    protected final JRootPane createRootPane() {
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                mainPanelManager.getStatusbarManager().specialKeyStatusChanged();
            }
        };
        final JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_NUM_LOCK, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_CAPS_LOCK, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    private CharacterList getCharacterList() {
        return (characterList == null) ? characterList = new CharacterList() : characterList;
    }

    public final void init(final Collection<String> openFiles) {
        getManagerDirector();
        boolean bookmarksactive = false;
        boolean openingFile = false;
        if (openFiles != null) {
            bookmarksactive = true;
            openingFile = true;
        } else {
            if (AppPrefs.getProperty(AppPrefs.NEW_FILE_AFTER_START, false)) {
                openingFile = true;
                mainPanelManager.getAreaManager().openFileInstance();
            }
        }
        if (!openingFile)
            mainPanelManager.getDataDividerManager().setGraphicMenu();
        final PluginToolsManager manager = mainPanelManager.getPluginToolsManager();
        manager.addPluginTool(getCharacterList());
        final BreakpointList breakpointList = new BreakpointList();
        manager.addPluginTool(breakpointList);
        manager.addPluginTool(new FindAll());
        if (bookmarksactive)
            manager.selectPluginTool(breakpointList);

        mainPanelManager.getAreaManager().grabActiveFocus();


        this.setVisible(true);

        //        SwingUtilities.invokeLater(new Runnable() {
        //            public void run() {
        final String[] chooser = new String[]{"FileChooser.lookInLabelText", "FileChooser.saveInLabelText", "FileChooser.fileNameLabelText", "FileChooser.filesOfTypeLabelMnemonic", "FileChooser.filesOfTypeLabelText", "FileChooser.upFolderToolTipText", "FileChooser.upFolderAccessibleName", "FileChooser.homeFolderToolTipText", "FileChooser.homeFolderAccessibleName", "FileChooser.newFolderToolTipText", "FileChooser.newFolderAccessibleName", "FileChooser.listViewButtonToolTipText", "FileChooser.listViewButtonAccessibleName", "FileChooser.detailsViewButtonToolTipText", "FileChooser.detailsViewButtonAccessibleName", "FileChooser.fileNameHeaderText", "FileChooser.fileSizeHeaderText", "FileChooser.fileTypeHeaderText", "FileChooser.fileDateHeaderText", "FileChooser.fileAttrHeaderText", "FileChooser.cancelButtonText", "FileChooser.cancelButtonToolTipText", "FileChooser.deleteFileButtonText", "FileChooser.filesLabelText", "FileChooser.foldersLabelText", "FileChooser.newFolderButtonText", "FileChooser.openButtonText", "FileChooser.openButtonToolTipText", "FileChooser.openDialogTitleText", "FileChooser.pathLabelText", "FileChooser.renameFileButtonText", "FileChooser.saveButtonText", "FileChooser.saveButtonToolTipText", "FileChooser.saveDialogTitleText", "ColorChooser.cancelText", "ColorChooser.hsbBlueText", "ColorChooser.hsbBrightnessText", "ColorChooser.hsbDisplayedMnemonicIndex", "ColorChooser.hsbGreenText", "ColorChooser.hsbHueText", "ColorChooser.hsbMnemonic", "ColorChooser.hsbNameText", "ColorChooser.hsbRedText", "ColorChooser.hsbSaturationText", "ColorChooser.okText", "ColorChooser.previewText", "ColorChooser.resetMnemonic", "ColorChooser.resetText", "ColorChooser.rgbBlueText", "ColorChooser.rgbDisplayedMnemonicIndex", "ColorChooser.rgbGreenText", "ColorChooser.rgbMnemonic", "ColorChooser.rgbNameText", "ColorChooser.rgbRedText", "ColorChooser.sampleText", "ColorChooser.swatchesDisplayedMnemonicIndex", "ColorChooser.swatchesMnemonic", "ColorChooser.swatchesNameText", "ColorChooser.swatchesRecentText"};
        String value;
        for (String aChooser : chooser) {
            value = aChooser;
            UIManager.put(value, Lng.getLabel(value));
        }
        //            }
        //        });
        //Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TextComponentContextMenuListener());
        Toolkit.getDefaultToolkit().addAWTEventListener(new TextComponentContextMenuListener(), AWTEvent.MOUSE_EVENT_MASK);
        if (!RiderStyles.isCorrectFont()) {
            Swinger.showErrorDialog(this, Lng.getLabel("message.error.fontloadfailed", new Object[]{RiderStyles.FONT_NAME, RiderStyles.FONT_FAMILY}));
        }
        if (openFiles != null)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (String openFile : openFiles) {
                        OpenFileAction.open(new File(openFile));
                    }
                }
            });

    }


    private JPanel getMainPanel() {
        if (rootPanel == null) {
            rootPanel = new JPanel();
            rootPanel.setLayout(new BorderLayout());
            //create panels
        }
        return rootPanel;
    }

    public final ManagerDirector getManagerDirector() {
        if (mainPanelManager == null) {
            mainPanelManager = new ManagerDirector(this, rootPanel);
        }
        return mainPanelManager;
    }


    protected final void processWindowEvent(final WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            ExitAction.getInstance().actionPerformed(null);//check for opened files
            //System.exit(0);
        }
    }


}
