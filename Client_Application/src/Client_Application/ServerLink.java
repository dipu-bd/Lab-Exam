/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Application;

import ExtraClass.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dipu
 */
public final class ServerLink {

    public static int port;
    public static String userName;
    public static String serverName;

    public static Object getResponce(Command command, Object... var)
    {
        try
        {
            //connecting to serverName using port                
            Socket server = new Socket(serverName, port);
            ObjectOutputStream outToServer = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream());

            //send login info       
            outToServer.writeObject(command);
            outToServer.flush();

            //send data
            for (Object v : var)
            {
                outToServer.writeObject(v);
            }

            //get response
            Object result = inFromServer.readObject();

            //close server
            outToServer.close();
            inFromServer.close();
            server.close();

            return result;
        }
        catch (IOException | ClassNotFoundException ex)
        {
            return null;
        }
    }

    public static int promptLogin(String pass)
    {
        Object result = getResponce(Command.LOGIN, userName, pass);
        if (result == null) return -1;
        if ((boolean) result) return 0;
        else return 1;
    }

    public static boolean logoutUser()
    {
        Object result = getResponce(Command.LOGOUT, userName);
        if (result == null) return false;
        return (boolean) result;
    }
}
