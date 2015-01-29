/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connector;

import java.net.*;
import java.io.*;

/**
 *
 * @author Dipu
 */
public class ServerPC 
{
    public static int PromptLogin(String serverName, int port, String userName, String password) 
    {
        int result = 0;        
        try
        {
            //connecting to serverName using port
            try (Socket client = new Socket(serverName, port)) {                
                //now connected to serverName
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                
                //send login info
                out.writeUTF("Login\\" + userName + "\\" + password);
                
                //get response
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);
                result = (in.readBoolean()) ? 1 : -1;
            }
        } 
        catch (IOException ex) 
        {
            result = 0;
        }        
        return result;
    }
}
