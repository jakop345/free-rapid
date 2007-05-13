package cz.cvut.felk.timejuggler.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.Date;
/**
 * @version 0.1
 * @created 14-IV-2007 16:38:21
 */
public class VCalendar extends DbElement {
	//TODO : Logging
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
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			Object params[] = { productId, version, calendarScale, method, name, getId() };
			String updateQuery = "UPDATE VCalendar SET prodid=?,version=?,calscale=?,method=?,name=? WHERE vCalendarID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
			// Pridani noveho kalendare do databaze
	        Object params[] = { productId, version, calendarScale, method, name };
	        String insertQuery = "INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES (?,?,?,?,?)";
	        template.executeUpdate(insertQuery, params);
	        // nastaveni klice objektu VCalendar
	        setId(template.getGeneratedId());
		}
	}
	
	/**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template){
		
		Vector<VEvent> events = getEvents();
		for (VEvent event : events) {
			event.delete(template);
		}

		Vector<VToDo> todos = getToDos();
		for (VToDo todo : todos) {
			todo.delete(template);
		}
		//TODO : if getId>0
		
		Object params[] = {	getId() };		
		String deleteQuery = "DELETE FROM VCalendar WHERE vCalendarID = ?";
		template.executeUpdate(deleteQuery, params);
		setId(-1);
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
     * Method getEventsByCalendar
     * @return
     */
    public Vector<VEvent> getEvents() {
        String sql = "SELECT * FROM VEvent,CalComponent,DateTime WHERE (VEvent.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=? AND CalComponent.dateTimeID=DateTime.dateTimeID)";
        Object params[] = {getId()};
        TimeJugglerJDBCTemplate<VEvent> template = new TimeJugglerJDBCTemplate<VEvent>() {
            protected void handleRow(ResultSet rs) throws SQLException {
                VEvent event = new VEvent();
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
                event.setDTimestamp(rs.getTimestamp("dtstamp"));
                
                //cast DateTime
                DateTime datetime = new DateTime();
                datetime.setPeriodsId(rs.getInt("periodsID"));
                datetime.setDistinctDatesId(rs.getInt("distinctDatesID"));
                datetime.setLastModified(rs.getTimestamp("lastmodified"));
                datetime.setCreated(rs.getTimestamp("created"));
                event.setDateTime(datetime);
                
                items.add(event);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
    }
    /**
     * Method getToDos
     * @return
     */
    public Vector<VToDo> getToDos() {
        String sql = "SELECT * FROM VToDo,CalComponent,DateTime WHERE (VToDo.calComponentID = CalComponent.calComponentID AND CalComponent.vCalendarID=? AND CalComponent.dateTimeID=DateTime.dateTimeID)";
        Object params[] = { getId() };
        TimeJugglerJDBCTemplate<VToDo> template = new TimeJugglerJDBCTemplate<VToDo>() {
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
                todo.setComponentId(rs.getInt("calComponentID"));	//DB
                todo.setDescription(rs.getString("description"));
                todo.setUid(rs.getString("uid"));
                todo.setClazz(rs.getString("clazz"));
                todo.setOrganizer(rs.getString("organizer"));
                todo.setSequence(rs.getInt("sequence"));
                todo.setStatus(rs.getString("status"));
                todo.setSummary(rs.getString("summary"));
                todo.setDTimestamp(rs.getTimestamp("dtstamp"));
                //cast datetime
                todo.setLastModified(rs.getTimestamp("lastmodified"));
                todo.setCreated(rs.getTimestamp("created"));

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
	public Vector<VEvent> getEvents(Date startDate,Date endDate) {
		// TODO: Add your code here
		return null;
	}
}