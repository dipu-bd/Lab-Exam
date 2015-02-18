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
package Server_Application;

import javax.swing.JOptionPane;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class of the project
 *
 * @author Dipu
 */
public class Program {

    public static final String defExtension = "labex";

    public static MainForm mainForm;
    public static SessionCreator sessionCreator;
    public static SessionViewer sessionViewer;

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            String win = null;
            String nimbus = null;
            for (LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    win = info.getClassName();
                }
                if ("Nimbus".equals(info.getName())) {
                    nimbus = info.getClassName();
                }
            }
            if (win != null) {
                javax.swing.UIManager.setLookAndFeel(win);
            } else if (nimbus != null) {
                javax.swing.UIManager.setLookAndFeel(nimbus);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Program.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold> 
        
        /* First create listening port */
        Connector.ClientListener.initialize();

        /* Create and display the form */
        mainForm = new MainForm();
        mainForm.setLocationRelativeTo(null);
        mainForm.setVisible(true);
    }
}
