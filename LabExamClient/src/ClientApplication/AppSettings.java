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
import java.util.prefs.Preferences;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * Class to get or set various application settings. All methods are static and
 * this class can not have any instance.
 */
public abstract class AppSettings
{

    public static final int HKEY_CURRENT_USER = 0x80000001;
    public static final int HKEY_LOCAL_MACHINE = 0x80000002;
    public static final String KEYBOARD_LAYOUT_KEY
            = "SYSTEM\\CurrentControlSet\\Control\\Keyboard Layout";

    //object to store and get preferences
    private static Preferences mPreferences;

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
    private static final String KEYLAYOUT_ENABLED = "keylayout_enabled";
    private static final String KEYLAYOUT_BACKUP = "keylayout_backup";
    private static final String SCANCODE_MAP = "Scancode Map";

    public static void initialize()
    {
        try {
            mPreferences = Preferences.userRoot().node("lab_exam");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Experimental
     public static void flush()
     {
     try {
     mPreferences.flush();
     }
     catch (BackingStoreException ex) {
     Logger.getLogger(AppSettings.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     */
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
    }

    /**
     * Gets whether the key layout has been configured.
     *
     * @return True if configured; False otherwise.
     */
    public static boolean getKeyLayoutEnabled()
    {
        return mPreferences.getBoolean(KEYLAYOUT_ENABLED, true);
    }

    /**
     * Sets whether the key layout has been configured.
     *
     * @param v True if configured; False otherwise.
     */
    public static void setKeyLayoutEnabled(boolean v)
    {
        mPreferences.putBoolean(KEYLAYOUT_BACKUP, v);
    }

    /**
     * Gets the backup data of keyboard layout.
     *
     * @return Byte array representing backup data.
     */
    public static byte[] getKeyLayoutBackup()
    {
        return mPreferences.getByteArray(KEYLAYOUT_BACKUP, null);
    }

    /**
     * Sets the backup data of keyboard layout.
     *
     * @param v Byte array representing backup data.
     */
    public static void setKeyLayoutBackup(byte[] v)
    {
        if (v == null) v = new byte[0];
        mPreferences.putByteArray(KEYLAYOUT_BACKUP, v);
    }

    /**
     * Gets the scan code data of keyboard layout from windows registry.
     *
     * @return Byte array representing data.
     */
    public static byte[] getScanCodeMap()
    {
        try {
            return Advapi32Util.registryGetBinaryValue(
                    WinReg.HKEY_LOCAL_MACHINE, KEYBOARD_LAYOUT_KEY, SCANCODE_MAP);
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * Sets the scan code data of keyboard layout to windows registry.
     *
     * @param v Byte array representing data.
     */
    public static void setScanCodeMap(byte[] v)
    {
        if (v != null) {
            Advapi32Util.registrySetBinaryValue(
                    WinReg.HKEY_LOCAL_MACHINE, KEYBOARD_LAYOUT_KEY, SCANCODE_MAP, v);
        }
    }

}
