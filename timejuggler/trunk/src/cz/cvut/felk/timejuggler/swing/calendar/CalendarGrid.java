package cz.cvut.felk.timejuggler.swing.calendar;

import java.awt.Component;
import java.util.Calendar;
import java.awt.Graphics;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import cz.cvut.felk.timejuggler.dao.CalendarEventDAO;
import cz.cvut.felk.timejuggler.entity.CalendarEvent;

/**
 * Komponenta zprostredkovavajici grafickou reprezentaci kalendarnich udalosti,
 * v kontextu celku.
 * 
 * @author Jerry!
 */
public class CalendarGrid extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private CalendarView calendarView = CalendarView.DAY;
	
	private CalendarEventDAO calendarEventDAO = null;
	
	private CalendarConfig calendarConfig = null;
	
	private Date startDate = null;
	
	private Map<CalendarEvent, CalendarGridEvent> calendarEvents = new HashMap<CalendarEvent, CalendarGridEvent>();

	public CalendarGrid(CalendarEventDAO calendarEventDAO, CalendarConfig calendarConfig) {
		super();
		this.calendarEventDAO = calendarEventDAO;
		this.calendarConfig = calendarConfig;
	}
		
	@Override
	protected void paintComponent(Graphics g) {
		recountCalendarEvents();
		switch (calendarView) {
			case DAY:
				paintCalendarDay(g);
				break;
			case WEEK:
				paintCalendarWeek(g);
				break;
			case MULTI_WEEK:
				paintCalendarMultiWeek(g);
				break;
			case MONTH:
				paintCalendarMonth(g);
				break;
			default:
				throw new RuntimeException("UnrealException");
		}
		super.paintComponent(g);
	}

	/**
	 * Aktualizuje mnozinu udalosti, ktere zobrazuje podle dat z DAO
	 *
	 */
	public void refreshCalendarEvents() {
		Date endDate = null;
		switch (calendarView) {
			case DAY:
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.roll(Calendar.DATE, true);
				c.set(Calendar.HOUR_OF_DAY,0);
				c.set(Calendar.MINUTE,0);
				endDate = c.getTime();
				break;
			case WEEK:
				break;
			case MULTI_WEEK:
				break;
			case MONTH:
				break;
			default:
				throw new RuntimeException("UnrealException");
		}				
		Set<CalendarEvent> nove = calendarEventDAO.getCalendarEvents(startDate, endDate);
		
		// TODO: tady to mergnout, zatim brutalne vykosim stavajici a udelam nove Jerry!
		for (CalendarGridEvent e : calendarEvents.values()) {
			this.remove(e);
		}
		calendarEvents.clear();
		for (CalendarEvent e : nove) {
			CalendarGridEvent ce = new CalendarGridEvent(e);
			calendarEvents.put(e,ce);
			this.add(ce);
		}
		recountCalendarEvents();
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti
	 */
	protected boolean recountCalendarEvents() {
		boolean result = false;
		for (Component component: calendarEvents.values()) {
			if (component instanceof CalendarGridEvent) {
				CalendarGridEvent calendarEvent = (CalendarGridEvent) component;
				switch (calendarView) {
					case DAY:
						result = recountCalendarEventDay(calendarEvent) || result;
						break;
					case WEEK:
						result = recountCalendarEventWeek(calendarEvent) || result;
						break;
					case MULTI_WEEK:
						result = recountCalendarEventMultiWeek(calendarEvent) || result;
						break;
					case MONTH:
						result = recountCalendarEventMonth(calendarEvent) || result;
						break;
					default:
						throw new RuntimeException("UnrealException");
				}				
			}
		}
		return result;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.DAY
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventDay(CalendarGridEvent event) {
		int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
		float minuteSize = new Float(this.getHeight()-1) / (hoursCount*60);
		float hourSize = new Float(this.getHeight()-1) / hoursCount;
		int topTime = (event.getCalendarEvent().getStartDate().getHours()-calendarConfig.getDayStartTime());
		int bottomTime = (event.getCalendarEvent().getEndDate().getHours()-event.getCalendarEvent().getStartDate().getHours());
		int top = Math.round(topTime*hourSize+event.getCalendarEvent().getStartDate().getMinutes()*minuteSize);
		int bottom = Math.round(bottomTime*hourSize+(event.getCalendarEvent().getEndDate().getMinutes()-event.getCalendarEvent().getStartDate().getMinutes())*minuteSize);
		
		boolean result = false;
		if (event.getY() != top)
			result = true || result;
		if (event.getHeight() != bottom)
			result = true || result;
		if (result) {
			event.setLocation(50,top); //event.getX()
			event.setSize(50, bottom); //event.getWidth() jak to bude se sirkou pri vice udalostech ?
		}
		return result;
	}

	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.WEEK
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventWeek(CalendarGridEvent event) {
		// TODO vypln si to tu! Jerry!		
		return false;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.MULTI_WEEK
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventMultiWeek(CalendarGridEvent event) {
		// TODO vypln si to tu! Jerry!		
		return false;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.MONTH
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventMonth(CalendarGridEvent event) {
		// TODO vypln si to tu! Jerry!		
		return false;
	}
	
	/**
	 * Jak bude probihat vykreslovani v pripade CalendarView.DAY
	 */
	protected void paintCalendarDay(Graphics g) {
		int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
		float hourSize = this.getHeight() / hoursCount;
		
		for (int i=0; i<hoursCount; i++) {
			int p = Math.round(i*hourSize);
			g.drawString(Integer.toString((i+calendarConfig.getDayStartTime())) ,0, p + g.getFont().getSize());
			g.drawLine(0, p, this.getWidth(), p);
		}
	}
	
	/**
	 * Jak bude probihat vykreslovani v pripade CalendarView.WEEK
	 */
	protected void paintCalendarWeek(Graphics g) {
		// TODO vypln si to tu! Jerry!		
	}
		
	/**
	 * Jak bude probihat vykreslovani v pripade CalendarView.MULTI_WEEK
	 */
	protected void paintCalendarMultiWeek(Graphics g) {
		// TODO vypln si to tu! Jerry!
	}

	/**
	 * Jak bude probihat vykreslovani v pripade CalendarView.MONTH
	 */
	protected void paintCalendarMonth(Graphics g) {
		// TODO vypln si to tu! Jerry!		
	}
	
	public CalendarView getCalendarView() {
		return calendarView;
	}

	public void setCalendarView(CalendarView calendarView) {
		this.calendarView = calendarView;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
