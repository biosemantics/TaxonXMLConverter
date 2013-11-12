/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

/**
 *
 * @author iychoi
 */
public class SchemaMappingEntry {
    private String element;
    private boolean hasType;
    
    public SchemaMappingEntry() {
        
    }
    
    public SchemaMappingEntry(String element, boolean hasType) {
        this.element = element;
        this.hasType = hasType;
    }
    
    public void setElement(String element) {
        this.element = element;
    }
    
    public String getElement() {
        return this.element;
    }
    
    public void setHasType(boolean hasType) {
        this.hasType = hasType;
    }
    
    public boolean getHasType() {
        return this.hasType;
    }
}
