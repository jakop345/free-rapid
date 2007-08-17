package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PropertyEntity;

import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:39:18 Hotove
 */
public class Resource extends DbElement implements PropertyEntity {
    private final static Logger logger = Logger.getLogger(Resource.class.getName());
    private String resource = "";

    private int componentId;

    public Resource() {

    }

    public Resource(String resource) {
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
            Object params[] = {resource, componentId, getId()};
            String updateQuery = "UPDATE Resource SET resource=?,calComponentID=? WHERE resourceID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            Object params[] = {resource, componentId};
            String insertQuery = "INSERT INTO Resource (resource,calComponentID) VALUES (?,?)";
            try {
                template.executeUpdate(insertQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(template.getGeneratedId());
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM Resource WHERE resourceID = ?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }


    public String getResource() {
        return resource;
    }

    /**
     * @param newVal
     */
    public void setResource(String newVal) {
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
     * @return
     */
    public String getValue() {
        return resource;
    }

    /**
     * Method setValue
     * @param newVal
     */
    public void setValue(String newVal) {
		resource = newVal;
	}

}