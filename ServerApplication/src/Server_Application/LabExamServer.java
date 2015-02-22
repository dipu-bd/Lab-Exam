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

    public static void BeginListen()
    {
        // Waiting for clients in port -> serverSocket.getLocalPort()          
        String curip = getIPAddress();
        while (true)
        {
            Socket client = null;
            try
            {
                Logger.getLogger("LabExam").log(Level.INFO,
                        "Waiting at " + curip + " on port " + serverSocket.getLocalPort());

                client = serverSocket.accept();
                String ip = client.getRemoteSocketAddress().toString();

                // Just connected
                Logger.getLogger("LabExam").log(Level.INFO, "Just connected to " + ip);

                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());

                String[] data = ((String) in.readObject()).split(" ");
                String user = data[0].trim();
                String pass = data[1].trim();

                System.out.println(user + " " + pass);

                if (CurrentExam.matchUser(user, pass))
                {
                    CurrentExam.assignUser(user, client);
                    out.writeObject(true);
                    out.flush();
                }
                else
                {
                    out.writeObject(false);
                    out.flush();
                    client.close();
                    Logger.getLogger("LabExam").log(Level.WARNING,
                            user + " tried to connect but failed.");
                }
            }
            catch (IOException | ClassNotFoundException ex)
            {
                if (stopListening)
                {
                    Logger.getLogger("LabExam").log(Level.INFO, "Server Stopped.");
                    return;
                }
                else
                {
                    Logger.getLogger("LabExam").log(Level.SEVERE,
                            "Error while listening to the socket.", ex);
                    break;
                }
            }
        }
    }

    static void StopListening()
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
}
