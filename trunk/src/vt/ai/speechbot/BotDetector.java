/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot;


/**
 *
 * @author taras
 */
public abstract class BotDetector {
    public static final double TRUSTED=75;
    public static final double BOT=10;
    public static final double NEUTRAL=50;
    public static final double SUSPICIOUS=30;
    static private boolean has3EqualRemark(SpeechState state) {
        String speech[]=state.getRemarks();
        for (int i = 0; i < speech.length; i++) {
            String string = speech[i];
            for (int j = i+1; j < speech.length; j++) {
                String string1 = speech[j];
                if (string.equals(string1)) {
                    for (int k = j+1; k < speech.length; k++) {
                        String string12 = speech[k];
                        if (string.equals(string12)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    static public double RateInterlocutorIsHuman(SpeechState state) {
        if (state.getRemarkCount()<=8) {
            return NEUTRAL;
        }        
        double speed=state.getAverageSymbolsPerMinuteSpeedOfInterlocutor();
        if (speed>160) 
            return BOT;
        if (has3EqualRemark(state)) 
            return BOT;
        if (state.getRemarkCount()>50) 
            return SUSPICIOUS;
        return TRUSTED;
    }
}
