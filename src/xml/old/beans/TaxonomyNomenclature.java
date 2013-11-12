/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.old.beans;

import java.util.ArrayList;
import java.util.List;
import common.xml.XMLWritable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyNomenclature extends XMLWritable {
    private String name;
    private String authority;
    private String rank;
    private String common_name;
    private String hierarchy;
    private String hierarchy_clean;
    private String name_info;
    private List<TaxonomyOtherInfo> other_infos;
    
    public TaxonomyNomenclature() {
        this.other_infos = new ArrayList<TaxonomyOtherInfo>();
    }
    
    public TaxonomyNomenclature(String name, String authority, String rank, String common_name, String name_info, String hierarchy, String hierarchy_clean) {
        this.name = name;
        this.authority = authority;
        this.rank = rank;
        this.common_name = common_name;
        this.name_info = name_info;
        this.hierarchy = hierarchy;
        this.hierarchy_clean = hierarchy_clean;
        
        this.other_infos = new ArrayList<TaxonomyOtherInfo>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAuthority() {
        return this.authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    public String getRank() {
        return this.rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public String getCommonName() {
        return this.common_name;
    }
    
    public void setCommonName(String common_name) {
        this.common_name = common_name;
    }
    
    public String getNameInfo() {
        return this.name_info;
    }
    
    public void setNameInfo(String name_info) {
        this.name_info = name_info;
    }
    
    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }
    
    public String getHierarchy() {
        return this.hierarchy;
    }
    
    public void setHierarchyClean(String hierarchy_clean) {
        this.hierarchy_clean = hierarchy_clean;
    }
    
    public String getHierarchyClean() {
        return this.hierarchy_clean;
    }
    
    public void addOtherInfo(TaxonomyOtherInfo otherInfo) {
        this.other_infos.add(otherInfo);
    }
    
    public List<TaxonomyOtherInfo> getOtherInfos() {
        return this.other_infos;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element nomenclature = doc.createElement("nomenclature");
        parent.appendChild(nomenclature);
        
        Element name = doc.createElement("name");
        nomenclature.appendChild(name);
        
        name.setTextContent(this.name);
        
        if(this.authority != null && !this.authority.trim().equals("")) {
            Element authority = doc.createElement("authority");
            nomenclature.appendChild(authority);
            
            authority.setTextContent(this.authority);
        }
        
        Element rank = doc.createElement("rank");
        nomenclature.appendChild(rank);
        
        rank.setTextContent(this.rank);
        
        if(this.common_name != null && !this.common_name.trim().equals("")) {
            Element common_name = doc.createElement("common_name");
            nomenclature.appendChild(common_name);

            common_name.setTextContent(this.common_name);
        }
        
        Element hierarchy = doc.createElement("taxon_hierarchy");
        nomenclature.appendChild(hierarchy);
        
        hierarchy.setTextContent(this.hierarchy);
        
        Element hierarchy_clean = doc.createElement("taxon_hierarchy_clean");
        nomenclature.appendChild(hierarchy_clean);
        
        hierarchy_clean.setTextContent(this.hierarchy_clean);
        
        Element name_info = doc.createElement("name_info");
        nomenclature.appendChild(name_info);
        
        name_info.setTextContent(this.name_info);
        
        if(this.other_infos != null) {
            for(TaxonomyOtherInfo other_info : this.other_infos) {
                other_info.toXML(doc, nomenclature);
            }
        }
    }
}
