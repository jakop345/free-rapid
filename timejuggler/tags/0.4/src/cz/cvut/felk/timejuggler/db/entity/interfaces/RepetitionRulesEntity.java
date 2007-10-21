package cz.cvut.felk.timejuggler.db.entity.interfaces;

/**
 * @author Jan Struz
 * @version 0.3
 * 
 */
 
public interface RepetitionRulesEntity extends Iterable<RepetitionRuleEntity> {
	void addRule(RepetitionRuleEntity rule);
}
