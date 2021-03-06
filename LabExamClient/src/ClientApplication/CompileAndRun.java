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

import Utilities.Functions;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Class for compiling and running source codes. The machine is assumed to be at
 * least Windows XP or later. This class should not have any instance.
 */
public abstract class CompileAndRun
{

    /**
     * Compiles a code and write output to a writer. Supports only Java, C++,
     * and ANSI-C.
     *
     * @param codeFile Code file to compile.
     * @param writer Writer to put outputs.
     * @return True on success; False otherwise.
     */
    public static boolean CompileCode(File codeFile, Writer writer)
    {
        try {
            //compilation command
            String commands;
            String javac = AppSettings.getJavaCompiler();
            String gpp = AppSettings.getGppCompiler();
            String gcc = AppSettings.getGccCompiler();
            String javaOptions = AppSettings.getJavaOptions();
            String gppOptions = AppSettings.getGppOptions();
            String gccOptions = AppSettings.getGccOptions();

            //generate command                      
            String dir = codeFile.getParentFile().toString();
            String name = codeFile.getAbsolutePath().toLowerCase();
            if (name.endsWith(".java")) {
                commands = String.format("\"%s\" %s \"%s\" \"%s\"",
                        javac, javaOptions, dir, codeFile.toString());
                writer.write("Compiling Java file... ");
            }
            else if (name.endsWith(".cpp")) {
                name = name.substring(0, name.length() - ".cpp".length()) + ".exe";
                commands = String.format("\"%s\" %s \"%s\" \"%s\"",
                        gpp, gppOptions, name, codeFile.toString());
                writer.write("Compiling C++ file... ");
            }
            else if (name.endsWith(".c")) {
                name = name.substring(0, name.length() - ".c".length()) + ".exe";
                commands = String.format("\"%s\" %s \"%s\" \"%s\"",
                        gcc, gccOptions, name, codeFile.toString());
                writer.write("Compiling C file... ");
            }
            else {
                writer.write("ERROR: This file can not be compiled!\n");
                return false;
            }

            //collect needed data            
            Process p = Runtime.getRuntime().exec(commands);
            p.waitFor();

            String err = Functions.readFully(p.getErrorStream(), "UTF-8");
            err = err.replace(AppSettings.getDefaultPath().toString() + "\\", "");
            if (err.length() > 0) writer.write("\n" + err + "\n");

            String inn = Functions.readFully(p.getInputStream(), "UTF-8");
            if (inn.length() > 0) writer.write("\n" + inn + "\n");

            if (p.exitValue() == 0) {
                writer.write("[Compilation OK]\n");
                return true;
            }
            else {
                writer.write("[Compilation Failed]\n");
                return false;
            }
        }
        catch (IOException | InterruptedException ex) {
            return false;
        }
    }

    /**
     * Gets the name of the class for running Java program.
     *
     * @param codeFile Original source code file path.
     * @return Chosen class name.
     * @throws InterruptedException
     */
    public static String getClassName(File codeFile)
            throws InterruptedException
    {
        ArrayList<String> names = new ArrayList<>();
        for (File f : codeFile.getParentFile().listFiles()) {
            if (f.isFile()) {
                if (f.getName().toLowerCase().endsWith(".class")) {
                    String t = f.getName();
                    t = t.substring(0, t.length() - ".class".length());
                    names.add(t);
                }
            }
        }

        if (names.isEmpty()) {
            return null;
        }
        else if (names.size() == 1) {
            return names.get(0);
        }
        else {
            ClassNameChoser mcp = new ClassNameChoser(names);
            mcp.setVisible(true);

            return mcp.getSelected();
        }
    }

    /**
     * Runs a Java, C++ or ANSI C program.
     *
     * @param codeFile Path to source code file.
     * @return True on success; False otherwise.
     */
    public static boolean RunProgram(File codeFile)
    {
        try {
            //format of the arguments         
            String command;
            String java = AppSettings.getJavaExePath();

            String name = codeFile.getAbsolutePath().toLowerCase();
            if (name.endsWith(".java")) {
                String className = getClassName(codeFile);
                if (className == null) return false;
                command = String.format("%s -classpath \"%s\" %s",
                        java, codeFile.getParent(), className);
            }
            else if (name.endsWith(".cpp")) {
                name = name.substring(0, name.length() - ".cpp".length()) + ".exe";
                command = name;
            }
            else if (name.endsWith(".c")) {
                name = name.substring(0, name.length() - ".c".length()) + ".exe";
                command = name;
            }
            else {
                return false;
            }

            //start console            
            ConsoleFrame cf = new ConsoleFrame(command);
            cf.setSize(750, 400);
            cf.setVisible(true);

            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
