package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * @version 0.1
 * @created 14-IV-2007 21:47:56
 * <p/>
 * Tato trida je nahrazena tridou EventTask !
 */
@Deprecated
public class VToDo extends CalComponent {
    private final static Logger logger = Logger.getLogger(VToDo.class.getName());
    private String geoGPS;
    private String location;
    private int priority = 0; // 0 = undefined
    private int percentcomplete;
    private Timestamp completed;
    //public Alarms m_Alarms;

    public VToDo() {
        super();
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        super.saveOrUpdate(template);
        if (getId() > 0) {
            logger.info("Database - Update: VToDo[" + getId() + "]...");
            Object params[] = {
                    getComponentId(), geoGPS,
                    location, priority, percentcomplete, getId()};
            String updateQuery = "UPDATE VToDo SET calComponentID=?,geo=?,location=?,priority=?,percentcomplete=?) WHERE vToDoID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("Database - Insert: VToDo[]...");
            Object params[] = {
                    getComponentId(), geoGPS,
                    location, priority, percentcomplete};

            String insertQuery = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete) VALUES (?,?,?,?,?)";
            try {
                template.executeUpdate(insertQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(template.getGeneratedId());
            logger.info("Database - VToDo new ID=" + getId());
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM VToDo WHERE vToDoID = ?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
            super.delete(template);
        }
    }

    public String getGeoGPS() {
        return geoGPS;
    }

    /**
     * @param newVal
     */
    public void setGeoGPS(String newVal) {
        geoGPS = newVal;
    }

    public String getLocation() {
        return location;
    }

    /**
     * @param newVal
     */
    public void setLocation(String newVal) {
        location = newVal;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * @param newVal
     */
    public void setPriority(int newVal) {
        priority = newVal;
    }

    public int getPercentComplete() {
        return percentcomplete;
    }

    /**
     * @param newVal
     */
    public void setPercentComplete(int newVal) {
        percentcomplete = newVal;
    }

    public Timestamp getCompleted() {
        return completed;
    }

    /**
     * @param newVal
     */
    public void setCompleted(Timestamp newVal) {
        completed = newVal;
    }

}