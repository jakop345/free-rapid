package cz.cvut.felk.timejuggler.swing.components.calendar;

import cz.cvut.felk.timejuggler.core.domain.DateInterval;
import cz.cvut.felk.timejuggler.entity.CalendarEvent;

public class CalendarGridEventFactoryImpl implements CalendarGridEventFactory {

	@Override
	public CalendarGridEvent createCalendarGridEvent(CalendarEvent calendarEvent, DateInterval dateInterval) {
		return new CalendarGridEvent(calendarEvent, dateInterval);
	}

}
