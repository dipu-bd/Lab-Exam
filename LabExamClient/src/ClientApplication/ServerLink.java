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
 * Methods to communicate with server application.
 *
 * @author Dipu
 */
public class ServerLink
{

    //port of the server
    private int mPort;
    //ip address of the server
    private String mServerIP;
    //registration number of candidate
    private String mRegNo;

    /**
     * Connect to the server, send some data, and receive some corresponding
     * data.
     *
     * @param command Which type of data/request is being sent
     * @param var Data to send
     * @return Data received from the server
     */
    public Object getResponce(Command command, Object... var)
    {
        Object result;
        try (
                //connecting to mServerName using mPort
                Socket server = new Socket(mServerIP, mPort);
                ObjectOutputStream outToServer = new ObjectOutputStream(server.getOutputStream());
                ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream())) {

            //send login info
            outToServer.writeObject(command);
            outToServer.flush();

            //send data
            for (Object v : var) {
                outToServer.writeObject(v);
            }
            outToServer.flush();

            //get response
            result = inFromServer.readObject();
            return result;
        }
        catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Checks if the server is available right now.
     *
     * @return True if server is available; False otherwise.
     */
    public boolean isAvailable()
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
    public int promptLogin(String pass)
    {
        Object result = getResponce(Command.LOGIN, mRegNo, pass);
        if (result == null) return -1;
        if ((boolean) result) return 0;
        else return 1;
    }

    /**
     * Logout the current user from the server.
     *
     * @return True on success; False otherwise.
     */
    public boolean logoutUser()
    {
        Object result = getResponce(Command.LOGOUT, mRegNo);
        if (result == null) return false;
        return (boolean) result;
    }

    /**
     * Gets the start time of the exam.
     *
     * @return -1 in Error; A UNIX-style time in success.
     */
    public long getStartTime()
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
    public long getStopTime()
    {
        Object result = getResponce(Command.STOP_TIME);
        if (result == null) return -1;
        return (long) result;
    }
    
    /**
     * Gets the current time at server clock
     * @return Current time in milliseconds
     */
    public long getServerTime()
    {
        Object result = getResponce(Command.CURRENT_TIME);
        if(result == null) return System.currentTimeMillis();
        return (long) result;
    }

    /**
     * Submits an answer to the server.
     *
     * @param qid ID of question to submit answer.
     * @param answers Data of answers to submit.
     * @return True on success; False otherwise.
     */
    public boolean submitAnswer(int qid, Object[] answers)
    {
        Object result = getResponce(Command.SUBMIT, mRegNo, qid, answers);
        if (result == null) return false;
        return (boolean) result;
    }

    /**
     * Get the title of the current exam.
     *
     * @return "-" on failure; Exam Title on success.
     */
    public String getExamTitle()
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
    public ArrayList<Question> getAllQuestions()
    {
        Object result = getResponce(Command.ALL_QUESTION);
        ArrayList<Question> ret = new ArrayList<Question>();
        if (result == null) return ret;
        //add all question
        for (Object q : ((Object[]) result)) {
            ret.add(Question.class.cast(q));
        }
        return ret;
    }

    /**
     * Set the mPort address used in connection
     *
     * @param port Integer value for mPort number
     */
    public void setPort(int port)
    {
        mPort = port;
    }

    /**
     * Get the current mPort number used to connect
     *
     * @return Integer mPort number
     */
    public int getPort()
    {
        return mPort;
    }

    /**
     * Sets the current candidates registration no.
     *
     * @param user Registration number of the candidate.
     */
    public void setRegistrationNo(String user)
    {
        mRegNo = user.trim();
    }

    /**
     * Gets the current candidates registration no.
     *
     * @return Registration number of the candidate.
     */
    public String getRegistrationNo()
    {
        return mRegNo;
    }

    /**
     * Set the server address to connect
     *
     * @param server Address of the server
     */
    public void setServerIP(String server)
    {
        mServerIP = server.trim();
    }

    /**
     * Get the server address currently in use
     *
     * @return Server address or name.
     */
    public String getServerIP()
    {
        return mServerIP;
    }

}
