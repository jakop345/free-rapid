package cz.cvut.felk.timejuggler.db.entity.interfaces;

import cz.cvut.felk.timejuggler.db.entity.RepetitionRules;
import cz.cvut.felk.timejuggler.db.entity.DistinctDates;
import cz.cvut.felk.timejuggler.db.entity.Duration;

import java.util.Date;

public interface PeriodEntity extends EntityElement {
	
	void setEndDate(Date endDate);
	
	void setStartDate(Date startDate);
	
	void setDuration(Duration duration);
	
	Date getEndDate();
	
	Date getStartDate();
	
	Duration getDuration();
	
	void setRepetitionRules(RepetitionRules repetitionRules);
	
	void setExceptionRules(RepetitionRules exceptionRules);
	
	void setExceptionDates(DistinctDates exceptionDates);
	
	RepetitionRules getRepetitionRules();
	
	RepetitionRules getExceptionRules();
	
	DistinctDates getExceptionDates();
	
}

