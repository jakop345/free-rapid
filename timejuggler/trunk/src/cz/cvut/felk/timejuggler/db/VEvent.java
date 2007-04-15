package cz.cvut.felk.timejuggler.db;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:35
 */
public class VEvent extends CalComponent {

    private String geoGPS = "";
    private String location = "";
    private int priority = 0;
    private String transparency = "";
    //public Categories m_Categories;
    //public Alarms m_Alarms;
    //public ComponentDetails m_ComponentDetails;
    //public CalComponent m_CalComponent;

    public VEvent() {

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