/*
 * SpeachBotManager.java
 *
 * Created on 23 ?????? 2008, 23:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.net.vtirclib.IRCActionListener;
import javax.net.vtirclib.IRCConnection;
import javax.net.vtirclib.UserName;
import vt.ai.speechbot.blondefinal.SpeechBot;

/**
 *
 * @author taras
 */
public class AgentManager extends Thread implements IRCActionListener {
    private static final int TIME_BETWEEN_AGENTS_STARTING=10000;
    private IRCConnection ircConnection;
    private Vector<Agent> agents = new Vector<Agent>();
    private Queue<String> addAgentQueue=new LinkedList<String>();
    private SpeechBot bot;
    private Set<String> ignoredAgents=new TreeSet<String>();
    private boolean live=true;
    public AgentManager(SpeechBot b, IRCConnection connection) {
        super();
        start();
        setActiveConnection(connection);
        bot=b;
    }
    public AgentManager(SpeechBot b) {
        super();
        start();
        bot=b;
    }
    public SpeechBot getSpeechBot() {
        return bot;
    }
    @Override
    public void run() {
        while (live) {
            try {
                sleep(TIME_BETWEEN_AGENTS_STARTING);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (!addAgentQueue.isEmpty()) {
                addAgentImmediately(addAgentQueue.poll());
            }
        }
    }
    public void setActiveConnection(IRCConnection conn) {
                ircConnection=conn;
    }
    public Agent agentByNick(String nick) {
        for (Agent elem : agents) {
            if (elem.getBotState().getInterlocutorNick().compareToIgnoreCase(nick)==0) {
                return elem;
            }
        }   
        return null;
    }
    public boolean containAgent(String nick) {
        return agentByNick(nick)!=null;
    }    
    public void ircNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice) {
        if (!isChannelMessage && !isYourMessage && !isNotice) {
            if (!containAgent(from)) {
                if (addAgentImmediately(from)==-1) return;
            }            
            agentByNick(from).interlocutorSaid(data);
            
        }
    }
    public void ircActionPerformed(int actionType, String where, String data) {
        if (where==null || !containAgent(where)) return;
        switch (actionType) {
            case NICK_CHANGED: {
                if (UserName.compare(where, ircConnection.getChanModel().getNick())==0) {
                    for (Agent agent : agents) {
                        agent.getBotState().setYourNick(data);
                    }
                }
                agentByNick(where).getBotState().setInterlocutorNick(data);
                break;
            }
            case USER_MODE_CHANGED: {
                if (data.equals("QUIT")) {
                    removeAgent(where, "user quit");
                }
                break;
            }
            case CONNECTION_TERMINATED: {
                kill();
            }
        }
    }
    synchronized private int addAgentImmediately(String nick) {
        if (ignoredAgents.contains(nick.toLowerCase())) return -1;
        if (containAgent(nick)) return 0;
        System.out.println("New agent created:"+nick);
        Agent tmp = new Agent(ircConnection.getChanModel().getNick(), nick, this);
        agents.add(tmp);
        tmp.start();
        return 1;
    }
    public void addAgent(String nick) {
        addAgentQueue.add(nick);
    }
    public void commitAllAgentSpeech(int minimumRemarks) {
        System.out.println("Auto commit");
        for (Agent agent : agents) {
            if (agent.getBotState().getRemarkCount()>minimumRemarks) {
                System.out.println("commit "+agent.getBotState().getInterlocutorNick());
                bot.commitAll(agent.getBotState());
            }
        }
    }
    public void commitAgentSpeech(String nick) {
        if (containAgent(nick)) {            
            commitAgentSpeech(agentByNick(nick));
        }        
    }
    public void commitAgentSpeech(Agent agent) {
       System.out.println("Agent commited:"+agent.getBotState().getInterlocutorNick());
       bot.commitAll(agent.getBotState());        
    }
    synchronized public void removeAgent(String nick, String reason) {    
        if (containAgent(nick)) {            
            double rate=BotDetector.RateInterlocutorIsHuman(agentByNick(nick).state);
            if (rate<=BotDetector.SUSPICIOUS) {
                if (rate<=BotDetector.BOT) {
                    bot.deleteFromDBState(agentByNick(nick).state);
                } else {
                    System.err.println("!Suspicious user:"+nick);
                }
            }
            System.out.println("Agent killed:"+nick);
            if (reason!=null) 
                System.out.println("Reason:"+reason);
            agentByNick(nick).kill();
            agents.remove(agentByNick(nick));
        }
    }
    synchronized public void removeAgent(String nick) {    
        removeAgent(nick, null);
    }
    public void ignoreUser(String nick) {
        ignoredAgents.add(nick.toLowerCase());
    }
    public void finalize() {
        kill();
    }
    public void kill() {
        live=false;
    }
    protected void sayTo(String nick, String data) {
        ircConnection.sendMessage(nick, data);
    }
}
