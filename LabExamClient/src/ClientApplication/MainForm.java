/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientApplication;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;
import UtilityClass.Question;

/**
 *
 * @author Dipu
 */
@SuppressWarnings("serial")
public class MainForm extends javax.swing.JFrame {

    public javax.swing.JFrame ParentForm;
    public final Timer timer;
    public final TimerTask refreshTask;
    public final TimerTask updateTask;

    public long StopTime = -1;
    public int selectedID = -1;
    public ArrayList<Question> allQuestion;
    private int curannounceID = 0;
    public ArrayList<String> announcements = new ArrayList<>();

    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        //jsyntaxpane.DefaultSyntaxKit.initKit();

        initComponents();

        //set to full screen
        this.SetToFullFocus();

        Program.loadDefaultFolder();
        loadValues();

        refreshTask = new TimerTask() {
            @Override
            public void run()
            {
                refreshValues();
            }
        };

        updateTask = new TimerTask() {
            @Override
            public void run()
            {
                updateValues();
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(updateTask, 0, 4200);
        timer.scheduleAtFixedRate(refreshTask, 0, 600);
    }

    private void SetToFullFocus()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(MainForm.MAXIMIZED_BOTH);
        this.setSize(screenSize);
        this.setFocusableWindowState(true);
    }

    private void loadValues()
    {
        registrationNoLabel.setText("   " + ServerLink.userName + "   ");
        examTitleLabel.setText(ServerLink.getExamTitle());
        downloadQuestions();

        double dsz = answerSplitterPane.getResizeWeight() * answerSplitterPane.getHeight();
        answerSplitterPane.setDividerLocation((int) dsz);
        dsz = mainSplitterPane.getResizeWeight() * mainSplitterPane.getWidth();
        mainSplitterPane.setDividerLocation((int) dsz);
    }

    private void downloadQuestions()
    {
        allQuestion = ServerLink.getAllQuestions();
        questionList.setListData(allQuestion.toArray());
    }

    public void updateValues()
    {
        StopTime = ServerLink.getStopTime();
        announcements.addAll(ServerLink.getAnnouncements(curannounceID));
        showAnnouncement();
    }

    public void refreshValues()
    {
        long now = System.currentTimeMillis();
        if (StopTime < now)
        {
            endExam();
            return;
        }

        String remain = UtilityClass.Functions.formatTimeSpan(StopTime - now);
        remain = "    " + remain + " remaining.    ";
        remainingTimeLabel.setText(remain);
    }

    public void endExam()
    {
        StopTime = ServerLink.getStopTime();
        long now = System.currentTimeMillis();
        if (StopTime < now)
        {
            timer.cancel();
            this.dispose();

            JOptionPane.showMessageDialog(this, "Exam is over.");
            ServerLink.logoutUser();
            ParentForm.setVisible(true);
        }
    }

    public void showAnnouncement()
    {
        while (curannounceID < announcements.size())
        {
            JOptionPane.showMessageDialog(this,
                    announcements.get(curannounceID), "Announcement", JOptionPane.INFORMATION_MESSAGE);
            ++curannounceID;
        }
    }

    public void loadQuestion(Object selected)
    {
        if (selected == null)
        {
            selectedID = -1;
            questionTitleBox.setText("No Question");
            markValueBox.setText("0");
            codeEditor.setText("");
            codeEditor.setEditable(false);
            questionDescBox.setText("");
        }
        else
        {
            Question ques = (Question) selected;
            selectedID = ques.ID;
            questionDescBox.setText(ques.Body);
            questionTitleBox.setText(ques.Title);
            markValueBox.setText(Integer.toString(ques.Mark));
            openSavedAnswer(ques.ID);
            codeEditor.setEditable(true);
        }
    }

    public File getAnswerFile(int qid)
    {
        return Program.defaultPath.resolve("Answer_" + qid + ".java").toFile();
    }

