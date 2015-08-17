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

import Utilities.AnswerData;
import Utilities.Functions;
import java.awt.Dimension;
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
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import javax.swing.JSplitPane;

/**
 * Main form for the examination.
 */
@SuppressWarnings("serial")
public class MainForm extends JFrame
{

    //update interval for server data in milliseconds
    final int SERVER_UPDATE_INTERVAL = 4200;
    //refresh rate for form data in milliseconds
    final int FORM_DATA_REFRESH_INTERVAL = 600;

    //parent form to this form
    private final JFrame mParentForm;
    //link to server
    private final ServerLink mServerLink;
    //timer for periodic tasks
    private final Timer mTimer;
    //to display pdf files
    private final SwingController mPdfViewer;

    //current server time
    private long mTimeDiff = 0;
    ;
    //stop time of the examination
    private long mStopTime = -1;
    //all question data of the examination
    private ArrayList<Question> mAllQuestion;
    //selected question data of the examination
    private Question mSelectedQues = null;
    //selected folder node on editor
    private DefaultMutableTreeNode mSelectedNode = null;

    /**
     * Creates a new MazinForm
     *
     * @param parent Parent object to this form
     * @param serverLink ServerLink to use for communication.
     */
    public MainForm(JFrame parent, ServerLink serverLink)
    {
        mParentForm = parent;
        mServerLink = serverLink;
        mTimer = new Timer();
        mPdfViewer = new SwingController();

        //init form
        initComponents();
        initPdfControl();
        initFileExplorer();

        Functions.setToFullFocus(this);

        //load default values
        loadValues();
        loadEditorTheme(0);

        //set up timer to begin timer task  
        initTimerTasks();
    }

    /**
     * Ends the exam and closes this form
     */
    public void endExam()
    {
        mStopTime = mServerLink.getStopTime();
        long now = System.currentTimeMillis() + mTimeDiff;
        if (mStopTime < now) {
            mTimer.cancel();
            this.dispose();

            JOptionPane.showMessageDialog(this, "Exam is over.");
            mServerLink.logoutUser();
            mParentForm.setVisible(true);
        }
    }

    /**
     * Initialize periodic tasks
     */
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
        mTimer.scheduleAtFixedRate(updateTask, 0, SERVER_UPDATE_INTERVAL);

