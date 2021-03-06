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

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Utilities.Candidate;
import Utilities.CandidateStatus;
import Utilities.Examination;
import Utilities.Functions;
import Utilities.Question;
import Utilities.UserChangeEvent;
import Utilities.UserChangedHandler;
import java.awt.Desktop;
import java.io.File;
import java.util.logging.Formatter;
import java.util.logging.StreamHandler;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Dipu
 */
public class SessionViewer extends javax.swing.JFrame {

    //number of predefined headers

    private final int PRE_HEADER_SIZE = 6;

    /**
     * Constructor for current class
     *
     * @param parent Parent frame to set
     * @param curExam Current Exam object to work with
     */
    public SessionViewer(JFrame parent, CurrentExam curExam) {
        mParentForm = parent;
        mCurrentExam = curExam;
        mExam = curExam.getExamination();
        mTimer = new Timer();
        mLogger = Logger.getLogger("LabExam");
        mLabExamServer = new LabExamServer();

        initComponents();
        setToFullFocus();

        initiateOthers();
        loadValues();
    }

    //parent frame of this frame
    private final JFrame mParentForm;
    //current exam object to work with
    private final CurrentExam mCurrentExam;
    //examination object to work with
    private final Examination mExam;
    //timer object 
    private final Timer mTimer;
    //logger to get logs
    private final Logger mLogger;
    //lab exam server object
    private final LabExamServer mLabExamServer;

