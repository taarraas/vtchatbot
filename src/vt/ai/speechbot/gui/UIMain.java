/*
 * UIMain.java
 *
 * Created on 23 ������ 2008, 19:45
 */

package vt.ai.speechbot.gui;

import java.awt.event.ActionListener; 
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.SortedMap;
import vt.ai.speechbot.*;
import vt.ai.speechbot.blondefinal.SpeechBot;
import javax.net.vtirclib.ChanModel;
import javax.net.vtirclib.IRCActionListener;
import javax.net.vtirclib.IRCConnection;
import javax.net.vtirclib.LogWriter;
import javax.net.vtirclib.WindowManager;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
public class UIMain extends javax.swing.JFrame implements ActionListener, IRCActionListener, ListSelectionListener{
    IRCConnection connection=null;
    AgentManager botManager=null;
    WindowManager lwManager=null;
    LogWindow logWindow=null;
    LogWriter lw=null;
    SpeechBot spBot=null;
    /** Creates new form UIMain */
    public UIMain() {
//        (new SpeechBot()).createInitDB();
        initComponents();
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        jButton3.addActionListener(this);
        jButton4.addActionListener(this);
        jButton5.addActionListener(this);
        jButton6.addActionListener(this);
        allChans.addActionListener(this);
        chanList.addListSelectionListener(this);
        
        logWindow=new LogWindow(this, this, this);
        lwManager=new WindowManager(logWindow.getChanList(), logWindow.getView(), new JList(), new JTextField());
        logWindow.setVisible(true);
        SortedMap<String, Charset> sm = Charset.availableCharsets();
        Set set = sm.keySet();
        Object[] s = set.toArray();        
        for (int i=0; i< s.length; i++) encodingList.addItem(s[i]);
        encodingList.setSelectedItem("windows-1251");
        if (false) {
            try {
                PrintStream pserr=new PrintStream(System.currentTimeMillis()+".err"),
                        psout=new PrintStream(System.currentTimeMillis()+".out");
                System.setErr(pserr);
                System.setOut(psout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            if (evt.getActionCommand().equals("connect")) {
                if (connection==null) {
                    connection = new IRCConnection(urlField.getText(), encodingList.getSelectedItem().toString(), this, nickField.getText(), "", null);
                    connection.setSendingRawData(true);
                    botManager=new AgentManager(spBot=new SpeechBot(), connection);                    
                    connection.addIRCActionListener(botManager);
                    connection.addIRCActionListener(lwManager);                    
                    connection.addIRCActionListener(lw=new LogWriter(urlField.getText()));
                    lw.setRAWDataLogging(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (connection==null) return;
               
        if (evt.getActionCommand().equals("join")) {
            connection.joinChannel(chanField.getText());
        }        
        if (evt.getActionCommand().equals("leave")) {
            connection.leaveChannel((String)chanList.getSelectedValue(), "No reason");
        }
        if (evt.getActionCommand().equals("All channels")) {
            showUsers();
        }
        if (evt.getActionCommand().equals("init all")) {
            for (int i = 0; i < userList.getModel().getSize(); i++) {
                botManager.addAgent(userList.getModel().getElementAt(i).toString());
            }
        }
        if (evt.getActionCommand().equals("db")) {
            //spBot.createInitDB();
        }
        if (evt.getActionCommand().equals("init selected")) {
            for (int elem : userList.getSelectedIndices()) {
                botManager.addAgent((String)userList.getModel().getElementAt(elem).toString());                
            }
        }
        if (evt.getActionCommand().equals("kill, ignore and commit")) {
            botManager.ignoreUser(logWindow.getSelectedUser());
            botManager.commitAgentSpeech(logWindow.getSelectedUser());
            botManager.removeAgent(logWindow.getSelectedUser());
            logWindow.setUIAgentOn(false);
        }        
        if (evt.getActionCommand().equals("kill, ignore")) {
            botManager.ignoreUser(logWindow.getSelectedUser());
            botManager.removeAgent(logWindow.getSelectedUser());
            logWindow.setUIAgentOn(false);
        }
        if (evt.getActionCommand().equals("commit")) {
            botManager.commitAgentSpeech(logWindow.getSelectedUser());
        }
        if (evt.getActionCommand().equals("send")) {
            String user=logWindow.getSelectedUser();
            botManager.agentByNick(user).cancelAllRemarks();
            botManager.agentByNick(user).getBotState().botSay(logWindow.getSend());
            connection.sendMessage(user, logWindow.getSend());
            logWindow.clearSend();
        }
    }

    @Override
    public void ircActionPerformed(int actionType, String where, String data) {
        switch (actionType) {
            case CHANNEL_CHANGED: {
                chanList.setModel(new DefaultComboBoxModel(connection.getChanModel().getLoggedChannels().toArray(new String[0])));
                showUsers();
                break;
            }
            case NICK_CHANGED: {
                showUsers();
                break;
            }
        }
    }
    @Override
    public void ircNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice) {
    }
        
    private void showUsers() {
        if (chanList.getModel().getSize()==0) return;
        Set<String> ret;
        ChanModel chanModel=connection.getChanModel();
        if (allChans.getModel().isSelected()) {
            ret=chanModel.getUsers();            
        } else {
            if (chanList.getSelectedIndex()==-1) chanList.setSelectedIndex(0);
            ret=chanModel.getUsersOnChannel(chanList.getSelectedValue().toString());
        }
        ret.remove(connection.getChanModel().getNick());            
        userList.setModel(new DefaultComboBoxModel(ret.toArray()));
    }
    
    private void refreshAgentUIState() {
            if (logWindow!=null && botManager!=null) {
                logWindow.setUIAgentOn(botManager.containAgent(logWindow.getSelectedUser()));                
            }        
    }
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource().equals(chanList)) 
            showUsers();
        else {
            refreshAgentUIState();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        urlField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nickField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        encodingList = new javax.swing.JComboBox();
        jButton6 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chanList = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();
        chanField = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        allChans = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Blonde chat bot");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("URL");

        urlField.setText("10.45.64.2");

        jLabel2.setText("nick");

        nickField.setText("Blonde");

        jButton1.setText("connect");

        jButton6.setText("db");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(urlField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nickField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(32, 32, 32)
                .add(encodingList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel1)
                .add(urlField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel2)
                .add(nickField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(encodingList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jButton1)
                .add(jButton6))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setViewportView(chanList);

        jButton2.setMnemonic(' ');
        jButton2.setText("join");

        chanField.setText("#main");

        jButton3.setText("leave");

        jScrollPane2.setViewportView(userList);

        allChans.setSelected(true);
        allChans.setText("All channels");
        allChans.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        allChans.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jButton4.setText("init all");

        jButton5.setText("init selected");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .add(chanField)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allChans, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButton5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .add(198, 198, 198))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton5))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jButton2)
                                    .add(chanField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jScrollPane2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButton3)
                            .add(allChans)))))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    }//GEN-LAST:event_formWindowClosed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            System.out.println("Closing");
            if (lw!=null) lw.finalize();
            if (botManager!=null) botManager.commitAllAgentSpeech(6);
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
// TODO add your handling code here:
}//GEN-LAST:event_formWindowClosing
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UIMain().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allChans;
    private javax.swing.JTextField chanField;
    private javax.swing.JList chanList;
    private javax.swing.JComboBox encodingList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nickField;
    private javax.swing.JTextField urlField;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
    
}
