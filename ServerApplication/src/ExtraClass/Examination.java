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

/**
 * Examination session object
 *
 * @author Dipu
 */
public class Examination implements Serializable {

    /**
     * Constructor of examination
     */
    public Examination()
    {
        ExamTitle = "Lab Exam";
        StartTime = new Date(System.currentTimeMillis());
        Duration = 120;
        LastProbID = 1;
        ExamPath = new File(ExamTitle);
    }

    @Override
    public String toString()
    {
        return String.format("%s [%d questions; %d participants]",
                ExamTitle, allQuestion.size(), allCandidate.size());
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
    public ArrayList<Question> allQuestion = new ArrayList<>();
    public int LastProbID = 1;

    /**
     * Registered usernames for the exam
     */
    public ArrayList<Candidate> allCandidate = new ArrayList<>(); 
    public int LastUserID = 1;

    /**
     * gets the total marks for the exam
     *
     * @return integer value with total marks
     */
    public int getTotalMarks()
    {
        int marks = 0;
        for (Question q : allQuestion)
        {
            marks += q.Mark;
        }
        return marks;
    }

    /**
     * Add new candidate
     *
     * @param name Name of the candidate
     * @param reg Registration Number
     */
    public void addCandidate(String name, String reg)
    {
        Candidate em = new Candidate(LastUserID);
        em.name = name;
        em.regno = reg;
        allCandidate.add(em); 
        ++LastUserID;
    }

    public int getCandidateIndex(int uid)
    {
        return Collections.binarySearch(allCandidate,
                new Candidate(uid), new UserComparator());
    }

    public boolean candidateExist(int uid)
    {
        if (allCandidate.isEmpty()) return false;
        return (getCandidateIndex(uid) >= 0);
    }

    public Candidate getCandidate(int uid)
    {
        if (allCandidate.isEmpty()) return null;
        int pos = getCandidateIndex(uid);
        if (pos >= 0) return allCandidate.get(pos);
        return null;
    }

    public void deleteCandidate(int uid)
    {
        if (allCandidate.isEmpty()) return;
        int pos = getCandidateIndex(uid);
        if (pos >= 0) allCandidate.remove(pos);
    }

    public int getCandidateID(String user)
    {
        for (Candidate cd : allCandidate)
            if (cd.regno.equals(user))
                return cd.uid;
        return -1;
    }

    /**
     * Add a new empty question to the list
     */
    public void addQuestion()
    {
        allQuestion.add(new Question(LastProbID));
        ++LastProbID;
    }

    public int getQuestionIndex(int qid)
    {
        return Collections.binarySearch(allQuestion,
                new Question(qid), new QuestionComparator());
    }

    public Question getQuestion(int qid)
    {
        if (allQuestion.isEmpty()) return null;
        int pos = getQuestionIndex(qid);
        if (pos >= 0) return allQuestion.get(pos);
        return null;
    }

    public boolean questionExist(int qid)
    {
        if (allQuestion.isEmpty()) return false;
        int pos = getQuestionIndex(qid);
        return (pos >= 0);
    }

    /**
     * Delete a question by id
     *
     * @param qid ID of the question to delete
     */
    public void deleteQuestion(int qid)
    {
        if (allQuestion.isEmpty()) return;
        int pos = getQuestionIndex(qid);
        if (pos >= 0) allQuestion.remove(pos);
    }

    /**
     * Comparator for getNames() method
     */
    private class QuestionComparator implements Comparator<Question> {

        @Override
        public int compare(Question q1, Question q2)
        {
            return q1.ID - q2.ID;
        }
    }

    /**
     * Comparator for getNames() method
     */
    private class UserComparator implements Comparator<Candidate> {

        @Override
        public int compare(Candidate u1, Candidate u2)
        {
            return u1.uid - u2.uid;
        }
    }
}
