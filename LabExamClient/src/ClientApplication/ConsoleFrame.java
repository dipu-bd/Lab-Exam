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

import Utilities.Functions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * Console frame to run and test a program.
 */
public class ConsoleFrame extends javax.swing.JDialog
{
    //commands to process
    private final String mCommands;
    //executing thread
    private Thread mExecThread;

    public ConsoleFrame(final String args)
    {
        initComponents();
        mCommands = args;
        mExecThread = null;
        showOutput("", true);
    }

    private void showOutput(final String input, final boolean showError)
    {
        if (mExecThread == null) {
            mExecThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    executeProgram(input, showError);
                }
            });
            mExecThread.start();
        }
    }

    void executeProgram(final String input, final boolean showError)
    {
        try {
            clearOutput();

            //execute process
            Process p = Runtime.getRuntime().exec(mCommands);
            appendText(">> Process started...\n");

            //give inputs
            appendText(">> Giving input...\n");
            try (OutputStream out = p.getOutputStream()) {
                out.write(input.getBytes());
            }
            appendText(">> Waiting to finish...\n");
            p.waitFor(); //wait till finish            

            //get outputs
            appendText(">> Getting output...\n");

            String inn = Functions.readFully(p.getInputStream(), "UTF-8");
            if (inn.length() > 0) {
                appendText(inn + "\n");
            }
            if (showError || p.exitValue() != 0) {
                String err = Functions.readFully(p.getErrorStream(), "UTF-8");
                if (err.length() > 0) {
                    appendText(err + "\n");
                }
            }
            appendText(">> Process exited with code: " + p.exitValue() + "\n");
        }
        catch (Exception ex) {
            final StringWriter sw = new StringWriter();
            PrintStream ps = new PrintStream(new OutputStream()
            {
                @Override
                public void write(int i) throws IOException
                {
                    sw.write(i);
                }
            });
            appendText(">> " + sw.toString() + "\n");

            ex.printStackTrace();
        }
        mExecThread = null;
    }

    private void stopExecution()
    {
        try {
            mExecThread.interrupt();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void appendText(final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                outputBox.append(text);
            }
        });
    }

    private void clearOutput()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                outputBox.setText("");
            }
        });
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        testCodeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Console Window");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(735, 300));
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setResizeWeight(0.4);

        jPanel1.setBackground(new java.awt.Color(0, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Box"));

        inputBox.setBackground(new java.awt.Color(5, 57, 57));
        inputBox.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        inputBox.setForeground(new java.awt.Color(196, 223, 14));
        inputBox.setCaretColor(new java.awt.Color(196, 223, 14));
        jScrollPane2.setViewportView(inputBox);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Output Box"));

        outputBox.setEditable(false);
        outputBox.setBackground(new java.awt.Color(5, 57, 57));
        outputBox.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        outputBox.setForeground(new java.awt.Color(196, 223, 14));
        outputBox.setCaretColor(new java.awt.Color(196, 223, 14));
        jScrollPane1.setViewportView(outputBox);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 329, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
        );

        jSplitPane1.setRightComponent(jPanel2);

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));

        testCodeButton.setFont(testCodeButton.getFont().deriveFont(testCodeButton.getFont().getSize()+1f));
        testCodeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/runtest.png"))); // NOI18N
        testCodeButton.setText("Run Test");
        testCodeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                testCodeButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Give your input in the Input Box and press Run Test to execute program.");

        closeButton.setFont(closeButton.getFont().deriveFont(closeButton.getFont().getSize()+1f));
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(testCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void testCodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_testCodeButtonActionPerformed
    {//GEN-HEADEREND:event_testCodeButtonActionPerformed
        showOutput(inputBox.getText(), false);
    }//GEN-LAST:event_testCodeButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeButtonActionPerformed
    {//GEN-HEADEREND:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        stopExecution();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    public final javax.swing.JTextArea inputBox = new javax.swing.JTextArea();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    public final javax.swing.JTextArea outputBox = new javax.swing.JTextArea();
    private javax.swing.JButton testCodeButton;
    // End of variables declaration//GEN-END:variables
}
