package cz.cvut.felk.timejuggler.swing.components.calendar;

import cz.cvut.felk.timejuggler.entity.CalendarEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Tato trida zapouzdruje udalost v kalendari do graficke komponenty
 * @author Jerry!
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
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        g.drawString(calendarEvent.getName(), 5, g.getFont().getSize());
        g.drawString(calendarEvent.getStartDate().toString(), 5, g.getFont().getSize() * 2);
        g.drawString(calendarEvent.getEndDate().toString(), 5, g.getFont().getSize() * 3);
        super.paint(g);
    }

    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }

}
