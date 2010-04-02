/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.rml;

import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author taras
 */
public class Lematizer {
    static {
        //System.load("/usr/lib/librml2jni.so.1.0.3");
        //Lematizer.setRMLDir("/home/taras/projects/lematizer");
    }    
    public enum Language {Russian, English, German};
    public enum Subject {FINANCE, COMPUTER, LITERATURE};
    boolean loaded, maximalPrediction, useStatictic, allowRussianJo;
    Language curLanguage;
    native private int create(Language language);
    native private void destroy(Language language);
    private Lematizer(Language language) {
        curLanguage=language;
        create(language);
    }        
    public static Lematizer getRussianLematizer() {
        return new Lematizer(Language.Russian);
    }
    public static Lematizer getEnglishLematizer() {
        return new Lematizer(Language.English);
    }
    @Override
    public void finalize() {
        destroy(curLanguage);
    }
    static native public void setRMLDir(String directory);
    
    //main interface
    native public Vector<Paradigm> createParadigmCollection(String normalForm, boolean isCapital, boolean usePrediction);
    native public int _createParadigmFromID(int id);
    public Paradigm createParadigmFromID(int id) {
        return new Paradigm();
    }
//    native public String getAllAncodesQuick(Language language, String wordForm, boolean  isCapital, boolean usePrediction);
    native private String getAllAncodesAndLemmasQuick(Language language, String wordForm, boolean  isCapital, boolean usePrediction);
    public String getAllAncodesAndLemmasQuick(String wordForm, boolean  isCapital, boolean usePrediction) {
        return getAllAncodesAndLemmasQuick(curLanguage, wordForm, isCapital, usePrediction);
    }
    public String[] getAllAncodes(String word) {
        if (word.isEmpty()) return new String[0];
        TreeSet<String> ret=new TreeSet<String>();
        String anclem=getAllAncodesAndLemmasQuick(word,
                Character.isUpperCase(word.charAt(0)), false);
        if (anclem.isEmpty()) return new String[0];
        String tmp[]=anclem.split("\\#");
        for (String string : tmp) {
            String anc=(string.split("\\ "))[1];
            for (int i = 0; i < anc.length()/2; i++) {
                ret.add(anc.substring(i*2, i*2+2));
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
    native private String toAncode(Language lang, String word, String ancode);
    public String toAncode(String word, String ancode) {
        return toAncode(curLanguage, word, ancode); 
    }
    static public void main(String argv[]) throws IOException{
        Lematizer lem=new Lematizer(Language.English);
        System.out.println(lem.getAllAncodesAndLemmasQuick("going", false, false));
        System.out.println(lem.getAllAncodesAndLemmasQuick("you", false, false));
        System.out.println(lem.getAllAncodes("walked"));
        System.out.println(lem.toAncode("walk", "vb"));
        lem.finalize();
    }
}
