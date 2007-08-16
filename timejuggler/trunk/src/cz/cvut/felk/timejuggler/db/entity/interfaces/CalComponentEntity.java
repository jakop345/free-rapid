package cz.cvut.felk.timejuggler.db.entity.interfaces;

import java.util.Date;
import java.util.List;

import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.DateTime;
import cz.cvut.felk.timejuggler.db.entity.Periods;
import cz.cvut.felk.timejuggler.db.entity.DistinctDates;
import cz.cvut.felk.timejuggler.db.entity.VAlarm;
import cz.cvut.felk.timejuggler.db.entity.Duration;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.Comment;
import cz.cvut.felk.timejuggler.db.entity.Contact;
import cz.cvut.felk.timejuggler.db.entity.RelatedTo;
import cz.cvut.felk.timejuggler.db.entity.RequestStatus;
import cz.cvut.felk.timejuggler.db.entity.Resource;
import cz.cvut.felk.timejuggler.db.entity.Attachment;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * 
 */

public interface CalComponentEntity extends EntityElement {
	
	void setComponentId(int componentId);	//TODO ??
	int getComponentId();		//TODO ??
	
	String getUid();
	void setUid(String newVal);
	String getUrl();
	void setUrl(String newVal);
	String getClazz();
	void setClazz(String newVal);
	String getDescription();
	void setDescription(String newVal);
	String getOrganizer();
	void setOrganizer(String newVal);
	int getSequence();
	void setSequence(int newVal);
	String getStatus();
	void setStatus(String newVal);
	String getSummary();
	void setSummary(String newVal);
	Date getRecurrenceId();
	void setRecurrenceId(Date newVal);
	Date getDTimestamp();
	void setDTimestamp(Date newVal);
	
	List<String> getAttendees();
	List<Category> getCategories();
	void addCategory(Category cat);
	void removeCategory(Category cat);

	/*List<PropertyEntity> getComments();
	List<PropertyEntity> getContacts();
	List<PropertyEntity> getRelatedTo();
	List<PropertyEntity> getRequestStatuses();
	List<PropertyEntity> getResources();*/
	
	List<Attachment> getAttachments();
	List<Comment> getComments();
	List<Contact> getContacts();
	List<RelatedTo> getRelatedTo();
	List<RequestStatus> getRequestStatuses();
	List<Resource> getResources();

	void setStartDate(Date startDate);
	void setCreated(Date created);
	void setLastModified(Date lastModified);
	void setPeriods(Periods periods);
	Date getStartDate();
	Date getCreated();
	Date getLastModified();
	Periods getPeriods();
	void setDistinctDates(DistinctDates distinctDates);
	DistinctDates getDistinctDates();
	
	/* 
	void setCalendarId(int calendarId);	//TODO ??
	int getCalendarId();	//TODO ??
	
	nahrazeno set/get Calendar */
	void setCalendar(VCalendar cal);
	VCalendar getCalendar();

	
	void setAlarms(List<VAlarm> alarms);
	List<VAlarm> getAlarms();
	void setEndDate(Date endDate);
	void setEndDate(Duration dur);
	Date getEndDate();
	void setDateTime(DateTime dateTime);
	DateTime getDateTime();
}
