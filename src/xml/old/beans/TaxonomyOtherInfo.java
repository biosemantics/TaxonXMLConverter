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
public class TaxonomyOtherInfo extends XMLWritable {
    private String otherInfo;
    
    public TaxonomyOtherInfo() {
        
    }
    
    public TaxonomyOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
    
    public String getOtherInfo() {
        return this.otherInfo;
    }
    
    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        Element otherInfo = doc.createElement("other_info");
        parent.appendChild(otherInfo);
        
        otherInfo.setTextContent(this.otherInfo);
    }
}
