/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBElement;
import xml.newformat.beans.treatment.Description;
import xml.newformat.beans.treatment.Discussion;
import xml.newformat.beans.treatment.HabitatElevationDistributionOrEcology;
import xml.newformat.beans.treatment.Material;
import xml.newformat.beans.treatment.Meta;
import xml.newformat.beans.treatment.ProcessedBy;
import xml.newformat.beans.treatment.Processor;
import xml.newformat.beans.treatment.TaxonIdentification;
import xml.newformat.beans.treatment.TaxonRelationArticulation;
import xml.newformat.beans.treatment.Treatment;
import xml.newformat.beans.treatment.Type;
import xml.old.beans.Taxonomy;
import xml.old.beans.TaxonomyDescription;
import xml.old.beans.TaxonomyDescription.TaxonomyDescriptionType;
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
public class TaxonConverter {

    private Treatment dest;
    private Taxonomy origin;
    private xml.newformat.beans.treatment.ObjectFactory treatmentObjectFactory;

    public TaxonConverter(Treatment treatment, Taxonomy taxon) {
        this.dest = treatment;
        this.origin = taxon;
        this.treatmentObjectFactory = new xml.newformat.beans.treatment.ObjectFactory();
    }

    public void convert(String bibligraph, String processor_name) throws IOException {
        convMeta(bibligraph, processor_name);
        convNomenclature();
        convCommonNames();
        convKeywords();
        convSynonyms();
        convScope();
        convTypeSpecies();
        convDiscussions();
        convDescriptions();
        convKeyFiles();
        convAllOthers();
    }

    private void convMeta(String bibligraph, String processor_name) throws IOException {
        TaxonomyMeta meta = this.origin.getMeta();
        if (meta != null) {
            // set bibrigraph
            Meta newMeta = this.treatmentObjectFactory.createMeta();
            //newMeta.setSource(bibligraph);
            newMeta.setSource(meta.getSource());

            // set processor
            ProcessedBy newProcessedBy = this.treatmentObjectFactory.createProcessedBy();
            Processor newProcessor = this.treatmentObjectFactory.createProcessor();
            newProcessor.setValue(processor_name);
            newProcessor.setProcessType("format conversion");
            newProcessedBy.getProcessorOrCharaparser().add(newProcessor);

            newMeta.setProcessedBy(newProcessedBy);

            // original filename -> otherinfo
            //newMeta.getOtherInfoOnMeta().add(meta.getSource());

            this.dest.setMeta(newMeta);
        }
    }

