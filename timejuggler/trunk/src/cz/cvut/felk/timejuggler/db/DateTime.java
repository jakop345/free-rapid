package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Calendar;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:37
 */
public class DateTime extends DbElement {
	//TODO : Logging
	private Timestamp created;	//datum vytvoreni objektu v databazi
	private Timestamp lastModified;
	private Periods periods;
	private int periodsId;
	private DistinctDates distinctDates;	//rdate
	private int distinctDatesId;

	public DateTime() {
		periods = new Periods();
		periods.addPeriod(new Period());
	}

	/**
     * Method saveOrUpdate
     * @param template
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

		if (getId() > 0) {
			Object params[] = {
				(distinctDates == null ? null : distinctDates.getId()), 
				(periods == null ? null : periods.getId()), created, lastModified, getId()
			};
			String updateQuery = "UPDATE DateTime SET distinctDatesID=?,periodsID=?,created=?,lastmodified=?) WHERE dateTimeID = ?";
			template.executeUpdate(updateQuery, params);
		}else{
			setCreated(new Timestamp(new Date().getTime()));

			Object params[] = {
				(distinctDates == null ? null : distinctDates.getId()), 
				(periods == null ? null : periods.getId()), created, lastModified
			};
			String insertQuery = "INSERT INTO DateTime (distinctDatesID,periodsID,created,lastmodified) VALUES (?,?,?,?)";
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
		((Period)periods.getPeriods().get(0)).setStartDate(startDate);
	}

	public void setCreated(Timestamp created) {
		this.created = created; 
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified; 
	}

	public void setPeriods(Periods periods) {
		this.periods = periods; 
	}

	public Date getStartDate() {
		return ((Period)periods.getPeriods().get(0)).getStartDate();
	}

	public Timestamp getCreated() {
		return (this.created); 
	}

	public Timestamp getLastModified() {
		return (this.lastModified); 
	}

	public Periods getPeriods() {
		//TODO : SELECT FROM Period
		return (this.periods); 
	}

	
	public void setDistinctDates(DistinctDates distinctDates) {
		this.distinctDates = distinctDates; 
	}

	public DistinctDates getDistinctDates() {
		//TODO : SELECT FROM DistinctDate
		return (this.distinctDates); 
	}

	
	public void setEndDate(Date endDate) {
		periods.getPeriods().get(0).setEndDate(endDate);
	}

	public Date getEndDate() {
		Date endDate = periods.getPeriods().get(0).getEndDate();
		if (endDate == null) {
			Duration dur = periods.getPeriods().get(0).getDuration();
			endDate = periods.getPeriods().get(0).getStartDate();
			if (dur == null) {
				return endDate;
			}else{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.add(Calendar.SECOND, dur.getSeconds());
				calendar.add(Calendar.MINUTE, dur.getMinutes());
				calendar.add(Calendar.HOUR, dur.getHours());
				calendar.add(Calendar.DATE, dur.getDays());
				calendar.add(Calendar.DATE, 7 * dur.getWeeks());
				return calendar.getTime();
			}
		}
		return endDate;
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