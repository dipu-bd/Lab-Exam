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
package Utilities;
 
import java.io.Serializable;
import java.security.SecureRandom;

/**
 * Class for storing data on candidates of the exam
 */ 
public final class Candidate implements Serializable {

    private final static int PASSWORD_LENGTH = 8;
    private static final long serialVersionUID = 1L;
    
    /**
     * Secured random number class to generate passwords
     */
    public Candidate(int id)
    {
        mId = id;
        mName = "";
        mRegNo = "";
        randomizePassword();
    }    
            
    //userid
    private int mId;
    //user name
    private String mName;
    //registration number
    private String mRegNo;
    //password
    private String mPassword;
    
    //overrides the default toString() methdod
    @Override
    public String toString()
    {
        return String.format("%s (%s)", mName, mRegNo);
    }     
    
    /**
     * Gets the ID of the candidate.
     * @return ID of the candidate.
     */
    public int getId() { return mId; }
    /**
     * Sets the ID of the candidate.
     * @param id ID of the candidate
     */
    public void setId(int id) { mId = id; }
    
    /**
     * Gets the name of the candidate.
     * @return Name as string.
     */
    public String getName() { return mName; }
    /**
     * Sets the name of the candidate.
     * @param name Name as string.
     */
    public void setName(String name) { mName = name; }
    
    /**
     * Gets the registration number of the candidate.
     * @return Registration number of the candidate.
     */
    public String getRegNo() { return mRegNo; }
    /**
     * Sets the registration number of the candidate.
     * @param regNo Registration number of the candidate.
     */
    public void setRegNo(String regNo) { mRegNo = regNo; }
    
    /**
     * Gets the password of the candidate.
     * @return Password as string.
     */
    public String getPassword() { return mPassword; }    
    /**
     * Sets the password of the candidate.
     * @param password Password as string.
     */
    public void setPassword(String password) { mPassword = password; }
        
    /**
     * Generate a random password and set it to password field
     */
    public void randomizePassword()
    {        
        SecureRandom random = new SecureRandom();        
        String pass = "";        
        for (int i = 1; i <= PASSWORD_LENGTH; ++i)
        {
            pass += Character.toString((char) (random.nextInt(26) + 'a'));
        }
        setPassword(pass);
    }
       
}
