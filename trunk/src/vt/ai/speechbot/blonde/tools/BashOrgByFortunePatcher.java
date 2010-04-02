/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.ai.speechbot.blonde.tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author taras
 */
public class BashOrgByFortunePatcher {
    public static void main(String argv[]) throws IOException{
        FileReader fr=new FileReader("./TeachLogs/fortune-bashorgru");
        int ch;
        String cur="";
        int i=0;
        LogTrainer lt=new LogTrainer();
        while (true) {
            ch=fr.read();
            if (ch=='%' || ch==-1) {
                lt.train(cur);
                System.out.print("#"+i);
                cur="";
                if ((ch=fr.read())!='\n') cur+=(char)ch;
                i++;
                if (ch==-1) return;
            } else {
                cur+=(char)ch;
            }
        }
    }
}
