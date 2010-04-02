/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.synonyms;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.text.StyledEditorKit.ForegroundAction;

/**
 *
 * @author taras
 */
public class GetSynonyms {
    WordNetDatabase wnDatabase;
    public GetSynonyms() {
        wnDatabase=WordNetDatabase.getFileInstance();
    }

    private double quality[];
    public double[] getQuality() {
        return quality;
    }

    /**
     * Returns synonyms of input word sorted by qualities from best quality to lower
     * Arrays synonyms and quality have same length
     * qualities stored in getQuality() before next call of this function
     * @param word  - input word
     * @return  - synonyms for this word
     */
    public String[] getSynonyms(String word) {
        Set<String> syn=new TreeSet<String>();
        Map<String, Double> qual=new TreeMap<String, Double>();
        Synset[] syns=wnDatabase.getSynsets(word, SynsetType.ADVERB);
        for (Synset synset : syns) {
            System.out.println("-------------");
            System.out.println(synset.getDefinition());
            for (String string : synset.getWordForms()) {
                System.out.println(string);
                syn.add(string);
                qual.put(string, 0.);
            }
        }
        return syn.toArray(new String[syn.size()]);
    }
    public static void main(String av[]) {
        GetSynonyms gs=new GetSynonyms();
        String word="good";
        String out[]=gs.getSynonyms(word);
        for (String string : out) {
            System.out.println(string);
        }
    }
}
