package cz.cvut.felk.timejuggler.swing.calendar;

import java.awt.Graphics;

import javax.swing.JComponent;

import cz.cvut.felk.timejuggler.entity.CalendarEvent;

/**
 * Tato trida zapouzdruje udalost v kalendari do graficke komponenty
 * 
 * 
 * @author Jerry!
 *
 */
public class CalendarGridEvent extends JComponent {

	private static final long serialVersionUID = 1L;
	
	CalendarEvent calendarEvent = null;

	public CalendarGridEvent(CalendarEvent calendarEvent) {
		super();
		this.calendarEvent = calendarEvent;
	}

	@Override
	public void paint(Graphics g) {
		//TODO jak budem kreslit? Zatim udelej obdelnicek Jerry!
		g.drawRect(0,0,this.getWidth()-1,this.getHeight()-1);
		g.drawLine(0,0,this.getWidth()-1,this.getHeight()-1);
		g.drawLine(0,this.getHeight()-1,this.getWidth()-1,0);
		super.paint(g);
	}

	public CalendarEvent getCalendarEvent() {
		return calendarEvent;
	}	
	
}
