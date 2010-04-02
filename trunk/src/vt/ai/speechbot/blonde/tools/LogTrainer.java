/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde.tools;

import vt.ai.speechbot.blonde2.*;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import vt.linguistics.UniversalDialogParser.DialogParser;

/**
 *
 * @author taras
 */
public class LogTrainer {    
    private static final String DIR="./TeachLogs/";    
    SpeechBot sb=new SpeechBot();
    SelfTrainingAI sti=sb.selftraining;
    DialogParser dp=new DialogParser();        
    int cntadded=0;
    public void train(String text) {
        Vector<String> ret=dp.parse(text);
        if (ret==null) return;
        String speech[]=ret.toArray(new String[0]);
        for (int i = 2; i < speech.length; i++) {
            try {
                sti.addToDB(speech[i - 2], speech[i - 1], speech[i]);                
                System.out.println("#"+cntadded + ":"+speech[i - 2] + " <-> " + speech[i - 1] + "<->" + speech[i]);
                cntadded++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }        
    }
    public void trainFile(String filename) throws IOException{
        FileReader fr=new FileReader(filename);
        char buf[]=new char[65000];
        int cnt=fr.read(buf);
        if (cnt==-1) {
            System.err.println("File:"+filename+" is empty");
            return;
        }
        String txt=String.valueOf(buf, 0, cnt);
        train(txt);
        
    }
    public static void main(String argv[]) throws IOException{
        
        String files[]=(new File(DIR)).list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        });
        LogTrainer lt=new LogTrainer();
        for (String string : files) {                        
            lt.trainFile(DIR+string);
        }
    }
}
