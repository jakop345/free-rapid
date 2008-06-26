package cz.cvut.felk.erm.db;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;


/**
 * Obe metody volat pouze z Tasku
 *
 * @author Ladislav Vitasek
 */

public class DatabaseDriverManager {
    private final static Logger logger = Logger.getLogger(DatabaseDriverManager.class.getName());

    private static DatabaseDriverManager instance = null;


    private Map<String, String[]> driverClassesCache;
    private Map<String, ClassLoader> libraryClassLoaders;

    private DatabaseDriverManager() {
        driverClassesCache = new HashMap<String, String[]>();
        libraryClassLoaders = new HashMap<String, ClassLoader>();
    }

    public static synchronized DatabaseDriverManager getInstance() {
        if (instance == null)
            return instance = new DatabaseDriverManager();
        return instance;
    }

    public String[] loadDriverClasses(String libraryPath) throws IOException {
        //
        logger.info("Loading driver classes from " + libraryPath);
        String[] classNames = driverClassesCache.get(libraryPath);
        if (classNames == null) {
            logger.info("Drivers not found in cache - browsing library");
            List<String> drivers = new ArrayList<String>();
            final File file = new File(libraryPath);
            if (!file.exists() && file.isFile())
                return new String[0];
            final URL[] urls = new URL[]{file.toURI().toURL()};
            final URLClassLoader classLoader = new URLClassLoader(urls);
            libraryClassLoaders.put(libraryPath, classLoader);

            final JarFile jarFile = new JarFile(file);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {

                    int index = name.lastIndexOf('.');
                    String className = name.substring(0, index);
                    className = className.replace('/', '.').replace('\\', '.');

                    try {
                        if (className.indexOf("Driver") > -1) {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (Driver.class.isAssignableFrom(clazz)) {
                                drivers.add(clazz.getName());
                            }
                        }
                    }
                    catch (Throwable throwable) {
                        // ignore
                    }
                }
            }
            classNames = drivers.toArray(new String[drivers.size()]);
            driverClassesCache.put(libraryPath, classNames);
            return classNames;
        }
        return classNames;
    }

    public Driver getDriver(String libraryPath, String className) throws Exception {

        if (!libraryClassLoaders.containsKey(libraryPath)) {
            loadDriverClasses(libraryPath);
        }
        ClassLoader classLoader = libraryClassLoaders.get(libraryPath);
        return (Driver) Class.forName(className, true, classLoader).newInstance();
    }

}