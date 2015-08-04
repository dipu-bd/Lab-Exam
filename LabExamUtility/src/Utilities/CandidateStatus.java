/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author Dipu
 */
public class CandidateStatus {

    /**
     * Creates a new CandidateStatus. Default value: loginCount = 1
     *
     * @param regNo Registration number of the candidate.
     * @param stat True if logged in; False otherwise.
     */
    public CandidateStatus(int regNo, boolean stat) {
        mRegNo = regNo;
        mLoginCount = 1;
        mConnected = stat;
    }

    //registration number
    private int mRegNo;
    //login attempts
    private int mLoginCount;
    //is connected now
    private boolean mConnected;

    /**
     * Gets the registration number of the candidate.
     *
     * @return Registration number of the candidate.
     */
    public int getRegistrationNo() {
        return mRegNo;
    }

    /**
     * Sets the registration number of the candidate.
     *
     * @param regNo Registration number of the candidate.
     */
    public void setRegistrationNo(int regNo) {
        mRegNo = regNo;
    }

    /**
     * Gets the number of login attempts by the candidate.
     *
     * @return Number of login attempts by the candidate.
     */
    public int getLoginCount() {
        return mLoginCount;
    }

    /**
     * Sets the number of login attempts by the candidate.
     *
     * @param count Number of login attempts by the candidate.
     */
    public void setLoginCount(int count) {
        mLoginCount = count;
    }

    /**
     * Increases the number of login attempts by the candidate by one.
     */
    public void increaseLoginCount() {
        mLoginCount += 1;
    }

    /**
     * Gets if the candidate is currently logged in or not.
     *
     * @return True if logged in; False otherwise.
     */
    public boolean isConnected() {
        return mConnected;
    }

    /**
     * Sets if the candidate is currently logged in or not.
     *
     * @param stat True if logged in; False otherwise.
     */
    public void setConnected(boolean stat) {
        mConnected = stat;
    }
}
