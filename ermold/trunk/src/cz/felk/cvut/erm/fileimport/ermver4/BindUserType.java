package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bind_userType complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_userType">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element ref="{}typename"/>
  *         &lt;element ref="{}datatypedef"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_userType", propOrder = {
         "typeName",
         "datatypeDef"
         })
 public class BindUserType {

     @XmlElement(name = "typename", required = true)
     protected String typeName;
     @XmlElement(name = "datatypedef", required = true)
     protected BindDatatypedef datatypeDef;

     /**
      * Gets the value of the typeName property.
      *
      * @return possible object is
      *         {@link String }
      */
     public String getTypeName() {
         return typeName;
     }

     /**
      * Sets the value of the typeName property.
      *
      * @param value allowed object is
      *              {@link String }
      */
     public void setTypeName(String value) {
         this.typeName = value;
     }

     public boolean isSetTypeName() {
         return (this.typeName != null);
     }

     /**
      * Gets the value of the datatypeDef property.
      *
      * @return possible object is
      *         {@link BindDatatypedef }
      */
     public BindDatatypedef getDatatypeDef() {
         return datatypeDef;
     }

     /**
      * Sets the value of the datatypeDef property.
      *
      * @param value allowed object is
      *              {@link BindDatatypedef }
      */
     public void setDatatypeDef(BindDatatypedef value) {
         this.datatypeDef = value;
     }

     public boolean isSetDatatypeDef() {
         return (this.datatypeDef != null);
     }

 }
