/*
 * SpeechBot.java
 *
 * Created on 23  2008, 23:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot;

/**
 *
 * @author taras
 */
public abstract class AbstractSpeechBot {
    
    /** Creates a new instance of SpeechBot */
    //abstract public SpeechBot(String database);
    abstract public void commitAll(SpeechState state);    
    abstract public void deleteFromDBState(SpeechState state);
}
