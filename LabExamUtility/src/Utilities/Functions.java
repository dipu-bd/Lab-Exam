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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;

/**
 * Useful multipurpose methods
 *
 * @author Dipu
 */
public abstract class Functions {

    /**
     * Set this frame into full focus and stop all key passing
     *
     * @param frame Frame to set full focus.
     */
    public static void setToFullFocus(JFrame frame) {
        frame.toFront();
        frame.requestFocus();
        frame.requestFocusInWindow();
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Recursively delete all files and folders from a directory
     *
     * @param file Path to delete
     * @return True in success, False otherwise.
     */
    public static boolean deleteDirectory(java.io.File file) {
        boolean res = true;
        if (file.isDirectory()) {
            for (File d : file.listFiles()) {
                res = deleteDirectory(d) & res;
            }
        }
        res = file.delete() & res;
        return res;
    }

    /**
     * Reads all texts from a stream using a specific encoding.
     *
     * @param inputStream inputStream to read from.
     * @param encoding Encoding used to read input stream. (Default is UTF-8).
     * @return String read from the given input stream.
     * @throws java.io.IOException Input output exception.
     */
    public static String readFully(java.io.InputStream inputStream, String encoding) throws java.io.IOException {
        if (encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        return new String(readFully(inputStream), encoding);
    }

    /**
     * Reads all bytes from a stream using a specific encoding.
     *
     * @param inputStream inputStream to read from.
     * @return byte array read from the given input stream.
     * @throws java.io.IOException Input output exception.
     */
    public static byte[] readFully(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        int buffer;
        while ((buffer = inputStream.read()) != -1) {
            baos.write(buffer);
        }
        return baos.toByteArray();
    }
 
    /**
     * Custom format a date variable using "EEE dd-MMM, yyyy hh:mm aa"
     *
     * @param date Date variable to format
     * @return Formatted string
     */
    public static String formatTime(Date date) {
        return (new SimpleDateFormat("EEE dd-MMM, yyyy hh:mm aa")).format(date);
    }

    /**
     * Format a span of time. It will take some time interval and return
     * day/hour/minute in that interval.
     *
     * @param time long time interval
     * @return Formatted time interval
     */
    public static String formatTimeSpan(long time) {
        if (time <= 0) {
            return "0 second";
        }

        //day, hour | min | sec
        long sec = time / 1000;
        long min = sec / 60;
        long hour = min / 60;
        long day = hour / 24;
        sec -= min * 60;
        min -= hour * 60;
        hour -= day * 24;

        String out = "";
        if (day > 0) {
            out += String.format("%d day", day);
            if (day > 1) {
                out += "s";
            }
        }
        if (hour > 0) {
            if (out.length() > 0) {
                out += " ";
            }
            out += String.format("%d hour", hour);
            if (hour > 1) {
                out += "s";
            }
        }
        if (day == 0) {
            if (min > 0) {
                if (out.length() > 0) {
                    out += " ";
                }
                out += String.format("%d minute", min);
                if (min > 1) {
                    out += "s";
                }
            }
            if (hour == 0 && sec > 0) {
                if (out.length() > 0) {
                    out += " ";
                }
                out += String.format("%d second", sec);
                if (sec > 1) {
                    out += "s";
                }
            }
        }

        return out;
    }

}
