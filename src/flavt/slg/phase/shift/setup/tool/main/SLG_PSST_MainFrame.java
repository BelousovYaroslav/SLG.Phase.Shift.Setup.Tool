/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flavt.slg.phase.shift.setup.tool.main;

import flavt.slg.lib.constants.SLG_ConstantsCmd;
import flavt.slg.lib.constants.SLG_ConstantsParams;
import flavt.slg.phase.shift.setup.tool.communication.SLG_PSST_CircleBuffer;
import flavt.slg.phase.shift.setup.tool.communication.SLG_PSST_StreamProcessingThread;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.log4j.Logger;

/**
 *
 * @author yaroslav
 */
public class SLG_PSST_MainFrame extends javax.swing.JFrame {
    static Logger logger = Logger.getLogger( SLG_PSST_MainFrame.class);
    private final SLG_PSST_App theApp;
    
    Timer tRefreshStates;
    Timer tRefreshValues;
    Timer tPolling;
    
    public String m_strPort;
    public static SerialPort serialPort;
    PortReader m_evListener;
    
    LinkedList m_lstRequestedParams;
    Iterator m_itRequestedParams;
    SLG_PSST_StreamProcessingThread thrProcessorRunnable;
    Thread thrProcessorThread;
    
    /**
     * Creates new form MainFrame
     */
    public SLG_PSST_MainFrame( SLG_PSST_App app) {
        
        class ReqItem {
            private final byte m_nParamIndex;
            private final byte m_nParamSubIndex;
            
            public ReqItem( byte Indx, byte SubIndx) {
                m_nParamIndex = Indx;
                m_nParamSubIndex = SubIndx;
            }
        }
        
        theApp = app;
        initComponents();
        
        m_lstRequestedParams = new LinkedList();
        m_lstRequestedParams.add( new ReqItem( ( byte) SLG_ConstantsParams.SLG_PARAM_PH_SH_USAGE, ( byte) 0) );
        m_lstRequestedParams.add( new ReqItem( ( byte) SLG_ConstantsParams.SLG_PARAM_PH_SH_CURRENT_VAL, ( byte) 0));
        for( int i=0; i < theApp.LIST_PARAMS_LEN; i++) {
            m_lstRequestedParams.add( new ReqItem( ( byte) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) i));
            m_lstRequestedParams.add( new ReqItem( ( byte) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) i));
        }
        m_itRequestedParams = m_lstRequestedParams.iterator();

        edtComPortValue.setText( theApp.GetSettings().GetComPort());
        
        theApp.m_bfCircleBuffer= new SLG_PSST_CircleBuffer();
        
        thrProcessorRunnable = new SLG_PSST_StreamProcessingThread( theApp);
        thrProcessorThread = new Thread( thrProcessorRunnable);
        thrProcessorThread.start();
        
        
        tRefreshStates = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e) {
                
                btnConnect.setEnabled( !theApp.m_bConnected);
                btnDisconnect.setEnabled( theApp.m_bConnected);
                
                
                boolean bAllDefined = true;
                for( int i = 0; i < theApp.LIST_PARAMS_LEN; bAllDefined = bAllDefined & theApp.m_bParamTDefined[i] & theApp.m_bParamPhshDefined[i++]);
                
                
                btnPhShRecalcCalib.setEnabled( theApp.m_bConnected && bAllDefined);
                btnResetCalibData.setEnabled( theApp.m_bConnected && bAllDefined);
                btnSaveData.setEnabled( theApp.m_bConnected && bAllDefined);
                
                btnCurrentPhaseShiftGet.setEnabled( theApp.m_bConnected && bAllDefined);
                btnCurrentPhaseShiftSetDec.setEnabled( theApp.m_bConnected && bAllDefined);
                btnCurrentPhaseShiftSetHex.setEnabled( theApp.m_bConnected && bAllDefined);
                
                JButton btnsTGet[] = { btnT1Get, btnT2Get, btnT3Get, btnT4Get,
                                       btnT5Get, btnT6Get, btnT7Get, btnT8Get,
                                       btnT9Get, btnT10Get, btnT11Get};
                
                JButton btnsTSet[] = { btnT1Set, btnT2Set, btnT3Set, btnT4Set,
                                       btnT5Set, btnT6Set, btnT7Set, btnT8Set,
                                       btnT9Set, btnT10Set, btnT11Set};
                
                JButton btnsPhshGet[] = { btnPS1Get, btnPS2Get, btnPS3Get, btnPS4Get,
                                          btnPS5Get, btnPS6Get, btnPS7Get, btnPS8Get,
                                          btnPS9Get, btnPS10Get, btnPS11Get};
                
                JButton btnsPhshSet[] = { btnPS1Set, btnPS2Set, btnPS3Set, btnPS4Set,
                                          btnPS5Set, btnPS6Set, btnPS7Set, btnPS8Set,
                                          btnPS9Set, btnPS10Set, btnPS11Set};
                        
                for( int i=0; i<11; i++) {
                    btnsTGet[i].setEnabled( theApp.m_bConnected && bAllDefined);
                    btnsTSet[i].setEnabled( theApp.m_bConnected && bAllDefined && theApp.m_nPhShUsage != SLG_PSST_App.PHASE_SHIFT_USAGE_CALIB);
                    btnsPhshGet[i].setEnabled( theApp.m_bConnected && bAllDefined);
                    btnsPhshSet[i].setEnabled( theApp.m_bConnected && bAllDefined && theApp.m_nPhShUsage != SLG_PSST_App.PHASE_SHIFT_USAGE_CALIB);
                }

            }
        });
        tRefreshStates.start();
        
        tRefreshValues = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if( theApp.m_bConnected) {
                    String strStatus = "";
                    
                    if( !theApp.m_strVersion.isEmpty())
                        strStatus = "  Версия ПО прибора = " + theApp.m_strVersion;
                    
                    strStatus +=
                            String.format( "  MF:%d CF:%d CSF:%d PC:%d",
                                    theApp.m_nMarkerFails,
                                    theApp.m_nCounterFails,
                                    theApp.m_nCheckSummFails,
                                    theApp.m_nPacksCounter);
                    
                    
                    
                    lblConnectionStateValue.setText( strStatus);
                }
                else {
                    lblConnectionStateValue.setText( "  Нет соединения");
                }
                    
                if( theApp.m_bConnected) {
                    switch( theApp.m_nPhShUsage) {
                        case SLG_PSST_App.PHASE_SHIFT_USAGE_CALIB:
                            lblPhaseShiftUsageValue.setText( "Калибровка"); break;
                        case SLG_PSST_App.PHASE_SHIFT_USAGE_MANUAL:
                            lblPhaseShiftUsageValue.setText( "Ручной"); break;
                        case SLG_PSST_App.PHASE_SHIFT_USAGE_UNKNOWN:
                            lblPhaseShiftUsageValue.setText( "НЕИЗВ"); break;
                        default:
                            logger.warn( "Странное значение использования фазового сдвига!");
                            lblPhaseShiftUsageValue.setText( "????"); break;
                
                    }
                }
                else {
                    lblPhaseShiftUsageValue.setText( "XXX");
                }
                
                if( theApp.m_bConnected) {
                    edtCurrentPhaseShiftHexValue.setText( String.format( "0x%02X", theApp.m_nCurrentPhaseShift));
                    edtCurrentPhaseShiftDecValue.setText( String.format( "%d", theApp.m_nCurrentPhaseShift));
                }
                else {
                    edtCurrentPhaseShiftHexValue.setText( "XXX");
                    edtCurrentPhaseShiftDecValue.setText( "XXX");
                }
                
                if( theApp.m_bConnected) {
                    lblCurrentTD1Value.setText( String.format( "%.2f", theApp.m_dblTD1));
                }
                else {
                    lblCurrentTD1Value.setText( "XXX");
                }
                
                JTextField edtsT[] =  { edtT1Show, edtT2Show, edtT3Show, edtT4Show, edtT5Show,
                                        edtT6Show, edtT7Show, edtT8Show, edtT9Show, edtT10Show,
                                        edtT11Show };
                
                JTextField edtsPS[] = { edtPS1Show, edtPS2Show, edtPS3Show, edtPS4Show, edtPS5Show,
                                        edtPS6Show, edtPS7Show, edtPS8Show, edtPS9Show, edtPS10Show,
                                        edtPS11Show };
                    
                for( int i = 0; i < theApp.LIST_PARAMS_LEN; i++) {
                    //T
                    if( theApp.m_bParamTDefined[i] == true) {
                        if( theApp.m_DevT[i] == 0xFFFF)
                            edtsT[i].setText( "---");
                        else
                            edtsT[i].setText( String.format( "%d", theApp.m_DevT[ i]));
                            
                    }
                    else
                        edtsT[i].setText( "???");
                    
                    
                    //Phsh
                    if( theApp.m_bParamPhshDefined[i] == true) {
                        if( theApp.m_DevPhsh[i] == 0xFFFF)
                            edtsPS[i].setText( "---");
                        else
                            edtsPS[i].setText( String.format( "%d", theApp.m_DevPhsh[ i]));
                            
                    }
                    else
                        edtsPS[i].setText( "???");
                }
            }
            
            
        });
        tRefreshValues.start();
        
        tPolling = new Timer( 200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if( theApp.m_bConnected && serialPort != null && serialPort.isOpened()) {
                    
                    if( theApp.m_strVersion.isEmpty()) {
                        SendComandRequestParam( ( byte) SLG_ConstantsParams.SLG_PARAM_VERSION, ( byte) 0);
                    }
                    else {
                        if( m_itRequestedParams.hasNext() == false)
                            m_itRequestedParams = m_lstRequestedParams.iterator();
                        
                        ReqItem item = ( ReqItem) m_itRequestedParams.next();
                        SendComandRequestParam( item.m_nParamIndex, item.m_nParamSubIndex);
                    }
                }
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
        edtComPortValue = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        btnDisconnect = new javax.swing.JButton();
        lblConnectionStateTitle = new javax.swing.JLabel();
        lblConnectionStateValue = new javax.swing.JLabel();
        lblPhaseShiftUsageTitle = new javax.swing.JLabel();
        lblPhaseShiftUsageValue = new javax.swing.JLabel();
        btnPhShRecalcCalib = new javax.swing.JButton();
        lblCurrentPhaseShiftTitle = new javax.swing.JLabel();
        btnCurrentPhaseShiftGet = new javax.swing.JButton();
        edtCurrentPhaseShiftHexValue = new javax.swing.JTextField();
        edtCurrentPhaseShiftHexValueToSet = new javax.swing.JTextField();
        btnCurrentPhaseShiftSetDec = new javax.swing.JButton();
        lblCurrentTD1Title = new javax.swing.JLabel();
        lblCurrentTD1Value = new javax.swing.JLabel();
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
        btnResetCalibData = new javax.swing.JButton();
        btnSaveData = new javax.swing.JButton();
        btnPhShRecalcOff = new javax.swing.JButton();
        edtCurrentPhaseShiftDecValue = new javax.swing.JTextField();
        edtCurrentPhaseShiftDecValueToSet = new javax.swing.JTextField();
        btnCurrentPhaseShiftSetHex = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("МЛГ3Б. Утилита для редактирования калибровки фазового сдвига  (С) ФЛАВТ   2017.10.23 14:30");
        setMinimumSize(new java.awt.Dimension(580, 770));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        lblPort.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPort.setText("<html><b><u>Порт</b></u></html>");
        getContentPane().add(lblPort);
        lblPort.setBounds(20, 10, 50, 30);
        getContentPane().add(edtComPortValue);
        edtComPortValue.setBounds(70, 10, 220, 30);

        btnConnect.setText("Соединить");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        getContentPane().add(btnConnect);
        btnConnect.setBounds(300, 10, 130, 30);

        btnDisconnect.setText("Разъединить");
        btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisconnectActionPerformed(evt);
            }
        });
        getContentPane().add(btnDisconnect);
        btnDisconnect.setBounds(440, 10, 130, 30);

        lblConnectionStateTitle.setText("Состояние связи:");
        getContentPane().add(lblConnectionStateTitle);
        lblConnectionStateTitle.setBounds(20, 50, 130, 30);

        lblConnectionStateValue.setText("jLabel2");
        lblConnectionStateValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblConnectionStateValue);
        lblConnectionStateValue.setBounds(150, 50, 420, 30);

        lblPhaseShiftUsageTitle.setText("Перевычисление фазового сдвига: ");
        getContentPane().add(lblPhaseShiftUsageTitle);
        lblPhaseShiftUsageTitle.setBounds(20, 90, 260, 30);

        lblPhaseShiftUsageValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPhaseShiftUsageValue.setText("???");
        lblPhaseShiftUsageValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblPhaseShiftUsageValue);
        lblPhaseShiftUsageValue.setBounds(290, 90, 280, 30);

        btnPhShRecalcCalib.setText("Калибровка");
        btnPhShRecalcCalib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPhShRecalcCalibActionPerformed(evt);
            }
        });
        getContentPane().add(btnPhShRecalcCalib);
        btnPhShRecalcCalib.setBounds(300, 130, 130, 30);

        lblCurrentPhaseShiftTitle.setText("Текущее (последнее выставленное) значение фазового сдвига: ");
        getContentPane().add(lblCurrentPhaseShiftTitle);
        lblCurrentPhaseShiftTitle.setBounds(20, 160, 470, 30);

        btnCurrentPhaseShiftGet.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnCurrentPhaseShiftGet.setText("req");
        btnCurrentPhaseShiftGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrentPhaseShiftGetActionPerformed(evt);
            }
        });
        getContentPane().add(btnCurrentPhaseShiftGet);
        btnCurrentPhaseShiftGet.setBounds(250, 190, 60, 60);

        edtCurrentPhaseShiftHexValue.setEditable(false);
        edtCurrentPhaseShiftHexValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtCurrentPhaseShiftHexValue.setEnabled(false);
        getContentPane().add(edtCurrentPhaseShiftHexValue);
        edtCurrentPhaseShiftHexValue.setBounds(370, 190, 60, 30);

        edtCurrentPhaseShiftHexValueToSet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtCurrentPhaseShiftHexValueToSet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                edtCurrentPhaseShiftHexValueToSetFocusLost(evt);
            }
        });
        getContentPane().add(edtCurrentPhaseShiftHexValueToSet);
        edtCurrentPhaseShiftHexValueToSet.setBounds(440, 190, 60, 30);

        btnCurrentPhaseShiftSetDec.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnCurrentPhaseShiftSetDec.setText("set");
        btnCurrentPhaseShiftSetDec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrentPhaseShiftSetDecActionPerformed(evt);
            }
        });
        getContentPane().add(btnCurrentPhaseShiftSetDec);
        btnCurrentPhaseShiftSetDec.setBounds(510, 220, 60, 30);

        lblCurrentTD1Title.setText("Текущая температура (TD1):");
        getContentPane().add(lblCurrentTD1Title);
        lblCurrentTD1Title.setBounds(20, 270, 210, 30);

        lblCurrentTD1Value.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCurrentTD1Value.setText("???");
        lblCurrentTD1Value.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblCurrentTD1Value);
        lblCurrentTD1Value.setBounds(230, 270, 60, 30);

        lblTemperature.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTemperature.setText("<html><b><u>Температура</b></u></html>");
        getContentPane().add(lblTemperature);
        lblTemperature.setBounds(20, 300, 270, 30);

        btnT1Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT1Get.setText("req");
        btnT1Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT1GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT1Get);
        btnT1Get.setBounds(20, 330, 60, 30);

        edtT1Show.setEditable(false);
        edtT1Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT1Show.setEnabled(false);
        getContentPane().add(edtT1Show);
        edtT1Show.setBounds(90, 330, 60, 30);

        edtT1Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT1Edit);
        edtT1Edit.setBounds(160, 330, 60, 30);

        btnT1Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT1Set.setText("set");
        btnT1Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT1SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT1Set);
        btnT1Set.setBounds(230, 330, 60, 30);

        btnT2Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT2Get.setText("req");
        btnT2Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT2GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT2Get);
        btnT2Get.setBounds(20, 360, 60, 30);

        edtT2Show.setEditable(false);
        edtT2Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT2Show.setEnabled(false);
        getContentPane().add(edtT2Show);
        edtT2Show.setBounds(90, 360, 60, 30);

        edtT2Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT2Edit);
        edtT2Edit.setBounds(160, 360, 60, 30);

        btnT2Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT2Set.setText("set");
        btnT2Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT2SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT2Set);
        btnT2Set.setBounds(230, 360, 60, 30);

        btnT3Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT3Get.setText("req");
        btnT3Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT3GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT3Get);
        btnT3Get.setBounds(20, 390, 60, 30);

        edtT3Show.setEditable(false);
        edtT3Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT3Show.setEnabled(false);
        getContentPane().add(edtT3Show);
        edtT3Show.setBounds(90, 390, 60, 30);

        edtT3Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT3Edit);
        edtT3Edit.setBounds(160, 390, 60, 30);

        btnT3Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT3Set.setText("set");
        btnT3Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT3SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT3Set);
        btnT3Set.setBounds(230, 390, 60, 30);

        btnT4Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT4Get.setText("req");
        btnT4Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT4GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT4Get);
        btnT4Get.setBounds(20, 420, 60, 30);

        edtT4Show.setEditable(false);
        edtT4Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT4Show.setEnabled(false);
        getContentPane().add(edtT4Show);
        edtT4Show.setBounds(90, 420, 60, 30);

        edtT4Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT4Edit);
        edtT4Edit.setBounds(160, 420, 60, 30);

        btnT4Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT4Set.setText("set");
        btnT4Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT4SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT4Set);
        btnT4Set.setBounds(230, 420, 60, 30);

        btnT5Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT5Get.setText("req");
        btnT5Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT5GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT5Get);
        btnT5Get.setBounds(20, 450, 60, 30);

        edtT5Show.setEditable(false);
        edtT5Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT5Show.setEnabled(false);
        getContentPane().add(edtT5Show);
        edtT5Show.setBounds(90, 450, 60, 30);

        edtT5Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT5Edit);
        edtT5Edit.setBounds(160, 450, 60, 30);

        btnT5Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT5Set.setText("set");
        btnT5Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT5SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT5Set);
        btnT5Set.setBounds(230, 450, 60, 30);

        btnT6Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT6Get.setText("req");
        btnT6Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT6GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT6Get);
        btnT6Get.setBounds(20, 480, 60, 30);

        edtT6Show.setEditable(false);
        edtT6Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT6Show.setEnabled(false);
        getContentPane().add(edtT6Show);
        edtT6Show.setBounds(90, 480, 60, 30);

        edtT6Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT6Edit);
        edtT6Edit.setBounds(160, 480, 60, 30);

        btnT6Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT6Set.setText("set");
        btnT6Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT6SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT6Set);
        btnT6Set.setBounds(230, 480, 60, 30);

        btnT7Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT7Get.setText("req");
        btnT7Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT7GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT7Get);
        btnT7Get.setBounds(20, 510, 60, 30);

        edtT7Show.setEditable(false);
        edtT7Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT7Show.setEnabled(false);
        getContentPane().add(edtT7Show);
        edtT7Show.setBounds(90, 510, 60, 30);

        edtT7Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT7Edit);
        edtT7Edit.setBounds(160, 510, 60, 30);

        btnT7Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT7Set.setText("set");
        btnT7Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT7SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT7Set);
        btnT7Set.setBounds(230, 510, 60, 30);

        btnT8Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT8Get.setText("req");
        btnT8Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT8GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT8Get);
        btnT8Get.setBounds(20, 540, 60, 30);

        edtT8Show.setEditable(false);
        edtT8Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT8Show.setEnabled(false);
        getContentPane().add(edtT8Show);
        edtT8Show.setBounds(90, 540, 60, 30);

        edtT8Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT8Edit);
        edtT8Edit.setBounds(160, 540, 60, 30);

        btnT8Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT8Set.setText("set");
        btnT8Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT8SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT8Set);
        btnT8Set.setBounds(230, 540, 60, 30);

        btnT9Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT9Get.setText("req");
        btnT9Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT9GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT9Get);
        btnT9Get.setBounds(20, 570, 60, 30);

        edtT9Show.setEditable(false);
        edtT9Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT9Show.setEnabled(false);
        getContentPane().add(edtT9Show);
        edtT9Show.setBounds(90, 570, 60, 30);

        edtT9Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT9Edit);
        edtT9Edit.setBounds(160, 570, 60, 30);

        btnT9Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT9Set.setText("set");
        btnT9Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT9SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT9Set);
        btnT9Set.setBounds(230, 570, 60, 30);

        btnT10Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT10Get.setText("req");
        btnT10Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT10GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT10Get);
        btnT10Get.setBounds(20, 600, 60, 30);

        edtT10Show.setEditable(false);
        edtT10Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT10Show.setEnabled(false);
        getContentPane().add(edtT10Show);
        edtT10Show.setBounds(90, 600, 60, 30);

        edtT10Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT10Edit);
        edtT10Edit.setBounds(160, 600, 60, 30);

        btnT10Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT10Set.setText("set");
        btnT10Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT10SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT10Set);
        btnT10Set.setBounds(230, 600, 60, 30);

        btnT11Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT11Get.setText("req");
        btnT11Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT11GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT11Get);
        btnT11Get.setBounds(20, 630, 60, 30);

        edtT11Show.setEditable(false);
        edtT11Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtT11Show.setEnabled(false);
        getContentPane().add(edtT11Show);
        edtT11Show.setBounds(90, 630, 60, 30);

        edtT11Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtT11Edit);
        edtT11Edit.setBounds(160, 630, 60, 30);

        btnT11Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnT11Set.setText("set");
        btnT11Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnT11SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnT11Set);
        btnT11Set.setBounds(230, 630, 60, 30);

        lblPhaseShift.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPhaseShift.setText("<html><b><u>Фазовый сдвиг</b></u></html>");
        getContentPane().add(lblPhaseShift);
        lblPhaseShift.setBounds(300, 300, 270, 30);

        btnPS1Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS1Get.setText("req");
        btnPS1Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS1GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS1Get);
        btnPS1Get.setBounds(300, 330, 60, 30);

        edtPS1Show.setEditable(false);
        edtPS1Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS1Show.setEnabled(false);
        getContentPane().add(edtPS1Show);
        edtPS1Show.setBounds(370, 330, 60, 30);

        edtPS1Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS1Edit);
        edtPS1Edit.setBounds(440, 330, 60, 30);

        btnPS1Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS1Set.setText("set");
        btnPS1Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS1SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS1Set);
        btnPS1Set.setBounds(510, 330, 60, 30);

        btnPS2Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS2Get.setText("req");
        btnPS2Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS2GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS2Get);
        btnPS2Get.setBounds(300, 360, 60, 30);

        edtPS2Show.setEditable(false);
        edtPS2Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS2Show.setEnabled(false);
        getContentPane().add(edtPS2Show);
        edtPS2Show.setBounds(370, 360, 60, 30);

        edtPS2Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS2Edit);
        edtPS2Edit.setBounds(440, 360, 60, 30);

        btnPS2Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS2Set.setText("set");
        btnPS2Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS2SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS2Set);
        btnPS2Set.setBounds(510, 360, 60, 30);

        btnPS3Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS3Get.setText("req");
        btnPS3Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS3GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS3Get);
        btnPS3Get.setBounds(300, 390, 60, 30);

        edtPS3Show.setEditable(false);
        edtPS3Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS3Show.setEnabled(false);
        getContentPane().add(edtPS3Show);
        edtPS3Show.setBounds(370, 390, 60, 30);

        edtPS3Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS3Edit);
        edtPS3Edit.setBounds(440, 390, 60, 30);

        btnPS3Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS3Set.setText("set");
        btnPS3Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS3SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS3Set);
        btnPS3Set.setBounds(510, 390, 60, 30);

        btnPS4Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS4Get.setText("req");
        btnPS4Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS4GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS4Get);
        btnPS4Get.setBounds(300, 420, 60, 30);

        edtPS4Show.setEditable(false);
        edtPS4Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS4Show.setEnabled(false);
        getContentPane().add(edtPS4Show);
        edtPS4Show.setBounds(370, 420, 60, 30);

        edtPS4Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS4Edit);
        edtPS4Edit.setBounds(440, 420, 60, 30);

        btnPS4Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS4Set.setText("set");
        btnPS4Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS4SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS4Set);
        btnPS4Set.setBounds(510, 420, 60, 30);

        btnPS5Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS5Get.setText("req");
        btnPS5Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS5GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS5Get);
        btnPS5Get.setBounds(300, 450, 60, 30);

        edtPS5Show.setEditable(false);
        edtPS5Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS5Show.setEnabled(false);
        getContentPane().add(edtPS5Show);
        edtPS5Show.setBounds(370, 450, 60, 30);

        edtPS5Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS5Edit);
        edtPS5Edit.setBounds(440, 450, 60, 30);

        btnPS5Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS5Set.setText("set");
        btnPS5Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS5SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS5Set);
        btnPS5Set.setBounds(510, 450, 60, 30);

        btnPS6Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS6Get.setText("req");
        btnPS6Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS6GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS6Get);
        btnPS6Get.setBounds(300, 480, 60, 30);

        edtPS6Show.setEditable(false);
        edtPS6Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS6Show.setEnabled(false);
        getContentPane().add(edtPS6Show);
        edtPS6Show.setBounds(370, 480, 60, 30);

        edtPS6Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS6Edit);
        edtPS6Edit.setBounds(440, 480, 60, 30);

        btnPS6Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS6Set.setText("set");
        btnPS6Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS6SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS6Set);
        btnPS6Set.setBounds(510, 480, 60, 30);

        btnPS7Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS7Get.setText("req");
        btnPS7Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS7GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS7Get);
        btnPS7Get.setBounds(300, 510, 60, 30);

        edtPS7Show.setEditable(false);
        edtPS7Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS7Show.setEnabled(false);
        getContentPane().add(edtPS7Show);
        edtPS7Show.setBounds(370, 510, 60, 30);

        edtPS7Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS7Edit);
        edtPS7Edit.setBounds(440, 510, 60, 30);

        btnPS7Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS7Set.setText("set");
        btnPS7Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS7SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS7Set);
        btnPS7Set.setBounds(510, 510, 60, 30);

        btnPS8Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS8Get.setText("req");
        btnPS8Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS8GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS8Get);
        btnPS8Get.setBounds(300, 540, 60, 30);

        edtPS8Show.setEditable(false);
        edtPS8Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS8Show.setEnabled(false);
        getContentPane().add(edtPS8Show);
        edtPS8Show.setBounds(370, 540, 60, 30);

        edtPS8Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS8Edit);
        edtPS8Edit.setBounds(440, 540, 60, 30);

        btnPS8Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS8Set.setText("set");
        btnPS8Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS8SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS8Set);
        btnPS8Set.setBounds(510, 540, 60, 30);

        btnPS9Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS9Get.setText("req");
        btnPS9Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS9GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS9Get);
        btnPS9Get.setBounds(300, 570, 60, 30);

        edtPS9Show.setEditable(false);
        edtPS9Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS9Show.setEnabled(false);
        getContentPane().add(edtPS9Show);
        edtPS9Show.setBounds(370, 570, 60, 30);

        edtPS9Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS9Edit);
        edtPS9Edit.setBounds(440, 570, 60, 30);

        btnPS9Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS9Set.setText("set");
        btnPS9Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS9SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS9Set);
        btnPS9Set.setBounds(510, 570, 60, 30);

        btnPS10Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS10Get.setText("req");
        btnPS10Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS10GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS10Get);
        btnPS10Get.setBounds(300, 600, 60, 30);

        edtPS10Show.setEditable(false);
        edtPS10Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS10Show.setEnabled(false);
        getContentPane().add(edtPS10Show);
        edtPS10Show.setBounds(370, 600, 60, 30);

        edtPS10Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS10Edit);
        edtPS10Edit.setBounds(440, 600, 60, 30);

        btnPS10Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS10Set.setText("set");
        btnPS10Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS10SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS10Set);
        btnPS10Set.setBounds(510, 600, 60, 30);

        btnPS11Get.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS11Get.setText("req");
        btnPS11Get.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS11GetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS11Get);
        btnPS11Get.setBounds(300, 630, 60, 30);

        edtPS11Show.setEditable(false);
        edtPS11Show.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtPS11Show.setEnabled(false);
        getContentPane().add(edtPS11Show);
        edtPS11Show.setBounds(370, 630, 60, 30);

        edtPS11Edit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(edtPS11Edit);
        edtPS11Edit.setBounds(440, 630, 60, 30);

        btnPS11Set.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnPS11Set.setText("set");
        btnPS11Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPS11SetActionPerformed(evt);
            }
        });
        getContentPane().add(btnPS11Set);
        btnPS11Set.setBounds(510, 630, 60, 30);

        btnResetCalibData.setText("Сбросить данные калибровки");
        btnResetCalibData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetCalibDataActionPerformed(evt);
            }
        });
        getContentPane().add(btnResetCalibData);
        btnResetCalibData.setBounds(20, 670, 550, 30);

        btnSaveData.setText("Сохранить данные калибровки в память МК");
        btnSaveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveDataActionPerformed(evt);
            }
        });
        getContentPane().add(btnSaveData);
        btnSaveData.setBounds(20, 710, 550, 30);

        btnPhShRecalcOff.setText("Ручной");
        btnPhShRecalcOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPhShRecalcOffActionPerformed(evt);
            }
        });
        getContentPane().add(btnPhShRecalcOff);
        btnPhShRecalcOff.setBounds(440, 130, 130, 30);

        edtCurrentPhaseShiftDecValue.setEditable(false);
        edtCurrentPhaseShiftDecValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtCurrentPhaseShiftDecValue.setEnabled(false);
        getContentPane().add(edtCurrentPhaseShiftDecValue);
        edtCurrentPhaseShiftDecValue.setBounds(370, 220, 60, 30);

        edtCurrentPhaseShiftDecValueToSet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        edtCurrentPhaseShiftDecValueToSet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                edtCurrentPhaseShiftDecValueToSetFocusLost(evt);
            }
        });
        getContentPane().add(edtCurrentPhaseShiftDecValueToSet);
        edtCurrentPhaseShiftDecValueToSet.setBounds(440, 220, 60, 30);

        btnCurrentPhaseShiftSetHex.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btnCurrentPhaseShiftSetHex.setText("set");
        btnCurrentPhaseShiftSetHex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrentPhaseShiftSetHexActionPerformed(evt);
            }
        });
        getContentPane().add(btnCurrentPhaseShiftSetHex);
        btnCurrentPhaseShiftSetHex.setBounds(510, 190, 60, 30);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DEC");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(320, 220, 40, 30);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("HEX");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(320, 190, 40, 30);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        for( int i=0; i<theApp.LIST_PARAMS_LEN; i++) {
            theApp.m_bParamTDefined[ i] = false;
            theApp.m_bParamPhshDefined[ i] = false;
        }
                        
        m_strPort = edtComPortValue.getText();
        if( m_strPort.isEmpty()) {
            logger.info( "Connect to no-port? Ha (3 times)");
            return;
        }
        
        theApp.m_bfCircleBuffer= new SLG_PSST_CircleBuffer();
        
        for( int i = 0; i < theApp.LIST_PARAMS_LEN; i++) {
            theApp.m_bParamTDefined[ i] = false;
            theApp.m_bParamPhshDefined[ i] = false;
        }
        
        serialPort = new SerialPort( m_strPort);
        try {
            //Открываем порт
            serialPort.openPort();

            //Выставляем параметры
            serialPort.setParams( 921600,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);

            //Включаем аппаратное управление потоком
            //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
            //                              SerialPort.FLOWCONTROL_RTSCTS_OUT);

            //Устанавливаем ивент лисенер и маску
            m_evListener = new PortReader();
            serialPort.addEventListener( m_evListener, SerialPort.MASK_RXCHAR);
        }
        catch( SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
            theApp.m_bConnected = false;
            SLG_PSST_App.MessageBoxError( "При попытке соединения получили исключительную ситуацию:\n\n" + ex.toString(), "SLG_PSST");
            return;
        }
        
        theApp.m_strVersion = "";
        theApp.m_bConnected = true;
        theApp.m_nPhShUsage = SLG_PSST_App.PHASE_SHIFT_USAGE_UNKNOWN;
        theApp.m_bParamsChanged = false;
        theApp.m_nCurrentPhaseShift = 0x00;
        theApp.m_dblTD1 = 0.;
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnResetCalibDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetCalibDataActionPerformed
        byte aBytes[] = new byte[4];
        aBytes[0] = SLG_ConstantsCmd.SLG_CMD_ACT_RESET_PHSH_CALIB;
        aBytes[1] = 0;
        aBytes[2] = 0;
        aBytes[3] = 0;
        
        try {
            serialPort.writeBytes( aBytes);
            theApp.m_bParamsChanged = true;
            logger.debug( ">> RESET PH_SH CALIB");
            logger.debug( String.format( ">> 0x%02x 0x%02x 0x%02x 0x%02x", aBytes[0], aBytes[1], aBytes[2], aBytes[3]));
        } catch (SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
            theApp.m_bConnected = false;
            SLG_PSST_App.MessageBoxError( "При попытке записи в порт получили исключительную ситуацию:\n\n" + ex.toString(), "SLG_PSST");
        }
    }//GEN-LAST:event_btnResetCalibDataActionPerformed

    private void btnPhShRecalcCalibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhShRecalcCalibActionPerformed
        SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_USAGE, ( byte) 0x00, ( byte) 0);
        theApp.m_bParamsChanged = true;
    }//GEN-LAST:event_btnPhShRecalcCalibActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
        if( theApp.m_bParamsChanged == true && theApp.m_bConnected == true) {
            int nRespond = SLG_PSST_App.MessageBoxYesNo( "Параметры были изменены, но не сохранены в память микроконтроллера!\nВыйти без сохранения?", "SLG_PSST");
            if( nRespond == JOptionPane.NO_OPTION) return;
        }
        
        if( tRefreshStates != null) { tRefreshStates.stop(); tRefreshStates = null; }
        if( tRefreshValues != null) { tRefreshValues.stop(); tRefreshValues = null; }
        if( tPolling != null)       { tPolling.stop();       tPolling = null; }
        
        theApp.m_bConnected = false;
        try {
            if( serialPort != null && serialPort.isOpened()) {
                serialPort.removeEventListener();
                serialPort.closePort();
            }
            
            thrProcessorRunnable.m_bStopThread = true;
            thrProcessorThread.join( 1000);
            if( thrProcessorThread.isAlive()) {
                logger.error( "Thread stopped, but alive!");
            }
        }
        catch( SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
        } catch (InterruptedException ex) {
            logger.error( "Processing thread join fails", ex);
        }
        
        String strComPort = edtComPortValue.getText();
        if( !strComPort.isEmpty()) {
            theApp.GetSettings().SetComPort( strComPort);
            theApp.GetSettings().SaveSettings();
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisconnectActionPerformed

        if( theApp.m_bParamsChanged == true) {
            int nRespond = SLG_PSST_App.MessageBoxYesNo( "Параметры были изменены, но не сохранены в память микроконтроллера!\nОтсоединиться без сохранения?", "SLG_PSST");
            if( nRespond == JOptionPane.NO_OPTION) return;
        }
        
        theApp.m_bConnected = false;
        try {
            serialPort.removeEventListener();
            serialPort.closePort();
            
            /*
            thrProcessorRunnable.m_bStopThread = true;
            thrProcessorThread.join( 1000);
            if( thrProcessorThread.isAlive()) {
                logger.error( "Thread stopped, but alive!");
            }*/
        }
        catch( SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
        }
        /*
        catch (InterruptedException ex) {
            logger.error( "Processing thread join fails", ex);
        }
        */
    }//GEN-LAST:event_btnDisconnectActionPerformed

    public void SendComandRequestParam( byte btParam, byte btParamIndex) {
        byte aBytes[] = new byte[4];
        aBytes[0] = SLG_ConstantsCmd.SLG_CMD_REQ;
        aBytes[1] = btParam;
        aBytes[2] = btParamIndex;
        aBytes[3] = 0;
        
        try {
            serialPort.writeBytes( aBytes);
            logger.debug( ">> REQ PARAM_" + btParam + "." + btParamIndex);
            logger.debug( String.format( ">> 0x%02X 0x%02X 0x%02X 0x%02X", aBytes[0], aBytes[1], aBytes[2], aBytes[3]));
        } catch (SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
            theApp.m_bConnected = false;
            SLG_PSST_App.MessageBoxError( "При попытке записи в порт получили исключительную ситуацию:\n\n" + ex.toString(), "SLG_PSST");
        }
    }
    
    public void SendComandSetParam( byte btParam, byte btParamIndex, byte btParamValue) {
        byte aBytes[] = new byte[4];
        aBytes[0] = SLG_ConstantsCmd.SLG_CMD_SET;
        aBytes[1] = btParam;
        aBytes[2] = btParamIndex;
        aBytes[3] = btParamValue;
        
        try {
            serialPort.writeBytes( aBytes);
            logger.debug( ">> SET PARAM_" + btParam + "." + btParamIndex + "=" + btParamValue);
            logger.debug( String.format( ">> 0x%02x 0x%02x 0x%02x 0x%02x", aBytes[0], aBytes[1], aBytes[2], aBytes[3]));
        } catch (SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
            theApp.m_bConnected = false;
            SLG_PSST_App.MessageBoxError( "При попытке записи в порт получили исключительную ситуацию:\n\n" + ex.toString(), "SLG_PSST");
        }
    }
    
    private void btnT1GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT1GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 0);
    }//GEN-LAST:event_btnT1GetActionPerformed

    private void btnT2GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT2GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 1);
    }//GEN-LAST:event_btnT2GetActionPerformed

    private void btnT3GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT3GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 2);
    }//GEN-LAST:event_btnT3GetActionPerformed

    private void btnT4GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT4GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 3);
    }//GEN-LAST:event_btnT4GetActionPerformed

    private void btnT5GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT5GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 4);
    }//GEN-LAST:event_btnT5GetActionPerformed

    private void btnT6GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT6GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 5);
    }//GEN-LAST:event_btnT6GetActionPerformed

    private void btnT7GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT7GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 6);
    }//GEN-LAST:event_btnT7GetActionPerformed

    private void btnT8GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT8GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 7);
    }//GEN-LAST:event_btnT8GetActionPerformed

    private void btnT9GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT9GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 8);
    }//GEN-LAST:event_btnT9GetActionPerformed

    private void btnT10GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT10GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 9);
    }//GEN-LAST:event_btnT10GetActionPerformed

    private void btnT11GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT11GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 10);
    }//GEN-LAST:event_btnT11GetActionPerformed

    private void btnPS1GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS1GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 0);
    }//GEN-LAST:event_btnPS1GetActionPerformed

    private void btnPS2GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS2GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 1);
    }//GEN-LAST:event_btnPS2GetActionPerformed

    private void btnPS3GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS3GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 2);
    }//GEN-LAST:event_btnPS3GetActionPerformed

    private void btnPS4GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS4GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 3);
    }//GEN-LAST:event_btnPS4GetActionPerformed

    private void btnPS5GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS5GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 4);
    }//GEN-LAST:event_btnPS5GetActionPerformed

    private void btnPS6GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS6GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 5);
    }//GEN-LAST:event_btnPS6GetActionPerformed

    private void btnPS7GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS7GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 6);
    }//GEN-LAST:event_btnPS7GetActionPerformed

    private void btnPS8GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS8GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 7);
    }//GEN-LAST:event_btnPS8GetActionPerformed

    private void btnPS9GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS9GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 8);
    }//GEN-LAST:event_btnPS9GetActionPerformed

    private void btnPS10GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS10GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 9);
    }//GEN-LAST:event_btnPS10GetActionPerformed

    private void btnPS11GetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS11GetActionPerformed
        SendComandRequestParam( ( byte )SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 10);
    }//GEN-LAST:event_btnPS11GetActionPerformed

    private void btnT1SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT1SetActionPerformed
        try {
            String strValue = edtT1Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 0, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру1\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT1SetActionPerformed

    private void btnT2SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT2SetActionPerformed
        try {
            String strValue = edtT2Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 1, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру2\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT2SetActionPerformed

    private void btnT3SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT3SetActionPerformed
        try {
            String strValue = edtT3Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 2, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру3\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT3SetActionPerformed

    private void btnT4SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT4SetActionPerformed
        try {
            String strValue = edtT4Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 3, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру4\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT4SetActionPerformed

    private void btnT5SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT5SetActionPerformed
        try {
            String strValue = edtT5Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 4, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }

        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру5\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT5SetActionPerformed

    private void btnT6SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT6SetActionPerformed
        try {
            String strValue = edtT6Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 5, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру6\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT6SetActionPerformed

    private void btnT7SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT7SetActionPerformed
        try {
            String strValue = edtT7Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 6, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру7\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT7SetActionPerformed

    private void btnT8SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT8SetActionPerformed
        try {
            String strValue = edtT8Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 7, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру8\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT8SetActionPerformed

    private void btnT9SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT9SetActionPerformed
        try {
            String strValue = edtT9Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 8, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру9\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT9SetActionPerformed

    private void btnT10SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT10SetActionPerformed
        try {
            String strValue = edtT10Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 9, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру10\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT10SetActionPerformed

    private void btnT11SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnT11SetActionPerformed
        try {
            String strValue = edtT11Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= -60 && IntValue <= +60) {
                IntValue += 128;
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_T, ( byte) 10, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Температура должна быть в диапазоне [-60;+60]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить температуру11\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnT11SetActionPerformed

    private void btnPS1SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS1SetActionPerformed
        try {
            String strValue = edtPS1Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 0, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг1\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS1SetActionPerformed

    private void btnPS2SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS2SetActionPerformed
        try {
            String strValue = edtPS2Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 1, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг2\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS2SetActionPerformed

    private void btnPS3SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS3SetActionPerformed
        try {
            String strValue = edtPS3Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 2, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг3\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS3SetActionPerformed

    private void btnPS4SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS4SetActionPerformed
        try {
            String strValue = edtPS4Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 3, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг4\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS4SetActionPerformed

    private void btnPS5SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS5SetActionPerformed
        try {
            String strValue = edtPS5Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 4, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг5\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS5SetActionPerformed

    private void btnPS6SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS6SetActionPerformed
        try {
            String strValue = edtPS6Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 5, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг6\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS6SetActionPerformed

    private void btnPS7SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS7SetActionPerformed
        try {
            String strValue = edtPS7Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 6, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг7\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS7SetActionPerformed

    private void btnPS8SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS8SetActionPerformed
        try {
            String strValue = edtPS8Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 7, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг8\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS8SetActionPerformed

    private void btnPS9SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS9SetActionPerformed
        try {
            String strValue = edtPS9Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 8, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг9\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS9SetActionPerformed

    private void btnPS10SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS10SetActionPerformed
        try {
            String strValue = edtPS10Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 9, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг10\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS10SetActionPerformed

    private void btnPS11SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPS11SetActionPerformed
        try {
            String strValue = edtPS11Edit.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CALIB_PH_SH, ( byte) 10, btValue);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ФазСдвиг11\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnPS11SetActionPerformed

    private void btnSaveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveDataActionPerformed
        byte aBytes[] = new byte[4];
        aBytes[0] = SLG_ConstantsCmd.SLG_CMD_ACT_SAVE_FLASH_PARAM;
        aBytes[1] = 3;
        aBytes[2] = 0;
        aBytes[3] = 0;
        
        try {
            serialPort.writeBytes( aBytes);
            theApp.m_bParamsChanged = false;
            logger.debug( ">> SAVE PH_SH CALIB");
            logger.debug( String.format( ">> 0x%02x 0x%02x 0x%02x 0x%02x", aBytes[0], aBytes[1], aBytes[2], aBytes[3]));
        } catch (SerialPortException ex) {
            logger.error( "COM-Communication exception", ex);
            theApp.m_bConnected = false;
            SLG_PSST_App.MessageBoxError( "При попытке записи в порт получили исключительную ситуацию:\n\n" + ex.toString(), "SLG_PSST");
        }
    }//GEN-LAST:event_btnSaveDataActionPerformed

    private void btnCurrentPhaseShiftGetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrentPhaseShiftGetActionPerformed
        SendComandRequestParam( ( byte) SLG_ConstantsParams.SLG_PARAM_PH_SH_CURRENT_VAL, ( byte) 0);
    }//GEN-LAST:event_btnCurrentPhaseShiftGetActionPerformed

    private void btnCurrentPhaseShiftSetDecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrentPhaseShiftSetDecActionPerformed
        try {
            String strValue = edtCurrentPhaseShiftDecValueToSet.getText();
            Integer IntValue = Integer.parseInt( strValue);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CURRENT_VAL, btValue, ( byte) 0);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ТекущийФазСдвиг\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnCurrentPhaseShiftSetDecActionPerformed

    private void btnPhShRecalcOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhShRecalcOffActionPerformed
        SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_USAGE, ( byte) 0xFF, ( byte) 0);
        theApp.m_bParamsChanged = true;
    }//GEN-LAST:event_btnPhShRecalcOffActionPerformed

    private void btnCurrentPhaseShiftSetHexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrentPhaseShiftSetHexActionPerformed
        try {
            String strValue = edtCurrentPhaseShiftHexValueToSet.getText();
            Integer IntValue = Integer.parseInt(strValue, 16);
            if( IntValue >= 0 && IntValue <= 254) {
                byte btValue = IntValue.byteValue();
                SendComandSetParam( ( byte ) SLG_ConstantsParams.SLG_PARAM_PH_SH_CURRENT_VAL, btValue, ( byte) 0);
                theApp.m_bParamsChanged = true;
            }
            else {
                SLG_PSST_App.MessageBoxInfo( "Значение фазового сдвига должно быть в диапазоне [0;254]", "SLG_PSST");
            }
        }
        catch( NumberFormatException e) {
            logger.error( "При обработке команды \"отправить ТекущийФазСдвиг\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_btnCurrentPhaseShiftSetHexActionPerformed

    private void edtCurrentPhaseShiftHexValueToSetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_edtCurrentPhaseShiftHexValueToSetFocusLost
        try {
            String strValue = edtCurrentPhaseShiftHexValueToSet.getText();
            Integer IntValue = Integer.parseInt(strValue, 16);
            if( IntValue >= 0 && IntValue <= 254) {
                edtCurrentPhaseShiftDecValueToSet.setText( String.format( "%d", IntValue));
            }            
        }
        catch( NumberFormatException e) {
            logger.error( "При потере фокуса \"ТекущийФазСдвиг HEX\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_edtCurrentPhaseShiftHexValueToSetFocusLost

    private void edtCurrentPhaseShiftDecValueToSetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_edtCurrentPhaseShiftDecValueToSetFocusLost
        try {
            String strValue = edtCurrentPhaseShiftDecValueToSet.getText();
            Integer IntValue = Integer.parseInt(strValue, 10);
            if( IntValue >= 0 && IntValue <= 254) {
                edtCurrentPhaseShiftHexValueToSet.setText( String.format( "%02X", IntValue));
            }            
        }
        catch( NumberFormatException e) {
            logger.error( "При потере фокуса \"ТекущийФазСдвиг DEC\" возникла исключительная ситуация!", e);
        }
    }//GEN-LAST:event_edtCurrentPhaseShiftDecValueToSetFocusLost


    private class PortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {            
            if( event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    int nReadyBytes = event.getEventValue();
                    byte bts[] = new byte[ nReadyBytes];
                    bts = serialPort.readBytes( nReadyBytes);
                    
                    /*
                    String strLogMessage;
                    strLogMessage = String.format( "READ %d BYTE. FIRST ONE=0x%02X", nReadyBytes, bts[0]);
                    logger.debug( strLogMessage);
                    */
                    
                    theApp.m_bfCircleBuffer.AddBytes( bts, nReadyBytes);
                }
                catch (SerialPortException ex) {
                    logger.error( "SerialPortException caught", ex);
                }
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnConnect;
    public javax.swing.JButton btnCurrentPhaseShiftGet;
    public javax.swing.JButton btnCurrentPhaseShiftSetDec;
    public javax.swing.JButton btnCurrentPhaseShiftSetHex;
    public javax.swing.JButton btnDisconnect;
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
    private javax.swing.JButton btnPhShRecalcCalib;
    private javax.swing.JButton btnPhShRecalcOff;
    private javax.swing.JButton btnResetCalibData;
    private javax.swing.JButton btnSaveData;
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
    private javax.swing.JTextField edtComPortValue;
    private javax.swing.JTextField edtCurrentPhaseShiftDecValue;
    private javax.swing.JTextField edtCurrentPhaseShiftDecValueToSet;
    private javax.swing.JTextField edtCurrentPhaseShiftHexValue;
    private javax.swing.JTextField edtCurrentPhaseShiftHexValueToSet;
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblConnectionStateTitle;
    private javax.swing.JLabel lblConnectionStateValue;
    private javax.swing.JLabel lblCurrentPhaseShiftTitle;
    private javax.swing.JLabel lblCurrentTD1Title;
    private javax.swing.JLabel lblCurrentTD1Value;
    private javax.swing.JLabel lblPhaseShift;
    private javax.swing.JLabel lblPhaseShiftUsageTitle;
    private javax.swing.JLabel lblPhaseShiftUsageValue;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblTemperature;
    // End of variables declaration//GEN-END:variables
}
