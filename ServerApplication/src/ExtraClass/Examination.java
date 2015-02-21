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
 *
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
    public HashMap<Integer, Question> allQuestion = new HashMap<>();
    public int LastProbID = 1;

    /**
     * Registered usernames for the exam
     */
    public ArrayList<String> userList = new ArrayList<>();

    /**
     * gets the total marks for the exam
     *
     * @return integer value with total marks
     */
    public int getTotalMarks() {
        int marks = 0;
        for (Question q : allQuestion.values()) {
            marks += q.Mark;
        }
        return marks;
    }

    /**
     * Add a new empty question to the list
     */
    public void addQuestion() {
        allQuestion.put(LastProbID, new Question(LastProbID));
        ++LastProbID;
    }

    /**
     * Delete a question by id
     *
     * @param id ID of the question to delete
     */
    public void deleteQuestion(int id) {
        if (!allQuestion.containsKey(id)) {
            return;
        }
        allQuestion.remove(id);
    }

    /**
     * Set the list to userList It will automatically ignore empty strings. Also
     * the array will be sorted
     *
     * @param list List of users to set
     */
    public void addUsers(String[] list) {
        userList.clear();
        for (String s : list) {
            String t = s.trim();
            if (t.length() > 0) {
                userList.add(t);
            }
        }
        Collections.sort(userList, new CustomComparator());
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
    public String toString() {
        return String.format("%s [%d questions; %d participants]",
                ExamTitle, allQuestion.size(), userList.size());
    }

}
