package cz.cvut.felk.timejuggler.db.entity;

import java.util.List;
import java.util.Iterator;
import java.util.logging.Logger;
/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:40:08
 * Hotovo
 */
public class RepetitionRules extends DbElement implements Iterable {
	private final static Logger logger = Logger.getLogger(RepetitionRules.class.getName());
	
	private List<RepetitionRule> repetitionRules;
	private int repetitionRulesId;
	
	public RepetitionRules(){
		repetitionRules = new List<RepetitionRule>();
	}

	public void store(){
	}

	/**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			//bez update
		}else{
			logger.info("Database - Insert: RepetitionRules[]...");
			String insertQuery = "INSERT INTO RepetitionRules";
			template.executeUpdate(insertQuery, null);
			setId(template.getGeneratedId());			
		}
		
		for (RepetitionRule rule : repetitionRules) {
			rule.setRepetitionRulesID(getId());
			rule.saveOrUpdate(template);
		}
	}

	/**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		for (RepetitionRule rule : repetitionRules) {
			rule.delete(template);
		}
		if (getId() > 0) {
			String deleteQuery = "DELETE FROM RepetitionRules WHERE repetitionRulesID = ? ";		
			Object params[] = { getId() };
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

	public void addRule(RepetitionRule rule){
		repetitionRules.add(rule);
	}
	
	public void setRepetitionRulesId(int repetitionRulesId) {
		this.repetitionRulesId = repetitionRulesId; 
	}

	public int getRepetitionRulesId() {
		return (this.repetitionRulesId); 
	}

	/**
	 * Method iterator
	 *
	 *
	 * @return
	 *
	 */
	public Iterator iterator() {
		return repetitionRules.iterator();
	}
}