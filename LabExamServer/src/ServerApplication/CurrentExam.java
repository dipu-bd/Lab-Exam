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

import Utilities.Candidate;
import Utilities.Examination;
import Utilities.UserChangeEvent;
import Utilities.UserChangedHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Currently opened exam session
 *
 * @author Dipu
 */
public final class CurrentExam
{

    /**
     * File where the curExam data is saved
     */
    public static File examFile = null;
    /**
     * Current exam object that is been working with
     */
    public static Examination curExam = new Examination();
    /**
     * List of logged in clients
     */
    public final static HashSet<Integer> logins = new HashSet<>();
    /**
     * Announcement messages by id
     */
    public final static ArrayList<String> announcements = new ArrayList<>();
    /**
     * List of Blacklisted users
     */
    public final static ArrayList<Integer> blacklist = new ArrayList<>();

    // <editor-fold defaultstate="collapsed" desc="Open Save and Print password list">     
    /**
     * Read a file and extract Examination class information stored in it
     *
     * @param file File object to open
     * @throws FileNotFoundException When specific file is not found
     * @throws IOException If any error occur while reading the file
     * @throws ClassNotFoundException If specific file does not have desired
     * format
     */
    public static void Open(File file) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        //clear some values
        logins.clear();
        examFile = file;
        announcements.clear();
        blacklist.clear();

        //open file
        try (FileInputStream fin = new FileInputStream(examFile);
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            curExam = (Examination) ois.readObject();
            curExam.recycleLastID();
        }
    }

    /**
     * Write curExam object to a specific file
     *
     * @throws FileNotFoundException When file/directory is not found
     * @throws IOException When writing to file is failed due to some reason
     */
    public static void Save() throws FileNotFoundException, IOException
    {
        try (FileOutputStream fos = new FileOutputStream(examFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(curExam);
            oos.flush();
        }
    }

    /**
     * Get a list of all users | passwords | IP-address | port
     *
     * @return list of users and passwords
     */
    public static String getUsers()
    {
        String output = "Name,\tRegistration No,\tPasswords\n";
        for (Candidate c : curExam.allCandidate) {
            output += c.name + ",\t" + c.regno + ",\t" + c.password + "\n";
        }
        return output;
    }

    public static void loadUsers(String input)
    {
        curExam.allCandidate.clear();
        for (String line : input.split("\n")) {
            String[] can = line.split(",");
            curExam.addCandidate(can[0].trim(), can[1].trim());
        }
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add to Remove Users">     
    /**
     * Assign a Socket to specific user.
     *
     * @param user Registration number of the candidate.
     * @param pass Password of the user
     * @param ip IP Address of client.
     * @return True on success.
     */
    public static boolean assignUser(String user, String pass, String ip)
    {
        int uid = curExam.getCandidateID(user);
        if (uid == -1) {
            return false;
        }
        Candidate cand = curExam.getCandidate(uid);
        if (cand == null) {
            return false;
        }

        if (!cand.password.equals(pass)) {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("%s(%s) tried to login with wrong password."
                            + " (attempted password: %s)", cand.name, cand.regno, pass));
            return false;
        }

        logins.add(uid);
        invokeUserChanged(new UserChangeEvent(uid, true));

        Logger.getLogger("LabExam").log(Level.INFO,
                String.format("%s(%s) connected via %s", cand.name, user, ip));

        return true;
    }

    /**
     * Delete an user from the connected list.
     *
     * @param user Registration number of the candidate.
     * @param ip IP Address of the client.
     * @return True if success, False otherwise.
     */
    public static boolean removeUser(String user, String ip)
    {
        int uid = curExam.getCandidateID(user);
        if (uid == -1) {
            return false;
        }

        String name = curExam.getCandidate(uid).name;
        if (!logins.contains(uid)) {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("%s(%s) tried to log out but failed.", name, user));
            return false;
        }

        addToBlacklist(uid);

        logins.remove(uid);
        invokeUserChanged(new UserChangeEvent(uid, true));

        Logger.getLogger("LabExam").log(Level.WARNING,
                String.format("%s(%s) logged out from %s", name, user, ip));

        return true;
    }
    //</editor-fold>

    public static File getSubmissionPath(String regno)
    {
        Path par = CurrentExam.curExam.ExamPath.toPath();
        par = par.resolve(regno);
        return par.toFile();
    }

    public static File getSubmissionPath(String regno, int qid)
    {
        Path par = CurrentExam.curExam.ExamPath.toPath();
        par = par.resolve(regno);
        par = par.resolve("Question_" + qid);
        return par.toFile();
    }

    /**
     * Submit an answer of a question.
     *
     * @param regno Registration no of the student who submitted the answer.
     * @param qid Question id of the answer.
     * @param files List of files for the answer.
     * @param data Data of the answer files.
     * @return False if answer saving failed, True otherwise.
     */
    public static boolean submitAnswer(String regno, int qid, Object[] files, Object[] data)
    {
        if (!CurrentExam.curExam.isRunning()) {
            return false;
        }

        //get userid and name
        int uid = curExam.getCandidateID(regno);
        if (!curExam.candidateExist(uid)) {
            return false;
        }
        String name = curExam.getCandidate(uid).name;

        try {
            if (!CurrentExam.examFile.exists()) {
                CurrentExam.examFile.mkdir();
            }

            //write all data in files
            Path par = getSubmissionPath(regno).toPath();
            for (int i = 0; i < files.length; ++i) {
                File f = par.resolve((String) files[i]).toFile();
                f.getParentFile().mkdirs(); 
                FileOutputStream fos = new FileOutputStream(f);
                fos.write((byte[]) data[i]);
                fos.flush();
                fos.close();
            }

            invokeUserSubmitted(new UserChangeEvent(uid, qid));

            Logger.getLogger("LabExam").log(Level.INFO,
                    String.format("%s(%s) submitted answer for Question %02d", name, regno, qid));
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger("LabExam").log(Level.SEVERE,
                    String.format("Failed to receive %s(%s)'s answer for Question.", name, regno));
            return false;
        }
    }

    public static ArrayList<String> getAnnoucements(int curSiz)
    {
        ArrayList<String> nlist = new ArrayList<>();
        for (int x = curSiz; x < CurrentExam.announcements.size(); ++x) {
            nlist.add(CurrentExam.announcements.get(x));
        }
        return nlist;
    }

    public static void addToBlacklist(int uid)
    {
        if (CurrentExam.curExam.isRunning()) {
            blacklist.add(uid);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="User Changed Event Trigger">     
    private static final ArrayList<UserChangedHandler> userChangeListener = new ArrayList<>();

    public static boolean addUserChangedHandler(UserChangedHandler handler)
    {
        return userChangeListener.add(handler);
    }

    public static boolean removeUserChangedHandler(UserChangedHandler handler)
    {
        return userChangeListener.remove(handler);
    }

    public static void invokeUserChanged(UserChangeEvent uce)
    {
        // Notify everybody who are connected
        for (UserChangedHandler handler : userChangeListener) {
            handler.userChanged(uce);
        }
    }

    public static void invokeUserSubmitted(UserChangeEvent uce)
    {
        // Notify everybody who are connected
        for (UserChangedHandler handler : userChangeListener) {
            handler.userSubmitted(uce);
        }
    }
    //</editor-fold>

}
