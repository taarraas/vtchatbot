/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author taras
 */
public class RecalcRevalentInfo {
    SelfTrainingAI selfTrainingAI;
    public RecalcRevalentInfo(SelfTrainingAI sf) {
        selfTrainingAI=sf;
    }
    private void writeNum(int num) {
        try {
            FileWriter fw=new FileWriter("revalentinfo.calc");
            fw.write(Integer.toString(num));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void recalcRevalentInfo() throws SQLException{
        FileReader fr;
        try {
            int initialRemarkNum;
            try {
                fr=new FileReader("revalentinfo.calc");
                StreamTokenizer st=new StreamTokenizer(fr);
                st.nextToken();
                initialRemarkNum=(int)st.nval;
            } catch(java.io.IOException e) {
                initialRemarkNum=0;
                selfTrainingAI.dbConnection.createStatement().execute("DROP TABLE IF EXISTS revalentinfo;");
                selfTrainingAI.dbConnection.createStatement().execute("create table revalentinfo ("
                                                        +"first INT not null,"
                                                        +"second INT not null,"
                                                        +"value DOUBLE not null"
                                                        +") CHARACTER SET utf8;");
            }
            
            ResultSet rs=selfTrainingAI.dbConnection.createStatement().
                    executeQuery("SELECT * FROM selftrainingmessages WHERE id>="+initialRemarkNum+" ORDER BY id;");
            while (rs.next()){
                int id;
                selfTrainingAI.addToDBRevalentInfo(rs.getString("message"), id=rs.getInt("id"));
                System.out.println(id);
                writeNum(id+1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String argv[]) throws Exception {
        SpeechBot sb=new SpeechBot();
        SelfTrainingAI sti=sb.selftraining;
        sti.deleteNotUsedRemarks();
        RecalcRevalentInfo rri=new RecalcRevalentInfo(sti);
        rri.recalcRevalentInfo();
        ResultSet rs=sti.dbConnection.createStatement().
                executeQuery("SELECT * FROM selftrainingmessages ORDER BY id;");
        System.setOut(new PrintStream("db.out"));
        while (rs.next())  {
            System.out.println("----------------------------------------------------");
            System.out.println(rs.getString("message"));
            int id=rs.getInt("id");
            System.out.println(id);
            Map<Integer, Double> rev=sti.getFullRevalentInfoFor(id);
            for (Integer i : rev.keySet()) {
                System.out.println("      >>"+rev.get(i)+"="+sti.getDBRemarkForID(i));
            }
        }
    }

}
