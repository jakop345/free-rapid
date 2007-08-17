package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-VII-2007 17:18:35
 * <p/>
 * Spolecna trida pro VEvent a VTodo
 */

public class EventTask extends CalComponent implements Comparable<EventTaskEntity>, EventTaskEntity {
    private final static Logger logger = Logger.getLogger(EventTask.class.getName());
    /* VEvent */

    private String geoGPS = "";
    private String location = "";
    private int priority = 0; // 0 = undefined
    private String transparency = "";

    /* VTodo */
    private int percentcomplete;
    private Timestamp completed;
    //public Alarms m_Alarms;

    private boolean isTodo;

    public final static String PROPERTYNAME_GEOGPS = "geoGPS";
    public final static String PROPERTYNAME_LOCATION = "location";
    public final static String PROPERTYNAME_PRIORITY = "priority";
    public final static String PROPERTYNAME_TRANSPARENCY = "transparency";
    public final static String PROPERTYNAME_PERCENTCOMPLETE = "percentcomplete";
    public final static String PROPERTYNAME_COMPLETED = "completed";
    public final static String PROPERTYNAME_ISTODO = "isTodo";

    /**
     * Method EventTask
     */
    public EventTask(boolean isTodo) {
        super();
        this.isTodo = isTodo;
    }

    public EventTask() {    // defaultne je to Event
        this(false);
    }


    /**
     * Method saveOrUpdate
     * @param template Ulozeni komponenty do databaze
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        super.saveOrUpdate(template);    // ulozeni CalComponent vlastnosti

        String updateQuery;
        String insertQuery;
        if (!isTodo) {
            // Ulozeni jako Event
            updateQuery = "UPDATE VEvent SET calComponentID=?,geo=?,location=?,priority=?,transp=?) WHERE vEventID = ? ";
            insertQuery = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?) ";
            if (getId() > 0) {
                logger.info("Database - Update: VEvent[" + getId() + "]...");
                Object params[] = {getComponentId(), geoGPS, location, priority, transparency, getId()};
                try {
                    template.executeUpdate(updateQuery, params);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("Database - Insert: VEvent[]...");
                Object params[] = {getComponentId(), geoGPS, location, priority, transparency};
                try {
                    template.executeUpdate(insertQuery, params);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                setId(template.getGeneratedId());
                logger.info("Database - VEvent new ID=" + getId());
            }
        } else {
            // Ulozeni jako ToDo
            updateQuery = "UPDATE VToDo SET calComponentID=?,geo=?,location=?,priority=?,percentcomplete=?) WHERE vToDoID = ? ";
            insertQuery = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete) VALUES (?,?,?,?,?)";
            if (getId() > 0) {
                logger.info("Database - Update: VToDo[" + getId() + "]...");
                Object params[] = {getComponentId(), geoGPS, location, priority, percentcomplete, getId()};
                try {
                    template.executeUpdate(updateQuery, params);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("Database - Insert: VToDo[]...");
                Object params[] = {getComponentId(), geoGPS, location, priority, percentcomplete};
                try {
                    template.executeUpdate(insertQuery, params);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                setId(template.getGeneratedId());
                logger.info("Database - VToDo new ID=" + getId());
            }
        }
    }

    /**
     * Method delete
     * @param template Ostraneni komponenty z databaze
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        String deleteQuery;
        if (!isTodo) {
            deleteQuery = "DELETE FROM VEvent WHERE vEventID = ?";
        } else {
            deleteQuery = "DELETE FROM VToDo WHERE vToDoID = ?";
        }
        if (getId() > 0) {
            Object params[] = {getId()};
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
            super.delete(template);
        }
    }


    /**
     * @param newVal
     */
    public void setPriority(int newVal) {
        /*if (newVal == null)
            throw new IllegalArgumentException("Priority cannot be null!");*/
        int oldVal = getPriority();
        priority = newVal;
        firePropertyChange(PROPERTYNAME_PRIORITY, oldVal, newVal);
    }

    public int getPriority() {
        return priority;
    }

    public String getGeoGPS() {
        return geoGPS;
    }

    /**
     * @param newVal
     */
    public void setGeoGPS(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("GeoGPS cannot be null!");
        final String oldVal = getGeoGPS();
        geoGPS = newVal;
        firePropertyChange(PROPERTYNAME_GEOGPS, oldVal, newVal);
    }

    public String getLocation() {
        return location;
    }

    /**
     * @param newVal
     */
    public void setLocation(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Location cannot be null!");
        final String oldVal = getLocation();
        location = newVal;
        firePropertyChange(PROPERTYNAME_LOCATION, oldVal, newVal);
    }

    public String getTransparency() {
        return transparency;
    }

    /**
     * @param newVal
     */
    public void setTransparency(String newVal) {
        /* TODO - asi predelat na konstanty - int */
        if (newVal == null)
            throw new IllegalArgumentException("Transparency cannot be null!");
        final String oldVal = getTransparency();
        transparency = newVal;
        firePropertyChange(PROPERTYNAME_TRANSPARENCY, oldVal, newVal);
    }

    public int getPercentComplete() {
        return percentcomplete;
    }

    /**
     * @param newVal
     */
    public void setPercentComplete(int newVal) {
        int oldVal = getPercentComplete();
        percentcomplete = newVal;
        firePropertyChange(PROPERTYNAME_PERCENTCOMPLETE, oldVal, newVal);
    }

    public Date getCompleted() {
        return new Date(completed.getTime());
    }

    /**
     * @param newVal
     */
    public void setCompleted(Date newVal) {
        Date oldVal = getCompleted();
        completed = (newVal == null ? null : new Timestamp(newVal.getTime()));
        firePropertyChange(PROPERTYNAME_COMPLETED, oldVal, newVal);
    }

    /**
     * @param konverze z Todo na Event a naopak
     */
    public void convert() {
        // TODO: napsat kod pro konverzi Event<->ToDo
        // ....
        // delete ***
        // change e/t
        // save ***
        this.isTodo = !this.isTodo;
    }

    public void setIsTodo(boolean isTodo) {
        final boolean oldVal = getIsTodo();
        this.isTodo = isTodo;
        firePropertyChange(PROPERTYNAME_ISTODO, oldVal, isTodo);
    }

    public boolean getIsTodo() {
        return (this.isTodo);
    }

    public int compareTo(EventTaskEntity o) {
        assert getSummary() != null;
        return getSummary().compareTo(o.getSummary());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EventTask event = (EventTask) o;
        return getId() == event.getId();
    }

    public Object clone() throws CloneNotSupportedException {
        EventTaskEntity clone;
        try {
            clone = (EventTaskEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return clone;
    }

    public String toString() {
        return getSummary();
	}
}
