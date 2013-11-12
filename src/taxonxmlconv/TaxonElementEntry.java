/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.File;

/**
 *
 * @author iychoi
 */
public class TaxonElementEntry {
    private File file;
    private String element;
    private String type;
    private String body;
    private SchemaMappingEntry map_to_element;
    
    public TaxonElementEntry() {
        
    }
    
    public TaxonElementEntry(File file, String element, String type, String body) {
        this.file = file;
        this.element = element;
        this.type = type;
        this.body = body;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public String getFilename() {
        return this.file.getName();
    }
    
    public String getFilepath() {
        return this.file.getPath();
    }
    
    public void setElement(String element) {
        this.element = element;
    }
    
    public String getElement() {
        return this.element;
    }
        
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getBody() {
        return this.body;
    }
    
    public void setMapToElement(SchemaMappingEntry element) {
        this.map_to_element = element;
    }
    
    public SchemaMappingEntry getMapToElement() {
        return this.map_to_element;
    }
}
