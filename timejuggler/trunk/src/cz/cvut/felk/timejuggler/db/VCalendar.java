package cz.cvut.felk.timejuggler.db;

/**
 * @version 0.1
 * @created 14-IV-2007 16:38:21
 */
public class VCalendar extends DbElement {

    private String productId = "-//CVUT //TimeJuggler Calendar 0.1//CZ";
    private String version = "2.0";
    private String calendarScale = "GREGORIAN";
    private String method = "PUBLISH";
    private String name = "";

//	private Vector events;

    public VCalendar() {

    }

    public VCalendar(int id) {
        super(id);
    }

    public VCalendar(String name) {
        this.name = name;
    }

    public void store() {

    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVersion() {
        return version;
    }

    /**
     * @param newVal
     */
    public void setVersion(String newVal) {
        version = newVal;
    }

    public String getCalendarScale() {
        return calendarScale;
    }

    /**
     * @param newVal
     */
    public void setCalendarScale(String newVal) {
        calendarScale = newVal;
    }

    public String getMethod() {
        return method;
    }

    /**
     * @param newVal
     */
    public void setMethod(String newVal) {
        method = newVal;
    }

    public String getName() {
        return name;
    }

    /**
     * @param newVal
     */
    public void setName(String newVal) {
        name = newVal;
    }

}