    private void convNomenclature() throws IOException {
        TaxonomyNomenclature nomenclature = this.origin.getNomenclature();
        if (nomenclature != null) {
            String rank = nomenclature.getRank();
            String name_info = nomenclature.getNameInfo();
            String name = nomenclature.getName();
            String authority = nomenclature.getAuthority();
            String common_name = nomenclature.getCommonName();
            String hierarchy = nomenclature.getHierarchy();
            String hierarchy_clean = nomenclature.getHierarchyClean();
            List<TaxonomyOtherInfo> otherinfos = nomenclature.getOtherInfos();
            
            if(name.split("\\s").length < 3) {
                // pass
                // all parts are name
            } else {
                if (authority == null) {
                    authority = getAuthority(name);
                    name = getPureName(name);
                }
            }
            
            String[] name_parts = name.split("\\s");
            if(name_parts.length == 1) {
                // pass
            } else if(name_parts.length == 2) {
                // pass
            } else if(name_parts.length == 3) {
                if(name_parts[1].charAt(0) == '(' && name_parts[1].charAt(name_parts[1].length()-1) == ')') {
                    // pass
                    name_parts[1] = name_parts[1].substring(1, name_parts[1].length()-1);
                } else {
                    System.err.println("Warning! - # of name_parts are 3");
                    // pass
                }
            } else {
                throw new IOException("Name parsing error : len is not 2,3");
            }
            
            String[] rank_parts = new String[name_parts.length];
            rank_parts[rank_parts.length - 1] = Rank.findRank(rank);
            for(int i=0;i<rank_parts.length;i++) {
                int me = rank_parts.length - 1 - i;
                String myrank = rank_parts[me];
                int prev = me -1;
                if(prev >=0) {
                    rank_parts[prev] = Rank.findParentRank(myrank, name_parts.length);
                }
            }
            
            for(int i=0;i<name_parts.length;i++) {
                System.out.println("name[" + i + "] : " + name_parts[i] + " - " + rank_parts[i]);
            }
            
            if(authority != null) {
                if (authority.charAt(0) == '(' && authority.charAt(authority.length() - 1) == ')') {
                    // pass
                    authority = authority.substring(1, authority.length() - 1);
                }
            }
            
            System.out.println("authority : " + authority);
            
            String newHierarchy = "";
            for(int i=0;i<name_parts.length;i++) {
                if(!newHierarchy.equals("")) {
                    newHierarchy += "; ";
                }
                newHierarchy += rank_parts[i] + " " + name_parts[i];
            }
            
            System.out.println("hierarchy : " + newHierarchy);

            TaxonIdentification newTaxonIdentification = this.treatmentObjectFactory.createTaxonIdentification();

            for(int i=0;i<name_parts.length;i++) {
                if (name_parts[i] != null) {
                    JAXBElement<String> newName;
                    if (rank_parts[i].toLowerCase().equals("family")) {
                        newName = this.treatmentObjectFactory.createFamilyName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subfamily")) {
                        newName = this.treatmentObjectFactory.createSubfamilyName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("tribe")) {
                        newName = this.treatmentObjectFactory.createTribeName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subtribe")) {
                        newName = this.treatmentObjectFactory.createSubtribeName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("genus")) {
                        newName = this.treatmentObjectFactory.createGenusName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subgenus")) {
                        newName = this.treatmentObjectFactory.createSubgenusName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("section")) {
                        newName = this.treatmentObjectFactory.createSectionName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subsection")) {
                        newName = this.treatmentObjectFactory.createSubsectionName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("series")) {
                        newName = this.treatmentObjectFactory.createSeriesName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("species")) {
                        newName = this.treatmentObjectFactory.createSpeciesName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subspecies")) {
                        newName = this.treatmentObjectFactory.createSubspeciesName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("variety")) {
                        newName = this.treatmentObjectFactory.createVarietyName(name_parts[i]);
                    } else {
                        throw new IOException("Unknown rank");
                    }

                    newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newName);
                }    
            }
            
            if (authority != null) {
                JAXBElement<String> newAuthority;
                if (rank.toLowerCase().equals("family")) {
                    newAuthority = this.treatmentObjectFactory.createFamilyAuthority(authority);
                } else if (rank.toLowerCase().equals("subfamily")) {
                    newAuthority = this.treatmentObjectFactory.createSubfamilyAuthority(authority);
                } else if (rank.toLowerCase().equals("tribe")) {
                    newAuthority = this.treatmentObjectFactory.createTribeAuthority(authority);
                } else if (rank.toLowerCase().equals("subtribe")) {
                    newAuthority = this.treatmentObjectFactory.createSubtribeAuthority(authority);
                } else if (rank.toLowerCase().equals("genus")) {
                    newAuthority = this.treatmentObjectFactory.createGenusAuthority(authority);
                } else if (rank.toLowerCase().equals("subgenus")) {
                    newAuthority = this.treatmentObjectFactory.createSubgenusAuthority(authority);
                } else if (rank.toLowerCase().equals("section")) {
                    newAuthority = this.treatmentObjectFactory.createSectionAuthority(authority);
                } else if (rank.toLowerCase().equals("subsection")) {
                    newAuthority = this.treatmentObjectFactory.createSubsectionAuthority(authority);
                } else if (rank.toLowerCase().equals("series")) {
                    newAuthority = this.treatmentObjectFactory.createSeriesAuthority(authority);
                } else if (rank.toLowerCase().equals("species")) {
                    newAuthority = this.treatmentObjectFactory.createSpeciesAuthority(authority);
                } else if (rank.toLowerCase().equals("subspecies")) {
                    newAuthority = this.treatmentObjectFactory.createSubspeciesAuthority(authority);
                } else if (rank.toLowerCase().equals("variety")) {
                    newAuthority = this.treatmentObjectFactory.createVarietyAuthority(authority);
                } else {
                    throw new IOException("Unknown rank");
                }

                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newAuthority);
            }

