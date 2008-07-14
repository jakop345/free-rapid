package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bind_relation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Bind_relation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}constructGroup"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bind_relation", propOrder = {
        "left",
        "top",
        "width",
        "height",
        "id",
        "name",
        "comment"
        })
public class BindRelation {

    protected int left;
    protected int top;
    protected int width;
    protected int height;
    protected int id;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String comment;

    /**
     * Gets the value of the left property.
     */
    public int getLeft() {
        return left;
    }

    /**
     * Sets the value of the left property.
     */
    public void setLeft(int value) {
        this.left = value;
    }

    public boolean isSetLeft() {
        return true;
    }

    /**
     * Gets the value of the top property.
     */
    public int getTop() {
        return top;
    }

    /**
     * Sets the value of the top property.
     */
    public void setTop(int value) {
        this.top = value;
    }

    public boolean isSetTop() {
        return true;
    }

    /**
     * Gets the value of the width property.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     */
    public void setWidth(int value) {
        this.width = value;
    }

    public boolean isSetWidth() {
        return true;
    }

    /**
     * Gets the value of the height property.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     */
    public void setHeight(int value) {
        this.height = value;
    }

    public boolean isSetHeight() {
        return true;
    }

    /**
     * Gets the value of the id property.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(int value) {
        this.id = value;
    }

    public boolean isSetId() {
        return true;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name != null);
    }

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComment(String value) {
        this.comment = value;
    }

    public boolean isSetComment() {
        return (this.comment != null);
    }

}
