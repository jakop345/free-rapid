package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.util.logging.Logger;

/**
 * @version 0.1
 * @created 14-IV-2007 16:38:21
 * <p/>
 * Trida reprezentujici objekt kalendar (VCalendar) v databazi
 */
public class VCalendar extends DbElement implements Comparable<VCalendarEntity>, VCalendarEntity {
    private final static Logger logger = Logger.getLogger(VCalendar.class.getName());

    private String productId = "-//CVUT //TimeJuggler Calendar 0.1//CZ";
    private String version = "2.0";
    private String calendarScale = "GREGORIAN";
    private String method = "PUBLISH";
    private String name = "";
    private boolean active = false;

    public final static String PROPERTYNAME_PRODUCTID = "productId";
    public final static String PROPERTYNAME_NAME = "name";
    public final static String PROPERTYNAME_VERSION = "version";
    public final static String PROPERTYNAME_CALENDARSCALE = "calendarScale";
    public final static String PROPERTYNAME_METHOD = "method";
    public final static String PROPERTYNAME_ACTIVE = "active";

    public VCalendar() {
        super();
    }

    public VCalendar(int id) {
        super(id);
    }

    public VCalendar(String name) {
        super();
        setName(name);
    }

    public VCalendar(String name, int id) {
        super(id);
        setName(name);
    }

    public void store() {

    }

    /**
     * Method saveOrUpdate
     * @param template Ulozi novy kalendar do databaze, nebo provede update udaju o kalendari
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            logger.info("Database - Update: VCalendar[" + getId() + "]:" + name + "...");
            Object params[] = {productId, version, calendarScale, method, name, active ? 1 : 0, getId()};
            String updateQuery = "UPDATE VCalendar SET prodid=?,version=?,calscale=?,method=?,name=?,active=? WHERE vCalendarID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (cz.cvut.felk.timejuggler.db.DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            // Pridani noveho kalendare do databaze
            logger.info("Database - Insert: VCalendar[]:" + name + "...");
            Object params[] = {productId, version, calendarScale, method, name, active ? 1 : 0};
            String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name,active) VALUES (?,?,?,?,?,?)";
            try {
                template.executeUpdate(insertQuery, params);
            } catch (cz.cvut.felk.timejuggler.db.DatabaseException e) {
                e.printStackTrace();
            }
            // nastaveni klice objektu VCalendar
            setId(template.getGeneratedId());
            logger.info("Database - VCalendar new ID=" + getId());
        }
    }

    /**
     * Method delete
     * @param template Odstrani kalendar z databaze
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            logger.info("Database - DELETE: VCalendar[" + getId() + "]:" + name + "...");
            /*List<EventTask> events = getEvents();
            for (EventTask event : events) {
                event.delete(template);
            }

            List<EventTask> todos = getToDos();
            for (EventTask todo : todos) {
                todo.delete(template);
            }*/

            Object params[] = {getId()};    //TODO: DELETE CASCADE / zachovat eventy..?! / dialog
            String deleteQuery = "DELETE FROM VCalendar WHERE vCalendarID = ?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (cz.cvut.felk.timejuggler.db.DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("ProductId cannot be null!");
        final String oldVal = getProductId();
        productId = newVal;
        firePropertyChange(PROPERTYNAME_PRODUCTID, oldVal, newVal);
    }

    public String getVersion() {
        return version;
    }

    /**
     * @param newVal
     */
    public void setVersion(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Version cannot be null!");
        final String oldVal = getVersion();
        version = newVal;
        firePropertyChange(PROPERTYNAME_VERSION, oldVal, newVal);
    }

    public String getCalendarScale() {
        return calendarScale;
    }

    /**
     * @param newVal
     */
    public void setCalendarScale(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("CalendarScale cannot be null!");
        final String oldVal = getCalendarScale();
        calendarScale = newVal;
        firePropertyChange(PROPERTYNAME_CALENDARSCALE, oldVal, newVal);
    }

    public String getMethod() {
        return method;
    }

    /**
     * @param newVal
     */
    public void setMethod(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Method cannot be null!");
        final String oldVal = getMethod();
        method = newVal;
        firePropertyChange(PROPERTYNAME_METHOD, oldVal, newVal);
    }

    public String getName() {
        return name;
    }

    /**
     * @param newVal
     */
    public void setName(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Name cannot be null!");
        final String oldVal = getName();
        name = newVal;
        firePropertyChange(PROPERTYNAME_NAME, oldVal, newVal);
    }

    public int compareTo(VCalendarEntity o) {
        assert getName() != null;
        return getName().compareTo(o.getName());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VCalendar cal = (VCalendar) o;
        return getId() == cal.getId();
    }

    public Object clone() throws CloneNotSupportedException {
        VCalendarEntity clone;
        try {
            clone = (VCalendarEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return clone;
    }

    public String toString() {
        return this.name;
    }

    /*
      * nastavi kalendar jako vybrany (checked)
      */
    public void setActive(boolean active) {
        boolean oldVal = isActive();
        this.active = active;
        firePropertyChange(PROPERTYNAME_ACTIVE, oldVal, active);
    }

    public boolean isActive() {
        return (this.active);
    }
}
