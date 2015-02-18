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
package Connector;

import ExtraClass.CurrentExam;
import ExtraClass.Question;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author Dipu
 */
public final class ProcessCommand {

    public static String result;
    public static Scanner scanner;

    public static void process(String ip, DataInputStream in, DataOutputStream out) throws IOException {
        try {
            scanner = new Scanner(in); 

            String command = scanner.next().toUpperCase();
            if (null != command) {
                switch (command) {
                    case "LOGIN":
                        LOGIN(ip);
                        break;
                    case "LOGOUT":
                        LOGOUT(ip);
                        break;
                    case "START_TIME":
                        START_TIME();
                        break;
                    case "DURATION":
                        DURATION();
                        break;
                    case "QUESTIONS":
                        QUESTIONS();
                        break;
                    case "SUBMIT":
                        SUBMIT(ip);
                        break;
                    case "EXAM_NAME":
                        EXAM_NAME();
                        break;
                    case "TOTAL_MARK":
                        TOTAL_MARK();
                        break;
                    case "DESCRIPTION":
                        DESCRIPTION(ip);
                        break;
                }
            }                        
        } catch (Exception ex) {            
            result = "-1 " + ex.getMessage();
        } finally { 
            out.writeUTF(result);        
            scanner.close();
        }
    }

    /*
     LOGIN <username> <password> 
     login to the server
     <username> : username of the user
     <password> : password of the user
     */
    public static void LOGIN(String ip) {        
        //get username and password
        String username = scanner.next();
        String password = scanner.next();

        if (!ExtraClass.CurrentExam.curExam.userToPass.containsKey(username)) {
            result = "-1 Username was not found";
            return;
        }

        String realpass = ExtraClass.CurrentExam.curExam.userToPass.get(username);
        if (!realpass.equals(password)) {
            result = "-1 Password mismatch";
            return;
        }

        ExtraClass.CurrentExam.ipToUsername.put(ip, username);
        result = "0";

        Logger.getLogger(ProcessCommand.class.getName()).info(
                String.format("{0} was connected from {0}", username, ip));
    }

    /*

     LOGOUT <username>
     logout from the server
     <username> : username of the user
     */
    public static void LOGOUT(String ip) {
        if(ipNotRegistered(ip)) return;

        String username = ExtraClass.CurrentExam.ipToUsername.get(ip);
        ExtraClass.CurrentExam.ipToUsername.remove(ip);
        result = "0";

        Logger.getLogger(ProcessCommand.class.getName()).info(
                String.format("{0} was logged out from {0}", username, ip));
    }

    /*
     START_TIME
     get the starting time of the exam
     */
    public static void START_TIME() {
        result = String.format("0 %d", ExtraClass.CurrentExam.curExam.StartTime.getTime());
    }

    /*
     DURATION
     get the ending time of the exam
     */
    public static void DURATION() {
        result = String.format("0 %d", ExtraClass.CurrentExam.curExam.Duration);
    }

    /*
     QUESTIONS
     get a list of question_id for this exam
     */
    public static void QUESTIONS() {
        result = String.format("0 %d ", ExtraClass.CurrentExam.curExam.AllQuestion.size());
        for (int key : ExtraClass.CurrentExam.curExam.AllQuestion.keySet()) {
            result += String.format("%d ", key);
        }
    }

    /*
     SUBMIT <question_id> <answer>
     submit code of a question.
     <question_id> : id of the question to submit
     <answer> : answer of the question
     @returns -> True if success False otherwise
     */
    public static void SUBMIT(String ip) {
        //check if exam is started
        if (isExamNotStarted()) {
            return;
        }
        //check if ip is registered
        if (ipNotRegistered(ip)) {
            return;
        }
        String username = ExtraClass.CurrentExam.ipToUsername.get(ip);
        //check if question exist
        int quesID = scanner.nextInt();
        if (isQuesNotExist(quesID)) {
            return;
        }

        //stored submitted answer
        String ans = "";
        while (scanner.hasNextLine()) {
            ans += scanner.nextLine() + "\n";
        }
        ExtraClass.CurrentExam.submitAnswer(username, quesID, ans);
        result = "0";
    }

    /*
     EXAM_NAME
     get the name of the exam
     */
    public static void EXAM_NAME() {
        result = "0 " + ExtraClass.CurrentExam.curExam.ExamTitle;
    }

    /*
     TOTAL_MARK
     get the total marks of the exam
     */
    public static void TOTAL_MARK() {
        result = String.format("0 %d", ExtraClass.CurrentExam.curExam.getTotalMarks());
    }

    /*
     DESCRIPTION <question_id>
     get the question description. e.g. question title, body, mark etc.
     <question_id> question id
     @returns -> [0 mark body] if valid
     */
    public static void DESCRIPTION(String ip) {
        //check if exam is started
        if (isExamNotStarted()) {
            return;
        }
        //check if ip is logged in
        if (ipNotRegistered(ip)) {
            return;
        }
        //get question
        int quesID = scanner.nextInt();
        if (isQuesNotExist(quesID)) {
            return;
        }
        //send question body
        Question ques = (Question) CurrentExam.curExam.AllQuestion.get(quesID);
        result = String.format("0 %d %s", ques.Mark, ques.Title, ques.Body);
    }

    public static boolean isExamRunning() {
        long now = System.currentTimeMillis();
        long start = ExtraClass.CurrentExam.curExam.StartTime.getTime();
        if (now >= start) {
            result = "-1 Exam is running";
        }
        return (now >= start);
    }

    public static boolean isExamNotStarted() {
        long now = System.currentTimeMillis();
        long start = ExtraClass.CurrentExam.curExam.StartTime.getTime();
        if (start > now) {
            result = "-1 Exam is not started yet";
        }
        return (start > now);
    }

    public static boolean isQuesNotExist(int quesID) {
        if (!ExtraClass.CurrentExam.curExam.AllQuestion.containsKey(quesID)) {
            result = "-1 Question id was not found";
            return true;
        }
        return false;
    }

    public static boolean ipNotRegistered(String ip) {
        if (!ExtraClass.CurrentExam.ipToUsername.containsKey(ip)) {
            result = "-1 Not registered";
            return true;
        }
        return false;
    }
}
