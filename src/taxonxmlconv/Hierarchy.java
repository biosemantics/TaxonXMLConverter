/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author iychoi
 */
public class Hierarchy {
    
    private List<HierarchyEntry> entries;
    
    public Hierarchy() {
        this.entries = new ArrayList<HierarchyEntry>();
    }
    
    public List<HierarchyEntry> getEntries() {
        return this.entries;
    }
    
    public void addEntry(String[] names, String[] ranks, String[] authorities) {
        HierarchyEntry entry = new HierarchyEntry(names, ranks, authorities);
        this.entries.add(entry);
    }
    
    public HierarchyEntry findParent(HierarchyEntry entry) throws IOException {
        int maxPoint = 0;
        HierarchyEntry parentEntry = null;
        for(HierarchyEntry entry_parent : this.entries) {
            String[] entry_names = entry_parent.getNames();
            int point = 0;
            
            if(Rank.compareRanks(entry_parent.getRanks()[entry_parent.getRanks().length - 1], entry.getRanks()[entry.getRanks().length - 1]) >= 0) {
                continue;
            }
            
            for(int i=0;i<entry_names.length;i++) {
                String entry_name = entry_names[entry_names.length - 1 - i];
                for(String name_part : entry.getNames()) {
                    if(entry_name.equalsIgnoreCase(name_part)) {
                        // find same part
                        point++;
                    }
                }
            }
            
            if(point >= maxPoint) {
                maxPoint = point;
                parentEntry = entry_parent;
            }
        }
        return parentEntry;
    }
    
    public HierarchyEntry getCompleteHierarchy(HierarchyEntry entry) throws IOException {
        HierarchyEntry parentEntry = findParent(entry);
        
        if(parentEntry == null) {
            System.out.println("No Parent");
            return entry;
        } else {
            // concat
            System.out.println("Found Parent");
            System.out.println("parent - " + parentEntry.toString());
            String[] parentNames = parentEntry.getNames();
            String[] parentRanks = parentEntry.getRanks();
            String[] parentAuthorities = parentEntry.getAuthorities();
            
            int pos = parentNames.length;
            for(int i=0;i<parentNames.length;i++) {
                if(parentNames[i].equalsIgnoreCase(entry.getNames()[0])) {
                    pos = i;
                    break;
                }
            }
            
            String[] newNameParts = new String[pos + entry.getNames().length];
            String[] newRankParts = new String[pos + entry.getNames().length];
            String[] newAuthorityParts = new String[pos + entry.getNames().length];
            
            for(int i=0;i<pos+entry.getNames().length;i++) {
                if(i < pos) {
                    newNameParts[i] = parentNames[i];
                    newRankParts[i] = parentRanks[i];
                    newAuthorityParts[i] = parentAuthorities[i];
                } else {
                    newNameParts[i] = entry.getNames()[i-pos];
                    newRankParts[i] = entry.getRanks()[i-pos];
                    if(entry.getAuthorities()[i-pos] == null && i < parentAuthorities.length) {
                        newAuthorityParts[i] = parentAuthorities[i];
                    } else {
                        newAuthorityParts[i] = entry.getAuthorities()[i-pos];
                    }
                }
            }
            
            return new HierarchyEntry(newNameParts, newRankParts, newAuthorityParts);
        }
    }
    
    public HierarchyEntry getCompleteHierarchyNCA(HierarchyEntry entry) throws IOException {
        HierarchyEntry parentEntry = findParent(entry);
        
        if(parentEntry == null) {
            System.out.println("No Parent");
            return entry;
        } else {
            // concat
            System.out.println("Found Parent");
            System.out.println("parent - " + parentEntry.toString());
            String[] parentNames = parentEntry.getNames();
            String[] parentRanks = parentEntry.getRanks();
            String[] parentAuthorities = parentEntry.getAuthorities();
            
            int pos = parentNames.length;
            for(int i=0;i<parentNames.length;i++) {
                if(parentNames[i].equalsIgnoreCase(entry.getNames()[0])) {
                    pos = i;
                    break;
                }
            }
            
            String[] newNameParts = new String[pos + entry.getNames().length];
            String[] newRankParts = new String[pos + entry.getNames().length];
            String[] newAuthorityParts = new String[pos + entry.getNames().length];
            
            for(int i=0;i<pos+entry.getNames().length;i++) {
                if(i < pos) {
                    newNameParts[i] = parentNames[i];
                    newRankParts[i] = parentRanks[i];
                    //newAuthorityParts[i] = parentAuthorities[i];
                } else {
                    newNameParts[i] = entry.getNames()[i-pos];
                    newRankParts[i] = entry.getRanks()[i-pos];
                    if(entry.getAuthorities()[i-pos] == null && i < parentAuthorities.length) {
                        //newAuthorityParts[i] = parentAuthorities[i];
                    } else {
                        newAuthorityParts[i] = entry.getAuthorities()[i-pos];
                    }
                }
            }
            
            return new HierarchyEntry(newNameParts, newRankParts, newAuthorityParts);
        }
    }

    public void addEntry(HierarchyEntry entry) {
        this.entries.add(entry);
    }
}
class HierarchyEntry {
    private String[] names;
    private String[] ranks;
    private String[] authorities;
    
    public HierarchyEntry(String[] names, String[] ranks, String[] authorities) {
        this.names = names;
        this.ranks = ranks;
        this.authorities = authorities;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public String[] getRanks() {
        return this.ranks;
    }
    
    public String[] getAuthorities() {
        return this.authorities;
    }
    
    public String toString() {
        String newHierarchy = "";
        for(int i=0;i<names.length;i++) {
            if (!newHierarchy.equals("")) {
                newHierarchy += "; ";
            }
            newHierarchy += ranks[i] + " " + names[i];
        }
        return newHierarchy;
    }
    
    public String getHierarchyString() {
        String newHierarchy = "";
        for(int i=0;i<names.length;i++) {
            if (!newHierarchy.equals("")) {
                newHierarchy += "; ";
            }
            newHierarchy += ranks[i] + " " + names[i];
        }
        return newHierarchy;
    }
}