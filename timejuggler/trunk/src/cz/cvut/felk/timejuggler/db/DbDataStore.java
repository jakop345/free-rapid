package cz.cvut.felk.timejuggler.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class DbDataStore {

    /**
     * Method main
     * @param args
     */
    public static void main(String[] args) {
        // Testing
        DbDataStore db = new DbDataStore();
        
		
		//VCalendar calendar1 = new VCalendar("Testovaci 1");
		//db.store(calendar1);

        /* Pridavani kalendare */
        /*
          VCalendar calendar1 = new VCalendar("Hlavni kalendar");
          VCalendar calendar2 = new VCalendar("Svatky");
          db.store(calendar1);
          db.store(calendar2);
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
		
		db.store(calendar1,event);
		*/
                  
        /* Pridavani ToDo */
        /*
          VToDo todo = new VToDo();
          Date deadline = new Date();
          deadline.setMonth(5);
          deadline.setDate(20);
          todo.setUid("123456-7898");
          todo.setDescription("Napsat ukoly!");
          todo.setSummary("Museji se napsat vsechny ukoly do skoly! :)");
          todo.setDue(new Timestamp(deadline.getTime()));
          db.store(calendar1,todo);
          */

		/* Vypis */

        Vector<VCalendar> cals = db.getCalendars();
        
        for (VCalendar cal: cals) {
        	System.out.println ("Calendar name:" + cal.getName());
        	System.out.println ("+--Events:");
        	Vector<VEvent> events = cal.getEvents();
        	for (VEvent e: events) {
        		System.out.println ("event: " + e.getDescription());
        		System.out.println ("+-startDate: " + e.getStartDate());
        		System.out.println ("+-created: " + e.getCreated());
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
    public void store(VCalendar cal) throws DatabaseException {
    	TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
    	cal.store(template);
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
    public <C extends CalComponent> void store (VCalendar cal,C component) throws DatabaseException {
        // Pridani noveho Eventu nebo Ukolu do kalendare
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        component.setCalendarId(cal.getId());
        component.store(template);
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
}
