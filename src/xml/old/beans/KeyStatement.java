/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import common.xml.XMLWritable;
import org.w3c.dom.Attr;

/**
 *
 * @author iychoi
 */
public class KeyStatement extends XMLWritable {

    private String id;
    private String statement;
    private String determination;
    private String determinationRefFilename;
    private String nextStatementId;
    
    public KeyStatement() {
        
    }
    
    public KeyStatement(String id, String statement, String determination, String determinationRefFilename, String nextStatementId) {
        this.id = id;
        this.statement = statement;
        this.determination = determination;
        this.determinationRefFilename = determinationRefFilename;
        this.nextStatementId = nextStatementId;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setStatement(String statement) {
        this.statement = statement;
    }
    
    public String getStatement() {
        return this.statement;
    }
    
    public void setDetermination(String determination) {
        this.determination = determination;
    }
    
    public String getDetermination() {
        return this.determination;
    }
    
    public void setDeterminationRefFilename(String determinationRefFilename) {
        this.determinationRefFilename = determinationRefFilename;
    }
    
    public String getDeterminationRefFilename() {
        return this.determinationRefFilename;
    }
    
    public void setNextStatementId(String nextStatementId) {
        this.nextStatementId = nextStatementId;
    }
    
    public String getNextStatementId() {
        return this.nextStatementId;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element key_statement = doc.createElement("key_statement");
        parent.appendChild(key_statement);
        
        Element id = doc.createElement("statement_id");
        key_statement.appendChild(id);
        
        id.setTextContent(this.id);
        
        Element statement = doc.createElement("statement");
        key_statement.appendChild(statement);
        
        statement.setTextContent(this.statement);
        
        if(this.nextStatementId != null && !this.nextStatementId.trim().equals("")) {
            Element nextStatementId = doc.createElement("next_statement_id");
            key_statement.appendChild(nextStatementId);
            
            nextStatementId.setTextContent(this.nextStatementId);
        }
        
        if(this.determination != null && !this.determination.trim().equals("")) {
            Element determination = doc.createElement("determination");
            key_statement.appendChild(determination);
            
            determination.setTextContent(this.determination);
            
            if(this.determinationRefFilename != null && !this.determinationRefFilename.trim().equals("")) {
                Attr attr = doc.createAttribute("file_name");
                attr.setValue(this.determinationRefFilename);
                determination.setAttributeNode(attr);
            }
        }
    }
    
}
