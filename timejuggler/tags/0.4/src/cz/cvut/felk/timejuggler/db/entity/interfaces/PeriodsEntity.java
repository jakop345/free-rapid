package cz.cvut.felk.timejuggler.db.entity.interfaces;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * interface
 */
 
public interface PeriodsEntity extends Iterable<PeriodEntity> {
	void addPeriod(PeriodEntity period);
}
