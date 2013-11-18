/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonxmlconv;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author iychoi
 */
public class XmlFilenameComparator implements Comparator<String> {

    public XmlFilenameComparator() {
        
    }
    
    @Override
    public int compare(String t, String t1) {
        if(XmlFilenameComparator.hasIDPart(t) && XmlFilenameComparator.hasIDPart(t1)) {
            int id1 = XmlFilenameComparator.getID(t);
            int id2 = XmlFilenameComparator.getID(t1);
            return id1 - id2;
        } else {
            return t.compareTo(t1);
        }
    }
    
    public static boolean hasIDPart(String text) {
        File file = new File(text);
        Pattern p1 = Pattern.compile("^(\\d+)\\.\\s.+$");
        Matcher mt1 = p1.matcher(file.getName());
        if(mt1.matches()) {
            return true;
        }
        return false;
    }
    
    public static int getID(String text) {
        File file = new File(text);
        Pattern p1 = Pattern.compile("^(\\d+)\\.\\s?.+$");
        Matcher mt1 = p1.matcher(file.getName());
        if(mt1.matches()) {
            return Integer.parseInt(mt1.group(1));
        }
        return -1;
    }
    
}
