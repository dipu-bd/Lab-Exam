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
package ServerApplication;

import UtilityClass.Examination;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Dipu
 */
public class MainForm extends javax.swing.JFrame {

    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        initComponents();
        getContentPane().setBackground(getBackground());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        newSessionButton = new javax.swing.JButton();
        openSessionButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        currentSessionBox = new javax.swing.JTextField();
        editSessionButton = new javax.swing.JButton();
        startExamButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lab Exam");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(199, 240, 240));
        setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        setResizable(false);

        jLabel1.setBackground(new java.awt.Color(114, 217, 217));
        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome to Lab Exam Application");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);

        newSessionButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        newSessionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/Files-New-Window-icon.png"))); // NOI18N
        newSessionButton.setText("New Session");
        newSessionButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newSessionButtonActionPerformed(evt);
            }
        });

        openSessionButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        openSessionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/open-file-icon.png"))); // NOI18N
        openSessionButton.setText("Open Session");
        openSessionButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openSessionButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(145, 225, 225));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel2.setText("Current Session :");

        currentSessionBox.setEditable(false);
        currentSessionBox.setBackground(new java.awt.Color(153, 255, 204));
        currentSessionBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        currentSessionBox.setFocusable(false);

        editSessionButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        editSessionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/edit-file-icon.png"))); // NOI18N
        editSessionButton.setText("Edit Session");
        editSessionButton.setEnabled(false);
        editSessionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editSessionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editSessionButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editSessionButtonActionPerformed(evt);
            }
        });

        startExamButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        startExamButton.setForeground(new java.awt.Color(102, 0, 0));
        startExamButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/start-icon.png"))); // NOI18N
        startExamButton.setText("Start Exam");
        startExamButton.setEnabled(false);
        startExamButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startExamButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        startExamButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startExamButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentSessionBox))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(currentSessionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(newSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(openSessionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(newSessionButton, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(openSessionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void OpenFile()
    {
        editSessionButton.setEnabled(true);
        startExamButton.setEnabled(true);
        currentSessionBox.setText(CurrentExam.examFile.getAbsolutePath());
    }

    private void showSessionCreator()
    {
        final MainForm cur = this;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                SessionCreator sc = new SessionCreator();
                sc.setModal(false);
                sc.ParentForm = cur;
                sc.setVisible(true);
                cur.setVisible(false);
            }
        });
    }

    private void showSessionViewer()
    {
        final MainForm cur = this;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                SessionViewer sc = new SessionViewer();
                sc.setVisible(true);
                sc.ParentForm = cur;
                cur.setVisible(false);
            }
        });
    }

    private void newSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSessionButtonActionPerformed
        try
        {
            JFileChooser saveFile = new JFileChooser();
            saveFile.setFileFilter(new FileNameExtensionFilter("Lab Exam File", Program.defExtension));
            saveFile.setAcceptAllFileFilterUsed(false);
            saveFile.setMultiSelectionEnabled(false);
            saveFile.setSelectedFile(new File("exam." + Program.defExtension));
            if (saveFile.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                CurrentExam.curExam = new Examination();
                CurrentExam.examFile = saveFile.getSelectedFile();
                CurrentExam.Save();
                OpenFile();
            }
        }
        catch (HeadlessException | IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Error while saving file");
        }
    }//GEN-LAST:event_newSessionButtonActionPerformed

    private void openSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSessionButtonActionPerformed
        // TODO add your handling code here:                 
        try
        {
            JFileChooser openFile = new JFileChooser();
            openFile.setFileFilter(new FileNameExtensionFilter("Lab Exam File", Program.defExtension));
            openFile.setAcceptAllFileFilterUsed(false);
            openFile.setMultiSelectionEnabled(false);
            openFile.setSelectedFile(new File("exam." + Program.defExtension));
            if (openFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                CurrentExam.Open(openFile.getSelectedFile());
                OpenFile();
            }
        }
        catch (HeadlessException | IOException | ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(this, "Error while opening file");
        }
    }//GEN-LAST:event_openSessionButtonActionPerformed

    private void editSessionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editSessionButtonActionPerformed
    {//GEN-HEADEREND:event_editSessionButtonActionPerformed
        showSessionCreator();
    }//GEN-LAST:event_editSessionButtonActionPerformed

    private void startExamButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startExamButtonActionPerformed
    {//GEN-HEADEREND:event_startExamButtonActionPerformed
        showSessionViewer();
    }//GEN-LAST:event_startExamButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField currentSessionBox;
    private javax.swing.JButton editSessionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton newSessionButton;
    private javax.swing.JButton openSessionButton;
    private javax.swing.JButton startExamButton;
    // End of variables declaration//GEN-END:variables
}
