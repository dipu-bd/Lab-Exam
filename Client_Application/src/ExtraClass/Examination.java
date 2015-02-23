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
     * Reduce LastProbID and LastUserID as minimum as possible.
     * It will replace all question id sequentially and store minimum possible value in LastProbID.
     * It will replace all candidate id sequentially and store minimum possible value in LastUserID.
     */
    public void recycleLastID()
    {
        LastProbID = 1;
        for(Question qs : allQuestion)
        {
            qs.ID = LastProbID;
            LastProbID++;
        }
        
        LastUserID = 1;
        for(Candidate cd : allCandidate)
        {
            cd.uid = LastUserID;
            LastUserID++;
        }
    }

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
     * Checks if the exam is running
     * @return True if exam is running
     */
    public boolean isRunning()
    {
        long now = System.currentTimeMillis();
        long start = StartTime.getTime();
        long stop = start + Duration * 60000;
        return start <= now && now <= stop;
    }
    
    /**
     * Checks if the exam is waiting to started ( Not yet started)
     * @return True if exam is waiting to be started
     */
    public boolean isWaiting()
    {
        long now = System.currentTimeMillis();
        long start = StartTime.getTime();
        return now < start;
    }
    
    /**
     * Checks if the exam is over (Past the duration)
     * @return True if the exam is over
     */
    public boolean isOver()
    {
        long now = System.currentTimeMillis();
        long start = StartTime.getTime();
        long stop = start + Duration * 60000;
        return now > stop;
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

    /**
     * Get the index of candidate by candidate id
     * @param uid ID of the candidate
     * @return Negative value if not found, otherwise a 0 based index of the candidate
     */
    public int getCandidateIndex(int uid)
    {
        return Collections.binarySearch(allCandidate,
                new Candidate(uid), new UserComparator());
    }

    /**
     * Checks if a candidate is in the list by their id
     * @param uid ID of the candidate
     * @return True if exist, False otherwise
     */
    public boolean candidateExist(int uid)
    {
        if (allCandidate.isEmpty()) return false;
        return (getCandidateIndex(uid) >= 0);
    }

    /**
     * Get the Candidate object by candidate id
     * @param uid ID of the candidate
     * @return A Candidate object if found, otherwise a null value.
     */
    public Candidate getCandidate(int uid)
    {
        if (allCandidate.isEmpty()) return null;
        int pos = getCandidateIndex(uid);
        if (pos >= 0) return allCandidate.get(pos);
        return null;
    }

    /**
     * Delete a candidate from the list by ID
     * @param uid ID of the candidate
     */
    public void deleteCandidate(int uid)
    {
        if (allCandidate.isEmpty()) return;
        int pos = getCandidateIndex(uid);
        if (pos >= 0) allCandidate.remove(pos);
    }
    
    /**
     * Get candidate ID by registration number.
     * @param regno Registration number of the candidate.
     * @return ID of the candidate if found, -1 otherwise.
     */
    public int getCandidateID(String regno)
    {
        for (Candidate cd : allCandidate)
            if (cd.regno.equals(regno))
                return cd.uid;
        return -1;
    }

    /**
     * Add a new empty question to the list.
     */
    public void addQuestion()
    {
        allQuestion.add(new Question(LastProbID));
        ++LastProbID;
    }

    /**
     * Get the index of the question by its ID.
     * @param qid ID of the question.
     * @return Negative value if not found, otherwise a 0 based index of the question.
     */
    public int getQuestionIndex(int qid)
    {
        return Collections.binarySearch(allQuestion,
                new Question(qid), new QuestionComparator());
    }

    /**
     * Get the Question object from question id.
     * @param qid ID of the question.
     * @return Null if not found, otherwise a Question object.
     */
    public Question getQuestion(int qid)
    {
        if (allQuestion.isEmpty()) return null;
        int pos = getQuestionIndex(qid);
        if (pos >= 0) return allQuestion.get(pos);
        return null;
    }

    /**
     * Checks if the question exists in the list by id.
     * @param qid ID of the question.
     * @return True if exists, False otherwise.
     */
    public boolean questionExist(int qid)
    {
        if (allQuestion.isEmpty()) return false;
        int pos = getQuestionIndex(qid);
        return (pos >= 0);
    }

    /**
     * Delete a question by id.
     *
     * @param qid ID of the question to delete.
     */
    public void deleteQuestion(int qid)
    {
        if (allQuestion.isEmpty()) return;
        int pos = getQuestionIndex(qid);
        if (pos >= 0) allQuestion.remove(pos);
    }
}
