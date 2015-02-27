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

import java.awt.AWTEvent;

/**
 *
 * @author Dipu
 */
@SuppressWarnings("serial")
public class UserChangeEvent extends AWTEvent {

    public int uid;
    public boolean status;
    public int quesID;

    public UserChangeEvent(int id, boolean login)
    {
        super(id, 0);
        this.uid = id;
        this.status = login;
        this.quesID = -1;
    }

    public UserChangeEvent(int uid, int qid)
    {
        super(uid, 0);
        this.uid = uid;
        this.quesID = qid;
        this.status = true;
    }
}
