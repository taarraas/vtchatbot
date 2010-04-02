/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author taras
 */
public class WordsSemantic {

    /**
     * @param args the command line arguments
     */ 
    private static Set<Synset> add(Set<Synset> a, Synset[] b) {
        for (Synset synset : b) {
            a.add(synset);
        }
        return a;
    }
    private static Synset[] get(NounSynset synset) {
        Set<Synset> ret=new HashSet<Synset>();
        ret=add(ret, synset.getHypernyms());
        ret=add(ret, synset.getHyponyms());
        ret=add(ret, synset.getInstanceHypernyms());
        ret=add(ret, synset.getInstanceHyponyms());
        ret=add(ret, synset.getMemberHolonyms());
        ret=add(ret, synset.getMemberMeronyms());
        ret=add(ret, synset.getPartHolonyms());
        ret=add(ret, synset.getPartMeronyms());
        ret=add(ret, synset.getRegionMembers());
        ret=add(ret, synset.getUsages());
        ret=add(ret, synset.getUsageMembers());
        return ret.toArray(new Synset[0]);
    }
    private static Synset[] get(VerbSynset synset) {
        Synset[] ret=synset.getUsages();
        return ret;
    }
    private static Synset[] get(AdverbSynset synset) {
        Synset[] ret=synset.getUsages();
        return ret;
    }
    private static Synset[] get(AdjectiveSynset synset) {
        Synset[] ret=synset.getUsages();
        return ret;
    }
    private static Synset[] getNew(Synset a) {
        if (a.getType().equals(SynsetType.ADJECTIVE)) {
            return get((AdjectiveSynset)a);
        } 
        if (a.getType().equals(SynsetType.ADVERB)) {
            return get((AdverbSynset)a);
        } 
        if (a.getType().equals(SynsetType.VERB)) {
            return get((VerbSynset)a);
        } 
        if (a.getType().equals(SynsetType.NOUN)) {
            return get((NounSynset)a);
        } 
        return new Synset[0];
    }
   static  private Synset getFirstCrossing(Synset[] a, Synset[] b) {
        for (Synset synset : b) {
            if (synset==null) continue;
            for (Synset synset1 : a) {
                if (synset.equals(synset1)) return synset;
            }
        }
        return null;
    }
    
    Map<Synset, Integer> shortestDistances=new HashMap<Synset, Integer>();
    Map<Synset, Synset> fromMap=new HashMap<Synset, Synset>();
    private void setShortestDistance(Synset synset, int distance) {
        shortestDistances.put(synset, distance);
    }

    public int getShortestDistance(Synset synset) {
        Integer d = shortestDistances.get(synset);
        return (d == null) ? Integer.MAX_VALUE>>1 : d;
    }

    private final Comparator<Synset> shortestDistanceComparator = new Comparator<Synset>() {

        public int compare(Synset left, Synset right) {
            int shortestDistanceLeft = getShortestDistance(left);
            int shortestDistanceRight = getShortestDistance(right);

            if (shortestDistanceLeft > shortestDistanceRight) {
                return +1;
            } else if (shortestDistanceLeft < shortestDistanceRight) {
                return -1;
            } else // equal
            {
                return 0;
            }
        }
    };
    PriorityQueue<Synset> queue=new PriorityQueue<Synset>(100, shortestDistanceComparator);
    private void addDistances(Synset[] a, Synset cur) {
        for (Synset synset : a) {
            if (getShortestDistance(synset)>getShortestDistance(cur)+1) {
                setShortestDistance(synset, getShortestDistance(cur)+1);
                fromMap.put(synset, cur);
            }
        }
    }
    private Set<Synset> used=new HashSet<Synset>();
    private void addToQueue(Synset[] a) {
        for (Synset synset : a) {
            if (used.contains(synset)) continue;
            queue.add(synset);
            used.add(synset);
        }
    }
    Synset to[];
    public WordsSemantic(Synset[] from, Synset[] to) {
        this.to=to;
        for (Synset synset : from) {
            setShortestDistance(synset, 0);
        }
        addToQueue(from);
        solve();
    }
    public void solve() {
        while (!queue.isEmpty()) {
            Synset cur=queue.poll();
//            System.out.println("---------------"+cur.getDefinition()+ " "+getShortestDistance(cur));
            Synset n[]=getNew(cur);
            addDistances(n, cur);
            addToQueue(n);
            Synset tmp=getFirstCrossing(n, to);
            if (tmp!=null) {
                System.out.println("distance:"+getShortestDistance(cur));
                while (tmp!=null) {
                    System.out.println(tmp.getDefinition());
                    tmp=fromMap.get(tmp);
                }
                return;
            }
        }        
    }
    public static void main(String[] args) {
        
        // TODO code application logic here      
        WordNetDatabase wnDatabase=WordNetDatabase.getFileInstance();
        Synset from[]=wnDatabase.getSynsets("first");
        Synset to[]=wnDatabase.getSynsets("zero");
        if (from.length==0) {
            System.out.println("1 have no synsets");
            return;
        }
        if (to.length==0) {
            System.out.println("2 have no synsets");
            return;
        }
        WordsSemantic m=new WordsSemantic(from, to);
    }

}
