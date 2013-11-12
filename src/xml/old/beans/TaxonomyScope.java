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
public class TaxonomyScope extends XMLWritable {
    private String scope;
    
    public TaxonomyScope() {
        
    }
    
    public TaxonomyScope(String scope) {
        this.scope = scope;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element scope = doc.createElement("scope");
        parent.appendChild(scope);
        
        scope.setTextContent(this.scope);
    }
}
