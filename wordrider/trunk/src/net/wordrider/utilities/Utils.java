package net.wordrider.utilities;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public final class Utils {
//    public static final int DEBUG_LOG = 0;
//    public static final int INFO_LOG = 1;
//    public static final int WARN_LOG = 2;
//    public static final int ERROR_LOG = 3;
//
    //    private static final String[] INFO = {"-[DEBUG]-", "-[INFO ]-", "-[WARN ]-", "-[ERROR]-"};
    //  private static final char[] WORD_SEPARATORS = {' ', '.', ',', '+', '-', '!', '?', ';', '/', '*', '@'};
    //private static final Hashtable resources = new Hashtable(1);
    //    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
    private static String actual_java_version = null;
    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    private Utils() {

    }

    public static String generateXorString(final String text) {
        final char[] textArray = text.toCharArray();
        final int length = textArray.length;
        if (length > 0) {
            final StringBuilder buffer = new StringBuilder(length);
            for (int i = 0; i < length; ++i)
                buffer.append((char) (textArray[i] ^ Consts.XOR_VALUE));
            return buffer.toString();
        } else return "";
    }


    public static String addParam(final String params, final String paramName, final String paramValue) {
        final String paramWithValue;
        String encoded;
        try {
            encoded = java.net.URLEncoder.encode(paramValue, "ISO-8859-2");
        } catch (UnsupportedEncodingException e) {
            encoded = "";
            LogUtils.processException(logger, e);
        }
        paramWithValue = paramName + "=" + encoded;
        return params.length() > 0 ? params + "&" + paramWithValue : paramWithValue;
    }

//    public static void log(final int severity, final String s) {
//        if (severity >= Consts.LOG_SEVERITY)
//            System.out.println(format.format(Calendar.getInstance().getTime()) + INFO[severity] + s);
//    }
//
//    public static void logDebug(final String s) {
//        if (Utils.DEBUG_LOG >= Consts.LOG_SEVERITY)
//            System.out.println(format.format(Calendar.getInstance().getTime()) + INFO[Utils.DEBUG_LOG] + s);
//    }
//
//    public static void processException(final Exception e) {
//        System.err.println(Consts.APPVERSION + " Error : " + e.getMessage());
//        if (ERROR_LOG >= Consts.LOG_SEVERITY)
//            e.printStackTrace();
//    }

    // --Commented out by Inspection START (4.2.05 16:22):
    //    public static final String loadFile(final String fileName) {
    //        if (resources.containsKey(fileName)) return (String) resources.get(fileName);
    //        final StringBuffer buffer = new StringBuffer(2000);
    //        try {
    //            final BufferedReader stream = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fileName)));
    //            final char[] lines = new char[2000];
    //            int read;
    //            while ((read = stream.read(lines)) != -1)
    //                buffer.append(lines, 0, read);
    //            stream.close();
    //        } catch (Exception e) {
    //            processException(e);
    //        }
    //        resources.put(fileName, buffer.toString());
    //        return buffer.toString();
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:22)

    public static String shortenFileName(final String text, final int lengthLimit) {
        final int textLength = text.length();
        if (textLength < lengthLimit)
            return text;
        final String fileSeparator = File.separator;
        final String[] separated = text.split((fileSeparator.equals("\\") ? "\\\\" : fileSeparator));
        final int separatedCount = separated.length;
        if (separatedCount > 4) {
            //int charsCount = separated[0].length() + separated[1].length() + separated[separatedCount -2].length() + separated[separatedCount -1].length() + 4;
            int extractIndex = 2, wouldDelete = 0;
            for (; extractIndex < (separatedCount - 2); ++extractIndex) {
                wouldDelete += separated[extractIndex].length();
                if ((textLength - wouldDelete + 3) < lengthLimit)
                    break;
            }
            final StringBuilder result = new StringBuilder(textLength - wouldDelete + 3);
            result.append(separated[0]).append(fileSeparator).append(separated[1]).append(fileSeparator).append("...");
            for (int i = extractIndex + 1; i < separatedCount; ++i) {
                result.append(fileSeparator);
                result.append(separated[i]);
            }
            return result.toString();
        } else
            return text;
    }

    public static boolean isJVMVersion(final double requiredVersion) {
        if (actual_java_version == null) {
            final String javaVersion = System.getProperty("java.version");
            if (javaVersion == null) {
                logger.severe("Java version was not specified. Fatal error.");
                return false;
            }
            actual_java_version = javaVersion.substring(0, 3);
        }
        final Double actualVersion = new Double(actual_java_version);
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

    public static String addFileSeparator(final String filePath) {
        return filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
    }

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
            if (isWindows())
                logger.severe("Couldn't load properties:" + propertiesFile + " This is fatal error. Reinstal application may fix this problem.");
            LogUtils.processException(logger, e);
            return props;
        }
    }
}
