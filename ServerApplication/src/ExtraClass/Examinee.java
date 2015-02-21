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

import java.io.Serializable;
import java.net.Socket;
import java.security.SecureRandom;

/**
 *
 * @author Dipu
 */
public final class Examinee implements Serializable {

    /**
     * Secured random number class to generate password and other uses
     */
    public final static SecureRandom random = new SecureRandom();

    public Examinee(String user) {
        name = user;
        client = null;
        randomizePassword();
    }

    public String name;
    public Socket client;
    public String password;

    /**
     * Generate a random password and set it to password field
     */
    public void randomizePassword() {
        this.password = "";
        for (int i = 0; i < 6; ++i) {
            char ch = (char) (Examinee.random.nextInt(26) + 'a');
            this.password += Character.toString(ch);
        }
    }
}
