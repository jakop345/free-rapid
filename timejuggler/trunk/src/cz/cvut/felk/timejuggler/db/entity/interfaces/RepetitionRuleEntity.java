package cz.cvut.felk.timejuggler.db.entity.interfaces;


public interface RepetitionRuleEntity extends EntityElement {
	
	int getFrequency();
	
	void setFrequency(int newVal);
	
	int getInterval();
	
	void setInterval(int newVal);
	
	int getRepeat();
	
	void setRepeat(int newVal);
	
	int getWeekStart();
	
	void setWeekStart(int newVal);
	
	public void setByHour(String byHour);

	public void setByWeekNo(String byWeekNo);

	public void setByYearDay(String byYearDay);

	public void setBySetPosition(String bySetPosition);

	public void setByMonth(String byMonth) ;

	public void setByMinute(String byMinute);

	public void setByMonthDay(String byMonthDay) ;

	public String getByHour() ;

	public String getByWeekNo() ;

	public String getByYearDay() ;

	public String getBySetPosition();

	public String getByMonth() ;

	public String getByMinute() ;

	public String getByMonthDay() ;
}
