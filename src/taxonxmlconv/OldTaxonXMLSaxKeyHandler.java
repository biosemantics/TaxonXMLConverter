/*
 * SAX handler for djvu xml 
 */
package taxonxmlconv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xml.old.beans.KeyDiscussion;
import xml.old.beans.KeyHeading;
import xml.old.beans.KeyStatement;
import xml.old.beans.KeyTo;

/**
 *
 * @author iychoi
 */
public class OldTaxonXMLSaxKeyHandler extends DefaultHandler {

    private String text;
    private KeyTo key;
    
    private boolean inKey = false;
    private boolean inHeading = false;
    private boolean inDiscussion = false;
    private boolean inStatement = false;
    
    private KeyStatement prevStatement;

    public OldTaxonXMLSaxKeyHandler() {
        super();
    }
    
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return super.resolveEntity(publicId, systemId);
    }
    
    private void processKey() {
        this.key = new KeyTo();
    }
    
    private void processKeyHeading(String text) {
        KeyHeading heading = new KeyHeading();
        heading.setHeading(text);
        this.key.setHeading(heading);
    }
    
    private void processDiscussion(String text) {
        KeyDiscussion discussion = new KeyDiscussion();
        discussion.setText(text);
        this.key.addDiscussion(discussion);
    }
    
    private void processKeyStatement() {
        KeyStatement statement = new KeyStatement();
        this.key.addStatement(statement);
        
        this.prevStatement = statement;
    }
    
    private void processStatementId(String text) {
        if(this.prevStatement != null) {
            this.prevStatement.setId(text);
        }
    }
    
    private void processStatement(String text) {
        if(this.prevStatement != null) {
            this.prevStatement.setStatement(text);
        }
    }
    
    private void processNextStatementId(String text) {
        if(this.prevStatement != null) {
            this.prevStatement.setNextStatementId(text);
        }
    }
    
    private void processDeterminationStart(List<String> attrNames, List<String> attrValues) {
        if(this.prevStatement != null) {
            for(int i=0;i<attrNames.size();i++) {
                String attr = attrNames.get(i);
                String value = attrValues.get(i);
                if(attr.equalsIgnoreCase("file_name")) {
                    this.prevStatement.setDeterminationRefFilename(value);
                }
            }
            this.prevStatement.setDetermination(text);
        }
    }
    
    private void processDeterminationEnd(String text) {
        if(this.prevStatement != null) {
            this.prevStatement.setDetermination(text);
        }
    }
    
    private boolean isInKeyLevel() {
        if(this.inKey && !this.inHeading && !this.inStatement && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInHeadingLevel() {
        if(this.inKey && this.inHeading && !this.inStatement && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInStatementLevel() {
        if(this.inKey && !this.inHeading && this.inStatement && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInDiscussionLevel() {
        if(this.inKey && !this.inHeading && !this.inStatement && this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private void processStartElement(String element, List<String> attrNames, List<String> attrValues) {
        if (element.equalsIgnoreCase("key")) {
            processKey();
            this.inKey = true;
        } else if (element.equalsIgnoreCase("key_heading") && isInKeyLevel()) {
            this.inHeading = true;
        } else if (element.equalsIgnoreCase("discussion") && isInKeyLevel()) {
            this.inDiscussion = true;
        } else if (element.equalsIgnoreCase("key_statement") && isInKeyLevel()) {
            processKeyStatement();
            this.inStatement = true;
        } else if (element.equalsIgnoreCase("determination") && isInStatementLevel()) {
            processDeterminationStart(attrNames, attrValues);
        }
    }
    
    private void processEndElement(String element, String text) {
        if (element.equalsIgnoreCase("key")) {
            this.inKey = false;
        } else if (element.equalsIgnoreCase("key_heading") && isInHeadingLevel()) {
            processKeyHeading(text);
            this.inHeading = false;
        } else if (element.equalsIgnoreCase("discussion") && isInDiscussionLevel()) {
            processDiscussion(text);
            this.inDiscussion = false;
        } else if (element.equalsIgnoreCase("key_statement") && isInStatementLevel()) {
            this.inStatement = false;
        } else if (element.equalsIgnoreCase("statement_id") && isInStatementLevel()) {
            processStatementId(text);
        } else if (element.equalsIgnoreCase("statement") && isInStatementLevel()) {
            processStatement(text);
        } else if (element.equalsIgnoreCase("next_statement_id") && isInStatementLevel()) {
            processNextStatementId(text);
        } else if (element.equalsIgnoreCase("determination") && isInStatementLevel()) {
            processDeterminationEnd(text);
        }
    }
        
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String element = qName;
        //System.out.println("element \"" + this.element + "\" found");
        this.text = null;
        List<String> attrNames = new ArrayList<String>();
        List<String> attrValues = new ArrayList<String>();
        
        if(attributes != null) {
            int attributeLen = attributes.getLength();
            for(int i=0;i<attributeLen;i++) {
                String attributeName = attributes.getQName(i);
                String attributeValue = attributes.getValue(i);
                //System.out.println("attribute \"" + attributeName + "\", value \"" + attributeValue + "\"");
                
                attrNames.add(attributeName);
                attrValues.add(attributeValue);
            }
        }

        processStartElement(element, attrNames, attrValues);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String element = qName;
        //if(this.text != null) {
        //    System.out.println("text \"" + this.text + "\" found");
        //}
        
        processEndElement(element, this.text);
        element = null;
        this.text = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch, start, length);

        if(this.text == null) {
            this.text = text;
        } else {
            this.text += text;
        }
    }

    public KeyTo getKey() {
        return this.key;
    }
}
