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

/**
 * Command for passing data
 *
 * @author Dipu
 */
public enum Command {

    /**
     * Empty command
     */
    EMPTY,
    /**
     * Login to the server
     */
    LOGIN,
    /**
     * Logout from the server
     */
    LOGOUT,
    /**
     * Get the examination start time
     */
    START_TIME,
    /**
     * Get the examination class object
     */
    EXAM_TITLE,
    /**
     * Submit answer to an specific question
     */
    SUBMIT,
    /**
     * Get the last announcement id
     */
    ANNOUNCEMENT,
    /**
     * Get the ending time of the exam
     */
    STOP_TIME,
    /**
     * Get list of all questions
     */
    ALL_QUES
}
