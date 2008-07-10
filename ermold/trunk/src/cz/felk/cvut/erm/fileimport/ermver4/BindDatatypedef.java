package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlType;
 import java.util.ArrayList;
 import java.util.List;


/**
 * <p>Java class for Bind_datatypedef complex type.
  * <p/>
  * <p>The following schema fragment specifies the expected content contained within this class.
  * <p/>
  * <pre>
  * &lt;complexType name="Bind_datatypedef">
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element ref="{}datatype"/>
  *         &lt;element ref="{}item" maxOccurs="unbounded" minOccurs="0"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
  */
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Bind_datatypedef", propOrder = {
         "dataType",
         "item"
         })
 public class BindDatatypedef {

     @XmlElement(name = "datatype", required = true)
     protected String dataType;
     protected List<BindItem> item;

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
      * Gets the value of the item property.
      * <p/>
      * <p/>
      * This accessor method returns a reference to the live list,
      * not a snapshot. Therefore any modification you make to the
      * returned list will be present inside the JAXB object.
      * This is why there is not a <CODE>set</CODE> method for the item property.
      * <p/>
      * <p/>
      * For example, to add a new item, do as follows:
      * <pre>
      *    getItem().add(newItem);
      * </pre>
      * <p/>
      * <p/>
      * <p/>
      * Objects of the following type(s) are allowed in the list
      * {@link BindItem }
      */
     public List<BindItem> getItem() {
         if (item == null) {
             item = new ArrayList<BindItem>();
         }
         return this.item;
     }

     public boolean isSetItem() {
         return ((this.item != null) && (!this.item.isEmpty()));
     }

     public void unsetItem() {
         this.item = null;
     }

 }
