package cz.cvut.felk.timejuggler.db;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DbDataStore {
	
	/**
	 * Method main
	 *
	 *
	 * @param args
	 *
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
		
		Vector events,todos;
		
		if (cals.size()>0) {
					/* Vypis */	
			System.out.println ("Kalendare:" + cals.size());
			for (Object i:cals) {	
				System.out.println (((VCalendar)i).getname() );
			}
			/* Nacteni eventu v kalendari  */
			events = db.getEventsByCalendar((VCalendar)cals.elementAt(0));
			
			/* Nacteni ukolu v kalendari  */
			todos = db.getToDosByCalendar((VCalendar)cals.elementAt(0));	

			System.out.println ("Udalosti:" + events.size());
			for (Object i:events) {	
				System.out.println (((VEvent)i).getdescription() );
				
			}
			System.out.println ("Ukoly:" + todos.size());
			for (Object i:todos) {	
				System.out.println (((VToDo)i).getdescription() );
				
			}

		}else{
			System.out.println ("Zadne kalendare v databazi!");
			System.exit(0);
		}
		
	}

	/**
	 * Method getCalendars
	 *
	 *
	 * @return
	 *
	 */
	public Vector getCalendars() {
		String sql="SELECT * FROM VCalendar";
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate(){
			protected void handleRow(ResultSet rs) throws SQLException {
				VCalendar cal = new VCalendar();
				cal.setid(Integer.valueOf(rs.getInt("vCalendarID")).intValue());
				cal.setprodid(rs.getString("prodid"));
				cal.setcalscale(rs.getString("calscale"));
				cal.setmethod(rs.getString("method"));
				cal.setversion(rs.getString("version"));
				cal.setname(rs.getString("name"));
				items.add((Object)cal);
			}
		};
		template.executeQuery(sql,null);
		return template.getItems();
	}

	/**
	 * Method getEventsByCalendar
	 *
	 *
	 * @return
	 *
	 */
	public Vector getEventsByCalendar(VCalendar cal) {
		String sql="SELECT * FROM VEvent,CalComponent WHERE (VEvent.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=?)";
		Object params[] = { new Integer(cal.getid()) };
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate(){
			protected void handleRow(ResultSet rs) throws SQLException {
				VEvent event = new VEvent();
				event.setid(rs.getInt("vEventID"));
				event.setlocation(rs.getString("location"));
				event.settransp(rs.getString("transp"));
				event.setpriority(rs.getInt("priority"));
				event.setgeo(rs.getString("geo"));
				//cast calcomponent
				event.setdescription(rs.getString("description"));
				event.setuid(rs.getString("uid"));
				event.setclazz(rs.getString("clazz"));
				event.setorganizer(rs.getString("organizer"));
				event.setsequence(rs.getInt("sequence"));
				event.setstatus(rs.getString("status"));
				event.setsummary(rs.getString("summary"));
				event.setdtstamp(rs.getTimestamp("dtstamp"));
				
				items.add((Object)event);
			}
		};
		template.executeQuery(sql,params);
		return template.getItems();
	}

	/**
	 * Method getToDosByCalendar
	 *
	 *
	 * @return
	 *
	 */
	public Vector getToDosByCalendar(VCalendar cal) {
		String sql="SELECT * FROM VToDo,CalComponent WHERE (VToDo.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=?)";
		Object params[] = { new Integer(cal.getid()) };
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate(){
			protected void handleRow(ResultSet rs) throws SQLException {
				VToDo todo = new VToDo();
				todo.setid(rs.getInt("vToDoID"));
				todo.setlocation(rs.getString("location"));
				todo.setpercentcomplete(rs.getInt("percentcomplete"));
				todo.setpriority(rs.getInt("priority"));
				todo.setgeo(rs.getString("geo"));
				todo.setdue(rs.getTimestamp("due"));
				todo.setcompleted(rs.getTimestamp("completed"));
				//cast calcomponent
				todo.setdescription(rs.getString("description"));
				todo.setuid(rs.getString("uid"));
				todo.setclazz(rs.getString("clazz"));
				todo.setorganizer(rs.getString("organizer"));
				todo.setsequence(rs.getInt("sequence"));
				todo.setstatus(rs.getString("status"));
				todo.setsummary(rs.getString("summary"));
				todo.setdtstamp(rs.getTimestamp("dtstamp"));
				
				items.add(todo);
			}
		};
		template.executeQuery(sql,params);
		return template.getItems();
	}

	/**
	 * Method store
	 *
	 *
	 */
	public void store(VCalendar cal) {
		// Pridani noveho kalendare do databaze
		Object params[] = {cal.getprodid(),cal.getversion(),cal.getcalscale(),cal.getmethod(),cal.getname() };
		String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES (?,?,?,?,?)";
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
		int rowsAffected = template.executeUpdate(insertQuery,params);
		// nastaveni klice objektu VCalendar
		cal.setid(template.getGeneratedId());
	}

	/**
	 * Method store
	 *
	 *
	 */
	public void store(VCalendar cal,VEvent event) {
		// Pridani noveho Eventu do kalendare
		Object params1[] = {
			event.getuid(),new Integer(cal.getid()),event.geturl(),event.getclazz(),
			event.getdescription(),event.getorganizer(),new Integer(event.getsequence()),
			event.getstatus(),event.getsummary(),new Timestamp(new Date().getTime())
		};
		String insertQuery1 = "INSERT INTO CalComponent (uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?)";
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
		template.executeUpdate(insertQuery1,params1);
		
		Object params2[] = {
			new Integer(template.getGeneratedId()),event.getgeo(),
			event.getlocation(),event.getpriority(),event.gettransp()
		};
		String insertQuery2 = "INSERT INTO VEvent (calComponentID,geo,location,priority,transp) VALUES (?,?,?,?,?)";		
		TimeJugglerJDBCTemplate template2 = new TimeJugglerJDBCTemplate();
		template2.executeUpdate(insertQuery2,params2);
		event.setid(template2.getGeneratedId());
		
	}
	/**
	 * Method store
	 *
	 *
	 */
	public void store(VCalendar cal,VToDo todo) {
		// Pridani noveho Ukolu do kalendare
		Object params1[] = {
			todo.getuid(),new Integer(cal.getid()),todo.geturl(),todo.getclazz(),
			todo.getdescription(),todo.getorganizer(),new Integer(todo.getsequence()),
			todo.getstatus(),todo.getsummary(),new Timestamp(new Date().getTime())
		};
		String insertQuery1 = "INSERT INTO CalComponent (uid,vCalendarID,url,clazz,description,organizer,sequence,status,summary,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?)";
		TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
		template.executeUpdate(insertQuery1,params1);
		
		Object params2[] = {
			new Integer(template.getGeneratedId()),todo.getgeo(),
			todo.getlocation(),todo.getpriority(),new Integer(todo.getpercentcomplete()),todo.getdue()
		};
		String insertQuery2 = "INSERT INTO VToDo (calComponentID,geo,location,priority,percentcomplete,due) VALUES (?,?,?,?,?,?)";		
		TimeJugglerJDBCTemplate template2 = new TimeJugglerJDBCTemplate();
		template2.executeUpdate(insertQuery2,params2);
		todo.setid(template2.getGeneratedId());
		
	}



}
