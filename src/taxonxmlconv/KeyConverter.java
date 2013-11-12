/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBElement;
import xml.newformat.beans.key.Determination;
import xml.newformat.beans.key.Key;
import xml.newformat.beans.key.KeyStatement;
import xml.newformat.beans.key.Meta;
import xml.newformat.beans.key.ProcessedBy;
import xml.newformat.beans.key.Processor;
import xml.old.beans.KeyHeading;
import xml.old.beans.KeyTo;

/**
 *
 * @author iychoi
 */
public class KeyConverter {

    private Key dest;
    private KeyTo origin;
    private xml.newformat.beans.key.ObjectFactory keyObjectFactory;
    
    public KeyConverter(Key key, KeyTo keyto) {
        this.dest = key;
        this.origin = keyto;
        this.keyObjectFactory = new xml.newformat.beans.key.ObjectFactory();
    }

    public void convert(String bibligraph, String processor_name) throws IOException {
        convMeta(bibligraph, processor_name);
        convHeading();
        convDiscussions();
        convStatements();
    }
    
    private void convMeta(String bibligraph, String processor_name) throws IOException {
        // set bibrigraph
        Meta newMeta = this.keyObjectFactory.createMeta();
        newMeta.setSource(bibligraph);

        // set processor
        ProcessedBy newProcessedBy = this.keyObjectFactory.createProcessedBy();
        Processor newProcessor = this.keyObjectFactory.createProcessor();
        newProcessor.setValue(processor_name);
        newProcessor.setProcessType("format conversion");
        newProcessedBy.getProcessorOrCharaparser().add(newProcessor);

        newMeta.setProcessedBy(newProcessedBy);

        this.dest.setMeta(newMeta);
    }
    
    private void convHeading() throws IOException {
        KeyHeading heading = this.origin.getHeading();
        if(heading != null) {
            String headingString = heading.getHeading();
            this.dest.setKeyHeading(headingString);
        }
    }
    
    private void convDiscussions() throws IOException {
        List<xml.old.beans.KeyDiscussion> discussions = this.origin.getDiscussions();
        if(discussions != null) {
            for(xml.old.beans.KeyDiscussion discussion : discussions) {
                JAXBElement<String> newDiscussion = this.keyObjectFactory.createDiscussion(discussion.getText());
                this.dest.getDiscussionOrKeyHeadOrKeyStatement().add(newDiscussion);
            }
        }
    }
    
    private void convStatements() throws IOException {
        List<xml.old.beans.KeyStatement> statements = this.origin.getStatements();
        if(statements != null) {
            for(xml.old.beans.KeyStatement statement : statements) {
                KeyStatement newStatement = this.keyObjectFactory.createKeyStatement();
                newStatement.setStatementId(statement.getId());
                newStatement.setStatement(statement.getStatement());
                
                if(statement.getNextStatementId() != null) {
                    newStatement.setNextStatementId(statement.getNextStatementId());
                }
                
                if(statement.getDetermination() != null) {
                    Determination determination = this.keyObjectFactory.createDetermination();
                    determination.setValue(statement.getDetermination());
                    if(statement.getDeterminationRefFilename() != null) {
                        determination.setFileName(statement.getDeterminationRefFilename());
                    }
                    newStatement.setDetermination(determination);
                }
                this.dest.getDiscussionOrKeyHeadOrKeyStatement().add(newStatement);
            }
        }
    }

    public Key getKey() {
        return this.dest;
    }
}
