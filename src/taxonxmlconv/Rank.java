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
public class Rank {

    private static List<String> rankPredefined = new ArrayList<String>();
    private static List<RankRelation> rankRelationForTwoPartNames = new ArrayList<RankRelation>();
    private static List<RankRelation> rankRelationForThreePartNames = new ArrayList<RankRelation>();
    
    static {
        initializeRankPredefined();
    }

    private static void initializeRankPredefined() {
        if (rankPredefined.isEmpty()) {
            rankPredefined.add("Phylum");
            rankPredefined.add("Subphylum");
            rankPredefined.add("Class");
            rankPredefined.add("Subclass");
            rankPredefined.add("Order");
            rankPredefined.add("Suborder");
            rankPredefined.add("Superfamily");
            rankPredefined.add("Family");
            rankPredefined.add("Subfamily");
            rankPredefined.add("Tribe");
            rankPredefined.add("Subtribe");
            rankPredefined.add("Genus");
            rankPredefined.add("Subgenus");
            rankPredefined.add("Section");
            rankPredefined.add("Subsection");
            rankPredefined.add("Series");
            rankPredefined.add("Species");
            rankPredefined.add("Subspecies");
            rankPredefined.add("Variety");
        }
        
        if(rankRelationForTwoPartNames.isEmpty()) {
            rankRelationForTwoPartNames.add(new RankRelation("Phylum", "Class"));
            rankRelationForTwoPartNames.add(new RankRelation("Class", "Order"));
            rankRelationForTwoPartNames.add(new RankRelation("Order", "Family"));
            rankRelationForTwoPartNames.add(new RankRelation("Family", "Tribe"));
            rankRelationForTwoPartNames.add(new RankRelation("Tribe", "Genus"));
            rankRelationForTwoPartNames.add(new RankRelation("Genus", "Species"));
            rankRelationForTwoPartNames.add(new RankRelation("Genus", "Subgenus"));
        }
        
        if(rankRelationForThreePartNames.isEmpty()) {
            rankRelationForThreePartNames.add(new RankRelation("Phylum", "Class"));
            rankRelationForThreePartNames.add(new RankRelation("Class", "Order"));
            rankRelationForThreePartNames.add(new RankRelation("Order", "Family"));
            rankRelationForThreePartNames.add(new RankRelation("Family", "Tribe"));
            rankRelationForThreePartNames.add(new RankRelation("Tribe", "Genus"));
            rankRelationForThreePartNames.add(new RankRelation("Genus", "Subgenus"));
            rankRelationForThreePartNames.add(new RankRelation("Subgenus", "Species"));
            rankRelationForThreePartNames.add(new RankRelation("Species", "Subspecies"));
        }
    }

    public static String[] getPredefinedRanks() {
        String[] arr = new String[rankPredefined.size()];
        
        arr = rankPredefined.toArray(arr);
        return arr;
    }
    
    public static String findRank(String rank) throws IOException {
        for (String rankDefined : rankPredefined) {
            if (rankDefined.trim().equalsIgnoreCase(rank.trim())) {
                return rankDefined;
            }
        }

        // default
        throw new IOException("cannot find rank info");
    }
    
    public static String findChildRank(String rank, int nameParts) throws IOException {
        if(nameParts == 2) {
            for (RankRelation rr : rankRelationForTwoPartNames) {
                if(rr.getParentRank().equalsIgnoreCase(rank.trim())) {
                    return rr.getChildRank();
                }
            }
        } else if(nameParts == 3) {
            for (RankRelation rr : rankRelationForThreePartNames) {
                if(rr.getParentRank().equalsIgnoreCase(rank.trim())) {
                    return rr.getChildRank();
                }
            }
        }
        
        throw new IOException("cannot find rank info");
    }
    
    public static String findParentRank(String rank, int nameParts) throws IOException {
        if(nameParts == 2) {
            for (RankRelation rr : rankRelationForTwoPartNames) {
                if (rr.getChildRank().equalsIgnoreCase(rank.trim())) {
                    return rr.getParentRank();
                }
            }
        } else if(nameParts == 3) {
            for (RankRelation rr : rankRelationForThreePartNames) {
                if (rr.getChildRank().equalsIgnoreCase(rank.trim())) {
                    return rr.getParentRank();
                }
            }
        }
        
        throw new IOException("cannot find rank info");
    }
}

class RankRelation {
    private String parentRank;
    private String childRank;
    
    public RankRelation(String parentRank, String childRank) {
        this.parentRank = parentRank;
        this.childRank = childRank;
    }
    
    public String getParentRank() {
        return this.parentRank;
    }
    
    public String getChildRank() {
        return this.childRank;
    }
}
