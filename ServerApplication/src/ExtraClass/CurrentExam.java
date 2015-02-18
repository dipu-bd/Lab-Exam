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
package ExtraClass;

import Server_Application.Program;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
 
/**
 * Currently opened exam session
 * @author Dipu
 */
public final class CurrentExam {
    
    /**
     * Secured random number class to generate password and other uses
     */
    public static final SecureRandom random = new SecureRandom();
    
    /**
     * File where the curExam data is saved
     */
    public static File examFile = new File("default." + Program.defExtension);
    
    /**
     * Current exam object that is been working with
     */
    public static Examination curExam = new Examination();
    
    public static HashMap<String, String> ipToUsername = new HashMap<>();

    private static String getRandomPass() {
        String res = "";
        for (int i = 0; i < 6; ++i) {
            char ch = (char) (random.nextInt(26) + 'a');
            res += Character.toString(ch);
        }
        return res;
    }

    /**
     * Generate password and save usernames for current exam 
     * @param list list of users
     */
    public static void GeneratePass(String[] list) {
        curExam.userToPass.clear();
        for (String str : list) {
            String s = str.trim();
            if (s.length() > 0) {
                curExam.userToPass.put(s, getRandomPass());
            }
        }
    }
    
    /**
     * Get a list of all users, passwords and IP-address:port
     * @return list of users and passwords
     */
    public static String printUsers()
    {        
        String output = "";
        output += " No \t Usernames \t Passwords \t IP Address : Port \n";
        output += "----\t-----------\t-----------\t-------------------\n";
                           
        String ip = Connector.ClientListener.GetIP();
        int port = Connector.ClientListener.CurrentPort;
        
        int pos = 0;
        for(String str : curExam.getUsers()) {   
            ++pos;
            output += String.format("%3d:\t", pos);
            output += String.format(" %-10s\t", str);
            output += String.format(" %-10s\t", curExam.userToPass.get(str));
            output += String.format(" %s:%d\n", ip, port);
        }
        
        return output;
    }

    /**
     * Read a file and extract Examination class information stored in it
     * @param file File object to open
     * @throws FileNotFoundException When specific file is not found
     * @throws IOException If any error occur while reading the file
     * @throws ClassNotFoundException If specific file does not have desired format
     */
    public static void Open(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        examFile = file;
        ipToUsername.clear(); 
        try (FileInputStream fin = new FileInputStream(examFile);
                ObjectInputStream ois = new ObjectInputStream(fin)) {
            curExam = (Examination) ois.readObject();             
        }
    }

    /**
     * Write curExam object to a specific file
     * @throws FileNotFoundException When file/directory is not found
     * @throws IOException When writing to file is failed due to some reason
     */
    public static void Save() throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(examFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(curExam);
            oos.flush();
        }
    }
    
    /**
     * Submit an answer of current exam
     * @param user Username who submitted
     * @param qid Question id of the answer
     * @param answer Answer body
     */
    public static void submitAnswer(String user, int qid, String answer) {
        
    }
}
