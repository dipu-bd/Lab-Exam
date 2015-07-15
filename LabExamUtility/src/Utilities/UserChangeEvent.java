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

import java.awt.AWTEvent;

/**
 * Event raised when a candidate's connection status is changed.
 */
@SuppressWarnings("serial")
public class UserChangeEvent extends AWTEvent {
    
    //user id
    private final int mUserId;
    //login status
    private final boolean mLoginStatus;
    //question id
    private final int mQuesId;
    
    /**
     * Gets the candidates id who is involved.
     * @return Id of the candidate.
     */
    public int getUserId() { return mUserId; } 
    
    /**
     * Gets if the candidate is logged in or logged out.
     * @return True if logged in; False if logged out.
     */
    public boolean mLoginStatus() { return mLoginStatus; } 
    
    /**
     * Gets the question id involved. -1 if no question is involved.
     * @return Id of the question. -1 if no question is involved.
     */
    public int getQuestionId() { return mQuesId; } 

    /**
     * Creates a new UserChangeEvent which specify that a candidate has logged in or logged out.
     * @param id Id of the involved candidate.
     * @param login True if user has logged in; False otherwise.
     */
    public UserChangeEvent(int id, boolean login)
    {
        super(id, 0);
        mUserId = id;
        mLoginStatus = login;
        mQuesId = -1; //default
    }

    /**
     * Creates a new UserChangeEvent which specify that a candidate has submitted answer to a question.
     * @param uid Id of the candidate.
     * @param qid Id of the question involved.
     */
    public UserChangeEvent(int uid, int qid)
    {
        super(uid, 0);
        mUserId = uid;
        mLoginStatus = true; //default
        mQuesId = qid;
    }
}
