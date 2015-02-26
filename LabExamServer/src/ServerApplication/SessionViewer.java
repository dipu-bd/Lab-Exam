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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import UtilityClass.Candidate;
import UtilityClass.Functions;
import UtilityClass.UserChangeEvent;
import UtilityClass.UserChangedHandler;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Dipu
 */
public class SessionViewer extends javax.swing.JFrame {

    public JFrame ParentForm;
    private final Timer timer;
    private final Logger logger;
    public DefaultTableModel tableModel;

    /**
     * Creates new form SessionViewer
     */
    public SessionViewer()
    {
        this.timer = new Timer();
        this.logger = Logger.getLogger("LabExam");

        initComponents();
        
        SetToFullFocus();
        initiateOthers();
        LoadValues();
    }

    public void SetToFullFocus()
    {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setFocusableWindowState(true);
    }

    /**
     * Initialize this frame. It loads all data needed for examination server.
     * It creates a new server socket and waits for candidates to connect.
     *
     * Also various other initialization task is performed from here.
     */
    private void initiateOthers()
    {
        //initialize logger        
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord lr)
            {
                String msg = (new Date(lr.getMillis())).toString() + " : ";
                msg += lr.getLevel().getName() + " : ";
                msg += lr.getMessage() + "\n";
                statusBox.append(msg);
            }

            @Override
            public void flush()
            {
            }

