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

import Server_Application.Program;
import java.awt.AWTEvent;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * Currently opened exam session
 *
 * @author Dipu
 */
public final class CurrentExam {

    /**
     * File where the curExam data is saved
     */
    public static File examFile = new File("default." + Program.defExtension);
    /**
     * Current exam object that is been working with
     */
    public static Examination curExam = new Examination();
    /**
     * Sockets for clients
     */
    public final static HashMap<String, Examinee> allUsers = new HashMap<>();

    /**
     * Read a file and extract Examination class information stored in it
     *
     * @param file File object to open
     * @throws FileNotFoundException When specific file is not found
     * @throws IOException If any error occur while reading the file
     * @throws ClassNotFoundException If specific file does not have desired
     * format
     */
    public static void Open(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        examFile = file;
        allUsers.clear();
        try (FileInputStream fin = new FileInputStream(examFile);
                ObjectInputStream ois = new ObjectInputStream(fin)) {
            curExam = (Examination) ois.readObject();
            for (String s : curExam.userList) {
                allUsers.put(s, new Examinee(s));
            }
        }
    }

    /**
     * Write curExam object to a specific file
     *
     * @throws FileNotFoundException When file/directory is not found
     * @throws IOException When writing to file is failed due to some reason
     */
    public static void Save() throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(examFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(curExam);
            oos.flush();
        }
    }

    /**
     * Checks if given user and pass matches existing one.
     *
     * @param user Examinee name
     * @param pass Password
     * @return True if match found, False otherwise.
     */
    public static boolean matchUser(String user, String pass) {
        if (!allUsers.containsKey(user)) {
            return false;
        }
        return allUsers.get(user).password.equals(pass);
    }

    /**
     * Get a list of all users | passwords | IP-address | port
     *
     * @return list of users and passwords
     */
    public static String printUsers() {
        String output = "";
        output += "  No \t Usernames     \tPasswords \t    IP Address     \t   Port  \n";
        output += "-----\t---------------\t----------\t--------------------\t--------\n";

        String ip = Server_Application.LabExamServer.getIPAddress();
        int port = Server_Application.LabExamServer.getPort();

        int pos = 0;
        for (String str : curExam.userList) {
            ++pos;
            output += String.format("%3d: \t", pos);
            output += String.format(" %-14s\t", str);
            output += String.format(" %-8s \t", allUsers.get(str).password);
            output += String.format(" %-18s \t %-6d \n", ip, port);
        }

        return output;
    }

    /**
     * Submit an answer of current exam
     *
     * @param user Username who submitted
     * @param qid Question id of the answer
     * @param answer Answer body
     */
    public static void submitAnswer(String user, int qid, String answer) {
        try {
            Path path = curExam.ExamPath.toPath();
            path = path.resolve(user);
            path = path.resolve(String.format("Q%d.txt", qid));

            File file = path.toFile();
            file.createNewFile();

            FileOutputStream fos;
            fos = new FileOutputStream(file);
            fos.write(answer.getBytes());
            fos.flush();
            fos.close();

            Logger.getLogger("Lab Exam").log(Level.INFO,
                    user + " submitted answer for Question " + qid);

        } catch (Exception ex) {
            Logger.getLogger("Lab Exam").log(Level.SEVERE, String.format(
                    "Failed to receive " + user + "%s's answer for Question " + qid));
        }
    }

    /**
     * Assign a Socket to specific user
     *
     * @param user Username to assign
     * @param client Socket to be assigned
     */
    public static void assignUser(String user, Socket client) {
        try {
            Examinee exmin = allUsers.get(user);

            if (exmin.client != null && exmin.client.isBound()) {
                Logger.getLogger("LabExam").log(Level.WARNING,
                        String.format("Disconnecting %s from %s", user, exmin.client.getInetAddress()));
                exmin.client.close();
            }

            exmin.client = client;
            Logger.getLogger("LabExam").log(Level.INFO,
                    user + " connected via " + client.getInetAddress());

            invokeUserChanged(new UserChangeEvent(user, client));

        } catch (IOException ex) {
            Logger.getLogger("LabExam").log(Level.SEVERE,
                    "Error while assigning a socket to " + user);
        }
    }

    private static ArrayList<UserChangedHandler> userChangeListener = new ArrayList<>();

    public static boolean addUserChangedHandler(UserChangedHandler handler) {
        return userChangeListener.add(handler);
    }

    public static boolean removeUserChangedHandler(UserChangedHandler handler) {
        return userChangeListener.remove(handler);
    }

    public static void invokeUserChanged(UserChangeEvent uce) {
        // Notify everybody that may be interested.
        for (UserChangedHandler handler : userChangeListener) {
            handler.userChanged(uce);
        }
    }

}
