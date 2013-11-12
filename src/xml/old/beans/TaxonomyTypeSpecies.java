/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import common.xml.XMLWritable;

/**
 *
 * @author iychoi
 */
public class TaxonomyTypeSpecies extends XMLWritable {
    private String typespecies;
    
    public TaxonomyTypeSpecies(String typespecies) {
        this.typespecies = typespecies;
    }
    
    public TaxonomyTypeSpecies() {
        
    }
    
    public String getTypeSpecies() {
        return this.typespecies;
    }
    
    public void setTypeSpecies(String typespecies) {
        this.typespecies = typespecies;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element typespecies = doc.createElement("type_species");
        parent.appendChild(typespecies);
        
        typespecies.setTextContent(this.typespecies);
    }
}
