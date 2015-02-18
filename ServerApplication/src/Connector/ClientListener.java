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
package Connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manage clients input one by one
 * @author Dipu
 */
public final class ClientListener {

    private static ServerSocket serverSocket;
    public static int CurrentPort = 1993;

    private static final int MIN_PORT_NUMBER = 1000;
    private static final int MAX_PORT_NUMBER = 9999;

    public static void initialize() {
        try {
            GeneratePort();
            serverSocket = new ServerSocket(CurrentPort);
            BeginListen();
        } catch (Exception ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, "Initialization failed.", ex);
        }
    }

    public static String GetIP() {
        try {
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (UnknownHostException ex) {
            return "127.0.0.1";
        }
    }

    public static void GeneratePort() {
        while (!isPortAvailable(CurrentPort)) {
            CurrentPort = ExtraClass.CurrentExam.random.nextInt();
            CurrentPort %= MAX_PORT_NUMBER - MIN_PORT_NUMBER + 1;
            CurrentPort += MIN_PORT_NUMBER;
        }
        
        Logger.getLogger(ClientListener.class.getName()).info("Port was set to " + CurrentPort);
    }

    public static boolean isPortAvailable(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            return false;
        }
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            DatagramSocket ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static void BeginListen() {
        while (true) {
            // Waiting for clients in port -> serverSocket.getLocalPort()  
            Logger.getLogger(ClientListener.class.getName()).info("Waiting for clients in port " + serverSocket.getLocalPort());

            try (Socket server = serverSocket.accept()) {
                String ip = String.format("{0}:{1}",
                        server.getRemoteSocketAddress(), server.getLocalPort());

                // Just connected
                Logger.getLogger(ClientListener.class.getName()).info("Just connected to " + ip);

                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());

                ProcessCommand.process(ip, in, out);
                in.close();
                out.flush();
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(ClientListener.class.getName()).severe("Error while listening to the socket.\r\n" + ex.getMessage());
            }
        }
    }
    
    
}
