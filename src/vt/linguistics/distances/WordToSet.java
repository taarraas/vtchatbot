/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import vt.linguistics.rml.Lematizer;

/**
 *
 * @author taras
 */
public class WordToSet {
    Lematizer lematizer=Lematizer.getRussianLematizer();
    public WordToSet() {        
    }    
    private void addToSetLemmas(String lemmas, Collection<String> set) {
        StringBuffer sb=new StringBuffer();
        char cur;
        for (int i = 0; i < lemmas.length(); i++) {
            cur=lemmas.charAt(i);
            if (cur=='#') {
                set.add(sb.toString());
                sb = new StringBuffer();
            } else {
                sb.append(cur);

            }
        }
        /*String lem[]=lemmas.split("#");
        if (lemmas.isEmpty()) return;
        for (String string : lem) {
            set.add(string);
        } */
    }
    boolean isCapital(String word) {
        if (word.isEmpty()) return false;
        return Character.isTitleCase( word.charAt(0));
    }
    private boolean tryAdd(String word, Collection<String> set) {
        String ret=lematizer.getAllAncodesAndLemmasQuick(word, isCapital(word), true);
        if (ret.isEmpty()) {
            return false;
        } else {
            addToSetLemmas(ret, set);
            return true;
        }
    }
    private String[] getNearWord(String word) {
        // realize it!!!!
        return new String[0];
    }
    public Collection<String> getLemmas(String word) {
        Collection<String> ret=new Vector<String>();
        if (!tryAdd(word, ret)) {
            String[] nearWords=getNearWord(word);
            for (String string : nearWords) {
                tryAdd(string, ret);
            }
        }
        return ret;
    }
}
