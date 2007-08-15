package cz.cvut.felk.timejuggler.swing.components.calendar;

import cz.cvut.felk.timejuggler.core.domain.DateInterval;
import cz.cvut.felk.timejuggler.dao.CalendarEventDAO;
import cz.cvut.felk.timejuggler.entity.CalendarEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Komponenta zprostredkovavajici grafickou reprezentaci kalendarnich udalosti, v kontextu celku.
 * @author Jerry!
 */
public class CalendarGrid extends JComponent implements ComponentListener {

    private static final long serialVersionUID = 1L;

    private CalendarView calendarView = CalendarView.DAY;

    private CalendarEventDAO calendarEventDAO = null;

    private CalendarConfig calendarConfig = null;

    private Date startDate = null;

    private Set<CalendarGridEvent> calendarEvents = new HashSet<CalendarGridEvent>();

    // layout ?
    int headerPadding = 15;

    int hourPadding = 30;
    
    int monthViewPadding = 8;
    
    public CalendarGrid(CalendarEventDAO calendarEventDAO, CalendarConfig calendarConfig) {
        super();
        this.calendarEventDAO = calendarEventDAO;
        this.calendarConfig = calendarConfig;
        this.addComponentListener(this);
    }

    @Override
    public void paint(Graphics g) {
    	Color color = g.getColor();
    	g.setColor(getBackground());
    	g.fillRect(0, 0, this.getWidth(), this.getHeight());
    	g.setColor(color);
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
        super.paint(g);
    }

	/**
     * Aktualizuje mnozinu udalosti, ktere zobrazuje podle dat z DAO
     */
    public void refreshCalendarEvents() {
        Date endDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        //nastavime koncove datum podle startovniho a typu zobrazeni
        switch (calendarView) {
            case DAY:
                break;
            case WEEK:
                calendar.add(Calendar.DATE, 6);
                break;
            case MULTI_WEEK:
            	if (true) throw new RuntimeException("Implement me!");
                break;
            case MONTH:
            	calendar.add(Calendar.DAY_OF_MONTH, 34);
                break;
            default:
                throw new RuntimeException("UnrealException");
        }
        endDate = calendar.getTime();
        
        Set<CalendarEvent> nove = calendarEventDAO.getCalendarEvents(startDate, endDate);

        // TODO: tady to mergnout, zatim brutalne vykosim stavajici a udelam nove Jerry!
        for (CalendarGridEvent e : calendarEvents) {
            this.remove(e);
        }
        calendarEvents.clear();
        for (CalendarEvent calendarEvent : nove) {
        	DateInterval dateInterval = null;        	
        	switch (calendarView) {
        		case DAY:
        			dateInterval = new DateInterval();
        			dateInterval.setStartDate(calendarEvent.getStartDate());
        			if (endDate.before(calendarEvent.getEndDate())) {
        				dateInterval.setEndDate(endDate);
        			} else {
        				dateInterval.setEndDate(calendarEvent.getEndDate());
        			}
        			pushSingleCalendarGridEvent(calendarEvent, dateInterval);
        			break;
        		case MONTH:
        			// rozdeleni do dnu bude stejny. Pro kresleni mesic se pak pouzije pouze startovni datum
        		case WEEK:
        			Calendar oneCalendarStart = Calendar.getInstance();
        			oneCalendarStart.setTime(calendarEvent.getStartDate());
        			oneCalendarStart.set(Calendar.HOUR_OF_DAY, 0);
        			oneCalendarStart.set(Calendar.MINUTE, 0);
        			oneCalendarStart.set(Calendar.SECOND, 0);

        			Calendar oneCalendarEnd = Calendar.getInstance();
        			oneCalendarEnd.setTime(calendarEvent.getStartDate());
        			oneCalendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        			oneCalendarEnd.set(Calendar.MINUTE, 59);
        			oneCalendarEnd.set(Calendar.SECOND, 59);
        			        			
        			if (calendarEvent.getEndDate().after(oneCalendarEnd.getTime())) {
            			dateInterval = new DateInterval();
        				dateInterval.setStartDate(calendarEvent.getStartDate());
        				dateInterval.setEndDate(oneCalendarEnd.getTime());
        				pushSingleCalendarGridEvent(calendarEvent, dateInterval);
        				oneCalendarStart.add(Calendar.DATE, 1);
        				oneCalendarEnd.add(Calendar.DATE, 1);        				
        				
	        			while (calendarEvent.getEndDate().after(oneCalendarEnd.getTime())) {
	            			dateInterval = new DateInterval();
	        				dateInterval.setStartDate(oneCalendarStart.getTime());
	        				dateInterval.setEndDate(oneCalendarEnd.getTime());
	        				pushSingleCalendarGridEvent(calendarEvent, dateInterval);
	        				
	        				oneCalendarStart.add(Calendar.DATE, 1);
	        				oneCalendarEnd.add(Calendar.DATE, 1);        				
	        			}

	        			dateInterval = new DateInterval();
        				dateInterval.setStartDate(oneCalendarStart.getTime());
        				dateInterval.setEndDate(calendarEvent.getEndDate());
        				pushSingleCalendarGridEvent(calendarEvent, dateInterval);
	        			
        			} else {
            			dateInterval = new DateInterval();
        				dateInterval.setStartDate(calendarEvent.getStartDate());
        				dateInterval.setEndDate(calendarEvent.getEndDate());        				
        				pushSingleCalendarGridEvent(calendarEvent, dateInterval);
        			}
        			break;
        		case MULTI_WEEK:
                	if (true) throw new RuntimeException("Implement me!");
        			break;
        		default:
                    throw new RuntimeException("UnrealException");
        			
        	}
        	
        }
        recountCalendarEvents();
        repaint();
    }

