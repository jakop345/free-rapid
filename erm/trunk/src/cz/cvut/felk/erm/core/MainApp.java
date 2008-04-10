package cz.cvut.felk.erm.core;

import cz.cvut.felk.erm.core.application.GlobalEDTExceptionHandler;
import cz.cvut.felk.erm.core.application.ListItemsConvertor;
import cz.cvut.felk.erm.core.tasks.CheckForNewVersionTask;
import cz.cvut.felk.erm.gui.StorageProperties;
import cz.cvut.felk.erm.gui.managers.ManagerDirector;
import cz.cvut.felk.erm.gui.managers.PluginToolsManager;
import cz.cvut.felk.erm.gui.plugintools.PluginTool1;
import cz.cvut.felk.erm.gui.plugintools.PluginTool2;
import cz.cvut.felk.erm.swing.LookAndFeels;
import cz.cvut.felk.erm.swing.TrayIconSupport;
import cz.cvut.felk.erm.utilities.LogUtils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;


/**
 * Hlavni trida aplikace
 *
 * @author Ladislav Vitasek
 */
public class MainApp extends SingleXFrameApplication {

    private ManagerDirector director;
    private Collection<String> filesToOpen;
    private static boolean debug = false;

    private TrayIconSupport trayIconSupport = null;

//    private static Logger logger = null;

    /**
     * Zpracuje parametry pri spusteni programu
     *
     * @param args vstupni parametry
     * @return kolekce jmen souboru pro nacteni v programu
     */
    private Collection<String> processArguments(final String[] args) {
        Collection<String> result = new LinkedList<String>();
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
                result.add(arg);
            }
        }
        return result;
    }

    private void showVersion() {
        System.out.println(Consts.APPVERSION);
        System.out.println("Authors (c) 2008: Ladislav Vitasek");
        this.exit();
    }


    private void showHelp() {
        System.out.println("Usage: erm [-options] [file(s)]");
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

        LookAndFeels.getInstance().loadLookAndFeelSettings();//inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        //Swinger.initLaF(); //inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        super.initialize(args);
        ResourceConverter.register(new ListItemsConvertor());
    }

    @Override
    protected void startup() {
        director = new ManagerDirector(getContext());
        initMainFrame();
        this.addExitListener(new MainAppExitListener());

        show(getMainFrame());
        getTrayIconSupport().setVisibleByDefault();
        setGlobalEDTExceptionHandler();
    }

    private void initMainFrame() {
        final JFrame frame = getMainFrame();
        frame.setJMenuBar(director.getMenuManager().getMenuBar());
        frame.setContentPane(director.getComponent());
        frame.pack();

        boolean openingFile = false;
        if (!this.filesToOpen.isEmpty()) {
            openingFile = true;
        } else {
//            if (AppPrefs.getProperty(AppPrefs.NEW_FILE_AFTER_START, false)) {
//                openingFile = true;
//                this.director.getAreaManager().openFileInstance();
//            }
        }

        final PluginToolsManager manager = director.getPluginToolsManager();
        final PluginTool1 pluginTool1 = new PluginTool1();
        manager.addPluginTool(pluginTool1);
        final PluginTool2 pluginTool2 = new PluginTool2();
        manager.addPluginTool(pluginTool2);
        director.getDockingManager().renewLayout();

        if (!openingFile)
            this.director.getBackgroundManager().setGraphicMenu();
        if (AppPrefs.getProperty(UserProp.NEW_VERSION, true))
            startCheckNewVersion();

    }


    private void setGlobalEDTExceptionHandler() {
        final GlobalEDTExceptionHandler eh = new GlobalEDTExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(eh);
        Thread.currentThread().setUncaughtExceptionHandler(eh);
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
     *
     * @return hlavni panel
     */
    public ManagerDirector getManagerDirector() {
        assert director != null; //calling getMainPanel before finished initialization
        return director;
    }

    /**
     * Hlavni spousteci metoda programu
     *
     * @param args vstupni parametry pro program
     */
    public static void main(String[] args) {
        //zde prijde overovani vstupnich pridavnych parametru
        Application.launch(MainApp.class, args); //spusteni
    }

    private void startCheckNewVersion() {

        final Thread appThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(30000);
                    MainApp.this.getContext().getTaskService().execute(new CheckForNewVersionTask(false));
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        };
        appThread.setPriority(Thread.MIN_PRIORITY);
        appThread.start();
    }

    public static ApplicationContext getAContext() {
        return Application.getInstance(MainApp.class).getContext();
    }

    /**
     * Exit listener. Pri ukoncovani provede ulozeni uzivatelskych properties.
     */
    private class MainAppExitListener implements Application.ExitListener {

        public boolean canExit(EventObject event) {
            return true;
        }

        public void willExit(EventObject event) {
            AppPrefs.store();
            director.getDockingManager().storeLayout();
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
