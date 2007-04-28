package cz.cvut.felk.timejuggler.db;

import java.sql.Time;
import java.util.Date;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:44
 */
public class Period extends DbElement {

	private Date endDate;
	private Date startDate;
	private Time startTime;
	private Time endTime;
	
	//private int rrule;
	//private int exrule;
	private Duration duration;
	private int periodsId;
	
	//private ExceptionRules m_ExceptionRules;
	//private RepetitionRules m_RepetitionRules;
	//private ExceptionDates m_ExceptionDates;
	//private DistinctDates m_DistinctDates;
	

	public Period(){

	}

	public void store(){
	}
	
	public void store(TimeJugglerJDBCTemplate template) {
		Integer durationId = null;
		if (duration != null) {
			duration.store(template);
			durationId = duration.getId();
		}
		
		Object params[] = { getStartDate(), getEndDate(), null, null, null, durationId, periodsId, null};
		String insertQuery = "INSERT INTO Period (startDate,endDate,rrule,exrule,timeID,durationID,periodsID,distinctDatesID) VALUES (?,?,?,?,?,?,?,?)";
		template.executeUpdate(insertQuery, params);
		setId(template.getGeneratedId());
	}

	
	public void setEndDate(Date endDate) {
		this.endDate = endDate; 
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate; 
	}

	public void setDuration(Duration duration) {
		this.duration = duration; 
	}

	public Date getEndDate() {
		return (this.endDate); 
	}

	public Date getStartDate() {
		return (this.startDate); 
	}

	public Duration getDuration() {
		return (this.duration); 
	}

	
	public void setPeriodsId(int periodsId) {
		this.periodsId = periodsId; 
	}

	public int getPeriodsId() {
		return (this.periodsId); 
	}

}