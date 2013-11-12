/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import common.xml.XMLWritable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyDescription extends XMLWritable {
    private String title;
    private String description;

    public enum TaxonomyDescriptionType {
        DESCRIPTION_GENERIC(""),
        DESCRIPTION_DEFINITION("Definition"),
        DESCRIPTION_DIAGNOSIS("Diagnosis"),
        DESCRIPTION_TYPESPECIES("type_species");
        
        private String name;
        
        TaxonomyDescriptionType(String name) {
            this.name = name;
        }
        
        public String toString() {
            return this.name;
        }
    }
    
    private TaxonomyDescriptionType type;
    
    public TaxonomyDescription(TaxonomyDescriptionType type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }
    
    public TaxonomyDescription() {
        
    }
    
    public TaxonomyDescriptionType getType() {
        return this.type;
    }
    
    public void setType(TaxonomyDescriptionType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element description = doc.createElement("description");
        parent.appendChild(description);
        
        if(this.type.equals(TaxonomyDescriptionType.DESCRIPTION_GENERIC) &&
                this.title != null && !this.title.trim().equals("")) {
            Attr attr = doc.createAttribute("type");
            attr.setValue(this.title.trim());
            description.setAttributeNode(attr);
        }
        
        if(this.type.equals(TaxonomyDescriptionType.DESCRIPTION_DIAGNOSIS) ||
                this.type.equals(TaxonomyDescriptionType.DESCRIPTION_DEFINITION) ||
                this.type.equals(TaxonomyDescriptionType.DESCRIPTION_TYPESPECIES)) {
            Attr attr = doc.createAttribute("type");
            attr.setValue(this.type.toString());
            description.setAttributeNode(attr);
        }
        
        description.setTextContent(this.description);
    }
}
