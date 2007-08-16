package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;
import java.util.logging.Logger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PropertyEntity;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:39:18
 * Hotove
 */
public class Resource extends DbElement implements PropertyEntity{
	private final static Logger logger = Logger.getLogger(Resource.class.getName());
	private String resource = "";
	
	private int componentId;

	public Resource(){

	}
	public Resource(String resource){
		this.resource = resource;
	}

    public void store() {

    }

	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			Object params[] = { resource, componentId, getId() };
			String updateQuery = "UPDATE Resource SET resource=?,calComponentID=? WHERE resourceID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
	        Object params[] = { resource, componentId };
	        String insertQuery = "INSERT INTO Resource (resource,calComponentID) VALUES (?,?)";
	        template.executeUpdate(insertQuery, params);
	        setId(template.getGeneratedId());
		}
	}
	
	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			Object params[] = {	getId() };		
			String deleteQuery = "DELETE FROM Resource WHERE resourceID = ?";
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}


	public String getResource(){
		return resource;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setResource(String newVal){
		resource = newVal;
	}

	
	public void setComponentId(int componentId) {
		this.componentId = componentId; 
	}

	public int getComponentId() {
		return (this.componentId); 
	}

	/**
	 * Method getValue
	 *
	 *
	 * @return
	 *
	 */
	public String getValue() {
		return resource;
	}

	/**
	 * Method setValue
	 *
	 *
	 * @param newVal
	 *
	 */
	public void setValue(String newVal) {
		resource = newVal;
	}

}