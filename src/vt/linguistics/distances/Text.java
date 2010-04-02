/*
 * RemarkComparator.java
 *
 * Created on 24  2008, 22:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.linguistics.distances;

import java.util.Set;
import java.util.TreeSet;
import vt.linguistics.distances.Word;

/**
 *
 * @author taras
 */
public class Text {
    
    /** Creates a new instance of RemarkComparator */
    public static final double minimumCompareLevel=0.35, 
            similarCompareLevel=0.9;
    public Text() {
    }
    private static double questionmarkCoef(String first, String second) {
        boolean ans1=first.indexOf('?')!=-1,
        ans2=second.indexOf('?')!=-1;
        if (ans1 && ans2) return 0.2; 
        else if (!ans1 && !ans2) return 0.1;
             else return -0.1;
    }
    
    private static Set<String> arrayToSet(String[] a) {
        Set<String> ret=new TreeSet<String>();
        for (String string : a) {
            ret.add(string);
        }
        return ret;
    }
    private static double wordSetCrossing(Set<String> a, Set<String> b) {
        double sum=0;
        for (String elema : a) {
            double max=0;
            String elemMax=null;
            for (String elemb : b) {
                double tmp;
                if ((tmp=Word.compare(elema, elemb))>max) {
                    max=tmp;
                    elemMax=elemb;
                }                
            }
            if (elemMax!=null) {
                b.remove(elemMax);
                sum+=max;
            }
        }        
        return sum/a.size();
    }
    public static double countWordsCoef(int aa, int bb) {
        int a=Math.min(aa, bb),                
            b=Math.max(aa, bb);
        if (b==0) return 0;
        return a/b;
    }
    public static double compareRemarks(String first, String second) {
        String a[]=first.split("(\\s|,|\\.|!|\\?)+"),
                b[]=second.split("(\\s|,|\\.|!|\\?)+");        
        double ret=0;
        //ret+=questionmarkCoef(first, second);        
        ret+=wordSetCrossing(arrayToSet(a), arrayToSet(b))/2;
        ret+=wordSetCrossing(arrayToSet(b), arrayToSet(a))/2;
        ret*=countWordsCoef(a.length, b.length);
        return ret;
    }
    public static double comparePairs(double nextToLastRevalent, double lastRevalent) {
        return (nextToLastRevalent*0.7+1)*lastRevalent;
    }
    public static double comparePairs(String nextToLastDB, String nextToLast, String lastDB, String last) {
        return comparePairs(compareRemarks(nextToLastDB, nextToLast), compareRemarks(lastDB, last));
    }
    public static void main(String argv[]) {
        String test[]={"� ������ �� ������?",
            "������ �� ����", 
            "������!     ��� ����?", 
            "������. ��� ������",
            "�� ���?",
            "��� ����������?",
            "������",
            "���??"};
        for (int i = 0; i < test.length; i++) {
            for (int j = i; j < test.length; j++) {
                System.out.println(test[i]+":-=vs=-:"+test[j]+":== "+compareRemarks(test[i], test[j]));
            }
        }
    }                
}
