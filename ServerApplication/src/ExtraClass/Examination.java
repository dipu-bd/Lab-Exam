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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Examination session object
 * @author Dipu
 */
public class Examination implements Serializable {

    /**
     * Constructor of examination
     */
    public Examination() {
        ExamTitle = "Lab Exam";
        StartTime = new Date(System.currentTimeMillis());
        Duration = 120;
        LastProbID = 1;
        ExamPath = new File(ExamTitle);
    }

    /**
     * Name of the exam
     */
    public String ExamTitle;
    
    /**
     * Path where the answers will be saved
     */
    public File ExamPath;
    
    /**
     * Start time of the exam
     */
    public Date StartTime;

    /**
     * Duration in minute
     */
    public int Duration;

    /**
     * All questions of the exam
     */
    public HashMap<Integer, Question> AllQuestion = new HashMap<>();
    public int LastProbID = 1;
    
    /**
     * Registered usernames for the exam
     */
    public HashMap<String, String> userToPass = new HashMap<>();

    /**
     * gets the total marks for the exam
     *
     * @return integer value with total marks
     */
    public int getTotalMarks() {
        int marks = 0;
        for (Question q : AllQuestion.values()) {
            marks += q.Mark;
        }
        return marks;
    }
    
    public void addQuestion()
    {
        AllQuestion.put(LastProbID, new Question(LastProbID));
        ++LastProbID;
    }
    
    public void deleteQuestion(int id)
    {
        if(!AllQuestion.containsKey(id)) return;
        AllQuestion.remove(id);
    }

    /**
     * Get all usernames in sorted order
     *
     * @return ArrayList containing names
     */
    public ArrayList<String> getUsers() {
        ArrayList<String> keys = new ArrayList<>();
        for (String s : userToPass.keySet()) {
            keys.add(s);
        }
        Collections.sort(keys, new CustomComparator());
        return keys;
    }

    /**
     * Comparator for getNames() method
     */
    private class CustomComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            try {
                return (int) (Long.parseLong(s1) - Long.parseLong(s2));
            } catch (Exception ex) {
                return s1.compareTo(s2);
            }
        }
    }
    
    @Override
    public String toString()
    {
        return String.format("%s [%d questions; %d participants]", 
                ExamTitle, AllQuestion.size(), userToPass.size());
    }

}