        //download data periodically
        TimerTask refreshTask = new TimerTask()
        {
            @Override
            public void run()
            {
                refreshValues();
            }
        };
        mTimer.scheduleAtFixedRate(refreshTask, 0, FORM_DATA_REFRESH_INTERVAL);
    }

    /**
     * Initialize PDF viewer
     */
    private void initPdfControl()
    {
        //factory to build all controls
        SwingViewBuilder factory = new SwingViewBuilder(mPdfViewer);
        mPdfViewer.setPageViewMode(2, true);

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
        JSplitPane jsp = factory.buildUtilityAndDocumentSplitPane(false);
        jsp.setPreferredSize(new Dimension(10, 10));
        pdfPanel.setViewportView(jsp);
    }

    /**
     * Initializes the file explorer to explorer working directory.
     */
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
        explorerPopup.add(deleteMenu);
        explorerPopup.add(new JPopupMenu.Separator());
        explorerPopup.add(refreshMenu);
    }

    /**
     * Load examination data.
     */
    private void loadValues()
    {
        registrationNoLabel.setText("   " + mServerLink.getRegistrationNo() + "   ");
        examTitleLabel.setText(mServerLink.getExamTitle());
        downloadQuestions();

        double dsz = codeSplitPane.getResizeWeight() * codeSplitPane.getHeight();
        codeSplitPane.setDividerLocation((int) dsz);
        dsz = mainSplitterPane.getResizeWeight() * mainSplitterPane.getWidth();
        mainSplitterPane.setDividerLocation((int) dsz);
    }

    /**
     * Download and set all questions of this examination.
     */
    private void downloadQuestions()
    {
        mAllQuestion = mServerLink.getAllQuestions();
        questionList.setListData(mAllQuestion.toArray());
        if (!mAllQuestion.isEmpty()) {
            questionList.setSelectedIndex(0);
        }
    }

    /**
     * Download and update some values from server periodically.
     */
    public void updateValues()
    {
        mStopTime = mServerLink.getStopTime();
        mTimeDiff = mServerLink.getServerTime() - System.currentTimeMillis();
    }

    /**
     * Refresh some displayed value periodically.
     */
    void refreshValues()
    {
        long now = System.currentTimeMillis() + mTimeDiff;
        if (mStopTime < now) {
            endExam();
            return;
        }

        String remain = Utilities.Functions.formatTimeSpan(mStopTime - now);
        remain = "    " + remain + " remaining.    ";
        remainingTimeLabel.setText(remain);
    }

    /**
     * Loads a PDF document into viewer from byte data.
     *
     * @param data Byte data of PDF document to load.
     * @param title Title of the PDF document.
     */
    private void showPdfDescription(byte[] data, String title)
    {
        if (data != null) {
            mPdfViewer.openDocument(data, 0, data.length, title, null);
            mPdfViewer.setZoom(1.50F);
        }
    }

    /**
     * Display selected PDF question.
     *
     * @param selected Selected Question type object.
     */
    void loadQuestion(Object selected)
    {
        try {
            mSelectedQues = (Question) selected;

            //clear values
            questionTitleBox.setText("No Question");
            markValueBox.setText("0");
            codeEditor.setText("");
            codeEditor.setEditable(false);
            if (mSelectedQues == null) {
                return;
            }

            //set new values        
            questionTitleBox.setText(mSelectedQues.getTitle());
            markValueBox.setText(Integer.toString(mSelectedQues.getMark()));
            loadFileExplorer();

            //show pdf description of question
            showPdfDescription(mSelectedQues.getBody(),
                    mSelectedQues.getTitle());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads file explorer folders and files.
     */
    void loadFileExplorer()
    {
        if (mSelectedQues == null) {
            return;
        }

        try {
            File qpath = AppSettings.getDefaultPath().resolve(
                    "Question_" + mSelectedQues.getId()).toFile();
            qpath.mkdirs();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                    new TreeNodeData(qpath, mSelectedQues.getTitle()));
            loadFileList(root, qpath);
            explorerTree.setModel(new DefaultTreeModel(root));
            explorerTree.setSelectionRow(0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads file list of file explorer.
     *
     * @param node Parent node to add file nodes.
     * @param dir Parent directory to look into.
     */
    void loadFileList(DefaultMutableTreeNode node, File dir)
    {
        if (!dir.isDirectory()) return;

        node.setAllowsChildren(true);
        for (File f : dir.listFiles()) {
            //if file then check if valid
            if (f.isFile()) {
                String nam = f.getName().toLowerCase();
                if (nam.endsWith(".exe") || nam.endsWith(".class")) {
                    f.delete();
                    continue;
                }
            }
            //add child
            DefaultMutableTreeNode child
                    = new DefaultMutableTreeNode(new TreeNodeData(f));
            if (f.isDirectory()) {
                loadFileList(child, f);
            }
            node.add(child);
        }
    }

    /**
     * Opens the selected file from file explorer into editor to edit.
     *
     * @param file File path that has been selected.
     */
    void openFileInEditor(File file)
    {
        try {
            //set highlighting style
            String nam = file.getName().toLowerCase();
            if (nam.endsWith(".exe") || nam.endsWith(".class")) {
                return;
            }
            else if (nam.endsWith(".java")) {
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
            paneForCodeEditor.setLineNumbersEnabled(true);
            questionTitleBox.setText(
                    mSelectedQues.getTitle() + " >> " + file.getName());
        }
        catch (Exception ex) {
            codeEditor.setText("Create a new file and select it to edit.");
            codeEditor.setEditable(false);
            questionTitleBox.setText(mSelectedQues.getTitle());
            ex.printStackTrace();
        }
    }

    /**
     * Saves the edited data to the file.
     */
    void saveFileFromEditor()
    {
        try {
            if (mSelectedNode == null) return;
            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
            if (!file.isFile()) return;

            String source = codeEditor.getText();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(source.getBytes());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes a opened file from editing.
     */
    void closeOpenedFile()
    {
        codeEditor.setText("");
        codeEditor.setEditable(false);
        questionTitleBox.setText(mSelectedQues.getTitle());
    }

    /**
     * Compile and run code
     */
    void compileAndRun()
    {
        if (mSelectedNode == null) return;
        File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
        if (!file.isFile()) return;

        saveFileFromEditor();
        consolePane.setText("");
        answerSplitterPane.getRightComponent().setVisible(true);

        boolean result;
        try (
                //initialize a writer to show output
                Writer writer = new Writer()
                {
                    @Override
                    public void close() throws IOException
                    {
                    }

                    @Override
                    public void flush() throws IOException
                    {
                    }

                    @Override
                    public void write(char[] value, int offset, int count) throws IOException
                    {
                        consolePane.append(new String(value, offset, count));
                    }
                }) {

            result = CompileAndRun.CompileCode(file, writer);
            if (!result) {
                JOptionPane.showMessageDialog(this, "Compilation Failed.");
                return;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        result = CompileAndRun.RunProgram(file);
        String status = (result ? "[OK]" : "[Failed]");
        consolePane.append("\nRun Report : " + status + "\n");
    }

    /**
     * Try to submit answer to a question. Notify on failure.
     */
    private void attemptSubmitAnswer()
    {
        if (mSelectedQues == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure to submit answers to \"" + mSelectedQues.getTitle() + "\"?\n"
                + "Your previous submission(if any) will be overwritten.",
                "Submit Answer", JOptionPane.YES_NO_OPTION);
        try {
            saveFileFromEditor();

            File qpath = AppSettings.getDefaultPath().resolve(
                    "Question_" + mSelectedQues.getId()).toFile();
            ArrayList<AnswerData> answers = new ArrayList<>();
            listAllFiles(qpath, answers);

            if (result == JOptionPane.YES_OPTION) {
                boolean res = mServerLink.submitAnswer(
                        mSelectedQues.getId(), answers.toArray());
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

    /**
     * Gets a list of all files inside a folder using a heuristic search.
     *
     * @param f Path to folder.
     * @param answers Answer data list to store data.
     * @throws IOException
     */
    private void listAllFiles(File f, ArrayList<AnswerData> answers)
            throws IOException
    {
        if (!f.exists()) return;

        //add data if file
        if (f.isFile()) {
            //delete if not valid file
            String nam = f.getName().toLowerCase();
            if (nam.endsWith(".exe") || nam.endsWith(".class")) {
                f.delete();
                return;
            }
            //add if valud
            int deflen = AppSettings.getDefaultPath().toFile().getAbsolutePath().length();
            answers.add(new AnswerData(
                    f.getAbsolutePath().substring(deflen + 1), //+1 is for separator
                    Functions.readFully(new FileInputStream(f))));
        }

        //if directory search inside of it
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                listAllFiles(c, answers);
            }
        }
    }

    /**
     * Creates new folder in selected path.
     */
    private void createNewFolder()
    {
        try {
            if (mSelectedNode == null) {
                JOptionPane.showMessageDialog(this, "Select a folder first.");
                return;
            }

            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
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
            mSelectedNode.add(node);
            ((DefaultTreeModel) explorerTree.getModel()).reload(mSelectedNode);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            //JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!"); 
        }
    }

    /**
     * Create new file in selected file.
     *
     * @param ext Extension of the file.
     */
    private void createNewFile(String ext)
    {
        try {
            if (mSelectedNode == null) {
                JOptionPane.showMessageDialog(this, "Select a folder first.");
                return;
            }

            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
            if (!file.isDirectory()) return;

            //get name of the file
            if (ext == null || ext.isEmpty()) ext = ".txt";
            String name = JOptionPane.showInputDialog(this, "Name of the file to create", "New File" + ext);
            name = name.replaceAll("[\\\\/:*?\\\"<>|]", "").trim();
            if (!name.endsWith(ext)) name += ext;

            //create new file
            File f = (file.toPath().resolve(name)).toFile();
            if (name.isEmpty() || f.exists()) {
                JOptionPane.showMessageDialog(this, "Another file with name \"" + name + "\" already exists!");
                return;
            }
            if (!f.createNewFile()) throw new FileNotFoundException();

            //add node to display
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeNodeData(f), true);
            mSelectedNode.add(node);
            ((DefaultTreeModel) explorerTree.getModel()).reload(mSelectedNode);
        }
        catch (HeadlessException | IOException ex) {
            ex.printStackTrace();
            //JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!"); 
        }
    }

    /**
     * Rename selected file into new one.
     */
    private void renameSelectedFile()
    {
        try {
            if (mSelectedNode == null) {
                JOptionPane.showMessageDialog(this, "Select a folder first.");
                return;
            }
            if (mSelectedNode.getLevel() == 0) {
                JOptionPane.showMessageDialog(this, "You can not rename this folder.");
                return;
            }

            //get new name            
            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
            String name = JOptionPane.showInputDialog(this, "Rename a file into another", file.getName());
            name = name.replaceAll("[\\\\:*?\\\"<>|/]", "").trim();

            //rename file
            File newFile = file.toPath().resolveSibling(name).toFile();
            closeOpenedFile();
            if (!file.renameTo(newFile)) {
                JOptionPane.showMessageDialog(this, "Couldn't rename from \"" + file.getName() + "\" to \"" + newFile.getName() + "\"!");
                return;
            }

            //reload the view
            mSelectedNode.setUserObject(new TreeNodeData(newFile));
            if (newFile.isDirectory()) {
                mSelectedNode.removeAllChildren();
                loadFileList(mSelectedNode, newFile);
            }
            ((DefaultTreeModel) explorerTree.getModel()).reload(mSelectedNode);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
        }
    }

    /**
     * Delete a selected file.
     */
    private void deleteSelectedFile()
    {
        try {
            if (mSelectedNode == null) {
                JOptionPane.showMessageDialog(this, "Select a folder first.");
                return;
            }
            if (mSelectedNode.getLevel() == 0) {
                JOptionPane.showMessageDialog(this, "You can not rename this folder.");
                return;
            }

            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete \"" + file.getName() + "\"?") == JOptionPane.NO_OPTION) {
                return;
            }

            //delete all files in directories
            closeOpenedFile();
            Functions.deleteDirectory(file);

            //remove node from view
            DefaultTreeModel model = (DefaultTreeModel) explorerTree.getModel();
            model.removeNodeFromParent(mSelectedNode);
            mSelectedNode = null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "SORRY!! Something went wrong. Please try again!");
        }
    }

    /**
     * Load theme for code editor.
     *
     * @param index Index of the theme (0 = LIGHT, 1 = BLACK).
     */
    private void loadEditorTheme(int index)
    {
        //load theme for editor from file        
        try {
            switch (index) {
                case 0: //Dark
                    Theme.load(getClass().getResourceAsStream("/Resources/dark.xml")).apply(codeEditor);
                    codeEditor.setFont(new Font("Consolas", Font.PLAIN, 14));
                    break;
                case 1: //Light
                    Theme.load(getClass().getResourceAsStream("/Resources/light.xml")).apply(codeEditor);
                    codeEditor.setFont(new Font("Consolas", Font.PLAIN, 14));
                    break;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
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
        refreshMenu = new javax.swing.JMenuItem();
        topPanel = new javax.swing.JPanel();
        registrationNoLabel = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();
        examTitleLabel = new javax.swing.JLabel();
        remainingTimeLabel = new javax.swing.JLabel();
        mainSplitterPane = new javax.swing.JSplitPane();
        mainLeftPanel = new javax.swing.JPanel();
        questionHeader = new javax.swing.JPanel();
        listOfQuesLabel = new javax.swing.JLabel();
        submitAnswerButton1 = new javax.swing.JButton();
        questionSplitterPane = new javax.swing.JSplitPane();
        quesListPanel = new javax.swing.JPanel();
        queslistPane = new javax.swing.JScrollPane();
        questionList = new javax.swing.JList();
        descPanel = new javax.swing.JPanel();
        descToolBar = new javax.swing.JToolBar();
        pdfPanel = new javax.swing.JScrollPane();
        mainRightPanel = new javax.swing.JPanel();
        answerHeaderPanel = new javax.swing.JPanel();
        questionTitleBox = new javax.swing.JLabel();
        markLabel = new javax.swing.JLabel();
        markValueBox = new javax.swing.JLabel();
        fullscreenButton = new javax.swing.JToggleButton();
        submitToolBar = new javax.swing.JPanel();
        submitAnswerButton = new javax.swing.JButton();
        compileAndRunButton = new javax.swing.JButton();
        saveCodeButton = new javax.swing.JButton();
        themeLabel = new javax.swing.JLabel();
        themeChooser = new javax.swing.JComboBox();
        answerSplitterPane = new javax.swing.JSplitPane();
        explorerContainer = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        explorerTree = new javax.swing.JTree();
        jPanel10 = new javax.swing.JPanel();
        deleteToolButton = new javax.swing.JButton();
        refreshViewButton = new javax.swing.JButton();
        newFolderToolButton = new javax.swing.JButton();
        newJavaToolButton = new javax.swing.JButton();
        newCPPToolButton = new javax.swing.JButton();
        newCToolButton = new javax.swing.JButton();
        codeSplitPane = new javax.swing.JSplitPane();
        consoleContainer = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        consolePane = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        paneForCodeEditor = new org.fife.ui.rtextarea.RTextScrollPane();
        codeEditor = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        newFolderMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/folder.png"))); // NOI18N
        newFolderMenu.setMnemonic('N');
        newFolderMenu.setText("New Folder");
        newFolderMenu.setToolTipText("Create a new folder inside selected folder");
        newFolderMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newFolderMenuActionPerformed(evt);
            }
        });

        newFileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/file.png"))); // NOI18N
        newFileMenu.setMnemonic('F');
        newFileMenu.setText("New File");
        newFileMenu.setToolTipText("Create a new text file inside selected folder");
        newFileMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newFileMenuActionPerformed(evt);
            }
        });

        newCPPMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/cpp.png"))); // NOI18N
        newCPPMenu.setMnemonic('C');
        newCPPMenu.setText("New C++ File");
        newCPPMenu.setToolTipText("Create a new C++ file inside selected folder");
        newCPPMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCPPMenuActionPerformed(evt);
            }
        });

        newJavaMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/java.png"))); // NOI18N
        newJavaMenu.setMnemonic('J');
        newJavaMenu.setText("New Java File");
        newJavaMenu.setToolTipText("Create a new Java file inside selected folder");
        newJavaMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newJavaMenuActionPerformed(evt);
            }
        });

        newCMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/ansi_c.png"))); // NOI18N
        newCMenu.setMnemonic('A');
        newCMenu.setText("New Ansi C File");
        newCMenu.setToolTipText("Create a new C file inside selected folder");
        newCMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCMenuActionPerformed(evt);
            }
        });

        renameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rename.png"))); // NOI18N
        renameMenu.setMnemonic('E');
        renameMenu.setText("Rename");
        renameMenu.setToolTipText("Rename selected node");
        renameMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                renameMenuActionPerformed(evt);
            }
        });

        deleteMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/delete.png"))); // NOI18N
        deleteMenu.setMnemonic('D');
        deleteMenu.setText("Delete");
        deleteMenu.setToolTipText("Delete selected node");
        deleteMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteMenuActionPerformed(evt);
            }
        });

        refreshMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/reload.png"))); // NOI18N
        refreshMenu.setMnemonic('R');
        refreshMenu.setText("Refresh");
        refreshMenu.setToolTipText("Reload the folder explorer");
        refreshMenu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refreshMenuActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Lab Exam");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(0, 204, 204));
        setUndecorated(true);
        setResizable(false);
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

        examTitleLabel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 22)); // NOI18N
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
                .addComponent(examTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
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
        mainSplitterPane.setDividerLocation(240);
        mainSplitterPane.setDividerSize(10);
        mainSplitterPane.setResizeWeight(0.38);
        mainSplitterPane.setContinuousLayout(true);
        mainSplitterPane.setDoubleBuffered(true);
        mainSplitterPane.setOneTouchExpandable(true);
        mainSplitterPane.setOpaque(false);
        mainSplitterPane.setPreferredSize(new java.awt.Dimension(0, 0));

        questionHeader.setBackground(java.awt.Color.cyan);
        questionHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 204, 255)));
        questionHeader.setAutoscrolls(true);

        listOfQuesLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        listOfQuesLabel.setText("List of Questions");

        submitAnswerButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/reload.png"))); // NOI18N
        submitAnswerButton1.setText("Redownload");
        submitAnswerButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                submitAnswerButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout questionHeaderLayout = new javax.swing.GroupLayout(questionHeader);
        questionHeader.setLayout(questionHeaderLayout);
        questionHeaderLayout.setHorizontalGroup(
            questionHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(questionHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(listOfQuesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(submitAnswerButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        questionHeaderLayout.setVerticalGroup(
            questionHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(questionHeaderLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(questionHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listOfQuesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        queslistPane.setViewportView(questionList);

        javax.swing.GroupLayout quesListPanelLayout = new javax.swing.GroupLayout(quesListPanel);
        quesListPanel.setLayout(quesListPanelLayout);
        quesListPanelLayout.setHorizontalGroup(
            quesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quesListPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(queslistPane, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        quesListPanelLayout.setVerticalGroup(
            quesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quesListPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(queslistPane, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        questionSplitterPane.setTopComponent(quesListPanel);

        descToolBar.setFloatable(false);
        descToolBar.setRollover(true);

        javax.swing.GroupLayout descPanelLayout = new javax.swing.GroupLayout(descPanel);
        descPanel.setLayout(descPanelLayout);
        descPanelLayout.setHorizontalGroup(
            descPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(descToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
            .addComponent(pdfPanel, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        descPanelLayout.setVerticalGroup(
            descPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(descPanelLayout.createSequentialGroup()
                .addComponent(descToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pdfPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
        );

        questionSplitterPane.setRightComponent(descPanel);

        javax.swing.GroupLayout mainLeftPanelLayout = new javax.swing.GroupLayout(mainLeftPanel);
        mainLeftPanel.setLayout(mainLeftPanelLayout);
        mainLeftPanelLayout.setHorizontalGroup(
            mainLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(questionHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(questionSplitterPane)
        );
        mainLeftPanelLayout.setVerticalGroup(
            mainLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainLeftPanelLayout.createSequentialGroup()
                .addComponent(questionHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(questionSplitterPane)
                .addGap(0, 0, 0))
        );

        mainSplitterPane.setLeftComponent(mainLeftPanel);

        answerHeaderPanel.setBackground(java.awt.Color.cyan);
        answerHeaderPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 204, 255)));
        answerHeaderPanel.setAutoscrolls(true);

        questionTitleBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        questionTitleBox.setText("No Question");
        questionTitleBox.setPreferredSize(new java.awt.Dimension(0, 0));

        markLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        markLabel.setText("Mark :");

        markValueBox.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        markValueBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        markValueBox.setText("0");
        markValueBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        fullscreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/fullscreen.png"))); // NOI18N
        fullscreenButton.setText("Full Screen");
        fullscreenButton.setToolTipText("Hides the problem descriptions.");
        fullscreenButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fullscreenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout answerHeaderPanelLayout = new javax.swing.GroupLayout(answerHeaderPanel);
        answerHeaderPanel.setLayout(answerHeaderPanelLayout);
        answerHeaderPanelLayout.setHorizontalGroup(
            answerHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(answerHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(questionTitleBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(markLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fullscreenButton)
                .addContainerGap())
        );
        answerHeaderPanelLayout.setVerticalGroup(
            answerHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(answerHeaderPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(answerHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(markLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(markValueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fullscreenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(questionTitleBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        submitToolBar.setBackground(new java.awt.Color(0, 233, 242));
        submitToolBar.setAutoscrolls(true);

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

        themeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        themeLabel.setText("Theme :");

        themeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dark", "Light" }));
        themeChooser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                themeChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout submitToolBarLayout = new javax.swing.GroupLayout(submitToolBar);
        submitToolBar.setLayout(submitToolBarLayout);
        submitToolBarLayout.setHorizontalGroup(
            submitToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(submitToolBarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(themeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        submitToolBarLayout.setVerticalGroup(
            submitToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, submitToolBarLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(submitToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitAnswerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compileAndRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveCodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(themeLabel)
                    .addComponent(themeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        answerSplitterPane.setBorder(null);
        answerSplitterPane.setDividerLocation(220);
        answerSplitterPane.setDividerSize(6);
        answerSplitterPane.setContinuousLayout(true);
        answerSplitterPane.setOneTouchExpandable(true);

        explorerContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("File Explorer"));
        explorerContainer.setPreferredSize(new java.awt.Dimension(0, 0));

        explorerTree.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        explorerTree.setToolTipText("");
        explorerTree.setAutoscrolls(true);
        explorerTree.setDoubleBuffered(true);
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
        explorerTree.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                explorerTreeKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(explorerTree);

        jPanel10.setLayout(new java.awt.GridLayout(3, 0, 1, 1));

        deleteToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/delete.png"))); // NOI18N
        deleteToolButton.setText("Delete");
        deleteToolButton.setToolTipText("Delete the selected file or folder");
        deleteToolButton.setPreferredSize(new java.awt.Dimension(110, 25));
        deleteToolButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteToolButtonActionPerformed(evt);
            }
        });
        jPanel10.add(deleteToolButton);

        refreshViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/reload.png"))); // NOI18N
        refreshViewButton.setText("Refresh");
        refreshViewButton.setToolTipText("Reload the folder explorer");
        refreshViewButton.setPreferredSize(new java.awt.Dimension(110, 25));
        refreshViewButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refreshViewButtonActionPerformed(evt);
            }
        });
        jPanel10.add(refreshViewButton);

        newFolderToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/folder.png"))); // NOI18N
        newFolderToolButton.setText("Folder");
        newFolderToolButton.setToolTipText("Create a new folder inside selected folder");
        newFolderToolButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newFolderToolButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newFolderToolButtonActionPerformed(evt);
            }
        });
        jPanel10.add(newFolderToolButton);

        newJavaToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/java.png"))); // NOI18N
        newJavaToolButton.setText("Java");
        newJavaToolButton.setToolTipText("Create a new Java file inside selected folder");
        newJavaToolButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newJavaToolButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newJavaToolButtonActionPerformed(evt);
            }
        });
        jPanel10.add(newJavaToolButton);

        newCPPToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/cpp.png"))); // NOI18N
        newCPPToolButton.setText("C++");
        newCPPToolButton.setToolTipText("Create a new C++ file inside selected folder");
        newCPPToolButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newCPPToolButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCPPToolButtonActionPerformed(evt);
            }
        });
        jPanel10.add(newCPPToolButton);

        newCToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/ansi_c.png"))); // NOI18N
        newCToolButton.setText("Ansi C");
        newCToolButton.setToolTipText("Create a new C file inside selected folder");
        newCToolButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newCToolButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newCToolButtonActionPerformed(evt);
            }
        });
        jPanel10.add(newCToolButton);

        javax.swing.GroupLayout explorerContainerLayout = new javax.swing.GroupLayout(explorerContainer);
        explorerContainer.setLayout(explorerContainerLayout);
        explorerContainerLayout.setHorizontalGroup(
            explorerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorerContainerLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(explorerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        explorerContainerLayout.setVerticalGroup(
            explorerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorerContainerLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        answerSplitterPane.setLeftComponent(explorerContainer);

        codeSplitPane.setDividerLocation(200);
        codeSplitPane.setDividerSize(6);
        codeSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        codeSplitPane.setResizeWeight(0.7);
        codeSplitPane.setOneTouchExpandable(true);

        consoleContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Compile and Run Report"));

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

        javax.swing.GroupLayout consoleContainerLayout = new javax.swing.GroupLayout(consoleContainer);
        consoleContainer.setLayout(consoleContainerLayout);
        consoleContainerLayout.setHorizontalGroup(
            consoleContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consoleContainerLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        consoleContainerLayout.setVerticalGroup(
            consoleContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consoleContainerLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
        );

        codeSplitPane.setRightComponent(consoleContainer);

        paneForCodeEditor.setLineNumbersEnabled(true);

        codeEditor.setEditable(false);
        codeEditor.setColumns(20);
        codeEditor.setRows(5);
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        codeEditor.setPaintMarkOccurrencesBorder(true);
        codeEditor.setPaintMatchedBracketPair(true);
        codeEditor.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                codeEditorKeyTyped(evt);
            }
        });
        paneForCodeEditor.setViewportView(codeEditor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(paneForCodeEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(paneForCodeEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        codeSplitPane.setLeftComponent(jPanel1);

        answerSplitterPane.setRightComponent(codeSplitPane);

        javax.swing.GroupLayout mainRightPanelLayout = new javax.swing.GroupLayout(mainRightPanel);
        mainRightPanel.setLayout(mainRightPanelLayout);
        mainRightPanelLayout.setHorizontalGroup(
            mainRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(answerHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(answerSplitterPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(submitToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainRightPanelLayout.setVerticalGroup(
            mainRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainRightPanelLayout.createSequentialGroup()
                .addComponent(answerHeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(submitToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(answerSplitterPane)
                .addGap(0, 0, 0))
        );

        mainSplitterPane.setRightComponent(mainRightPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainSplitterPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainSplitterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
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
            mServerLink.logoutUser();
            mParentForm.setLocationRelativeTo(null);
            mParentForm.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        mStopTime = mServerLink.getStopTime();
        long now = System.currentTimeMillis() + mTimeDiff;
        if (now < mStopTime) {
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
        loadEditorTheme(themeChooser.getSelectedIndex());
    }//GEN-LAST:event_themeChooserActionPerformed

    private void explorerTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_explorerTreeValueChanged
    {//GEN-HEADEREND:event_explorerTreeValueChanged
        //ckise the opened file
        closeOpenedFile();
        //open selected file if valid
        if (mSelectedQues != null && explorerTree.getSelectionPath() != null) {
            mSelectedNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();
            File file = ((TreeNodeData) mSelectedNode.getUserObject()).getFile();
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

    private void explorerTreeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_explorerTreeMouseClicked
    {//GEN-HEADEREND:event_explorerTreeMouseClicked
        //select node on right mouse button click too
        if (SwingUtilities.isRightMouseButton(evt)) {
            int row = explorerTree.getClosestRowForLocation(evt.getX(), evt.getY());
            explorerTree.setSelectionRow(row);
            explorerPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_explorerTreeMouseClicked

    private void fullscreenButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fullscreenButtonActionPerformed
    {//GEN-HEADEREND:event_fullscreenButtonActionPerformed
        if (fullscreenButton.isSelected()) {
            mainSplitterPane.setDividerLocation(0);
            mainSplitterPane.setDividerSize(0);
            fullscreenButton.setText("Reset View");
        }
        else {
            double dsz = mainSplitterPane.getResizeWeight() * mainSplitterPane.getWidth();
            mainSplitterPane.setDividerLocation((int) dsz); //show
            mainSplitterPane.setDividerSize(10);
            fullscreenButton.setText("Full Screen");
        }
    }//GEN-LAST:event_fullscreenButtonActionPerformed

    private void newFolderToolButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newFolderToolButtonActionPerformed
    {//GEN-HEADEREND:event_newFolderToolButtonActionPerformed
        createNewFolder();
    }//GEN-LAST:event_newFolderToolButtonActionPerformed

    private void refreshViewButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refreshViewButtonActionPerformed
    {//GEN-HEADEREND:event_refreshViewButtonActionPerformed
        loadFileExplorer();
    }//GEN-LAST:event_refreshViewButtonActionPerformed

    private void newJavaToolButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newJavaToolButtonActionPerformed
    {//GEN-HEADEREND:event_newJavaToolButtonActionPerformed
        createNewFile(".java");
    }//GEN-LAST:event_newJavaToolButtonActionPerformed

    private void newCPPToolButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCPPToolButtonActionPerformed
    {//GEN-HEADEREND:event_newCPPToolButtonActionPerformed
        createNewFile(".cpp");
    }//GEN-LAST:event_newCPPToolButtonActionPerformed

    private void newCToolButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCToolButtonActionPerformed
    {//GEN-HEADEREND:event_newCToolButtonActionPerformed
        createNewFile(".c");
    }//GEN-LAST:event_newCToolButtonActionPerformed

    private void deleteToolButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteToolButtonActionPerformed
    {//GEN-HEADEREND:event_deleteToolButtonActionPerformed
        deleteSelectedFile();
    }//GEN-LAST:event_deleteToolButtonActionPerformed

    private void codeEditorKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_codeEditorKeyTyped
    {//GEN-HEADEREND:event_codeEditorKeyTyped
        saveFileFromEditor();
    }//GEN-LAST:event_codeEditorKeyTyped

    private void refreshMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refreshMenuActionPerformed
    {//GEN-HEADEREND:event_refreshMenuActionPerformed
        loadFileExplorer();
    }//GEN-LAST:event_refreshMenuActionPerformed

    private void explorerTreeKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_explorerTreeKeyReleased
    {//GEN-HEADEREND:event_explorerTreeKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_F2:
                if (!explorerTree.isSelectionEmpty())
                    renameSelectedFile();
            case KeyEvent.VK_F5:
                loadFileExplorer();
            case KeyEvent.VK_N:
                if (evt.isControlDown() && !evt.isShiftDown() && !evt.isAltDown()
                        && !explorerTree.isSelectionEmpty())
                    createNewFolder();
                break;
        }
    }//GEN-LAST:event_explorerTreeKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel answerHeaderPanel;
    private javax.swing.JSplitPane answerSplitterPane;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea codeEditor;
    private javax.swing.JSplitPane codeSplitPane;
    private javax.swing.JButton compileAndRunButton;
    private javax.swing.JPanel consoleContainer;
    private javax.swing.JTextArea consolePane;
    private javax.swing.JMenuItem deleteMenu;
    private javax.swing.JButton deleteToolButton;
    private javax.swing.JPanel descPanel;
    private javax.swing.JToolBar descToolBar;
    private javax.swing.JLabel examTitleLabel;
    private javax.swing.JPanel explorerContainer;
    private final javax.swing.JPopupMenu explorerPopup = new javax.swing.JPopupMenu();
    private javax.swing.JTree explorerTree;
    private javax.swing.JToggleButton fullscreenButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel listOfQuesLabel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JPanel mainLeftPanel;
    private javax.swing.JPanel mainRightPanel;
    private javax.swing.JSplitPane mainSplitterPane;
    private javax.swing.JLabel markLabel;
    private javax.swing.JLabel markValueBox;
    private javax.swing.JMenuItem newCMenu;
    private javax.swing.JMenuItem newCPPMenu;
    private javax.swing.JButton newCPPToolButton;
    private javax.swing.JButton newCToolButton;
    private javax.swing.JMenuItem newFileMenu;
    private javax.swing.JMenuItem newFolderMenu;
    private javax.swing.JButton newFolderToolButton;
    private javax.swing.JMenuItem newJavaMenu;
    private javax.swing.JButton newJavaToolButton;
    private org.fife.ui.rtextarea.RTextScrollPane paneForCodeEditor;
    private javax.swing.JScrollPane pdfPanel;
    private javax.swing.JPanel quesListPanel;
    private javax.swing.JScrollPane queslistPane;
    private javax.swing.JPanel questionHeader;
    private javax.swing.JList questionList;
    private javax.swing.JSplitPane questionSplitterPane;
    private javax.swing.JLabel questionTitleBox;
    private javax.swing.JMenuItem refreshMenu;
    private javax.swing.JButton refreshViewButton;
    private javax.swing.JLabel registrationNoLabel;
    private javax.swing.JLabel remainingTimeLabel;
    private javax.swing.JMenuItem renameMenu;
    private javax.swing.JButton saveCodeButton;
    private javax.swing.JButton submitAnswerButton;
    private javax.swing.JButton submitAnswerButton1;
    private javax.swing.JPanel submitToolBar;
    private javax.swing.JComboBox themeChooser;
    private javax.swing.JLabel themeLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
