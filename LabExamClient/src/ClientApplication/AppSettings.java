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
package ClientApplication;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class to get or set various application settings. All methods are static and
 * this class can not have any instance.
 */
public abstract class AppSettings
{

    //object to store and get preferences
    private static final Preferences mPreferences 
            = Preferences.userRoot().node("lab_exam");

    // Preference keys for this package
    private static final String JAVAC_PATH = "javac_path";
    private static final String JAVA_PATH = "java_path";
    private static final String GPP_PATH = "gpp_path";
    private static final String GCC_PATH = "gcc_path";
    private static final String JAVA_OPTIONS = "java_options";
    private static final String GPP_OPTIONS = "gpp_options";
    private static final String GCC_OPTIONS = "gcc_options";
    private static final String DEFAULT_PATH = "dafault_path";
    private static final String DEFAULT_PORT = "default_port";

    public static void flush()
    {
        try {
            mPreferences.flush();
        }
        catch (BackingStoreException ex) {
            Logger.getLogger(AppSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the path where java.exe is located.
     *
     * @return String to represent the whole path.
     */
    public static String getJavaExePath()
    {
        return mPreferences.get(JAVA_PATH, "java");
    }

    /**
     * Sets the path where java.exe is located.
     *
     * @param v String to represent the whole path.
     */
    public static void setJavaExePath(String v)
    {
        mPreferences.put(JAVA_PATH, v);
        flush();
    }

    /**
     * Gets the path where javac.exe is located.
     *
     * @return String to represent the whole path.
     */
    public static String getJavaCompiler()
    {
        return mPreferences.get(JAVAC_PATH, "javac");
    }

    /**
     * Sets the path where javac.exe is located.
     *
     * @param v String to represent the whole path.
     */
    public static void setJavaCompiler(String v)
    {
        mPreferences.put(JAVAC_PATH, v);
        flush();
    }

    /**
     * Gets options to compile java programs.
     *
     * @return Options to use on compile time.
     */
    public static String getJavaOptions()
    {
        return mPreferences.get(JAVA_OPTIONS, "-g -d");
    }

    /**
     * Sets options to compile java programs.
     *
     * @param v Options to use on compile time.
     */
    public static void setJavaOptions(String v)
    {
        mPreferences.put(JAVA_OPTIONS, v);
        flush();
    }

    /**
     * Gets the path where gpp.exe is located.
     *
     * @return String to represent the whole path.
     */
    public static String getGppCompiler()
    {
        return mPreferences.get(GPP_PATH, "g++");
    }

    /**
     * Sets the path where gpp.exe is located.
     *
     * @param v String to represent the whole path.
     */
    public static void setGppCompiler(String v)
    {
        mPreferences.put(GPP_PATH, v);
        flush();
    }

    /**
     * Gets options to compile C++ programs.
     *
     * @return Options to use on compile time.
     */
    public static String getGppOptions()
    {
        return mPreferences.get(GPP_OPTIONS, "-Wall -std=c++11 -o");
    }

    /**
     * Sets options to compile C++ programs.
     *
     * @param v Options to use on compile time.
     */
    public static void setGppOptions(String v)
    {
        mPreferences.put(GPP_OPTIONS, v);
        flush();
    }

    /**
     * Gets the path where gcc.exe is located.
     *
     * @return String to represent the whole path.
     */
    public static String getGccCompiler()
    {
        return mPreferences.get(GCC_PATH, "gcc");
    }

    /**
     * Sets the path where gcc.exe is located.
     *
     * @param v String to represent the whole path.
     */
    public static void setGccCompiler(String v)
    {
        mPreferences.put(GCC_PATH, v);
        flush();
    }

    /**
     * Gets options to compile ANSI C programs.
     *
     * @return Options to use on compile time.
     */
    public static String getGccOptions()
    {
        return mPreferences.get(GCC_OPTIONS, "-Wall -ansi -o");
    }

    /**
     * Sets options to compile ANSI C programs.
     *
     * @param v Options to use on compile time.
     */
    public static void setGccOptions(String v)
    {
        mPreferences.put(GCC_OPTIONS, v);
        flush();
    }

    /**
     * Gets the default path where the user code files and folder will be
     * stored.
     *
     * @return Path containing the location. (Not necessarily valid).
     */
    public static java.nio.file.Path getDefaultPath()
    {
        String path = mPreferences.get(DEFAULT_PATH,
                System.getProperty("user.home") + File.separator + "Lab Exam");
        return (new File(path)).toPath();
    }

    /**
     * Gets the default path where the user code files and folder will be
     * stored.
     *
     * @param v Path containing the location. (Not necessarily valid).
     */
    public static void setDefaultPath(java.nio.file.Path v)
    {
        mPreferences.put(DEFAULT_PATH, v.toString());
        flush();
    }

    /**
     * Gets the default port number.
     *
     * @return Default port number.
     */
    public static int getDefaultPort()
    {
        return mPreferences.getInt(DEFAULT_PORT, 1661);
    }

    /**
     * Sets the default port number.
     *
     * @param v Default port number.
     */
    public static void setDefaultPort(int v)
    {
        mPreferences.putInt(DEFAULT_PORT, v);
        flush();
    }
}
