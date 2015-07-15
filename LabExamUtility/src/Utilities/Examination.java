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

import java.io.File; 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class to hold all informations about an examination.
 */
public class Examination implements Serializable  {
    
    //serial version uid for serialization
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates an instance of the class
     */
    public Examination()
    {
        mExamTitle = "Lab Exam";         
        mStartTime = new Date(System.currentTimeMillis());
        mDuration = 120;
        mAllQuestion = new ArrayList<>(); 
        mAllCandidate = new ArrayList<>(); 
        mLastProbID = 0;
        mLastUserID = 0;
    }

    //overrides the default toString() method
    @Override
    public String toString()
    {
        return String.format("%s [%d questions; %d participants]",
                mExamTitle, mAllQuestion.size(), mAllCandidate.size());
    }
  
    //title of the exam
    private String mExamTitle; 
    //path to save submission data
    private File mSubmissionPath;
    //start time of the examination
    private Date mStartTime; 
    //duration of the examination
    private int mDuration;      
    //last used problem id
    private int mLastProbID;
    //last used user id
    private int mLastUserID;
    //list of all question
    private final ArrayList<Question> mAllQuestion;
    //list of all candidates
    private final ArrayList<Candidate> mAllCandidate;
        
    /**
     * Gets the path where candidates submission data is to be saved.
     * @return Path to save submission data of candidates.
     */
    public File getSubmissionPath() { return mSubmissionPath; }    
    /**
     * Sets the path where candidates submission data is to be saved.
     * @param path Path to save submission data of candidates.
     */
    public void setSubmissionPath(File path) { mSubmissionPath = path; }
    
    /**
     * Gets title of the examination.
     * @return Title of the examination.
     */
    public String getExamTitle() { return mExamTitle; }
    /**
     * Sets title of the examination.
     * @param title Title of the examination.
     */
    public void setExamTitle(String title) { mExamTitle = title; }
        
    /**
     * Gets the start time of the examination.
     * @return Start time of the examination.
     */
    public Date getStartTime() { return mStartTime; }
    /**
     * Sets the start time of the examination.
     * @param startTime Start Time of the examination.
     */
    public void setStartTime(Date startTime) { mStartTime = startTime; }
    
    /**
     * Gets the duration of the examination minutes.
     * @return Duration of the examination in minutes.
     */
    public int getDuration() { return mDuration; }
    /**
     * Set the duration of the examination in minutes.
     * @param duration Duration of the examination in minutes.
     */
    public void setDuration(int duration) { mDuration = duration; }
    
    /**
     * Get a list of all questions of this examination.
     * @return List of Question objects for the examination.
     */
    public ArrayList<Question> getAllQuestion() { return mAllQuestion; }
    /**
     * Gets the number of questions in this examination.
     * @return Number of questions in this examination.
     */
    public int getQuestionCount() { return mAllQuestion.size(); }
    
    /**
     * Gets a list of candidates in this examination.
     * @return List of candidates in this examination.
     */
    public ArrayList<Candidate> getAllCandidate() { return mAllCandidate; }
    /**
     * Gets the number of candidates in this examination.
     * @return Number of candidate in this examination.
     */
    public int getCandidateCount() { return mAllCandidate.size(); }
    
    /**
     * Gets the total marks of the exam
     * @return integer value with total marks
     */
    public int getTotalMarks()
    {
        int marks = 0;
        for (Question q : mAllQuestion)
        {
            marks += q.getMark();
        }
        return marks;
    }
    
    /**
     * Gets the ending time of the examination.
     * @return Date object representing end time of the exam.
     */
    public Date getStopTime()
    {
        long start = mStartTime.getTime();
        long stop = start + mDuration * 60000;
        return new Date(stop);
    }
    
    /**
     * Checks if the exam is running now.
     * @return True if exam is running.
     */
    public boolean isRunning()
    {
        long now = System.currentTimeMillis();
        long start = mStartTime.getTime();
        long stop = start + mDuration * 60000;
        return start <= now && now <= stop;
    }        
    /**
     * Checks if the exam is waiting to be started ( Not yet started)
     * @return True if exam is waiting to be started.
     */
    public boolean isWaiting()
    {
        long now = System.currentTimeMillis();
        long start = mStartTime.getTime();
        return now < start;
    }
    /**
     * Checks if the exam is over (Past the duration)
     * @return True if the exam is over
     */
    public boolean isOver()
    {
        long now = System.currentTimeMillis();
        long start = mStartTime.getTime();
        long stop = start + mDuration * 60000;
        return now > stop;
    }
    
