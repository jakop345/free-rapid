package cz.cvut.felk.timejuggler.utilities;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.db.DatabaseException;

import java.util.logging.Logger;
import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * 
 */

public class DbHelper {
	private final static Logger logger = Logger.getLogger(DbHelper.class.getName());
	
	private static DbHelper instance;
	private String source;
	private String targetdir;
	private File databasedirectory;
	
	private MainApp app;
	private final ApplicationContext appContext;
	
	/**
	 * Method DbHelper
	 *
	 *
	 */
	private DbHelper() {
        app = MainApp.getInstance(MainApp.class);
        appContext = app.getContext();

		source = AppPrefs.getAppPath() + "/" + Consts.DB_DEFAULT;
		targetdir = appContext.getLocalStorage().getDirectory().getPath();	// databaze v zipu musi byt v adresari db/
		databasedirectory = new File(targetdir + "/" + Consts.DB_LOCALDIR);
	}	
	
	/**
	 * Method getInstance
	 *
	 *
	 */
	public static synchronized DbHelper getInstance(){
		if (instance == null) {
			instance = new DbHelper();
		}
		return instance;
	}
	
	/**
	 * Method isDatabasePresent
	 *
	 * overi, zda existuje lokalni databaze
	 */
	public boolean isDatabasePresent(){	
    	return databasedirectory.exists();
	}
	
	/**
	 * Method localDbCreate
	 *
	 * vytvori novou lokalni databazi (podle db_init/db.zip)
	 */
	public void localDbCreate() throws DatabaseException {
		logger.info("Creating new database...");
    	if (!databasedirectory.exists()) {
    		//Unzip (inicializacni databaze)
    		try {
    			databasedirectory.mkdir();
    			unzip(source, targetdir);
		    }
		    catch (IOException e) {
		    	//LogUtils.processException(logger, e);
	    		databasedirectory.delete();
	    		throw new DatabaseException("Nastal problem s vychozi databazi : " + source);
		    }
    	}
	}

    /*
     * Method unzip
     *
     * Rozbali zip soubor 
     */
	public void unzip(String source, String targetdir) throws IOException{
		Enumeration entries;

		ZipFile zipped_db = new ZipFile(source);						
		
		entries = zipped_db.entries();
		while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	
			if(entry.isDirectory()) {
				// Assume directories are stored parents first then children.
				logger.info("Extracting directory: " + entry.getName());
				// This is not robust, just for demonstration purposes.
	        	(new File(targetdir + "/" + entry.getName())).mkdir();
	        	continue;
	        }

	        logger.info("Extracting file: " + entry.getName());
	        copyInputStream(zipped_db.getInputStream(entry),
	           new BufferedOutputStream(new FileOutputStream(targetdir + "/" + entry.getName())));
		}
		zipped_db.close();
    }
    
    private final void copyInputStream(InputStream in, OutputStream out) throws IOException {
    	byte[] buffer = new byte[1024];
    	int len;

    	while((len = in.read(buffer)) >= 0)
      		out.write(buffer, 0, len);

    	in.close();
    	out.close();
	}
}
