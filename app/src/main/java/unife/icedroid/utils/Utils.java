package unife.icedroid.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import android.util.Log;
import unife.icedroid.exceptions.CommandImpossibleToRun;

public class Utils {

    /**
     * TO-DO
    */
    public static ArrayList<String> rootExec(String command) throws CommandImpossibleToRun {
        Process interactiveShell = null;
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedReader error = null;
        String su = "su";
        ArrayList<String> results = new ArrayList<String>();

        try {
            //Invoke the interactive shell
            interactiveShell = Runtime.getRuntime().exec(su);
            input = new BufferedReader(new InputStreamReader(interactiveShell.getInputStream()));
            output = new PrintWriter(interactiveShell.getOutputStream());
            error = new BufferedReader(new InputStreamReader(interactiveShell.getErrorStream()));

            //Run the command
            output.println(command);
            output.println("exit");
            output.flush();
            interactiveShell.waitFor();

            //Check for errors
            if (error.readLine() != null) {
                throw new CommandImpossibleToRun();
            }

            //Check to save some outputs
            String line = null;
            while ((line = input.readLine()) != null) {
                results.add(line);
            }

            //Close all
            input.close();
            output.close();
            interactiveShell.destroy();

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("rootExec()", (msg != null)? msg : "Impossible to run the command");
            throw new CommandImpossibleToRun("Impossible to run the command");
        }

        Log.i("rootExec()", "Command executed: " + command);
        return results;
    }
}
