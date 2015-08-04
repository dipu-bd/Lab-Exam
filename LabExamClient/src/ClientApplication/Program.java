/*
 * Copyright (C) 2015 Dipu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ClientApplication;

import java.awt.Insets;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Start point of the application.
 */
public class Program
{

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
        catch (ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Program.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        KeyHook.blockWindowsKey();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
        KeyHook.unblockWindowsKey();
    }
}
