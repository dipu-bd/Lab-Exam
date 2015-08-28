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
 * Commands used for passing data between server and client computers.
 */
public enum Command
{
    /**
     * Empty command to check connection.
     */
    EMPTY,
    /**
     * Login to the server.
     */
    LOGIN,
    /**
     * Logout from the server.
     */
    LOGOUT,
    /**
     * Get the examination start time.
     */
    START_TIME,
    /**
     * Get the examination class object.
     */
    EXAM_TITLE,
    /**
     * Submit answer to an specific question.
     */
    SUBMIT,
    /**
     * Get the ending time of the exam.
     */
    STOP_TIME,
    /**
     * Get the current time in server.
     */
    CURRENT_TIME,
    /**
     * Get list of all questions.
     */
    ALL_QUESTION,
    /**
     * Get the name of candidate.
     */
    CANDIDATE_NAME,
    /**
     * Number of total submissions on all question by a candidate.
     */
    TOTAL_SUBMISSION,
    /**
     * Total marks submitted by a candidate.
     */
    MARK_SUBMITTED,
    /**
     * Number of total question submitted by a candidate.
     */
    QUES_SUBMITTED,
    /**
     * Number of time user has logged in
     */
    LOGIN_ATTEMPT
}
