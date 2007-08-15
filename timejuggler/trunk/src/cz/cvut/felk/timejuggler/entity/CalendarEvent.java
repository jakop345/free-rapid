package cz.cvut.felk.timejuggler.entity;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;

/**
 * Trida predstavujici udalost v kalendari
 * 
 * @author Jerry!
 *
 */
public class CalendarEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date startDate;
	private Date endDate;
	private String name;
	private Color color;
	
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
