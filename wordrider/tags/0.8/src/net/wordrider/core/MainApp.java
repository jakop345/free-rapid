/**
 *
 * @author Vity
 */
package net.wordrider.core;

import net.wordrider.core.actions.CheckForNewVersion;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class MainApp {
    private MainAppFrame mainAppFrame;
    private static final MainApp ourInstance = new MainApp();
    private static Logger logger;
    private static boolean debug = false;

    public static void makeProgress() {
        System.out.println("Loading ... please wait");
    }


    public static MainApp getInstance() {
        return ourInstance;
    }

    private MainApp() {
    }

    private static Collection<String> processArguments(final String[] args) {
        Collection<String> result = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.equals("-d") || arg.equals("-debug") || arg.equals("--debug")) {
                    debug = true;
                } else if (arg.equals("-h") || arg.endsWith("-help")) {
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

    private static void showVersion() {
        System.out.println(Consts.APPVERSION);
        System.out.printf("created by VitySoft(c) 2004-2006 %s%n", Consts.WEBURL);
        System.exit(0);
    }


    public static void main(final String[] args) {
        final Collection<String> files = processArguments(args);
        if (!Utils.isJVMVersion(1.5)) {
            System.err.println("Whooops. Application requires a 1.5 version or later of the Java platform.");
            System.err.println("Please upgrade to a newer version. Read 'readme.txt' file for available URLs.");
            System.exit(-1);
        }
        LogUtils.initLogging(debug);
        logger = Logger.getLogger(MainApp.class.getName());
        if (OneInstanceClient.checkInstance(files)) return;
        System.out.println("Test1");
        try {
            getInstance().start(files);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    private static void showHelp() {
        System.out.println("Usage: wordrider [-options] [file(s)]");
        System.out.println("commands: -h, --help  - to view this message");
        System.out.println("          -v, --version - display version information and exit");
        System.out.println("          -d, -debug - enable debug log level\n");
        System.out.println("           file(s) - path to file(s) for opening");
        System.out.println("min. Java version required : 1.5");
        System.out.println("See the readme.txt for more information.\n");
        System.exit(0);
    }


    private void start(final Collection<String> openFiles) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
        getMainAppFrame().init(openFiles);
//            }
//        });
        if (AppPrefs.getProperty(AppPrefs.NEW_VERSION, false))
            startCheckNewVersion();        
    }

    private void startCheckNewVersion() {
        final Thread appThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(30000);
                    CheckForNewVersion.check(false);
                } catch (InterruptedException e) {
                    //ignore
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        };
        appThread.setPriority(Thread.MIN_PRIORITY);
        appThread.start();
    }

    public final MainAppFrame getMainAppFrame() {
        if (mainAppFrame == null) {
            return mainAppFrame = new MainAppFrame();
        }
        return mainAppFrame;
    }


}
