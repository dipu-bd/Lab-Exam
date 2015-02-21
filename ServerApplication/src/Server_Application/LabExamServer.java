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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manage clients input one by one
 *
 * @author Dipu
 */
public final class LabExamServer {

    private static ServerSocket serverSocket;

    public static String getIPAddress() {
        try {
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (UnknownHostException ex) {
            return "127.0.0.1";
        }
    }

    public static int getPort() {
        if (serverSocket == null || !serverSocket.isBound()) {
            return 0;
        }
        return serverSocket.getLocalPort();
    }

    public static void initialize() {
        try {
            int siz = CurrentExam.allUsers.size();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            serverSocket = new ServerSocket(0, siz + 10);
            serverSocket.setReuseAddress(true);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    LabExamServer.BeginListen();
                }
            });
            t.start();

            Logger.getLogger("LabExam").log(
                    Level.INFO, "Waiting for users to connect...");

        } catch (IOException ex) {
            Logger.getLogger("LabExam").log(Level.SEVERE,
                    "Error when attempted to initialize connection. " + ex.getMessage());
        }
    }

    public static void BeginListen() {
        // Waiting for clients in port -> serverSocket.getLocalPort()          
        while (true) {
            Logger.getLogger("LabExam").log(Level.INFO, "Waiting on port " + serverSocket.getLocalPort());
            try (Socket client = serverSocket.accept()) {
                String ip = client.getRemoteSocketAddress().toString();

                // Just connected
                Logger.getLogger("LabExam").log(Level.INFO, "Just connected to " + ip);

                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());

                String[] data = ((String) in.readObject()).split(" ");
                String user = data[0].trim();
                String pass = data[1].trim();
                if (CurrentExam.matchUser(user, pass)) {
                    CurrentExam.assignUser(user, client);
                    out.writeObject(true);
                    out.flush();                    
                } else {
                    out.writeObject(false);
                    out.flush();
                    client.close();
                    Logger.getLogger("LabExam").log(Level.WARNING,
                            user + " tried to connect but failed.");
                }
            } catch (Exception ex) {
                Logger.getLogger("LabExam").log(Level.SEVERE,
                        "Error while listening to the socket.", ex);
            }
        }
    }
}
