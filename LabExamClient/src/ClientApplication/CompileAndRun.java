/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientApplication;

import UtilityClass.Functions;
import java.io.File;
import java.io.Writer;

/**
 *
 * @author Dipu
 */
public final class CompileAndRun {

    public static boolean CompileCode(File codeFile, Writer writer)
    {
        try
        {
            //file to compile             
            String dir = codeFile.getParentFile().toString();
            String commands = String.format("javac -g -d \"%s\" \"%s\"", dir, codeFile.toString());

            //collect needed data            
            Process p = Runtime.getRuntime().exec(commands);
            p.waitFor();

            String err = Functions.readFully(p.getErrorStream(), "UTF-8");
            if (err.length() > 0) writer.write(err + "\n");

            String inn = Functions.readFully(p.getInputStream(), "UTF-8");
            if (inn.length() > 0) writer.write(inn + "\n");

            return (p.exitValue() == 0);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static boolean RunProgram(File codePath)
    {
        try
        {
            //format of the arguments 
            String format = "java -classpath \"%s\" Main";
            final String command = String.format(format, codePath.toString());

            //start console            
            ConsoleFrame cf = new ConsoleFrame(command);
            cf.setVisible(true);

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
