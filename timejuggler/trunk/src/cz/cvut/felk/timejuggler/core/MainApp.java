package cz.cvut.felk.timejuggler.core;

import application.Application;
import application.ApplicationContext;
import application.SessionStorage;
import cz.cvut.felk.timejuggler.gui.MainPanelManager;
import cz.cvut.felk.timejuggler.gui.StorageProperties;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
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
        LogUtils.initLogging(debug);
        // logger = Logger.getLogger(MainApp.class.getName());
        Swinger.initLaF(); //inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        super.initialize(args);
    }

    protected void startup() {
        mainPanel = new MainPanelManager(getContext());
        final JFrame frame = getMainFrame();
        frame.setJMenuBar(mainPanel.getMenuManager().getMenuBar());
        frame.getContentPane().add(getMainPanelComponent());
        //resourceMap.injectComponents(frame);
        this.addExitListener(new MainAppExitListener());
        frame.pack();
        show(frame);
    }

    private JComponent getMainPanelComponent() {
        return mainPanel.getComponent();
    }

    @Override
    protected void injectSessionProperties() {
        super.injectSessionProperties();
        SessionStorage storage = getContext().getSessionStorage();
        storage.putProperty(JXStatusBar.class, new StorageProperties.XStatusBarProperty());
        storage.putProperty(JToolBar.class, new StorageProperties.JToolbarProperty());
        new StorageProperties().registerPersistenceDelegates();
    }

    /**
     * Vraci komponentu hlavniho panelu obsahujici dalsi komponenty
     * @return hlavni panel
     */
    public MainPanelManager getMainPanel() {
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
}
