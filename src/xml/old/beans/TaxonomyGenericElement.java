/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import common.utils.StringUtil;
import common.xml.XMLWritable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyGenericElement extends XMLWritable {
    private String name;
    private String text;
    
    private List<TaxonomyGenericElement> innerElements;
    
    public TaxonomyGenericElement(String name, String text) {
        this.name = name;
        this.text = text;
        
        this.innerElements = new ArrayList<TaxonomyGenericElement>();
    }
    
    public TaxonomyGenericElement() {
        this.innerElements = new ArrayList<TaxonomyGenericElement>();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void addElement(TaxonomyGenericElement elem) {
        this.innerElements.add(elem);
    }
    
    public List<TaxonomyGenericElement> getInnerElements() {
        return this.innerElements;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        Element elem = doc.createElement(StringUtil.getSafeTagName(this.name));
        parent.appendChild(elem);
        
        if(this.text != null && !this.text.trim().equals("")) {
            elem.setTextContent(this.text);
        }
        
        if(this.innerElements != null) {
            for(TaxonomyGenericElement innerElem : this.innerElements) {
                innerElem.toXML(doc, elem);
            }
        }
    }
}
