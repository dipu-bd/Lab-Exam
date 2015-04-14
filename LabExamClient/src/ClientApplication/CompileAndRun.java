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

/**
 *
 * @author Dipu
 */
public final class CompileAndRun
{

    public static boolean CompileCode(File codeFile, Writer writer)
    {
        try {
            //file to compile             
            String dir = codeFile.getParentFile().toString();
            String commands = String.format("javac -g -d \"%s\" \"%s\"", dir, codeFile.toString());

            //collect needed data            
            Process p = Runtime.getRuntime().exec(commands);
            p.waitFor();

            String err = Functions.readFully(p.getErrorStream(), "UTF-8");
            if (err.length() > 0)
                writer.write(err + "\n");

            String inn = Functions.readFully(p.getInputStream(), "UTF-8");
            if (inn.length() > 0)
                writer.write(inn + "\n");

            return (p.exitValue() == 0);
        }
        catch (IOException | InterruptedException ex) {
            return false;
        }
    }

    public static boolean RunProgram(File codePath)
    {
        //format of the arguments 
        String format = "java -classpath \"%s\" Main";
        final String command = String.format(format, codePath.toString());

        //start console            
        ConsoleFrame cf = new ConsoleFrame(command);
        cf.setSize(750, 400);
        cf.setVisible(true);

        return true;
    }
}