    /**
     * Initialize this frame. It loads all data needed for examination server.
     * It creates a new server socket and waits for candidates to connect.
     *
     * Also various other initialization task is performed from here.
     */
    private void initiateOthers() {
        //initialize logger           
        mLogger.addHandler(new StreamHandler(System.out, new Formatter() {
            @Override
            public String format(LogRecord lr) {
                String msg = (new Date(lr.getMillis())).toString() + " : ";
                msg += lr.getLevel().getName() + " : ";
                msg += lr.getMessage() + "\n";
                processLogs(msg);
                return msg;
            }
        }));

        //initialize timer
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                setRemainingTime();
            }
        };
        mTimer.scheduleAtFixedRate(tt, 0, 500);

        //initialize server  
        mLabExamServer.setCurrentExam(mCurrentExam);

        //initialize table 
        loadCandidateList();

        //add user changed lister
        mCurrentExam.addUserChangedHandler(new UserChangedHandler() {
            @Override
            public void userChanged(UserChangeEvent ae) {
                loadCandidateList();
            }

            @Override
            public void userSubmitted(UserChangeEvent ae) {
                loadCandidateList();
            }
        });
    }

    /**
     * Load the exam information and show them to the user.
     */
    public final void loadValues() {
        titleBox.setText(mExam.getExamTitle());
        questionCountBox.setText(Integer.toString(mExam.getQuestionCount()));
        startTimeBox.setText(Functions.formatTime(mExam.getStartTime()));
        ipAddressBox.setText(LabExamServer.getServerIPAddress() + ":" + mLabExamServer.getPort());
        candidateCount.setText("Total number of candidates : " + mExam.getCandidateCount());
    }

    /**
     * This method is called periodically. It will update the remaining
     * time/refresh the exam status.
     */
    private void setRemainingTime() {
        try {
            long now = System.currentTimeMillis();
            long start = mExam.getStartTime().getTime();
            long stop = start + mExam.getDuration() * 60000;

            String msg;
            if (mExam.isWaiting()) {
                msg = "Exam will start in ";
                msg += Functions.formatTimeSpan(start - now);
                remainingTimeBox.setText(msg);
            }
            else if (mExam.isOver()) {
                msg = "Exam is finished.";
                remainingTimeBox.setText(msg);
            }
            else //exam is running
            {
                msg = "Exam is running... ";
                msg += Functions.formatTimeSpan(stop - now);
                msg += " remaining.";
                remainingTimeBox.setText(msg);
            }

            if (mLabExamServer.isBusy()) {
                endExamButton.setText("Stop Exam");
            }
            else {
                if (mExam.isRunning()) {
                    endExamButton.setText("Start Exam");
                }
                else {
                    endExamButton.setText("Exit");
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(SessionViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Make this SessionCreator frame visible as a modal. This enables editing
     * exam information.
     */
    private void showSessionCreator() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                (new SessionCreator(null, mCurrentExam)).setVisible(true);
            }
        });
    }

    /**
     * Process messages by logger. Display and save to log file.
     *
     * @param msg Log message to process.
     */
    private void processLogs(String msg) {
        statusBox.append(msg);
    }

    /**
     * This list will show the candidates status and problems they submitted
     */
    private void loadCandidateList() {
        /* If any changes occurs here, values inside 
         *      openSubmissionFolder()
         * should be updated.
         */

        //add headers
        int qcount = mExam.getQuestionCount();
        String[] header = new String[qcount + PRE_HEADER_SIZE];
        header[0] = "ID";
        header[1] = "Name";
        header[2] = "Reg No";
        header[3] = "Password";
        header[4] = "Status";
        header[5] = "Login \r\nAttempts";
        //add questions as headers
        int row = PRE_HEADER_SIZE;
        for (Question ques : mExam.getAllQuestion()) {
            header[row++] = String.format("Question %02d", ques.getId());
        }

        //add items
        int siz = mExam.getCandidateCount();
        Object data[][] = new Object[siz][qcount + PRE_HEADER_SIZE];
        row = 0;
        for (Candidate cd : mExam.getAllCandidate()) {
            //set general data
            data[row][0] = cd.getId();
            data[row][1] = cd.getName();
            data[row][2] = cd.getRegNo();
            data[row][3] = cd.getPassword();

            //set candidate status
            if (mCurrentExam.isAvaiable(cd.getId())) {
                CandidateStatus cs = mCurrentExam.getCandidateStatus(cd.getId());
                data[row][4] = cs.isConnected() ? "Connected" : "Disconnected";
                data[row][5] = cs.getLoginCount();
            }
            else {
                data[row][4] = "Disconnected";
                data[row][5] = 0;
            }

            //set question data
            int col = PRE_HEADER_SIZE;
            for (Question qt : mExam.getAllQuestion()) {
                File subPath = mCurrentExam.getSubmissionPath(cd.getRegNo(), qt.getId());
                data[row][col++] = subPath.exists() ? "Submitted" : "-";
            }
            row++;
        }

        //set the table model
        submissionTable.setModel(
                new DefaultTableModel(data, header) {
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return false;
                    }
                }
        );

    }

    /**
     * Open the folder in which the submissions of candidates are saved.
     */
    private void openSubmissionFolder() {
        try {
            int r = submissionTable.getSelectedRow();
            int c = submissionTable.getSelectedColumn();

            if (r >= 0 && c >= PRE_HEADER_SIZE) //on question list 
            {
                String reg = (String) submissionTable.getValueAt(r, 2); //reg no
                String ques = submissionTable.getColumnName(c);
                ques = ques.substring(ques.lastIndexOf(" "));
                int qid = Integer.parseInt(ques.trim());
                File path = mCurrentExam.getSubmissionPath(reg, qid);
                if (path.exists()) {
                    Desktop.getDesktop().open(path);
                }
            }
            else if (r >= 0 && c == 5) //login attempts
            {
                int id = (int) submissionTable.getValueAt(r, 0); //id
                String msg = "IP(s) used by this candidate:";
                for (String ip : mCurrentExam.getCandidateStatus(id).getIpList()) {
                    msg = msg + System.lineSeparator() + "    " + ip;
                }
                JOptionPane.showMessageDialog(this, msg);
            }
        }
        catch (NumberFormatException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Maximize the current window and bring it into focus.
     */
    private void setToFullFocus() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setFocusableWindowState(true);
    }

    /**
     * Stops the server in the middle of an examination.
     */
    private void StopExamination() {
        if (mLabExamServer.isBusy()) {
            //stop exam             
            if (mExam.isRunning()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you going to stop the current examination?",
                        "Exit Application", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            mLabExamServer.StopListening();
        }
    }

    /**
     * This will close the current application with a warning message. It will
     * make the parent form visible after closing
     */
    private void exitApplication() {
        StopExamination();
        if (!mLabExamServer.isBusy()) {
            mTimer.cancel();
            this.dispose();
            mParentForm.setVisible(true);
        }
    }

    void addExtraFiveMinutes() {
        final int MIN_TO_ADD = 5;
        try {
            long time = mExam.getStartTime().getTime();
            long now = System.currentTimeMillis();
            if (now > time) {
                mExam.setDuration(mExam.getDuration() + MIN_TO_ADD);
            }
            else {
                time += MIN_TO_ADD * 60 * 1000; //MIN_TO_ADD min in milis
                mExam.getStartTime().setTime(time);
                startTimeBox.setText(mExam.getStartTime().toString());
            }
            mCurrentExam.SaveToFile();
            mLogger.log(Level.INFO, "Added extra 10 minutes");
        }
        catch (IOException ex) {
            mLogger.log(Level.SEVERE, "Failed to save data", ex);
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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel9 = new javax.swing.JPanel();
        endExamButton = new javax.swing.JButton();
        editorButton = new javax.swing.JButton();
        mainTabPane = new javax.swing.JTabbedPane();
        tabPage1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusBox = new javax.swing.JTextArea();
        tabPage2 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        submissionTable = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        refreshSubmissionButton = new javax.swing.JButton();
        openSubFolderButton = new javax.swing.JButton();
        candidateCount = new javax.swing.JLabel();
        tabPage3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        titleBox = new javax.swing.JLabel();
        questionCountBox = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        startTimeBox = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        add5minButton = new javax.swing.JButton();
        remainingTimeBox = new javax.swing.JLabel();
        ipAddressBox = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Lab Exam Session");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(5, 236, 236));

        endExamButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        endExamButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/stop.png"))); // NOI18N
        endExamButton.setText("Exit");
        endExamButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                endExamButtonActionPerformed(evt);
            }
        });

        editorButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        editorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/editor.png"))); // NOI18N
        editorButton.setText("Editor");
        editorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(endExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        mainTabPane.setBackground(new java.awt.Color(195, 210, 221));
        mainTabPane.setDoubleBuffered(true);
        mainTabPane.setOpaque(true);

        statusBox.setEditable(false);
        statusBox.setBackground(new java.awt.Color(0, 40, 50));
        statusBox.setColumns(20);
        statusBox.setForeground(new java.awt.Color(208, 255, 143));
        statusBox.setLineWrap(true);
        statusBox.setRows(5);
        statusBox.setText("Welcome to LAB Exam. \n\n");
        statusBox.setCaretColor(new java.awt.Color(208, 255, 143));
        jScrollPane1.setViewportView(statusBox);
        statusBox.getCaret().setVisible(true);
        ((DefaultCaret)statusBox.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        javax.swing.GroupLayout tabPage1Layout = new javax.swing.GroupLayout(tabPage1);
        tabPage1.setLayout(tabPage1Layout);
        tabPage1Layout.setHorizontalGroup(
            tabPage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        tabPage1Layout.setVerticalGroup(
            tabPage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Exam Status", tabPage1);

        submissionTable.setAutoCreateRowSorter(true);
        submissionTable.setBackground(new java.awt.Color(223, 255, 255));
        submissionTable.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        submissionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        submissionTable.setCellSelectionEnabled(true);
        submissionTable.setFillsViewportHeight(true);
        submissionTable.setFocusable(false);
        submissionTable.setGridColor(java.awt.Color.cyan);
        submissionTable.setIntercellSpacing(new java.awt.Dimension(3, 3));
        submissionTable.setRowHeight(20);
        submissionTable.setSelectionBackground(java.awt.Color.cyan);
        submissionTable.setSelectionForeground(new java.awt.Color(0, 0, 51));
        submissionTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        submissionTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                submissionTableMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(submissionTable);
        submissionTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (submissionTable.getColumnModel().getColumnCount() > 0)
        {
            submissionTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            submissionTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            submissionTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        jPanel11.setBackground(new java.awt.Color(186, 242, 242));

        refreshSubmissionButton.setText("Refresh");
        refreshSubmissionButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refreshSubmissionButtonActionPerformed(evt);
            }
        });

        openSubFolderButton.setText("Open Folder");
        openSubFolderButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openSubFolderButtonActionPerformed(evt);
            }
        });

        candidateCount.setText(" ");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(candidateCount, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(refreshSubmissionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openSubFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openSubFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshSubmissionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(candidateCount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout tabPage2Layout = new javax.swing.GroupLayout(tabPage2);
        tabPage2.setLayout(tabPage2Layout);
        tabPage2Layout.setHorizontalGroup(
            tabPage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPage2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(tabPage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)))
        );
        tabPage2Layout.setVerticalGroup(
            tabPage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPage2Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        mainTabPane.addTab("Candidate Status", tabPage2);

        javax.swing.GroupLayout tabPage3Layout = new javax.swing.GroupLayout(tabPage3);
        tabPage3.setLayout(tabPage3Layout);
        tabPage3Layout.setHorizontalGroup(
            tabPage3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        tabPage3Layout.setVerticalGroup(
            tabPage3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 337, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Exam Overview", tabPage3);

        jPanel5.setBackground(new java.awt.Color(163, 236, 239));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        titleBox.setBackground(new java.awt.Color(191, 243, 250));
        titleBox.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        titleBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleBox.setText("Exam Title");
        titleBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 1, true));
        titleBox.setOpaque(true);
        titleBox.setPreferredSize(new java.awt.Dimension(60, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(titleBox, gridBagConstraints);

        questionCountBox.setBackground(new java.awt.Color(236, 244, 251));
        questionCountBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        questionCountBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        questionCountBox.setText("10");
        questionCountBox.setToolTipText("Total marked of all questions");
        questionCountBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));
        questionCountBox.setOpaque(true);
        questionCountBox.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel5.add(questionCountBox, gridBagConstraints);
        questionCountBox.getAccessibleContext().setAccessibleName("100");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Question Count :");
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jLabel2, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("IP Address :");
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jLabel4, gridBagConstraints);

        startTimeBox.setBackground(new java.awt.Color(247, 248, 251));
        startTimeBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        startTimeBox.setForeground(new java.awt.Color(51, 51, 255));
        startTimeBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startTimeBox.setText("2/18/15 12:12 PM");
        startTimeBox.setToolTipText("");
        startTimeBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));
        startTimeBox.setOpaque(true);
        startTimeBox.setPreferredSize(new java.awt.Dimension(200, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel5.add(startTimeBox, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Start Time :");
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jLabel6, gridBagConstraints);

        add5minButton.setText("Extend 5 minutes");
        add5minButton.setFocusable(false);
        add5minButton.setPreferredSize(new java.awt.Dimension(100, 23));
        add5minButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                add5minButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel5.add(add5minButton, gridBagConstraints);

        remainingTimeBox.setBackground(new java.awt.Color(204, 255, 204));
        remainingTimeBox.setFont(new java.awt.Font("Segoe UI Semibold", 2, 14)); // NOI18N
        remainingTimeBox.setForeground(new java.awt.Color(102, 51, 0));
        remainingTimeBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        remainingTimeBox.setText("Exam will start in X minutes");
        remainingTimeBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        remainingTimeBox.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel5.add(remainingTimeBox, gridBagConstraints);
        remainingTimeBox.getAccessibleContext().setAccessibleDescription("");

        ipAddressBox.setBackground(new java.awt.Color(230, 245, 243));
        ipAddressBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        ipAddressBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ipAddressBox.setToolTipText("IP Address of the server.");
        ipAddressBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102)));
        ipAddressBox.setOpaque(true);
        ipAddressBox.setPreferredSize(new java.awt.Dimension(200, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel5.add(ipAddressBox, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainTabPane)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainTabPane)
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void endExamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endExamButtonActionPerformed
        if (endExamButton.getText().equalsIgnoreCase("exit")) {
            exitApplication();
        }
        else if (mLabExamServer.isBusy()) {
            StopExamination();
        }
        else {
            mLabExamServer.StartListening();
        }
    }//GEN-LAST:event_endExamButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitApplication();
    }//GEN-LAST:event_formWindowClosing

    private void add5minButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add5minButtonActionPerformed
        addExtraFiveMinutes();
    }//GEN-LAST:event_add5minButtonActionPerformed

    private void editorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editorButtonActionPerformed
    {//GEN-HEADEREND:event_editorButtonActionPerformed
        showSessionCreator();
    }//GEN-LAST:event_editorButtonActionPerformed

    private void refreshSubmissionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refreshSubmissionButtonActionPerformed
    {//GEN-HEADEREND:event_refreshSubmissionButtonActionPerformed
        loadCandidateList();
    }//GEN-LAST:event_refreshSubmissionButtonActionPerformed

    private void openSubFolderButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openSubFolderButtonActionPerformed
    {//GEN-HEADEREND:event_openSubFolderButtonActionPerformed
        try {
            File path = mExam.getSubmissionPath();
            if (!path.exists()) {
                path.mkdirs();
            }
            Desktop.getDesktop().open(path);
        }
        catch (Exception ex) {
        }
    }//GEN-LAST:event_openSubFolderButtonActionPerformed

    private void submissionTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_submissionTableMouseClicked
    {//GEN-HEADEREND:event_submissionTableMouseClicked
        if (evt.getClickCount() == 2) {
            openSubmissionFolder();
        }
    }//GEN-LAST:event_submissionTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add5minButton;
    private javax.swing.JLabel candidateCount;
    private javax.swing.JButton editorButton;
    private javax.swing.JButton endExamButton;
    private javax.swing.JLabel ipAddressBox;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JButton openSubFolderButton;
    private javax.swing.JLabel questionCountBox;
    private javax.swing.JButton refreshSubmissionButton;
    private javax.swing.JLabel remainingTimeBox;
    private javax.swing.JLabel startTimeBox;
    private javax.swing.JTextArea statusBox;
    private javax.swing.JTable submissionTable;
    private javax.swing.JPanel tabPage1;
    private javax.swing.JPanel tabPage2;
    private javax.swing.JPanel tabPage3;
    private javax.swing.JLabel titleBox;
    // End of variables declaration//GEN-END:variables
}
