package unife.icedroid.utils;

import android.util.Log;
import unife.icedroid.exceptions.WifiAdhocImpossibleToEnable;
import unife.icedroid.exceptions.WifiAdhocImpossibleToDisable;
import java.util.ArrayList;

public class NICManager {
    private final static String TAG = "NICManager";
    private final static boolean DEBUG = true;

    public static void startWifiAdhoc(Settings s) throws WifiAdhocImpossibleToEnable {
        try {
            //Check if the interface is already in adhoc state
            if (!checkInterfaceStatus(s, "Mode:Ad-Hoc", "ESSID:\"" + s.getNetworkESSID() + "\"")) {
                String cmd;
                //Pull down Wifi interface
                cmd = "iplink set " + s.getNetworkInterface() + " down";
                Utils.rootExec(cmd);

                //Set wifi ad-hoc mode
                cmd = "iwconfig " + s.getNetworkInterface() + " mode ad-hoc channel " +
                        s.getNetworkChannel() + " essid " + s.getNetworkESSID();
                Utils.rootExec(cmd);

                //Pull up wifi interface
                cmd = "iplink set " + s.getNetworkInterface() + " up";
                Utils.rootExec(cmd);

                //Set IP address and network settings
                s.getHostIP();

                //Controls to check that the interface is on ad-hoc mode and on the right essid
                if (!checkInterfaceStatus(s, "Mode:Ad-Hoc",
                        "ESSID:\"" + s.getNetworkESSID() + "\"")) {
                    throw new WifiAdhocImpossibleToEnable("Impossible to enable Wifi Ad-Hoc");
                }
            }
        } catch(Exception ex){
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg :
                                            "startWifiAdhoc(): Impossible to enable Wifi Ad-Hoc");
            throw new WifiAdhocImpossibleToEnable("Impossible to enable Wifi Ad-Hoc");
        }
    }

    public static void stopWifiAdhoc(Settings s) throws WifiAdhocImpossibleToDisable {
        try {
            String cmd;
            //Pull down Wifi interface
            cmd = "iplink set " + s.getNetworkInterface() + " down";
            Utils.rootExec(cmd);

            //Set wifi managed mode
            cmd = "iwconfig " + s.getNetworkInterface() + " mode managed";
            Utils.rootExec(cmd);

            //Pull up wifi interface
            cmd = "iplink set " + s.getNetworkInterface() + " up";
            Utils.rootExec(cmd);

            //Controls to check that the interface is not on ad-hoc mode
            if (!checkInterfaceStatus(s, "Mode:Managed")) {
                throw new WifiAdhocImpossibleToDisable("Impossible to disable Wifi Ad-Hoc");
            }

        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) {
                Log.e(TAG, (msg != null)? msg :
                        "stopWifiAdhoc(): Impossible to disable Wifi Ad-Hoc");
            }
            throw new WifiAdhocImpossibleToDisable("Impossible to disable Wifi Ad-Hoc");
        }
    }

    private static boolean checkInterfaceStatus(Settings s, String... fields) {
        String cmd;
        cmd = "iwconfig " + s.getNetworkInterface();
        try {
            ArrayList<String> results = Utils.exec(cmd);
            for (String f : fields) {
                if (!containsSubstring(results, f)) {
                    return false;
                }
            }
        } catch (Exception ex) {}
        return true;
    }

    private static boolean containsSubstring (ArrayList<String> results, String substring) {
        for (String line : results) {
            if (line.contains(substring)) {
                return true;
            }
        }
        return false;
    }
}
