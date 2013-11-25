/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.IOException;
import java.util.ArrayList;
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

    private Hierarchy hierarchy;
    private Treatment dest;
    private Taxonomy origin;
    private xml.newformat.beans.treatment.ObjectFactory treatmentObjectFactory;

    public TaxonConverter(Hierarchy hierarchy, Treatment treatment, Taxonomy taxon) {
        this.hierarchy = hierarchy;
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
            //String hierarchy = nomenclature.getHierarchy();
            //String hierarchy_clean = nomenclature.getHierarchyClean();
            List<TaxonomyOtherInfo> otherinfos = nomenclature.getOtherInfos();
            
            if (authority != null) {
                name = name.trim() + " " + authority.trim();
            }
            
            name = removeFirstRank(name);
                
            System.out.println("name : " + name);
            
            HierarchyEntry hierarchy = getHierarchy(name, Rank.findRank(rank));
            
            for(int i=0;i<hierarchy.getNames().length;i++) {
                System.out.println("name[" + i + "] : " + hierarchy.getNames()[i] + " - " + hierarchy.getRanks()[i] + " - " + hierarchy.getAuthorities()[i]);
            }
            
            // check to hierarchy
            HierarchyEntry completeHierarchy = this.hierarchy.getCompleteHierarchyNCA(hierarchy);
            
            // generate hierarchy
            String newHierarchy = completeHierarchy.getHierarchyString();

            String[] name_parts = completeHierarchy.getNames();
            String[] rank_parts = completeHierarchy.getRanks();
            String[] authority_parts = completeHierarchy.getAuthorities();
            
            System.out.println("hierarchy : " + newHierarchy);

            TaxonIdentification newTaxonIdentification = this.treatmentObjectFactory.createTaxonIdentification();

            for(int i=0;i<name_parts.length;i++) {
                if (name_parts[i] != null) {
                    JAXBElement<String> newName = null;
                    JAXBElement<String> newAuthority = null;
                    if (rank_parts[i].toLowerCase().equals("order")) {
                        newName = this.treatmentObjectFactory.createOrderName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createOrderAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("suborder")) {
                        newName = this.treatmentObjectFactory.createSuborderName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSuborderAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("superfamily")) {
                        newName = this.treatmentObjectFactory.createSuperfamilyName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSuperfamilyAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("family")) {
                        newName = this.treatmentObjectFactory.createFamilyName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createFamilyAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subfamily")) {
                        newName = this.treatmentObjectFactory.createSubfamilyName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSubfamilyAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("tribe")) {
                        newName = this.treatmentObjectFactory.createTribeName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createTribeAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subtribe")) {
                        newName = this.treatmentObjectFactory.createSubtribeName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSubtribeAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("genus")) {
                        newName = this.treatmentObjectFactory.createGenusName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createGenusAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("genus_group")) {
                        newName = this.treatmentObjectFactory.createGenusGroupName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subgenus")) {
                        newName = this.treatmentObjectFactory.createSubgenusName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSubgenusAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("section")) {
                        newName = this.treatmentObjectFactory.createSectionName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSectionAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subsection")) {
                        newName = this.treatmentObjectFactory.createSubsectionName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSubsectionAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("series")) {
                        newName = this.treatmentObjectFactory.createSeriesName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSeriesAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("species")) {
                        newName = this.treatmentObjectFactory.createSpeciesName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSpeciesAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("species_group")) {
                        newName = this.treatmentObjectFactory.createSpeciesGroupName(name_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("subspecies")) {
                        newName = this.treatmentObjectFactory.createSubspeciesName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createSubspeciesAuthority(authority_parts[i]);
                    } else if (rank_parts[i].toLowerCase().equals("variety")) {
                        newName = this.treatmentObjectFactory.createVarietyName(name_parts[i]);
                        newAuthority = this.treatmentObjectFactory.createVarietyAuthority(authority_parts[i]);
                    } else {
                        throw new IOException("Unknown rank");
                    }

                    newTaxonIdentification.getOrderNameOrOrderAuthorityOrSuborderName().add(newName);
                    
                    if (newAuthority != null && newAuthority.getValue() != null) {
                        newTaxonIdentification.getOrderNameOrOrderAuthorityOrSuborderName().add(newAuthority);
                    }
                }    
            }
            
            if (name_info != null) {
                JAXBElement<String> newNameInfo = this.treatmentObjectFactory.createOtherInfoOnName(name_info);
                newTaxonIdentification.getOrderNameOrOrderAuthorityOrSuborderName().add(newNameInfo);
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
                    newTaxonIdentification.getOrderNameOrOrderAuthorityOrSuborderName().add(newOtherInfo);
                }
            }

            this.dest.getTaxonIdentification().add(newTaxonIdentification);
            
            // add to hierarchy
            this.hierarchy.addEntry(completeHierarchy);
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
        String new_name = "";
        String[] names = name.split("\\s");
        for(int i=0;i<names.length;i++) {
            new_name += names[i].trim() + " ";
        }
        
        new_name = new_name.trim();
        String authority = getAuthority(new_name);
        
        if(authority == null) {
            return name;
        }
        
        int idx = new_name.lastIndexOf(authority);
        if(idx >= 0) {
            return new_name.substring(0, idx).trim();
        }
        
        System.out.println("No purename found - " + new_name);
        return null;
    }
    
    private boolean isPossibleAuthority(String name) {
        if(name.charAt(0) == '(' || name.charAt(name.length() - 1) == ')') {
            return true;
        }
        
        if(name.charAt(0) >= 'A' && name.charAt(0) <= 'Z') {
            // check others
            boolean others = false;
            for(int i=1;i<name.length();i++) {
                if(name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {
                    others = true;
                }
            }
            if(!others) {
                return true;
            }
        }
        
        //if(name.charAt(0) >= 'A' && name.charAt(0) <= 'Z') {
            // check others
            if(name.charAt(name.length() - 1) == '.') {
                return true;
            }
        //}
        
        if(name.equalsIgnoreCase("nov.")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("sp.")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("in")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("ex")) {
            return true;
        }
        
        if(name.endsWith(",")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("et")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("&")) {
            return true;
        }
        
        if(name.equalsIgnoreCase("de")) {
            return true;
        }
        
        return false;
    }
    
    private String getAuthority(String name) {
        
        String buffer = "";
        boolean foundAuthority = false;
        String[] names = name.trim().split("\\s");
        for(int i=0;i<names.length;i++) {
            int me = names.length - 1 - i;
            if(me == 0) {
                break;
            }
            if(isPossibleAuthority(names[me].trim())) {
                buffer = names[me] + " " + buffer;
                foundAuthority = true;
            } else {
                break;
            }
        }
        
        if(foundAuthority) {
            return buffer.trim();
        } else {
            System.out.println("No authority found - " + name);
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

    private String removeFirstRank(String name) {
        String[] name_parts = name.split("\\s");
        boolean hasRank = false;
        if(name_parts.length > 1) {
            if(Rank.checkRank(name_parts[0])) {
                hasRank = true;
            }
            
            if(hasRank) {
                String newName = "";
                for(int i=1;i<name_parts.length;i++) {
                    if(!newName.equals("")) {
                        newName += " ";
                    }
                    newName += name_parts[i];
                }
                return newName;
            }
            return name;
            
        } else {
            return name;
        }
    }

    private HierarchyEntry getHierarchy(String name, String rank) throws IOException {
        List<String> new_name_parts = new ArrayList<String>();
        List<String> new_rank_parts = new ArrayList<String>();
        List<String> new_auth_parts = new ArrayList<String>();
        
        String firstPart = null;
        boolean hasSecondPart = false;
        if(name.indexOf(" ssp. ") >= 0 || name.indexOf(" var. ") >= 0) {
            // split into two
            int idx = 0;
            int len = 0;
            String rnk = null;
            if(name.indexOf(" ssp. ") >= 0) {
                idx = name.indexOf(" ssp. ");
                len = 6;
                rnk = "Subspecies";
            } else if(name.indexOf(" var. ") >= 0) {
                idx = name.indexOf(" var. ");
                len = 6;
                rnk = "Variety";
            }
            
            firstPart = name.substring(0, idx).trim();
            String secondPart = name.substring(idx + 6).trim();
            
            String secondAuth = getAuthority(secondPart);
            String secondName = getPureName(secondPart);
            System.out.println("secondName : " + secondName);
            if(secondName.split("\\s").length > 1) {
                throw new IOException("second part has more than 2 parts : " + secondName);
            }
            
            new_name_parts.add(0, secondName);
            new_rank_parts.add(0, rnk);
            new_auth_parts.add(0, secondAuth);
            hasSecondPart = true;
        } else {
            firstPart = name;
        }
        
        String firstAuth = getAuthority(firstPart);
        String firstName = getPureName(firstPart);
        
        String[] name_parts = firstName.split("\\s");
        for(int i=name_parts.length-1;i>=0;i--) {
            String pure_name = removeBrace(name_parts[i]);
            new_name_parts.add(0, pure_name);
            if(i == name_parts.length-1) {
                new_auth_parts.add(0, firstAuth);
            } else {
                new_auth_parts.add(0, null);
            }
        }
        
        String prevRank = rank;
        for(int i=0;i<name_parts.length;i++) {
            int me = name_parts.length - 1 - i;
            if(hasSecondPart) {
                String newRank = Rank.findParentRank(prevRank, name_parts.length + 1);
                new_rank_parts.add(0, newRank);
                prevRank = newRank;
            } else {
                if(me == name_parts.length - 1) {
                    new_rank_parts.add(0, prevRank);
                } else {
                    String newRank = Rank.findParentRank(prevRank, name_parts.length);
                    new_rank_parts.add(0, newRank);
                    prevRank = newRank;
                }
            }
        }
        
        String[] entryNames = new String[new_name_parts.size()];
        String[] entryRanks = new String[new_rank_parts.size()];
        String[] entryAuths = new String[new_auth_parts.size()];
        
        entryNames = new_name_parts.toArray(entryNames);
        entryRanks = new_rank_parts.toArray(entryRanks);
        entryAuths = new_auth_parts.toArray(entryAuths);
        
        HierarchyEntry entry = new HierarchyEntry(entryNames, entryRanks, entryAuths);
        return entry;
    }
    
    private String removeBrace(String name) {
        if(name.startsWith("(") && name.endsWith(")")) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }
}
