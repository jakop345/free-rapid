package cz.cvut.felk.timejuggler.core;

import application.Application;
import application.ApplicationContext;
import application.ResourceConverter;
import application.SessionStorage;
import cz.cvut.felk.timejuggler.core.application.GlobalEDTExceptionHandler;
import cz.cvut.felk.timejuggler.core.application.ListItemsConvertor;
import cz.cvut.felk.timejuggler.core.data.DataProvider;
import cz.cvut.felk.timejuggler.gui.MainPanelManager;
import cz.cvut.felk.timejuggler.gui.StorageProperties;
import cz.cvut.felk.timejuggler.swing.LookAndFeels;
import cz.cvut.felk.timejuggler.swing.TrayIconSupport;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

/**
 * Hlavni trida aplikace
 * @author Vity
 */
public class MainApp extends SingleXFrameApplication {

    private MainPanelManager mainPanel;
    private Collection<String> filesToOpen;
    private static boolean debug = false;
    private DataProvider dataProvider;
    private TrayIconSupport trayIconSupport = null;

//    private static Logger logger = null;

    /**
     * Zpracuje parametry pri spusteni programu
     * @param args vstupni parametry
     * @return kolekce jmen souboru pro nacteni v programu
     */
    private Collection<String> processArguments(final String[] args) {
        Collection<String> result = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.equals("-d") || arg.equals("--debug")) {
                    debug = true;
                } else if (arg.equals("-h") || arg.endsWith("--help")) {
                    showHelp();
                } else if (arg.equals("-v") || arg.equals("--version")) {
                    showVersion();
                }
            } else {
                if (result == null)
                    result = new LinkedList<String>();
                result.add(arg);
            }
        }
        return result;
    }

    private void showVersion() {
        System.out.println(Consts.APPVERSION);
        System.out.println("Authors (c) 2007: Jan Struz, Ladislav Vitasek, Jiri Holy, Jan Zikan");
        this.exit();
    }


    private void showHelp() {
        System.out.println("Usage: timejuggler [-options] [file(s)]");
        System.out.println("commands: -h, --help  - to view this message");
        System.out.println("          -v, --version - display version information and exit");
        System.out.println("          -d, --debug - enable debug log level\n");
        System.out.println("           file(s) - path to file(s) for opening");
        System.out.println("min. Java version required : 1.6");
        System.out.println("See the readme.txt for more information.\n");
        this.exit();
    }

    @Override
    protected void initialize(String[] args) {
        filesToOpen = processArguments(args);
        LogUtils.initLogging(debug);//logovani nejdrive
        if (OneInstanceClient.checkInstance(filesToOpen)) {
            this.exit();
            return;
        }
        this.dataProvider = new DataProvider();
        this.dataProvider.init();
        // logger = Logger.getLogger(MainApp.class.getName());
        LookAndFeels.getInstance().loadLookAndFeelSettings();//inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        //Swinger.initLaF(); //inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        super.initialize(args);
        ResourceConverter.register(new ListItemsConvertor());
    }

    @Override
    protected void startup() {
        mainPanel = new MainPanelManager(getContext());
        initMainFrame();
        this.addExitListener(new MainAppExitListener());
        show(getMainFrame());
        getTrayIconSupport().setVisibleByDefault();
        setGlobalEDTExceptionHandler();
    }

    private void initMainFrame() {
        final JFrame frame = getMainFrame();
        frame.setJMenuBar(getMainPanel().getMenuManager().getMenuBar());
        frame.getContentPane().add(getMainPanelComponent());
        frame.pack();
    }


    private void setGlobalEDTExceptionHandler() {
        final GlobalEDTExceptionHandler eh = new GlobalEDTExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(eh);
        Thread.currentThread().setUncaughtExceptionHandler(eh);
    }

    private JComponent getMainPanelComponent() {
        return mainPanel.getComponent();
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    @Override
    protected void injectSessionProperties() {
        super.injectSessionProperties();
        SessionStorage storage = getContext().getSessionStorage();
        storage.putProperty(JXStatusBar.class, new StorageProperties.XStatusBarProperty());
        storage.putProperty(JToolBar.class, new StorageProperties.JToolbarProperty());
        storage.putProperty(JXMultiSplitPane.class, new StorageProperties.XMultipleSplitPaneProperty());
        new StorageProperties().registerPersistenceDelegates();
    }

    /**
     * Vraci komponentu hlavniho panelu obsahujici dalsi komponenty
     * @return hlavni panel
     */
    public MainPanelManager getMainPanel() {
        assert mainPanel != null; //calling getMainPanel before finished initialization
        return mainPanel;
    }

    /**
     * Hlavni spousteci metoda programu
     * @param args vstupni parametry pro program
     */
    public static void main(String[] args) {
        //zde prijde overovani vstupnich pridavnych parametru
        Application.launch(MainApp.class, args); //spusteni
    }

    public static ApplicationContext getAContext() {
        return Application.getInstance(MainApp.class).getContext();
    }

    /**
     * Exit listener. Pri ukoncovani provede ulozeni uzivatelskych properties.
     */
    private static class MainAppExitListener implements Application.ExitListener {

        public boolean canExit(EventObject event) {
            return true;
        }

        public void willExit(EventObject event) {
            AppPrefs.store();
        }
    }

    public Collection<String> getFilesToOpen() {
        return filesToOpen;
    }

    public TrayIconSupport getTrayIconSupport() {
        if (trayIconSupport == null) {
            trayIconSupport = new TrayIconSupport();
        }
        return trayIconSupport;
    }
}
