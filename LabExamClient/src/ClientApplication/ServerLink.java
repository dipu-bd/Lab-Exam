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

import Utilities.Command;
import Utilities.Question;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Dipu
 */
public final class ServerLink
{

    private static int port;
    private static String userName;
    private static String serverName;

    /**
     * Connect to the server, send some data, and receive some corresponding
     * data.
     *
     * @param command Which type of data/request is being sent
     * @param var Data to send
     * @return Data received from the server
     */
    public static Object getResponce(Command command, Object... var)
    {
        try {
            //connecting to serverName using port                
            Socket server = new Socket(serverName, port);
            ObjectOutputStream outToServer = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream());

            //send login info       
            outToServer.writeObject(command);
            outToServer.flush();

            //send data
            for (Object v : var) {
                outToServer.writeObject(v);
            }
            outToServer.flush();

            //get response
            Object result = inFromServer.readObject();

            //close server
            outToServer.close();
            inFromServer.close();
            server.close();

            return result;
        }
        catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

//<editor-fold defaultstate="collapsed" desc="Function to get different data from the server">
    /**
     * Checks if the server is available right now.
     *
     * @return True if server is available; False otherwise.
     */
    public static boolean isAvailable()
    {
        Object result = getResponce(Command.EMPTY);
        return (result != null && (boolean) result);
    }

    /**
     * Attempt to login to the server using current username and provided
     * password
     *
     * @param pass Password to use to login to the server
     * @return -1 in error; 0 in success; 1 is failure.
     */
    public static int promptLogin(String pass)
    {
        Object result = getResponce(Command.LOGIN, userName, pass);
        if (result == null) return -1;
        if ((boolean) result) return 0;
        else return 1;
    }

    /**
     * Logout the current user from the server.
     *
     * @return True on success; False otherwise.
     */
    public static boolean logoutUser()
    {
        Object result = getResponce(Command.LOGOUT, userName);
        if (result == null) return false;
        return (boolean) result;
    }

    /**
     * Gets the start time of the exam.
     *
     * @return -1 in Error; A UNIX-style time in success.
     */
    public static long getStartTime()
    {
        Object result = getResponce(Command.START_TIME);
        if (result == null) return -1;
        return (long) result;
    }

    /**
     * Gets the stop time of the exam.
     *
     * @return -1 in Error; A UNIX-style time in success.
     */
    public static long getStopTime()
    {
        Object result = getResponce(Command.STOP_TIME);
        if (result == null) return -1;
        return (long) result;
    }

    /**
     * Submits an answer to the server.
     *
     * @param qid ID of question to submit answer.
     * @param files List of files to submit.
     * @param data Data of files to submit.
     * @return True on success; False otherwise.
     */
    public static boolean submitAnswer(int qid, Object[] files, Object[] data)
    {
        Object result = getResponce(Command.SUBMIT, userName, qid, files, data);
        if (result == null) return false;
        return (boolean) result;
    }

    /**
     * Get the title of the current exam.
     *
     * @return "-" on failure; Exam Title on success.
     */
    public static String getExamTitle()
    {
        Object result = getResponce(Command.EXAM_TITLE);
        if (result == null) return "-";
        return (String) result;
    }

    /**
     * Get the list of all questions
     *
     * @return Empty string will return in case failed or exam is not running.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Question> getAllQuestions()
    {
        Object result = getResponce(Command.ALL_QUES);
        if (result == null)
            return new ArrayList<>();
        return (ArrayList<Question>) result;
    }

    /**
     * Get all the announcements that has been made.
     *
     * @return List of array with desired messages
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> getAnnouncements(int curSiz)
    {
        Object result = getResponce(Command.ANNOUNCEMENT, curSiz);
        if (result == null)
            return new ArrayList<>();
        return (ArrayList<String>) result;
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Get or Set methods">
    /**
     * Set the port address used in connection
     *
     * @param port Integer value for port number
     */
    public static void setPort(int port)
    {
        ServerLink.port = port;
    }

    /**
     * Get the current port number used to connect
     *
     * @return Integer port number
     */
    public static int getPort()
    {
        return ServerLink.port;
    }

    /**
     * Set the current username used to connect
     *
     * @param user Registration number of the user
     */
    public static void setUsername(String user)
    {
        ServerLink.userName = user.trim();
    }

    /**
     * Get the currently assigned registration number
     *
     * @return Registration number currently in use.
     */
    public static String getUsername()
    {
        return ServerLink.userName;
    }

    /**
     * Set the server address to connect
     *
     * @param server Address of the server
     */
    public static void setServerName(String server)
    {
        ServerLink.serverName = server.trim();
    }

    /**
     * Get the server address currently in use
     *
     * @return Server address or name.
     */
    public static String getServerName()
    {
        return ServerLink.serverName;
    }
//</editor-fold>
}
