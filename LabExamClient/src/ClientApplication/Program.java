/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientApplication;

import java.awt.Insets;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Dipu
 */
public class Program
{

    public static Path defaultPath;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //<editor-fold desc="Set the Look and Feel" defaultstate="collapsed" > 
        try {
            String win = null;
            String nimbus = null;
            for (UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName()))
                    win = info.getClassName();
                if ("Nimbus".equals(info.getName()))
                    nimbus = info.getClassName();
            }
            if (win != null)
                javax.swing.UIManager.setLookAndFeel(win);
            else if (nimbus != null)
                javax.swing.UIManager.setLookAndFeel(nimbus);

            //set look and feel for other controls
            UIDefaults uidef = UIManager.getDefaults();
            uidef.put("TabbedPane.tabInsets", new Insets(5, 25, 5, 25));
            uidef.put("TabbedPane.tabBorderInsets", new Insets(0, 0, 0, 0));
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Program.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }

    public static void loadDefaultFolder()
    {
        JFileChooser fr = new JFileChooser();
        defaultPath = fr.getFileSystemView().getDefaultDirectory().toPath();
        defaultPath = defaultPath.resolve("Lab Exam");
        //Functions.deleteDirectory(defaultPath.toFile());
        defaultPath.toFile().mkdirs();
    }
}
