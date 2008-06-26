package cz.cvut.felk.erm.db;

import com.jgoodies.binding.beans.Model;
import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.Consts;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.utilities.Utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DBConnection extends Model implements Cloneable, Serializable {
    private final static Logger logger = Logger.getLogger(DBConnection.class.getName());

    private static final long serialVersionUID = 1L;

    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String DESC_PROPERTY = "description";
    public static final String DRIVER_LIBRARY_PROPERTY = "driverLibrary";
    public static final String DRIVER_PROPERTY = "driver";
    public static final String URL_PROPERTY = "url";

    public static final String USER_PROPERTY = "user";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String TESTED_PROPERTY = "tested";
    public static final String VALID_PROPERTY = "valid";


    private String id = "";

    private String name = "";
    private String description = "";
    private String driverLibrary = "";
    private String driver = "";
    private String url = "";

    private String user = "";

    private boolean tested = false;
    private boolean valid = false;


    private transient String password = null;
    /**
     * property urcena pouze pro serializaci
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private String pswd = "Never read";

    static {
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(DBConnection.class);
            PropertyDescriptor[] propertyDescriptors =
                    info.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getName().equals("password")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (IntrospectionException e) {
            logger.log(Level.SEVERE, "Probably invalid property name", e);
        }
    }

    public DBConnection() {
        this(false);
    }

    public DBConnection(final boolean generateNewID) {
        if (generateNewID)
            generateNewId();
    }

    public void generateNewId() {
        setId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        final String oldValue = this.id;
        this.id = id;
        firePropertyChange(ID_PROPERTY, oldValue, id);
    }

    public String getName() {
        return name;
    }


    public String getPswd() {
        return Utils.generateXorString(password);//encrypt
    }

    public void setPswd(String pswd) {
        setPassword(Utils.generateXorString(pswd));//decrypt
    }

    public void setName(String name) {
        final String oldValue = this.name;
        this.name = name;
        firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        final String oldValue = this.description;
        this.description = description;
        firePropertyChange(DESC_PROPERTY, oldValue, description);
    }

    public String getDriverLibrary() {
        return driverLibrary;
    }

    public void setDriverLibrary(String driverLibrary) {
        final String oldValue = this.driverLibrary;
        this.driverLibrary = driverLibrary;
        firePropertyChange(DRIVER_LIBRARY_PROPERTY, oldValue, driverLibrary);
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        final String oldValue = this.driver;
        this.driver = driver;
        firePropertyChange(DRIVER_PROPERTY, oldValue, driver);
    }

    public boolean isTested() {
        return tested;
    }

    public void setTested(boolean tested) {
        boolean oldValue = this.tested;
        this.tested = tested;
        firePropertyChange(TESTED_PROPERTY, oldValue, tested);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        final String oldValue = this.url;
        this.url = url;
        firePropertyChange(URL_PROPERTY, oldValue, url);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        final String oldValue = this.user;
        this.user = user;
        firePropertyChange(USER_PROPERTY, oldValue, user);
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(final String password) {
        final String oldValue = this.password;
        this.password = password;
        firePropertyChange(PASSWORD_PROPERTY, oldValue, password);
    }

    public boolean isValid() {
        return Utils.hasValue(driver) && Utils.hasValue(user) && Utils.hasValue(url);
    }

    public boolean getValid() {
        return isValid();
    }

    public void setValid(boolean setValid) {

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBConnection that = (DBConnection) o;

        return id.equals(that.id);
    }

    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public DBConnection doCopy() {
        try {
            return (DBConnection) clone();
        } catch (CloneNotSupportedException e) {
            //ignore
            return null;
        }
    }


    public static DBConnection createDefaultConnectionSettings() {
        final String name = AppPrefs.getProperty(UserProp.DB_DEFAULT_NAME, Consts.DB_NAME);
        final String desc = AppPrefs.getProperty(UserProp.DB_DEFAULT_DESCRIPTION, Consts.DB_DESCRIPTION);
        final String driver = AppPrefs.getProperty(UserProp.DB_DEFAULT_DRIVER, Consts.DB_DRIVER);
        final String url = AppPrefs.getProperty(UserProp.DB_DEFAULT_URL, Consts.DB_URL);
        String driverLibrary = AppPrefs.getProperty(UserProp.DB_DEFAULT_DRIVERLIBRARY, Consts.DB_LIBRARY_FILE);
        if (driverLibrary.startsWith("jdbc")) {
            driverLibrary = Utils.addFileSeparator(AppPrefs.getAppPath()) + driverLibrary;
        }

        final String user = AppPrefs.getProperty(UserProp.DB_DEFAULT_USERNAME, Consts.DB_USERNAME);
        final String password = AppPrefs.getProperty(UserProp.DB_DEFAULT_PASSWORD, Consts.DB_PASSWORD);
        final DBConnection conn = new DBConnection();
        conn.generateNewId();
        conn.setName(name);
        conn.setDescription(desc);
        conn.setDriverLibrary(driverLibrary);
        conn.setDriver(driver);
        conn.setUrl(url);
        conn.setUser(user);
        conn.setPassword(password);
        return conn;
    }

    public String toString() {
        //return name == null ? "" : name;
        return name;
    }
}
