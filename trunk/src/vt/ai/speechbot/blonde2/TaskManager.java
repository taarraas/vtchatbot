/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import vt.ai.speechbot.Agent;
import vt.ai.speechbot.SpeechState;

/**
 *
 * @author taras
 */
public class TaskManager {
    LinkedBlockingQueue<Agent> queue=new LinkedBlockingQueue<Agent>();
    Map<Agent, SpeechState> speechs=new TreeMap<Agent, SpeechState>();
    private SpeechBot spbot;
    private Agent nowCalculating=null;
    private Task task;
    public TaskManager(SpeechBot spbot) {
        this.spbot=spbot;
        task=new Task(spbot);
    }
    public SpeechState taskComlete(String answer) {
        assert nowCalculating!=null;
        System.out.println("task comppleted "+nowCalculating.getBotState().getInterlocutorNick());
        System.out.println("remark:"+answer);
        nowCalculating.answerComputed(answer);
        nowCalculating=queue.poll();
        if (nowCalculating!=null) {
            System.out.println("next task "+nowCalculating.getBotState().getInterlocutorNick());
        } else {
            System.out.println("has no new task");
        }
        if (nowCalculating==null) return null;
        else return nowCalculating.getBotState();
    }
    public void startTask(Agent agent) {
        System.out.println("start task");
        task.kill();
        task=new Task(spbot);
        task.getAnswer(agent);
    }
    synchronized public void newTask(Agent agent) {
        if (nowCalculating!=null && nowCalculating.equals(agent))
            startTask(agent);
        else {
            if (!queue.contains(this)) queue.offer(agent);
            if (nowCalculating==null) {
                nowCalculating=queue.poll();
                startTask(nowCalculating);
            }
        }
    }
}
