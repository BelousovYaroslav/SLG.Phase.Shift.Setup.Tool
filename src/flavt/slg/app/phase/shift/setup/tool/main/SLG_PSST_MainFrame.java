/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flavt.slg.app.phase.shift.setup.tool.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author yaroslav
 */
public class SLG_PSST_MainFrame extends javax.swing.JFrame {
    private final SLG_PSST_App theApp;
    
    Timer tRefreshStates;
    Timer tRefreshValues;
    Timer tPolling;
    
    /**
     * Creates new form MainFrame
     */
    public SLG_PSST_MainFrame( SLG_PSST_App app) {
        theApp = app;
        initComponents();        
        
        tRefreshStates = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e) {
                btnT1Get.setEnabled( theApp.m_bConnected);
                btnT1Set.setEnabled( theApp.m_bConnected);
                btnT2Get.setEnabled( theApp.m_bConnected);
                btnT2Set.setEnabled( theApp.m_bConnected);
                btnT3Get.setEnabled( theApp.m_bConnected);
                btnT3Set.setEnabled( theApp.m_bConnected);
                btnT4Get.setEnabled( theApp.m_bConnected);
                btnT4Set.setEnabled( theApp.m_bConnected);
                btnT5Get.setEnabled( theApp.m_bConnected);
                btnT5Set.setEnabled( theApp.m_bConnected);
                
                btnT6Get.setEnabled( theApp.m_bConnected);
                btnT6Set.setEnabled( theApp.m_bConnected);
                btnT7Get.setEnabled( theApp.m_bConnected);
                btnT7Set.setEnabled( theApp.m_bConnected);
                btnT8Get.setEnabled( theApp.m_bConnected);
                btnT8Set.setEnabled( theApp.m_bConnected);
                btnT9Get.setEnabled( theApp.m_bConnected);
                btnT9Set.setEnabled( theApp.m_bConnected);
                btnT10Get.setEnabled( theApp.m_bConnected);
                btnT10Set.setEnabled( theApp.m_bConnected);
                
                btnT11Get.setEnabled( theApp.m_bConnected);
                btnT11Set.setEnabled( theApp.m_bConnected);
                
                
                btnPS1Get.setEnabled( theApp.m_bConnected);
                btnPS1Set.setEnabled( theApp.m_bConnected);
                btnPS2Get.setEnabled( theApp.m_bConnected);
                btnPS2Set.setEnabled( theApp.m_bConnected);
                btnPS3Get.setEnabled( theApp.m_bConnected);
                btnPS3Set.setEnabled( theApp.m_bConnected);
                btnPS4Get.setEnabled( theApp.m_bConnected);
                btnPS4Set.setEnabled( theApp.m_bConnected);
                btnPS5Get.setEnabled( theApp.m_bConnected);
                btnPS5Set.setEnabled( theApp.m_bConnected);
                
                btnPS6Get.setEnabled( theApp.m_bConnected);
                btnPS6Set.setEnabled( theApp.m_bConnected);
                btnPS7Get.setEnabled( theApp.m_bConnected);
                btnPS7Set.setEnabled( theApp.m_bConnected);
                btnPS8Get.setEnabled( theApp.m_bConnected);
                btnPS8Set.setEnabled( theApp.m_bConnected);
                btnPS9Get.setEnabled( theApp.m_bConnected);
                btnPS9Set.setEnabled( theApp.m_bConnected);
                btnPS10Get.setEnabled( theApp.m_bConnected);
                btnPS10Set.setEnabled( theApp.m_bConnected);
                
                btnPS11Get.setEnabled( theApp.m_bConnected);
                btnPS11Set.setEnabled( theApp.m_bConnected);
            }
        });
        tRefreshStates.start();
        
        tRefreshValues = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if( theApp.m_bConnected) {
                    lblPacksCounter.setText( "Счётчик посылок: " + theApp.m_nPacksCounter);
                }
                else {
                    lblPacksCounter.setText( "Нет соединения");
                }
                    
                    
                JTextField edtsT[] =  { edtT1Show, edtT2Show, edtT3Show, edtT4Show, edtT5Show,
                                        edtT6Show, edtT7Show, edtT8Show, edtT9Show, edtT10Show,
                                        edtT11Show };
                
                JTextField edtsPS[] = { edtPS1Show, edtPS2Show, edtPS3Show, edtPS4Show, edtPS5Show,
                                        edtPS6Show, edtPS7Show, edtPS8Show, edtPS9Show, edtPS10Show,
                                        edtPS11Show };
                    
                for( int i=0; i<11; i++) {
                    edtsT[i].setText( "" + theApp.m_DevT[ i]);
                    edtsPS[i].setText( "" + theApp.m_DevPS[ i]);
                }
            }
            
            
        });
        tRefreshValues.start();
        
        tPolling = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
            
        });
        tPolling.start();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPort = new javax.swing.JLabel();
        edtPort = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        lblPacksCounter = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        lblTemperature = new javax.swing.JLabel();
        btnT1Get = new javax.swing.JButton();
        edtT1Show = new javax.swing.JTextField();
        edtT1Edit = new javax.swing.JTextField();
        btnT1Set = new javax.swing.JButton();
        btnT2Get = new javax.swing.JButton();
        edtT2Show = new javax.swing.JTextField();
        edtT2Edit = new javax.swing.JTextField();
        btnT2Set = new javax.swing.JButton();
        btnT3Get = new javax.swing.JButton();
        edtT3Show = new javax.swing.JTextField();
        edtT3Edit = new javax.swing.JTextField();
        btnT3Set = new javax.swing.JButton();
        btnT4Get = new javax.swing.JButton();
        edtT4Show = new javax.swing.JTextField();
        edtT4Edit = new javax.swing.JTextField();
        btnT4Set = new javax.swing.JButton();
        btnT5Get = new javax.swing.JButton();
        edtT5Show = new javax.swing.JTextField();
        edtT5Edit = new javax.swing.JTextField();
        btnT5Set = new javax.swing.JButton();
        btnT6Get = new javax.swing.JButton();
        edtT6Show = new javax.swing.JTextField();
        edtT6Edit = new javax.swing.JTextField();
        btnT6Set = new javax.swing.JButton();
        btnT7Get = new javax.swing.JButton();
        edtT7Show = new javax.swing.JTextField();
        edtT7Edit = new javax.swing.JTextField();
        btnT7Set = new javax.swing.JButton();
        btnT8Get = new javax.swing.JButton();
        edtT8Show = new javax.swing.JTextField();
        edtT8Edit = new javax.swing.JTextField();
        btnT8Set = new javax.swing.JButton();
        btnT9Get = new javax.swing.JButton();
        edtT9Show = new javax.swing.JTextField();
        edtT9Edit = new javax.swing.JTextField();
        btnT9Set = new javax.swing.JButton();
        btnT10Get = new javax.swing.JButton();
        edtT10Show = new javax.swing.JTextField();
        edtT10Edit = new javax.swing.JTextField();
        btnT10Set = new javax.swing.JButton();
        btnT11Get = new javax.swing.JButton();
        edtT11Show = new javax.swing.JTextField();
        edtT11Edit = new javax.swing.JTextField();
        btnT11Set = new javax.swing.JButton();
        lblPhaseShift = new javax.swing.JLabel();
        btnPS1Get = new javax.swing.JButton();
        edtPS1Show = new javax.swing.JTextField();
        edtPS1Edit = new javax.swing.JTextField();
        btnPS1Set = new javax.swing.JButton();
        btnPS2Get = new javax.swing.JButton();
        edtPS2Show = new javax.swing.JTextField();
        edtPS2Edit = new javax.swing.JTextField();
        btnPS2Set = new javax.swing.JButton();
        btnPS3Get = new javax.swing.JButton();
        edtPS3Show = new javax.swing.JTextField();
        edtPS3Edit = new javax.swing.JTextField();
        btnPS3Set = new javax.swing.JButton();
        btnPS4Get = new javax.swing.JButton();
        edtPS4Show = new javax.swing.JTextField();
        edtPS4Edit = new javax.swing.JTextField();
        btnPS4Set = new javax.swing.JButton();
        btnPS5Get = new javax.swing.JButton();
        edtPS5Show = new javax.swing.JTextField();
        edtPS5Edit = new javax.swing.JTextField();
        btnPS5Set = new javax.swing.JButton();
        btnPS6Get = new javax.swing.JButton();
        edtPS6Show = new javax.swing.JTextField();
        edtPS6Edit = new javax.swing.JTextField();
        btnPS6Set = new javax.swing.JButton();
        btnPS7Get = new javax.swing.JButton();
        edtPS7Show = new javax.swing.JTextField();
        edtPS7Edit = new javax.swing.JTextField();
        btnPS7Set = new javax.swing.JButton();
        btnPS8Get = new javax.swing.JButton();
        edtPS8Show = new javax.swing.JTextField();
        edtPS8Edit = new javax.swing.JTextField();
        btnPS8Set = new javax.swing.JButton();
        btnPS9Get = new javax.swing.JButton();
        edtPS9Show = new javax.swing.JTextField();
        edtPS9Edit = new javax.swing.JTextField();
        btnPS9Set = new javax.swing.JButton();
        btnPS10Get = new javax.swing.JButton();
        edtPS10Show = new javax.swing.JTextField();
        edtPS10Edit = new javax.swing.JTextField();
        btnPS10Set = new javax.swing.JButton();
        btnPS11Get = new javax.swing.JButton();
        edtPS11Show = new javax.swing.JTextField();
        edtPS11Edit = new javax.swing.JTextField();
        btnPS11Set = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("МЛГ3Б. Утилита для редактирования калибровки фазового сдвига  (С) ФЛАВТ   2017.07.31 15:52");
        setMaximumSize(new java.awt.Dimension(850, 500));
        setMinimumSize(new java.awt.Dimension(850, 500));
        setPreferredSize(new java.awt.Dimension(850, 500));
        setResizable(false);
        getContentPane().setLayout(null);

        lblPort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPort.setText("<html><b><u>Порт</b></u></html>");
        getContentPane().add(lblPort);
        lblPort.setBounds(580, 10, 270, 30);
        getContentPane().add(edtPort);
        edtPort.setBounds(580, 50, 260, 30);

        btnConnect.setText("Соединиться с прибором");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        getContentPane().add(btnConnect);
        btnConnect.setBounds(580, 90, 260, 40);

        lblPacksCounter.setText("Счётчик посылок:");
        getContentPane().add(lblPacksCounter);
        lblPacksCounter.setBounds(580, 140, 260, 20);

        btnExit.setText("Выход");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit);
        btnExit.setBounds(580, 340, 260, 40);

        lblTemperature.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTemperature.setText("<html><b><u>Температура</b></u></html>");
        getContentPane().add(lblTemperature);
        lblTemperature.setBounds(10, 10, 270, 30);

        btnT1Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT1Get.setText("req");
        getContentPane().add(btnT1Get);
        btnT1Get.setBounds(20, 50, 60, 30);

        edtT1Show.setEditable(false);
        edtT1Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT1Show.setText("-50");
        getContentPane().add(edtT1Show);
        edtT1Show.setBounds(90, 50, 60, 30);

        edtT1Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT1Edit);
        edtT1Edit.setBounds(160, 50, 60, 30);

        btnT1Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT1Set.setText("set");
        getContentPane().add(btnT1Set);
        btnT1Set.setBounds(230, 50, 60, 30);

        btnT2Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT2Get.setText("req");
        getContentPane().add(btnT2Get);
        btnT2Get.setBounds(20, 80, 60, 30);

        edtT2Show.setEditable(false);
        edtT2Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT2Show.setText("-40");
        getContentPane().add(edtT2Show);
        edtT2Show.setBounds(90, 80, 60, 30);

        edtT2Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT2Edit);
        edtT2Edit.setBounds(160, 80, 60, 30);

        btnT2Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT2Set.setText("set");
        getContentPane().add(btnT2Set);
        btnT2Set.setBounds(230, 80, 60, 30);

        btnT3Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT3Get.setText("req");
        getContentPane().add(btnT3Get);
        btnT3Get.setBounds(20, 110, 60, 30);

        edtT3Show.setEditable(false);
        edtT3Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT3Show.setText("-30");
        getContentPane().add(edtT3Show);
        edtT3Show.setBounds(90, 110, 60, 30);

        edtT3Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT3Edit);
        edtT3Edit.setBounds(160, 110, 60, 30);

        btnT3Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT3Set.setText("set");
        getContentPane().add(btnT3Set);
        btnT3Set.setBounds(230, 110, 60, 30);

        btnT4Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT4Get.setText("req");
        getContentPane().add(btnT4Get);
        btnT4Get.setBounds(20, 140, 60, 30);

        edtT4Show.setEditable(false);
        edtT4Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT4Show.setText("-20");
        getContentPane().add(edtT4Show);
        edtT4Show.setBounds(90, 140, 60, 30);

        edtT4Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT4Edit);
        edtT4Edit.setBounds(160, 140, 60, 30);

        btnT4Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT4Set.setText("set");
        getContentPane().add(btnT4Set);
        btnT4Set.setBounds(230, 140, 60, 30);

        btnT5Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT5Get.setText("req");
        getContentPane().add(btnT5Get);
        btnT5Get.setBounds(20, 170, 60, 30);

        edtT5Show.setEditable(false);
        edtT5Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT5Show.setText("-10");
        getContentPane().add(edtT5Show);
        edtT5Show.setBounds(90, 170, 60, 30);

        edtT5Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT5Edit);
        edtT5Edit.setBounds(160, 170, 60, 30);

        btnT5Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT5Set.setText("set");
        getContentPane().add(btnT5Set);
        btnT5Set.setBounds(230, 170, 60, 30);

        btnT6Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT6Get.setText("req");
        getContentPane().add(btnT6Get);
        btnT6Get.setBounds(20, 200, 60, 30);

        edtT6Show.setEditable(false);
        edtT6Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT6Show.setText("0");
        getContentPane().add(edtT6Show);
        edtT6Show.setBounds(90, 200, 60, 30);

        edtT6Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT6Edit);
        edtT6Edit.setBounds(160, 200, 60, 30);

        btnT6Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT6Set.setText("set");
        getContentPane().add(btnT6Set);
        btnT6Set.setBounds(230, 200, 60, 30);

        btnT7Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT7Get.setText("req");
        getContentPane().add(btnT7Get);
        btnT7Get.setBounds(20, 230, 60, 30);

        edtT7Show.setEditable(false);
        edtT7Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT7Show.setText("10");
        getContentPane().add(edtT7Show);
        edtT7Show.setBounds(90, 230, 60, 30);

        edtT7Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT7Edit);
        edtT7Edit.setBounds(160, 230, 60, 30);

        btnT7Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT7Set.setText("set");
        getContentPane().add(btnT7Set);
        btnT7Set.setBounds(230, 230, 60, 30);

        btnT8Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT8Get.setText("req");
        getContentPane().add(btnT8Get);
        btnT8Get.setBounds(20, 260, 60, 30);

        edtT8Show.setEditable(false);
        edtT8Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT8Show.setText("20");
        getContentPane().add(edtT8Show);
        edtT8Show.setBounds(90, 260, 60, 30);

        edtT8Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT8Edit);
        edtT8Edit.setBounds(160, 260, 60, 30);

        btnT8Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT8Set.setText("set");
        getContentPane().add(btnT8Set);
        btnT8Set.setBounds(230, 260, 60, 30);

        btnT9Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT9Get.setText("req");
        getContentPane().add(btnT9Get);
        btnT9Get.setBounds(20, 290, 60, 30);

        edtT9Show.setEditable(false);
        edtT9Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT9Show.setText("30");
        getContentPane().add(edtT9Show);
        edtT9Show.setBounds(90, 290, 60, 30);

        edtT9Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT9Edit);
        edtT9Edit.setBounds(160, 290, 60, 30);

        btnT9Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT9Set.setText("set");
        getContentPane().add(btnT9Set);
        btnT9Set.setBounds(230, 290, 60, 30);

        btnT10Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT10Get.setText("req");
        getContentPane().add(btnT10Get);
        btnT10Get.setBounds(20, 320, 60, 30);

        edtT10Show.setEditable(false);
        edtT10Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT10Show.setText("40");
        getContentPane().add(edtT10Show);
        edtT10Show.setBounds(90, 320, 60, 30);

        edtT10Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT10Edit);
        edtT10Edit.setBounds(160, 320, 60, 30);

        btnT10Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT10Set.setText("set");
        getContentPane().add(btnT10Set);
        btnT10Set.setBounds(230, 320, 60, 30);

        btnT11Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT11Get.setText("req");
        getContentPane().add(btnT11Get);
        btnT11Get.setBounds(20, 350, 60, 30);

        edtT11Show.setEditable(false);
        edtT11Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT11Show.setText("50");
        getContentPane().add(edtT11Show);
        edtT11Show.setBounds(90, 350, 60, 30);

        edtT11Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT11Edit);
        edtT11Edit.setBounds(160, 350, 60, 30);

        btnT11Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT11Set.setText("set");
        getContentPane().add(btnT11Set);
        btnT11Set.setBounds(230, 350, 60, 30);

        lblPhaseShift.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPhaseShift.setText("<html><b><u>Фазовый сдвиг</b></u></html>");
        getContentPane().add(lblPhaseShift);
        lblPhaseShift.setBounds(290, 10, 270, 30);

        btnPS1Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS1Get.setText("req");
        getContentPane().add(btnPS1Get);
        btnPS1Get.setBounds(300, 50, 60, 30);

        edtPS1Show.setEditable(false);
        edtPS1Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS1Show);
        edtPS1Show.setBounds(370, 50, 60, 30);
        getContentPane().add(edtPS1Edit);
        edtPS1Edit.setBounds(440, 50, 60, 30);

        btnPS1Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS1Set.setText("set");
        getContentPane().add(btnPS1Set);
        btnPS1Set.setBounds(510, 50, 60, 30);

        btnPS2Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS2Get.setText("req");
        getContentPane().add(btnPS2Get);
        btnPS2Get.setBounds(300, 80, 60, 30);

        edtPS2Show.setEditable(false);
        edtPS2Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS2Show);
        edtPS2Show.setBounds(370, 80, 60, 30);
        getContentPane().add(edtPS2Edit);
        edtPS2Edit.setBounds(440, 80, 60, 30);

        btnPS2Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS2Set.setText("set");
        getContentPane().add(btnPS2Set);
        btnPS2Set.setBounds(510, 80, 60, 30);

        btnPS3Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS3Get.setText("req");
        getContentPane().add(btnPS3Get);
        btnPS3Get.setBounds(300, 110, 60, 30);

        edtPS3Show.setEditable(false);
        edtPS3Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS3Show);
        edtPS3Show.setBounds(370, 110, 60, 30);
        getContentPane().add(edtPS3Edit);
        edtPS3Edit.setBounds(440, 110, 60, 30);

        btnPS3Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS3Set.setText("set");
        getContentPane().add(btnPS3Set);
        btnPS3Set.setBounds(510, 110, 60, 30);

        btnPS4Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS4Get.setText("req");
        getContentPane().add(btnPS4Get);
        btnPS4Get.setBounds(300, 140, 60, 30);

        edtPS4Show.setEditable(false);
        edtPS4Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS4Show);
        edtPS4Show.setBounds(370, 140, 60, 30);
        getContentPane().add(edtPS4Edit);
        edtPS4Edit.setBounds(440, 140, 60, 30);

        btnPS4Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS4Set.setText("set");
        getContentPane().add(btnPS4Set);
        btnPS4Set.setBounds(510, 140, 60, 30);

        btnPS5Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS5Get.setText("req");
        getContentPane().add(btnPS5Get);
        btnPS5Get.setBounds(300, 170, 60, 30);

        edtPS5Show.setEditable(false);
        edtPS5Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS5Show);
        edtPS5Show.setBounds(370, 170, 60, 30);
        getContentPane().add(edtPS5Edit);
        edtPS5Edit.setBounds(440, 170, 60, 30);

        btnPS5Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS5Set.setText("set");
        getContentPane().add(btnPS5Set);
        btnPS5Set.setBounds(510, 170, 60, 30);

        btnPS6Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS6Get.setText("req");
        getContentPane().add(btnPS6Get);
        btnPS6Get.setBounds(300, 200, 60, 30);

        edtPS6Show.setEditable(false);
        edtPS6Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS6Show);
        edtPS6Show.setBounds(370, 200, 60, 30);
        getContentPane().add(edtPS6Edit);
        edtPS6Edit.setBounds(440, 200, 60, 30);

        btnPS6Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS6Set.setText("set");
        getContentPane().add(btnPS6Set);
        btnPS6Set.setBounds(510, 200, 60, 30);

        btnPS7Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS7Get.setText("req");
        getContentPane().add(btnPS7Get);
        btnPS7Get.setBounds(300, 230, 60, 30);

        edtPS7Show.setEditable(false);
        edtPS7Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS7Show);
        edtPS7Show.setBounds(370, 230, 60, 30);
        getContentPane().add(edtPS7Edit);
        edtPS7Edit.setBounds(440, 230, 60, 30);

        btnPS7Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS7Set.setText("set");
        getContentPane().add(btnPS7Set);
        btnPS7Set.setBounds(510, 230, 60, 30);

        btnPS8Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS8Get.setText("req");
        getContentPane().add(btnPS8Get);
        btnPS8Get.setBounds(300, 260, 60, 30);

        edtPS8Show.setEditable(false);
        edtPS8Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS8Show);
        edtPS8Show.setBounds(370, 260, 60, 30);
        getContentPane().add(edtPS8Edit);
        edtPS8Edit.setBounds(440, 260, 60, 30);

        btnPS8Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS8Set.setText("set");
        getContentPane().add(btnPS8Set);
        btnPS8Set.setBounds(510, 260, 60, 30);

        btnPS9Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS9Get.setText("req");
        getContentPane().add(btnPS9Get);
        btnPS9Get.setBounds(300, 290, 60, 30);

        edtPS9Show.setEditable(false);
        edtPS9Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS9Show);
        edtPS9Show.setBounds(370, 290, 60, 30);
        getContentPane().add(edtPS9Edit);
        edtPS9Edit.setBounds(440, 290, 60, 30);

        btnPS9Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS9Set.setText("set");
        getContentPane().add(btnPS9Set);
        btnPS9Set.setBounds(510, 290, 60, 30);

        btnPS10Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS10Get.setText("req");
        getContentPane().add(btnPS10Get);
        btnPS10Get.setBounds(300, 320, 60, 30);

        edtPS10Show.setEditable(false);
        edtPS10Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS10Show);
        edtPS10Show.setBounds(370, 320, 60, 30);
        getContentPane().add(edtPS10Edit);
        edtPS10Edit.setBounds(440, 320, 60, 30);

        btnPS10Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS10Set.setText("set");
        getContentPane().add(btnPS10Set);
        btnPS10Set.setBounds(510, 320, 60, 30);

        btnPS11Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS11Get.setText("req");
        getContentPane().add(btnPS11Get);
        btnPS11Get.setBounds(300, 350, 60, 30);

        edtPS11Show.setEditable(false);
        edtPS11Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS11Show);
        edtPS11Show.setBounds(370, 350, 60, 30);
        getContentPane().add(edtPS11Edit);
        edtPS11Edit.setBounds(440, 350, 60, 30);

        btnPS11Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS11Set.setText("set");
        getContentPane().add(btnPS11Set);
        btnPS11Set.setBounds(510, 350, 60, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        if( theApp.m_bConnected == true) {
            theApp.m_bConnected = false;
        }
        else {
            String strPort = edtPort.getText();
            //if( strPort.isEmpty())
            theApp.m_bConnected = true;
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        if( tRefreshStates != null) { tRefreshStates.stop(); tRefreshStates = null; }
        if( tRefreshValues != null) { tRefreshValues.stop(); tRefreshValues = null; }
        dispose();
    }//GEN-LAST:event_btnExitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnConnect;
    public javax.swing.JButton btnExit;
    public javax.swing.JButton btnPS10Get;
    public javax.swing.JButton btnPS10Set;
    public javax.swing.JButton btnPS11Get;
    public javax.swing.JButton btnPS11Set;
    public javax.swing.JButton btnPS1Get;
    public javax.swing.JButton btnPS1Set;
    public javax.swing.JButton btnPS2Get;
    public javax.swing.JButton btnPS2Set;
    public javax.swing.JButton btnPS3Get;
    public javax.swing.JButton btnPS3Set;
    public javax.swing.JButton btnPS4Get;
    public javax.swing.JButton btnPS4Set;
    public javax.swing.JButton btnPS5Get;
    public javax.swing.JButton btnPS5Set;
    public javax.swing.JButton btnPS6Get;
    public javax.swing.JButton btnPS6Set;
    public javax.swing.JButton btnPS7Get;
    public javax.swing.JButton btnPS7Set;
    public javax.swing.JButton btnPS8Get;
    public javax.swing.JButton btnPS8Set;
    public javax.swing.JButton btnPS9Get;
    public javax.swing.JButton btnPS9Set;
    public javax.swing.JButton btnT10Get;
    public javax.swing.JButton btnT10Set;
    public javax.swing.JButton btnT11Get;
    public javax.swing.JButton btnT11Set;
    public javax.swing.JButton btnT1Get;
    public javax.swing.JButton btnT1Set;
    public javax.swing.JButton btnT2Get;
    public javax.swing.JButton btnT2Set;
    public javax.swing.JButton btnT3Get;
    public javax.swing.JButton btnT3Set;
    public javax.swing.JButton btnT4Get;
    public javax.swing.JButton btnT4Set;
    public javax.swing.JButton btnT5Get;
    public javax.swing.JButton btnT5Set;
    public javax.swing.JButton btnT6Get;
    public javax.swing.JButton btnT6Set;
    public javax.swing.JButton btnT7Get;
    public javax.swing.JButton btnT7Set;
    public javax.swing.JButton btnT8Get;
    public javax.swing.JButton btnT8Set;
    public javax.swing.JButton btnT9Get;
    public javax.swing.JButton btnT9Set;
    private javax.swing.JTextField edtPS10Edit;
    private javax.swing.JTextField edtPS10Show;
    private javax.swing.JTextField edtPS11Edit;
    private javax.swing.JTextField edtPS11Show;
    private javax.swing.JTextField edtPS1Edit;
    private javax.swing.JTextField edtPS1Show;
    private javax.swing.JTextField edtPS2Edit;
    private javax.swing.JTextField edtPS2Show;
    private javax.swing.JTextField edtPS3Edit;
    private javax.swing.JTextField edtPS3Show;
    private javax.swing.JTextField edtPS4Edit;
    private javax.swing.JTextField edtPS4Show;
    private javax.swing.JTextField edtPS5Edit;
    private javax.swing.JTextField edtPS5Show;
    private javax.swing.JTextField edtPS6Edit;
    private javax.swing.JTextField edtPS6Show;
    private javax.swing.JTextField edtPS7Edit;
    private javax.swing.JTextField edtPS7Show;
    private javax.swing.JTextField edtPS8Edit;
    private javax.swing.JTextField edtPS8Show;
    private javax.swing.JTextField edtPS9Edit;
    private javax.swing.JTextField edtPS9Show;
    private javax.swing.JTextField edtPort;
    private javax.swing.JTextField edtT10Edit;
    private javax.swing.JTextField edtT10Show;
    private javax.swing.JTextField edtT11Edit;
    private javax.swing.JTextField edtT11Show;
    private javax.swing.JTextField edtT1Edit;
    private javax.swing.JTextField edtT1Show;
    private javax.swing.JTextField edtT2Edit;
    private javax.swing.JTextField edtT2Show;
    private javax.swing.JTextField edtT3Edit;
    private javax.swing.JTextField edtT3Show;
    private javax.swing.JTextField edtT4Edit;
    private javax.swing.JTextField edtT4Show;
    private javax.swing.JTextField edtT5Edit;
    private javax.swing.JTextField edtT5Show;
    private javax.swing.JTextField edtT6Edit;
    private javax.swing.JTextField edtT6Show;
    private javax.swing.JTextField edtT7Edit;
    private javax.swing.JTextField edtT7Show;
    private javax.swing.JTextField edtT8Edit;
    private javax.swing.JTextField edtT8Show;
    private javax.swing.JTextField edtT9Edit;
    private javax.swing.JTextField edtT9Show;
    private javax.swing.JLabel lblPacksCounter;
    private javax.swing.JLabel lblPhaseShift;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblTemperature;
    // End of variables declaration//GEN-END:variables
}
