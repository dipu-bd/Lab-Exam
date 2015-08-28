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
    //question list    
    private final ArrayList<Question> mQuestions = new ArrayList<>();

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
        if (result == null) {
            return -1;
        }
        if ((boolean) result) {
            return 0;
        }
        else {
            return 1;
        }
    }

    /**
     * Logout the current user from the server.
     *
     * @return True on success; False otherwise.
     */
    public boolean logoutUser()
    {
        Object result = getResponce(Command.LOGOUT, mRegNo);
        if (result == null) {
            return false;
        }
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
        if (result == null) {
            return -1;
        }
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
        if (result == null) {
            return -1;
        }
        return (long) result;
    }

    /**
     * Gets the current time at server clock
     *
     * @return Current time in milliseconds
     */
    public long getServerTime()
    {
        Object result = getResponce(Command.CURRENT_TIME);
        if (result == null) {
            return System.currentTimeMillis();
        }
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
        if (result == null || !(boolean) result) {
            return false;
        }
        return true;
    }

    /**
     * Get the title of the current exam.
     *
     * @return "-" on failure; Exam Title on success.
     */
    public String getExamTitle()
    {
        Object result = getResponce(Command.EXAM_TITLE);
        if (result == null) {
            return "-";
        }
        return (String) result;
    }

    /**
     * Gets the name of the candidate that has logged in.
     *
     * @return Name of the candidate.
     */
    public String getCandidateName()
    {
        Object result = getResponce(Command.CANDIDATE_NAME, mRegNo);
        if (result == null) {
            return "-";
        }
        return (String) result;
    }

    /**
     * Downloads and sets the list of all questions locally
     */
    @SuppressWarnings("unchecked")
    public void downloadAllQuestions()
    {
        Object result = getResponce(Command.ALL_QUESTION);
        if (result != null) {
            //add all question
            mQuestions.clear();
            for (Object q : ((Object[]) result)) {
                mQuestions.add(Question.class.cast(q));
            }
        }
    }

    /**
     * Get the list of all questions
     *
     * @return Empty string will return in case failed or exam is not running.
     */
    @SuppressWarnings("unchecked")
    public final ArrayList<Question> getAllQuestions()
    {
        if (mQuestions.isEmpty()) {
            downloadAllQuestions();
        }
        return mQuestions;
    }

    /**
     * Gets the number of questions.
     *
     * @return Number of questions.
     */
    public int getQuestionCount()
    {
        return getAllQuestions().size();
    }

    /**
     * Gets the total mark of the examination.
     *
     * @return Total mark of the examination.
     */
    public int getTotalMarks()
    {
        int total = 0;
        for (Question q : getAllQuestions()) {
            total += q.getMark();
        }
        return total;
    }

    /**
     * Gets the number of total submission in this session.
     *
     * @return Number of total submission in this session.
     */
    public int getTotalSubmission()
    {
        Object result = getResponce(Command.TOTAL_SUBMISSION, mRegNo);
        if (result == null) {
            return 0;
        }
        return (int) result;
    }

    /**
     * Gets the number of question which answer has been submitted.
     *
     * @return Number of question which answer has been submitted.
     */
    public int getSubmittedQuestionCount()
    {
        Object result = getResponce(Command.QUES_SUBMITTED, mRegNo);
        if (result == null) {
            return 0;
        }
        return (int) result;
    }

    /**
     * Gets the total marks that has been submitted by the user.
     *
     * @return The total marks that has been submitted by the user.
     */
    public int getSubmittedMarks()
    {
        Object result = getResponce(Command.MARK_SUBMITTED, mRegNo);
        if (result == null) {
            return 0;
        }
        return (int) result;
    }

    /**
     * Gets the total number of login attempts made by this user.
     *
     * @return The total number of login attempts made by this user.
     */
    public int getLoginAttempts()
    {
        Object result = getResponce(Command.LOGIN_ATTEMPT, mRegNo);
        if (result == null) {
            return 0;
        }
        return (int) result;
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
        if (mServerIP.isEmpty()) {
            mServerIP = "127.0.0.1";
        }
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
