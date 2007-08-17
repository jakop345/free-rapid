package cz.cvut.felk.timejuggler.utilities;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.db.InitiateDatabaseException;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Jan Struz
 * @version 0.1
 */

public class DbHelper {
    private final static Logger logger = Logger.getLogger(DbHelper.class.getName());

    private static DbHelper instance;
    private File source;
    private File targetdir;
    private File databasedirectory;

    /**
     * Method DbHelper
     */
    private DbHelper() {
        ApplicationContext appContext = MainApp.getAContext();
        source = new File(AppPrefs.getAppPath(), Consts.DB_DEFAULT);
        targetdir = appContext.getLocalStorage().getDirectory();    // databaze v zipu musi byt v adresari db/
        databasedirectory = new File(targetdir, Consts.DB_LOCALDIR);
    }

    /**
     * Method getInstance
     */
    public static synchronized DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper();
        }
        return instance;
    }

    /**
     * Method isDatabasePresent
     * <p/>
     * overi, zda existuje lokalni databaze
     */
    public boolean isDatabasePresent() {
        return databasedirectory.exists();
    }

    /**
     * Method localDbCreate
     * <p/>
     * vytvori novou lokalni databazi (podle db_init/db.zip)
     */
    public void localDbCreate() throws InitiateDatabaseException {
        logger.info("Checking for existing database...");
        if (!databasedirectory.exists()) {
            //Unzip (inicializacni databaze)
            logger.info("Database is not present, initiating db_init copy");
            try {
                if (!databasedirectory.mkdirs())
                    throw new IOException("Couldn't create database directory:" + databasedirectory);
                unzip(source, targetdir);
            }
            catch (IOException e) {
                //LogUtils.processException(logger, e);
                databasedirectory.delete();
                throw new InitiateDatabaseException("Error during initializing default database : " + source, e);
            }
            logger.info("Database was created sucessfully");
        } else logger.info("Database is already present");
    }

    /*
     * Method unzip
     *
     * Rozbali zip soubor 
     */
    public void unzip(File source, File targetdir) throws IOException {
        Enumeration entries;

        ZipFile zipped_db = null;
        try {
            zipped_db = new ZipFile(source);
            entries = zipped_db.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
                    logger.info("Extracting directory: " + entry.getName());
                    // This is not robust, just for demonstration purposes.
                    (new File(targetdir, entry.getName())).mkdir();
                    continue;
                }

                logger.info("Extracting file: " + entry.getName());
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = zipped_db.getInputStream(entry);
                    out = new BufferedOutputStream(new FileOutputStream(new File(targetdir, entry.getName())));
                    copyInputStream(in, out);
                } finally {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                }
            }
        }
        finally {
            if (zipped_db != null)
                zipped_db.close();
        }

    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[8192];//koukal jsem, ze soubory jsou vetsinou v tyhle velikosti
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

    }
}
