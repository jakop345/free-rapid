package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PeriodsEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PropertyEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DistinctDatesEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VAlarmEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DurationEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DateTimeEntity;



import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:43
 * <p/>
 * Spolecna nadtrida eventu, tasku atd..
 */
public class CalComponent extends DbElement {
    private final static Logger logger = Logger.getLogger(CalComponent.class.getName());

    private final static String PROPERTYNAME_UID = "uid";
    private final static String PROPERTYNAME_URL = "url";
    private final static String PROPERTYNAME_CLAZZ = "clazz";
    private final static String PROPERTYNAME_DESCRIPTION = "description";
    private final static String PROPERTYNAME_ORGANIZER = "organizer";
    private final static String PROPERTYNAME_SEQUENCE = "sequence";
    private final static String PROPERTYNAME_STATUS = "status";
    private final static String PROPERTYNAME_SUMMARY = "summary";
    private final static String PROPERTYNAME_COMPONENTID = "componentId";
    private final static String PROPERTYNAME_CALENDARID = "calendarId";


    /**
     * uid - povinny parametr! (globalne unikatni (MAILTO://email@...))
     */
    private String uid = "";
    private String url = "";
    /**
     * clazz klasifikace (PUBLIC/PRIVATE...)
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
    private int componentId;
     /**/
    //private int calendarId; nahrazeno vcalendar
    private VCalendar vcalendar;

    private List<VAlarmEntity> alarms; //TODO VAlarmEntity / VAlarm

    private Categories _categories;

    /*
    * nasobne properties
    */
    @Deprecated
    private List<Category> categories;

    private List<Comment> comments;
    private List<Contact> contacts;
    private List<Attachment> attachments;
    private List<RelatedTo> relatedto;
    private List<Resource> resources;

    //private DateTime dateTime;
    private DateTime dateTime;

    public CalComponent() {
        dateTime = new DateTime();
        _categories = new Categories();
    }

    public void setComponentId(int componentId) {
        int oldVal = getComponentId();
        this.componentId = componentId;
        _categories.setComponentId(componentId);
        firePropertyChange(PROPERTYNAME_COMPONENTID, oldVal, componentId);
    }

    public int getComponentId() {
        return (this.componentId);
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
        if (newVal == null)
            throw new IllegalArgumentException("Uid cannot be null!");
        final String oldVal = getUid();
        uid = newVal;
        firePropertyChange(PROPERTYNAME_UID, oldVal, newVal);
    }

    public String getUrl() {
        return url;
    }

    /**
     * @param newVal
     */
    public void setUrl(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Url cannot be null!");
        final String oldVal = getUrl();
        url = newVal;
        firePropertyChange(PROPERTYNAME_URL, oldVal, newVal);
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
        if (newVal == null)
            throw new IllegalArgumentException("Clazz cannot be null!");
        final String oldVal = getClazz();
        clazz = newVal;
        firePropertyChange(PROPERTYNAME_CLAZZ, oldVal, newVal);
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
        if (newVal == null)
            throw new IllegalArgumentException("Description cannot be null!");
        final String oldVal = getDescription();
        description = newVal;
        firePropertyChange(PROPERTYNAME_DESCRIPTION, oldVal, newVal);
    }

    public String getOrganizer() {
        return organizer;
    }

    /**
     * @param newVal
     */
    public void setOrganizer(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Organizer cannot be null!");
        final String oldVal = getOrganizer();
        organizer = newVal;
        firePropertyChange(PROPERTYNAME_ORGANIZER, oldVal, newVal);
    }

    public int getSequence() {
        return sequence;
    }

    /**
     * @param newVal
     */
    public void setSequence(int newVal) {
        final int oldVal = getSequence();
        sequence = newVal;
        firePropertyChange(PROPERTYNAME_SEQUENCE, oldVal, newVal);
    }

    public String getStatus() {
        return status;
    }

    /**
     * @param newVal
     */
    public void setStatus(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Status cannot be null!");
        final String oldVal = getStatus();
        status = newVal;
        firePropertyChange(PROPERTYNAME_STATUS, oldVal, newVal);
    }

    public String getSummary() {
        return summary;
    }

    /**
     * @param newVal
     */
    public void setSummary(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Summary cannot be null!");
        final String oldVal = getSummary();
        summary = newVal;
        firePropertyChange(PROPERTYNAME_SUMMARY, oldVal, newVal);
    }

