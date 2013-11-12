//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.12 at 10:10:02 AM MST 
//


package xml.newformat.beans.treatment;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}publication_title" minOccurs="0"/>
 *         &lt;element ref="{}place_in_publication" minOccurs="0"/>
 *         &lt;element ref="{}other_info_on_pub" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "publicationTitle",
    "placeInPublication",
    "otherInfoOnPub"
})
@XmlRootElement(name = "place_of_publication")
public class PlaceOfPublication {

    @XmlElement(name = "publication_title")
    protected String publicationTitle;
    @XmlElement(name = "place_in_publication")
    protected String placeInPublication;
    @XmlElement(name = "other_info_on_pub")
    protected List<String> otherInfoOnPub;

    /**
     * Gets the value of the publicationTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicationTitle() {
        return publicationTitle;
    }

    /**
     * Sets the value of the publicationTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicationTitle(String value) {
        this.publicationTitle = value;
    }

    /**
     * Gets the value of the placeInPublication property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaceInPublication() {
        return placeInPublication;
    }

    /**
     * Sets the value of the placeInPublication property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaceInPublication(String value) {
        this.placeInPublication = value;
    }

    /**
     * Gets the value of the otherInfoOnPub property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherInfoOnPub property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherInfoOnPub().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOtherInfoOnPub() {
        if (otherInfoOnPub == null) {
            otherInfoOnPub = new ArrayList<String>();
        }
        return this.otherInfoOnPub;
    }

}