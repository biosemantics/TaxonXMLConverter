/*
 * Read text content from djvu xml file
 */
package taxonxmlconv;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import xml.old.beans.KeyTo;
import xml.old.beans.Taxonomy;

/**
 *
 * @author iychoi
 */
public class OldTaxonXMLReader {
    private File file;
    
    private SAXParserFactory saxFactory;
    private SAXParser saxParser;
    private OldTaxonXMLSaxTaxonHandler taxonHandler;
    private OldTaxonXMLSaxKeyHandler keyHandler;
    
    public OldTaxonXMLReader(File xmlFile) {
        if(xmlFile == null || !xmlFile.exists() || !xmlFile.isFile())
            throw new IllegalArgumentException("Cannot find xml file");
        
        this.file = xmlFile;
        this.saxFactory = SAXParserFactory.newInstance();
        
        this.saxFactory.setValidating(false);
    }
    
    public Taxonomy parseTaxon() throws IOException {
        try {
            this.saxParser = this.saxFactory.newSAXParser();
            this.taxonHandler = new OldTaxonXMLSaxTaxonHandler();
            
            this.saxParser.parse(this.file, this.taxonHandler);
            
            return this.taxonHandler.getTaxonomy();
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex.getMessage());
        } catch (SAXException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public KeyTo parseKey() throws IOException {
        try {
            this.saxParser = this.saxFactory.newSAXParser();
            this.keyHandler = new OldTaxonXMLSaxKeyHandler();
            
            this.saxParser.parse(this.file, this.keyHandler);
            
            return this.keyHandler.getKey();
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex.getMessage());
        } catch (SAXException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
