/*
 * TimeParser.java
 *
 * Created on 3 ������ 2008, 19:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.linguistics.UniversalDialogParser;

/**
 *
 * @author Taras
 */
public class TimeParser {
    
    /** Creates a new instance of TimeParser */
    static public boolean isTime(String concept) {
        if (concept.matches("\\d?\\d\\:\\d?\\d\\:\\d?\\d")) return true;
        return false;
    }
    static public int findTime(String remark) {
        String words[]=remark.split("\\s");
        for (int i = 0; i<words.length; i++) {
            if (isTime(words[i])) return i;
        }
        return -1;
    }      
}
