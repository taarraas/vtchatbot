/*
 * SelfTrainingAI.java
 *
 * Created on 28 ?????? 2008, 10:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vt.ai.speechbot.blondefinal;

import vt.linguistics.distances.Text;
import java.io.PrintStream;
import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import vt.ai.speechbot.SpeechState;

/**
 *
 * @author taras
 */
public class SelfTrainingAI {
//    private static final int COUNT_LOADED_REMARKS_FOR_RANDOM_ANSWER=10;
    private static final int MAXIMUM_REMARK_LENGTH=255;
    Connection dbConnection;     
    public SelfTrainingAI(Connection database) throws SQLException{
        dbConnection=database;
        getDBRemarkforIdstatement=dbConnection.createStatement();
        stDBBestAnswer=dbConnection.createStatement();
        stDBBestAnswerTmp=dbConnection.createStatement();        
    }    

    Statement getDBRemarkforIdstatement;
    protected String getDBRemarkForID(int id) throws SQLException{
        ResultSet rs=getDBRemarkforIdstatement.executeQuery("SELECT * FROM selftrainingmessages WHERE id="+id);
        if (rs.next()) 
            return rs.getString("message");
        else 
            return null;
    }

        
    protected Map<Integer, Double> getFullRevalentInfoFor(int id) throws SQLException{
        Map<Integer, Double> res=new TreeMap<Integer, Double>();
        PreparedStatement statement=dbConnection.prepareStatement( 
                "SELECT * FROM revalentinfo WHERE first=?;");
        statement.setInt(1, id);        
        ResultSet rs=statement.executeQuery();
        while (rs.next()) {
            res.put(rs.getInt("second"), rs.getDouble("value"));
        }
        rs.close();
        statement.close();
        
        //same, but for second...
        statement=dbConnection.prepareStatement( 
                "SELECT * FROM revalentinfo \n" +
                "WHERE second=?;");
        statement.setInt(1, id);
        
        rs=statement.executeQuery();
        while (rs.next()) {
            res.put(rs.getInt("first"), rs.getDouble("value"));
        }        
        rs.close();
        statement.close();
        return res;
    }
    
