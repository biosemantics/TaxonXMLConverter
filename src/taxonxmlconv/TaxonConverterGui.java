/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import common.utils.StreamUtil;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import xml.old.beans.Taxonomy;
import xml.old.beans.TaxonomyDescription;
import xml.old.beans.TaxonomyDiscussion;
import xml.old.beans.TaxonomyGenericElement;
import xml.old.beans.TaxonomyKeywords;
import xml.old.beans.TaxonomyScope;
import xml.old.beans.TaxonomySynonym;
import xml.old.beans.TaxonomyTypeSpecies;

/**
 *
 * @author iychoi
 */
public class TaxonConverterGui extends javax.swing.JFrame {

    private File workingParentDir;
    private File[] taxonFiles;
    private List<TaxonElementEntry> xmlEntries;
    private List<SchemaMappingEntry> newXmlEntries;
    
    /**
     * Creates new form WordChecker
     */
    public TaxonConverterGui() {
        initComponents();
        
        this.tblOldTaxonElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                fireTableSelected();
            }
        });
    }
    
    private boolean isOldTaxonXML(File in) throws IOException {
        String content;
        try {
            content = StreamUtil.readFileString(in);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        
        if(content.indexOf("<treatment>") >= 0) {
            return true;
        }
        return false;
    }

    private void loadTaxonFiles(File file) {
        this.workingParentDir = file.getParentFile();
        if(file.isFile()) {
            this.taxonFiles = new File[1];
            this.taxonFiles[0] = file;
        } else if(file.isDirectory()) {
            this.taxonFiles = file.listFiles(new FileFilter(){

                @Override
                public boolean accept(File file) {
                    if(file.getName().toLowerCase().endsWith(".xml")) {
                        try {
                            if(isOldTaxonXML(file)) {
                                return true;
                            }
                        } catch (IOException ex) {
                            return false;
                        }
                    }
                    return false;
                }
            });
        }

        this.lblTaxons.setText(file.getName() + " (" + this.taxonFiles.length + " files loaded)");
    }

    private void startParsing() throws Exception {
        if (this.taxonFiles == null || this.taxonFiles.length == 0) {
            throw new Exception("taxonFile is null");
        }

        processParsing();
        prepareNewSchemaEntries();
    }

    private void processParsing() throws Exception {
        this.xmlEntries = new ArrayList<TaxonElementEntry>();
        
        //populate array
        for(File xmlFile : this.taxonFiles) {
            List<TaxonElementEntry> entries = readOldXML(xmlFile);
            this.xmlEntries.addAll(entries);
        }
        
        showXMLEntriesToTable();
    }
    
    private List<TaxonElementEntry> readOldXML(File xmlFile) throws IOException {
        List<TaxonElementEntry> elements = new ArrayList<TaxonElementEntry>();
        
        OldTaxonXMLReader reader = new OldTaxonXMLReader(xmlFile);
        Taxonomy taxon = reader.parseTaxon();
        
        // descriptions
        if(taxon.getDescriptions() != null) {
            for(TaxonomyDescription desc : taxon.getDescriptions()) {
                String title = desc.getTitle();
                if(title == null || title.trim().isEmpty()) {
                    title = desc.getType().toString();
                }
                String content = desc.getDescription();

                TaxonElementEntry entry = new TaxonElementEntry();
                entry.setFile(xmlFile);
                entry.setElement("description");
                entry.setType(title);
                entry.setBody(content);
                // mapping?
                //entry.setMapToElement(null);
                
                elements.add(entry);
            }
        }
        
        // scope
        if(taxon.getScope() != null) {
            TaxonomyScope scope = taxon.getScope();
            String content = scope.getScope();
            
            TaxonElementEntry entry = new TaxonElementEntry();
            entry.setFile(xmlFile);
            entry.setElement("scope");
            entry.setType(null);
            entry.setBody(content);
            // mapping?
            //entry.setMapToElement(null);
            
            elements.add(entry);
        }
        
        // keywords
        if(taxon.getKeywords() != null) {
            TaxonomyKeywords keywords = taxon.getKeywords();
            String content = keywords.getKeywords();
            
            TaxonElementEntry entry = new TaxonElementEntry();
            entry.setFile(xmlFile);
            entry.setElement("keywords");
            entry.setType(null);
            entry.setBody(content);
            // mapping?
            //entry.setMapToElement(null);
            
            elements.add(entry);
        }
        
        // synonym
        if(taxon.getSynonyms() != null) {
            for(TaxonomySynonym synonym : taxon.getSynonyms()) {
                String content = synonym.getSynonym();

                TaxonElementEntry entry = new TaxonElementEntry();
                entry.setFile(xmlFile);
                entry.setElement("synonym");
                entry.setType(null);
                entry.setBody(content);
                // mapping?
                //entry.setMapToElement(null);

                elements.add(entry);
            }
        }
            
        // typespecies
        if(taxon.getTypeSpecies() != null) {
            for(TaxonomyTypeSpecies typespecies : taxon.getTypeSpecies()) {
                String content = typespecies.getTypeSpecies();

                TaxonElementEntry entry = new TaxonElementEntry();
                entry.setFile(xmlFile);
                entry.setElement("typespecies");
                entry.setType(null);
                entry.setBody(content);
                // mapping?
                //entry.setMapToElement(null);

                elements.add(entry);
            }
        }
        
        // inner
        if(taxon.getInnerElements() != null) {
            for(TaxonomyGenericElement generic : taxon.getInnerElements()) {
                if(generic.getInnerElements() != null && generic.getInnerElements().size() != 0) {
                    throw new IOException("cascading elements");
                }
                
                String title = generic.getName();
                String content = generic.getText();

                TaxonElementEntry entry = new TaxonElementEntry();
                entry.setFile(xmlFile);
                entry.setElement(title);
                entry.setType(title);
                entry.setBody(content);
                // mapping?
                //entry.setMapToElement(null);

                elements.add(entry);
            }
        }
        
        // discussion
        if(taxon.getDiscussionNonTitled() != null) {
            for(TaxonomyDiscussion discussion : taxon.getDiscussionNonTitled()) {
                if(discussion.getInnerElements() != null && discussion.getInnerElements().size() != 0) {
                    throw new IOException("cascading elements");
                }
                
                String content = discussion.getText();

                TaxonElementEntry entry = new TaxonElementEntry();
                entry.setFile(xmlFile);
                entry.setElement("discussion");
                entry.setType(null);
                entry.setBody(content);
                // mapping?
                //entry.setMapToElement(null);

                elements.add(entry);
            }
        }
        
        // discussion
        if(taxon.getDiscussion() != null) {
            TaxonomyDiscussion discussion = taxon.getDiscussion();
            if(discussion.getText() != null && !discussion.getText().trim().equals("")) {
                throw new IOException("cascading elements");
            }
                    
            if(discussion.getInnerElements() != null) {
                for(TaxonomyGenericElement generic : discussion.getInnerElements()) {
                    if (generic.getInnerElements() != null && generic.getInnerElements().size() != 0) {
                        throw new IOException("cascading elements");
                    }
                    
                    String title = generic.getName();
                    String content = generic.getText();

                    TaxonElementEntry entry = new TaxonElementEntry();
                    entry.setFile(xmlFile);
                    entry.setElement("discussion/" + title);
                    entry.setType(title);
                    entry.setBody(content);
                    // mapping?
                    //entry.setMapToElement(null);

                    elements.add(entry);
                }
            }
        }
        
        return elements;
    }
    
    private void prepareNewSchemaEntries() throws Exception {
        this.newXmlEntries = new ArrayList<SchemaMappingEntry>();
        
        //populate array
        
        showXMLMappingEntriesToTable();
    }

    private void showXMLEntriesToTable() {
        DefaultTableModel model = (DefaultTableModel) this.tblOldTaxonElements.getModel();
        model.setRowCount(0);
        
        if (this.xmlEntries != null) {
            for(TaxonElementEntry entry : this.xmlEntries) {
                Object[] rowData = new Object[5];
                rowData[0] = entry.getFilename();
                rowData[1] = entry.getElement();
                rowData[2] = entry.getType();
                rowData[3] = entry.getBody();
                if(entry.getMapToElement() != null) {
                    rowData[4] = entry.getMapToElement().getElement();
                } else {
                    rowData[4] = null;
                }
                model.addRow(rowData);
            }
        }
    }
    
    private void showXMLMappingEntriesToTable() {
        DefaultTableModel model = (DefaultTableModel) this.tblNewTaxonElements.getModel();
        model.setRowCount(0);
        
        if (this.xmlEntries != null) {
            for(SchemaMappingEntry entry : this.newXmlEntries) {
                Object[] rowData = new Object[2];
                rowData[0] = entry.getElement();
                rowData[1] = entry.getHasType();
                model.addRow(rowData);
            }
        }
    }
    
    private void fireTableSelected() {
        try {
            int[] selectedRows = this.tblOldTaxonElements.getSelectedRows();
            if(selectedRows.length > 0) {
                showParagraph(selectedRows[0]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showParagraph(int index) {
        this.txtTitle.setText("");
        this.txtBody.setText("");
        DefaultTableModel model = (DefaultTableModel) this.tblOldTaxonElements.getModel();
        String title = (String) model.getValueAt(index, 2);
        String body = (String) model.getValueAt(index, 3);

        if(title != null) {
            this.txtTitle.setText(title);
        }

        if(body != null) {
            this.txtBody.setText(body);
        }

        this.txtBody.setCaretPosition(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnStart = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOldTaxonElements = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        btnLoadTaxons = new javax.swing.JButton();
        lblTaxons = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNewTaxonElements = new javax.swing.JTable();
        btnUpdateMapping = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtBody = new javax.swing.JTextArea();
        txtTitle = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnStart.setText("Start!");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        tblOldTaxonElements.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "file", "element", "type", "body", "map_to"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOldTaxonElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblOldTaxonElements.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblOldTaxonElements);
        tblOldTaxonElements.getColumnModel().getColumn(0).setHeaderValue("file");
        tblOldTaxonElements.getColumnModel().getColumn(3).setHeaderValue("body");
        tblOldTaxonElements.getColumnModel().getColumn(4).setHeaderValue("map_to");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnLoadTaxons.setText("Load TaxonXML");
        btnLoadTaxons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadTaxonsActionPerformed(evt);
            }
        });

        lblTaxons.setText("Not Loaded");

        tblNewTaxonElements.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "element", "has_type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNewTaxonElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblNewTaxonElements.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblNewTaxonElements);

        btnUpdateMapping.setText("Update Mapping");
        btnUpdateMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateMappingActionPerformed(evt);
            }
        });

        txtBody.setColumns(20);
        txtBody.setLineWrap(true);
        txtBody.setRows(5);
        jScrollPane3.setViewportView(txtBody);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLoadTaxons)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTaxons)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)
                            .addComponent(txtTitle))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdateMapping, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLoadTaxons)
                        .addComponent(lblTaxons)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateMapping)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        try {
            startParsing();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            //exportErrata();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnLoadTaxonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadTaxonsActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Taxon XML file", "xml"));

        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            if(file.getName().toLowerCase().endsWith(".xml")) {
                loadTaxonFiles(file);
            } else {
                JOptionPane.showMessageDialog(this, "You can select only taxon xml files");
            }
        }
    }//GEN-LAST:event_btnLoadTaxonsActionPerformed

    private void btnUpdateMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateMappingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateMappingActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TaxonConverterGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TaxonConverterGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TaxonConverterGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TaxonConverterGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TaxonConverterGui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoadDjvu;
    private javax.swing.JButton btnLoadTaxons;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnUpdateMapping;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTaxons;
    private javax.swing.JTable tblNewTaxonElements;
    private javax.swing.JTable tblOldTaxonElements;
    private javax.swing.JTextArea txtBody;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables

    
}
