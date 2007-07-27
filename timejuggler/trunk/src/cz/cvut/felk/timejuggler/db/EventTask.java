package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-VII-2007 17:18:35
 * 
 * Spolecna trida pro VEvent a VTodo
 */
 
public class EventTask extends CalComponent {
	private final static Logger logger = Logger.getLogger(EventTask.class.getName());
	/* VEvent */

    private String geoGPS;
    private String location;
    private int priority = 0; // 0 = undefined
    private String transparency;
    
    /* VTodo */
    //TODO due Timestamp zmenit na Date, nebo ostatni na Timestamp!?
    
    //private Timestamp due;
    //private String geoGPS;
    //private String location;
    //private int priority = 0; // 0 = undefined
    private int percentcomplete;
    private Timestamp completed;
    //public Alarms m_Alarms;
    
    
    
    private boolean isTodo;
    
	/**
	 * Method EventTask
	 *
	 *
	 */
	public EventTask(boolean isTodo) {
		super();
		this.isTodo = isTodo;
	}
	
	public EventTask() {	// defaultne je to Event
		this(false);
	}
	
	
	/**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
    	super.saveOrUpdate(template);

    	String updateQuery;
    	String insertQuery;
		if (!isTodo) {
			updateQuery = "UPDATE VEvent SET calComponentID=?,geo=?,location=?,priority=?,transp=?) WHERE vEventID = ? ";			
			insertQuery = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?) ";
			if (getId() > 0) {
				logger.info("Database - Update: VEvent[" + getId() + "]...");
	        	Object params[] = {getComponentId(), geoGPS, location, priority, transparency, getId() };
	        	template.executeUpdate(updateQuery, params);
	        }else{
	        	logger.info("Database - Insert: VEvent[]...");
	        	Object params[] = {getComponentId(), geoGPS, location, priority, transparency };
		        template.executeUpdate(insertQuery, params);
		        setId(template.getGeneratedId());
		        logger.info("Database - VEvent new ID=" + getId());
	        }
		}else{
			updateQuery = "UPDATE VToDo SET calComponentID=?,geo=?,location=?,priority=?,percentcomplete=?,due=?) WHERE vToDoID = ? ";
			insertQuery = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete,due) VALUES (?,?,?,?,?,?)";
			if (getId() > 0) {
				logger.info("Database - Update: VToDo[" + getId() + "]...");
	        	Object params[] = {getComponentId(), geoGPS, location, priority, percentcomplete, getDue(), getId() };
	        	template.executeUpdate(updateQuery, params);
	        }else{
	        	logger.info("Database - Insert: VToDo[]...");
	        	Object params[] = {getComponentId(), geoGPS, location, priority, percentcomplete, getDue() };
		        template.executeUpdate(insertQuery, params);
		       	setId(template.getGeneratedId());
		       	logger.info("Database - VToDo new ID=" + getId());
	        }
		}
    }
    
    /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template){
		String deleteQuery;
		if (!isTodo) {
			deleteQuery = "DELETE FROM VEvent WHERE vEventID = ?";
		}else{
			deleteQuery = "DELETE FROM VToDo WHERE vToDoID = ?";
		}		
		if (getId() > 0) {
			Object params[] = {	getId() };		
			template.executeUpdate(deleteQuery, params);
			setId(-1);
			super.delete(template);
		}
	}
	
	
	
	
	
	
	
	
	/**
     * @param newVal
     */
    public void setPriority(int newVal) {
        priority = newVal;
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
    
    public String getTransparency() {
        return transparency;
    }

    /**
     * @param newVal
     */
    public void setTransparency(String newVal) {
        transparency = newVal;
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
    
    /**
     * @param
     * 
     * konverze z Todo na Event a naopak
     */
    public void convert() {
        // TODO: napsat kod
        // ....
        // delete ***
        // change e/t
        // save ***
        this.isTodo = !this.isTodo;
    }    

	/*
	 * pomocna funkce, TODO: asi zmenim Timestamp na Date
	 */
	private Timestamp getDue(){
		
		return new Timestamp(getEndDate().getTime());
	}

	
	public void setIsTodo(boolean isTodo) {
		this.isTodo = isTodo; 
	}

	public boolean getIsTodo() {
		return (this.isTodo); 
	}
}
