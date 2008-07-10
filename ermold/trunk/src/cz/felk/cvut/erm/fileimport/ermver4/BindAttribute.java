package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bind_attribute complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_attribute">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;group ref="{}constructGroup"/>
  *         &lt;element ref="{}datatype"/>
  *         &lt;element ref="{}arbitrary"/>
  *         &lt;element ref="{}primary"/>
  *         &lt;element ref="{}uniq"/>
  *         &lt;element ref="{}position"/>
  *         &lt;choice>
  *           &lt;element ref="{}ent"/>
  *           &lt;element ref="{}rel"/>
  *         &lt;/choice>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_attribute", propOrder = {
         "left",
         "top",
         "width",
         "height",
         "id",
         "name",
         "comment",
         "dataType",
         "arbitrary",
         "primary",
         "uniq",
         "position",
         "ent",
         "rel"
         })
 public class BindAttribute {

     protected int left;
     protected int top;
     protected int width;
     protected int height;
     protected int id;
     @XmlElement(required = true)
     protected String name;
     @XmlElement(required = true)
     protected String comment;
     @XmlElement(name = "datatype", required = true)
     protected String dataType;
     protected boolean arbitrary;
     protected boolean primary;
     protected boolean uniq;
     protected int position;
     protected Integer ent;
     protected Integer rel;

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

     /**
      * Gets the value of the dataType property.
      *
      * @return possible object is
      *         {@link String }
      */
     public String getDataType() {
         return dataType;
     }

     /**
      * Sets the value of the dataType property.
      *
      * @param value allowed object is
      *              {@link String }
      */
     public void setDataType(String value) {
         this.dataType = value;
     }

     public boolean isSetDataType() {
         return (this.dataType != null);
     }

     /**
      * Gets the value of the arbitrary property.
      */
     public boolean isArbitrary() {
         return arbitrary;
     }

     /**
      * Sets the value of the arbitrary property.
      */
     public void setArbitrary(boolean value) {
         this.arbitrary = value;
     }

     public boolean isSetArbitrary() {
         return true;
     }

     /**
      * Gets the value of the primary property.
      */
     public boolean isPrimary() {
         return primary;
     }

     /**
      * Sets the value of the primary property.
      */
     public void setPrimary(boolean value) {
         this.primary = value;
     }

     public boolean isSetPrimary() {
         return true;
     }

     /**
      * Gets the value of the uniq property.
      */
     public boolean isUniq() {
         return uniq;
     }

     /**
      * Sets the value of the uniq property.
      */
     public void setUniq(boolean value) {
         this.uniq = value;
     }

     public boolean isSetUniq() {
         return true;
     }

     /**
      * Gets the value of the position property.
      */
     public int getPosition() {
         return position;
     }

     /**
      * Sets the value of the position property.
      */
     public void setPosition(int value) {
         this.position = value;
     }

     public boolean isSetPosition() {
         return true;
     }

     /**
      * Gets the value of the ent property.
      *
      * @return possible object is
      *         {@link Integer }
      */
     public Integer getEnt() {
         return ent;
     }

     /**
      * Sets the value of the ent property.
      *
      * @param value allowed object is
      *              {@link Integer }
      */
     public void setEnt(Integer value) {
         this.ent = value;
     }

     public boolean isSetEnt() {
         return (this.ent != null);
     }

     /**
      * Gets the value of the rel property.
      *
      * @return possible object is
      *         {@link Integer }
      */
     public Integer getRel() {
         return rel;
     }

     /**
      * Sets the value of the rel property.
      *
      * @param value allowed object is
      *              {@link Integer }
      */
     public void setRel(Integer value) {
         this.rel = value;
     }

     public boolean isSetRel() {
         return (this.rel != null);
     }

 }
