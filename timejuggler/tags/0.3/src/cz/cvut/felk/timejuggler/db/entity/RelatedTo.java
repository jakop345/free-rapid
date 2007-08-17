package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PropertyEntity;

import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:39:36 Hotovo
 */
public class RelatedTo extends DbElement implements PropertyEntity {
    private final static Logger logger = Logger.getLogger(RelatedTo.class.getName());
    private String relatedto = "";
    private int componentId;

    public RelatedTo() {

    }

    public RelatedTo(String relatedto) {
        this.relatedto = relatedto;
    }

    public void store() {

    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            Object params[] = {relatedto, componentId, getId()};
            String updateQuery = "UPDATE RelatedTo SET relatedto=?,calComponentID=? WHERE relatedToID =? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            Object params[] = {relatedto, componentId};
            String insertQuery = "INSERT INTO RelatedTo (relatedto,calComponentID) VALUES (?,?) ";
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
            String deleteQuery = "DELETE FROM RelatedTo WHERE relatedToID = ?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }


    public String getRelatedto() {
        return relatedto;
    }

    /**
     * @param newVal
     */
    public void setRelatedto(String newVal) {
        relatedto = newVal;
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
        return relatedto;
    }

    /**
     * Method setValue
     * @param newVal
     */
    public void setValue(String newVal) {
		relatedto = newVal;
	}

}