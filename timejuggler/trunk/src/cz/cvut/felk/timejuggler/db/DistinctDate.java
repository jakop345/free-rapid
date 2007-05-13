package cz.cvut.felk.timejuggler.db;

import java.util.Date;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:47:15
 * Hotovo
 */
public class DistinctDate extends DbElement {
	//TODO : Logging
	private Date date;
	private int distinctDatesId;

	public DistinctDate(){

	}
	
	public void store(){
	}

	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template){
		if (getId() > 0) {
			Object params[] = { date, distinctDatesId, getId() };
			String updateQuery = "UPDATE DistinctDate SET Date=?,distinctDatesID=?) WHERE distinctDateID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
			Object params[] = { date, distinctDatesId };
			String insertQuery = "INSERT INTO DistinctDate (Date,distinctDatesID) VALUES (?,?) ";
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
			String deleteQuery = "DELETE FROM DistinctDate WHERE distinctDateID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

	public Date getDate(){
		return date;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setDate(Date newVal){
		date = newVal;
	}

	
	public void setDistinctDatesId(int distinctDatesId) {
		this.distinctDatesId = distinctDatesId; 
	}

	public int getDistinctDatesId() {
		return (this.distinctDatesId); 
	}

}