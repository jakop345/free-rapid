package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bind_strong complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_strong">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element ref="{}left"/>
  *         &lt;element ref="{}top"/>
  *         &lt;element ref="{}width"/>
  *         &lt;element ref="{}height"/>
  *         &lt;element ref="{}ent"/>
  *         &lt;element ref="{}child"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_strong", propOrder = {
         "left",
         "top",
         "width",
         "height",
         "ent",
         "child"
         })
 public class BindStrong {

     protected int left;
     protected int top;
     protected int width;
     protected int height;
     protected int ent;
     protected int child;

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
      * Gets the value of the child property.
      */
     public int getChild() {
         return child;
     }

     /**
      * Sets the value of the child property.
      */
     public void setChild(int value) {
         this.child = value;
     }

     public boolean isSetChild() {
         return true;
     }

 }
