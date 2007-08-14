package cz.cvut.felk.timejuggler.db.entity.interfaces;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * 
 */
 
public interface VCalendarEntity extends EntityElement {
	
	String getVersion();
	
	void setVersion(String newVal);
	
	void setProductId(String productId);
	
	String getProductId();
	
	void setCalendarScale(String newVal);
	
	String getCalendarScale();
	
	String getMethod();
	
	void setMethod(String newVal);
	
	String getName();
	
	void setName(String newVal);
	
	Object clone() throws CloneNotSupportedException;
}
