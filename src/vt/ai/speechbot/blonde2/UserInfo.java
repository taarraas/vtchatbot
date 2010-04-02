/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author taras
 */
public class UserInfo {
    Connection dbConnection;
    public UserInfo(Connection database) throws SQLException{
        dbConnection=database;
    }
    public int sessionCount=0,
        asBotSessionCount=0;
    public String identifier="";
    public boolean getUserInfo(String identifier) {
        this.identifier=identifier;
        try {
            PreparedStatement stat=dbConnection.prepareStatement("SELECT * FROM userinfo " +
                    "WHERE identifier=?;");
            stat.setString(1, identifier);
            ResultSet rs=stat.executeQuery();
            if (rs.next()) {
                sessionCount=rs.getInt("sessionCount");
                asBotSessionCount=rs.getInt("asBotSessionCount");
                stat.close();
                return true;
            } else {
                stat.close();
                sessionCount=0;
                asBotSessionCount=0;
                return false;
            }             
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setUserInfo(String identifier, int sessionCount, int asBotSessionCount) {
        try {
            PreparedStatement stat=dbConnection.prepareStatement("DELETE FROM userinfo " +
                    "WHERE identifier=?;");
            stat.setString(1, identifier);
            stat.execute();
            stat.close();
            
            stat = dbConnection.prepareStatement(
                    "INSERT INTO userinfo(identifier, sessionCount, asBotSessionCount) " +
                    "VALUES (?, ?, ?);");
            stat.setString(1, identifier);
            stat.setInt(2, sessionCount);
            stat.setInt(3, asBotSessionCount);
            stat.execute();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addAsBot(String identifier) {
        getUserInfo(identifier);
        setUserInfo(identifier, sessionCount, asBotSessionCount+1);        
    }
    public void addSession(String identifier) {
        getUserInfo(identifier);
        setUserInfo(identifier, sessionCount+1, asBotSessionCount);
    }
}
