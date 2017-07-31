/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flavt.slg.app.phase.shift.setup.tool.main;

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
    
    public boolean m_bConnected;
    private final String m_strSLGrootEnvVar;
    public String GetSLGRoot() { return m_strSLGrootEnvVar; }
    
    static Logger logger = Logger.getLogger(SLG_PSST_App.class);

    public final int m_DevT[] = new int[11];
    public final int m_DevPS[] = new int[11];
    
    public int m_nPacksCounter;
            
    SLG_PSST_Settings m_pSettings;
    public SLG_PSST_App() {
        m_strSLGrootEnvVar = System.getenv( "SLG_ROOT");
        
        //SETTINGS
        m_pSettings = new SLG_PSST_Settings( m_strSLGrootEnvVar);
        
        m_pSingleInstanceSocketServer = null;
        //ПРОВЕРКА ОДНОВРЕМЕННОГО ЗАПУСКА ТОЛЬКО ОДНОЙ КОПИИ ПРОГРАММЫ
        try {
            m_pSingleInstanceSocketServer = new ServerSocket( m_pSettings.GetSingleInstanceSocketServerPort());
        }
        catch( Exception ex) {
            MessageBoxError( "Уже есть запущенный экземпляр утилиты редактирования параметров калибровки фазового сдвига.\nПоищите на других \"экранах\".", "Утилита редактирования калибровки фазового сдвига");
            logger.error( "Не смогли открыть сокет для проверки запуска только одной копии программы! Программа уже запущена?", ex);
            m_pSingleInstanceSocketServer = null;
            return;
        }
        
        m_bConnected = false;
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
        else {
            String strAbsPath = file.getAbsolutePath();
            PropertyConfigurator.configure( strAbsPath);
        }
        
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