            @Override
            public void close() throws SecurityException
            {
            }
        });

        //initialize timer
        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {
                SetRemainingTime();
            }
        };
        timer.scheduleAtFixedRate(tt, 0, 500);

        //initialize server 
        LabExamServer.initialize();

        //initialize examinee table        
        tableModel = (DefaultTableModel) candidateTable.getModel();
        loadCandidateList();
        CurrentExam.addUserChangedHandler(new UserChangedHandler() {
            @Override
            public void userChanged(UserChangeEvent ae)
            {
                loadCandidateList();
            }
        });
    }

    public final void LoadValues()
    {
        titleBox.setText(CurrentExam.curExam.ExamTitle);
        totalMarkBox.setText(Integer.toString(CurrentExam.curExam.getTotalMarks()));
        quesCountBox.setText(Integer.toString(CurrentExam.curExam.allQuestion.size()));
        startTimeBox.setText(CurrentExam.curExam.StartTime.toString());

        String candidate = "Total number of candidates : ";
        candidate += CurrentExam.curExam.allCandidate.size();
        candidateCount.setText(candidate);
    }

    private void SetRemainingTime()
    {
        try
        {
            long now = System.currentTimeMillis();
            long start = CurrentExam.curExam.StartTime.getTime();
            long stop = start + CurrentExam.curExam.Duration * 60000;

            String msg = "";
            if (now < start) //exam waiting
            {
                msg = "Exam will start in ";
                msg += Functions.formatTimeSpan(start - now);
                remainingTimeBox.setText(msg);
                endExamButton.setText("Exit");
            }
            else if (now > stop) //exam finished
            {
                msg = "Exam is finished.";
                remainingTimeBox.setText(msg);
                endExamButton.setText("Exit");
            }
            else //exam is running
            {
                msg = "Exam is running... ";
                msg += Functions.formatTimeSpan(stop - now);
                msg += " remaining.";
                remainingTimeBox.setText(msg);
                endExamButton.setText("Stop Exam");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(SessionViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays the candidate list in candidate table
     */
    private void loadCandidateList()
    {
        //set status
        String cdc = "Status : ";
        int total = CurrentExam.curExam.allCandidate.size();
        int connected = CurrentExam.logins.size();
        cdc += String.format("%d out of %d candidates are connected.", total, connected);
        candidateCount.setText(cdc);

        //clear up previous data
        tableModel.setRowCount(0);

        //show list 
        for (Candidate cd : CurrentExam.curExam.allCandidate)
        {
            String status = "Disconnected";
            if (CurrentExam.logins.contains(cd.uid))
            {
                status = "Connected";
            }
            tableModel.addRow(new Object[]
            {
                cd.uid, cd.name, cd.regno, cd.password, status
            });
        }
    }

    private void saveToTextFile()
    {
        try
        {
            JFileChooser saveFile = new JFileChooser();
            saveFile.setMultiSelectionEnabled(false);
            saveFile.setAcceptAllFileFilterUsed(false);
            saveFile.setSelectedFile(new File("examinee.txt"));
            saveFile.setFileFilter(new FileNameExtensionFilter("Text File", "txt"));
            if (saveFile.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                String data = CurrentExam.printUsers();
                File file = saveFile.getSelectedFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data.getBytes());
                fos.close();
            }
        }
        catch (HeadlessException | IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE,
                    "Error while saving passwords", ex);
        }
    }

    private void exitApplication()
    {
        int result = JOptionPane.YES_OPTION;
        if (CurrentExam.curExam.isRunning())
        {
            result = JOptionPane.showConfirmDialog(this,
                    "Are you going to stop the current examination?",
                    "Exit Application", JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.YES_OPTION)
        {
            LabExamServer.StopListening();
            timer.cancel();
            this.dispose();
            ParentForm.setVisible(true);
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

        jPanel9 = new javax.swing.JPanel();
        endExamButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusBox = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        candidateTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        refreshExamineeButton = new javax.swing.JButton();
        saveToTextButton = new javax.swing.JButton();
        candidateCount = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        announceBox = new javax.swing.JTextArea();
        announceButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        announceList = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        titleBox = new javax.swing.JLabel();
        totalMarkBox = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        quesCountBox = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        startTimeBox = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        add10minButton = new javax.swing.JButton();
        remainingTimeBox = new javax.swing.JLabel();

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
        endExamButton.setText("Exit");
        endExamButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                endExamButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(endExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(endExamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jTabbedPane1.setBackground(new java.awt.Color(195, 210, 221));
        jTabbedPane1.setOpaque(true);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Exam Status", jPanel1);

        candidateTable.setAutoCreateRowSorter(true);
        candidateTable.setBackground(new java.awt.Color(223, 255, 255));
        candidateTable.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        candidateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "ID", "Candidate", "Registration No", "Password", "Status"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        candidateTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        candidateTable.setCellSelectionEnabled(true);
        candidateTable.setFillsViewportHeight(true);
        candidateTable.setFocusable(false);
        candidateTable.setGridColor(java.awt.Color.cyan);
        candidateTable.setIntercellSpacing(new java.awt.Dimension(3, 3));
        candidateTable.setRowHeight(20);
        candidateTable.setSelectionBackground(java.awt.Color.cyan);
        candidateTable.setSelectionForeground(new java.awt.Color(0, 0, 51));
        candidateTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(candidateTable);
        if (candidateTable.getColumnModel().getColumnCount() > 0)
        {
            candidateTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            candidateTable.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        jPanel6.setBackground(new java.awt.Color(186, 242, 242));

        refreshExamineeButton.setText("Refresh");
        refreshExamineeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refreshExamineeButtonActionPerformed(evt);
            }
        });

        saveToTextButton.setText("Save to Text File");
        saveToTextButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveToTextButtonActionPerformed(evt);
            }
        });

        candidateCount.setText(" ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(candidateCount, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(refreshExamineeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveToTextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveToTextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshExamineeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(candidateCount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedPane1.addTab("Candidates", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 795, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Candidate Status", jPanel3);

        jPanel7.setBackground(new java.awt.Color(237, 240, 230));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Announcement Message :"));
        jPanel7.setToolTipText("");

        jLabel1.setText("Write your message here :");

        jScrollPane3.setBackground(new java.awt.Color(240, 248, 229));
        jScrollPane3.setOpaque(false);

        announceBox.setBackground(new java.awt.Color(255, 252, 244));
        announceBox.setColumns(20);
        announceBox.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        announceBox.setLineWrap(true);
        announceBox.setRows(1);
        announceBox.setTabSize(4);
        jScrollPane3.setViewportView(announceBox);

        announceButton.setText("Announce");
        announceButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                announceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                        .addGap(3, 3, 3)
                        .addComponent(announceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane3))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(announceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setBackground(new java.awt.Color(177, 245, 245));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Past Announcement"));

        announceList.setBackground(new java.awt.Color(235, 253, 255));
        announceList.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        announceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        announceList.setSelectionBackground(java.awt.Color.cyan);
        jScrollPane4.setViewportView(announceList);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jScrollPane4)
                .addGap(3, 3, 3))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedPane1.addTab("Announcements", jPanel4);

        jPanel5.setBackground(new java.awt.Color(163, 236, 239));

        titleBox.setBackground(new java.awt.Color(191, 243, 250));
        titleBox.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        titleBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleBox.setText("Title");
        titleBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 1, true));
        titleBox.setOpaque(true);

        totalMarkBox.setBackground(new java.awt.Color(236, 244, 251));
        totalMarkBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        totalMarkBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalMarkBox.setText("10");
        totalMarkBox.setToolTipText("Total marked of all questions");
        totalMarkBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));
        totalMarkBox.setOpaque(true);
        totalMarkBox.setPreferredSize(new java.awt.Dimension(100, 23));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Question Count :");

        quesCountBox.setBackground(new java.awt.Color(247, 248, 251));
        quesCountBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        quesCountBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        quesCountBox.setText("10");
        quesCountBox.setToolTipText("Number of questions");
        quesCountBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));
        quesCountBox.setOpaque(true);
        quesCountBox.setPreferredSize(new java.awt.Dimension(20, 23));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Total Marks :");

        startTimeBox.setBackground(new java.awt.Color(247, 248, 251));
        startTimeBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        startTimeBox.setForeground(new java.awt.Color(51, 51, 255));
        startTimeBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startTimeBox.setText("2/18/15 12:12 PM");
        startTimeBox.setToolTipText("Click to edit start time");
        startTimeBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));
        startTimeBox.setOpaque(true);
        startTimeBox.setPreferredSize(new java.awt.Dimension(120, 23));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Start Time :");

        add10minButton.setText("Extend 10 minutes");
        add10minButton.setFocusable(false);
        add10minButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                add10minButtonActionPerformed(evt);
            }
        });

        remainingTimeBox.setBackground(new java.awt.Color(204, 255, 204));
        remainingTimeBox.setFont(new java.awt.Font("Segoe UI Semibold", 2, 14)); // NOI18N
        remainingTimeBox.setForeground(new java.awt.Color(102, 51, 0));
        remainingTimeBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        remainingTimeBox.setText("Exam will start in X minutes");
        remainingTimeBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        remainingTimeBox.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quesCountBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalMarkBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startTimeBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(remainingTimeBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add10minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(titleBox)
                .addGap(3, 3, 3)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(quesCountBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(totalMarkBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(startTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remainingTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(add10minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        totalMarkBox.getAccessibleContext().setAccessibleName("100");
        quesCountBox.getAccessibleContext().setAccessibleName("100");
        remainingTimeBox.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void endExamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endExamButtonActionPerformed
        exitApplication();
    }//GEN-LAST:event_endExamButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitApplication();
    }//GEN-LAST:event_formWindowClosing

    private void add10minButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add10minButtonActionPerformed
        try
        {
            long time = CurrentExam.curExam.StartTime.getTime();
            long now = System.currentTimeMillis();
            if (now > time)
            {
                CurrentExam.curExam.Duration += 10;
            }
            else
            {
                time += 10 * 60 * 1000; //10 min in milis
                CurrentExam.curExam.StartTime.setTime(time);
                startTimeBox.setText(CurrentExam.curExam.StartTime.toString());
            }
            CurrentExam.Save();
            logger.log(Level.INFO, "Added extra 10 minutes");
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Failed to save data", ex);
        }
    }//GEN-LAST:event_add10minButtonActionPerformed

    private void refreshExamineeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshExamineeButtonActionPerformed
        loadCandidateList();
    }//GEN-LAST:event_refreshExamineeButtonActionPerformed

    private void saveToTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToTextButtonActionPerformed
        saveToTextFile();
    }//GEN-LAST:event_saveToTextButtonActionPerformed

    @SuppressWarnings("unchecked")
    private void announceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_announceButtonActionPerformed
    {//GEN-HEADEREND:event_announceButtonActionPerformed
        if (CurrentExam.curExam.isRunning())
        {
            String message = announceBox.getText();
            CurrentExam.announcements.add(message);
            announceList.setListData(CurrentExam.announcements.toArray());
            announceBox.setText("");
        }
        else
        {
            JOptionPane.showMessageDialog(this,
                    "You can only announce messages when the exam is running.");
        }
    }//GEN-LAST:event_announceButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add10minButton;
    private javax.swing.JTextArea announceBox;
    private javax.swing.JButton announceButton;
    private javax.swing.JList announceList;
    private javax.swing.JLabel candidateCount;
    private javax.swing.JTable candidateTable;
    private javax.swing.JButton endExamButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel quesCountBox;
    private javax.swing.JButton refreshExamineeButton;
    private javax.swing.JLabel remainingTimeBox;
    private javax.swing.JButton saveToTextButton;
    private javax.swing.JLabel startTimeBox;
    private javax.swing.JTextArea statusBox;
    private javax.swing.JLabel titleBox;
    private javax.swing.JLabel totalMarkBox;
    // End of variables declaration//GEN-END:variables
}
