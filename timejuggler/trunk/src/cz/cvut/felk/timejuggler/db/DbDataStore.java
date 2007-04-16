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
		
//		VCalendar calendar1 = new VCalendar("Hlavni kalendar");
//		db.store(calendar1);

        /* Pridavani kalendare */
        /*
          VCalendar calendar1 = new VCalendar("Hlavni kalendar");
          VCalendar calendar2 = new VCalendar("Svatky");
          db.store(calendar1);
          db.store(calendar2);
          */

//		VEvent event = new VEvent();
//		event.setDescription("Muj event 1");
//		db.store(calendar1,event);
		
        /* Pridavani Eventu */
        /*
          VEvent event = new VEvent();
          event.setUid("01234568-012144");
          event.setDescription("Muj event 1");
          event.setSummary("Toto je prvni event ulozeny do databaze!");
          db.store(calendar1,event);
          */
        /* Pridavani ToDo */
        /*
          VToDo todo = new VToDo();
          Date deadline= new Date();
          deadline.setMonth(5);
          deadline.setDate(20);
          todo.setUid("123456-7898");
          todo.setDescription("Napsat ukoly!");
          todo.setSummary("Museji se napsat vsechny ukoly do skoly! :)");
          todo.setDue(new Timestamp(deadline.getTime()));
          db.store(calendar1,todo);
          */

        /* Nacteni kalendaru */
        Vector cals = db.getCalendars();

        Vector events, todos;

        if (!cals.isEmpty()) {
            /* Vypis */
            System.out.println("Kalendare:" + cals.size());
            for (Object i : cals) {
                System.out.println(((VCalendar) i).getName());
	            /* Nacteni ukolu v kalendari  */
	            todos = db.getToDosByCalendar((VCalendar) i);
	            /* Nacteni eventu v kalendari  */
	            events = db.getEventsByCalendar((VCalendar) i);
	            System.out.println("+ Udalosti:" + events.size());
	            for (Object e : events) {
	                System.out.println(((VEvent) e).getDescription());
	            }
	            System.out.println("+ Ukoly:" + todos.size());
	            for (Object t : todos) {
	                System.out.println(((VToDo) t).getDescription());
	            }
	            //todos.clear();
	            //events.clear();
            }
        } else {
            System.out.println("Zadne kalendare v databazi!");
        }
        ConnectionManager.getInstance().shutdown();

    }

    /**
     * Method getCalendars
     * @return
     */
    public Vector getCalendars() {
        String sql = "SELECT * FROM VCalendar";
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate() {
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
     * Method getEventsByCalendar
     * @return
     */
    public Vector getEventsByCalendar(VCalendar cal) {
        String sql = "SELECT * FROM VEvent,CalComponent WHERE (VEvent.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=?)";
        Object params[] = {cal.getId()};
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate() {
            protected void handleRow(ResultSet rs) throws SQLException {
                VEvent event = new VEvent();
                event.setId(rs.getInt("vEventID"));
                event.setLocation(rs.getString("location"));
                event.setTransparency(rs.getString("transp"));
                event.setPriority(rs.getInt("priority"));
                event.setGeoGPS(rs.getString("geo"));
                //cast calcomponent
                event.setDescription(rs.getString("description"));
                event.setUid(rs.getString("uid"));
                event.setClazz(rs.getString("clazz"));
                event.setOrganizer(rs.getString("organizer"));
                event.setSequence(rs.getInt("sequence"));
                event.setStatus(rs.getString("status"));
                event.setSummary(rs.getString("summary"));
                event.setDTimestamp(rs.getTimestamp("dtstamp"));

                items.add(event);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method getToDosByCalendar
     * @return
     */
    public Vector getToDosByCalendar(VCalendar cal) {
        String sql = "SELECT * FROM VToDo,CalComponent WHERE (VToDo.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=?)";
        Object params[] = {cal.getId()};
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate() {
            protected void handleRow(ResultSet rs) throws SQLException {
                VToDo todo = new VToDo();
                todo.setId(rs.getInt("vToDoID"));
                todo.setLocation(rs.getString("location"));
                todo.setPercentComplete(rs.getInt("percentcomplete"));
                todo.setPriority(rs.getInt("priority"));
                todo.setGeoGPS(rs.getString("geo"));
                todo.setDue(rs.getTimestamp("due"));
                todo.setCompleted(rs.getTimestamp("completed"));
                //cast calcomponent
                todo.setDescription(rs.getString("description"));
                todo.setUid(rs.getString("uid"));
                todo.setClazz(rs.getString("clazz"));
                todo.setOrganizer(rs.getString("organizer"));
                todo.setSequence(rs.getInt("sequence"));
                todo.setStatus(rs.getString("status"));
                todo.setSummary(rs.getString("summary"));
                todo.setDTimestamp(rs.getTimestamp("dtstamp"));

                items.add(todo);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

    /**
     * Method store
     */
    public void store(VCalendar cal) throws SQLException {
        // Pridani noveho kalendare do databaze
        Object params[] = {cal.getProductId(), cal.getVersion(), cal.getCalendarScale(), cal.getMethod(), cal.getName()};
        String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES (?,?,?,?,?)";
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        int rowsAffected = template.executeUpdate(insertQuery, params);
        template.commit();
        // nastaveni klice objektu VCalendar
        cal.setId(template.getGeneratedId());
    }

    /**
     * Method store
     */
    public void store(VCalendar cal, VEvent event) throws SQLException{
        // Pridani noveho Eventu do kalendare
        Object[] params1 = {
                event.getUid(), cal.getId(), event.getUrl(), event.getClazz(),
                event.getDescription(), event.getOrganizer(), event.getSequence(),
                event.getStatus(), event.getSummary(), new Timestamp(new Date().getTime())
        };
        String insertQuery1 = "INSERT INTO CalComponent (uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?)";
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        template.executeUpdate(insertQuery1, params1);

        Object params2[] = {
                template.getGeneratedId(), event.getGeoGPS(),
                event.getLocation(), event.getPriority(), event.getTransparency()
        };
        String insertQuery2 = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?)";
        TimeJugglerJDBCTemplate template2 = new TimeJugglerJDBCTemplate();
        template2.executeUpdate(insertQuery2, params2);
        template2.commit();
        event.setId(template2.getGeneratedId());

    }

    /**
     * Method store
     */
    public void store(VCalendar cal, VToDo todo) throws SQLException {
        // Pridani noveho Ukolu do kalendare
        Object[] params1 = {
                todo.getUid(), cal.getId(), todo.getUrl(), todo.getClazz(),
                todo.getDescription(), todo.getOrganizer(), todo.getSequence(),
                todo.getStatus(), todo.getSummary(), new Timestamp(new Date().getTime())
        };
        String insertQuery1 = "INSERT INTO CalComponent (uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?)";
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        template.executeUpdate(insertQuery1, params1);

        Object[] params2 = {
                template.getGeneratedId(), todo.getGeoGPS(),
                todo.getLocation(), todo.getPriority(), todo.getPercentComplete(), todo.getDue()
        };
        String insertQuery2 = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete,due) VALUES (?,?,?,?,?,?)";
        TimeJugglerJDBCTemplate template2 = new TimeJugglerJDBCTemplate();
        template2.executeUpdate(insertQuery2, params2);
       	template2.commit();
        
        todo.setId(template2.getGeneratedId());

    }


}
