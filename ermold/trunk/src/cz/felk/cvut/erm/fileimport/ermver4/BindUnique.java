package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlType;
 import java.util.ArrayList;
 import java.util.List;


/**
 * <p>Java class for Bind_unique complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_unique">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;group ref="{}constructGroup"/>
  *         &lt;element ref="{}ent"/>
  *         &lt;element ref="{}atr" maxOccurs="unbounded"/>
  *         &lt;element ref="{}primary"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_unique", propOrder = {
         "left",
         "top",
         "width",
         "height",
         "id",
         "name",
         "comment",
         "ent",
         "atr",
         "primary"
         })
 public class BindUnique {

     protected int left;
     protected int top;
     protected int width;
     protected int height;
     protected int id;
     @XmlElement(required = true)
     protected String name;
     @XmlElement(required = true)
     protected String comment;
     protected int ent;
     @XmlElement(type = Integer.class)
     protected List<Integer> atr;
     protected boolean primary;

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
      * Gets the value of the ent property.
      */
     public int getEnt() {
         return ent;
     }

     /**
      * Sets the value of the ent property.
      */
     public void setEnt(int value) {
         this.ent = value;
     }

     public boolean isSetEnt() {
         return true;
     }

     /**
      * Gets the value of the atr property.
      * <p/>
      * <p/>
      * This accessor method returns a reference to the live list,
      * not a snapshot. Therefore any modification you make to the
      * returned list will be present inside the JAXB object.
      * This is why there is not a <CODE>set</CODE> method for the atr property.
      * <p/>
      * <p/>
      * For example, to add a new item, do as follows:
      * <pre>
      *    getAtr().add(newItem);
      * </pre>
      * <p/>
      * <p/>
      * <p/>
      * Objects of the following type(s) are allowed in the list
      * {@link Integer }
      */
     public List<Integer> getAtr() {
         if (atr == null) {
             atr = new ArrayList<Integer>();
         }
         return this.atr;
     }

     public boolean isSetAtr() {
         return ((this.atr != null) && (!this.atr.isEmpty()));
     }

     public void unsetAtr() {
         this.atr = null;
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

 }
