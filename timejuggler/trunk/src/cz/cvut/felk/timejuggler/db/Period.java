package cz.cvut.felk.timejuggler.db;

import java.sql.Time;
import java.util.Date;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:44
 * Hotovo
 */
public class Period extends DbElement {
	//TODO : Logging
	//TODO : startTime, endTime !?
	private Date endDate;
	private Date startDate;
	private Time startTime;
	private Time endTime;
	
	private RepetitionRules repetitionRules;	//rrule
	private RepetitionRules exceptionRules;		//exrule	
	private DistinctDates exceptionDates;		//exdate

	private int repetitionRulesId;
	private int exceptionRulesId;
	private int exceptionDatesId;

		
	private Duration duration;
	private int periodsId;

	public Period(){
	}

	public Period(Date startDate, Date endDate){
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Period(Date startDate, Duration duration){
		this.startDate = startDate;
		this.duration = duration;
	}
	
	public Period(Date startDate){
		this.startDate = startDate;
	}

	
	public void store(){
	}

	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		if (duration != null) {
			duration.saveOrUpdate(template);
		}
		if (repetitionRules != null) {
			repetitionRules.saveOrUpdate(template);
		}
		if (exceptionRules != null) {
			exceptionRules.saveOrUpdate(template);
		}
		if (exceptionDates != null) {
			exceptionDates.saveOrUpdate(template);
		}
		
		if (getId() > 0) {
			Object params[] = { startDate, startTime, endDate, endTime, 
				(repetitionRules == null ? null : repetitionRules.getId()), 
				(exceptionRules == null ? null : exceptionRules.getId()), 
				(duration == null ? null : duration.getId()), periodsId, 
				(exceptionDates == null ? null : exceptionDates.getId()), getId() };
			String updateQuery = "UPDATE Period SET startDate=?,startTime=?,endDate=?,endTime=?,rrule=?,exrule=?,durationID=?,periodsID=?,distinctDatesID=?) WHERE periodID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
			Object params[] = { startDate, startTime, endDate, endTime, repetitionRules.getId(), exceptionRules.getId(), duration.getId(), periodsId, exceptionDates.getId()};
			String insertQuery = "INSERT INTO Period (startDate,startTime,endDate,endTime,rrule,exrule,durationID,periodsID,distinctDatesID) VALUES (?,?,?,?,?,?,?,?,?)";
			template.executeUpdate(insertQuery, params);
			setId(template.getGeneratedId());
		}
	}
	
	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		if (repetitionRules != null) repetitionRules.delete(template);
		if (exceptionRules != null) exceptionRules.delete(template);
		if (exceptionDates != null) exceptionDates.delete(template);
		
		if (getId() > 0) {
			String deleteQuery = "DELETE FROM Period WHERE periodID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
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

	
	public void setRepetitionRules(RepetitionRules repetitionRules) {
		this.repetitionRules = repetitionRules; 
	}

	public void setExceptionRules(RepetitionRules exceptionRules) {
		this.exceptionRules = exceptionRules; 
	}

	public void setExceptionDates(DistinctDates exceptionDates) {
		this.exceptionDates = exceptionDates; 
	}

	public void setRepetitionRulesId(int repetitionRulesId) {
		this.repetitionRulesId = repetitionRulesId; 
	}

	public void setExceptionRulesId(int exceptionRulesId) {
		this.exceptionRulesId = exceptionRulesId; 
	}

	public void setExceptionDatesId(int exceptionDatesId) {
		this.exceptionDatesId = exceptionDatesId; 
	}

	public RepetitionRules getRepetitionRules() {
		//TODO : SELECT FROM RepetitionRule
		return (this.repetitionRules); 
	}

	public RepetitionRules getExceptionRules() {
		//TODO : SELECT FROM RepetitionRule
		return (this.exceptionRules); 
	}

	public DistinctDates getExceptionDates() {
		//TODO : SELECT FROM DistinctDate
		return (this.exceptionDates); 
	}

	public int getRepetitionRulesId() {
		return (this.repetitionRulesId); 
	}

	public int getExceptionRulesId() {
		return (this.exceptionRulesId); 
	}

	public int getExceptionDatesId() {
		return (this.exceptionDatesId); 
	}

}