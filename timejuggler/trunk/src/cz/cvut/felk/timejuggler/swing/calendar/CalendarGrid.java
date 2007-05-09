package cz.cvut.felk.timejuggler.swing.calendar;

import java.awt.Component;
import java.util.Calendar;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.dnd.*;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

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
	
	// layout ?
	int headerPadding = 15;

	int hourPadding = 30;
	
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
		Calendar calendar = Calendar.getInstance();
		switch (calendarView) {
			case DAY:
				calendar.setTime(startDate);
				calendar.roll(Calendar.DATE, true);
				calendar.set(Calendar.HOUR_OF_DAY,0);
				calendar.set(Calendar.MINUTE,0);
				endDate = calendar.getTime();
				break;
			case WEEK:
				calendar.setTime(startDate);
				calendar.roll(Calendar.DATE, 7);
				calendar.set(Calendar.HOUR_OF_DAY,0);
				calendar.set(Calendar.MINUTE,0);
				endDate = calendar.getTime();
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
			
			DragSource ds = DragSource.getDefaultDragSource();
			DragGestureListener dgl = new DragGestureListener() {
				public void dragGestureRecognized(DragGestureEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0.getDragAction());
					
				}				
			};

			ds.createDefaultDragGestureRecognizer(ce, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
			
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
		switch (calendarView) {
			case DAY:
				result = recountCalendarEventDay();
				break;
			case WEEK:
				result = recountCalendarEventWeek();
				break;
			case MULTI_WEEK:
				result = recountCalendarEventMultiWeek();
				break;
			case MONTH:
				result = recountCalendarEventMonth();
				break;
			default:
				throw new RuntimeException("UnrealException");
		}
		return result;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.DAY
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventDay() {
		int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
		float minuteSize = new Float(this.getHeight()-1) / (hoursCount*60);
		float hourSize = new Float(this.getHeight()-1) / hoursCount;
		int eventNumber = 0;
		int eventsCount = 0;
		boolean result = false;

		for (Component component: calendarEvents.values()) {
			if (component instanceof CalendarGridEvent) {
				eventsCount++;
			}
		}
		
		for (Component component: calendarEvents.values()) {
			if (component instanceof CalendarGridEvent) {
				CalendarGridEvent event = (CalendarGridEvent) component;

				int topTime = (event.getCalendarEvent().getStartDate().getHours()-calendarConfig.getDayStartTime());
				int bottomTime = (event.getCalendarEvent().getEndDate().getHours()-event.getCalendarEvent().getStartDate().getHours());
				int top = Math.round(topTime*hourSize+event.getCalendarEvent().getStartDate().getMinutes()*minuteSize);
				int bottom = Math.round(bottomTime*hourSize+(event.getCalendarEvent().getEndDate().getMinutes()-event.getCalendarEvent().getStartDate().getMinutes())*minuteSize);
				
				int leftPadding = 50;
				int rightPadding = 10;
				int width = (this.getWidth() - leftPadding - rightPadding)/eventsCount;
				
				if (event.getY() != top)
					result = true || result;
				if (event.getHeight() != bottom)
					result = true || result;
				if (result) {
					event.setLocation(leftPadding+eventNumber*width,top); //event.getX()
					event.setSize(width, bottom); //event.getWidth() jak to bude se sirkou pri vice udalostech ?
				}
				eventNumber++;
			}
		}
		return result;
	}

	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.WEEK
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventWeek() {
		int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();

		float hourSize = (this.getHeight()-headerPadding) / hoursCount;
		float daySize = (this.getWidth()-hourPadding)/7;
		float minuteSize = new Float(this.getHeight()-headerPadding) / (hoursCount*60);
		
		boolean result = false;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(calendar.getTime());
		endCalendar.roll(Calendar.DATE, 1);
		
		for (int dayInWeek=0; dayInWeek<7; dayInWeek++) {
			Set<CalendarGridEvent> eventsInDay = new HashSet<CalendarGridEvent>();
			for (Component component: calendarEvents.values()) {
				if (component instanceof CalendarGridEvent) {
					CalendarGridEvent event = (CalendarGridEvent) component;
					Date d = event.getCalendarEvent().getStartDate();
					if (calendar.getTime().before(d) && endCalendar.getTime().after(d)) {
						eventsInDay.add(event);
					}
				}
			}
			int eventNumber = 0;

			for (CalendarGridEvent event : eventsInDay) {
				float width = (daySize)/eventsInDay.size();
				int topTime = (event.getCalendarEvent().getStartDate().getHours()-calendarConfig.getDayStartTime());
				int bottomTime = (event.getCalendarEvent().getEndDate().getHours()-event.getCalendarEvent().getStartDate().getHours());
				int top = Math.round(topTime*hourSize+event.getCalendarEvent().getStartDate().getMinutes()*minuteSize);
				int bottom = Math.round(bottomTime*hourSize+(event.getCalendarEvent().getEndDate().getMinutes()-event.getCalendarEvent().getStartDate().getMinutes())*minuteSize);
				if (event.getY() != top)
					result = true || result;
				if (event.getHeight() != bottom)
					result = true || result;
				if (result) {
					event.setLocation(Math.round(hourPadding+eventNumber*width+daySize*dayInWeek),top+headerPadding); //event.getX()
					event.setSize(Math.round(width), bottom); //event.getWidth() jak to bude se sirkou pri vice udalostech ?
				}
				eventNumber++;
				
			}
			endCalendar.roll(Calendar.DATE, 1);
			calendar.roll(Calendar.DATE, 1);
		}
		return result;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.MULTI_WEEK
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventMultiWeek() {
		// TODO vypln si to tu! Jerry!		
		return false;
	}
	
	/**
	 * Prepocita umisteni komponent typu CalendarEvent
	 * aby odpovidalo skutecnosti v pripade CalendarView.MONTH
	 * @return Zda byla pri prepoctu provedena zmena
	 */
	protected boolean recountCalendarEventMonth() {
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
		int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
		float hourSize = (this.getHeight()-headerPadding) / hoursCount;
		float daySize = (this.getWidth()-hourPadding)/7;
		
		for (int i=0; i<hoursCount; i++) {
			int p = Math.round(i*hourSize);
			g.drawString(Integer.toString((i+calendarConfig.getDayStartTime())) ,0, p + g.getFont().getSize()+headerPadding);
			g.drawLine(0, p+headerPadding, this.getWidth(), p+headerPadding);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.startDate);
		for (int i=0; i<7; i++) {
			int p = Math.round(i*daySize);
			g.drawLine(p+hourPadding, 0, p+hourPadding, this.getHeight());
			String den = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
			g.drawString(den, p+hourPadding+2, g.getFont().getSize());
			calendar.roll(Calendar.DATE, 1);
		}
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
	
	public Date getDateByPosition(int x, int y) {
		Date result = new Date(0);
		switch (calendarView) {
			case DAY:
				int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
				float minuteSize = new Float(this.getHeight()-1) / (hoursCount*60);
				float hourSize = new Float(this.getHeight()-1) / hoursCount;
				int hodin = new Double(Math.ceil(y/hourSize)).intValue();
				int minut = new Double((y-hodin*hourSize)/minuteSize).intValue();
				result.setHours(hodin);
				result.setMinutes(minut);
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
		return result;
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
