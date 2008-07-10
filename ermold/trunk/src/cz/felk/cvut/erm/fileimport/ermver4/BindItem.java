package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bind_item complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_item">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element ref="{}itemname"/>
  *         &lt;element ref="{}datatype"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_item", propOrder = {
         "itemName",
         "dataType"
         })
 public class BindItem {

     @XmlElement(name = "itemname", required = true)
     protected String itemName;
     @XmlElement(name = "datatype", required = true)
     protected String dataType;

     /**
      * Gets the value of the itemName property.
      *
      * @return possible object is
      *         {@link String }
      */
     public String getItemName() {
         return itemName;
     }

     /**
      * Sets the value of the itemName property.
      *
      * @param value allowed object is
      *              {@link String }
      */
     public void setItemName(String value) {
         this.itemName = value;
     }

     public boolean isSetItemName() {
         return (this.itemName != null);
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

 }
