/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author taras
 */
public class Word implements Distance{
    public static boolean isLower(char ch) {
        if ((ch>='a' && ch<='z')||(ch>='а' && ch<='я')) 
            return true;
        else 
            return false;
    }
    private static int min(int a, int b) {
        return a<b?a:b;
    }
    private static double caseCompare(String a, String b) {
        if (a.compareToIgnoreCase(b)!=0) return 0;
        if (a.equals(b)) return 1;
        boolean isAllBadCase=true,
                isAInUp=false, isAInLo=false;
        int combcC=0, combcc=0, combCc=0, combCC=0;
        for (int i = 0; i < a.length(); i++) {
            char cha=a.charAt(i),
                    chb=b.charAt(i);
            if (isLower(cha)==isLower(chb)) isAllBadCase=false;
            if (isLower(cha)) isAInLo=true;
            else isAInUp=true;
            if (isLower(cha)) {
                if (isLower(chb)) 
                    combcc++;
                else 
                    combcC++;
            } else {
                if (isLower(chb)) 
                    combCc++;
                else 
                    combCC++;
            }
        }
        assert combcc+combcC+combCc+combCC==a.length();
        //just caps lock on
        if (isAInLo&&isAInUp) {
            if (isAllBadCase) return 1;
        }
        if (combCc!=0) return 1./min(combCc,a.length()-combCc+1);
        if (!isAInUp) {
            return (double)combcc/a.length()+
                    (double)combcC/a.length()*0.8;
        } else {
            return (double)combCC/a.length();
        }
    }    

    private static double asNearLetters(String aa, String bb) {        
        double ret=0;
        String a=aa.toLowerCase(),
                b=bb.toLowerCase();
        Map<String, Double> map=new TreeMap<String, Double>();
        map.put("ао", 0.8);
        map.put("еи", 0.8);
        map.put("её", 1.);        
        map.put("дт", 0.3);
        map.put("жш", 0.1);
        map.put("бп", 0.3);
        map.put("зс", 0.5);
        map.put("шщ", 0.9);
        
        for (int i = 0; i < a.length(); i++) {
            char cha=a.charAt(i),
                    chb=b.charAt(i);
            if (cha==chb) {
                ret+=1./a.length();
                continue;
            }
            Double tmp;
            if ((tmp=map.get(String.valueOf(cha)+chb))!=null) {
                ret+=tmp/a.length();
                continue;
            }
            if ((tmp=map.get(String.valueOf(chb)+cha))!=null) {
                ret+=tmp/a.length();
                continue;
            }
            ret+=-1./a.length();
        }
        if (ret<0) return 0;
        return ret*0.9;
    }
    
    /**
     * а длиннее чем b на 1 символ
     * @param a
     * @param b
     * @return
     */
    private static double asLostChars(String a, String b) {
        if (b.length()==0) return 0;
        int i=0;
        while (i<b.length() && a.charAt(i)==b.charAt(i)) i++;
        String aModified;
        if (i==b.length()) {
            aModified=a.substring(0, i);
        } else {
            aModified=a.substring(0, i)+a.substring(i+1);
        }
        
        return caseCompare(aModified, b)*(1-1./a.length());
    }
    /**
     * Сравнивает слово a с b
     * Порядок имеет разницу!
     * @param a - слово, которое сравниваем
     * @param b - слово, с котороым сравниваем
     * @return  value >=0 and <=1, 1 - equal
     */
    public static double compare(String a, String b) {
        if (a.compareToIgnoreCase(b)==0) 
            return caseCompare(a, b);                    
        if (a.length()==b.length()) return asNearLetters(a, b);
        if (Math.abs(a.length()-b.length())<=1) {
            if (a.length()<b.length()) 
                return asLostChars(b, a);
            else 
                return asLostChars(a, b);
        }
        return 0;
    }
    public static void main(String argv[]) {
        String test[]={"НЕ","не","бьяка","бяка","бяко","бяка","", "б", "", "бякк", "ябка", "БяКа", "бяКа", "БЯкА"};
        for (int i = 0; i < test.length; i++) {
            String string1 = test[i];
            System.out.println(string1);
            for (int j = 0; j < test.length; j++) {
                String string2 = test[j];
                System.out.println("   >>"+compare(string1, string2)+":"+string2);
            }
        }
    }

    @Override
    public double getDistance(String a, String b) {
       return 1./(compare(a, b)+1) - 1;
    }

    @Override
    public double getMaxDistance(String a, String b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}