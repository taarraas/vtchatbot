/*
 * SpeechBotFirst.java
 *
 * Created on 24 ������ 2008, 9:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import vt.ai.speechbot.*;

/**
 *
 * @author taras
 */
public class SpeechBot extends AbstractSpeechBot{
    
    /** Creates a new instance of SpeechBotFirst */
    Connection dbConnection;
    PreprogrammedAI preprogrammed;
    public UserInfo userInfo;
    public SelfTrainingAI selftraining;
    DumbDBUseAI dumb;
    TaskManager taskManager=new TaskManager(this);
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public SpeechBot() {
        /*
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch (Exception e) {
            System.err.println("JDBC-ODBC Bridge not found. May be loaded from *nix");
        }
        try {
            dbConnection=DriverManager.getConnection("jdbc:odbc:Driver=Microsoft Access Driver (*.mdb);DBQ="+database);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to database"); 
        }*/
        try {
            // Load the JDBC driver
            //String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver
            //Class.forName(driverName);
            Class.forName("com.mysql.jdbc.Driver");
            // Create a connection to the database
            String serverName = "localhost";
            String mydatabase = "blonde";
            String url = "jdbc:mysql://"+serverName+"/"+mydatabase; // a JDBC url
            Properties prop=new Properties();
            String username = "root";
            String password = ",jzybot";
            prop.put("user", username);
            prop.put("password", password);
            prop.put("useUnicode", "true");
            prop.put("characterEncoding", "UTF-8");
            dbConnection = DriverManager.getConnection(url, prop);
            preprogrammed=new PreprogrammedAI(dbConnection);
            userInfo= new UserInfo(dbConnection);
            selftraining=new SelfTrainingAI(dbConnection);
            dumb=new DumbDBUseAI(dbConnection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not find the database driver");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to the database");
        }
    }
    public void getAnswer(Agent agent) {
        if (agent.getBotState().getRemarkCount()<2) {
            userInfo.addSession(agent.getBotState().getInterlocutorNick());
        }
        taskManager.newTask(agent);
    }
    public void commitAll(SpeechState state) {
        selftraining.commitAll(state);
    }
    public void createInitDB() {
        try {
            BufferedReader re=new BufferedReader(new InputStreamReader(new FileInputStream("create.sql"), "UTF-8"));
            String sql="";
            int cur;
            while ((cur=re.read())!=-1) sql+=(char)cur;
            String parsed[]=sql.split("\\;");
            Statement st=dbConnection.createStatement();
            for (String string : parsed) {
                //if (string.isEmpty()) continue;
                st.execute(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }       
    public void deleteFromDBState(SpeechState state) {
        selftraining.deleteFromDBState(state);
    }    
}
