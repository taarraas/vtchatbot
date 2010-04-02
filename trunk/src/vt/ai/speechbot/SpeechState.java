/*
 * SpeechBotState.java
 *
 * Created on 23  2008, 23:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot;

import java.util.Vector;

/**
 *
 * @author taras
 */
public class SpeechState {
    public String convertToMeta(String remark) {
        return convertToMeta(this, remark);
    }
    public String convertFromMeta(String remark) {
        return convertFromMeta(this, remark);
    }
    static public String convertToMeta(SpeechState state, String remark) {        
        String ret=remark.replace(state.getInterlocutorNick(), "%INTERLOCUTOR%")
                .replace(state.getYourNick(), "%YOU%");
        return ret;
        
    }
    static public String convertToMeta(String remark, String youNick, String interlocutorNick) {        
        String ret=remark.replace(interlocutorNick, "%INTERLOCUTOR%")
                .replace(youNick, "%YOU%");
        return ret;
        
    }
    static public String convertFromMeta(SpeechState state, String remark) {
        if (remark==null) return null;
        String ret=remark.replace("%INTERLOCUTOR%", state.getInterlocutorNick())
                .replace("%YOU%", state.getYourNick());
        //System.out.println("fromMeta:"+remark+" >> "+ ret);
        return ret;        
    }    
    public static final String BEFORE_FIRST_REMARK="BEFORE_FIRST_REMARK";
    private Vector<String> speech=new Vector<String>();
    private Vector<Long> speechTimes=new Vector<Long>();
    private boolean isBotInitiator;
    private String yourNick, interlocutorNick;
    public String[] getRemarks() {
        return speech.toArray(new String[speech.size()]);
    }
    public SpeechState(String yourNick, String interlocutorNick) {
        setYourNick(yourNick);
        setInterlocutorNick(interlocutorNick);
    }
    public void setYourNick(String nick) {
        yourNick=nick;
    }
    public String getYourNick() {
        return yourNick;
    }
    public void setInterlocutorNick(String nick) {
        interlocutorNick=nick;
    }
    public String getInterlocutorNick() {
        return interlocutorNick;
    }
    private String remarkByNumber(int num) {
        if (num>=speech.size()) {
            return null;
        } else {
            if (num>0 || (num==0 && !speech.isEmpty())) 
                return speech.get(num);
            else
                return BEFORE_FIRST_REMARK;
        }
    }
    public double getAverageSymbolsPerMinuteSpeedOfInterlocutor() {
        int symbols=0;
        long time=0;
        int cur=isBotInitiator?3:2;
        for (; cur < getRemarkCount(); cur+=2) {
            symbols+=speech.elementAt(cur).length();
            time+=speechTimes.elementAt(cur)-speechTimes.elementAt(cur-1);
        }        
        return (double)symbols*60000/time;
    }
    public String getLastRemark() {
        return remarkByNumber(getRemarkCount()-1);
    }    
    public String getNextToLastRemark() {
        return remarkByNumber(getRemarkCount()-2);
    }
    public boolean contains(String remark) {
        if (remark==null) {
            return false;
        }
        if (speech.contains(remark))
            return true;
        for (String string : speech) {
            if (string.contains(remark)) return true;
        }
        return false;
    }
    public String getLastInterlocutorRemark() {
        if (isLastBot())
            return getNextToLastRemark();
        else
            return getLastRemark();
    }
    public String getLastBotRemark() {
        if (!isLastBot()) 
            return getNextToLastRemark();
        else
            return getLastRemark();
    }
    private boolean isLastBot() {
        return isBotInitiator?getRemarkCount()%2==1:getRemarkCount()%2==0;
    }
    
    /** if there few remark, write it in one remark */
    public void say(String remark, boolean isBot) {
        if (getRemarkCount()==0) {
            isBotInitiator=isBot;
        }
        if (isLastBot()==isBot) {
            speechTimes.set(speech.size()-1, System.currentTimeMillis());
            if (isBot) 
                speech.set(speech.size()-1, speech.lastElement()+"\n"+remark);
            else 
                speech.set(speech.size()-1, speech.lastElement()+"\n"+remark);
        } else {
            speechTimes.add(System.currentTimeMillis());
            speech.add(remark);
        }
    }
    public void botSay(String remark) {
        say(remark, true);
    }
    public void interlocutorSay(String remark) {
        say(remark, false);
    }
    public int getRemarkCount() {
        return speech.size();
    }
    public String[] getPartsOfDialog(int remarksInPart, int partNumber) {
        String ret[]=new String[remarksInPart];
        int coef=isBotInitiator?1:0;
        for (int i=0; i<remarksInPart; i++) {
            ret[remarksInPart-i-1]=remarkByNumber(partNumber*2-i+coef);
        }
        return ret;
    }
    public int getCompletePairCount() {
        return isBotInitiator?getRemarkCount()/2:(getRemarkCount()+1)/2;
    }   
    public static void main(String argv[]) {
        SpeechState st=new SpeechState("Bot", "Interl");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");
        
        st.botSay("������");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");

        st.interlocutorSay("��");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");

        st.interlocutorSay("��");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");

        st.botSay("�");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");
        
        st.botSay("��");
        System.out.println("bot:"+st.getLastBotRemark());
        System.out.println("int:"+st.getLastInterlocutorRemark());
        System.out.println("count:"+st.getRemarkCount());
        System.out.println("pbot:"+st.getPartsOfDialog(2, 0)[0]);
        System.out.println("pint:"+st.getPartsOfDialog(2, 0)[1]);
        System.out.println("---------------------------");
    }    
}