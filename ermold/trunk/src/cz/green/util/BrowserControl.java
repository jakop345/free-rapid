package cz.green.util;

import java.io.IOException;

/**
 * A simple, static class to display a URL in the system browser.
 * <p/>
 * Unix Unix, the system browser is hard-coded to be 'netscape'. Netscape must be in your PATH for this to work. This
 * has been tested with the following platforms: AIX, HP-UX and Solaris.
 * <p/>
 * Under Windows, this will bring up the default browser under windows, usually either Netscape or Microsoft IE. The
 * default browser is determined by the OS. This has been tested under Windows 95/98/NT.
 * <p/>
 * Examples: BrowserControl.displeyURL("http://www.javaworld.com") BrowserControl.displeyURL("file://c:\\docs\\index.html")
 * BrowserControl.displeyURL("file://user/joe/index.html")
 * <p/>
 * Note - you must include the url type -- either "http://" or "file://"
 */
public class BrowserControl {
    /**
     * Used to identify the windows platform.
     */
    private static final String WIN_ID = "Windows";
    /**
     * The default system browser under windows.
     */
    private static final String WIN_PATH = "rundll32";
    /**
     * The flag to display a url.
     */
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

    private BrowserControl() {
    }

    /**
     * Displey a file in the system browser. If you want to displey a file, you must include the absolute path name.
     *
     * @param url the file's url (the url must start with either "http://" or "file://"
     */
    public static void displayURL(String url) {
        boolean windows = isWindowsPlatform();
        String cmd = null;
        if (windows) {
            //cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
            try {
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                Runtime.getRuntime().exec(cmd);
            } catch (IOException x) {
                //couldn't exec browser
                System.err.println("Error bringing up browser, cmd='" + cmd + "'");
                System.err.println("Caught: " + x);
            }
        } else {
            try {
                //Under Unix, Netscape has to be running for the "-remote"
                //command to work. So, we try sending the command and
                //check for an exit value. If the exit command is 0,
                //it worked, otherwise we need to start the browser.
                //cmd = 'netscape -remote openURL(http://www.javaworld.com)'
                cmd = "firefox " + url;
                Runtime.getRuntime().exec(cmd);
            } catch (IOException x) {
                //couldn't exec browser
                try {
                    //wait for exit code -- if it's 0, command worked, 
                    //otherwise we need to start the browser up.
///				int exitCode = p.waitFor();
//				if (exitCode != 0) {
                    //Command failed, start up the browser
                    //cmd = 'netscape http://www.javaworld.com'
                    cmd = "mozilla " + url;
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException ex) {
                    //couldn't exec browser
                    System.err.println("Error bringing up browser, cmd='" + cmd + "'");
                    System.err.println("Caught: " + ex);
                }
            }


        }
    }

    /**
     * Try to determine whether this application is running under Windows or some other platform by examing the
     * "os.name" property.
     *
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name");
        return (os != null && os.startsWith(WIN_ID));
    }

}
