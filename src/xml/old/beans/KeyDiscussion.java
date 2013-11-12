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
public class KeyDiscussion extends XMLWritable {
    private String text;
    
    public KeyDiscussion() {
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element discussion = doc.createElement("discussion");
        parent.appendChild(discussion);

        discussion.setTextContent(this.text);
    }
}
