package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.RepetitionRuleEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.RepetitionRulesEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:40:08 Hotovo
 */
public class RepetitionRules extends DbElement implements Iterable<RepetitionRuleEntity>, RepetitionRulesEntity {
    private final static Logger logger = Logger.getLogger(RepetitionRules.class.getName());

    private List<RepetitionRuleEntity> repetitionRules;
    private int repetitionRulesId;

    public RepetitionRules() {
        repetitionRules = new ArrayList<RepetitionRuleEntity>();
    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (getId() > 0) {
            //bez update
        } else {
            logger.info("Database - Insert: RepetitionRules[]...");
            String insertQuery = "INSERT INTO RepetitionRules (repetitionRulesID) VALUES (DEFAULT)";
            template.executeUpdate(insertQuery, null);
            setId(template.getGeneratedId());
        }

        for (RepetitionRuleEntity rule : repetitionRules) {
            ((RepetitionRule)rule).setRepetitionRulesID(getId());
            ((RepetitionRule)rule).saveOrUpdate(template);
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        for (RepetitionRuleEntity rule : repetitionRules) {
            ((RepetitionRule)rule).delete(template);
        }
        if (getId() > 0) {
            String deleteQuery = "DELETE FROM RepetitionRules WHERE repetitionRulesID = ? ";
            Object params[] = {getId()};
            template.executeUpdate(deleteQuery, params);
            setId(-1);
        }
    }

    public void addRule(RepetitionRuleEntity rule) {
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
     * @return
     */
    public Iterator<RepetitionRuleEntity> iterator() {
        return repetitionRules.iterator();
	}
}