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

/**
 * Classes to store question description 
 */
public class Question implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the class
     * @param id Question id for this object
     */
    public Question(int id)
    {
        mId = id;     
        mMark = 0; 
        mTitle = "Question " + Integer.toString(id);   
    }
    
    // ID of this question
    private int mId;
    // Title of this question    
    private String mTitle;
    // Mark for this question     
    private int mMark;    
    // Byte data of PDF question
    public byte[] mBody;

    // overrides the default toString() method
    @Override
    public String toString()
    {
        return String.format("%2d : %s [Mark = %d]", mId, mTitle, mMark);
    }
    
    /**
     * Gets the id of this question.
     * @return Id of the question.
     */
    public int getId() { return mId; }
    /**
     * Sets the id of the question.
     * @param id Id of the question.
     */
    public void setId(int id) { mId = id; }
    
    /**
     * Gets the title of the question.
     * @return Title of the question.
     */
    public String getTitle() { return mTitle; }
    /**
     * Sets the title of the question.
     * @param title Title of the question.
     */
    public void setTitle(String title) { mTitle = title; }
    
    /**
     * Gets the mark of the question.
     * @return Mark of the question.
     */
    public int getMark() { return mMark; }    
    /**
     * Gets the mark of the question.
     * @param mark Mark of the question.
     */
    public void setMark(int mark) { mMark = mark; }
        
    /**
     * Gets the PDF description of the question.
     * @return byte[] array of PDF description.
     */
    public byte[] getBody() { return mBody; }
    /**
     * Sets the PDF description of the question.
     * @param body byte[] array of PDF description.
     */
    public void setBody(byte[] body) { mBody = body; }
 }
