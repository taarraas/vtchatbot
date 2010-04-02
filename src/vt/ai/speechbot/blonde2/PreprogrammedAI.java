/*
 * PreprogrammedAI.java
 *
 * Created on 28 ������ 2008, 9:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import vt.utils.Utils;
import java.sql.*;
import vt.ai.speechbot.*;
import java.util.Vector;

/**
 *
 * @author taras
 */
public class PreprogrammedAI {
    
    /** Creates a new instance of PreprogrammedAI */
    Connection dbConnection;    
    private static final int WHOLE_WORD=0, //0-as word in text(left and right are space or punctuation symbols)
            CHARACTERS_GROUP=1, //1-as group of characters
            ONE_WORD=2; //2-only this word
    private static final int COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER=10;
    public String[] tableToArray(String table) throws SQLException {
        Statement statement=dbConnection.createStatement();
        ResultSet rs=statement.executeQuery("SELECT * from "+table);
        Vector<String> res = new Vector<String>();
        while (rs.next()) {
            res.add(rs.getString("message"));
        }
        statement.close();
        return res.toArray(new String[0]);
    }    
    public PreprogrammedAI(Connection database) {
        dbConnection=database;
    }
    public boolean isEqualTo(String word, int compareType, String remark, boolean casesensetive) {  
        //System.out.println(word+" "+wordType+" "+remark);
        String wordN, remarkN;
        if (casesensetive) {
            wordN=word;
            remarkN=remark;
        } else {
            wordN=word.toLowerCase();
            remarkN=remark.toLowerCase();
        }
        switch (compareType) {
            case WHOLE_WORD: {
                return remarkN.matches(".*(\\s|\\.|\\,|^)"+wordN+"(\\s|\\.|\\,|\\z|&).*");
            }
            case CHARACTERS_GROUP: {
                return remarkN.indexOf(wordN)!=-1;
            }
            case ONE_WORD: {
                return remarkN.equals(wordN);
            }
            default: {
                throw new RuntimeException("Unknown type:"+compareType+" of word:"+word);
            }
        }
    }
    public String getRemarkOfType(int type, SpeechState state) throws SQLException {
        Statement statement=dbConnection.createStatement();
        ResultSet rs=statement.executeQuery("SELECT * FROM preprogrammedanswers WHERE wordtype="+type);
        boolean can;
        String res=null;
        Vector<String> answ=new Vector<String>();
        while (rs.next() && answ.size()<COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER) {
            if (!state.contains(res=rs.getString("message"))) {
                answ.add(res);
            }
        }
        if (answ.size()>0) 
            return answ.elementAt(Utils.getRandom(answ.size()));
        else 
            return null;        
    }
    public String getAnswer(vt.ai.speechbot.SpeechState state) {
        try {
            Statement statement=dbConnection.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM preprogrammedwords");
            String tmp=null;
            //int type;
            while (rs.next()) {
                if (isEqualTo(rs.getString("word"), rs.getInt("comparetype"),
                        state.getLastInterlocutorRemark(), rs.getBoolean("casesensetive"))
                && (tmp=getRemarkOfType(rs.getInt("type"), state))!=null) break;
            }
            if (tmp!=null)
                return tmp;
            else 
                if ((tmp=getRemarkOfType(0, state))!=null) {
                    return tmp;
                } else {
                    return null;
                }           
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("cant get answer");             
        }
    }    
    public String getTimeOutRemark(SpeechState state) {
        try {
            Statement statement=dbConnection.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM preprogrammedtimeout WHERE minimumremarksbefore<="+state.getRemarkCount());
            boolean can;
            String res=null;
            while ((can=rs.next()) && state.contains(res=rs.getString("message")));
            if (can) 
                return res;
            else 
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("cant get timeout");             
        }
    }
    public static void main(String argv[]) {
    }    
}
