/*
 * SpeechBotAgent.java
 *
 * Created on 23 ?????? 2008, 23:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author taras
 */
public class Agent extends Thread{
    
    /** Creates a new instance of SpeechBotAgent */
    private static final int waitingSeconds=600;
    SpeechState state;
    AgentManager botManager;
    boolean live=true;
    Queue<String> toSay =new LinkedList<String>();
    Queue<Integer> millisToSay=new LinkedList<Integer>();
    boolean isAnswerComputing=false;
    long timeOfStartingComputingAnswer;
    long millisOfAnswerComputing;
    public Agent(String yourNick, String interlocutorNick, AgentManager manager) {
        state=new SpeechState(yourNick, interlocutorNick);
        botManager=manager;
    }    
    public void start() {
        super.start();     
        serve();
    }
    public void kill() {
        live=false;
    }
    synchronized private void commit() {
            botManager.commitAgentSpeech(this);
    }
    synchronized private void commitIgnoreAndKill(String reason) {
        System.out.println(reason+" - commit, ignore and kill :"+state.getInterlocutorNick());
            botManager.ignoreUser(state.getInterlocutorNick());
            kill();
        
    }
    synchronized private void ignoreAndKill(String reason) {
        System.out.println(reason+" - ignore and kill :"+state.getInterlocutorNick());
            botManager.commitAgentSpeech(this);
            botManager.ignoreUser(state.getInterlocutorNick());
            kill();
        
    }
    synchronized private void offerSay(String remark) {
        String remarks[]=remark.split("\\n");
        for (String elem : remarks) {
            assert elem!=null && !elem.isEmpty();
            toSay.add(elem);            
        }
  //      System.out.println("remarks:"+remarks.length);
        if (toSay.size()==1) {
            int time=remark.length()*2000;
            time+=remark.length()<7?5000:0;
            time-=millisOfAnswerComputing;
            time=Math.max(time, 0);
            millisToSay.add(Integer.valueOf(time));
        } else {
            for (String string:toSay) {
                int time=string.length()*1000;
                millisToSay.add(Integer.valueOf(time));
            }
        }
    }
    synchronized public void cancelAllRemarks() {
        millisToSay.clear();
        toSay.clear();
    }    
    private void discreteSleep() throws InterruptedException{
        long cur=System.currentTimeMillis(), tmp;
        
        sleep(tmp=millisToSay.poll());
        if (Math.abs(tmp-(System.currentTimeMillis()-cur))>10000) {
            System.out.println("Too many time in sleep. Agent :"+state.getInterlocutorNick());
            System.out.println("Must be:"+tmp+" was:"+(System.currentTimeMillis()-cur));
        }
        /*
        while (alreadyTimePassed+1000<millisToSay.peek()) {
            sleep(1000);
            if (!live) return;
            alreadyTimePassed+=1000;
        }
        millisToSay.poll();
        alreadyTimePassed=0;*/
    }
    private boolean waitTimeout() {
        try {
                int cyclesProcessed=0;
                while (millisToSay.isEmpty()) {
                    cyclesProcessed++;
                    sleep(1000);
                    if (cyclesProcessed==waitingSeconds) {
                        commitIgnoreAndKill("Time out of waiting for answer");
                        return true;
                    }
                }
        } catch (java.lang.InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void run() {
        while (live) {
            try {
                if (waitTimeout()) return;
                discreteSleep();
                synchronized (this) {
                    System.out.println("Say:"+state.getInterlocutorNick());
                    if (!live) return;
                    assert(!toSay.isEmpty());
                    assert(toSay.peek()!=null);
                    botManager.sayTo(state.getInterlocutorNick(), toSay.peek());
                    state.botSay(toSay.poll());
                }
            } catch (Throwable thr) {
                thr.printStackTrace();
            }
        }
    }
    
    synchronized public void serve() {
        cancelAllRemarks();
        double rate=BotDetector.RateInterlocutorIsHuman(state);
        if (rate<=BotDetector.BOT) {
            ignoreAndKill("interlocutor is bot. rate="+rate);            
        } else {
            if (rate>=BotDetector.TRUSTED) commit();
            timeOfStartingComputingAnswer=System.currentTimeMillis();
            isAnswerComputing=true;
            botManager.getSpeechBot().getAnswer(this);
        }
    }

    synchronized public void interlocutorSaid(String remark) {
        state.interlocutorSay(remark);
        serve();
    }
    public SpeechState getBotState() {
        return state;
    }
    public void answerComputed(String remark) {
        if (remark==null) {
            commitIgnoreAndKill("Too long speech");
            return;
        }
        millisOfAnswerComputing=System.currentTimeMillis()-timeOfStartingComputingAnswer;
        //System.out.println("Time of computing answer:"+millisOfAnswerComputing+" ms");
        isAnswerComputing=false;
        offerSay(remark);
    }
}
