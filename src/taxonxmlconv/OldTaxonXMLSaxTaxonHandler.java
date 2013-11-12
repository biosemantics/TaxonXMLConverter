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
import xml.old.beans.Taxonomy;
import xml.old.beans.TaxonomyDescription;
import xml.old.beans.TaxonomyDiscussion;
import xml.old.beans.TaxonomyGenericElement;
import xml.old.beans.TaxonomyKeyFile;
import xml.old.beans.TaxonomyKeywords;
import xml.old.beans.TaxonomyMeta;
import xml.old.beans.TaxonomyNomenclature;
import xml.old.beans.TaxonomyOtherInfo;
import xml.old.beans.TaxonomyScope;
import xml.old.beans.TaxonomySynonym;
import xml.old.beans.TaxonomyTypeSpecies;

/**
 *
 * @author iychoi
 */
public class OldTaxonXMLSaxTaxonHandler extends DefaultHandler {

    private String text;
    private Taxonomy taxonomy;
    
    private boolean inTreatment = false;
    private boolean inMeta = false;
    private boolean inNomenclature = false;
    private boolean inDiscussion = false;
    
    private TaxonomyDescription prevDescription;

    public OldTaxonXMLSaxTaxonHandler() {
        super();
    }
    
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return super.resolveEntity(publicId, systemId);
    }
    
    private void processTreatment() {
        this.taxonomy = new Taxonomy();
    }
    
    private void processMeta() {
        this.taxonomy.setMeta(new TaxonomyMeta());
    }
    
    private void processMetaSource(String text) {
        this.taxonomy.getMeta().setSource(text);
    }
    
    private void processNomenclature() {
        this.taxonomy.setNomenclture(new TaxonomyNomenclature());
    }
    
    private void processNomenclatureName(String text) {
        this.taxonomy.getNomenclature().setName(text);
    }
    
    private void processNomenclatureAuthority(String text) {
        this.taxonomy.getNomenclature().setAuthority(text);
    }
    
    private void processNomenclatureCommonName(String text) {
        this.taxonomy.getNomenclature().setCommonName(text);
    }
    
    private void processNomenclatureRank(String text) {
        this.taxonomy.getNomenclature().setRank(text);
    }
    
    private void processNomenclatureTaxonHierarchy(String text) {
        this.taxonomy.getNomenclature().setHierarchy(text);
    }
    
    private void processNomenclatureTaxonHierarchyClean(String text) {
        this.taxonomy.getNomenclature().setHierarchyClean(text);
    }
    
    private void processNomenclatureNameInfo(String text) {
        this.taxonomy.getNomenclature().setNameInfo(text);
    }
    
    private void processNomenclatureOtherInfo(String text) {
        this.taxonomy.getNomenclature().addOtherInfo(new TaxonomyOtherInfo(text));
    }
    
    private void processSynonym(String text) {
        this.taxonomy.addSynonym(new TaxonomySynonym(text));
    }
    
    private void processTypeSpecies(String text) {
        this.taxonomy.addTypeSpecies(new TaxonomyTypeSpecies(text));
    }
    
    private void processScope(String text) {
        this.taxonomy.setScope(new TaxonomyScope(text));
    }
    
    private void processKeywords(String text) {
        this.taxonomy.setKeywords(new TaxonomyKeywords(text));
    }
    
    private void processDescriptionStart(List<String> attrNames, List<String> attrValues) {
        TaxonomyDescription description = new TaxonomyDescription();
        for(int i=0;i<attrNames.size();i++) {
            String attr = attrNames.get(i);
            String value = attrValues.get(i);
            if(attr.equalsIgnoreCase("type")) {
                if(value.equalsIgnoreCase("Diagnosis")) {
                    description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DIAGNOSIS);
                } else if(value.equalsIgnoreCase("Definition")) {
                    description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DEFINITION);
                } else if(value.equalsIgnoreCase("type_species")) {
                    description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_TYPESPECIES);
                } else {
                    description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_GENERIC);
                    description.setTitle(value);
                }
            }
        }
        this.taxonomy.addDescription(description);
        
        this.prevDescription = description;
    }
    
    private void processDescriptionEnd(String text) {
        if(this.prevDescription != null) {
            this.prevDescription.setDescription(text);
        }
    }
    
    private void processDiscussion() {
        this.taxonomy.setDiscussion(new TaxonomyDiscussion());
    }
    
    private void processKeyFile(String text) {
        this.taxonomy.addKeyFile(new TaxonomyKeyFile(text));
    }
    
    private void processGeneric(String element, String text) {
        this.taxonomy.addElement(new TaxonomyGenericElement(element, text));
    }
    
    private void processGenericDiscussion(String element, String text) {
        if(this.taxonomy.getDiscussion() == null) {
            this.taxonomy.setDiscussion(new TaxonomyDiscussion());
        }
        
        this.taxonomy.getDiscussion().addElement(new TaxonomyGenericElement(element, text));
    }
    
    private void processUntitledDiscussion(String element, String text) {
        TaxonomyDiscussion discussion = new TaxonomyDiscussion();
        discussion.setText(text);
        this.taxonomy.addDiscussionNonTitled(discussion);
    }
    
    private boolean isInTreatmentLevel() {
        if(this.inTreatment && !this.inMeta && !this.inNomenclature && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInMetaLevel() {
        if(this.inTreatment && this.inMeta && !this.inNomenclature && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInNomenclatureLevel() {
        if(this.inTreatment && !this.inMeta && this.inNomenclature && !this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private boolean isInDiscussionLevel() {
        if(this.inTreatment && !this.inMeta && !this.inNomenclature && this.inDiscussion) {
            return true;
        }
        return false;
    }
    
    private void processStartElement(String element, List<String> attrNames, List<String> attrValues) {
        if (element.equalsIgnoreCase("treatment")) {
            processTreatment();
            this.inTreatment = true;
        } else if (element.equalsIgnoreCase("meta") && isInTreatmentLevel()) {
            processMeta();
            this.inMeta = true;
        } else if (element.equalsIgnoreCase("nomenclature") && isInTreatmentLevel()) {
            processNomenclature();
            this.inNomenclature = true;
        } else if (element.equalsIgnoreCase("description") && isInTreatmentLevel()) {
            processDescriptionStart(attrNames, attrValues);
        } else if (element.equalsIgnoreCase("discussion") && isInTreatmentLevel()) {
            // defer generating discussion element
            //processDiscussion();
            this.inDiscussion = true;
        }
    }
    
    private void processEndElement(String element, String text) {
        if (element.equalsIgnoreCase("treatment")) {
            this.inTreatment = false;
        } else if (element.equalsIgnoreCase("meta") && isInMetaLevel()) {
            this.inMeta = false;
        } else if (element.equalsIgnoreCase("source") && isInMetaLevel()) {
            processMetaSource(text);
        } else if (element.equalsIgnoreCase("nomenclature") && isInNomenclatureLevel()) {
            this.inNomenclature = false;
        } else if (element.equalsIgnoreCase("name") && isInNomenclatureLevel()) {
            processNomenclatureName(text);
        } else if (element.equalsIgnoreCase("authority") && isInNomenclatureLevel()) {
            processNomenclatureAuthority(text);
        } else if (element.equalsIgnoreCase("common_name") && isInNomenclatureLevel()) {
            processNomenclatureCommonName(text);
        } else if (element.equalsIgnoreCase("rank") && isInNomenclatureLevel()) {
            processNomenclatureRank(text);
        } else if (element.equalsIgnoreCase("taxon_hierarchy") && isInNomenclatureLevel()) {
            processNomenclatureTaxonHierarchy(text);
        } else if (element.equalsIgnoreCase("taxon_hierarchy_clean") && isInNomenclatureLevel()) {
            processNomenclatureTaxonHierarchyClean(text);
        } else if (element.equalsIgnoreCase("name_info") && isInNomenclatureLevel()) {
            processNomenclatureNameInfo(text);
        } else if (element.equalsIgnoreCase("other_info") && isInNomenclatureLevel()) {
            processNomenclatureOtherInfo(text);
        } else if (element.equalsIgnoreCase("synonym") && isInTreatmentLevel()) {
            processSynonym(text);
        } else if (element.equalsIgnoreCase("type_species") && isInTreatmentLevel()) {
            processTypeSpecies(text);
        } else if (element.equalsIgnoreCase("scope") && isInTreatmentLevel()) {
            processScope(text);
        } else if (element.equalsIgnoreCase("keywords") && isInTreatmentLevel()) {
            processKeywords(text);
        } else if (element.equalsIgnoreCase("description") && isInTreatmentLevel()) {
            processDescriptionEnd(text);
        } else if (element.equalsIgnoreCase("discussion") && isInDiscussionLevel()) {
            this.inDiscussion = false;

            if (text != null && !text.trim().equals("")) {
                processUntitledDiscussion(element, text);
            }
        } else if (element.equalsIgnoreCase("key_file") && isInTreatmentLevel()) {
            processKeyFile(text);
        } else if (isInDiscussionLevel()) {
            // discussion generic
            processGenericDiscussion(element, text);
        } else if (isInTreatmentLevel()) {
            processGeneric(element, text);
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

    public Taxonomy getTaxonomy() {
        return this.taxonomy;
    }
}
