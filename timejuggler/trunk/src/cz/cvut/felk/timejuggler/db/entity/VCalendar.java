package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.sql.Timestamp;

import java.util.logging.Logger;
/**
 * @version 0.1
 * @created 14-IV-2007 16:38:21
 *
 * Trida reprezentujici objekt kalendar (VCalendar) v databazi
 */
public class VCalendar extends DbElement {
	private final static Logger logger = Logger.getLogger(VCalendar.class.getName());
	
    private String productId = "-//CVUT //TimeJuggler Calendar 0.1//CZ";
    private String version = "2.0";
    private String calendarScale = "GREGORIAN";
    private String method = "PUBLISH";
    private String name = "";

    public VCalendar() {

    }

    public VCalendar(int id) {
        super(id);
    }

    public VCalendar(String name) {
        this.name = name;
    }

    public void store() {

    }

	/**
     * Method saveOrUpdate
     * @param template
     *
     * Ulozi novy kalendar do databaze, nebo provede update udaju o kalendari
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			logger.info("Database - Update: VCalendar[" + getId() + "]:" + name + "...");
			Object params[] = { productId, version, calendarScale, method, name, getId() };
			String updateQuery = "UPDATE VCalendar SET prodid=?,version=?,calscale=?,method=?,name=? WHERE vCalendarID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
			// Pridani noveho kalendare do databaze
			logger.info("Database - Insert: VCalendar[]:" + name + "...");
	        Object params[] = { productId, version, calendarScale, method, name };
	        String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES (?,?,?,?,?)";
	        template.executeUpdate(insertQuery, params);
	        // nastaveni klice objektu VCalendar
	        setId(template.getGeneratedId());
	        logger.info("Database - VCalendar new ID=" + getId());
		}
	}
	
	/**
     * Method delete
     * @param template
     *
     * Odstrani kalendar z databaze
     */
	public void delete(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			logger.info("Database - DELETE: VCalendar[" + getId() + "]:" + name + "...");
			List<EventTask> events = getEvents();
			for (EventTask event : events) {
				event.delete(template);
			}
	
			List<EventTask> todos = getToDos();
			for (EventTask todo : todos) {
				todo.delete(template);
			}
			
			Object params[] = {	getId() };		
			String deleteQuery = "DELETE FROM VCalendar WHERE vCalendarID = ?";
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVersion() {
        return version;
    }

    /**
     * @param newVal
     */
    public void setVersion(String newVal) {
        version = newVal;
    }

    public String getCalendarScale() {
        return calendarScale;
    }

    /**
     * @param newVal
     */
    public void setCalendarScale(String newVal) {
        calendarScale = newVal;
    }

    public String getMethod() {
        return method;
    }

    /**
     * @param newVal
     */
    public void setMethod(String newVal) {
        method = newVal;
    }

    public String getName() {
        return name;
    }

    /**
     * @param newVal
     */
    public void setName(String newVal) {
        name = newVal;
    }

    /**
     * Method getEvents
     * @return
     *
     * Vraci vsechny udalosti typu Event v danem kalendari
     */
    public List<EventTask> getEvents() {
        String sql = "SELECT * FROM VEvent,CalComponent,DateTime WHERE (VEvent.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=? AND CalComponent.dateTimeID=DateTime.dateTimeID)";
        Object params[] = {getId()};
        TimeJugglerJDBCTemplate<List<EventTask>> template = new TimeJugglerJDBCTemplate<List<EventTask>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new List<EventTask>();
                EventTask event = new EventTask();	// Vytvori udalost typu Event
                Timestamp ts;
                event.setId(rs.getInt("vEventID"));	//DB
                event.setLocation(rs.getString("location"));
                event.setTransparency(rs.getString("transp"));
                event.setPriority(rs.getInt("priority"));
                event.setGeoGPS(rs.getString("geo"));
                
                //cast calcomponent
                event.setComponentId(rs.getInt("calComponentID"));	//DB
                event.setDescription(rs.getString("description"));
                event.setUid(rs.getString("uid"));
                event.setClazz(rs.getString("clazz"));
                event.setOrganizer(rs.getString("organizer"));
                event.setSequence(rs.getInt("sequence"));
                event.setStatus(rs.getString("status"));
                event.setSummary(rs.getString("summary"));                
                ts = rs.getTimestamp("dtstamp");
                if (ts != null) event.setDTimestamp(new Date(ts.getTime()));
                
                //cast DateTime
                event.getDateTime().setPeriodsId(rs.getInt("periodsID"));
                event.getDateTime().setDistinctDatesId(rs.getInt("distinctDatesID"));
                
                ts = rs.getTimestamp("lastmodified");
                if (ts != null) event.setLastModified(new Date(ts.getTime()));
                ts = rs.getTimestamp("created");
                if (ts != null) event.setCreated(new Date(ts.getTime()));
                ts = rs.getTimestamp("startDate");
                if (ts != null) event.setStartDate(new Date(ts.getTime()));
                ts = rs.getTimestamp("endDate");
                if (ts != null) event.setEndDate(new Date(ts.getTime()));
                                
                // TODO: Nacitat Duration z DB (durationID)
                
                items.add(event);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }
    /**
     * Method getToDos
     * @return
     *
     * Vraci vsechny udalosti typu ToDo v danem kalendari
     */
    public List<EventTask> getToDos() {
        String sql = "SELECT * FROM VToDo,CalComponent,DateTime WHERE (VToDo.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=? AND CalComponent.dateTimeID=DateTime.dateTimeID)";
        Object params[] = { getId() };
        TimeJugglerJDBCTemplate<List<EventTask>> template = new TimeJugglerJDBCTemplate<List<EventTask>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new List<EventTask>();
                EventTask todo = new EventTask(true);	// Vytvori udalost typu ToDo
                Timestamp ts;
                todo.setId(rs.getInt("vToDoID"));
                todo.setLocation(rs.getString("location"));
                todo.setPercentComplete(rs.getInt("percentcomplete"));
                todo.setPriority(rs.getInt("priority"));
                todo.setGeoGPS(rs.getString("geo"));
                ts = rs.getTimestamp("due");
                if (ts != null) todo.setEndDate(new Date(ts.getTime()));
                ts = rs.getTimestamp("completed");
                todo.setCompleted(new Date(ts.getTime()));
                //cast calcomponent
                todo.setComponentId(rs.getInt("calComponentID"));	//DB
                todo.setDescription(rs.getString("description"));
                todo.setUid(rs.getString("uid"));
                todo.setClazz(rs.getString("clazz"));
                todo.setOrganizer(rs.getString("organizer"));
                todo.setSequence(rs.getInt("sequence"));
                todo.setStatus(rs.getString("status"));
                todo.setSummary(rs.getString("summary"));
                ts = rs.getTimestamp("dtstamp");
                if (ts != null) todo.setDTimestamp(new Date(ts.getTime()));
                //cast DateTime                
                todo.getDateTime().setPeriodsId(rs.getInt("periodsID"));
                todo.getDateTime().setDistinctDatesId(rs.getInt("distinctDatesID"));
                ts = rs.getTimestamp("lastmodified");
                if (ts != null) todo.setLastModified(new Date(ts.getTime()));
                ts = rs.getTimestamp("created");
                if (ts != null) todo.setCreated(new Date(ts.getTime()));
                ts = rs.getTimestamp("startDate");
                if (ts != null) todo.setStartDate(new Date(ts.getTime()));

				// TODO: Nacitat Duration z DB (durationID)
                
                items.add(todo);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }

	/**
	 * Method getEvents
	 *
	 *
	 * @return
	 *
	 */
	public List<EventTask> getEvents(Date startDate,Date endDate) {
		// TODO: Napsat SELECT pro vraceni udalosti mezi danymi casy
		return null;
	}
}