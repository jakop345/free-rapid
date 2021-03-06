package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:21 Hotovo
 */
public class Attachment extends DbElement {
    //TODO : Logging
    private String attach = "";
    private boolean isBinary = false;

    private int componentId;

    public Attachment() {

    }

    public Attachment(String attach) {
        this.attach = attach;
    }

    public void store() {

    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            Object params[] = {attach, componentId, (isBinary ? 1 : 0), getId()};
            String updateQuery = "UPDATE Attachment SET attach=?,calComponentID=?,isBinary=? WHERE attachmentID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            Object params[] = {attach, componentId, (isBinary ? 1 : 0)};
            String insertQuery = "INSERT INTO Attachment (attach,calComponentID,isBinary) VALUES (?,?,?) ";
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
            String deleteQuery = "DELETE FROM Attachment WHERE attachmentID = ?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }

    public String getAttach() {
        return attach;
    }

    /**
     * @param newVal
     */
    public void setAttach(String newVal) {
        attach = newVal;
    }

    public boolean isIsBinary() {
        return isBinary;
    }

    /**
     * @param newVal
     */
    public void setIsBinary(boolean newVal) {
        isBinary = newVal;
    }


    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
		return (this.componentId); 
	}

}