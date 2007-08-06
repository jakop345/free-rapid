package cz.cvut.felk.timejuggler.db.entity;

import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:48
 * <p/>
 * Reprezentuje kategorii eventu nebo tasku.. Hotovo
 */
public class Category extends DbElement implements Comparable {
    private final static Logger logger = Logger.getLogger(Category.class.getName());

    private String name;

    private int componentId = -1;

    private Color color = null;

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Color color) {
        this(name);
        this.color = color;
    }

    public boolean hasAssignedColor() {
        return color != null;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void store() {

    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            logger.info("Database - Update: Category[" + getId() + "]:" + name + "...");
            Object params[] = {name, componentId, getId()};
            String updateQuery = "UPDATE Category SET name=?,calComponentID=? WHERE categoryID = ? ";
            template.executeUpdate(updateQuery, params);
        } else {
            logger.info("Database - Insert: Category[]:" + name + "...");
            Object params[] = {name, componentId};
            String insertQuery = "INSERT INTO Category (name,calComponentID) VALUES (?,?) ";
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
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM Category WHERE categoryID = ? ";
            template.executeUpdate(deleteQuery, params);
            setId(-1);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;
        return componentId == category.componentId && name.equals(category.name);
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 31 * result + componentId;
        return result;
    }

    public int compareTo(Object o) {
        return getName().compareTo(((Category) o).getName()); //pridat i podporu pro componentId?
    }

    public String getName() {
        return name;
    }

    /**
     * @param newVal
     */
    public void setName(String newVal) {
        name = newVal;
    }


    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
        return (this.componentId);
    }

}