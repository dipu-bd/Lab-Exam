/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Application;

import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Dipu
 */
public class Program {
    
    public static LoginForm loginForm;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            String win = null;
            String nimbus = null;
            for (UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Windows".equals(info.getName()))
                {
                    win = info.getClassName();
                }
                if ("Nimbus".equals(info.getName()))
                {
                    nimbus = info.getClassName();
                }
            }
            if (win != null)
            {
                javax.swing.UIManager.setLookAndFeel(win);
            }
            else if (nimbus != null)
            {
                javax.swing.UIManager.setLookAndFeel(nimbus);
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Program.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold>
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                loginForm = new LoginForm();
                loginForm.setLocationRelativeTo(null);
                loginForm.setVisible(true);
            }
        });
    }

}
