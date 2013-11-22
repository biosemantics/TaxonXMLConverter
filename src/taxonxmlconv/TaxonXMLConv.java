/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import common.utils.StreamUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import xml.newformat.beans.key.Key;
import xml.newformat.beans.treatment.Treatment;
import xml.old.beans.KeyTo;
import xml.old.beans.Taxonomy;

/**
 *
 * @author iychoi
 */
public class TaxonXMLConv {

    private File oldXML;
    private File newXML;
    private String bibrigraph;
    private String processor_name;
            
    private xml.newformat.beans.treatment.ObjectFactory taxonObjectFactory;
    private xml.newformat.beans.key.ObjectFactory keyObjectFactory;
    
    private Hierarchy hierarchy;
    
    public TaxonXMLConv(File oldXML, File newXML, String bibrigraph, String processor_name) {
        this.oldXML = oldXML;
        this.newXML = newXML;
        this.bibrigraph = bibrigraph;
        this.processor_name = processor_name;
        
        this.taxonObjectFactory = new xml.newformat.beans.treatment.ObjectFactory();
        this.keyObjectFactory = new xml.newformat.beans.key.ObjectFactory();
        
        this.hierarchy = new Hierarchy();
    }
    
    private boolean isOldTaxonXML(String xml) {
        if(xml.indexOf("<treatment>") >= 0) {
            return true;
        }
        return false;
    }
    
    private boolean isOldKeyXML(String xml) {
        if(xml.indexOf("<key>") >= 0) {
            return true;
        }
        return false;
    }
    
    private void convFile(File in, File out, String bibligraph, String processor_name) throws IOException {
        // check xml file
        System.out.println("Converting : "+ in.getAbsolutePath());
        System.out.println("to : "+ out.getAbsolutePath());
        String content;
        try {
            content = StreamUtil.readFileString(in);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        
        boolean isTaxonXML = false;
        boolean isKeyXML = false;
        if(isOldTaxonXML(content)) {
            isTaxonXML = true;
        }
        if(isOldKeyXML(content)) {
            isKeyXML = true;
        }
        
        OldTaxonXMLReader reader = new OldTaxonXMLReader(in);
        
        if(isTaxonXML) {
            Taxonomy taxon = reader.parseTaxon();
            Treatment newTaxon = convTaxon(this.hierarchy, taxon, bibligraph, processor_name);
            try {
                createXML(newTaxon, out);
                System.out.println("new Taxon XML was created : " + out.getName());
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
        
        if(isKeyXML) {
            KeyTo key = reader.parseKey();
            Key newKey = convKey(key, bibligraph, processor_name);
            try {
                createXML(newKey, out);
                System.out.println("new Key XML was created : " + out.getName());
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }
    
    public void startConv() throws IOException {
        if(this.oldXML.isFile()) {
            convFile(this.oldXML, this.newXML, this.bibrigraph, this.processor_name);
        } else if(this.oldXML.isDirectory()) {
            this.newXML.mkdirs();
            
            File[] targets = this.oldXML.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File file, String string) {
                    if(string.endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            });
            
            List<String> targetTaxon_list = new ArrayList<String>();
            List<String> targetKey_list = new ArrayList<String>();
            for(File in : targets) {
                String content;
                try {
                    content = StreamUtil.readFileString(in);
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
                
                if (isOldTaxonXML(content)) {
                    targetTaxon_list.add(in.getPath());
                }
                if (isOldKeyXML(content)) {
                    targetKey_list.add(in.getPath());
                }
            }
            
            Collections.sort(targetTaxon_list, new XmlFilenameComparator());
            Collections.sort(targetKey_list, new XmlFilenameComparator());
            
            for(String in : targetTaxon_list) {
                File oldFile = new File(in);
                File out = new File(this.newXML, oldFile.getName());
                convFile(oldFile, out, this.bibrigraph, this.processor_name);
            }
            
            for(String in : targetKey_list) {
                File oldFile = new File(in);
                File out = new File(this.newXML, oldFile.getName());
                convFile(oldFile, out, this.bibrigraph, this.processor_name);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if(args.length != 4) {
                System.err.println("Parameters : <input xml> <output xml> <bibrigraph> <name>");
                return;
            }
            
            File oldXML = new File(args[0]);
            File newXML = new File(args[1]);
            String bibrigraph = args[2];
            String name = args[3];
            
            TaxonXMLConv conv = new TaxonXMLConv(oldXML, newXML, bibrigraph, name);
            conv.startConv();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private Treatment convTaxon(Hierarchy hierarchy, Taxonomy taxon, String bibligraph, String processor_name) throws IOException {
        if(taxon == null) {
            return null;
        }
        
        Treatment treatment = this.taxonObjectFactory.createTreatment();
        
        TaxonConverter tc = new TaxonConverter(hierarchy, treatment, taxon);
        tc.convert(bibligraph, processor_name);
        
        return tc.getTreatment();
    }
    
    private Key convKey(KeyTo keyto, String bibligraph, String processor_name) throws IOException {
        if(keyto == null) {
            return null;
        }
        
        Key key = this.keyObjectFactory.createKey();
        
        KeyConverter kc = new KeyConverter(key, keyto);
        kc.convert(bibligraph, processor_name);
        return kc.getKey();
    }
    
    private void createXML(Treatment treatment, File outFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Treatment.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(treatment, outFile);
    }
    
    private void createXML(Key key, File outFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Key.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(key, outFile);
    }
}