    Statement stDBBestAnswer, stDBBestAnswerTmp;
    int idBestAnswer;
    private String getDBBestAnswer(SpeechState state, Set<Integer> exclude) throws SQLException{
        String remarkLast=SpeechState.convertToMeta(state, state.getLastRemark()),
                remarkNextToLast=SpeechState.convertToMeta(state, state.getNextToLastRemark());
        int idLast=addToDBRemark(remarkLast),
                idNextToLast=addToDBRemark(remarkNextToLast);
        idBestAnswer=-1;
        double bestCompareLevel=Text.minimumCompareLevel;
        Map<Integer, Double> revalentLast=getFullRevalentInfoFor(idLast),
                    revalentNextToLast=getFullRevalentInfoFor(idNextToLast);
        ResultSet rs=stDBBestAnswer.executeQuery("SELECT * FROM selftrainingconnections;");
        while (rs.next()) {
            Double revNextToLast=revalentNextToLast.get(rs.getInt("idprevremark")),
                   revLast=revalentLast.get(rs.getInt("idcurremark"));
            double curcmp=Text.comparePairs(revNextToLast==null?0:revNextToLast.doubleValue(),
                    revLast==null?0:revLast.doubleValue());
            if (curcmp>bestCompareLevel) {
                    Integer cur=rs.getInt("idAnswer");
                    if (!exclude.contains(cur)) {
                        bestCompareLevel=curcmp;
                        idBestAnswer=cur;
                    }
            }
        }
        rs.close();
        return SpeechState.convertFromMeta(state, getDBRemarkForID(idBestAnswer));
    } 
    
    
    private void addToDBRevalentInfo(String remark, int id) throws SQLException{
        ResultSet rs=dbConnection.createStatement().executeQuery(
                "SELECT * FROM selftrainingmessages;");
        PreparedStatement statement=dbConnection.prepareStatement(
                "INSERT INTO revalentinfo (first, second, value) VALUES (?, ?, ?);");
        statement.setInt(1, id);
        while (rs.next()) {            
            double currevalent=-1;
            String curString=rs.getString("message");
            if (curString.equals(remark)) 
                continue;
            if ((currevalent=Text.compareRemarks(remark, curString))>Text.minimumCompareLevel) {
                statement.setInt(2, rs.getInt("id"));
                statement.setDouble(3, currevalent);
                statement.execute();
            }
        }
        statement.close();
        rs.close();
    }   
    /**
     * you can use it as getEqualDBRemark
     * @param remark
     * @return id of this remark
     * @throws java.sql.SQLException
     */
    private int addToDBRemark(String remark) throws SQLException{
        int tmp=getEqualDBRemark(remark);        
        if (tmp==-1) {
            PreparedStatement statement=dbConnection.prepareStatement(
                    "INSERT INTO selftrainingmessages (message) VALUES (?)");
            if (remark.length()>MAXIMUM_REMARK_LENGTH) remark=remark.substring(0, MAXIMUM_REMARK_LENGTH);
            statement.setString(1, remark);
            statement.execute();
            statement.close();
            tmp=getEqualDBRemark(remark);        
           addToDBRevalentInfo(remark, tmp);
        }
        return tmp;
    }
    private void addToDBConnection(long from1, long from2, long to) throws SQLException{
        PreparedStatement statement=dbConnection.prepareStatement(
                "INSERT INTO selftrainingconnections (idprevremark, idcurremark, idanswer) VALUES (?, ?, ?);");
        statement.setInt(1, (int)from1);
        statement.setInt(2, (int)from2);
        statement.setInt(3, (int)to);
        statement.execute();
        statement.close();
    }
    public void addToDB(String first, String second, String interlocutorAnswer) throws SQLException {
        long from1=addToDBRemark(first),
                from2=addToDBRemark(second),
                to=addToDBRemark(interlocutorAnswer);
        addToDBConnection(from1, from2, to);
    }
    public void commitAll(SpeechState state) {
        try {
            for (int i = 0; i < state.getCompletePairCount(); i++) {
                String pair[]=state.getPartsOfDialog(3, i);
                addToDB(pair[0], pair[1], pair[2]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw(new RuntimeException("cannot commit"));
        }
    }
    private int getEqualDBRemark(String remark) throws SQLException{
        if (remark.length()>MAXIMUM_REMARK_LENGTH) remark=remark.substring(0, MAXIMUM_REMARK_LENGTH);
        PreparedStatement statement=dbConnection.prepareStatement("Select * from selftrainingmessages where message=?");
        statement.setString(1, remark);
        ResultSet rs=statement.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            return -1;
        }
    }
    public String getAnswer(SpeechState state) {
        try {
            String ans="";
            Set<Integer> excl=new TreeSet<Integer>();
            while (state.contains(ans=getDBBestAnswer(state, excl))) {
                excl.add(idBestAnswer);
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace();
            throw(new RuntimeException("cannot get answer for:"+state.getLastInterlocutorRemark()));
        }
        
    }
    public void recalcRevalentInfo() throws SQLException{
        dbConnection.createStatement().execute("DROP TABLE IF EXISTS revalentinfo;");
        dbConnection.createStatement().execute("create table revalentinfo ("
                                                    +"first INT not null,"
                                                    +"second INT not null,"
                                                    +"value DOUBLE not null"
                                                    +") CHARACTER SET utf8;");
        ResultSet rs=dbConnection.createStatement().
                executeQuery("SELECT * FROM selftrainingmessages;");
        while (rs.next()){
            addToDBRevalentInfo(rs.getString("message"), rs.getInt("id"));
        }
    }
    public static void main(String argv[]) throws Exception {
        SpeechBot sb=new SpeechBot();
        SelfTrainingAI sti=sb.selftraining;
        sti.recalcRevalentInfo();
        ResultSet rs=sti.dbConnection.createStatement().
                executeQuery("SELECT * FROM selftrainingmessages;");
        System.setOut(new PrintStream("db.out"));
        while (rs.next())  {
            System.out.println("----------------------------------------------------");
            System.out.println(rs.getString("message"));
            int id=rs.getInt("id");
            Map<Integer, Double> rev=sti.getFullRevalentInfoFor(id);
            for (Integer i : rev.keySet()) {
                System.out.println("      >>"+rev.get(i)+"="+sti.getDBRemarkForID(i));
            }
        }        
    }
}
