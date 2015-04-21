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
import Utilities.Question;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

/**
 *
 * @author Dipu
 */
@SuppressWarnings("serial")
public class MainForm extends javax.swing.JFrame
{

    javax.swing.JFrame ParentForm;
    private final Timer timer = new Timer();

    long StopTime = -1;
    ArrayList<Question> allQuestion;
    private Question selectedQues = null;
    private DefaultMutableTreeNode selectedNode = null;
    final SwingController pdfController = new SwingController();
    private int curannounceID = 0;
    ArrayList<String> announcements = new ArrayList<>();

    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        //init form
        initComponents();
        setToFullFocus();
        initPdfControl();
        initFileExplorer();

        //load default values
        loadValues();

        //set up timer to begin timer task  
        initTimerTasks();
    }

    public void endExam()
    {
        StopTime = ServerLink.getStopTime();
        long now = System.currentTimeMillis();
        if (StopTime < now) {
            KeyHook.unblockWindowsKey();

            timer.cancel();
            this.dispose();

            JOptionPane.showMessageDialog(this, "Exam is over.");
            ServerLink.logoutUser();
            ParentForm.setVisible(true);
        }
    }

    private void initTimerTasks()
    {
        //update data in display 
        TimerTask updateTask = new TimerTask()
        {
            @Override
            public void run()
            {
                updateValues();
            }
        };
        timer.scheduleAtFixedRate(updateTask, 0, 4200);

        //download data periodically
        TimerTask refreshTask = new TimerTask()
        {
            @Override
            public void run()
            {
                refreshValues();
            }
        };
        timer.scheduleAtFixedRate(refreshTask, 0, 600);
    }

    private void setToFullFocus()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(MainForm.MAXIMIZED_BOTH);
        this.toFront();
        this.setSize(screenSize);
        this.requestFocus();
        this.requestFocusInWindow();
        this.setAlwaysOnTop(true);

        KeyHook.blockWindowsKey();
    }

    private void initPdfControl()
    {
        //factory to build all controls
        SwingViewBuilder factory = new SwingViewBuilder(pdfController);
        pdfController.setPageViewMode(2, true);

        //build tool bar        
        descToolBar.add(factory.buildZoomOutButton());
        descToolBar.add(factory.buildZoomCombBox());
        descToolBar.add(factory.buildZoomInButton());
        descToolBar.add(factory.buildFitWidthButton());
        descToolBar.add(factory.buildFitPageButton());
        descToolBar.add(factory.buildFitActualSizeButton());
        descToolBar.add(factory.buildPanToolButton());
        descToolBar.add(factory.buildTextSelectToolButton());

        //add pdf viewer panel           
        javax.swing.JSplitPane jsp = factory.buildUtilityAndDocumentSplitPane(false);
        jsp.setPreferredSize(new Dimension(10, 10));
        pdfPanel.setViewportView(jsp);
    }

    private void initFileExplorer()
    {
        explorerTree.setModel(new DefaultTreeModel(null));
        explorerTree.setCellRenderer(new MyTreeRenderer());
        explorerTree.putClientProperty("JTree.lineStyle", "Angled");
        explorerTree.getSelectionModel().setSelectionMode(1); //javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION        
        //explorerTree.setComponentPopupMenu(explorerPopup);

        //build up popup menu
        explorerPopup.add(newFolderMenu);
        explorerPopup.add(new JPopupMenu.Separator());
        explorerPopup.add(newFileMenu);
        explorerPopup.add(newJavaMenu);
        explorerPopup.add(newCPPMenu);
        explorerPopup.add(newCMenu);
        explorerPopup.add(new JPopupMenu.Separator());
        explorerPopup.add(renameMenu);
        explorerPopup.add(new JPopupMenu.Separator());
        explorerPopup.add(deleteMenu);
    }

    private void loadValues()
    {
        registrationNoLabel.setText("   " + ServerLink.getUsername() + "   ");
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
        if (!allQuestion.isEmpty()) {
            questionList.setSelectedIndex(0);
        }
    }

    public void updateValues()
    {
        StopTime = ServerLink.getStopTime();
        announcements.addAll(ServerLink.getAnnouncements(curannounceID));
        showAnnouncement();
    }

    void refreshValues()
    {
        long now = System.currentTimeMillis();
        if (StopTime < now) {
            endExam();
            return;
        }

        String remain = Utilities.Functions.formatTimeSpan(StopTime - now);
        remain = "    " + remain + " remaining.    ";
        remainingTimeLabel.setText(remain);
    }

    void showAnnouncement()
    {
        while (curannounceID < announcements.size()) {
            JOptionPane.showMessageDialog(this,
                    announcements.get(curannounceID), "Announcement", JOptionPane.INFORMATION_MESSAGE);
            ++curannounceID;
        }
    }

    void loadQuestion(Object selected)
    {
        selectedQues = (Question) selected;

        //clear values
        questionTitleBox.setText("No Question");
        markValueBox.setText("0");
        codeEditor.setText("");
        codeEditor.setEditable(false);
        if (selectedQues == null) {
            return;
        }

        //set new values        
        questionTitleBox.setText(selectedQues.Title);
        markValueBox.setText(Integer.toString(selectedQues.Mark));
        loadFileExplorer();

        //show pdf description of question
        if (selectedQues.Body != null) {
            pdfController.openDocument(selectedQues.Body, 0,
                    selectedQues.Body.length, selectedQues.Title, null);
        }
    }

    void loadFileExplorer()
    {
        if (selectedQues == null) {
            return;
        }

        try {
            File qpath = Program.defaultPath.resolve("Question_" + selectedQues.ID).toFile();
            qpath.mkdirs();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeNodeData(qpath, selectedQues.Title));
            loadFileList(root, qpath);
            explorerTree.setModel(new DefaultTreeModel(root));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void loadFileList(DefaultMutableTreeNode node, File dir)
    {
        if (!dir.isDirectory()) {
            node.setAllowsChildren(false);
            return;
        }

        node.setAllowsChildren(true);
        for (File f : dir.listFiles()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeNodeData(f));
            loadFileList(child, f);
            node.add(child);
        }
    }

    void openFileInEditor(File file)
    {
        try {
            //get text
            StringWriter sw = new StringWriter();
            FileInputStream fis = new FileInputStream(file);
            for (int data = fis.read(); data != -1; data = fis.read()) {
                sw.write(data);
            }
            fis.close();
            sw.close();

            //set data to view  
            codeEditor.setEditable(true);
            codeEditor.setText(sw.toString());
            questionTitleBox.setText(selectedQues.Title + " >> " + file.getName());

            //set highlighting style
            String nam = file.getName().toLowerCase();
            if (nam.endsWith(".java")) {
                codeEditor.setSyntaxEditingStyle("text/java");
            }
            else if (nam.endsWith(".cpp")) {
                codeEditor.setSyntaxEditingStyle("text/cpp");
            }
            else if (nam.endsWith(".c")) {
                codeEditor.setSyntaxEditingStyle("text/c");
            }
            else {
                codeEditor.setSyntaxEditingStyle("text/plain");
            }
        }
        catch (Exception ex) {
            codeEditor.setText("Create a new file and select it to edit.");
            codeEditor.setEditable(false);
            questionTitleBox.setText(selectedQues.Title);
        }
    }

    void saveFileFromEditor()
    {
        try {
            if (selectedNode == null) return;
            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
            if (!file.isFile()) return;

            String source = codeEditor.getText();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(source.getBytes());
            fos.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void closeOpenedFile()
    {
        codeEditor.setText("");
        codeEditor.setEditable(false);
        questionTitleBox.setText(selectedQues.Title);
    }

    void compileAndRun()
    {
        if (selectedNode == null) return;
        File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
        if (!file.isFile()) return;

        consolePane.setText("");
        answerSplitterPane.getRightComponent().setVisible(true);

        try {
            StringWriter writer = new StringWriter();
            boolean result = CompileAndRun.CompileCode(file, writer);

            String status = (result ? "[OK]" : "[Failed]");
            consolePane.append("Compilation Report : " + status + "\n");
            consolePane.append(writer.toString());

            writer.close();

            if (!result) {
                JOptionPane.showMessageDialog(this, "Compilation Failed.");
                return;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean result = CompileAndRun.RunProgram(file);
        String status = (result ? "[OK]" : "[Failed]");
        consolePane.append("\nRun Report : " + status + "\n");
    }

    private void attemptSubmitAnswer()
    {
        if (selectedQues == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure to submit answers to \"" + selectedQues.Title + "\"?",
                "Submit Answer", JOptionPane.YES_NO_OPTION);
        try {
            File qpath = Program.defaultPath.resolve("Question_" + selectedQues.ID).toFile();
            ArrayList<Object> data = new ArrayList<>();
            ArrayList<Object> files = new ArrayList<>();
            listAllFiles(qpath, files, data);

            if (result == JOptionPane.YES_OPTION) {
                boolean res = ServerLink.submitAnswer(selectedQues.ID, files.toArray(), data.toArray());
                if (res) {
                    JOptionPane.showMessageDialog(this, "Submission Successful.");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Submission Failed");
                }
            }
        }
        catch (IOException | HeadlessException ex) {
            ex.printStackTrace();
        }
    }

    private void listAllFiles(File f, ArrayList<Object> list, ArrayList<Object> data) throws IOException
    {
        if (!f.exists()) return;
        if (f.isFile()) {
            data.add(Functions.readFully(new FileInputStream(f)));
            int deflen = Program.defaultPath.toFile().getAbsolutePath().length();
            list.add(f.getAbsolutePath().substring(deflen + 1)); //+1 is for separator
        }
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                listAllFiles(c, list, data);
            }
        }
    }

    private void createNewFolder()
    {
        try {
            if (selectedNode == null) return;

            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
            if (!file.isDirectory()) return;

            //create new folder
            String name = JOptionPane.showInputDialog(this, "Name of the folder to create", "New Folder");
            name = name.replaceAll("[\\\\/:*?\\\"<>|]", "").trim();
            File f = (file.toPath().resolve(name)).toFile();
            if (f.exists()) {
                JOptionPane.showMessageDialog(this, "Another folder with name \"" + name + "\" already exists");
                return;
            }
            f.mkdirs();

            //add node to display
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeNodeData(f), true);
            DefaultTreeModel model = (DefaultTreeModel) explorerTree.getModel();
            model.insertNodeInto(node, selectedNode, selectedNode.getChildCount());
        }
        catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
            ex.printStackTrace();
        }
    }

    private void createNewFile(String ext)
    {
        try {
            if (selectedNode == null) return;

            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
            if (!file.isDirectory()) return;

            //create new file
            if (ext == null || ext.isEmpty()) ext = ".txt";
            String name = JOptionPane.showInputDialog(this, "Name of the file to create", "New File");
            name = name.replaceAll("[\\\\/:*?\\\"<>|]", "").trim();
            File f = (file.toPath().resolve(name + ext)).toFile();
            if (name.isEmpty() || f.exists()) {
                JOptionPane.showMessageDialog(this, "Another file with name \"" + name + ext + "\" already exists!");
                return;
            }
            if (!f.createNewFile()) throw new FileNotFoundException();

            //add node to display
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeNodeData(f), true);
            DefaultTreeModel model = (DefaultTreeModel) explorerTree.getModel();
            model.insertNodeInto(node, selectedNode, selectedNode.getChildCount());
        }
        catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
            ex.printStackTrace();
        }
    }

    private void renameSelectedFile()
    {
        try {

            //get the file to rename
            if (selectedNode == null || selectedNode.getLevel() == 0) return;
            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();

            //rename the file
            String name = JOptionPane.showInputDialog(this, "Rename a file into another", file.getName());
            name = name.replaceAll("[\\\\:*?\\\"<>|/]", "").trim();
            File newFile = file.toPath().resolveSibling(name).toFile();
            closeOpenedFile();
            if (!file.renameTo(newFile)) {
                JOptionPane.showMessageDialog(this, "Couldn't rename from \"" + file.getName() + "\" to \"" + newFile.getName() + "\"!");
                return;
            }

            //reload the view
            DefaultTreeModel model = (DefaultTreeModel) explorerTree.getModel();
            selectedNode.setUserObject(new TreeNodeData(newFile));
            selectedNode.removeAllChildren();
            loadFileList(selectedNode, newFile);
            model.reload(selectedNode);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
            ex.printStackTrace();
        }
    }

    private void deleteSelectedFile()
    {
        try {
            if (selectedNode == null || selectedNode.getLevel() == 0) return;

            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete \"" + file.getName() + "\"?") == JOptionPane.NO_OPTION) {
                return;
            }

            //delete all files in directories
            closeOpenedFile();
            deleteFileRecursively(file);

            //remove node from view
            DefaultTreeModel model = (DefaultTreeModel) explorerTree.getModel();
            model.removeNodeFromParent(selectedNode);
            selectedNode = null;
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
            ex.printStackTrace();
        }
    }

    private void deleteFileRecursively(File f) throws java.io.IOException
    {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFileRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
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

        newFolderMenu = new javax.swing.JMenuItem();
        newFileMenu = new javax.swing.JMenuItem();
        newCPPMenu = new javax.swing.JMenuItem();
        newJavaMenu = new javax.swing.JMenuItem();
        newCMenu = new javax.swing.JMenuItem();
        renameMenu = new javax.swing.JMenuItem();
        deleteMenu = new javax.swing.JMenuItem();
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
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        questionList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        descToolBar = new javax.swing.JToolBar();
        pdfPanel = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        questionTitleBox = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        markValueBox = new javax.swing.JLabel();
        fullscreenButton = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        submitAnswerButton = new javax.swing.JButton();
        compileAndRunButton = new javax.swing.JButton();
        saveCodeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        themeChooser = new javax.swing.JComboBox();
        answerSplitterPane = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        consolePane = new javax.swing.JTextArea();
        codeSplitPane = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        explorerTree = new javax.swing.JTree();
        PaneForCodeEditor = new org.fife.ui.rtextarea.RTextScrollPane();
        codeEditor = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        newFolderMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/folder.png"))); // NOI18N
        newFolderMenu.setText("New Folder");
        newFolderMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newFolderMenuActionPerformed(evt);
            }
        });

        newFileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/file.png"))); // NOI18N
        newFileMenu.setText("New File");
        newFileMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newFileMenuActionPerformed(evt);
            }
        });

        newCPPMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/cpp.png"))); // NOI18N
        newCPPMenu.setText("New C++ File");
        newCPPMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCPPMenuActionPerformed(evt);
            }
        });

        newJavaMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/java.png"))); // NOI18N
        newJavaMenu.setText("New Java File");
        newJavaMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newJavaMenuActionPerformed(evt);
            }
        });

        newCMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/ansi_c.png"))); // NOI18N
        newCMenu.setText("New Ansi C File");
        newCMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCMenuActionPerformed(evt);
            }
        });

        renameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rename.png"))); // NOI18N
        renameMenu.setText("Rename");
        renameMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                renameMenuActionPerformed(evt);
            }
        });

        deleteMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/delete.png"))); // NOI18N
        deleteMenu.setText("Delete");
        deleteMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteMenuActionPerformed(evt);
            }
        });

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
        topPanel.setAutoscrolls(true);

        registrationNoLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        registrationNoLabel.setForeground(new java.awt.Color(204, 204, 0));
        registrationNoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        registrationNoLabel.setText("   Registration No   ");
        registrationNoLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 0, new java.awt.Color(153, 153, 255)));

        logoutButton.setFont(logoutButton.getFont().deriveFont(logoutButton.getFont().getSize()+2f));
        logoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/logout.png"))); // NOI18N
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
                .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        mainSplitterPane.setDividerLocation(250);
        mainSplitterPane.setDividerSize(10);
        mainSplitterPane.setResizeWeight(0.45);
        mainSplitterPane.setContinuousLayout(true);
        mainSplitterPane.setDoubleBuffered(true);
        mainSplitterPane.setOneTouchExpandable(true);
        mainSplitterPane.setOpaque(false);

        jPanel7.setBackground(java.awt.Color.cyan);
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 204, 255)));
        jPanel7.setAutoscrolls(true);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("List of Questions");

        submitAnswerButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/reload.png"))); // NOI18N
        submitAnswerButton1.setText("Redownload");
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
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        questionList.setBackground(new java.awt.Color(255, 249, 255));
        questionList.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
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

        descToolBar.setFloatable(false);
        descToolBar.setRollover(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(descToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
            .addComponent(pdfPanel, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(descToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pdfPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
        );

        questionSplitterPane.setRightComponent(jPanel1);

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
        jPanel4.setAutoscrolls(true);

        questionTitleBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        questionTitleBox.setText("No Question");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Mark :");

        markValueBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        markValueBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        markValueBox.setText("0");
        markValueBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        fullscreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/fullscreen.png"))); // NOI18N
        fullscreenButton.setText("Fullscreen");
        fullscreenButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fullscreenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionTitleBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fullscreenButton)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fullscreenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(questionTitleBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel5.setBackground(new java.awt.Color(0, 233, 242));
        jPanel5.setAutoscrolls(true);

        submitAnswerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/submit.png"))); // NOI18N
        submitAnswerButton.setText("Submit");
        submitAnswerButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                submitAnswerButtonActionPerformed(evt);
            }
        });

        compileAndRunButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/runtest.png"))); // NOI18N
        compileAndRunButton.setText("Compile and Run");
        compileAndRunButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                compileAndRunButtonActionPerformed(evt);
            }
        });

        saveCodeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/save.png"))); // NOI18N
        saveCodeButton.setText("Save");
        saveCodeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveCodeButtonActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Theme :");

        themeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Light", "Dark" }));
        themeChooser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                themeChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(themeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        answerSplitterPane.setBorder(null);
        answerSplitterPane.setDividerLocation(200);
        answerSplitterPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        answerSplitterPane.setResizeWeight(0.75);
        answerSplitterPane.setContinuousLayout(true);

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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        answerSplitterPane.setRightComponent(jPanel6);

        codeSplitPane.setDividerLocation(180);

        explorerTree.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        explorerTree.setToolTipText("");
        explorerTree.setAutoscrolls(true);
        explorerTree.setRowHeight(22);
        explorerTree.setShowsRootHandles(true);
        explorerTree.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                explorerTreeMouseClicked(evt);
            }
        });
        explorerTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt)
            {
                explorerTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(explorerTree);

        codeSplitPane.setLeftComponent(jScrollPane2);

        codeEditor.setEditable(false);
        codeEditor.setColumns(20);
        codeEditor.setRows(5);
        codeEditor.setText("class Main //don't rename the main class\n{\n\n}");
        codeEditor.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        codeEditor.setSyntaxEditingStyle("text/java");
        codeEditor.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                codeEditorKeyTyped(evt);
            }
        });
        PaneForCodeEditor.setViewportView(codeEditor);

        codeSplitPane.setRightComponent(PaneForCodeEditor);

        answerSplitterPane.setLeftComponent(codeSplitPane);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(answerSplitterPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(answerSplitterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
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
        if (result == JOptionPane.YES_OPTION) {
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
        if (now < StopTime) {
            JOptionPane.showMessageDialog(this,
                    "Don't try to exit. Otherwise you will be marked as suspicious.",
                    "Lab Exam", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_formWindowClosing

    private void submitAnswerButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_submitAnswerButtonActionPerformed
    {//GEN-HEADEREND:event_submitAnswerButtonActionPerformed
        attemptSubmitAnswer();
    }//GEN-LAST:event_submitAnswerButtonActionPerformed

    private void questionListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_questionListValueChanged
    {//GEN-HEADEREND:event_questionListValueChanged
        loadQuestion(questionList.getSelectedValue());
    }//GEN-LAST:event_questionListValueChanged

    private void formWindowLostFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowLostFocus
    {//GEN-HEADEREND:event_formWindowLostFocus
        this.setToFullFocus();
    }//GEN-LAST:event_formWindowLostFocus

    private void saveCodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveCodeButtonActionPerformed
    {//GEN-HEADEREND:event_saveCodeButtonActionPerformed
        saveFileFromEditor();
    }//GEN-LAST:event_saveCodeButtonActionPerformed

    private void compileAndRunButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_compileAndRunButtonActionPerformed
    {//GEN-HEADEREND:event_compileAndRunButtonActionPerformed
        compileAndRun();
    }//GEN-LAST:event_compileAndRunButtonActionPerformed

    private void submitAnswerButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_submitAnswerButton1ActionPerformed
    {//GEN-HEADEREND:event_submitAnswerButton1ActionPerformed
        downloadQuestions();
    }//GEN-LAST:event_submitAnswerButton1ActionPerformed

    private void themeChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_themeChooserActionPerformed
    {//GEN-HEADEREND:event_themeChooserActionPerformed
        try {
            if (themeChooser.getSelectedIndex() == 0) {
                Theme.load(getClass().getResourceAsStream("/Resources/light.xml")).apply(codeEditor);
                codeEditor.setFont(new Font("Consolas", Font.PLAIN, 14));
            }
            else {
                Theme.load(getClass().getResourceAsStream("/Resources/dark.xml")).apply(codeEditor);
                codeEditor.setFont(new Font("Consolas", Font.PLAIN, 14));
            }
        }
        catch (Exception ex) {
        }
    }//GEN-LAST:event_themeChooserActionPerformed

    private void explorerTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_explorerTreeValueChanged
    {//GEN-HEADEREND:event_explorerTreeValueChanged
        closeOpenedFile();
        if (selectedQues != null && explorerTree.getSelectionPath() != null) {
            selectedNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();
            File file = ((TreeNodeData) selectedNode.getUserObject()).getFile();
            if (file.isFile()) {
                openFileInEditor(file);
            }
        }
    }//GEN-LAST:event_explorerTreeValueChanged

    private void newFolderMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newFolderMenuActionPerformed
    {//GEN-HEADEREND:event_newFolderMenuActionPerformed
        createNewFolder();
    }//GEN-LAST:event_newFolderMenuActionPerformed

    private void newFileMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newFileMenuActionPerformed
    {//GEN-HEADEREND:event_newFileMenuActionPerformed
        createNewFile(null);
    }//GEN-LAST:event_newFileMenuActionPerformed

    private void newCPPMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCPPMenuActionPerformed
    {//GEN-HEADEREND:event_newCPPMenuActionPerformed
        createNewFile(".cpp");
    }//GEN-LAST:event_newCPPMenuActionPerformed

    private void newJavaMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newJavaMenuActionPerformed
    {//GEN-HEADEREND:event_newJavaMenuActionPerformed
        createNewFile(".java");
    }//GEN-LAST:event_newJavaMenuActionPerformed

    private void newCMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCMenuActionPerformed
    {//GEN-HEADEREND:event_newCMenuActionPerformed
        createNewFile(".c");
    }//GEN-LAST:event_newCMenuActionPerformed

    private void renameMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_renameMenuActionPerformed
    {//GEN-HEADEREND:event_renameMenuActionPerformed
        renameSelectedFile();
    }//GEN-LAST:event_renameMenuActionPerformed

    private void deleteMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteMenuActionPerformed
    {//GEN-HEADEREND:event_deleteMenuActionPerformed
        deleteSelectedFile();
    }//GEN-LAST:event_deleteMenuActionPerformed

    private void codeEditorKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_codeEditorKeyTyped
    {//GEN-HEADEREND:event_codeEditorKeyTyped
        saveFileFromEditor();
    }//GEN-LAST:event_codeEditorKeyTyped

    private void explorerTreeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_explorerTreeMouseClicked
    {//GEN-HEADEREND:event_explorerTreeMouseClicked
        if (SwingUtilities.isRightMouseButton(evt)) {
            int row = explorerTree.getClosestRowForLocation(evt.getX(), evt.getY());
            explorerTree.setSelectionRow(row);
            explorerPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_explorerTreeMouseClicked

    private void fullscreenButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fullscreenButtonActionPerformed
    {//GEN-HEADEREND:event_fullscreenButtonActionPerformed
        if (fullscreenButton.isSelected()) {            
            mainSplitterPane.setDividerLocation(0); //hide
        }
        else {            
            double dsz = mainSplitterPane.getResizeWeight() * mainSplitterPane.getWidth();
            mainSplitterPane.setDividerLocation((int) dsz); //show
        }
    }//GEN-LAST:event_fullscreenButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.fife.ui.rtextarea.RTextScrollPane PaneForCodeEditor;
    private javax.swing.JSplitPane answerSplitterPane;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea codeEditor;
    private javax.swing.JSplitPane codeSplitPane;
    private javax.swing.JButton compileAndRunButton;
    private javax.swing.JTextArea consolePane;
    private javax.swing.JMenuItem deleteMenu;
    private javax.swing.JToolBar descToolBar;
    private javax.swing.JLabel examTitleLabel;
    private final javax.swing.JPopupMenu explorerPopup = new javax.swing.JPopupMenu();
    private javax.swing.JTree explorerTree;
    private javax.swing.JToggleButton fullscreenButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JMenuItem newCMenu;
    private javax.swing.JMenuItem newCPPMenu;
    private javax.swing.JMenuItem newFileMenu;
    private javax.swing.JMenuItem newFolderMenu;
    private javax.swing.JMenuItem newJavaMenu;
    private javax.swing.JScrollPane pdfPanel;
    private javax.swing.JList questionList;
    private javax.swing.JSplitPane questionSplitterPane;
    private javax.swing.JLabel questionTitleBox;
    private javax.swing.JLabel registrationNoLabel;
    private javax.swing.JLabel remainingTimeLabel;
    private javax.swing.JMenuItem renameMenu;
    private javax.swing.JButton saveCodeButton;
    private javax.swing.JButton submitAnswerButton;
    private javax.swing.JButton submitAnswerButton1;
    private javax.swing.JComboBox themeChooser;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
