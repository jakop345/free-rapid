package cz.cvut.felk.timejuggler.db.entity.interfaces;

import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.1
 * 
 * 
 */
 
public interface EventTaskEntity extends /*CalComponentEntity*/ EntityElement {
	
	void setPriority(int newVal);
    
    int getPriority();
    
    String getGeoGPS();

    void setGeoGPS(String newVal);

    String getLocation();

    void setLocation(String newVal);
    
    String getTransparency();

    void setTransparency(String newVal);

    int getPercentComplete();

    void setPercentComplete(int newVal);

    Date getCompleted();

    void setCompleted(Date newVal);
    
    void convert();

	void setIsTodo(boolean isTodo);

	boolean getIsTodo();
	
}
