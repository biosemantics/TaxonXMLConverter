/*
 * KeyTo Bean
 */
package xml.old.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class KeyTo {
    private String filename;
    private KeyHeading heading;
    private List<KeyDiscussion> discussions;
    private List<KeyStatement> statements;
    
    public KeyTo() {
        this.discussions = new ArrayList<KeyDiscussion>();
        this.statements = new ArrayList<KeyStatement>();
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setHeading(KeyHeading heading) {
        this.heading = heading;
    }
    
    public KeyHeading getHeading() {
        return this.heading;
    }
    
    public void addDiscussion(KeyDiscussion discuss) {
        this.discussions.add(discuss);
    }
    
    public List<KeyDiscussion> getDiscussions() {
        return this.discussions;
    }
    
    public void addStatement(KeyStatement statement) {
        this.statements.add(statement);
    }
    
    public List<KeyStatement> getStatements() {
        return this.statements;
    }
    
    public void toXML(File file) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        toXML(doc);
        
        // write xml to file    
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
    
    public void toXML(Document doc) {
        Element key = doc.createElement("key");
        doc.appendChild(key);
        
        if(this.heading != null) {
            this.heading.toXML(doc, key);
        }
        
        if(this.discussions != null) {
            for(KeyDiscussion discuss : this.discussions) {
                discuss.toXML(doc, key);
            }
        }
        
        if(this.statements != null) {
            for(KeyStatement statement : this.statements) {
                statement.toXML(doc, key);
            }
        }
    }
}
