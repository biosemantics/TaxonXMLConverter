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
public class KeyHeading extends XMLWritable {

    private String heading;
    
    public KeyHeading() {
    }
    
    public String getHeading() {
        return this.heading;
    }
    
    public void setHeading(String heading) {
        this.heading = heading;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element heading = doc.createElement("key_heading");
        parent.appendChild(heading);

        heading.setTextContent(this.heading);
    }
}
