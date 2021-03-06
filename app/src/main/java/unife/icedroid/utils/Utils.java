package unife.icedroid.utils;

import android.util.Log;
import unife.icedroid.exceptions.CommandImpossibleToRun;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Utils {
    private final static String TAG = "Utils";
    private final static boolean DEBUG = true;

    public static ArrayList<String> rootExec(String command) throws CommandImpossibleToRun {
        Process interactiveShell = null;
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedReader error = null;
        String su = "su";
        ArrayList<String> results = new ArrayList<>();

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
            if (DEBUG) Log.e(TAG, (msg != null)? msg :
                    "rootExec() - impossible to run the command: " + command);
            throw new CommandImpossibleToRun("Impossible to run the command");
        }

        if (DEBUG) Log.i(TAG, "rootExec() - Command executed: " + command);
        return results;
    }

    public static ArrayList<String> exec(String command) throws CommandImpossibleToRun {
        Process process = null;
        BufferedReader input = null;
        BufferedReader error = null;
        ArrayList<String> results = new ArrayList<>();

        try {
            //Run the command
            process = Runtime.getRuntime().exec(command);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            //Check for errors
            if (error.readLine() != null) {
                throw new CommandImpossibleToRun();
            }

            //Check to save some outputs
            String line = null;
            while ((line = input.readLine()) != null) {
                results.add(line);
            }

        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null)? msg :
                    "exec() - Impossible to run the command: " + command);
            throw new CommandImpossibleToRun("Impossible to run the command");
        }

        if (DEBUG) Log.i(TAG, "exec() - Command executed: " + command);
        return results;
    }
}
