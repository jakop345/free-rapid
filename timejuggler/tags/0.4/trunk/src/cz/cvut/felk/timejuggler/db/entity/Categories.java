package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 *          <p/>
 *          <p/>
 *          Propojovaci tabulka ke kategoriim a eventum
 */

public class Categories extends DbElement {
    private final static Logger logger = Logger.getLogger(Categories.class.getName());

    private int categoryId;
    private int componentId;

    //TODO: ....
    private List<Category> categories;

    public Categories() {
        categories = new ArrayList<Category>();
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {

        if (getId() > 0) {

            /* TODO update nebude asi potreba.. pri zmene kategorii v eventu provest DELETE + INSERT ?
            
            logger.info("Database - Update: Categories[" + getId() + "]:" + name + "...");
            Object params[] = {name, componentId, getId()};
            String updateQuery = "UPDATE Categories SET name=?,calComponentID=? WHERE categoryID = ? ";
            template.executeUpdate(updateQuery, params);
            
            */
        } else {
            logger.info("Database - Insert: Categories[]:...");
            String insertQuery = "INSERT INTO Categories (categoryID,calComponentID) VALUES (?,?) ";
            for (Category c : categories) {
                Object params[] = {c.getId(), componentId};
                template.executeUpdate(insertQuery, params);
                /*setId(template.getGeneratedId());*/
            }
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (componentId > 0) {
            Object params[] = {componentId};
            String deleteQuery = "DELETE FROM Categories WHERE calComponentID = ? ";
            template.executeUpdate(deleteQuery, params);
            setComponentId(-1);
        }
    }

    public void store() {

    }

    public void addCategory(Category cat) {
        categories.add(cat);
    }

    public void removeCategory(Category cat) {
        categories.remove(cat);
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
        return (this.componentId);
    }
}
