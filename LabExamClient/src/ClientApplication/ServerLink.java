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

    public static int port;
    public static String userName;
    public static String serverName;

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
            return null;
        }
    }

    public static boolean isAvailable()
    {
        Object result = getResponce(Command.EMPTY);
        return (result != null && (boolean) result);
    }

    public static int promptLogin(String pass)
    {
        Object result = getResponce(Command.LOGIN, userName, pass);
        if (result == null)
            return -1;
        if ((boolean) result)
            return 0;
        else
            return 1;
    }

    public static boolean logoutUser()
    {
        Object result = getResponce(Command.LOGOUT, userName);
        if (result == null)
            return false;
        return (boolean) result;
    }

    public static long getStartTime()
    {
        Object result = getResponce(Command.START_TIME);
        if (result == null)
            return -1;
        return (long) result;
    }

    public static long getStopTime()
    {
        Object result = getResponce(Command.STOP_TIME);
        if (result == null)
            return -1;
        return (long) result;
    }

    public static boolean submitAnswer(int qid, String answer)
    {
        Object result = getResponce(Command.SUBMIT, userName, qid, answer);
        if (result == null)
            return false;
        return (boolean) result;
    }

    public static String getExamTitle()
    {
        Object result = getResponce(Command.EXAM_TITLE);
        if (result == null)
            return "-";
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
}
