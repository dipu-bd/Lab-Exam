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

import Utilities.Command;
import Utilities.Examination;
import Utilities.Question;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manage clients input one by one
 *
 * @author Dipu
 */
public class LabExamServer {

    private static final int DEFAULT_PORT = 1661;
    private static final int MAX_SIMULTANEOUS_CONNECTION = 25;

    public LabExamServer() {
        mCurExam = null;
        mServerSocket = null;
        mStopListening = false;
    }

    private CurrentExam mCurExam;
    //true to stop listening
    private boolean mStopListening;
    //server socket to receive clients
    private ServerSocket mServerSocket;

    /**
     * Sets a new current exam object to use in server.
     *
     * @param curExam Current exam to begin.
     */
    public void setCurrentExam(CurrentExam curExam) {
        this.StopListening();
        mCurExam = curExam;
        this.initialize(); //initialize server
    }

    /**
     * Gets the current IP address of the server machine.
     *
     * @return IP address of the server machine.
     */
    public static String getServerIPAddress() {
        try {
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        }
        catch (UnknownHostException ex) {
            return "127.0.0.1";
        }
    }

    /**
     * Gets the IP address of a client socket.
     *
     * @param clientSocket Socket to get IP address.
     * @return IP address of the client socket.
     */
    public static String getClientIP(Socket clientSocket) {
        String ip = clientSocket.getRemoteSocketAddress().toString();
        if (ip.startsWith("/")) {
            return ip.substring(1);
        }
        return ip;
    }

    /**
     * Gets the address of the current port of the server.
     *
     * @return Port address of the server.
     */
    public int getPort() {
        if (mServerSocket != null && mServerSocket.isBound()) {
            return mServerSocket.getLocalPort();
        }
        return 0;
    }

    /**
     * Checks whether the exam server is up and running.
     *
     * @return True if server is busy; False otherwise.
     */
    public boolean isBusy() {
        return (mServerSocket != null && !mServerSocket.isClosed());
    }

    /**
     * Starts the server and wait for the clients to connect.
     */
    public void StartListening() {
        if (!isBusy()) {
            mServerSocket = null;
            initialize();
        }
    }

    /**
     * Stops the server from waiting for clients.
     */
    public void StopListening() {
        try {
            if (isBusy()) {
                mStopListening = true;
                mServerSocket.close();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(LabExamServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new server socket.
     *
     * @param port Port address to use.
     * @param siz Number of client socket to receive simultaneously.
     * @return True on success; False otherwise.
     */
    private boolean createSocket(int port, int siz) {
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
            mServerSocket = new ServerSocket(port, siz);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * Initializes a server to run an exam
     */
    private void initialize() {
        //try to create socket with default port
        //   on failure use socket's own choice
        if (!createSocket(DEFAULT_PORT, MAX_SIMULTANEOUS_CONNECTION)) {
            createSocket(0, MAX_SIMULTANEOUS_CONNECTION);
        }

        // run on a separate thread
        mStopListening = false;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                beginListening();
            }
        })).start();

        Logger.getLogger("LabExam").log(
                Level.INFO, "Waiting for users to connect...");
    }

    /**
     * Start the server and wait for clients indefinitely.
     */
    private void beginListening() {
        // Waiting for clients in port -> serverSocket.getLocalPort()                  
        Logger.getLogger("LabExam").log(Level.INFO,
                String.format("Waitinig on %s on port %d.",
                        getServerIPAddress(), mServerSocket.getLocalPort()));

        while (!mStopListening) {
            try (Socket clientSocket = mServerSocket.accept()) {
                ProcessCommand(clientSocket);
                Thread.sleep(15);
            }
            catch (IOException | InterruptedException | ClassNotFoundException ex) {
                //ex.printStackTrace();
                if (!mStopListening) {
                    Logger.getLogger("LabExam").log(Level.SEVERE,
                            "Error while listening to the socket.", ex);
                }
            }
        }

        mStopListening = false;
        Logger.getLogger("LabExam").log(Level.INFO, "Exam server stopped.");
    }

    /**
     * Receive and process the command from client sockets
     *
     * @param client Client socket to work with.
     */
    private void ProcessCommand(Socket client)
            throws IOException, ClassNotFoundException {

        int id;
        String regNo, pass;
        Examination exam = mCurExam.getExamination();

        //get input-output
        ObjectInputStream input = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        Command command = (Command) input.readObject();

        //Logger.getLogger("LabExam").log(Level.INFO,
        //      String.format("Received %s packet from %s", command, getClientIP(client)));
        //process commands        
        switch (command) {
            case EMPTY:
                output.writeObject(true);
                break;

            case START_TIME:
                output.writeObject(exam.getStartTime().getTime());
                break;

            case STOP_TIME:
                output.writeObject(exam.getStopTime().getTime());
                break;

            case CURRENT_TIME:
                output.writeObject(System.currentTimeMillis());
                break;

            case EXAM_TITLE:
                output.writeObject(exam.getExamTitle());
                break;

            case LOGOUT:
                regNo = (String) input.readObject();
                output.writeObject(mCurExam.removeUser(regNo, getClientIP(client)));
                break;

            case LOGIN:
                regNo = (String) input.readObject();
                pass = (String) input.readObject();
                output.writeObject(mCurExam.assignUser(regNo, pass, getClientIP(client)));
                break;

            case ALL_QUESTION:
                output.writeObject(exam.getAllQuestion().toArray());
                break;

            case SUBMIT:
                regNo = (String) input.readObject();
                id = (int) input.readObject();
                Object[] data = (Object[]) input.readObject();
                output.writeObject(mCurExam.receiveAnswer(regNo, id, data));
                break;

            case CANDIDATE_NAME:
                regNo = (String) input.readObject();
                id = exam.getCandidateID(regNo);
                if (id >= 0) {
                    output.writeObject(exam.getCandidate(id).getName());
                }
                break;

            case LOGIN_ATTEMPT:
                regNo = (String) input.readObject();
                id = exam.getCandidateID(regNo);
                if (id >= 0) {
                    output.writeObject(mCurExam.getCandidateStatus(id).getLoginCount());
                }
                break;
                
            case TOTAL_SUBMISSION:
                regNo = (String) input.readObject();
                id = exam.getCandidateID(regNo);
                if (id >= 0) {
                    output.writeObject(mCurExam.getCandidateStatus(id).getTotalSubmissions());
                }
                break;

            case MARK_SUBMITTED:
                regNo = (String) input.readObject();
                id = exam.getCandidateID(regNo);
                if (id >= 0) {
                    output.writeObject(mCurExam.getCandidateStatus(id).getTotalMarkSubmitted());
                }
                break;

            case QUES_SUBMITTED:
                regNo = (String) input.readObject();
                id = exam.getCandidateID(regNo);
                if (id >= 0) {
                    output.writeObject(mCurExam.getCandidateStatus(id).getQuestionSubmitted());
                }
                break;

            default:
                output.writeObject(null);
                break;
        }

        output.close();
        input.close();
    }
}
