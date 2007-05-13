package cz.cvut.felk.timejuggler.db;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:35
 */
public class VEvent extends CalComponent {
	//TODO : Logging
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
    	//TODO : Update
    	super.saveOrUpdate(template);		
		
        Object params[] = {
                getComponentId(), geoGPS,
                location, priority, transparency
        };
        String insertQuery = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?)";
        template.executeUpdate(insertQuery, params);        
        setId(template.getGeneratedId());
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