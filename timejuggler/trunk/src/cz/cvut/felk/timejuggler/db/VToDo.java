package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * @version 0.1
 * @created 14-IV-2007 21:47:56
 */
public class VToDo extends CalComponent {
	private final static Logger logger = Logger.getLogger(VToDo.class.getName());
	
    private Timestamp due;
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
    public void saveOrUpdate (TimeJugglerJDBCTemplate template){
    	super.saveOrUpdate(template);
        
        if (getId() > 0) {
        	logger.info("Database - Update: VToDo[" + getId() + "]...");
	        Object params[] = {
	                getComponentId(), geoGPS,
	                location, priority, percentcomplete, due, getId() };
	        String updateQuery = "UPDATE VToDo SET calComponentID=?,geo=?,location=?,priority=?,percentcomplete=?,due=?) WHERE vToDoID = ? ";
	        template.executeUpdate(updateQuery, params);
        }else{
        	logger.info("Database - Insert: VToDo[]...");
	        Object params[] = {
	                getComponentId(), geoGPS,
	                location, priority, percentcomplete, due };
	
	        String insertQuery = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete,due) VALUES (?,?,?,?,?,?)";
	        template.executeUpdate(insertQuery, params);
	       	setId(template.getGeneratedId());
	       	logger.info("Database - VToDo new ID=" + getId());
        }
    }

    /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			Object params[] = {	getId() };		
			String deleteQuery = "DELETE FROM VToDo WHERE vToDoID = ?";
			template.executeUpdate(deleteQuery, params);
			setId(-1);
			super.delete(template);
		}
	}

    public Timestamp getDue() {
        return due;
    }

    /**
     * @param newVal
     */
    public void setDue(Timestamp newVal) {
        due = newVal;
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