            if (name_info != null) {
                JAXBElement<String> newNameInfo = this.treatmentObjectFactory.createOtherInfoOnName(name_info);
                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newNameInfo);
            }

            if (common_name != null) {
                // common_name will be moved to treatment
                //JAXBElement<String> newCommonName = this.treatmentObjectFactory.createOtherInfoOnName(common_name);
                //newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newCommonName);
            }

            if (newHierarchy != null) {
                newTaxonIdentification.getTaxonHierarchy().add(newHierarchy);
            }

            if (otherinfos != null) {
                for (TaxonomyOtherInfo otherinfo : otherinfos) {
                    JAXBElement<String> newOtherInfo = this.treatmentObjectFactory.createOtherInfoOnName(otherinfo.getOtherInfo());
                    newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newOtherInfo);
                }
            }

            this.dest.getTaxonIdentification().add(newTaxonIdentification);
        }
    }

    private void convCommonNames() throws IOException {
        TaxonomyNomenclature nomenclature = this.origin.getNomenclature();
        if (nomenclature != null) {
            String commonName = nomenclature.getCommonName();
            if(commonName != null) {
                String[] commonNames = commonName.split("[,;]");
                if (commonNames != null) {
                    for (String commonNameString : commonNames) {
                        if (!commonNameString.trim().equals("")) {
                            JAXBElement<String> newOtherName = this.treatmentObjectFactory.createOtherName(commonNameString.trim());
                            this.dest.getDescriptionOrTypeOrSynonym().add(newOtherName);
                        }
                    }
                }
            }
        }
    }

    private void convKeywords() throws IOException {
        // keywords
        TaxonomyKeywords keywords = this.origin.getKeywords();
        if (keywords != null) {
            String keyword_str = keywords.getKeywords();

            Discussion discussion = this.treatmentObjectFactory.createDiscussion();
            discussion.setType("keywords");
            discussion.setValue(keyword_str);

            this.dest.getDescriptionOrTypeOrSynonym().add(discussion);
        }
    }

    private void convSynonyms() throws IOException {
        // synonyms
        List<TaxonomySynonym> synonyms = this.origin.getSynonyms();
        if (synonyms != null) {
            for (TaxonomySynonym synonym : synonyms) {
                String synonym_str = synonym.getSynonym();
                JAXBElement<String> newSynonym = this.treatmentObjectFactory.createSynonym(synonym_str);
                this.dest.getDescriptionOrTypeOrSynonym().add(newSynonym);
            }
        }
    }

    private void convScope() throws IOException {
        // scope
        TaxonomyScope scope = this.origin.getScope();
        if (scope != null) {
            String scope_str = scope.getScope();

            Discussion discussion = this.treatmentObjectFactory.createDiscussion();
            discussion.setType("scope");
            discussion.setValue(scope_str);

            this.dest.getDescriptionOrTypeOrSynonym().add(discussion);
        }
    }

    private void convTypeSpecies() throws IOException {
        List<TaxonomyTypeSpecies> typespecies = this.origin.getTypeSpecies();
        if (typespecies != null) {
            for (TaxonomyTypeSpecies species : typespecies) {
                String species_str = species.getTypeSpecies();
                Type newType = this.treatmentObjectFactory.createType();
                newType.setType("type_species");
                newType.setValue(species_str);
                this.dest.getDescriptionOrTypeOrSynonym().add(newType);
            }
        }
    }

    private void convDiscussions() throws IOException {
        List<TaxonomyDiscussion> discussions = this.origin.getDiscussionNonTitled();
        if (discussions != null) {
            for (TaxonomyDiscussion discussion : discussions) {
                if (discussion.getInnerElements() != null) {
                    List<TaxonomyGenericElement> innerDiscussions = discussion.getInnerElements();
                    if (innerDiscussions != null) {
                        for (TaxonomyGenericElement element : innerDiscussions) {
                            String type = convertSubtitle(element.getName());
                            String content = element.getText();

                            Discussion newDiscussion = this.treatmentObjectFactory.createDiscussion();
                            newDiscussion.setType(type);
                            newDiscussion.setValue(content);
                            this.dest.getDescriptionOrTypeOrSynonym().add(newDiscussion);
                        }
                    }
                }

                String discussion_str = discussion.getText();
                Discussion newDiscussion = this.treatmentObjectFactory.createDiscussion();
                newDiscussion.setValue(discussion_str);
                this.dest.getDescriptionOrTypeOrSynonym().add(newDiscussion);
            }
        }

        TaxonomyDiscussion discussion = this.origin.getDiscussion();
        if (discussion != null) {
            List<TaxonomyGenericElement> innerDiscussions = discussion.getInnerElements();
            if (innerDiscussions != null) {
                for (TaxonomyGenericElement element : innerDiscussions) {
                    String type = convertSubtitle(element.getName());
                    String content = element.getText();

                    Discussion newDiscussion = this.treatmentObjectFactory.createDiscussion();
                    newDiscussion.setType(type);
                    newDiscussion.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newDiscussion);
                }
            }
        }
    }

    private void convDescriptions() throws IOException {
        List<TaxonomyDescription> descriptions = this.origin.getDescriptions();
        if (descriptions != null) {
            for (TaxonomyDescription description : descriptions) {
                String type = description.getTitle();
                String content = description.getDescription();
                if (description.getType() != null) {
                    if (!description.getType().equals(TaxonomyDescriptionType.DESCRIPTION_GENERIC)) {
                        type = description.getType().toString();
                    }
                }
                
                type = convertSubtitle(type);

                Description newDescription = this.treatmentObjectFactory.createDescription();
                newDescription.setType(type);
                newDescription.setValue(content);
                this.dest.getDescriptionOrTypeOrSynonym().add(newDescription);
            }
        }
    }

    private void convKeyFiles() throws IOException {
        List<TaxonomyKeyFile> keyFiles = this.origin.getKeyFiles();
        if (keyFiles != null) {
            for (TaxonomyKeyFile keyFile : keyFiles) {
                String keyfile = keyFile.getKeyFile();

                this.treatmentObjectFactory.createKeyFile(keyfile);
                JAXBElement<String> newKeyFile = this.treatmentObjectFactory.createKeyFile(keyfile);
                this.dest.getDescriptionOrTypeOrSynonym().add(newKeyFile);
            }
        }
    }

    private void convAllOthers() throws IOException {
        List<TaxonomyGenericElement> elements = this.origin.getInnerElements();
        if (elements != null) {
            for (TaxonomyGenericElement element : elements) {
                String type = convertSubtitle(element.getName());
                String content = element.getText();

                if (isTypeRelated(type)) {
                    Type newType = this.treatmentObjectFactory.createType();
                    newType.setType(type);
                    newType.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newType);
                } else if (isMaterialRelated(type)) {
                    Material newMaterial = this.treatmentObjectFactory.createMaterial();
                    newMaterial.setType(type);
                    newMaterial.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newMaterial);
                } else if (isArticulationRelated(type)) {
                    TaxonRelationArticulation newArticulation = this.treatmentObjectFactory.createTaxonRelationArticulation();
                    newArticulation.setType(type);
                    newArticulation.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newArticulation);
                } else if (isHabitatRelated(type)) {
                    HabitatElevationDistributionOrEcology newHabitat = this.treatmentObjectFactory.createHabitatElevationDistributionOrEcology();
                    newHabitat.setType(type);
                    newHabitat.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newHabitat);
                } else if (isExceptionalDescriptions(type)) {
                    Description newDescription = this.treatmentObjectFactory.createDescription();
                    newDescription.setType(type);
                    newDescription.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newDescription);
                } else if (isHabit(type)) {
                    Description newDescription = this.treatmentObjectFactory.createDescription();
                    newDescription.setType("habit");
                    newDescription.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newDescription);
                } else {
                    Discussion newDiscussion = this.treatmentObjectFactory.createDiscussion();
                    newDiscussion.setType(type);
                    newDiscussion.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newDiscussion);
                }
            }
        }
    }

    public Treatment getTreatment() {
        return this.dest;
    }

    private boolean isTypeRelated(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("type") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isMaterialRelated(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("material") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isArticulationRelated(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("articulation") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isHabitatRelated(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("habitat") >= 0) {
            return true;
        } else if (lower.indexOf("elevation") >= 0) {
            return true;
        } else if (lower.indexOf("distribution") >= 0) {
            return true;
        } else if (lower.indexOf("distribute") >= 0) {
            return true;
        } else if (lower.indexOf("ecology") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isHabit(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("habit") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isExceptionalDescriptions(String type) {
        String lower = type.toLowerCase();
        if (lower.indexOf("isolated_species_in_sediments") >= 0) {
            return true;
        } else if (lower.indexOf("massive_spicular_skeleton") >= 0) {
            return true;
        }
        return false;
    }
    
    private String getPureName(String name) {
        String authority = getAuthority(name);
        
        int idx = name.indexOf(authority);
        if(idx >= 0) {
            return name.substring(0, idx).trim();
        }
        
        System.out.println("No purename found - " + name);
        return null;
    }
    
    private String getAuthority(String name) {
        String name2 = name;
        if(name2.endsWith(", sp. nov.")) {
            int lastIdx = name2.indexOf(", sp. nov.");
            if(lastIdx >= 0) {
                name2 = name2.substring(0, lastIdx);
            }
        }
        
        int startPos = name2.length()-1;
        
        int commaIdx = name2.indexOf(",");
        if(commaIdx >= 0) {
            startPos = Math.min(startPos, commaIdx - 1);
        }
        
        int etIdx = name2.indexOf(" et ");
        if(etIdx >= 0) {
            startPos = Math.min(startPos, etIdx - 1);
        }
        
        int spacePos = 0;
        for(int i=startPos;i>=0;i--) {
            if(name2.charAt(i) == ' ') {
                if(i >= 1 && name2.charAt(i - 1) != '.') {
                    spacePos = i;
                    break;
                }
            }
        }

        String authority = name2.substring(spacePos + 1).trim();
        if((authority.charAt(0) >= 'A' && authority.charAt(0) <= 'Z')
                || authority.charAt(0) == '(') {
            return authority;
        } else {
            System.out.println("No authority found - " + name2);
            return null;
        }
    }
    
    private String convertSubtitle(String type) {
        if(type == null) {
            return null;
        }
        
        String newTitle = type.toLowerCase().trim();
        if(newTitle.endsWith(".")) {
            newTitle = newTitle.substring(0, newTitle.length()-1);
        }
        return newTitle;
    }
}
