package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;

import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:48
 * <p/>
 * Reprezentuje kategorii eventu nebo tasku.. Hotovo
 */
public class Category extends DbElement implements Comparable<CategoryEntity>, CategoryEntity {
    private final static Logger logger = Logger.getLogger(Category.class.getName());

    private String name = "";

    private Color color = null;
    public final static String PROPERTYNAME_NAME = "name";
    public final static String PROPERTYNAME_COLOR = "color";


    public Category() {
        super();//dodrzovat volani predka v konstruktoru
    }

    public Category(String name) {
        this();
        setName(name);
    }

    public Category(String name, Color color) {
        this(name);
        setColor(color);
    }

    public Category(String name, Color color, int id) {
        this(name, color);
        setId(id);
    }

    public boolean hasAssignedColor() {
        return color != null;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color oldValue = getColor();
        this.color = color;
        firePropertyChange(PROPERTYNAME_COLOR, oldValue, color);
    }

    public void store() {

    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        //TODO: pridat ukladani barvy (hotovo) - barva se da ukladat jednoduse jako string - search google Color.decode - uz je to jako int,.. :)
        if (isAssigned()) {
            logger.info("Database - Update: Category[" + getId() + "]:" + name + "...");
            Object params[] = {name, color == null ? null : color.getRGB(), getId()};
            String updateQuery = "UPDATE Category SET name=?,color=? WHERE categoryID = ? ";
            template.executeUpdate(updateQuery, params);
        } else {
            logger.info("Database - Insert: Category[]:" + name + "...");
            Object params[] = {name, color == null ? null : color.getRGB()};
            String insertQuery = "INSERT INTO Category (name,color) VALUES (?,?) ";
            template.executeUpdate(insertQuery, params);
            setId(template.getGeneratedId());
        }
        super.saveOrUpdate(template);//nastaveni changed na false
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (isAssigned()) {
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM Category WHERE categoryID = ? ";
            template.executeUpdate(deleteQuery, params);
            setId(-1);
        }
    }

    public int compareTo(CategoryEntity o) {
        assert getName() != null;
        return getName().compareTo(o.getName());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Category category = (Category) o;
        return getId() == category.getId() /*&& name.equals(category.getName())*/;
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
        final String oldVal = getName();
        name = newVal;
        firePropertyChange(PROPERTYNAME_NAME, oldVal, name);
    }

    public Object clone() throws CloneNotSupportedException {
        CategoryEntity clone;
        try {
            clone = (CategoryEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return clone;
    }

	public String toString(){
		return this.name;
	}

}