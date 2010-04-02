/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import vt.ai.speechbot.Agent;
import vt.ai.speechbot.SpeechState;

/**
 *
 * @author taras
 */
public class Task extends Thread {
    SpeechBot speechBot;
    boolean isKilled=false;
    public Task(SpeechBot speechBot) {
        super();
        this.speechBot=speechBot;
    }
    private String calcAnswer(SpeechState state) {
        String answer;
        if ((answer=speechBot.selftraining.getAnswer(state))==null) {
           answer=speechBot.dumb.getAnswer(state);
           if (answer==null) {
               answer=speechBot.preprogrammed.getAnswer(state);
           }
        }
        return answer;
    }
    SpeechState state;
    public void getAnswer(Agent agent) {
        System.out.println("start get answer for user"+agent.getBotState().getInterlocutorNick());
        this.state=agent.getBotState();
        start();
    }
    public void kill() {
        isKilled=true;
        speechBot.selftraining.stopCalcingRevalentInfo();
        interrupt();
    }
    public void run() {
        String answer=calcAnswer(state);
        if (isKilled) return;
        while ((state=speechBot.getTaskManager().taskComlete(answer))!=null) {
            answer=calcAnswer(state);
            if (isKilled) return;
        }
    }
}
