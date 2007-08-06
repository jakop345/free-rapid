package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;

import java.util.logging.Logger;
/**
 * @version 0.1
 * @created 14-IV-2007 17:18:35
 *
 *  Tato trida je nahrazena tridou EventTask !
 */
@Deprecated public class VEvent extends CalComponent {
	private final static Logger logger = Logger.getLogger(VEvent.class.getName());
	
    private String geoGPS;
    private String location;
    private int priority = 0; // 0 = undefined
    private String transparency;

    public VEvent() {
		super();
    }
   
	/**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
    	super.saveOrUpdate(template);
		
		if (getId() > 0) {
			logger.info("Database - Update: VEvent[" + getId() + "]...");
	        Object params[] = {
	                getComponentId(), geoGPS,
	                location, priority, transparency, getId() };
			String updateQuery = "UPDATE VEvent SET calComponentID=?,geo=?,location=?,priority=?,transp=?) WHERE vEventID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
			logger.info("Database - Insert: VEvent[]...");
	        Object params[] = {
	                getComponentId(), geoGPS,
	                location, priority, transparency
	        };
	        String insertQuery = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?) ";
	        template.executeUpdate(insertQuery, params);
	        setId(template.getGeneratedId());
	        logger.info("Database - VEvent new ID=" + getId());
		}
    }
    
    /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			Object params[] = {	getId() };		
			String deleteQuery = "DELETE FROM VEvent WHERE vEventID = ?";
			template.executeUpdate(deleteQuery, params);
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

    public String getTransparency() {
        return transparency;
    }

    /**
     * @param newVal
     */
    public void setTransparency(String newVal) {
        transparency = newVal;
    }



}