    public void openSavedAnswer(int qid)
    {
        //try to open file        
        try
        {
            File file = getAnswerFile(qid);
            StringWriter sw = new StringWriter();
            FileInputStream fis = new FileInputStream(file);
            for (int data = fis.read(); data != -1; data = fis.read())
                sw.write(data);
            codeEditor.setText(sw.toString());
        }
        catch (Exception ex)
        {
            codeEditor.setText("class Main //don't change the class name\n{\n\t\n}\n");
        }
    }

    public boolean saveAnswer(int qid)
    {
        try
        {
            File file = getAnswerFile(qid);
            String source = codeEditor.getText();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(source.getBytes());
            fos.close();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public void compileAndRun()
    {
        consolePane.setText("");
        answerSplitterPane.getRightComponent().setVisible(true);

        final File codeFile = getAnswerFile(selectedID);
        try
        {
            StringWriter writer = new StringWriter();
            boolean result = CompileAndRun.CompileCode(codeFile, writer);

            String status = (result ? "[OK]" : "[Failed]");
            consolePane.append("Compilation Report : " + status + "\n");
            consolePane.append(writer.toString());

            writer.close();

            if (!result)
            {
                JOptionPane.showMessageDialog(this, "Compilation Failed.");
                return;
            }
        }
        catch (Exception ex)
        {
        }

        boolean result = CompileAndRun.RunProgram(codeFile.getParentFile());
        String status = (result ? "[OK]" : "[Failed]");
        consolePane.append("\nRun Report : " + status + "\n");
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

        topPanel = new javax.swing.JPanel();
        registrationNoLabel = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();
        examTitleLabel = new javax.swing.JLabel();
        remainingTimeLabel = new javax.swing.JLabel();
        mainSplitterPane = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        submitAnswerButton1 = new javax.swing.JButton();
        questionSplitterPane = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        questionDescBox = new javax.swing.JTextArea();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        questionList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        questionTitleBox = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        markValueBox = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        submitAnswerButton = new javax.swing.JButton();
        compileAndRunButton = new javax.swing.JButton();
        saveCodeButton = new javax.swing.JButton();
        answerSplitterPane = new javax.swing.JSplitPane();
        answerPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        consolePane = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Lab Exam");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(0, 204, 204));
        setUndecorated(true);
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener()
        {
            public void windowGainedFocus(java.awt.event.WindowEvent evt)
            {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt)
            {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        topPanel.setBackground(new java.awt.Color(0, 102, 102));
        topPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(60, 102, 122), 4, true));

        registrationNoLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        registrationNoLabel.setForeground(new java.awt.Color(204, 204, 0));
        registrationNoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        registrationNoLabel.setText("   Registration No   ");
        registrationNoLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 0, new java.awt.Color(153, 153, 255)));

        logoutButton.setFont(logoutButton.getFont().deriveFont(logoutButton.getFont().getSize()+2f));
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                logoutButtonActionPerformed(evt);
            }
        });

        examTitleLabel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 24)); // NOI18N
        examTitleLabel.setForeground(new java.awt.Color(153, 255, 255));
        examTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        examTitleLabel.setText("Exam Title");

        remainingTimeLabel.setBackground(new java.awt.Color(240, 218, 235));
        remainingTimeLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        remainingTimeLabel.setText("  Remaining Time  ");
        remainingTimeLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(174, 213, 224)));
        remainingTimeLabel.setOpaque(true);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(examTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remainingTimeLabel)
                .addGap(10, 10, 10)
                .addComponent(registrationNoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registrationNoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(examTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remainingTimeLabel))
                .addGap(5, 5, 5))
        );

        mainSplitterPane.setBackground(new java.awt.Color(204, 204, 255));
        mainSplitterPane.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 5, 5, 5, new java.awt.Color(0, 153, 153)));
        mainSplitterPane.setDividerLocation(300);
        mainSplitterPane.setDividerSize(10);
        mainSplitterPane.setResizeWeight(0.4);
        mainSplitterPane.setContinuousLayout(true);
        mainSplitterPane.setDoubleBuffered(true);
        mainSplitterPane.setOneTouchExpandable(true);
        mainSplitterPane.setOpaque(false);

        jPanel7.setBackground(java.awt.Color.cyan);
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 204, 255)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("List of Questions");

        submitAnswerButton1.setText("Refresh");
        submitAnswerButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                submitAnswerButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitAnswerButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitAnswerButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        questionSplitterPane.setBorder(null);
        questionSplitterPane.setDividerLocation(120);
        questionSplitterPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        questionSplitterPane.setContinuousLayout(true);
        questionSplitterPane.setDoubleBuffered(true);
        questionSplitterPane.setOneTouchExpandable(true);

        questionDescBox.setEditable(false);
        questionDescBox.setBackground(new java.awt.Color(239, 249, 255));
        questionDescBox.setFont(new java.awt.Font("Candara", 0, 18)); // NOI18N
        questionDescBox.setLineWrap(true);
        questionDescBox.setBorder(null);
        jScrollPane2.setViewportView(questionDescBox);

        questionSplitterPane.setRightComponent(jScrollPane2);

        questionList.setBackground(new java.awt.Color(255, 249, 255));
        questionList.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        questionList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                questionListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(questionList);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        questionSplitterPane.setTopComponent(jPanel9);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(questionSplitterPane)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(questionSplitterPane)
                .addGap(0, 0, 0))
        );

        mainSplitterPane.setLeftComponent(jPanel2);

        jPanel4.setBackground(java.awt.Color.cyan);
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 204, 255)));

        questionTitleBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        questionTitleBox.setText("No Question");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Mark :");

        markValueBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        markValueBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        markValueBox.setText("0");
        markValueBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionTitleBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(questionTitleBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel5.setBackground(new java.awt.Color(0, 233, 242));

        submitAnswerButton.setText("Submit");
        submitAnswerButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                submitAnswerButtonActionPerformed(evt);
            }
        });

        compileAndRunButton.setText("Compile and Run");
        compileAndRunButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                compileAndRunButtonActionPerformed(evt);
            }
        });

        saveCodeButton.setText("Save");
        saveCodeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveCodeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        answerSplitterPane.setBorder(null);
        answerSplitterPane.setDividerLocation(200);
        answerSplitterPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        answerSplitterPane.setResizeWeight(0.7);
        answerSplitterPane.setContinuousLayout(true);

        answerPanel.setBackground(new java.awt.Color(221, 251, 251));

        codeEditor.setEditable(false);
        codeEditor.setColumns(20);
        codeEditor.setRows(5);
        codeEditor.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        codeEditor.setSyntaxEditingStyle("text/java");
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setAntiAliasingEnabled(true);
        codeEditor.setBackground(new java.awt.Color(250, 253, 255));
        rTextScrollPane1.setViewportView(codeEditor);

        rTextScrollPane1.setFoldIndicatorEnabled(true);
        rTextScrollPane1.setLineNumbersEnabled(true);

        javax.swing.GroupLayout answerPanelLayout = new javax.swing.GroupLayout(answerPanel);
        answerPanel.setLayout(answerPanelLayout);
        answerPanelLayout.setHorizontalGroup(
            answerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(answerPanelLayout.createSequentialGroup()
                .addComponent(rTextScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        answerPanelLayout.setVerticalGroup(
            answerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(answerPanelLayout.createSequentialGroup()
                .addComponent(rTextScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        answerSplitterPane.setLeftComponent(answerPanel);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Compile and Run Report"));

        consolePane.setEditable(false);
        consolePane.setBackground(new java.awt.Color(0, 45, 50));
        consolePane.setColumns(20);
        consolePane.setForeground(new java.awt.Color(99, 255, 52));
        consolePane.setRows(5);
        consolePane.setText("Test console");
        consolePane.setCaretColor(new java.awt.Color(99, 255, 52));
        jScrollPane4.setViewportView(consolePane);
        (consolePane.getCaret()).setVisible(true);
        ((DefaultCaret)consolePane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4)
                .addGap(0, 0, 0))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        answerSplitterPane.setRightComponent(jPanel6);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(answerSplitterPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(answerSplitterPane)
                .addGap(0, 0, 0))
        );

        mainSplitterPane.setRightComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainSplitterPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainSplitterPane)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_logoutButtonActionPerformed
    {//GEN-HEADEREND:event_logoutButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this,
                "You are about to logout. Logout will add you to the blacklist.\n\n"
                + "Are you ABSOLUTELY sure you want to logout?\n",
                "LOGOUT", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
        {
            ServerLink.logoutUser();
            ParentForm.setLocationRelativeTo(null);
            ParentForm.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        StopTime = ServerLink.getStopTime();
        long now = System.currentTimeMillis();
        if (now < StopTime)
        {
            JOptionPane.showMessageDialog(this,
                    "Don't try to exit. Otherwise you will be marked as suspicious.",
                    "Lab Exam", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_formWindowClosing

    private void submitAnswerButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_submitAnswerButtonActionPerformed
    {//GEN-HEADEREND:event_submitAnswerButtonActionPerformed
        if (selectedID == -1) return;
        int result = JOptionPane.showConfirmDialog(this, "Are you sure to submit this answer?",
                "Submit Answer", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
        {
            boolean res = ServerLink.submitAnswer(selectedID, codeEditor.getText());
            if (res) JOptionPane.showMessageDialog(this, "Submission Successful.");
            else JOptionPane.showMessageDialog(this, "Submission Failed");
        }
    }//GEN-LAST:event_submitAnswerButtonActionPerformed

    private void questionListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_questionListValueChanged
    {//GEN-HEADEREND:event_questionListValueChanged
        loadQuestion(questionList.getSelectedValue());
    }//GEN-LAST:event_questionListValueChanged

    private void formWindowLostFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowLostFocus
    {//GEN-HEADEREND:event_formWindowLostFocus
        this.SetToFullFocus();
    }//GEN-LAST:event_formWindowLostFocus

    private void saveCodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveCodeButtonActionPerformed
    {//GEN-HEADEREND:event_saveCodeButtonActionPerformed
        if (selectedID != -1) saveAnswer(selectedID);
    }//GEN-LAST:event_saveCodeButtonActionPerformed

    private void compileAndRunButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_compileAndRunButtonActionPerformed
    {//GEN-HEADEREND:event_compileAndRunButtonActionPerformed
        if (selectedID != -1) compileAndRun();
    }//GEN-LAST:event_compileAndRunButtonActionPerformed

    private void submitAnswerButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_submitAnswerButton1ActionPerformed
    {//GEN-HEADEREND:event_submitAnswerButton1ActionPerformed
        downloadQuestions();
    }//GEN-LAST:event_submitAnswerButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel answerPanel;
    private javax.swing.JSplitPane answerSplitterPane;
    private final org.fife.ui.rsyntaxtextarea.RSyntaxTextArea codeEditor = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
    private javax.swing.JButton compileAndRunButton;
    private javax.swing.JTextArea consolePane;
    private javax.swing.JLabel examTitleLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton logoutButton;
    private javax.swing.JSplitPane mainSplitterPane;
    private javax.swing.JLabel markValueBox;
    private javax.swing.JTextArea questionDescBox;
    private javax.swing.JList questionList;
    private javax.swing.JSplitPane questionSplitterPane;
    private javax.swing.JLabel questionTitleBox;
    private final org.fife.ui.rtextarea.RTextScrollPane rTextScrollPane1 = new org.fife.ui.rtextarea.RTextScrollPane();
    private javax.swing.JLabel registrationNoLabel;
    private javax.swing.JLabel remainingTimeLabel;
    private javax.swing.JButton saveCodeButton;
    private javax.swing.JButton submitAnswerButton;
    private javax.swing.JButton submitAnswerButton1;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
