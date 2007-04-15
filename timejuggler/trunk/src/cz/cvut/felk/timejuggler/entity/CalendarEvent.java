package cz.cvut.felk.timejuggler.entity;

import java.util.Date;

/**
 * Trida predstavujici udalost v kalendari
 * 
 * @author Jerry!
 *
 */
public class CalendarEvent {
	private Date startDate;
	private Date endDate;
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date stopDate) {
		this.endDate = stopDate;
	}
}
