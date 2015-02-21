/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Application;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dipu
 */
public final class ServerLink {

    public static Socket server;
    public static ObjectOutputStream outToServer;
    public static ObjectInputStream inFromServer;

    public static int PromptLogin(String serverName, int port, String userName, String password) {
        int result;
        try {
            //connecting to serverName using port                
            server = new Socket(serverName, port);

            //now connected to serverName 
            outToServer = new ObjectOutputStream(server.getOutputStream());

            //send login info                                
            outToServer.writeObject(userName + " " + password);
            outToServer.flush();

            //get response
            inFromServer = new ObjectInputStream(server.getInputStream());
            if (!(boolean) inFromServer.readObject()) { 
                Logger.getLogger("LabExam").log(Level.WARNING,
                        "Password mismatch.");
                result = -1;
            } else {
                Logger.getLogger("LabExam").log(Level.INFO,
                        "Connected to the server.");
                result = 0;
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger("LabExam").log(Level.SEVERE,
                    "Error while connecting to the server. " + ex.getMessage());
            result = -1;
        }
        return result;
    }
}
