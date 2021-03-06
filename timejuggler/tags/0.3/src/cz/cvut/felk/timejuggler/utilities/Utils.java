package cz.cvut.felk.timejuggler.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Pomocne utility pro spravu aplikace Test na system.
 * @author Vity
 */
public final class Utils {
    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * pomocna promenna pro ulozeni verze JVM na ktere bezime
     */
    private static String actualJavaVersion = null;

    private Utils() {

    }


    /**
     * Provede test na verzi JVM na ktere aplikaci bezi
     * @param requiredVersion pozadovana verze
     * @return vraci true, pokud aplikace bezi na pozadovane verzi, pokud ne, vraci false
     */
    public static boolean isJVMVersion(final double requiredVersion) {
        if (actualJavaVersion == null) {
            final String javaVersion = System.getProperty("java.version");
            if (javaVersion == null) {
                return false;
            }
            actualJavaVersion = javaVersion.substring(0, 3);
        }
        final Double actualVersion = new Double(actualJavaVersion);
        return actualVersion.compareTo(requiredVersion) >= 0;
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getPureFilename(final File f) {
        final String[] fileName = f.getName().split("\\.", 2);
        return fileName[0];
    }

    /**
     * Prida na danou cestu oddelovac, pokud jiz oddelovac na konci ma, nic se nepridava
     * @param filePath cesta
     * @return cesta s oddelovacem
     */
    public static String addFileSeparator(final String filePath) {
        return filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
    }

    /**
     * Otestuje zda aplikace bezi na Windows
     * @return
     */
    public static boolean isWindows() {
        final String osName = System.getProperty("os.name");
        return (osName == null || osName.startsWith("Windows"));
//        return false;
    }

    public static Properties loadProperties(final String propertiesFile, final boolean isResource) {
        final Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = (!isResource) ? new FileInputStream(propertiesFile) : Utils.class.getClassLoader().getResourceAsStream(propertiesFile);
            if (inputStream == null)
                throw new IOException("Couldn't read Properties file");
            props.load(inputStream);
            inputStream.close();
            return props;
        } catch (IOException e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                LogUtils.processException(logger, ex);
            }
            logger.warning("Couldn't load properties:" + propertiesFile);
            return props;
        }
    }
}
