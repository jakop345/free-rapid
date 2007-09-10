package cz.cvut.felk.timejuggler.db.entity.interfaces;

import cz.cvut.felk.timejuggler.db.entity.interfaces.DistinctDateEntity;
/**
 * @author Jan Struz
 * @version 0.3
 * 
 * interface
 * 
 */

public interface DistinctDatesEntity extends Iterable<DistinctDateEntity> {
	//TODO: jedno add vyhodit
	void addDate(DistinctDateEntity date);
	
	void addDistinctDate(DistinctDateEntity date);
}
