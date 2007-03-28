package net.wordrider.core;

import net.wordrider.utilities.Consts;

import java.awt.*;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Vity
 */
final class MainAppSplash {
    private MainAppSplash() {
    }

    private static boolean containsInterruble(final String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-") && !(arg.equals("-d") || arg.equals("-debug") || arg.equals("--debug")))
                return true;
        }
        return false;
    }

    @SuppressWarnings({"RedundantArrayCreation"})
    public static void main(final String[] args) {
//        System.out.println(System.getProperty("sun.java2d.translaccel", null));
//        System.out.println(System.getProperty("sun.java2d.opengl", null));
        Frame splashFrame = null;

        if (!(containsInterruble(args) || OneInstanceServer.isWordRiderInUse())) {

            final URL imageURL = ((URLClassLoader) MainAppSplash.class.getClassLoader()).findResource(Consts.IMAGESDIR + "splash.gif");

            if (imageURL != null) {
                splashFrame = SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(imageURL));                
            } else {
                System.err.println("Splash image not found");
            }
        }

        try {
            Class.forName(MainApp.class.getName())
                    .getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.flush();
            System.exit(-1);
        }
        // Dispose the splash screen
        // -------------------------
        if (splashFrame != null) splashFrame.dispose();
    }
}