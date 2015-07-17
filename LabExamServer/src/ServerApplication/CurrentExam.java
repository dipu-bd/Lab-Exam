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
package ServerApplication;

import Utilities.AnswerData;
import Utilities.Candidate;
import Utilities.Examination;
import Utilities.UserChangeEvent;
import Utilities.UserChangedHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Currently opened exam session
 *
 * @author Dipu
 */
public class CurrentExam
{
    CurrentExam()
    {
        mClients = new HashSet<>();
        mCurExam = new Examination();
        userChangeListener = new ArrayList<>();
    }

    //file where the exam data is stored
    private File mExamPath;
    // Exam modifying currently.
    private Examination mCurExam;
    // List of logged in candidates.
    private final HashSet<Integer> mClients;
    // To store connected method to this handler
    private final ArrayList<UserChangedHandler> userChangeListener;

    /**
     * Gets the file where this object is stored.
     *
     * @return File in which this object is stored.
     */
    public File getExamPath()
    {
        return mExamPath;
    }

    /**
     * Sets the file where this object is stored.
     *
     * @param path File in which this object is stored.
     */
    public void setExamPath(File path)
    {
        mExamPath = path;
    }

    /**
     * Gets the currently opened examination object.
     *
     * @return Examination object.
     */
    public Examination getExamination()
    {
        return mCurExam;
    }

    /**
     * Sets the current examination object
     *
     * @param exam Examination object.
     */
    public void setExamination(Examination exam)
    {
        mCurExam = exam;
    }

    /**
     * Get id of the logged in candidates.
     *
     * @return HasSet of the connected candidates.
     */
    public HashSet<Integer> getAllClients()
    {
        return mClients;
    }

    /**
     * Checks if a candidate is logged in or not
     * @param candidateId Candidate id to check.
     * @return True if logged in; False otherwise.
     */
    public boolean isLoggedIn(int candidateId)
    {
        return mClients != null && mClients.contains(candidateId);
    }

    /**
     * Reads a file and extract Examination class information stored in it.
     *
     * @param file File object to open.
     * @throws FileNotFoundException When specific file is not found.
     * @throws IOException If any error occur while reading the file.
     * @throws ClassNotFoundException If specific file does not have desired
     * format.
     */
    public void LoadFromFile(File file)
            throws FileNotFoundException, IOException, ClassNotFoundException
    {
        //a little clean up
        mClients.clear();
        //load data from file
        try (FileInputStream fin = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            mCurExam = (Examination) ois.readObject();
            mCurExam.recycleLastIDs();
            mExamPath = file;
        }
    }

    /**
     * Writes currently opened examination data to file.
     *
     * @throws FileNotFoundException When file/directory is not found
     * @throws IOException When writing to file is failed due to some reason
     */
    public void SaveToFile()
            throws FileNotFoundException, IOException
    {
        try (FileOutputStream fos = new FileOutputStream(mExamPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(mCurExam);
            oos.flush();
        }
    }

    /**
     * Gets a list of candidates as comma separated values in this format: Name,
     * RegNo, Password.
     *
     * @return Candidate list in CSV format.
     */
    public String getCandidateCSV()
    {
        String output = "";
        for (Candidate c : mCurExam.getAllCandidate()) {
            output += String.format("%s,%s,%s\n",
                    c.getName(), c.getRegNo(), c.getPassword());
        }
        return output;
    }

    /**
     * Sets the candidates list from comma separated values in this format:
     * Name, RegNo.
     *
     * @param input CSV in this format: Name, RegNo.
     */
    public void setCandidateCSV(String input)
    {
        mCurExam.getAllCandidate().clear();
        for (String line : input.split("\n")) {
            if (line.trim().isEmpty())
                continue;
            String[] val = line.split(",");
            Candidate c = mCurExam.addCandidate(val[0].trim(), val[1].trim());
            if (val.length == 3)
                c.setPassword(val[2].trim());
        }
    }

    /**
     * Assign a Socket to specific user.
     *
     * @param regNo Registration number of the candidate.
     * @param pass Password of the user
     * @param ip IP Address of client.
     * @return True on success.
     */
    public boolean assignUser(String regNo, String pass, String ip)
    {
        //check if candidate exist
        int uid = mCurExam.getCandidateID(regNo);
        if (!mCurExam.isCandidateExist(uid))
            return false;

        //check the candidates password
        Candidate candy = mCurExam.getCandidate(uid);
        if (!candy.getPassword().equals(pass)) {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("%s[%s] tried to login with wrong password. (attempted password: %s)",
                            candy.getName(), candy.getRegNo(), pass));
            return false;
        }

        //add to connected client list and raise user change event
        mClients.add(uid);
        invokeUserChanged(new UserChangeEvent(uid, true));

        Logger.getLogger("LabExam").log(Level.INFO,
                String.format("%s[%s] connected via %s",
                        candy.getName(), candy.getRegNo(), ip));

        return true;
    }

