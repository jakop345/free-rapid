package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:37
 */
public class DateTime extends DbElement {

	private Date startDate;
	private Timestamp created;	//datum vytvoreni objektu v databazi
	private Timestamp lastModified;
	private Vector<Period> periods;
	private Vector<Date> distinctDates;	//rdate

	public DateTime(){

	}
	
	public void store(TimeJugglerJDBCTemplate template) {
		setCreated(new Timestamp(new Date().getTime()));
		String insertQuery;
		
		Integer distinctDatesId = null;
		/* ulozeni presnych datumu */
		if (distinctDates != null) {
			insertQuery = "INSERT INTO DistinctDates";
			template.executeUpdate(insertQuery, null);
			distinctDatesId = template.getGeneratedId();
			
			for (Date date: distinctDates) {
				Object params[] = { date, distinctDatesId };
				insertQuery = "INSERT INTO DistinctDate (Date,distinctDatesID) VALUES (?,?) ";
				template.executeUpdate(insertQuery, params);
			}
		}
			
		Integer periodsId = null;
		/* ulozeni period */
		if (periods != null) {
			insertQuery = "INSERT INTO Periods";
			template.executeUpdate(insertQuery, null);
			periodsId = template.getGeneratedId();
			
			for (Period period: periods) {
				period.setPeriodsId(periodsId);
				period.store(template);
			}
		}
		
		Object params[] = {
			distinctDatesId, getStartDate(), periodsId, getCreated(), getLastModified()
		};
		System.out.println ("Storing Datetime, created = " + getCreated());
		insertQuery = "INSERT INTO DateTime (distinctDatesID,dtstart,periodsID,created,lastmodified) VALUES (?,?,?,?,?)";
		template.executeUpdate(insertQuery, params);
		setId(template.getGeneratedId());
	}

	public void store(){
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate; 
	}

	public void setCreated(Timestamp created) {
		this.created = created; 
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified; 
	}

	public void setPeriods(Vector<Period> periods) {
		this.periods = periods; 
	}

	public Date getStartDate() {
		return (this.startDate); 
	}

	public Timestamp getCreated() {
		return (this.created); 
	}

	public Timestamp getLastModified() {
		return (this.lastModified); 
	}

	public Vector<Period> getPeriods() {
		return (this.periods); 
	}

	
	public void setDistinctDates(Vector<Date> distinctDates) {
		this.distinctDates = distinctDates; 
	}

	public Vector<Date> getDistinctDates() {
		return (this.distinctDates); 
	}



}