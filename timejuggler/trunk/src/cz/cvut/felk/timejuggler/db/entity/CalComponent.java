package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DateTimeEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PeriodsEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DistinctDatesEntity;

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

    /**
     * uid - povinny parametr! (globalne unikatni (MAILTO://email@...))
     */
    private String uid;
    private String url;
    /**
     * clazz klasifikace (PUBLIC/PRIVATE...)
     */
    private String clazz;
    /**
     * popis komponenty
     */
    private String description;
    private String organizer;
    private int sequence = 0;
    private String status;
    private String summary;
    private Timestamp recurrenceid;
    private Timestamp dtstamp;
    private int componentId;
    private int calendarId;
    private List<VAlarm> alarms;

    private Categories _categories;

    /*
    * nasobne properties
    */
    @Deprecated private List<Category> categories;

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
        this.componentId = componentId;
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
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {

        if (dateTime != null) {
            dateTime.saveOrUpdate(template);
        }


        if (getComponentId() > 0) {
            logger.info("Database - Update: CalComponent[" + getId() + "]...");
            //TODO : increment sequence
            Object params[] = {
                    dateTime.getId(), uid, calendarId, url, clazz,
                    description, organizer, sequence,
                    status, summary, dtstamp, getComponentId()};
            String updateQuery = "UPDATE CalComponent SET dateTimeID=?,uid=?,vCalendarID=?,url=?,clazz=?,description=?,organizer=?,sequence=?,status=?,summary=?,dtstamp=?) WHERE calComponentID = ? ";
            template.executeUpdate(updateQuery, params);
        } else {
            if (alarms != null) {
                for (VAlarm alarm : alarms) {
                    alarm.setComponentId(componentId);
                    alarm.saveOrUpdate(template);
                }
            }
            logger.info("Database - Insert: CalComponent[]...");
            Object params[] = {
                    dateTime.getId(), uid, calendarId, url, clazz,
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
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            if (dateTime != null) {
                dateTime.delete(template);
            }
            if (alarms != null) {
                for (VAlarm alarm : alarms) {
                    alarm.delete(template);
                }
            }
            logger.info("Database - DELETE: CalComponent[" + getId() + "]...");
            String deleteQuery = "DELETE FROM CalComponent WHERE calComponentID = ?";
            Object params[] = {getId()};
            template.executeUpdate(deleteQuery, params);
        }
        setComponentId(-1);
    }

    /**
     * Method getAttachments
     * @return
     */
    public List<Attachment> getAttachments() {
        String sql = "SELECT * FROM Attachment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<Attachment>> template = new TimeJugglerJDBCTemplate<List<Attachment>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<Attachment>();
                Attachment attach = new Attachment();
                attach.setAttach(rs.getString("name"));
                attach.setIsBinary(rs.getInt("isBinary") == 1);
                items.add(attach);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getAttendees
     * @return
     */
    public List<String> getAttendees() {
        // TODO: Add your code here
        return null;
    }

    /**
     * Method getCategories
     * @return
     */
    public List<Category> getCategories() {
        String sql = "SELECT * FROM Category,Categories WHERE Categories.calComponentID=? ";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<Category>> template = new TimeJugglerJDBCTemplate<List<Category>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new ArrayList<Category>();
            	Category c = new Category(rs.getString("name"));
            	c.setId(rs.getInt("categoryID"));
            	int col = rs.getInt("color");
            	if (!rs.wasNull()) c.setColor(new Color(col));
                items.add(c);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
	}
	
	@Deprecated public void setCategories(List<Category> categories){
		this.categories = categories;
	}
	
	public void addCategory(Category cat){
		_categories.addCategory(cat);
	}
	
	public void removeCategory(Category cat){
		_categories.removeCategory(cat);
	}	

    /*public void removeCategory(Category cat) {
        _categories.removeCategory(cat);
    }
    */

    /**
     * Method getComments
     * @return
     */
    public List<Comment> getComments() {
        String sql = "SELECT * FROM Comment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<Comment>> template = new TimeJugglerJDBCTemplate<List<Comment>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<Comment>();
                items.add(new Comment(rs.getString("comment")));
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getContacts
     * @return
     */
    public List<Contact> getContacts() {
        String sql = "SELECT * FROM Contact WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<Contact>> template = new TimeJugglerJDBCTemplate<List<Contact>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<Contact>();
                items.add(new Contact(rs.getString("contact")));
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getRelatedTo
     * @return
     */
    public List<RelatedTo> getRelatedTo() {
        String sql = "SELECT * FROM RelatedTo WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<RelatedTo>> template = new TimeJugglerJDBCTemplate<List<RelatedTo>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<RelatedTo>();
                items.add(new RelatedTo(rs.getString("relatedto")));
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getRequestStatuses
     * @return
     */
    public List<RequestStatus> getRequestStatuses() {
        String sql = "SELECT * FROM RequestStatus WHERE calComponentID = ? ";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<RequestStatus>> template = new TimeJugglerJDBCTemplate<List<RequestStatus>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<RequestStatus>();
                items.add(new RequestStatus(rs.getString("rstatus")));
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getResources
     * @return
     */
    public List<Resource> getResources() {
        String sql = "SELECT * FROM Resource WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<List<Resource>> template = new TimeJugglerJDBCTemplate<List<Resource>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                if (items == null) items = new ArrayList<Resource>();
                items.add(new Resource(rs.getString("resource")));
            }
        };
        template.executeQuery(sql, params);
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

    public void setPeriods(Periods periods) {
        dateTime.setPeriods(periods);
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

    public Periods getPeriods() {
        return (dateTime.getPeriods());
    }

    public void setDistinctDates(DistinctDates distinctDates) {
        dateTime.setDistinctDates(distinctDates);
    }

    public DistinctDates getDistinctDates() {
        return (dateTime.getDistinctDates());
    }


    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    public int getCalendarId() {
        return (this.calendarId);
    }


    public void setAlarms(List<VAlarm> alarms) {
        this.alarms = alarms;
    }

    public List<VAlarm> getAlarms() {
        // TODO: SELECT Alarms
        return (this.alarms);
    }


    public void setEndDate(Date endDate) {
        dateTime.setEndDate(endDate);
    }

    public void setEndDate(Duration dur) {
        dateTime.setEndDate(dur);
    }

    public Date getEndDate() {
        return (dateTime.getEndDate());
    }


    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public DateTime getDateTime() {
        return (this.dateTime);
    }
}