    /**
     * Delete an user from the connected list.
     *
     * @param regNo Registration number of the candidate.
     * @param ip IP Address of the client.
     * @return True if success, False otherwise.
     */
    public boolean removeUser(String regNo, String ip)
    {
        //check if candidate exist
        int uid = mCurExam.getCandidateID(regNo);
        if (!mCurExam.isCandidateExist(uid))
            return false;

        //checks if the candidate really was connected
        String name = mCurExam.getCandidate(uid).getName();
        if (!mClients.contains(uid)) {
            Logger.getLogger("LabExam").log(Level.WARNING,
                    String.format("Something is wrong!! %s[%s] tried to log out but failed!",
                            name, regNo));
            return false;
        }

        // remove user from list and raise user changed event
        mClients.remove(uid);
        invokeUserChanged(new UserChangeEvent(uid, true));

        Logger.getLogger("LabExam").log(Level.WARNING,
                String.format("%s[%s] logged out from %s.",
                        name, regNo, ip));

        return true;
    }

    /**
     * Gets the path to save submission data of the candidate
     *
     * @param regno Registration number of the candidate.
     * @return Path to save candidate's submissions.
     */
    public File getSubmissionPath(String regno)
    {
        Path par = mCurExam.getSubmissionPath().toPath();
        par = par.resolve(regno);
        return par.toFile();
    }

    /**
     * Gets the path to save submission of a question.
     *
     * @param regno Registration number of the candidate.
     * @param qid ID of the question involved.
     * @return Path to save submission to the question.
     */
    public File getSubmissionPath(String regno, int qid)
    {
        Path par = mCurExam.getSubmissionPath().toPath();
        par = par.resolve(regno);
        par = par.resolve("Question_" + qid);
        return par.toFile();
    }

    /**
     * Receive and save an answer of a question.
     *
     * @param regNo Registration number of the candidate who submitted the
     * answer.
     * @param quesId Id of the question.
     * @param answers Answer data of the submission.
     * @return True on success False otherwise.
     */
    public boolean receiveAnswer(String regNo, int quesId, Object[] answers)
    {
        // do not take submission if exam is not running
        if (!mCurExam.isRunning()) {
            return false;
        }

        //convert registration number to id
        int uid = mCurExam.getCandidateID(regNo);
        if (!mCurExam.isCandidateExist(uid))
            return false;
        String name = mCurExam.getCandidate(uid).getName();

        //get path to save answer
        Path par = this.getSubmissionPath(regNo).toPath();

        int success = 0;
        for (Object data : answers) {            
            AnswerData ans = AnswerData.class.cast(data);
            try {
                //save submitted data 
                File f = par.resolve(ans.getRelativeFilePath()).toFile();
                f.getParentFile().mkdirs();

                try ( //save answer data
                        FileOutputStream fos = new FileOutputStream(f)) {
                    fos.write(ans.getFileData());
                }

                success++;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger("LabExam").log(Level.SEVERE,
                        String.format("Failed to save %s(%s)'s answer %s for Question %02d.",
                                name, regNo, ans.getRelativeFilePath(), quesId));
            }
        }

        //tell listeners that an answer is submitted.
        if (success > 0) {
            invokeUserSubmitted(new UserChangeEvent(uid, quesId));
            Logger.getLogger("LabExam").log(Level.INFO,
                    String.format("%s(%s) submitted for Question %02d. (%d out of %d data saved)",
                            name, regNo, quesId, success, answers.length));
        }

        return (success == answers.length);
    }

    /**
     * Adds a new event handler to monitor user changed events.
     *
     * @param handler Event handler to add.
     * @return True if successfully added; False otherwise.
     */
    public boolean addUserChangedHandler(UserChangedHandler handler)
    {
        return userChangeListener.add(handler);
    }

    /**
     * Removes an event handler from monitoring user changed events.
     *
     * @param handler Event handler to remove.
     * @return True if successfully removed; False otherwise.
     */
    public boolean removeUserChangedHandler(UserChangedHandler handler)
    {
        return userChangeListener.remove(handler);
    }

    /**
     * Raise all event handler that is currently monitoring user changed events.
     *
     * @param uce User Change Event to invoke.
     */
    public void invokeUserChanged(UserChangeEvent uce)
    {
        // Notify everybody who are connected
        for (UserChangedHandler handler : userChangeListener) {
            handler.userChanged(uce);
        }
    }

    /**
     * Raise all event handler that is currently monitoring user changed events.
     *
     * @param uce User Change Event to invoke.
     */
    public void invokeUserSubmitted(UserChangeEvent uce)
    {
        // Notify everybody who are connected
        for (UserChangedHandler handler : userChangeListener) {
            handler.userSubmitted(uce);
        }
    }
}
