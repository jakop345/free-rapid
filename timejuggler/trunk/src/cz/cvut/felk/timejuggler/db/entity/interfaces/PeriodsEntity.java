package cz.cvut.felk.timejuggler.db.entity.interfaces;

public interface PeriodsEntity extends Iterable<PeriodEntity> {
	void addPeriod(PeriodEntity period);
}
