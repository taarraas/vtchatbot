/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author taras
 */
public class WordDistances implements Distance{
    WordToSet wordToSet=new WordToSet();
    public WordFormDistance wordFormDistance=new WordFormDistance();

    public double lastMax;
    @Override
    public double getDistance(String a, String b) {
        Collection<String> sa=wordToSet.getLemmas(a);
        Collection<String> sb=wordToSet.getLemmas(b);
        double max=0;
        lastMax=0;
        for (String string : sb) {
            lastMax=Math.max(max, wordFormDistance.getMaxDistance(string, string));
        }
        for (String string : sa) {
            lastMax=Math.max(max, wordFormDistance.getMaxDistance(string, string));
            for (String string1 : sb) {
                max=Math.max(max, wordFormDistance.getDistance(string, string1));
                lastMax=Math.max(lastMax, wordFormDistance.getMaxDistance(string, string1));
            }
        }
        return max;
    }

    public double getMinMaxDistance(String a) {
        Collection<String> lem=wordToSet.getLemmas(a);
        double min=Double.MAX_VALUE;
        for (String string : lem) {
            min=Math.min(min, wordFormDistance.getMaxDistance(string));
        }
        return min;
    }

    @Override
    public double getMaxDistance(String a, String b) {
        getDistance(a, b);
        return lastMax;
    }
}
