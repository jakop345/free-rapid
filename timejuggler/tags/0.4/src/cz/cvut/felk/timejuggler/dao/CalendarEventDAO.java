package cz.cvut.felk.timejuggler.dao;

import java.util.Set;
import java.util.Date;

import cz.cvut.felk.timejuggler.entity.CalendarEvent;


/**
 * 
 * Tridy implementujici toto rozhrani zajistuji pristup
 * k datovym skladum a operace nad daty.
 * 
 * @author Jerry!
 *
 */
public interface CalendarEventDAO {
	
	/**
	 * Vraci mnozinu udalosti v intervalu danem parametry 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Set<CalendarEvent> getCalendarEvents(Date startDate, Date endDate);
	
	public void saveCalendarEvent(CalendarEvent calendarEvent);
}
