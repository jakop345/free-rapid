package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;

import java.util.List;
import java.util.Iterator;
import java.util.logging.Logger;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:46:34
 * Hotovo
 */
public class DistinctDates extends DbElement implements Iterable {
	private final static Logger logger = Logger.getLogger(DistinctDates.class.getName());
	
	private List<DistinctDate> distinctDates;
	private int distinctDatesId;
	
	public DistinctDates(){
		distinctDates = new List<DistinctDate>();
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
			logger.info("Database - Insert: DistinctDates[]...");
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
	
	public void setDistinctDatesId(int distinctDatesId) {
		this.distinctDatesId = distinctDatesId; 
	}

	public int getDistinctDatesId() {
		return (this.distinctDatesId); 
	}

	/**
	 * Method iterator
	 *
	 *
	 * @return
	 *
	 */
	public Iterator iterator() {
		return distinctDates.iterator();
	}

	/**
	 * Method addDistinctDate
	 *
	 *
	 */
	public void addDistinctDate(DistinctDate date) {
		distinctDates.add(date);
	}

}