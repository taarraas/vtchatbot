/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blondefinal;

import vt.utils.Utils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import vt.ai.speechbot.SpeechState;

/**
 *
 * @author taras
 */
public class DumbDBUseAI {
    private static final int MINIMIMUM_QUESTION_REMARK_LENGTH=15;
    private static final int COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER=500;
    Connection dbConnection;
    Statement stat;
    public DumbDBUseAI(Connection database) throws SQLException{
        dbConnection=database;
        stat=dbConnection.createStatement();
    }
    static private boolean isQuestionRemark(String remark) {
        return remark.indexOf('?')!=-1;
    }
    private String getRandomQuestion(SpeechState state) throws SQLException{
        String[] randomRemark=new String[COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER];
        int count=0;
        ResultSet rs=stat.executeQuery("Select * from selftrainingmessages;");
        while (rs.next() && count<COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER) {
            String current=rs.getString("message");
            if (!isQuestionRemark(current)) continue;
            if (current.length()<MINIMIMUM_QUESTION_REMARK_LENGTH) continue;
            if (state.contains(current)) continue;
            randomRemark[count++]=current;
        }
        return randomRemark[Utils.getRandom(count)];
    }
    public String getAnswer(SpeechState state) {
        if (isQuestionRemark(state.getLastRemark())) return null;
        if (state.getRemarkCount()<5) return null;
        try {
            return state.convertFromMeta(getRandomQuestion(state));
        } catch (SQLException e) {
            System.err.println("Cannot get dumb answer to agent:"+state.getInterlocutorNick());
            e.printStackTrace();
            return null;
        }
    }
}