    /**
     * Vytvori jeden {@link CalendarGridEvent} s prislusnym nastavenim
     * 
     * @param calendarEvent
     * @param dateInterval
     */
    private void pushSingleCalendarGridEvent(CalendarEvent calendarEvent, DateInterval dateInterval) {
        CalendarGridEvent ce = new CalendarGridEvent(calendarEvent, dateInterval);

        DragSource ds = DragSource.getDefaultDragSource();
        DragGestureListener dgl = new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent arg0) {
                // TODO Auto-generated method stub
                System.out.println(arg0.getDragAction());

            }
        };

        ds.createDefaultDragGestureRecognizer(ce, DnDConstants.ACTION_COPY_OR_MOVE, dgl);

        calendarEvents.add(ce);
        this.add(ce);    	
    }

    /**
     * Prepocita umisteni komponent typu CalendarEvent aby odpovidalo skutecnosti
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
     * Prepocita umisteni komponent typu CalendarEvent aby odpovidalo skutecnosti v pripade CalendarView.DAY
     * @return Zda byla pri prepoctu provedena zmena
     */
    protected boolean recountCalendarEventDay() {
        int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
        float minuteSize = new Float(this.getHeight() - 1) / (hoursCount * 60);
        float hourSize = new Float(this.getHeight() - 1) / hoursCount;
        int eventNumber = 0;
        int eventsCount = 0;
        boolean result = false;

        for (Component component : calendarEvents) {
            if (component instanceof CalendarGridEvent) {
                eventsCount++;
            }
        }

        Calendar calendarBottom = Calendar.getInstance();
        Calendar calendarTop = Calendar.getInstance();
        for (Component component : calendarEvents) {
            if (component instanceof CalendarGridEvent) {
                CalendarGridEvent event = (CalendarGridEvent) component;

                Date bottomDate = event.getVisibleDateInterval().getEndDate();
                calendarBottom.setTime(bottomDate);
                calendarBottom.set(Calendar.MINUTE, 0);
                calendarBottom.set(Calendar.SECOND, 0);
                calendarBottom.set(Calendar.HOUR_OF_DAY, calendarConfig.getDayEndTime());
                if (calendarBottom.getTime().before(bottomDate)) {
                	bottomDate = calendarBottom.getTime();
                }

                Date topDate = event.getVisibleDateInterval().getStartDate();
                calendarTop.setTime(topDate);
                calendarTop.set(Calendar.MINUTE, 0);
                calendarTop.set(Calendar.SECOND, 0);
                calendarTop.set(Calendar.HOUR_OF_DAY, calendarConfig.getDayStartTime());
                if (calendarTop.getTime().after(topDate)) {
                	topDate = calendarTop.getTime();
                }
                
                int topTime = (topDate.getHours() - calendarConfig.getDayStartTime());                        
                int bottomTime = (bottomDate.getHours() - topDate.getHours());
                int top = Math.round(topTime * hourSize + topDate.getMinutes() * minuteSize);
                int bottom = Math.round(bottomTime * hourSize + (bottomDate.getMinutes() - topDate.getMinutes()) * minuteSize);

                int leftPadding = 50;
                int rightPadding = 10;
                int width = (this.getWidth() - leftPadding - rightPadding) / eventsCount;

                if (event.getY() != top)
                    result = true || result;
                if (event.getHeight() != bottom)
                    result = true || result;
                if (result) {
                    event.setLocation(leftPadding + eventNumber * width, top); //event.getX()
                    event.setSize(width, bottom); //event.getWidth() jak to bude se sirkou pri vice udalostech ?
                }
                eventNumber++;
            }
        }
        return result;
    }

    /**
     * Prepocita umisteni komponent typu CalendarEvent aby odpovidalo skutecnosti v pripade CalendarView.WEEK
     * @return Zda byla pri prepoctu provedena zmena
     */
    protected boolean recountCalendarEventWeek() {
        int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();

        float hourSize = (this.getHeight() - headerPadding) / hoursCount;
        float daySize = (this.getWidth() - hourPadding) / 7;
        float minuteSize = new Float(this.getHeight() - headerPadding) / (hoursCount * 60);

        boolean result = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(calendar.getTime());
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);

        Calendar calendarBottom = Calendar.getInstance();
        Calendar calendarTop = Calendar.getInstance();
        for (int dayInWeek = 0; dayInWeek < 7; dayInWeek++) {
            Set<CalendarGridEvent> eventsInDay = new HashSet<CalendarGridEvent>();
            for (Component component : calendarEvents) {
                if (component instanceof CalendarGridEvent) {
                    CalendarGridEvent event = (CalendarGridEvent) component;
                    Date d = event.getVisibleDateInterval().getStartDate();
                    if ((calendar.getTime().compareTo(d)<=0) && endCalendar.getTime().after(d)) {
                        eventsInDay.add(event);
                    }
                }
            }
            int eventNumber = 0;

            for (CalendarGridEvent event : eventsInDay) {
                Date bottomDate = event.getVisibleDateInterval().getEndDate();
                calendarBottom.setTime(bottomDate);
                calendarBottom.set(Calendar.MINUTE, 0);
                calendarBottom.set(Calendar.SECOND, 0);
                calendarBottom.set(Calendar.HOUR_OF_DAY, calendarConfig.getDayEndTime());
                if (calendarBottom.getTime().before(bottomDate)) {
                	bottomDate = calendarBottom.getTime();
                }

                Date topDate = event.getVisibleDateInterval().getStartDate();
                calendarTop.setTime(topDate);
                calendarTop.set(Calendar.MINUTE, 0);
                calendarTop.set(Calendar.SECOND, 0);
                calendarTop.set(Calendar.HOUR_OF_DAY, calendarConfig.getDayStartTime());
                if (calendarTop.getTime().after(topDate)) {
                	topDate = calendarTop.getTime();
                }

                float width = (daySize) / eventsInDay.size();
                int topTime = (topDate.getHours() - calendarConfig.getDayStartTime());
                int bottomTime = (bottomDate.getHours() - topDate.getHours());
                int top = Math.round(topTime * hourSize + topDate.getMinutes() * minuteSize);
                int bottom = Math.round(bottomTime * hourSize + (bottomDate.getMinutes() - topDate.getMinutes()) * minuteSize);
                if (event.getY() != top)
                    result = true || result;
                if (event.getHeight() != bottom)
                    result = true || result;
                if (result) {
                    event.setLocation(Math.round(hourPadding + eventNumber * width + daySize * dayInWeek), top + headerPadding); //event.getX()
                    event.setSize(Math.round(width), bottom); //event.getWidth() jak to bude se sirkou pri vice udalostech ?
                }
                eventNumber++;

            }
            endCalendar.add(Calendar.DATE, 1);
            calendar.add(Calendar.DATE, 1);
        }
        return result;
    }

    /**
     * Prepocita umisteni komponent typu CalendarEvent aby odpovidalo skutecnosti v pripade CalendarView.MULTI_WEEK
     * @return Zda byla pri prepoctu provedena zmena
     */
    protected boolean recountCalendarEventMultiWeek() {
    	if (true) throw new RuntimeException("Implement me!");
        return false;
    }

    /**
     * Prepocita umisteni komponent typu CalendarEvent aby odpovidalo skutecnosti v pripade CalendarView.MONTH
     * @return Zda byla pri prepoctu provedena zmena
     */
    protected boolean recountCalendarEventMonth() {
        float weekSize = (this.getHeight() - headerPadding - monthViewPadding*5) / 5;
        float daySize = (this.getWidth()-5) / (float) 7;

        boolean result = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(calendar.getTime());
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);

        
        for (int row = 0; row<5; row++) {
        	for (int col = 0; col<7; col++) {
                Set<CalendarGridEvent> eventsInDay = new HashSet<CalendarGridEvent>();
                for (Component component : calendarEvents) {
                    if (component instanceof CalendarGridEvent) {
                        CalendarGridEvent event = (CalendarGridEvent) component;
                        Date d = event.getVisibleDateInterval().getStartDate();
                        if ((calendar.getTime().compareTo(d)<=0) && (endCalendar.getTime().compareTo(d)>=0)) {
                            eventsInDay.add(event);
                        }
                    }
                }
                int eventNumber = 0;

                float eventHeight = weekSize / eventsInDay.size();
                int roundedEventHeight = Math.round(eventHeight);
                int roundedDaySize = Math.round(daySize);
                for (CalendarGridEvent event : eventsInDay) {
                	event.setLocation(Math.round(col*daySize), Math.round(row*weekSize + eventHeight*eventNumber) + headerPadding + (row+1)*monthViewPadding);
                	event.setSize(roundedDaySize, roundedEventHeight);
                	eventNumber++;
                }
                
                endCalendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.DATE, 1);                
        	}
        }
        
        
 
        return result;
    }

    /**
     * Jak bude probihat vykreslovani v pripade CalendarView.DAY
     */
    protected void paintCalendarDay(Graphics g) {
        int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
        float hourSize = this.getHeight() / hoursCount;

        for (int i = 0; i < hoursCount; i++) {
            int p = Math.round(i * hourSize);
            g.drawString(Integer.toString((i + calendarConfig.getDayStartTime())), 0, p + g.getFont().getSize());
            g.drawLine(0, p, this.getWidth(), p);
        }
    }

    /**
     * Jak bude probihat vykreslovani v pripade CalendarView.WEEK
     */
    protected void paintCalendarWeek(Graphics g) {
        int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
        float hourSize = (this.getHeight() - headerPadding) / hoursCount;
        float daySize = (this.getWidth() - hourPadding) / 7;

        for (int i = 0; i < hoursCount; i++) {
            int p = Math.round(i * hourSize);
            g.drawString(Integer.toString((i + calendarConfig.getDayStartTime())), 0, p + g.getFont().getSize() + headerPadding);
            g.drawLine(0, p + headerPadding, this.getWidth(), p + headerPadding);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        for (int i = 0; i < 7; i++) {
            int p = Math.round(i * daySize);
            g.drawLine(p + hourPadding, 0, p + hourPadding, this.getHeight());
            String den = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            g.drawString(den, p + hourPadding + 2, g.getFont().getSize());
            calendar.add(Calendar.DATE, 1);
        }
    }

    /**
     * Jak bude probihat vykreslovani v pripade CalendarView.MULTI_WEEK
     */
    protected void paintCalendarMultiWeek(Graphics g) {
    	if (true) throw new RuntimeException("Implement me!");
    }

    /**
     * Jak bude probihat vykreslovani v pripade CalendarView.MONTH
     */
    protected void paintCalendarMonth(Graphics g) {
    	monthViewPadding = g.getFont().getSize() + 4;
        float weekSize = (this.getHeight() - headerPadding) / 5;
        float daySize = this.getWidth() / 7;

        for (int i = 0; i < 5; i++) {
            int p = Math.round(i * weekSize);
            g.drawLine(0, p + headerPadding, this.getWidth(), p + headerPadding);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        for (int i = 0; i < 7; i++) {
            int p = Math.round(i * daySize);
            g.drawLine(p , 0, p , this.getHeight());
            String den = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            g.drawString(den, p + 2, g.getFont().getSize());
            calendar.roll(Calendar.DATE, 1);
        }
        
        calendar.setTime(startDate);
        for (int row = 0; row<5; row++) {
            int p = Math.round(row * weekSize);
        	for (int col = 0; col < 7; col++) {
                int q = Math.round(col * daySize);
        		String datum = SimpleDateFormat.getDateInstance().format(calendar.getTime());
        		g.drawString(datum, q + 2, p + g.getFont().getSize()*2 + 2);
        		calendar.add(Calendar.DATE, 1);
			}
        }
    }

    public Date getDateByPosition(int x, int y) {
        Date result = new Date(0);
        switch (calendarView) {
            case DAY:
                int hoursCount = calendarConfig.getDayEndTime() - calendarConfig.getDayStartTime();
                float minuteSize = new Float(this.getHeight() - 1) / (hoursCount * 60);
                float hourSize = new Float(this.getHeight() - 1) / hoursCount;
                int hodin = new Double(Math.ceil(y / hourSize)).intValue();
                int minut = new Double((y - hodin * hourSize) / minuteSize).intValue();
                result.setHours(hodin);
                result.setMinutes(minut);
                break;
            case WEEK:
            	if (true) throw new RuntimeException("Implement me!");
                break;
            case MULTI_WEEK:
            	if (true) throw new RuntimeException("Implement me!");
                break;
            case MONTH:
            	if (true) throw new RuntimeException("Implement me!");
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

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// nic
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		//nic
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
        recountCalendarEvents();        		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		//nic
	}

}
