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
 *
 * @author Dipu
 */
public final class CompileAndRun
{

    public static boolean CompileCode(File codeFile, Writer writer)
    {
        try {
            //compilation command
            String commands = null;

            //generate command                      
            String dir = codeFile.getParentFile().toString();
            String name = codeFile.getAbsolutePath().toLowerCase();
            if (name.endsWith(".java")) {
                commands = String.format("javac -g -d \"%s\" \"%s\"", dir, codeFile.toString());
                writer.write("Compiling Java file... ");
            }
            else if (name.endsWith(".cpp")) {
                name = name.substring(0, name.length() - ".cpp".length()) + ".exe";
                commands = String.format("g++ -Wall -std=c++11 -o \"%s\" \"%s\"", name, codeFile.toString());
                writer.write("Compiling C++ file... ");
            }
            else if (name.endsWith(".c")) {
                name = name.substring(0, name.length() - ".c".length()) + ".exe";
                commands = String.format("gcc -Wall -ansi -o \"%s\" \"%s\"", name, codeFile.toString());
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
            err = err.replace(Program.defaultPath.toString() + "\\", "");
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
            ex.printStackTrace();
            return false;
        }
    }

    public static String getClassName(File codeFile) throws InterruptedException
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

        if (names.size() == 0) {
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

    public static boolean RunProgram(File codeFile)
    {
        try {
            //format of the arguments         
            String command = null;
            String name = codeFile.getAbsolutePath().toLowerCase();
            if (name.endsWith(".java")) {
                String className = getClassName(codeFile);
                if (className == null) return false;
                command = String.format("java -classpath \"%s\" %s", codeFile.getParent(), className);
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
            ex.printStackTrace();
            return false;
        }
    }
}
