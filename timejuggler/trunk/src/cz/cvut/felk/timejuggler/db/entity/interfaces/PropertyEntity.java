package cz.cvut.felk.timejuggler.db.entity.interfaces;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * 
 */

public interface PropertyEntity extends EntityElement {
	String getValue();
	void setValue(String newVal);
}
