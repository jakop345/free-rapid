package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:37
 *
 * trida reprezentujici casove udaje udalosti 
 * (zacatek+konec platnosti, cas zmeny, casove useky (periods))
 */
public class DateTime extends DbElement {
	private final static Logger logger = Logger.getLogger(DateTime.class.getName());
	
	private Timestamp created;	//datum vytvoreni objektu v databazi
	private Timestamp lastModified;
	private Periods periods;	// jednotlive useky
	private int periodsId;
	private DistinctDates distinctDates;	//rdate
	private int distinctDatesId;
	
	// TODO: upravit ukladani / cteni z DB
	private Timestamp endDate;	// datum kdy konci platnost udalosti (nemusi byt)
	// TODO: nacitat Duration z DB
	private Duration duration;	// delka trvani udalosti (nemusi byt, alternativa k endDate)
	private Timestamp startDate;	// zacatek platnosti udalosti
	/* pokud udalost nema ani koncove datum, ani delku trvani, jedna se o "vyroci", 
	 * tedy plati "cely den" (+navic pripadne opakovani ...)*/
	
	public DateTime() {
	}

	/**
     * Method saveOrUpdate
     * @param template
     *
     * Ulozeni casovych udaju do databaze / update udaju
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		/* ulozeni presnych datumu */
		if (distinctDates != null) {
			distinctDates.saveOrUpdate(template);
		}
		/* ulozeni period */
		if (periods != null) {
			periods.saveOrUpdate(template);
		}
		
		/* ulozeni - delka trvani udalosti (alternativa k endDate) */
		if (duration != null) {
			duration.saveOrUpdate(template);
		}

		if (getId() > 0) {
			logger.info("Database - Update: DateTime[" + getId() + "]...");
			Object params[] = {
				(distinctDates == null ? null : distinctDates.getId()), 
				(periods == null ? null : periods.getId()), created, lastModified,
				startDate, endDate,
				(duration == null ? null : duration.getId()),
				getId()
			};
			String updateQuery = "UPDATE DateTime SET distinctDatesID=?,periodsID=?,created=?,lastmodified=?, startDate=?, endDate=?, durationID=? WHERE dateTimeID = ?";
			template.executeUpdate(updateQuery, params);
		}else{
			//setCreated(new Timestamp(new Date().getTime()));
			logger.info("Database - Insert: DateTime[]...startdate=" + startDate);
			Object params[] = {
				(distinctDates == null ? null : distinctDates.getId()), 
				(periods == null ? null : periods.getId()), created, lastModified, 
				startDate, endDate,
				(duration == null ? null : duration.getId())
			};
			String insertQuery = "INSERT INTO DateTime (distinctDatesID,periodsID,created,lastmodified,startDate,endDate,durationID ) VALUES (?,?,?,?,?,?,?)";
			template.executeUpdate(insertQuery, params);
			setId(template.getGeneratedId());
		}
	}

	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		if (distinctDates != null) {
			distinctDates.delete(template);
		}
		if (periods != null) {
			periods.delete(template);
		}
		
		if (getId() > 0) {
			String deleteQuery = "DELETE FROM DateTime WHERE dateTimeID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

	public void store(){
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = (startDate == null ? null : new Timestamp(startDate.getTime()));
	}

	public void setCreated(Date created) {
		this.created = (created == null ? null :new Timestamp(created.getTime()));
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = (lastModified == null ? null :new Timestamp(lastModified.getTime()));
	}

	public void setPeriods(Periods periods) {
		this.periods = periods; 
	}

	public Date getStartDate() {
		return (this.startDate == null ? null : new Date(this.startDate.getTime()));
	}

	public Date getCreated() {
		return (this.created == null ? null : new Date(created.getTime()));
	}

	public Date getLastModified() {
		return (this.lastModified == null ? null : new Date(lastModified.getTime()));
	}

	public Periods getPeriods() {
		// TODO: SELECT .. JOIN - Duration
		String sql = "SELECT * FROM Period WHERE periodsID = ?";
        Object params[] = {periodsId};
        TimeJugglerJDBCTemplate<Periods> template = new TimeJugglerJDBCTemplate<Periods>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new Periods();
            	
            	Period period = new Period();
            	Timestamp ts;
            	ts = rs.getTimestamp("startDate");
            	if (ts != null) period.setStartDate(new Date(ts.getTime()));
            	
            	//TODO: SELECT, durationID
            	//period.setDuration();
            	
            	ts = rs.getTimestamp("endDate");
            	if (ts != null) period.setEndDate(new Date(ts.getTime()));
                
                items.addPeriod(period);
            }
        };
        template.executeQuery(sql, params);
        return template.getItems();
	}

	
	public void setDistinctDates(DistinctDates distinctDates) {
		this.distinctDates = distinctDates; 
	}

	public DistinctDates getDistinctDates() {
		//TODO : SELECT FROM DistinctDate
		return (this.distinctDates); 
	}

	
	public void setEndDate(Date endDate) {
		this.endDate = (endDate == null ? null : new Timestamp(endDate.getTime()));
	}
	
	public void setEndDate(Duration dur) {
		this.duration = dur;
	}

	public Date getEndDate() {
		// Vypocita koncove datum platnosti nebo vrati null;
		if (endDate == null) {
			Date myEndDate = new Date(startDate.getTime());
			if (duration == null) {
				return null;	// udalost nema konec (vyroci), vrati null 
			}else{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(myEndDate);
				calendar.add(Calendar.SECOND, duration.getSeconds());
				calendar.add(Calendar.MINUTE, duration.getMinutes());
				calendar.add(Calendar.HOUR, duration.getHours());
				calendar.add(Calendar.DATE, duration.getDays());
				calendar.add(Calendar.DATE, 7 * duration.getWeeks());
				return calendar.getTime();
			}
		}
		return new Date(endDate.getTime());
	}

	
	public void setPeriodsId(int periodsId) {
		this.periodsId = periodsId; 
	}

	public void setDistinctDatesId(int distinctDatesId) {
		this.distinctDatesId = distinctDatesId; 
	}

	public int getPeriodsId() {
		return (this.periodsId); 
	}

	public int getDistinctDatesId() {
		return (this.distinctDatesId); 
	}



}