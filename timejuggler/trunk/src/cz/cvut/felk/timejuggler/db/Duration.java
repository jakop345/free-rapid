package cz.cvut.felk.timejuggler.db;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:50
 */
public class Duration extends DbElement {

	private boolean negative = false;
	private int days;
	private int weeks;
	private int hours;
	private int minutes;
	private int seconds;

	public Duration(){

	}

	public void store(){
		
		
	}
	public void store(TimeJugglerJDBCTemplate template) {
		Object params[] = { (negative ? 1 : 0), days, weeks, hours, minutes, seconds };
		String insertQuery = "INSERT INTO Duration (negative,days,weeks,hours,minutes,seconds) VALUES (?,?,?,?,?,?)";
		template.executeUpdate(insertQuery, params);
		setId(template.getGeneratedId());
	}
	
	public void setNegative(boolean negative) {
		this.negative = negative; 
	}

	public void setDays(int days) {
		this.days = days; 
	}

	public void setWeeks(int weeks) {
		this.weeks = weeks; 
	}

	public void setHours(int hours) {
		this.hours = hours; 
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes; 
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds; 
	}

	public boolean isNegative() {
		return (this.negative); 
	}

	public int getDays() {
		return (this.days); 
	}

	public int getWeeks() {
		return (this.weeks); 
	}

	public int getHours() {
		return (this.hours); 
	}

	public int getMinutes() {
		return (this.minutes); 
	}

	public int getSeconds() {
		return (this.seconds); 
	}

}