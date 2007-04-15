package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:43
 */
public class CalComponent extends DbElement {

    /**
     * povinny parametr! (globalne unikatni (MAILTO://email@...))
     */
    private String uid = "";
    private String url = "";
    /**
     * klasifikace (PUBLIC/PRIVATE...)
     */
    private String clazz = "";
    /**
     * popis komponenty
     */
    private String description = "";
    private String organizer = "";
    private int sequence = 0;
    private String status = "";
    private String summary = "";
    private Timestamp recurrenceid;
    private Timestamp dtstamp;
    /*public DateTime m_DateTime; not implemented */
    /*public VCalendar m_VCalendar; not implemented */

    public CalComponent() {

    }

    public CalComponent(int id) {
        super(id);
    }

    /**
     * povinny parametr! (globalne unikatni (MAILTO://email@...))
     */
    public String getUid() {
        return uid;
    }

    /**
     * povinny parametr! (globalne unikatni (MAILTO://email@...))
     * @param newVal
     */
    public void setUid(String newVal) {
        uid = newVal;
    }

    public String getUrl() {
        return url;
    }

    /**
     * @param newVal
     */
    public void setUrl(String newVal) {
        url = newVal;
    }

    /**
     * klasifikace (PUBLIC/PRIVATE...)
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * klasifikace (PUBLIC/PRIVATE...)
     * @param newVal
     */
    public void setClazz(String newVal) {
        clazz = newVal;
    }

    /**
     * popis komponenty
     */
    public String getDescription() {
        return description;
    }

    /**
     * popis komponenty
     * @param newVal
     */
    public void setDescription(String newVal) {
        description = newVal;
    }

    public String getOrganizer() {
        return organizer;
    }

    /**
     * @param newVal
     */
    public void setOrganizer(String newVal) {
        organizer = newVal;
    }

    public int getSequence() {
        return sequence;
    }

    /**
     * @param newVal
     */
    public void setSequence(int newVal) {
        sequence = newVal;
    }

    public String getStatus() {
        return status;
    }

    /**
     * @param newVal
     */
    public void setStatus(String newVal) {
        status = newVal;
    }

    public String getSummary() {
        return summary;
    }

    /**
     * @param newVal
     */
    public void setSummary(String newVal) {
        summary = newVal;
    }

    public Timestamp getRecurrenceId() {
        return recurrenceid;
    }

    /**
     * @param newVal
     */
    public void setRecurrenceId(Timestamp newVal) {
        recurrenceid = newVal;
    }

    public Timestamp getDTimestamp() {
        return dtstamp;
    }

    /**
     * @param newVal
     */
    public void setDTimestamp(Timestamp newVal) {
        dtstamp = newVal;
    }

    public void store() {

    }

}