/*
 * DialogParser.java
 *
 * Created on 27  2008, 15:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.linguistics.UniversalDialogParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import vt.ai.speechbot.SpeechState;
/**
 *
 * @author Taras
 */
public class DialogParser {
    Vector<String> rem=new Vector<String>(),
            remnick=new Vector<String>();
    
    private void deleteBracketsInRemnick() {
        for (int i = 0; i < remnick.size(); i++) {
            String cur=remnick.get(i);
            cur=cur.substring(1, cur.length()-1);
            remnick.set(i, cur);
        }
    }
    private static boolean isInBrackets(String nick1, String nick2) {
        if (nick1.length()<2 || nick2.length()<2) return false;
        char ch11=nick1.charAt(0),
             ch12=nick1.charAt(nick1.length()-1),
             ch21=nick2.charAt(0),
             ch22=nick2.charAt(nick2.length()-1);
                
        if (ch11!=ch21 || ch12!=ch22) return false;
        if (ch11=='<' && ch12=='>') return true;
        if (ch11=='\"' && ch12==ch11) return true;
        return false;
    }
    /**
     * parse in form:
     * {?time? ?nick? remark}
     */
    private boolean parseType1(String text, int skipFirst) {
        rem.clear();
        remnick.clear();
        String txt[]=text.split("\\n");
        Set<String> nicks=new TreeSet<String>();
        for (int i=skipFirst; i<txt.length; i++) {
            String elem=txt[i];
            String cur[]=elem.split("\\s+");
            if (cur.length<2 || !TimeParser.isTime(cur[0])) return false;
            nicks.add(cur[1]);
            String remark=elem.replaceFirst("\\S+\\s+\\S+\\s","");
            rem.add(remark);
            remnick.add(cur[1]);
        }
        if (nicks.size()!=2) return false;
        String nick[]=nicks.toArray(new String[0]);
        if (isInBrackets(nick[0], nick[1])) deleteBracketsInRemnick();
        return true;
    }
    
    /**
     * parse in form:
     * {?nick? ?time? remark}
     */
    private boolean parseType2(String text, int skipFirst) {
        rem.clear();
        remnick.clear();
        String txt[]=text.split("\\n");
        Set<String> nicks=new TreeSet<String>();
        for (int i=skipFirst; i<txt.length; i++) {
            String elem=txt[i];
            String cur[]=elem.split("\\s+");
            if (cur.length<2 || !TimeParser.isTime(cur[1])) return false;
            nicks.add(cur[0]);
            String remark=elem.replaceFirst("\\S+\\s+\\S+\\s","");
            rem.add(remark);
            remnick.add(cur[0]);
        }
        if (nicks.size()!=2) return false;
        String nick[]=nicks.toArray(new String[0]);
        if (isInBrackets(nick[0], nick[1])) deleteBracketsInRemnick();
        return true;
    }
    private String interlocutor(String nick) {
        if (remnick.elementAt(0).equals(nick)) 
            return remnick.elementAt(1);
        else 
            return remnick.elementAt(0);
    }
    public Vector<String> afterParse() {
        Vector<String> ret=new Vector<String>();
        String lastNick=null, curRemark=null;        
        for (int i = 0; i < rem.size(); i++) {
            if (remnick.elementAt(i).equals(lastNick)){
                curRemark+="\n"+rem.elementAt(i);
            } else {
                if (lastNick!=null) {
                    //ret.add(lastNick+"\t"+curRemark);
                    ret.add(SpeechState.convertToMeta(curRemark, lastNick, interlocutor(lastNick)));  
                }
                lastNick=remnick.elementAt(i);
                curRemark=rem.elementAt(i);                
            }
        }
        //ret.add(lastNick+"\t"+curRemark);
        ret.add(curRemark);  
        return ret;
    }
    private Vector<String> parseType3(String text, int skipFirst) {
        String txt[]=text.split("\\n");
        Vector<String> res=new Vector<String>();
        boolean isFirst=true;
        for (int i = skipFirst; i < txt.length; i++) {
            String elem=txt[i];
            if (!elem.matches("\\ *\\-")) return null;
            res.add(elem.replaceFirst("\\ *\\-", ""));
        }
        return res;
    }
    /**
     * parse in form:
     * {?nick?remark}
     */
    private boolean parseType4(String text, int skipFirst) {
        rem.clear();
        remnick.clear();
        String txt[]=text.split("\\n");
        Set<String> nicks=new TreeSet<String>();
        for (int i=skipFirst; i<txt.length; i++) {
            String elem=txt[i];
            String cur[]=elem.split("\\s+");
            if (cur.length==0) return false;
            nicks.add(cur[0]);
            String remark=elem.replaceFirst("\\S+\\s+","");
            rem.add(remark);
            remnick.add(cur[0]);
        }
        if (nicks.size()>2) return false;
        String nick[]=nicks.toArray(new String[0]);
        if (nick.length==2 && isInBrackets(nick[0], nick[1])) deleteBracketsInRemnick();
        if (nick.length==1 && isInBrackets(nick[0], nick[0])) deleteBracketsInRemnick();
        return true;
    }    
    
    public Vector<String> parse(String text) {
        Vector<String> tmp;        
        if (parseType1(text, 0)) return afterParse();
        if (parseType2(text, 0)) return afterParse();
        if (parseType4(text, 0)) return afterParse();
        if ((tmp=parseType3(text, 0))!=null) return tmp;
        if (parseType1(text, 1)) return afterParse();
        if (parseType2(text, 1)) return afterParse();
        if (parseType4(text, 1)) return afterParse();
        if ((tmp=parseType3(text, 1))!=null) return tmp;
        return null;
    }
    public Vector<String> parseFromFile(String filename) throws IOException{
        FileReader fr=new FileReader(filename);
        char buf[]=new char[65000];
        int cnt=fr.read(buf);
        if (cnt==-1) {
            System.out.println("File:"+filename+" is empty");
            return null;
        }
        String txt=String.valueOf(buf, 0, cnt);
        return parse(txt);
        
    }
    public static void main(String argv[]) throws IOException{
        DialogParser dp=new DialogParser();
        Vector<String> val=dp.parseFromFile("./TeachLogs/bash291.log");
        if (val==null) return;
        for (String elem : val) {
            System.out.println(elem);
        }
    }
}
