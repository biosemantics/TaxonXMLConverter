/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import common.xml.XMLWritable;
import common.utils.StringUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyMeta extends XMLWritable {
    private String source;
    
    public TaxonomyMeta() {
        
    }
    
    public TaxonomyMeta(String source) {
        setSource(source);
    }
    
    public TaxonomyMeta(File file) {
        setSource(file);
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public void setSource(File file) {
        this.source = file.getName();
    }
    
    public String getSource() {
        return this.source;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element meta = doc.createElement("meta");
        parent.appendChild(meta);
        
        Element source = doc.createElement("source");
        meta.appendChild(source);
        
        source.setTextContent(this.source);
    }
}
