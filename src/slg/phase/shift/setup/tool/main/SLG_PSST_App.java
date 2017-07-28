/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slg.phase.shift.setup.tool.main;

import java.io.File;
import java.net.ServerSocket;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author yaroslav
 */
public class SLG_PSST_App {
    public SLG_PSST_MainFrame m_pMainWnd;
    
    private ServerSocket m_pSingleInstanceSocketServer;
    
    private final String m_strSLGrootEnvVar;
    public String GetSLGRoot() { return m_strSLGrootEnvVar; }
    
    static Logger logger = Logger.getLogger(SLG_PSST_App.class);

    public SLG_PSST_App() {
        m_strSLGrootEnvVar = System.getenv( "SLG_ROOT");
        
        //SETTINGS
        //m_pSettings = new HVV_ArcViewerSettings( m_strAMSrootEnvVar);
        
        m_pSingleInstanceSocketServer = null;
        //ПРОВЕРКА ОДНОВРЕМЕННОГО ЗАПУСКА ТОЛЬКО ОДНОЙ КОПИИ ПРОГРАММЫ
        //try {
        //    m_pSingleInstanceSocketServer = new ServerSocket( m_pSettings.GetSingleInstanceSocketServerPort());
        //}
        //catch( Exception ex) {
        //    MessageBoxError( "Модуль просмотра архивных данных уже запущен.\nПоищите на других \"экранах\".", "Модуль просмотра архивных данных");
        //    logger.error( "Не смогли открыть сокет для проверки запуска только одной копии программы! Программа уже запущена?", ex);
        //    m_pSingleInstanceSocketServer = null;
        //    m_pResources = null;
        //    return;
        //}
        
        //RESOURCES
        //m_pResources = HVV_Resources.getInstance();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //главная переменная окружения
        String strSLGrootEnvVar = System.getenv( "SLG_ROOT");
        if( strSLGrootEnvVar == null) {
            MessageBoxError( "Не задана переменная окружения SLG_ROOT!", "HVV_Poller");
            return;
        }
        
        //настройка логгера
        String strlog4jPropertiesFile = strSLGrootEnvVar + "/etc/log4j.phase.shift.setup.tool.properties";
        File file = new File( strlog4jPropertiesFile);
        if(!file.exists())
            System.out.println("It is not possible to load the given log4j properties file :" + file.getAbsolutePath());
        else
            PropertyConfigurator.configure( file.getAbsolutePath());
        
        SLG_PSST_App appInstance = new SLG_PSST_App();
        if( appInstance.m_pSingleInstanceSocketServer != null) {
            logger.info( "SLG_PSST_APP::main(): Start point!");
            appInstance.start();
        }
    }
    
    public void start() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger( SLG_PSST_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger( SLG_PSST_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger( SLG_PSST_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger( SLG_PSST_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        m_pMainWnd = new SLG_PSST_MainFrame( this);
        java.awt.EventQueue.invokeLater( new Runnable() {
            public void run() {
                m_pMainWnd.setVisible( true);
            }
        });
    }
    
    /**
     * Функция для сообщения пользователю информационного сообщения
     * @param strMessage сообщение
     * @param strTitleBar заголовок
     */
    public static void MessageBoxInfo( String strMessage, String strTitleBar)
    {
        JOptionPane.showMessageDialog( null, strMessage, strTitleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Функция для сообщения пользователю сообщения об ошибке
     * @param strMessage сообщение
     * @param strTitleBar заголовок
     */
    public static void MessageBoxError( String strMessage, String strTitleBar)
    {
        JOptionPane.showMessageDialog( null, strMessage, strTitleBar, JOptionPane.ERROR_MESSAGE);
    }
}
