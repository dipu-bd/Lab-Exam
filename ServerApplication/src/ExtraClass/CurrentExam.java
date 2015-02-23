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
package ExtraClass;

import Server_Application.LabExamServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Currently opened exam session
 *
 * @author Dipu
 */
public final class CurrentExam {

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
    public final static HashMap<Integer, String> announcement = new HashMap<>();
    public static int curAnnounceID = 0;

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
        logins.clear();
        examFile = file;
        announcement.clear();
        curAnnounceID = 0;
        try (FileInputStream fin = new FileInputStream(examFile);
                ObjectInputStream ois = new ObjectInputStream(fin))
        {
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
                ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(curExam);
            oos.flush();
        }
    }

    /**
     * Get a list of all users | passwords | IP-address | port
     *
     * @return list of users and passwords
     */
    public static String printUsers()
    {
        String output = "";
        String newline = System.lineSeparator();
        output += " No  \t IP Address         \t Port   \t Registration No  \t Passwords " + newline;
        output += "-----\t--------------------\t--------\t------------------\t-----------" + newline;

        String ip = Server_Application.LabExamServer.getIPAddress();
        int port = Server_Application.LabExamServer.getPort();

        int pos = 0;
        for (Candidate c : curExam.allCandidate)
        {
            ++pos;
            output += String.format(" %02d: \t", pos);
            output += String.format(" %-18s \t", ip);
            output += String.format(" %-6d \t", port);
            output += String.format(" %-16s \t", c.regno);
            output += String.format(" %-8s ", c.password) + newline;
        }

        return output;
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
        if (uid == -1) return false;
        Candidate cand = curExam.getCandidate(uid);
        if (cand == null) return false;

        if (!cand.password.equals(pass))
        {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("%s(%s) tried to login with wrong password.", cand.name, cand.regno));
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
        if (uid == -1) return false;

        String name = curExam.getCandidate(uid).name;
        if (!logins.contains(uid))
        {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("%s(%s) tried to log out but failed.", name, user));
            return false;
        }

        logins.remove(uid);
        invokeUserChanged(new UserChangeEvent(uid, true));

        Logger.getLogger("LabExam").log(Level.WARNING,
                String.format("%s(%s) logged out from %s", name, user, ip));

        return true;
    }
    //</editor-fold>

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
        for (UserChangedHandler handler : userChangeListener)
        {
            handler.userChanged(uce);
        }
    }
    //</editor-fold>

    /**
     * Announce a message to all connected clients
     *
     * @param message
     */
    public static void addAnnouncement(String message)
    {
        ++curAnnounceID;
        announcement.put(curAnnounceID, message);
        LabExamServer.AnnounceMessage(message);
    }

    /**
     * Submit an answer of current exam
     *
     * @param user Username who submitted
     * @param qid Question id of the answer
     * @param answer Answer body
     */
    public static void submitAnswer(String user, int qid, String answer)
    {
        try
        {
            Path path = curExam.ExamPath.toPath();
            path = path.resolve(user);
            path = path.resolve(String.format("Q%02d.txt", qid));

            FileOutputStream fos;
            fos = new FileOutputStream(path.toFile());
            fos.write(answer.getBytes());
            fos.close();

            Logger.getLogger("Lab Exam").log(Level.INFO,
                    user + " submitted answer for Question " + qid);

        }
        catch (Exception ex)
        {
            Logger.getLogger("Lab Exam").log(Level.SEVERE, String.format(
                    "Failed to receive " + user + "%s's answer for Question " + qid));
        }
    }

}
