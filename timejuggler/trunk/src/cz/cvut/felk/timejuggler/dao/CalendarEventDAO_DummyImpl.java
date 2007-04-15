package cz.cvut.felk.timejuggler.dao;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import cz.cvut.felk.timejuggler.entity.CalendarEvent;

/**
 * Moje testovaci dummy implementace
 * 
 * @author Jerry!
 *
 */
public class CalendarEventDAO_DummyImpl implements CalendarEventDAO{
	private Set<CalendarEvent> data = new HashSet<CalendarEvent>();

	public Set<CalendarEvent> getCalendarEvents(Date startDate, Date endDate) {
		Set<CalendarEvent> result = new HashSet<CalendarEvent>();
		for (CalendarEvent e : data) {
			if (e.getStartDate().before(endDate)&&(e.getStartDate().after(startDate)) ||
				e.getEndDate().before(endDate)&&(e.getEndDate().after(startDate))	) {
				result.add(e);
			}
		}
		return result;
	}

	public void saveCalendarEvent(CalendarEvent calendarEvent) {
		data.add(calendarEvent);
	}
	
	
}
