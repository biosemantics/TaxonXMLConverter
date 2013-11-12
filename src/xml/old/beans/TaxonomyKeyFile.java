/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import common.utils.StringUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import common.xml.XMLWritable;

/**
 *
 * @author iychoi
 */
public class TaxonomyKeyFile extends XMLWritable {
    private String keyFile;
    
    public TaxonomyKeyFile() {
        
    }
    
    public TaxonomyKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public TaxonomyKeyFile(File file) {
        this.keyFile = file.getName();
    }
    
    public String getKeyFile() {
        return this.keyFile;
    }
    
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public void setKeyFile(File file) {
        this.keyFile = file.getName();
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element keyfile = doc.createElement("key_file");
        parent.appendChild(keyfile);
        
        keyfile.setTextContent(this.keyFile);
    }
}
