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
    
    public void addEntry(String[] names, String[] ranks) {
        HierarchyEntry entry = new HierarchyEntry(names, ranks);
        this.entries.add(entry);
    }

    public HierarchyEntry getFullHierarchy(String[] name_parts, String[] rank_parts) throws IOException {
        int maxPoint = 0;
        HierarchyEntry parentEntry = null;
        for(HierarchyEntry entry : this.entries) {
            String[] entry_names = entry.getNames();
            int point = 0;
            
            if(Rank.compareRanks(entry.getRanks()[entry.getRanks().length - 1], rank_parts[rank_parts.length - 1]) >= 0) {
                continue;
            }
            
            for(int i=0;i<entry_names.length;i++) {
                String entry_name = entry_names[entry_names.length - 1 - i];
                for(String name_part : name_parts) {
                    if(entry_name.equalsIgnoreCase(name_part)) {
                        // find same part
                        point++;
                    }
                }
            }
            
            if(point >= maxPoint) {
                maxPoint = point;
                parentEntry = entry;
            }
        }
        
        if(parentEntry == null) {
            System.out.println("No Parent");
            return new HierarchyEntry(name_parts, rank_parts);
        } else {
            // concat
            System.out.println("Found Parent");
            System.out.println("parent - " + parentEntry.toString());
            String[] parentNames = parentEntry.getNames();
            String[] parentRanks = parentEntry.getRanks();
            
            int pos = parentNames.length;
            for(int i=0;i<parentNames.length;i++) {
                if(parentNames[i].equalsIgnoreCase(name_parts[0])) {
                    pos = i;
                    break;
                }
            }
            
            String[] newNameParts = new String[pos + name_parts.length];
            String[] newRankParts = new String[pos + rank_parts.length];
            
            for(int i=0;i<pos+name_parts.length;i++) {
                if(i < pos) {
                    newNameParts[i] = parentNames[i];
                    newRankParts[i] = parentRanks[i];
                } else {
                    newNameParts[i] = name_parts[i-pos];
                    newRankParts[i] = rank_parts[i-pos];
                }
            }
            
            return new HierarchyEntry(newNameParts, newRankParts);
        }
    }

    public void addEntry(HierarchyEntry entry) {
        this.entries.add(entry);
    }
}
class HierarchyEntry {
    private String[] names;
    private String[] ranks;
    
    public HierarchyEntry(String[] names, String[] ranks) {
        this.names = names;
        this.ranks = ranks;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public String[] getRanks() {
        return this.ranks;
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
}