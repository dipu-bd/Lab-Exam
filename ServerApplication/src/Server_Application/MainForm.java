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

import ExtraClass.CurrentExam;
import ExtraClass.Examination;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Dipu
 */
public class MainForm extends javax.swing.JFrame {

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        newSessionButton = new javax.swing.JButton();
        openSessionButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lab Exam");
        setBackground(new java.awt.Color(243, 238, 245));
        setResizable(false);

        jLabel1.setBackground(new java.awt.Color(153, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome to Lab Exam Application");
        jLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 255, 255), new java.awt.Color(204, 255, 255), new java.awt.Color(0, 204, 204), new java.awt.Color(204, 255, 255)));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        newSessionButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        newSessionButton.setText("New Session");
        newSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSessionButtonActionPerformed(evt);
            }
        });

        openSessionButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        openSessionButton.setText("Open Session");
        openSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSessionButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("What do you want to do next?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(openSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ShowCreator() {
        if (Program.sessionCreator == null) {
            Program.sessionCreator = new SessionCreator();
        }
        Program.sessionCreator.setLocationRelativeTo(null);
        Program.sessionCreator.setVisible(true);
        this.setVisible(false);
    }
 
    private void newSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSessionButtonActionPerformed
        try {
            JFileChooser saveFile = new JFileChooser();
            saveFile.setFileFilter(new FileNameExtensionFilter("Lab Exam File", Program.defExtension));
            saveFile.setAcceptAllFileFilterUsed(false);
            saveFile.setMultiSelectionEnabled(false);
            saveFile.setSelectedFile(new File("exam." + Program.defExtension));
            if (saveFile.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                CurrentExam.curExam = new Examination();
                CurrentExam.examFile = saveFile.getSelectedFile();
                CurrentExam.Save();
                this.ShowCreator();
            }
        } catch (HeadlessException | IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE,
                    "Error while saving passwords", ex);
        }
    }//GEN-LAST:event_newSessionButtonActionPerformed

    private void openSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSessionButtonActionPerformed
        // TODO add your handling code here:                 
        try {
            JFileChooser openFile = new JFileChooser();
            openFile.setFileFilter(new FileNameExtensionFilter("Lab Exam File", Program.defExtension));
            openFile.setAcceptAllFileFilterUsed(false);
            openFile.setMultiSelectionEnabled(false);
            openFile.setSelectedFile(new File("exam." + Program.defExtension));
            if (openFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                CurrentExam.Open(openFile.getSelectedFile());
                this.ShowCreator();
            }
        } catch (HeadlessException | IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE,
                    "Error while saving passwords", ex);
        }
    }//GEN-LAST:event_openSessionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton newSessionButton;
    private javax.swing.JButton openSessionButton;
    // End of variables declaration//GEN-END:variables
}
