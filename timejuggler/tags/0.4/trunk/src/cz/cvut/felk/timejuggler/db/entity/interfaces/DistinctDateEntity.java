package cz.cvut.felk.timejuggler.db.entity.interfaces;

import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.3
 * 
 * interface pro polozku datumu
 */

public interface DistinctDateEntity extends EntityElement {
	Date getDate();
	void setDate(Date newVal);
}