    public Date getRecurrenceId() {
        return new Date(recurrenceid.getTime());
    }

    /**
     * @param newVal
     */
    public void setRecurrenceId(Date newVal) {
        recurrenceid = (newVal == null ? null : new Timestamp(newVal.getTime()));
    }

    public Date getDTimestamp() {
        return (dtstamp == null ? null : new Date(dtstamp.getTime()));
    }

    /**
     * @param newVal
     */
    public void setDTimestamp(Date newVal) {
        dtstamp = (newVal == null ? null : new Timestamp(newVal.getTime()));
    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template Ulozeni spolecnych udaju z Todo, Eventu,.. do databaze
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        assert vcalendar != null;
        assert vcalendar.getId() > 0;

        if (dateTime != null) {
            dateTime.saveOrUpdate(template);
        }

        if (getComponentId() > 0) {
            logger.info("Database - Update: CalComponent[" + getId() + "]...");
            //TODO : increment sequence
            Object params[] = {
                    dateTime.getId(), uid, vcalendar.getId(), url, clazz,
                    description, organizer, sequence,
                    status, summary, dtstamp, getComponentId()};
            String updateQuery = "UPDATE CalComponent SET dateTimeID=?,uid=?,vCalendarID=?,url=?,clazz=?,description=?,organizer=?,sequence=?,status=?,summary=?,dtstamp=?) WHERE calComponentID = ? ";

            template.executeUpdate(updateQuery, params);


        } else {
            if (alarms != null) {
                for (VAlarmEntity alarm : alarms) {
                    ((VAlarm)alarm).setComponentId(componentId);
                    ((VAlarm)alarm).saveOrUpdate(template);
                }
            }
            logger.info("Database - Insert: CalComponent[]...");
            Object params[] = {
                    dateTime.getId(), uid, vcalendar.getId(), url, clazz,
                    description, organizer, sequence,
                    status, summary, new Timestamp(System.currentTimeMillis())
            };
            String insertQuery = "INSERT INTO CalComponent (dateTimeID,uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            template.executeUpdate(insertQuery, params);

            setComponentId(template.getGeneratedId());
            logger.info("Database - CalComponent new ID=" + getComponentId() + " dtstamp=" + new Timestamp(System.currentTimeMillis()));
        }

        if (categories != null) {
            for (Category c : categories) {
                //c.setComponentId(getComponentId());
                c.saveOrUpdate(template);
            }
        }
        if (_categories != null) {
            _categories.setComponentId(getComponentId());
            _categories.saveOrUpdate(template);
        }
        // TODO: comments
        // TODO: attachments
        // TODO: contacts
        // TODO: related-to
        // TODO: attendees
        // TODO: resources
        // TODO: request-status
        super.saveOrUpdate(template);
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (componentId > 0) {
            if (dateTime != null) {
                dateTime.delete(template);
            }
            if (alarms != null) {
                for (VAlarmEntity alarm : alarms) {
                    ((VAlarm)alarm).delete(template);
                }
            }
            /* nesmaze kategorie prirazene k eventu, pouze propojovaci tabulku */
            if (_categories != null) {
                _categories.delete(template);
            }

            logger.info("Database - DELETE: CalComponent[" + componentId + "]...");
            String deleteQuery = "DELETE FROM CalComponent WHERE calComponentID = ?";
            Object params[] = {componentId};

            template.executeUpdate(deleteQuery, params);

        }
        setComponentId(-1);
    }

    /**
     * Method getAttachments
     * @return
     */
    public List<PropertyEntity> getAttachments() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM Attachment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                Attachment attach = new Attachment();
                attach.setAttach(rs.getString("name"));
                attach.setIsBinary(rs.getInt("isBinary") == 1);
                items.add(attach);
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    /**
     * Method getAttendees
     * @return
     */
    public List<String> getAttendees() {
        // TODO: getAttendees
        return null;
    }

