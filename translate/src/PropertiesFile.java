import utilities.LogUtils;
import utilities.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class PropertiesFile {
    private final static Logger logger = Logger.getLogger(PropertiesFile.class.getName());
    
    private File file;
    private String language;
    private Map<String, String> properties = new LinkedHashMap<String, String>();
    private Map<String, String> missingProperties = new LinkedHashMap<String, String>();
    private Set<String> forRemovingProperties = new LinkedHashSet<String>();


    public PropertiesFile(File file, String language) {
        this.file = file;
        //hack for UIStringsManager
        if (file.getName().equals("UIStringsManager.properties")) {
            this.file = new File(file.getParentFile(), "UIStringsManager_en.properties");
        }
        this.language = language;
        if (!isFileMissing()) {
            load();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getMissingPropertiesIn(PropertiesFile propertiesFile) {

        final Map<String, String> missingProperties = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!propertiesFile.getProperties().containsKey(entry.getKey())) {
                missingProperties.put(entry.getKey(), entry.getValue());
            }
        }
        return missingProperties;
    }

    public Set<String> getPropertiesForRemovingIn(PropertiesFile propertiesFile) {
        final Set<String> forRemoving = new HashSet<String>();
        for (String key: propertiesFile.getProperties().keySet()) {
            if (!properties.containsKey(key)) {
                forRemoving.add(key);
            }
        }
        return forRemoving;
    }

    public Map<String, String> getMissingProperties() {
        return missingProperties;
    }

    public void setMissingProperties(Map<String, String> missingProperties) {
        this.missingProperties = missingProperties;
    }

    public Set<String> getForRemovingProperties() {
        return forRemovingProperties;
    }

    public void setForRemovingProperties(Set<String> forRemovingProperties) {
        this.forRemovingProperties = forRemovingProperties;
    }

    public boolean isFileMissing() {
        return !file.exists();
    }


    private void load() {
        try {
            final Scanner scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNextLine()) {
                final String line = Utils.ltrim(scanner.nextLine());
                if (line.trim().isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                final int i = line.indexOf('=');
                final String key = line.substring(0, i).trim();
                final String value = Utils.ltrim(line.substring(i + 1));
                properties.put(key, value);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            LogUtils.processException(logger, e);
        }

    }

}
