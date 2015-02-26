/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientApplication;

import UtilityClass.Functions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

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
            final String dir = codeFile.getParentFile().toString() + File.separator;

            //collect needed data            
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager
                    = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> compilationUnits1
                    = fileManager.getJavaFileObjects(codeFile);

            //compile
            JavaCompiler.CompilationTask task = compiler.getTask(writer,
                    fileManager, null, null, null, compilationUnits1);

            boolean res = task.call();
            fileManager.close();

            return res;
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
