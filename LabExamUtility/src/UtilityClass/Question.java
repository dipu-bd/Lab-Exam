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

/**
 * Question description
 *
 * @author Dipu
 */
public class Question implements Serializable {

    /**
     * Constructor
     *
     * @param id Question id for this object
     */
    public Question(int id)
    {
        ID = id;
        Title = "Question " + Integer.toString(id);
        Body = "";
        Mark = 0;
    }

    /**
     * ID of this question
     */
    public int ID;
    /**
     * Title of this question
     */
    public String Title;
    /**
     * Mark for this question
     */
    public int Mark;
    /**
     * Full question description
     */
    public String Body;

    @Override
    public String toString()
    {
        return String.format("%2d : %s [Mark = %d]", ID, Title, Mark);
    }
}
