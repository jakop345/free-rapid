package cz.cvut.felk.timejuggler.swing.components.calendar;

import cz.cvut.felk.timejuggler.core.domain.DateInterval;
import cz.cvut.felk.timejuggler.entity.CalendarEvent;

/**
 * Factory pro tvorbu instanci {@link CalendarGridEvent} podle zadanych parametru
 * 
 * @author Jerry!
 *
 */
public interface CalendarGridEventFactory {
	
	/**
	 * Vytvori {@link CalendarGridEvent} podle parametru
	 * 
	 * @param calendarEvent
	 * @param dateInterval
	 * @return
	 */
	public CalendarGridEvent createCalendarGridEvent(CalendarEvent calendarEvent, DateInterval dateInterval);
	
}
