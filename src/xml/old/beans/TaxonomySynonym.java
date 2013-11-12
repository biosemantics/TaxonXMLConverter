/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import common.xml.XMLWritable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomySynonym extends XMLWritable {

    private String synonym;
    
    public TaxonomySynonym(String synonym) {
        this.synonym = synonym;
    }
    
    public TaxonomySynonym() {
        
    }
    
    public String getSynonym() {
        return this.synonym;
    }
    
    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element synonym = doc.createElement("synonym");
        parent.appendChild(synonym);
        
        synonym.setTextContent(this.synonym);
    }
}
