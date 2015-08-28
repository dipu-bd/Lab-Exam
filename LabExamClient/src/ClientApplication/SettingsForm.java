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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * To modify and change settings
 */
public class SettingsForm extends javax.swing.JDialog
{

    /**
     * Creates new form SettingsForm
     */
    public SettingsForm()
    {
        initComponents();
        getContentPane().setBackground(getBackground());

        loadDefaultValues();
    }

    /**
     * loads the default values from settings
     */
    private void loadDefaultValues()
    {
        defaultPathText.setText(AppSettings.getDefaultPath().toString());
        javaText.setText(AppSettings.getJavaExePath());
        javacText.setText(AppSettings.getJavaCompiler());
        javaOptionText.setText(AppSettings.getJavaOptions());
        gppText.setText(AppSettings.getGppCompiler());
        gppOptionText.setText(AppSettings.getGppOptions());
        gccText.setText(AppSettings.getGccCompiler());
        gccOptionText.setText(AppSettings.getGccOptions());
    }

    /**
     * Shows a Folder Dialog to select a path and returns the selected path.
     *
     * @param def Default path to return if a path could not be selected.
     * @param type (0 = Files Only, 1 = Directories Only)
     * @return Returns the selected path, on cancel returns the default value.
     */
    public String getSelectedPath(String def, int type)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(type);
        fileChooser.setSelectedFile(new java.io.File(def));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toString();
        }
        return def;
    }

    /**
     * Changes the windows keyboard layout to disable Alt and Win keys. It also
     * makes a backup of previous layout before changing.
     *
     * @return True on success; False otherwise.
     */
    public boolean disableShortcutKeys()
    {
        try {
            //first check if already disabled
            if (!AppSettings.getKeyLayoutEnabled()) {
                return false;
            }

            //backup current configs
            byte[] backup = AppSettings.getScanCodeMap();
            AppSettings.setKeyLayoutBackup(backup);

            //create new config
        /*DataFormat: http://www.northcode.com/blog.php/2007/07/25/Securing-Windows-For-Use-As-A-Kiosk */
            /*Scan Codes: http://www.quadibloc.com/comp/scan.htm */
            byte[] data = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //Header: Version. Set to all zeroes.
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //Header: Flags. Set to all zeroes.
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, //Number of entries in the map (including null entry).
                (byte) 0x00, (byte) 0x00, (byte) 0x5B, (byte) 0xE0, //Left  Windows Key (0xE05B) -> Disable (0x0000).
                (byte) 0x00, (byte) 0x00, (byte) 0x5C, (byte) 0xE0, //Right Windows Key (0xE05C) -> Disable (0x0000).
                (byte) 0x00, (byte) 0x00, (byte) 0x5D, (byte) 0xE0, //Windows  Menu Key (0xE05D) -> Disable (0x0000).
                (byte) 0x2A, (byte) 0x00, (byte) 0x38, (byte) 0x00, //Left  ALT Key (0x0038) -> Left Shift Key (0x002A).
                (byte) 0x36, (byte) 0x00, (byte) 0x38, (byte) 0xE0, //Right ALT Key (0xE038) -> Right Shift Key (0x0036).
                //(byte)0x00, (byte)0x00, (byte)0x1D, (byte)0x00, //Left  Control Key (0x001D) -> Disable (0x0000).
                //(byte)0x00, (byte)0x00, (byte)0x1D, (byte)0xE0, //Right Control Key (0xE01D) -> Disable (0x0000).
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 //Null entry.
            };

            //store new configs
            AppSettings.setScanCodeMap(data);

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Changes the windows keyboard layout to enable Alt and Win keys. It uses
     * backup data to restore previous keyboard layout.
     *
     * @return True on success; False otherwise.
     */
    public boolean enableShortcutKeys()
    {
        try {
            //first check if keys are disabled
            if (AppSettings.getKeyLayoutEnabled()) {
                return false;
            }

            //restore scan code configs
            byte[] backup = AppSettings.getKeyLayoutBackup();
            AppSettings.setScanCodeMap(backup);

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        defaultPathText = new javax.swing.JTextField();
        defaultPathButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        javacText = new javax.swing.JTextField();
        javacButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        javaText = new javax.swing.JTextField();
        javaButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        javaOptionText = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        gppText = new javax.swing.JTextField();
        gppButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        gppOptionText = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        gccText = new javax.swing.JTextField();
        gccButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        gccOptionText = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        okButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        disableKeysButton = new javax.swing.JButton();
        enableKeysButton = new javax.swing.JButton();

        setTitle("Settings");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(220, 210, 230));
        setModal(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(217, 229, 241));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Default"));

        jLabel1.setText("Default Path :");

        defaultPathText.setEditable(false);
        defaultPathText.setBackground(new java.awt.Color(240, 245, 245));
        defaultPathText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        defaultPathText.setText("...");

        defaultPathButton.setText("...");
        defaultPathButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                defaultPathButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultPathText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultPathButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(defaultPathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultPathButton))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(217, 229, 241));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Java Compilers"));

        jLabel3.setText("javac.exe");

        javacText.setEditable(false);
        javacText.setBackground(new java.awt.Color(240, 245, 245));
        javacText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        javacText.setText("javac");

        javacButton.setText("...");
        javacButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                javacButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("java.exe");

        javaText.setEditable(false);
        javaText.setBackground(new java.awt.Color(240, 245, 245));
        javaText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        javaText.setText("java");

        javaButton.setText("...");
        javaButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                javaButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Compiler Options: ");

        javaOptionText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        javaOptionText.setText("-g -o");
        javaOptionText.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                javaOptionTextKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(javacText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(javacButton))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(javaText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(javaButton))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(javaOptionText)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(javacText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(javacButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(javaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(javaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(javaOptionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBackground(new java.awt.Color(217, 229, 241));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("C++ Compilers"));

        jLabel9.setText("gpp.exe");

        gppText.setEditable(false);
        gppText.setBackground(new java.awt.Color(240, 245, 245));
        gppText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        gppText.setText("gpp");

        gppButton.setText("...");
        gppButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                gppButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("Compiler Options: ");

        gppOptionText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        gppOptionText.setText("-Wall -std=c++11 -o");
        gppOptionText.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                gppOptionTextKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gppText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gppButton))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gppOptionText, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(gppText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gppButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(gppOptionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel6.setBackground(new java.awt.Color(217, 229, 241));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("ANSI C Compilers"));

        jLabel10.setText("gcc.exe");

        gccText.setEditable(false);
        gccText.setBackground(new java.awt.Color(240, 245, 245));
        gccText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        gccText.setText("gcc");

        gccButton.setText("...");
        gccButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                gccButtonActionPerformed(evt);
            }
        });

        jLabel12.setText("Compiler Options: ");

        gccOptionText.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        gccOptionText.setText("-Wall -ansi -o");
        gccOptionText.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                gccOptionTextKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gccText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gccButton))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gccOptionText)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(gccText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gccButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(gccOptionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setBackground(new java.awt.Color(0, 204, 204));
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0, 204, 204)));

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/submit.png"))); // NOI18N
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButtonActionPerformed(evt);
            }
        });

        okButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/reload.png"))); // NOI18N
        okButton1.setText("Reset All");
        okButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(okButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        jPanel2.setBackground(new java.awt.Color(217, 229, 241));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Change windows shortcuts keys"));

        disableKeysButton.setText("Disable Keys");
        disableKeysButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                disableKeysButtonActionPerformed(evt);
            }
        });

        enableKeysButton.setText("Enable Keys");
        enableKeysButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                enableKeysButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(disableKeysButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enableKeysButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(disableKeysButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enableKeysButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void defaultPathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_defaultPathButtonActionPerformed
    {//GEN-HEADEREND:event_defaultPathButtonActionPerformed
        String newPath = getSelectedPath(AppSettings.getDefaultPath().toString(), JFileChooser.DIRECTORIES_ONLY);
        AppSettings.setDefaultPath((new java.io.File(newPath)).toPath());
        defaultPathText.setText(newPath);
    }//GEN-LAST:event_defaultPathButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void javacButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_javacButtonActionPerformed
    {//GEN-HEADEREND:event_javacButtonActionPerformed
        String newPath = getSelectedPath(AppSettings.getJavaCompiler(), JFileChooser.FILES_ONLY);
        AppSettings.setJavaCompiler(newPath);
        javacText.setText(newPath);
    }//GEN-LAST:event_javacButtonActionPerformed

    private void javaButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_javaButtonActionPerformed
    {//GEN-HEADEREND:event_javaButtonActionPerformed
        String newPath = getSelectedPath(AppSettings.getJavaExePath(), JFileChooser.FILES_ONLY);
        AppSettings.setJavaExePath(newPath);
        javaText.setText(newPath);
    }//GEN-LAST:event_javaButtonActionPerformed

    private void javaOptionTextKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_javaOptionTextKeyTyped
    {//GEN-HEADEREND:event_javaOptionTextKeyTyped
        AppSettings.setJavaOptions(javaOptionText.getText());
    }//GEN-LAST:event_javaOptionTextKeyTyped

    private void gppOptionTextKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_gppOptionTextKeyTyped
    {//GEN-HEADEREND:event_gppOptionTextKeyTyped
        AppSettings.setGppOptions(gppOptionText.getText());
    }//GEN-LAST:event_gppOptionTextKeyTyped

    private void gppButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gppButtonActionPerformed
    {//GEN-HEADEREND:event_gppButtonActionPerformed
        String newPath = getSelectedPath(AppSettings.getGppCompiler(), JFileChooser.FILES_ONLY);
        AppSettings.setGppCompiler(newPath);
        gppText.setText(newPath);
    }//GEN-LAST:event_gppButtonActionPerformed

    private void gccButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gccButtonActionPerformed
    {//GEN-HEADEREND:event_gccButtonActionPerformed
        String newPath = getSelectedPath(AppSettings.getGccCompiler(), JFileChooser.FILES_ONLY);
        AppSettings.setGccCompiler(newPath);
        gccText.setText(newPath);
    }//GEN-LAST:event_gccButtonActionPerformed

    private void gccOptionTextKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_gccOptionTextKeyTyped
    {//GEN-HEADEREND:event_gccOptionTextKeyTyped
        AppSettings.setGccOptions(gccOptionText.getText());
    }//GEN-LAST:event_gccOptionTextKeyTyped

    private void okButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButton1ActionPerformed
    {//GEN-HEADEREND:event_okButton1ActionPerformed
        int sel = JOptionPane.showConfirmDialog(this,
                "All settings will be reset to their default values.\r\nSure to continue?",
                "Reset Settings", JOptionPane.YES_NO_OPTION);
        if (sel != JOptionPane.YES_OPTION) {
            return;
        }

        AppSettings.setJavaExePath("java");
        AppSettings.setJavaCompiler("javac");
        AppSettings.setJavaOptions("-g -d");
        AppSettings.setGppCompiler("g++");
        AppSettings.setGppOptions("-Wall -std=c++11 -o");
        AppSettings.setGccCompiler("gcc");
        AppSettings.setGccOptions("-Wall -ansi -o");
        loadDefaultValues();
    }//GEN-LAST:event_okButton1ActionPerformed

    private void disableKeysButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_disableKeysButtonActionPerformed
    {//GEN-HEADEREND:event_disableKeysButtonActionPerformed
        if (disableShortcutKeys()) {
            JOptionPane.showMessageDialog(this, String.format(
                    "Disabled the windows shortcut keys.%n"
                    + "Please RESTART the computer to make it working."));
        }
        else
        {  
            JOptionPane.showMessageDialog(this, String.format(
                    "Failed to disable the windows shortcut keys.%n"
                    + "Make sure this application has Administrator privilege."));
            
        }
    }//GEN-LAST:event_disableKeysButtonActionPerformed

    private void enableKeysButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_enableKeysButtonActionPerformed
    {//GEN-HEADEREND:event_enableKeysButtonActionPerformed
        if (enableShortcutKeys()) {
            JOptionPane.showMessageDialog(this, String.format(
                    "Enabled the windows shortcut keys.%n"
                    + "Please RESTART the computer to make it working."));
        }
        else 
        {
             JOptionPane.showMessageDialog(this, String.format(
                    "Failed to enable the windows shortcut keys.%n"
                    + "Make sure this application has Administrator privilege."));
        }
    }//GEN-LAST:event_enableKeysButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton defaultPathButton;
    private javax.swing.JTextField defaultPathText;
    private javax.swing.JButton disableKeysButton;
    private javax.swing.JButton enableKeysButton;
    private javax.swing.JButton gccButton;
    private javax.swing.JTextField gccOptionText;
    private javax.swing.JTextField gccText;
    private javax.swing.JButton gppButton;
    private javax.swing.JTextField gppOptionText;
    private javax.swing.JTextField gppText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JButton javaButton;
    private javax.swing.JTextField javaOptionText;
    private javax.swing.JTextField javaText;
    private javax.swing.JButton javacButton;
    private javax.swing.JTextField javacText;
    private javax.swing.JButton okButton;
    private javax.swing.JButton okButton1;
    // End of variables declaration//GEN-END:variables
}
