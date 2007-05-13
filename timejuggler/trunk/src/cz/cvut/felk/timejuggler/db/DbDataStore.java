package cz.cvut.felk.timejuggler.db;

import cz.cvut.felk.timejuggler.utilities.LogUtils;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Calendars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;


public class DbDataStore {
    private final static Logger logger = Logger.getLogger(DbDataStore.class.getName());
    //TODO : dopsat ICS export/import, + jako transakce
    //TODO : upravit ukazkovy kod
    /**
     * Method main
     * @param args
     */
    public static void main(String[] args) {
        // Testing
        DbDataStore db = new DbDataStore();
        
        /* ICS import (uklada se do db (saveOrUpdate) automaticky)*/
        /*
        try {
        	VCalendar imported = db.importICS("/path/svatky.ics");
	    }
	    catch (IOException ex) {
	    	// Chyba IO
	    	ex.printStackTrace();
	    }
	    catch (ParserException ex) {
	    	// Chyba parsovani (nespravny format ?)
	    	ex.printStackTrace();
	    }
	    */
        
		
		//VCalendar calendar1 = new VCalendar("Testovaci 1");
		//db.saveOrUpdate(calendar1);

        /* Pridavani kalendare */
        /*
          VCalendar calendar1 = new VCalendar("Hlavni kalendar");
          VCalendar calendar2 = new VCalendar("Svatky");
          db.saveOrUpdate(calendar1);
          db.saveOrUpdate(calendar2);
          */
		
		/* Pridavani Eventu */		
		/*
		VEvent event = new VEvent();
		event.setUid("01234568-012144");
		event.setDescription("Muj event 2");
		event.setSummary("Toto je druhy event ulozeny do databaze!");
		Date dt = new Date();
		dt.setDate(27);
		dt.setMonth(4);
		dt.setYear(2007);
		event.setStartDate(dt);
		
		db.saveOrUpdate(calendar1,event);
		*/
                  
        /* Pridavani Ukolu */
        /*
          VToDo todo = new VToDo();
          Date deadline = new Date();
          deadline.setMonth(5);
          deadline.setDate(20);
          todo.setUid("123456-7898");
          todo.setDescription("Napsat ukoly!");
          todo.setSummary("Museji se napsat vsechny ukoly do skoly! :)");
          todo.setDue(new Timestamp(deadline.getTime()));
          db.saveOrUpdate(calendar1,todo);
          */

		/* Vypis */

        Vector<VCalendar> cals = db.getCalendars();
        
        for (VCalendar cal: cals) {
        	System.out.println ("Calendar name:" + cal.getName());
        	System.out.println ("+--Events:");
            try {
                db.exportICS(cal, new File("vystup.ics"));
            } catch (URISyntaxException e) {
                LogUtils.processException(logger, e);
            } catch (ValidationException e) {
                LogUtils.processException(logger, e);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
            Vector<VEvent> events = cal.getEvents();
        	for (VEvent e: events) {
        		System.out.println ("event.description: " + e.getDescription());
        		System.out.println ("event.summary: " + e.getSummary());
        		System.out.println ("+-startDate: " + e.getStartDate());
        		System.out.println ("+-created: " + e.getCreated());
        		System.out.println ("++++++++++++++++++++++++++++++++++");
        		Vector<Period> periods = e.getPeriods();
        		if (periods != null) {
	        		for (Period p:periods) {
	        			System.out.println ("+-period " + p.getStartDate() + " ... " + p.getEndDate());
	        		}
        		}
        	}
        	
        	System.out.println ("+--Todos:");
        	Vector<VToDo> todos = cal.getToDos();
        	for (VToDo todo: todos) {
        		System.out.println ("todo: " + todo.getDescription());
        	}

        	System.out.println ();
        }
        if (cals.isEmpty()) {
        	System.out.println("Zadne kalendare v databazi!");
        }

        ConnectionManager.getInstance().shutdown();
    }

    /**
     * Method getCalendars
     * @return
     */
    public Vector<VCalendar> getCalendars() {
        String sql = "SELECT * FROM VCalendar";
        TimeJugglerJDBCTemplate<VCalendar> template = new TimeJugglerJDBCTemplate<VCalendar>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                VCalendar cal = new VCalendar();
                cal.setId(Integer.valueOf(rs.getInt("vCalendarID")).intValue());
                cal.setProductId(rs.getString("prodid"));
                cal.setCalendarScale(rs.getString("calscale"));
                cal.setMethod(rs.getString("method"));
                cal.setVersion(rs.getString("version"));
                cal.setName(rs.getString("name"));
                items.add(cal);
            }
        };
        template.executeQuery(sql, null);
        return template.getItems();
    }

    /**
     * Method store
     */
    public void saveOrUpdate(VCalendar cal) throws DatabaseException {
    	TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
    	cal.saveOrUpdate(template);
    	template.commit();
    }
    /**
     * Method delete
     */
    public void delete(VCalendar cal) throws DatabaseException {
    	TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
    	cal.delete(template);
    	template.commit();
    }
    /**
     * Method store
     */
    public <C extends CalComponent> void saveOrUpdate (VCalendar cal,C component) throws DatabaseException {
        // Pridani noveho Eventu nebo Ukolu do kalendare
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        component.setCalendarId(cal.getId());
        component.saveOrUpdate(template);
        template.commit();        
    }
    /**
     * Method delete
     */
    public <C extends CalComponent> void delete (C component) throws DatabaseException {
        // Odstraneni Eventu nebo Ukolu z kalendare
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        component.delete(template);
        template.commit();        
    }



	/**
	 * Method importICS
	 *
	 *
	 * @return
	 *
	 */
	public VCalendar importICS(String filename) throws IOException, ParserException{
		// TODO: categories,periods,..
		// castecne funkcni
		Property prop;
		
		VCalendar newcal = new VCalendar();
		Calendar calendar = Calendars.load(filename);		
		prop = calendar.getMethod();
			newcal.setMethod(prop == null ? null : prop.getValue());
		prop = calendar.getCalendarScale();
			newcal.setCalendarScale(prop == null ? null :prop.getValue());
		prop = calendar.getProductId();
			newcal.setProductId(prop == null ? null :prop.getValue());
		prop = calendar.getVersion();
			newcal.setVersion(prop == null ? null :prop.getValue());
		newcal.setName(filename);
		saveOrUpdate(newcal);
		
		ComponentList complist = calendar.getComponents(Component.VEVENT);
		
		for (Object obj : complist) {
			Component comp = (Component)obj;
			
			VEvent event = new VEvent();
			
			prop = comp.getProperty(Property.CLASS);
				event.setClazz(prop == null ? null : prop.getValue());
			/*
			prop = comp.getProperty(Property.CREATED);
				event.setCreated(prop == null ? null : new Timestamp(((Created)prop).getDateTime().getTime()));
			*/
			prop = comp.getProperty(Property.DESCRIPTION);
				event.setDescription(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.DTSTART);
				event.setStartDate(prop == null ? null : (((DtStart)prop).getDate()));
			prop = comp.getProperty(Property.GEO);
				event.setGeoGPS(prop == null ? null : ((prop).getValue()));
			prop = comp.getProperty(Property.LAST_MODIFIED);
				event.setLastModified(prop == null ? null : new Timestamp(((LastModified)prop).getDateTime().getTime()));
			prop = comp.getProperty(Property.LOCATION);
				event.setLocation(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.ORGANIZER);
				event.setOrganizer(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.PRIORITY);
				event.setPriority(prop == null ? 0 : ((Priority)prop).getLevel());
			prop = comp.getProperty(Property.DTSTAMP);
				event.setDTimestamp(prop == null ? null : (new Timestamp(((DtStamp)prop).getDateTime().getTime())));
			prop = comp.getProperty(Property.SEQUENCE);
				event.setSequence(prop == null ? 0 : ((Sequence)prop).getSequenceNo());
			prop = comp.getProperty(Property.STATUS);
				event.setStatus(prop == null ? null : (prop).getValue());
			prop = comp.getProperty(Property.SUMMARY);
				event.setSummary(prop == null ? null : (prop).getValue());
			prop = comp.getProperty(Property.TRANSP);
				event.setTransparency(prop == null ? null : (prop).getValue());
			prop = comp.getProperty(Property.URL);
				event.setUrl(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.RECURRENCE_ID);
				event.setRecurrenceId(prop == null ? null : new Timestamp(((RecurrenceId)prop).getDate().getTime()));
			/* TODO: + end date ? , duration, Alarms */
			prop = comp.getProperty(Property.UID);
				event.setUid(prop == null ? null : (prop).getValue());
			saveOrUpdate(newcal, event);
		}
		
		return newcal;
	}

    /**
     * Export ICS dat do souboru
     * @param calendar ????
     * @param outputFile vystupni soubor pro ulozeni dat
     * @throws URISyntaxException ????
     * @throws ValidationException Chyba - Nevalidni ical
     * @throws IOException Chyba IO pri zapisu
     */
	public void exportICS(VCalendar calendar, File outputFile) throws URISyntaxException, ValidationException, IOException {
		// TODO: Period property - nutne alespon jedna
		// zatim nefunkcni
		Calendar ical;
		Vector<VEvent> events = calendar.getEvents();
		ComponentFactory iCalFactory = ComponentFactory.getInstance();
		
    	for (VEvent e: events) {
    		PropertyList propList = new PropertyList();
    		String value;
    		
    		value = e.getClazz();
    		if (value != null) propList.add(new Clazz(value));
    		Timestamp ts = e.getCreated();
    		if (ts != null) propList.add(new Created(new DateTime(new Date(ts.getTime()))));
    		value = e.getDescription();
    		if (value != null) propList.add(new Description(value));
    		//propList.add(new DtStart());
    		value = e.getGeoGPS();
    		if (value != null) propList.add(new Geo(value));
    		//propList.add(new LastModified());
    		value = e.getLocation();
    		if (value != null) propList.add(new Location(value));
    		

  			value = e.getOrganizer();
  			if (value != null) propList.add(new Organizer(value));


    		
    		propList.add(new Priority(e.getPriority()));
    		//propList.add(new DtStamp());
    		propList.add(new Sequence(e.getSequence()));
    		value = e.getStatus();
    		if (value != null) propList.add(new Status(value));
    		value = e.getSummary();
    		if (value != null) propList.add(new Summary(value));
    		value = e.getTransparency();
    		if (value != null) propList.add(new Transp(value));
    		try {
	   			value = e.getUrl();
    			if (value != null) propList.add(new Url(new URI(value)));
		    }
		    catch (URISyntaxException ex) {
		    	LogUtils.processException(logger, ex);
		    }
    		//propList.add(new RecurrenceId());
    		//propList.add(new DtEnd());
    		//propList.add(new Duration());
    		value = e.getUid();
    		if (value != null) propList.add(new Uid(value));
    		
    		iCalFactory.createComponent(Component.VEVENT, propList);
			
			
    		/*Vector<Period> periods = e.getPeriods();
    		if (periods != null) {
        		for (Period p:periods) {
        			System.out.println ("+-period " + p.getStartDate() + " ... " + p.getEndDate());
        		}
    		}*/
    	}
    	
    	ComponentList compList = new ComponentList();
    	ical = new Calendar(compList);
    	CalendarOutputter exporter = new CalendarOutputter(true);
    	OutputStream outStream = null;
    	try {
	    	outStream = new FileOutputStream(outputFile);
	    	exporter.output(ical, outStream);
	    }
//	    catch (IOException ex) {
//	    	// Chyba IO pri zapisu
//	    	LogUtils.processException(logger, ex);
//	    }
//	    catch(ValidationException ex){
//	    	// Chyba - Nevalidni ical
//	    	LogUtils.processException(logger, ex);
//	    }
        finally {
            if (outStream != null)
               outStream.close();
        }
    }
}
