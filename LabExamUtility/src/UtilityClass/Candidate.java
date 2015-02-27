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
package UtilityClass;

import java.io.Serializable;
import java.security.SecureRandom;

/**
 *
 * @author Dipu
 */
public final class Candidate implements Serializable {

    private final static int PASS_LENGTH = 8;
    
    /**
     * Secured random number class to generate password and other uses
     */
    public final static SecureRandom random = new SecureRandom();

    public Candidate(int id)
    {
        uid = id;
        name = "";
        regno = "";
        randomizePassword();
    }
    
    @Override
    public String toString()
    {
        return String.format("%s (%s)", name, regno);
    }    

    public int uid;
    public String name;
    public String regno;
    public String password;

    /**
     * Generate a random password and set it to password field
     */
    public void randomizePassword()
    {
        this.password = "";
        for (int i = 0; i < PASS_LENGTH; ++i)
        {
            char ch = (char) (Candidate.random.nextInt(26) + 'a');
            this.password += Character.toString(ch);
        }
    }
}
