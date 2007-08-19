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
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (getId() > 0) {
            Object params[] = {attach, componentId, (isBinary ? 1 : 0), getId()};
            String updateQuery = "UPDATE Attachment SET attach=?,calComponentID=?,isBinary=? WHERE attachmentID = ? ";
            template.executeUpdate(updateQuery, params);
        } else {
            Object params[] = {attach, componentId, (isBinary ? 1 : 0)};
            String insertQuery = "INSERT INTO Attachment (attach,calComponentID,isBinary) VALUES (?,?,?) ";
            template.executeUpdate(insertQuery, params);
            setId(template.getGeneratedId());
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (getId() > 0) {
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM Attachment WHERE attachmentID = ?";
            template.executeUpdate(deleteQuery, params);
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