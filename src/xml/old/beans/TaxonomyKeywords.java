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
public class TaxonomyKeywords extends XMLWritable {
    private String keywords;
    
    public TaxonomyKeywords() {
        
    }
    
    public TaxonomyKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public String getKeywords() {
        return this.keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        Element keywords = doc.createElement("keywords");
        parent.appendChild(keywords);
        
        keywords.setTextContent(this.keywords);
    }
}
