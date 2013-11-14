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
        convAllOthers();
    }
    
    private void convMeta(String bibligraph, String processor_name) throws IOException {
        TaxonomyMeta meta = this.origin.getMeta();
        if(meta != null) {
            // set bibrigraph
            Meta newMeta = this.treatmentObjectFactory.createMeta();
            newMeta.setSource(bibligraph);
            
            // set processor
            ProcessedBy newProcessedBy = this.treatmentObjectFactory.createProcessedBy();
            Processor newProcessor = this.treatmentObjectFactory.createProcessor();
            newProcessor.setValue(processor_name);
            newProcessor.setProcessType("format conversion");
            newProcessedBy.getProcessorOrCharaparser().add(newProcessor);
            
            newMeta.setProcessedBy(newProcessedBy);
            
            // original filename -> otherinfo
            newMeta.getOtherInfoOnMeta().add(meta.getSource());
            
            this.dest.setMeta(newMeta);
        }
    }
    
    private void convNomenclature() throws IOException {
        TaxonomyNomenclature nomenclature = this.origin.getNomenclature();
        if(nomenclature != null) {
            String rank = nomenclature.getRank();
            String name_info = nomenclature.getNameInfo();
            String name = nomenclature.getName();
            String authority = nomenclature.getAuthority();
            String common_name = nomenclature.getCommonName();
            String hierarchy = nomenclature.getHierarchy();
            String hierarchy_clean = nomenclature.getHierarchyClean();
            List<TaxonomyOtherInfo> otherinfos = nomenclature.getOtherInfos();
            
            TaxonIdentification newTaxonIdentification = this.treatmentObjectFactory.createTaxonIdentification();
            
            if(name != null) {
                JAXBElement<String> newName;
                switch(rank.toLowerCase()) {
                    case "family":
                        newName = this.treatmentObjectFactory.createFamilyName(name);
                        break;
                    case "subfamily":
                        newName = this.treatmentObjectFactory.createSubfamilyName(name);
                        break;
                    case "tribe":
                        newName = this.treatmentObjectFactory.createTribeName(name);
                        break;
                    case "subtribe":
                        newName = this.treatmentObjectFactory.createSubtribeName(name);
                        break;
                    case "genus":
                        newName = this.treatmentObjectFactory.createGenusName(name);
                        break;
                    case "subgenus":
                        newName = this.treatmentObjectFactory.createSubgenusName(name);
                        break;
                    case "section":
                        newName = this.treatmentObjectFactory.createSectionName(name);
                        break;
                    case "subsection":
                        newName = this.treatmentObjectFactory.createSubsectionName(name);
                        break;
                    case "series":
                        newName = this.treatmentObjectFactory.createSeriesName(name);
                        break;
                    case "species":
                        newName = this.treatmentObjectFactory.createSpeciesName(name);
                        break;
                    case "subspecies":
                        newName = this.treatmentObjectFactory.createSubspeciesName(name);
                        break;
                    case "variety":
                        newName = this.treatmentObjectFactory.createVarietyName(name);
                        break;
                    default:
                        throw new IOException("Unknown rank");
                }

                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newName);
            }
            
            if(authority != null) {
                JAXBElement<String> newAuthority;
                switch(rank.toLowerCase()) {
                    case "family":
                        newAuthority = this.treatmentObjectFactory.createFamilyAuthority(authority);
                        break;
                    case "subfamily":
                        newAuthority = this.treatmentObjectFactory.createSubfamilyAuthority(authority);
                        break;
                    case "tribe":
                        newAuthority = this.treatmentObjectFactory.createTribeAuthority(authority);
                        break;
                    case "subtribe":
                        newAuthority = this.treatmentObjectFactory.createSubtribeAuthority(authority);
                        break;
                    case "genus":
                        newAuthority = this.treatmentObjectFactory.createGenusAuthority(authority);
                        break;
                    case "subgenus":
                        newAuthority = this.treatmentObjectFactory.createSubgenusAuthority(authority);
                        break;
                    case "section":
                        newAuthority = this.treatmentObjectFactory.createSectionAuthority(authority);
                        break;
                    case "subsection":
                        newAuthority = this.treatmentObjectFactory.createSubsectionAuthority(authority);
                        break;
                    case "series":
                        newAuthority = this.treatmentObjectFactory.createSeriesAuthority(authority);
                        break;
                    case "species":
                        newAuthority = this.treatmentObjectFactory.createSpeciesAuthority(authority);
                        break;
                    case "subspecies":
                        newAuthority = this.treatmentObjectFactory.createSubspeciesAuthority(authority);
                        break;
                    case "variety":
                        newAuthority = this.treatmentObjectFactory.createVarietyAuthority(authority);
                        break;
                    default:
                        throw new IOException("Unknown rank");
                }
                
                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newAuthority);
            }
            
            if(name_info != null) {
                JAXBElement<String> newNameInfo = this.treatmentObjectFactory.createOtherInfoOnName(name_info);
                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newNameInfo);
            }
            
            if(common_name != null) {
                // common_name will be moved to treatment
                //JAXBElement<String> newCommonName = this.treatmentObjectFactory.createOtherInfoOnName(common_name);
                //newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newCommonName);
            }
            
            if(hierarchy != null) {
                JAXBElement<String> newHierarchy = this.treatmentObjectFactory.createTaxonHierarchy(hierarchy);
                newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newHierarchy);
            }
            
            if(hierarchy_clean != null) {
                // dropped
            }
            
            if(otherinfos != null) {
                for(TaxonomyOtherInfo otherinfo : otherinfos) {
                    JAXBElement<String> newOtherInfo = this.treatmentObjectFactory.createOtherInfoOnName(otherinfo.getOtherInfo());
                    newTaxonIdentification.getFamilyNameOrFamilyAuthorityOrSubfamilyName().add(newOtherInfo);
                }
            }
            
            this.dest.getTaxonIdentification().add(newTaxonIdentification);
        }
    }
    
    private void convCommonNames() throws IOException {
        TaxonomyNomenclature nomenclature = this.origin.getNomenclature();
        if(nomenclature != null) {
            String commonName = nomenclature.getCommonName();
            
            String[] commonNames = commonName.split("[,;]");
            if(commonNames != null) {
                for(String commonNameString : commonNames) {
                    if(!commonNameString.trim().equals("")) {
                        JAXBElement<String> newOtherName = this.treatmentObjectFactory.createOtherName(commonNameString.trim());
                        this.dest.getDescriptionOrTypeOrSynonym().add(newOtherName);
                    }
                }
            }
        }
    }
    
    private void convKeywords() throws IOException {
        // keywords
        TaxonomyKeywords keywords = this.origin.getKeywords();
        if(keywords != null) {
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
        if(synonyms != null) {
            for(TaxonomySynonym synonym : synonyms) {
                String synonym_str = synonym.getSynonym();
                JAXBElement<String> newSynonym = this.treatmentObjectFactory.createSynonym(synonym_str);
                this.dest.getDescriptionOrTypeOrSynonym().add(newSynonym);
            }
        }
    }
    
    private void convScope() throws IOException {
        // scope
        TaxonomyScope scope = this.origin.getScope();
        if(scope != null) {
            String scope_str = scope.getScope();
            
            Discussion discussion = this.treatmentObjectFactory.createDiscussion();
            discussion.setType("scope");
            discussion.setValue(scope_str);
            
            this.dest.getDescriptionOrTypeOrSynonym().add(discussion);
        }
    }
    
    private void convTypeSpecies() throws IOException {
        List<TaxonomyTypeSpecies> typespecies = this.origin.getTypeSpecies();
        if(typespecies != null) {
            for(TaxonomyTypeSpecies species : typespecies) {
                String species_str = species.getTypeSpecies();
                Type newType = this.treatmentObjectFactory.createType();
                newType.setType("type species");
                newType.setValue(species_str);
                this.dest.getDescriptionOrTypeOrSynonym().add(newType);
            }
        }
    }
    
    private void convDiscussions() throws IOException {
        List<TaxonomyDiscussion> discussions = this.origin.getDiscussionNonTitled();
        if(discussions != null) {
            for(TaxonomyDiscussion discussion : discussions) {
                if(discussion.getInnerElements() != null) {
                    List<TaxonomyGenericElement> innerDiscussions = discussion.getInnerElements();
                    if (innerDiscussions != null) {
                        for (TaxonomyGenericElement element : innerDiscussions) {
                            String type = element.getName();
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
        if(discussion != null) {
            List<TaxonomyGenericElement> innerDiscussions = discussion.getInnerElements();
            if(innerDiscussions != null) {
                for(TaxonomyGenericElement element : innerDiscussions) {
                    String type = element.getName();
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
        if(descriptions != null) {
            for(TaxonomyDescription description : descriptions) {
                String type = description.getTitle();
                String content = description.getDescription();
                if(description.getType() != null) {
                    if(!description.getType().equals(TaxonomyDescriptionType.DESCRIPTION_GENERIC)) {
                        type = description.getType().toString();
                    }
                }
                
                Description newDescription = this.treatmentObjectFactory.createDescription();
                newDescription.setType(type);
                newDescription.setValue(content);
                this.dest.getDescriptionOrTypeOrSynonym().add(newDescription);
            }
        }
    }
    
    private void convAllOthers() throws IOException {
        List<TaxonomyGenericElement> elements = this.origin.getInnerElements();
        if(elements != null) {
            for(TaxonomyGenericElement element : elements) {
                String type = element.getName();
                String content = element.getText();
                
                if(isTypeRelated(type)) {
                    Type newType = this.treatmentObjectFactory.createType();
                    newType.setType(type);
                    newType.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newType);
                } else if(isMaterialRelated(type)) {
                    Material newMaterial = this.treatmentObjectFactory.createMaterial();
                    newMaterial.setType(type);
                    newMaterial.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newMaterial);
                } else if(isArticulationRelated(type)) {
                    TaxonRelationArticulation newArticulation = this.treatmentObjectFactory.createTaxonRelationArticulation();
                    newArticulation.setType(type);
                    newArticulation.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newArticulation);
                } else if(isHabitatRelated(type)) {
                    HabitatElevationDistributionOrEcology newHabitat = this.treatmentObjectFactory.createHabitatElevationDistributionOrEcology();
                    newHabitat.setType(type);
                    newHabitat.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newHabitat);
                } else if(isExceptionalDescriptions(type)) {
                    Description newDescription = this.treatmentObjectFactory.createDescription();
                    newDescription.setType(type);
                    newDescription.setValue(content);
                    this.dest.getDescriptionOrTypeOrSynonym().add(newDescription);
                } else if(isHabit(type)) {
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
        if(lower.indexOf("type") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isMaterialRelated(String type) {
        String lower = type.toLowerCase();
        if(lower.indexOf("material") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isArticulationRelated(String type) {
        String lower = type.toLowerCase();
        if(lower.indexOf("articulation") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isHabitatRelated(String type) {
        String lower = type.toLowerCase();
        if(lower.indexOf("habitat") >= 0) {
            return true;
        } else if(lower.indexOf("elevation") >= 0) {
            return true;
        } else if(lower.indexOf("distribution") >= 0) {
            return true;
        } else if(lower.indexOf("distribute") >= 0) {
            return true;
        } else if(lower.indexOf("ecology") >= 0) {
            return true;
        }
        return false;
    }
    
    private boolean isHabit(String type) {
        String lower = type.toLowerCase();
        if(lower.indexOf("habit") >= 0) {
            return true;
        }
        return false;
    }

    private boolean isExceptionalDescriptions(String type) {
        String lower = type.toLowerCase();
        if(lower.indexOf("isolated_species_in_sediments") >= 0) {
            return true;
        } else if(lower.indexOf("massive_spicular_skeleton") >= 0) {
            return true;
        }
        return false;
    }
}
