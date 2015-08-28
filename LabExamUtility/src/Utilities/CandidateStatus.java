/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.util.ArrayList;

/**
 *
 * @author Dipu
 */
public class CandidateStatus
{

    /**
     * Creates a new CandidateStatus. Default value: loginCount = 1
     *
     * @param regNo Registration number of the candidate.
     */
    public CandidateStatus(int regNo)
    {
        mRegNo = regNo;
        mLoginCount = 0;
        mTotalMarks = 0;
        mTotalSubmission = 0;
        mConnected = true;
    }

    //registration number
    private int mRegNo;
    //login attempts
    private int mLoginCount;
    //is connected now
    private boolean mConnected;
    //total marks
    private int mTotalMarks;
    //total submissions
    private int mTotalSubmission;
    //submissions
    private final ArrayList<Integer> mSubmissions = new ArrayList<>();
    //ip addresses
    private final ArrayList<String> mIpList = new ArrayList<>();

    /**
     * Gets the registration number of the candidate.
     *
     * @return Registration number of the candidate.
     */
    public int getRegistrationNo()
    {
        return mRegNo;
    }

    /**
     * Sets the registration number of the candidate.
     *
     * @param regNo Registration number of the candidate.
     */
    public void setRegistrationNo(int regNo)
    {
        mRegNo = regNo;
    }

    /**
     * Gets the number of login attempts by the candidate.
     *
     * @return Number of login attempts by the candidate.
     */
    public int getLoginCount()
    {
        return mLoginCount;
    }

    /**
     * Sets the number of login attempts by the candidate.
     *
     * @param count Number of login attempts by the candidate.
     */
    public void setLoginCount(int count)
    {
        mLoginCount = count;
    }

    /**
     * Increases the number of login attempts by the candidate by one.
     */
    public void increaseLoginCount()
    {
        mLoginCount += 1;
    }

    /**
     * Gets if the candidate is currently logged in or not.
     *
     * @return True if logged in; False otherwise.
     */
    public boolean isConnected()
    {
        return mConnected;
    }

    /**
     * Sets if the candidate is currently logged in or not.
     *
     * @param stat True if logged in; False otherwise.
     */
    public void setConnected(boolean stat)
    {
        mConnected = stat;
    }

    /**
     * Gets all of the IP used by this candidates.
     *
     * @return List of all IP addresses.
     */
    public String[] getIpList()
    {
        return mIpList.toArray(new String[mIpList.size()]);
    }

    /**
     * Checks whether this candidate's IP list has the given ip address.
     *
     * @param ip IP address to check.s
     * @return True if exist; False otherwise.
     */
    public boolean hasIp(String ip)
    {
        ip = ip.substring(0, ip.indexOf(":"));
        return mIpList.contains(ip);
    }

    /**
     * Adds a new IP address to this candidate's IP list.
     *
     * @param ip IP Address to add.
     */
    public void addIp(String ip)
    {
        ip = ip.substring(0, ip.indexOf(":"));
        if (!mIpList.contains(ip)) {
            mIpList.add(ip);
        }
    }

    /**
     * Adds a new submission to submission list.
     *
     * @param qid Question id that has been submitted.
     * @param mark Mark of the question.
     */
    public void addSubmission(int qid, int mark)
    {
        mTotalSubmission++;
        if (!mSubmissions.contains(qid)) {
            mTotalMarks += mark;
            mSubmissions.add(qid);
        }
    }

    /**
     * Gets the total marks that has been submitted.
     *
     * @return The total marks that has been submitted.
     */
    public int getTotalMarkSubmitted()
    {
        return mTotalMarks;
    }

    /**
     * Gets the total number of time user has submitted an answer.
     *
     * @return The total number of time user has submitted an answer.
     */
    public int getTotalSubmissions()
    {
        return mTotalSubmission;
    }

    /**
     * Gets the number of question that has been submitted
     *
     * @return Number of question that has been submitted
     */
    public int getQuestionSubmitted()
    {
        return mSubmissions.size();
    }
}
