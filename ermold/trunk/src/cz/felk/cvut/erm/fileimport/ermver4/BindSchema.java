package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Bind_schema complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Bind_schema">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}scale"/>
 *         &lt;element ref="{}left"/>
 *         &lt;element ref="{}top"/>
 *         &lt;element ref="{}width"/>
 *         &lt;element ref="{}height"/>
 *         &lt;element ref="{}id"/>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}notation"/>
 *         &lt;element ref="{}comment"/>
 *         &lt;choice>
 *           &lt;element ref="{}usertype" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{}relation"/>
 *           &lt;element ref="{}atribute"/>
 *           &lt;element ref="{}entity"/>
 *           &lt;element ref="{}cardinality"/>
 *           &lt;element ref="{}unique"/>
 *           &lt;element ref="{}strong"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bind_schema", propOrder = {
        "scale",
        "left",
        "top",
        "width",
        "height",
        "id",
        "name",
        "notation",
        "comment",
        "userType",
        "relationOrAtributeOrEntity"
        })
public class BindSchema {

    protected float scale;
    protected int left;
    protected int top;
    protected int width;
    protected int height;
    protected int id;
    @XmlElement(required = true)
    protected String name;
    protected int notation;
    @XmlElement(required = true)
    protected String comment;
    @XmlElement(name = "usertype")
    protected List<BindUserType> userType;
    @XmlElements({
    @XmlElement(name = "unique", type = BindUnique.class),
    @XmlElement(name = "atribute", type = BindAttribute.class),
    @XmlElement(name = "entity", type = BindEntity.class),
    @XmlElement(name = "strong", type = BindStrong.class),
    @XmlElement(name = "relation", type = BindRelation.class),
    @XmlElement(name = "cardinality", type = BindCardinality.class)
            })
    protected List<Object> relationOrAtributeOrEntity;

    /**
     * Gets the value of the scale property.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the value of the scale property.
     */
    public void setScale(float value) {
        this.scale = value;
    }

    public boolean isSetScale() {
        return true;
    }

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
     * Gets the value of the notation property.
     */
    public int getNotation() {
        return notation;
    }

    /**
     * Sets the value of the notation property.
     */
    public void setNotation(int value) {
        this.notation = value;
    }

    public boolean isSetNotation() {
        return true;
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

    /**
     * Gets the value of the userType property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userType property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserType().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link BindUserType }
     */
    public List<BindUserType> getUserType() {
        if (userType == null) {
            userType = new ArrayList<BindUserType>();
        }
        return this.userType;
    }

    public boolean isSetUserType() {
        return ((this.userType != null) && (!this.userType.isEmpty()));
    }

    public void unsetUserType() {
        this.userType = null;
    }

    /**
     * Gets the value of the relationOrAtributeOrEntity property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationOrAtributeOrEntity property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationOrAtributeOrEntity().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link BindUnique }
     * {@link BindAttribute }
     * {@link BindEntity }
     * {@link BindStrong }
     * {@link BindRelation }
     * {@link BindCardinality }
     */
    public List<Object> getRelationOrAtributeOrEntity() {
        if (relationOrAtributeOrEntity == null) {
            relationOrAtributeOrEntity = new ArrayList<Object>();
        }
        return this.relationOrAtributeOrEntity;
    }

    public boolean isSetRelationOrAtributeOrEntity() {
        return ((this.relationOrAtributeOrEntity != null) && (!this.relationOrAtributeOrEntity.isEmpty()));
    }

    public void unsetRelationOrAtributeOrEntity() {
        this.relationOrAtributeOrEntity = null;
    }

}
