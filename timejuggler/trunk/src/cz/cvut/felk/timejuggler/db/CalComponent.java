package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:43
 */
public class CalComponent extends DbElement {

    /**
     * povinny parametr! (globalne unikatni (MAILTO://email@...))
     */
    private String uid;
    private String url;
    /**
     * klasifikace (PUBLIC/PRIVATE...)
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
    private Vector<VAlarm> alarms;
    
    private DateTime dateTime;

    public CalComponent() {

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

	 /**
     * Method saveOrUpdate
     * @param template
     */	
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
    	if (dateTime != null) {
    		dateTime.saveOrUpdate(template);
    	}

    	if (getComponentId() > 0) {
    		//TODO : increment sequence
			Object params[] = {
	                dateTime.getId(), uid, calendarId, url, clazz,
	                description, organizer, sequence,
	                status, summary, dtstamp, getComponentId())
	        };
	        String updateQuery = "UPDATE CalComponent SET dateTimeID=?,uid=?,vCalendarID=?,url=?,clazz=?,description=?,organizer=?,sequence=?,status=?,summary=?,dtstamp=?) WHERE calComponentID = ? ";
	        template.executeUpdate(updateQuery, params);
    	}else{	    	
	    	if (alarms != null) {
	    		for (VAlarm alarm : alarms) {
	    			alarm.setComponentId(componentId);
	    			alarm.saveOrUpdate(template);
	    		}
	    	}
	    	
			Object params[] = {
	                dateTime.getId(), uid, calendarId, url, clazz,
	                description, organizer, sequence,
	                status, summary, new Timestamp(new Date().getTime())
	        };
	        String insertQuery = "INSERT INTO CalComponent (dateTimeID,uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	        template.executeUpdate(insertQuery, params);
	        setComponentId(template.getGeneratedId()) ;
    	}
    }
    
   	 /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
    	if (dateTime != null) {
    		dateTime.delete(template);
    	}
		if (alarms != null) {
			for (VAlarm alarm : alarms) {
				alarm.delete(template);
			}
		}
		    	    	
    	if (getId() > 0) {
			String deleteQuery = "DELETE FROM CalComponent WHERE calComponentID = ?";
			Object params[] = {	getId() };
			template.executeUpdate(deleteQuery, params);
    	}
    	
		setComponentId(-1);
    }
	/**
	 * Method getAttachments
	 *
	 *
	 * @return
	 *
	 */
	public Vector<Attachment> getAttachments() {
        String sql = "SELECT * FROM Attachment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<Attachment> template = new TimeJugglerJDBCTemplate<Attachment>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	Attachment attach = new Attachment();
            	attach.setAttach(rs.getString("name"));
            	attach.setIsBinary(rs.getInt("isBinary") == 1 ? true : false);
                items.add(attach);
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getAttendees
	 *
	 *
	 * @return
	 *
	 */
	public Vector<String> getAttendees() {
		// TODO: Add your code here
		return null;
	}

	/**
	 * Method getCategories
	 *
	 *
	 * @return
	 *
	 */
	public Vector<Category> getCategories() {
        String sql = "SELECT * FROM Category WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<Category> template = new TimeJugglerJDBCTemplate<Category>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new Category(rs.getString("name")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getComments
	 *
	 *
	 * @return
	 *
	 */
	public Vector<Comment> getComments() {
        String sql = "SELECT * FROM Comment WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<Comment> template = new TimeJugglerJDBCTemplate<Comment>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new Comment(rs.getString("comment")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getContacts
	 *
	 *
	 * @return
	 *
	 */
	public Vector<Contact> getContacts() {
        String sql = "SELECT * FROM Contact WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<Contact> template = new TimeJugglerJDBCTemplate<Contact>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new Contact(rs.getString("contact")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getRelatedTo
	 *
	 *
	 * @return
	 *
	 */
	public Vector<RelatedTo> getRelatedTo() {
        String sql = "SELECT * FROM RelatedTo WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<RelatedTo> template = new TimeJugglerJDBCTemplate<RelatedTo>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new RelatedTo(rs.getString("relatedto")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getRequestStatuses
	 *
	 *
	 * @return
	 *
	 */
	public Vector<RequestStatus> getRequestStatuses() {
        String sql = "SELECT * FROM RequestStatus WHERE calComponentID = ? ";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<RequestStatus> template = new TimeJugglerJDBCTemplate<RequestStatus>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new RequestStatus(rs.getString("rstatus")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	/**
	 * Method getResources
	 *
	 *
	 * @return
	 *
	 */
	public Vector<Resource> getResources() {
        String sql = "SELECT * FROM Resource WHERE calComponentID = ?";
        Object params[] = {getComponentId()};
        TimeJugglerJDBCTemplate<Resource> template = new TimeJugglerJDBCTemplate<Resource>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                items.add(new Resource(rs.getString("resource")));
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
	}

	
/*	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime; 
	}*/

/*	public DateTime getDateTime() {
		return (this.dateTime); 
	}*/

	public void setStartDate(Date startDate) {
		dateTime.setStartDate(startDate); 
	}

	public void setCreated(Timestamp created) {
		dateTime.setCreated(created); 
	}

	public void setLastModified(Timestamp lastModified) {
		dateTime.setLastModified(lastModified); 
	}

	public void setPeriods(Periods periods) {
		dateTime.setPeriods(periods); 
	}

	public Date getStartDate() {
		return (dateTime.getStartDate()); 
	}

	public Timestamp getCreated() {
		return (dateTime.getCreated()); 
	}

	public Timestamp getLastModified() {
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

	
	public void setAlarms(Vector<VAlarm> alarms) {
		this.alarms = alarms; 
	}

	public Vector<VAlarm> getAlarms() {
		return (this.alarms); 
	}

	
	public void setEndDate(Date endDate) {
		dateTime.setEndDate(endDate);
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