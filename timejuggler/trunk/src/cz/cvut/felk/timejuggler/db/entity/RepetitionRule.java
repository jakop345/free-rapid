package cz.cvut.felk.timejuggler.db.entity;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:38:50
 * Hotovo
 */
public class RepetitionRule  extends DbElement {
	//TODO : Logging
	//TODO : Pridat operace pro manipulaci s opakovanim + konstanty
	
	private int frequency;
	private int interval;
	private int repeat;
	private int weekStart; //TODO: default value = MONDAY
	
	private String byHour = "";
	private String byWeekNo = "";
	private String byYearDay = "";
	private String bySetPosition = "";
	private String byMonth = "";
	private String byMinute = "";
	private String byMonthDay = "";
	
	private int repetitionRulesID;
	/*
	public ExceptionRules m_ExceptionRules;
	public RepetitionRules m_RepetitionRules;
	public NumberList m_NumberList;
	public ByDayOfWeek m_ByDayOfWeek;
	*/

	public RepetitionRule(){

	}

	public void store(){
	}
	 
	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		
		if (getId() > 0) {
	 		Object params[] = { repetitionRulesID, frequency, interval, repeat, 
	 							weekStart, byHour, byWeekNo, byYearDay, 
	 							bySetPosition, byMonth, byMinute, byMonthDay, getId() };
			String updateQuery = "UPDATE RepetitionRule SET repetitionRulesID=?,frequency=?,interval=?,repeat=?,weekStart=?,byHour=?,byWeekNo=?,byYearDay=?,bySetPosition=?,byMonth=?,byMinute=?,byMonthDay=?) WHERE repetitionRuleID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
	 		Object params[] = { repetitionRulesID, frequency, interval, repeat, 
	 							weekStart, byHour, byWeekNo, byYearDay, 
	 							bySetPosition, byMonth, byMinute, byMonthDay };
			String insertQuery = "INSERT INTO RepetitionRule (repetitionRulesID,frequency,interval,repeat,weekStart,byHour,byWeekNo,byYearDay,bySetPosition,byMonth,byMinute,byMonthDay) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
			template.executeUpdate(insertQuery, params);
			setId(template.getGeneratedId());			
		}
	}

	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			String deleteQuery = "DELETE FROM RepetitionRule WHERE repetitionRuleID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}
	
	public int getFrequency(){
		return frequency;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFrequency(int newVal){
		frequency = newVal;
	}

	public int getInterval(){
		return interval;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setInterval(int newVal){
		interval = newVal;
	}

	public int getRepeat(){
		return repeat;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setRepeat(int newVal){
		repeat = newVal;
	}

	public int getWeekStart(){
		return weekStart;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setWeekStart(int newVal){
		weekStart = newVal;
	}

	
	public void setRepetitionRulesID(int repetitionRulesID) {
		this.repetitionRulesID = repetitionRulesID; 
	}

	public int getRepetitionRulesID() {
		return (this.repetitionRulesID); 
	}

}