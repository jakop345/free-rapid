package cz.cvut.felk.timejuggler.db.entity.interfaces;

import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.3
 * 
 * interface
 * 
 */
 
public interface PeriodEntity extends EntityElement {
	
	void setEndDate(Date endDate);
	
	void setStartDate(Date startDate);
	
	void setDuration(DurationEntity duration);
	
	Date getEndDate();
	
	Date getStartDate();
	
	DurationEntity getDuration();
	
	void setRepetitionRules(RepetitionRulesEntity repetitionRules);
	
	void setExceptionRules(RepetitionRulesEntity exceptionRules);
	
	void setExceptionDates(DistinctDatesEntity exceptionDates);
	
	RepetitionRulesEntity getRepetitionRules();
	
	RepetitionRulesEntity getExceptionRules();
	
	DistinctDatesEntity getExceptionDates();
	
}

