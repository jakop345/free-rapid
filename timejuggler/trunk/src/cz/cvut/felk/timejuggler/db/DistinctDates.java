package cz.cvut.felk.timejuggler.db;

import java.util.Vector;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:46:34
 * Hotovo
 */
public class DistinctDates extends DbElement {
	//TODO : Logging
	private Vector<DistinctDate> distinctDates;
	private int distinctDatesId;
	
	public DistinctDates(){
		distinctDates = new Vector<DistinctDate>();
	}

	public void store(){
	}

	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {		
		if (getId() > 0) {
			//bez Update
		}else{
			String insertQuery = "INSERT INTO DistinctDates";
			template.executeUpdate(insertQuery, null);
			setId(template.getGeneratedId());
		}
		
		for (DistinctDate date : distinctDates) {
			date.setDistinctDatesId(getId());
			date.saveOrUpdate(template);
		}
	}

	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		for (DistinctDate date : distinctDates) {
			date.delete(template);
		}
		if (getId() > 0) {
			String deleteQuery = "DELETE FROM DistinctDates WHERE distinctDatesID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

	public void addDate(DistinctDate date){
		distinctDates.add(date);
	}
	
	public void setDistinctDates(Vector<DistinctDate> distinctDates) {
		this.distinctDates = distinctDates; 
	}

	public void setDistinctDatesId(int distinctDatesId) {
		this.distinctDatesId = distinctDatesId; 
	}

	public Vector<DistinctDate> getDistinctDates() {
		return (this.distinctDates); 
	}

	public int getDistinctDatesId() {
		return (this.distinctDatesId); 
	}

}