    /**
     * Method getCategories
     * @return
     */
    public List<CategoryEntity> getCategories() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM Category,Categories WHERE Categories.calComponentID=? AND Categories.CategoryId=Category.CategoryId ";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<CategoryEntity>> template = new TimeJugglerJDBCTemplate<List<CategoryEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<CategoryEntity>();
                Category c = new Category(rs.getString("name"));
                c.setId(rs.getInt("categoryID"));
                int col = rs.getInt("color");
                if (!rs.wasNull()) c.setColor(new Color(col));
                items.add(c);
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems() == null ? new ArrayList<CategoryEntity>() : template.getItems();
    }

    @Deprecated
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(CategoryEntity cat) {
        _categories.addCategory((Category)cat);
    }

    public void removeCategory(CategoryEntity cat) {
        _categories.removeCategory((Category)cat);
    }

    /*public void removeCategory(Category cat) {
        _categories.removeCategory(cat);
    }
    */

    /**
     * Method getComments
     * @return
     */
    public List<PropertyEntity> getComments() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM Comment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                items.add(new Comment(rs.getString("comment")));
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    /**
     * Method getContacts
     * @return
     */
    public List<PropertyEntity> getContacts() /*throws DatabaseException */{
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM Contact WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                items.add(new Contact(rs.getString("contact")));
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    /**
     * Method getRelatedTo
     * @return
     */
    public List<PropertyEntity> getRelatedTo() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM RelatedTo WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                items.add(new RelatedTo(rs.getString("relatedto")));
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    /**
     * Method getRequestStatuses
     * @return
     */
    public List<PropertyEntity> getRequestStatuses() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM RequestStatus WHERE calComponentID = ? ";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                items.add(new RequestStatus(rs.getString("rstatus")));
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    /**
     * Method getResources
     * @return
     */
    public List<PropertyEntity> getResources() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
        String sql = "SELECT * FROM Resource WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<PropertyEntity>> template = new TimeJugglerJDBCTemplate<List<PropertyEntity>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<PropertyEntity>();
                items.add(new Resource(rs.getString("resource")));
            }
        };
		try {
			template.executeQuery(sql, params);
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        

        return template.getItems();
    }

    public void setStartDate(Date startDate) {
        dateTime.setStartDate(startDate);
    }

    public void setCreated(Date created) {
        dateTime.setCreated(created);
    }

    public void setLastModified(Date lastModified) {
        dateTime.setLastModified(lastModified);
    }

    public void setPeriods(PeriodsEntity periods) {
        dateTime.setPeriods((Periods)periods);
    }

    public Date getStartDate() {
        return (dateTime.getStartDate());
    }

    public Date getCreated() {
        return (dateTime.getCreated());
    }

    public Date getLastModified() {
        return (dateTime.getLastModified());
    }

    public PeriodsEntity getPeriods() /*throws DatabaseException*/ {
    	//TODO: tady je docasne try, protoze vadi neException v Interface
    	try {
    		return ((PeriodsEntity)dateTime.getPeriods());
	    }
	    catch (DatabaseException ex) {
	    	ex.printStackTrace();
	    }
        return /*treba*/ null; //TODO: :-P
    }

    public void setDistinctDates(DistinctDatesEntity distinctDates) {
        dateTime.setDistinctDates((DistinctDates)distinctDates);
    }

    public DistinctDatesEntity getDistinctDates() {
        return ((DistinctDatesEntity)dateTime.getDistinctDates());
    }

    public void setCalendar(VCalendarEntity cal) {
        vcalendar = (VCalendar)cal;
    }

    public VCalendarEntity getCalendar() {
        return (VCalendarEntity)vcalendar;
    }

    /*
     public void setCalendarId(int calendarId) {
         this.calendarId = calendarId;
     }

     public int getCalendarId() {
         return (this.calendarId);
     }
     */

    public void setAlarms(List<VAlarmEntity> alarms) {
        this.alarms = alarms;
    }

    public List<VAlarmEntity> getAlarms() {
        // TODO: SELECT Alarms
        return (this.alarms);
    }


    public void setEndDate(Date endDate) {
        dateTime.setEndDate(endDate);
    }

    public void setEndDate(DurationEntity dur) {
        dateTime.setEndDate((Duration)dur);
    }

    public Date getEndDate() {
        return (dateTime.getEndDate());
    }


    public void setDateTime(DateTimeEntity dateTime) {
        this.dateTime = (DateTime)dateTime;
    }

    public DateTimeEntity getDateTime() {
        return ((DateTimeEntity)this.dateTime);
    }
}