    /**
     * Reduce LastProbID and LastUserID as minimum as possible.
     * It will replace all question id sequentially and store minimum possible value in LastProbID.
     * It will replace all candidate id sequentially and store minimum possible value in LastUserID.
     */
    public void recycleLastIDs()
    {        
        mLastProbID = 0;
        for(Question qs : mAllQuestion)
        {
            qs.setId(++mLastProbID);
        }
        
        mLastUserID = 0;
        for(Candidate cd : mAllCandidate)
        {
            cd.setId(++mLastUserID);
        }
    }
    
    
    
    /**
     * Adds a new candidate
     *
     * @param name Name of the candidate.
     * @param reg Registration Number.
     * @return Added Candidate object.
     */
    public Candidate addCandidate(String name, String reg)
    {
        Candidate cd = new Candidate(++mLastUserID);
        cd.setName(name);
        cd.setRegNo(reg);
        mAllCandidate.add(cd); 
        return cd;
    }

    /**
     * Get the index of candidate by candidate id
     * @param id ID of the candidate
     * @return Negative value if not found, otherwise a 0 based index of the candidate
     */
    public int getCandidateIndex(int id)
    {
        return Collections.binarySearch(mAllCandidate,
                new Candidate(id), new UserComparator());
    }

    /**
     * Checks if a candidate is in the list by their id
     * @param id ID of the candidate
     * @return True if exist, False otherwise
     */
    public boolean isCandidateExist(int id)
    {
        return (!mAllCandidate.isEmpty()) && 
                (getCandidateIndex(id) >= 0);
    }

    /**
     * Get the Candidate object by candidate id
     * @param id ID of the candidate
     * @return A Candidate object if found, otherwise a null value.
     */
    public Candidate getCandidate(int id)
    {
        if (mAllCandidate.isEmpty()) return null;
        int pos = getCandidateIndex(id);
        if (pos < 0) return null;
        return mAllCandidate.get(pos);        
    }

    /**
     * Delete a candidate from the list by ID
     * @param uid ID of the candidate
     * @return True of success; False otherwise.
     */
    public boolean deleteCandidate(int uid)
    {
        if (mAllCandidate.isEmpty()) return false;
        int pos = getCandidateIndex(uid);
        if (pos < 0) return false;
        mAllCandidate.remove(pos);
        return true;
    }
    
    /**
     * Get candidate ID by registration number.
     * @param regno Registration number of the candidate.
     * @return ID of the candidate if found, -1 otherwise.
     */
    public int getCandidateID(String regno)
    {
        for (Candidate cd : mAllCandidate)
        {
            if (cd.getRegNo().equals(regno))
                return cd.getId();
        }
        return -1;
    }
    
    public Object[] getQuestionList()
    {
        return mAllQuestion.toArray();
    }
    
    /**
     * Add a new empty question to the list.
     */
    public void addQuestion()
    {
        mAllQuestion.add(new Question(++mLastProbID));
    }

    
    
    
    /**
     * Get the index of the question by its ID.
     * @param qid ID of the question.
     * @return Negative value if not found, otherwise a 0 based index of the question.
     */
    public int getQuestionIndex(int qid)
    {
        return Collections.binarySearch(mAllQuestion,
                new Question(qid), new QuestionComparator());
    }

    /**
     * Get the Question object from question id.
     * @param qid ID of the question.
     * @return Null if not found, otherwise a Question object.
     */
    public Question getQuestion(int qid)
    {
        if (mAllQuestion.isEmpty()) return null;
        int pos = getQuestionIndex(qid);
        if (pos >= 0) return mAllQuestion.get(pos);
        return null;
    }

    /**
     * Checks if the question exists in the list by id.
     * @param qid ID of the question.
     * @return True if exists, False otherwise.
     */
    public boolean questionExist(int qid)
    {
        if (mAllQuestion.isEmpty()) return false;
        int pos = getQuestionIndex(qid);
        return (pos >= 0);
    }

    /**
     * Delete a question by id.
     * @param qid ID of the question to delete.
     * @return True on success; False otherwise.
     */
    public boolean deleteQuestion(int qid)
    {
        if (mAllQuestion.isEmpty()) return false;
        int pos = getQuestionIndex(qid);
        if (pos < 0) return false;
        mAllQuestion.remove(pos);
        return true;
    }
}
