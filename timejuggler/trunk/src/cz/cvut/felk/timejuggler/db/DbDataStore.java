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
          event.setuid("01234568-012144");
          event.setdescription("Muj event 1");
          event.setsummary("Toto je prvni event ulozeny do databaze!");
          db.store(calendar1,event);
          */

        /* Pridavani ToDo */
        /*
          VToDo todo = new VToDo();
          Date deadline= new Date();
          deadline.setMonth(5);
          deadline.setDate(20);
          todo.setuid("123456-7898");
          todo.setdescription("Napsat ukoly!");
          todo.setsummary("Museji se napsat vsechny ukoly do skoly! :)");
          todo.setdue(new Timestamp(deadline.getTime()));
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
            }
            /* Nacteni eventu v kalendari  */
            events = db.getEventsByCalendar((VCalendar) cals.elementAt(0));

            /* Nacteni ukolu v kalendari  */
            todos = db.getToDosByCalendar((VCalendar) cals.elementAt(0));

            System.out.println("Udalosti:" + events.size());
            for (Object i : events) {
                System.out.println(((VEvent) i).getDescription());

            }
            System.out.println("Ukoly:" + todos.size());
            for (Object i : todos) {
                System.out.println(((VToDo) i).getDescription());

            }

        } else {
            System.out.println("Zadne kalendare v databazi!");
            System.exit(0);
        }

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
                todo.setGeo(rs.getString("geo"));
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
    public void store(VCalendar cal) {
        // Pridani noveho kalendare do databaze
        Object params[] = {cal.getProductId(), cal.getVersion(), cal.getCalendarScale(), cal.getMethod(), cal.getName()};
        String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES (?,?,?,?,?)";
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        int rowsAffected = template.executeUpdate(insertQuery, params);
        // nastaveni klice objektu VCalendar
        cal.setId(template.getGeneratedId());
    }

    /**
     * Method store
     */
    public void store(VCalendar cal, VEvent event) {
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
        event.setId(template2.getGeneratedId());

    }

    /**
     * Method store
     */
    public void store(VCalendar cal, VToDo todo) {
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
                template.getGeneratedId(), todo.getGeo(),
                todo.getLocation(), todo.getPriority(), todo.getPercentComplete(), todo.getDue()
        };
        String insertQuery2 = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete,due) VALUES (?,?,?,?,?,?)";
        TimeJugglerJDBCTemplate template2 = new TimeJugglerJDBCTemplate();
        template2.executeUpdate(insertQuery2, params2);
        todo.setId(template2.getGeneratedId());

    }


}
