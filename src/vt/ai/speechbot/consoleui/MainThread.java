/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.consoleui;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import javax.net.vtirclib.IRCConnection;
import javax.net.vtirclib.LogWriter;
import vt.ai.speechbot.AgentManager;
import vt.ai.speechbot.blondefinal.SpeechBot;

/**
 *
 * @author taras
 */
public class MainThread extends Thread{
    IRCConnection connection;
    int interval;
    AgentManager botManager;
    LogWriter lw;
    SpeechBot spBot=new SpeechBot();

    public MainThread(IRCConnection conn, int interval) {
        connection=conn;
        this.interval=interval; 
        lw=new LogWriter(connection.getLoggedServer());
        lw.setRAWDataLogging(true);
        botManager=new AgentManager(spBot, connection);
        connection.addIRCActionListener(lw);
        connection.addIRCActionListener(botManager);
        start();
    }
    
    public boolean addAgent() {
        String all[] = connection.getChanModel().getUsers().toArray(new String[0]);
        for (int i = 0; i < all.length; i++) {
            if (((SpeechBot)spBot).userInfo.getUserInfo(all[i])) continue;
            botManager.addAgent(all[i]);
            return true;
        }
        return false;
    }
    
    public void run() {             
        System.out.println("Started");
        System.out.println("Interval : "+interval);
        try {
            sleep(12000);
        } catch (Exception e) {
            e.printStackTrace();                    
        }
        if (interval==-1) {
            while (!connection.isDisconnected()) {
                try {
                    sleep(6000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
                while (!connection.isDisconnected()) {
                    try {                
                        addAgent();
                        sleep(60000*interval);                
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
        }
        System.out.println("stop");
        System.exit(-1);
    }
    
    static private void printUsage(){
        System.out.println("Java ChatBot console client");
        System.out.println("usage: java -jar ChatBot [-u username [-p password]] [-e encoding] [-s] [-i time_interval] [-c channels] ircserver[:port]");
        System.out.println("default: username=Blonde; password=\"\"; encoding=windows-1251; interval=30");
        System.out.println("where encoding is one of UTF-8, KOI8-R, windows-1251");
        System.out.println("timeinterval - time beetwen creatings of agent in minutes");
        System.out.println("-s - silent mode. interval is ignoring.");
    }
    public static void main(String argv[]) throws Exception{
            try {
                PrintStream pserr=new PrintStream(System.currentTimeMillis()+".err"),
                        psout=new PrintStream(System.currentTimeMillis()+".out");
                System.setErr(pserr);
                System.setOut(psout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        if (argv.length<1) {
            printUsage();
            return;
        }
        int cur=0;
        String serverName=argv[argv.length-1],
                username="Manve",
                password="",
                encoding="windows-1251",
                channels="#main;#sex;#football;#sport;#rightside;#wow;#help;#chan;#chat;#announces;#freebsd";
        boolean isSilent=false;
        int time=30;
        while (cur<argv.length-1) {
            if (argv[cur].charAt(0)!='-') {
                System.out.println("invalid argument:"+argv[cur]);
                printUsage();
                return;
            }
            switch (argv[cur].charAt(1)) {
                case 'u': {
                    username=argv[cur+1];
                    break;
                }
                case 'p' :{
                    password=argv[cur+1];
                    break;
                }
                case 'e' :{
                    encoding=argv[cur+1];
                    break;
                }
                case 'c' :{
                    channels=argv[cur+1];
                    break;
                }
                case 'i' :{
                    time=Integer.valueOf(argv[cur+1]);
                    break;
                }
                case 's': {
                    isSilent=true;
                    cur--;
                    break;
                } 
            }
            cur+=2;
        }         
        IRCConnection conn=new IRCConnection(serverName, encoding, null, username, password, channels.split("\\;"));
        conn.setSendingRawData(true);
        time = isSilent?-1:time;
        MainThread mt=new MainThread(conn, time);
    }
}
