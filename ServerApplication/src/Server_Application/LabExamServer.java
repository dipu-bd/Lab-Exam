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
package Server_Application;

import ExtraClass.Command;
import ExtraClass.CurrentExam;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manage clients input one by one
 *
 * @author Dipu
 */
public final class LabExamServer {

    private static boolean stopListening = false;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static Command command;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;

    public static String getIPAddress()
    {
        try
        {
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        }
        catch (UnknownHostException ex)
        {
            return "127.0.0.1";
        }
    }

    public static int getPort()
    {
        if (serverSocket == null || !serverSocket.isBound())
        {
            return 0;
        }
        return serverSocket.getLocalPort();
    }

    public static void initialize()
    {
        int siz = CurrentExam.curExam.allCandidate.size();
        if (!createSocket(1993, siz + 10))
            createSocket(0, siz + 10);

        stopListening = false;
        (new Thread(new Runnable() {
            @Override
            public void run()
            {
                LabExamServer.BeginListen();
            }
        })).start();

        Logger.getLogger("LabExam").log(
                Level.INFO, "Waiting for users to connect...");
    }

    public static boolean createSocket(int port, int siz)
    {
        try
        {
            if (serverSocket != null) serverSocket.close();
            serverSocket = new ServerSocket(port, siz);
            serverSocket.setReuseAddress(true);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String getClientIP()
    {
        String ip = clientSocket.getRemoteSocketAddress().toString();
        if (ip.startsWith("/")) ip = ip.substring(1);
        return ip;
    }

    public static void BeginListen()
    {
        // Waiting for clients in port -> serverSocket.getLocalPort()          
        String curip = getIPAddress();
        while (true)
        {
            Logger.getLogger("LabExam").log(Level.INFO,
                    "Waiting at " + curip + " on port " + serverSocket.getLocalPort());

            try
            {
                clientSocket = serverSocket.accept();

                // Just connected
                Logger.getLogger("LabExam").log(Level.INFO, "Just connected to " + getClientIP());

                //get input-output
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());

                command = (Command) input.readObject();
                ProcessCommand();

                output.flush();
                output.close();
                input.close();
                clientSocket.close();
            }
            catch (IOException | ClassNotFoundException ex)
            {
                if (stopListening)
                {
                    Logger.getLogger("LabExam").log(Level.INFO, "Exam stopped.");
                }
                else
                {
                    Logger.getLogger("LabExam").log(Level.SEVERE,
                            "Error while listening to the socket.", ex);
                }
                break;
            }
        }
    }

    public static void StopListening()
    {
        try
        {
            stopListening = true;
            serverSocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(LabExamServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void AnnounceMessage(String message)
    {

    }

    private static void ProcessCommand() throws IOException, ClassNotFoundException
    {
        boolean result;
        String user, pass;

        switch (command)
        {
            case LOGIN:
                user = (String) input.readObject();
                pass = (String) input.readObject();
                result = CurrentExam.assignUser(user, pass, getClientIP());
                output.writeObject(result);
                output.flush();
                break;
            case LOGOUT:
                user = (String) input.readObject();
                output.writeObject(CurrentExam.removeUser(user, getClientIP()));
                output.flush();
                break;

        }
    }
}
