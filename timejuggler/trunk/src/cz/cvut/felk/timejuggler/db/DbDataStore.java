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
import java.util.Iterator;
import java.util.logging.Logger;


public class DbDataStore {
    private final static Logger logger = Logger.getLogger(DbDataStore.class.getName());
    //TODO : dopsat ICS export/import
    //TODO : upravit ukazkovy kod
    /**
     * Method main
     * @param args
     */
    public static void main(String[] args) {
    	// Testing
    	importTest();
    	System.exit(0);
    	
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
        
        int i = 0;
        for (VCalendar cal: cals) {
            try {
                db.exportICS(cal, new File("vystup" + i + ".ics"));
                i++;
            } catch (URISyntaxException e) {
                LogUtils.processException(logger, e);
            } catch (ValidationException e) {
                LogUtils.processException(logger, e);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
        
        db.showDB();
        
        ConnectionManager.getInstance().shutdown();
    }

    /**
     * Method importTest
     * @param 
     *
     * Pro otestovani importu ICS
     *
     */
	public static void importTest(){
		/* 
		 * Ukazkovy kod
		 * ************************/
		DbDataStore db = new DbDataStore();
		/* pri importu je ulozeni do db automaticke */
		try {
			VCalendar cal = db.importICS("G:\\pokus\\USHolidays.ics");
			/* nastaveni jmena pro importovany kalendar */
			cal.setName("USHolidays.ics - imported 1");
			/* ulozeni zmen */
			db.saveOrUpdate(cal);
			/* vypis obsahu db */
			db.showDB();
			
			/* vymazani kalendare z db */
			//db.delete(cal);
	    }
	    catch (IOException e) {
	    	LogUtils.processException(logger, e);
	    }
	    catch (ParserException e) {
	    	LogUtils.processException(logger, e);
	    }
	    catch (DatabaseException e) {
	    	LogUtils.processException(logger, e);
	    }		
		/* odpojeni databaze */
		ConnectionManager.getInstance().shutdown();
		// konec programu
	}

    /**
     * Method showDB
     * @param 
     *
     * Pro vypis obsahu DB
     *
     */
	public void showDB(){
		Vector<VCalendar> cals = getCalendars();
        
        for (VCalendar cal: cals) {
        	System.out.println ("Calendar name:" + cal.getName());
        	System.out.println ("+--Events:");

            Vector<EventTask> events = cal.getEvents();
            if (events != null) {
	        	for (EventTask e: events) {
	        		//System.out.println ("event.description: " + e.getDescription());
	        		System.out.println ("event.summary: " + e.getSummary());
	        		System.out.println ("+-startDate: " + e.getStartDate());
	        		System.out.println ("+-endDate: " + e.getEndDate());
	        		System.out.println ("+-created: " + e.getCreated());
	        		System.out.println ("+-dtstamp: " + e.getDTimestamp());
	        		
	        		Vector<Category> cats = e.getCategories();
	        		if (cats != null) {
	        			System.out.print ("+-categories " );
		        		for (Object o : cats) {
		        			Category c = (Category)o;
		        			System.out.print (c.getName() + ",");
		        		}
		        		System.out.println ();
	        		}
	        		Periods periods = e.getPeriods();
	        		if (periods != null) {
		        		for (Object o : periods) {
		        			Period p = (Period)o;
		        			System.out.println ("+-period " + p.getStartDate() + " ... " + p.getEndDate());
		        		}
	        		}
	        		
	        		System.out.println ("++++++++++++++++++++++++++++++++++");
	        	}
        	}
        	System.out.println ("+--Todos:");
        	Vector<EventTask> todos = cal.getToDos();
        	if (todos != null) {
	        	for (EventTask todo: todos) {
	        		System.out.println ("todo: " + todo.getDescription());
	        	}
        	}
        	System.out.println ();
        }
        if (cals.isEmpty()) {
        	System.out.println("Zadne kalendare v databazi!");
        }
	}

    /**
     * Method getCalendars
     * @return
     */
    public Vector<VCalendar> getCalendars() {
        String sql = "SELECT * FROM VCalendar";
        TimeJugglerJDBCTemplate<Vector<VCalendar>> template = new TimeJugglerJDBCTemplate<Vector<VCalendar>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new Vector<VCalendar>();
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
     * Method saveOrUpdate
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
    	// TODO: vymazat take vsechny eventy a todo v kalendari
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
	public VCalendar importICS(String filename) throws IOException, ParserException, DatabaseException {
		logger.info("Importing ICS file " + filename);
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();	// import jako 1 transakce
		
		// TODO: categories,periods,..
		// Import je castecne funkcni
		Property prop;
		
		// Vytvoreni nove instance tridy VCalendar (typu Timejuggler)
		VCalendar newcal = new VCalendar();
		
		// Vytvoreni nove instance tridy Calendar (typu iCal) ze souboru ICS
		Calendar calendar = Calendars.load(filename);
		
		// Instance pomocne tridy pro prevod objektu iCal na typ Timejuggler
		ICalTransformer transformer = ICalTransformer.getInstance();		
		
		// Nastaveni vlastnosti novemu kalendari, ktere byly precteny ze souboru
		prop = calendar.getMethod();
			newcal.setMethod(prop == null ? null : prop.getValue());
		prop = calendar.getCalendarScale();
			newcal.setCalendarScale(prop == null ? null :prop.getValue());
		prop = calendar.getProductId();
			newcal.setProductId(prop == null ? null :prop.getValue());
		prop = calendar.getVersion();
			newcal.setVersion(prop == null ? null :prop.getValue());
		newcal.setName(filename);
		
		// Ulozeni nastaveni kalendare do DB
		newcal.saveOrUpdate(template);
		
		// Pro kazdou komponentu VEVENT ze souboru, vytvor instanci VEvent a uloz ji do kalendare
		ComponentList complist = calendar.getComponents(Component.VEVENT);
		
		for (Object obj : complist) {
			Component comp = (Component)obj;
			// TODO: rozlisit Component.VEVENT a Component.VTODO
			
			EventTask event = new EventTask();
			
			prop = comp.getProperty(Property.CLASS);
				event.setClazz(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.CREATED);
				event.setCreated(prop == null ? null : new Date(((Created)prop).getDateTime().getTime()));
			prop = comp.getProperty(Property.DESCRIPTION);
				event.setDescription(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.DTSTART);
				event.setStartDate(prop == null ? null : (((DtStart)prop).getDate()));
			prop = comp.getProperty(Property.GEO);
				event.setGeoGPS(prop == null ? null : ((prop).getValue()));
			prop = comp.getProperty(Property.LAST_MODIFIED);
				event.setLastModified(prop == null ? null : new Date(((LastModified)prop).getDateTime().getTime()));
			prop = comp.getProperty(Property.LOCATION);
				event.setLocation(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.ORGANIZER);
				event.setOrganizer(prop == null ? null : prop.getValue());
			prop = comp.getProperty(Property.PRIORITY);
				event.setPriority(prop == null ? 0 : ((Priority)prop).getLevel());
			prop = comp.getProperty(Property.DTSTAMP);
				event.setDTimestamp(prop == null ? null : (new Date(((DtStamp)prop).getDateTime().getTime())));
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
				event.setRecurrenceId(prop == null ? null : new Date(((RecurrenceId)prop).getDate().getTime()));
			prop = comp.getProperty(Property.DTEND);
				event.setEndDate(prop == null ? null : ((DtEnd)prop).getDate());
			prop = comp.getProperty(Property.DURATION);
				if (prop != null) event.setEndDate(transformer.makeDuration(prop));
			prop = comp.getProperty(Property.UID);
				event.setUid(prop == null ? null : (prop).getValue());
			
			/* Categories */
			prop = comp.getProperty(Property.CATEGORIES);
				
			CategoryList catList = ((Categories)prop).getCategories();	// iCal
			Vector<Category> cats = new Vector<Category>();	// Timejuggler
			for (Iterator<?> it = catList.iterator(); it.hasNext();) {
				cats.add(new Category(it.next().toString()));
			}
			event.setCategories(cats);	// Prirazeni kategorii udalosti
			
			
			
			
			/* Cast Periods + Recurrence Dates */
			/* priprava */
			
			cz.cvut.felk.timejuggler.db.Periods eventPeriods = event.getPeriods();
			
			prop = comp.getProperty(Property.RDATE);
				RDate rdate = (RDate)prop;
				//TODO : DbDataStore - Periods ***
				//TODO : DbDataStore - Dates
				//TODO : DbDataStore - Rules
				//TODO : DbDataStore - categories ...
				PeriodList plist;
				if (rdate != null) {
					plist = rdate.getPeriods();
					if (plist != null) {
						Periods periods = new Periods();
						
						for (Object pobj : plist) {
							net.fortuna.ical4j.model.Period p = (net.fortuna.ical4j.model.Period)pobj;
							cz.cvut.felk.timejuggler.db.Period newPeriod = transformer.makePeriod(p);
							periods.addPeriod(newPeriod);
						}
						event.setPeriods(periods);
					}
				}
				
				
				//TODO : dates
/*				DateList dlist = rdate.getDates();
				DistinctDates ds = new DistinctDates();
				for (Date d : dlist) {		
					ds.addDate(new DistinctDate(d));
				}
*/
				//eventPeriods.
				

				
			/* TODO: + ? , Alarms */

			//Ulozeni eventu do kalendare
			event.setCalendarId(newcal.getId());
			event.saveOrUpdate(template);
			//saveOrUpdate(newcal, event);
		}
		template.commit();	// potvrzeni transakce
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
		logger.info("Exporting calendar to file " + outputFile.getPath() + "...");
		// TODO: Period property
		// Funkcni - castecne expertuje Eventy
		Calendar ical;
		Vector<EventTask> events = calendar.getEvents();	// sada Timejuggler
		ComponentList compList = new ComponentList();	// sada pro iCal
		
		PropertyList propList;
		ComponentFactory iCalFactory = ComponentFactory.getInstance();
		
    	for (EventTask e: events) {
    		propList = new PropertyList();
    		
    		// Nastaveni vlastnosti pro Eventy k ulozeni do souboru
    		String value;
    		value = e.getClazz();
    		if (value != null) propList.add(new Clazz(value));
    		Date tmpdate = e.getCreated();
    		if (tmpdate != null) propList.add(new Created(new DateTime(tmpdate.getTime())));
    		value = e.getDescription();
    		if (value != null) propList.add(new Description(value));
    		if (e.getStartDate() != null) propList.add(new DtStart(new net.fortuna.ical4j.model.Date(e.getStartDate())));
    		
    		value = e.getGeoGPS();
    		if (value != null) propList.add(new Geo(value));
    		tmpdate = e.getLastModified();
    		if (tmpdate != null) propList.add(new LastModified(new DateTime(tmpdate.getTime())));
    		
    		value = e.getLocation();
    		if (value != null) propList.add(new Location(value));
    		

  			value = e.getOrganizer();
  			if (value != null) propList.add(new Organizer(value));


    		
    		propList.add(new Priority(e.getPriority()));
    		propList.add(new DtStamp());
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
    		if (e.getEndDate() != null) propList.add(new DtEnd(new net.fortuna.ical4j.model.Date(e.getEndDate())));
    		
    		//TODO: nastavit duration nebo dtend property
    		//propList.add(new Duration());
    		value = e.getUid();
    		if (value != null) propList.add(new Uid(value));
    		
    		net.fortuna.ical4j.model.Component comp = iCalFactory.createComponent(Component.VEVENT, propList);
    		
			compList.add(comp);
			
    		Periods periods = e.getPeriods();
    		if (periods != null) {
        		for (Object o:periods) {
        			Period p = (Period)o;
        			System.out.println ("+-period " + p.getStartDate() + " ... " + p.getEndDate());
        		}
    		}
    	}
    	propList = new PropertyList();
    	
    	// Nastaveni vlastnosti kalendare
    	// Povinne: ProductID, Version
    	// Nepovinne: CalendarScale, Method
    	String value;
    	value = calendar.getProductId();
    	if (value != null) propList.add(new ProdId(value));
    	value = calendar.getVersion();
    	if (value != null) propList.add(Version.VERSION_2_0);
    	value = calendar.getCalendarScale();
    	if (value != null) propList.add(new CalScale(value));
    	value = calendar.getMethod();
    	if (value != null) propList.add(new Method(value));
    	
    	// Vytvoreni objektu Calendar (typ iCal) pro ulozeni do souboru
    	ical = new Calendar(propList, compList);
		
    	CalendarOutputter exporter = new CalendarOutputter(true);
    	OutputStream outStream = null;
    	try {
	    	outStream = new FileOutputStream(outputFile);
	    	exporter.output(ical, outStream);
	    	logger.info("Export finished.");
	    }
//	    catch (IOException ex) {
//    		// Chyba IO pri zapisu
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
