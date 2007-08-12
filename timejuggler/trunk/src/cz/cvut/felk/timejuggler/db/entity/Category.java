package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:48
 * <p/>
 * Reprezentuje kategorii eventu nebo tasku.. Hotovo
 */
public class Category extends DbElement {
    private final static Logger logger = Logger.getLogger(Category.class.getName());

    private String name = "";

    private Color color = null;

    public Category() {

    }

    public Category(String name) {
        setName(name);
    }

    public Category(String name, Color color) {
        this(name);
        setColor(color);
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
        //TODO: pridat ukladani barvy - barva se da ukladat jednoduse jako string - search google Color.decode
        if (getId() > 0) {
            logger.info("Database - Update: Category[" + getId() + "]:" + name + "...");
            Object params[] = {name, color == null ? null : color.getRGB(), getId()};
            String updateQuery = "UPDATE Category SET name=?,color=? WHERE categoryID = ? ";
            template.executeUpdate(updateQuery, params);
        } else {
            logger.info("Database - Insert: Category[]:" + name + "...");
            Object params[] = {name, color == null ? -1 : color.getRGB()};//TODO proc je tady -1 a nahore null??
            String insertQuery = "INSERT INTO Category (name,color) VALUES (?,?) ";
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
        final Category category = (Category) o;
        return getId() == category.getId() && name.equals(category.getName());
    }


    public String getName() {
        return name;
    }

    /**
     * @param newVal
     */
    public void setName(String newVal) {
        if (newVal == null)
            throw new IllegalArgumentException("Name cannot be null!");
        name = newVal;
    }

/*
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
        return (this.componentId);
    }
*/

    public Object clone() throws CloneNotSupportedException {
        Category clone;
        try {
            clone = (Category) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return clone;
    }


}