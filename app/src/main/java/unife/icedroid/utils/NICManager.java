package unife.icedroid.utils;

import android.util.Log;

import java.util.ArrayList;

import unife.icedroid.exceptions.WifiAdhocImpossibleToEnable;
import unife.icedroid.exceptions.WifiAdhocImpossibleToDisable;

public class NICManager {

    public static void startWifiAdhoc() throws WifiAdhocImpossibleToEnable {
        try {
            String cmd;
            //Pull down Wifi interface
            cmd = "ip link set " + Settings.NW_IF + " down";
            Utils.rootExec(cmd);

            //Set wifi ad-hoc mode
            cmd = "iwconfig " + Settings.NW_IF + " mode ad-hoc channel " + Settings.NW_CHANNEL + " essid " + Settings.NW_ESSID;
            Utils.rootExec(cmd);

            //Pull up wifi interface
            cmd = "ip link set " + Settings.NW_IF + " up";
            Utils.rootExec(cmd);

            //Set IP address and network settings (Settings.HOST_IP sarà sostituito da una versione dinamica cos ìcome broadcast)
            cmd = "ip addr add " + Settings.getIPAddress() + Settings.NW_MASK + " broadcast " + Settings.NW_BROADCAST_ADDRESS + " dev " + Settings.NW_IF;
            Utils.rootExec(cmd);

            //Controls to check that the interface is on ad-hoc mode and on the right essid
            cmd = "iwconfig" + Settings.NW_IF;
            ArrayList<String> results = Utils.exec(cmd);
            if (! containsSubstring(results, "Mode:Ad-Hoc") || ! containsSubstring(results, "ESSID " + Settings.NW_ESSID)) {
                throw new WifiAdhocImpossibleToEnable("Impossible to enable Wifi Ad-Hoc");
            }

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("startWifiAdhoc()", (msg != null)? msg : "Impossible to enable Wifi Ad-Hoc");
            throw new WifiAdhocImpossibleToEnable("Impossible to enable Wifi Ad-Hoc");
        }
    }

    public static void stopWifiAdhoc() throws WifiAdhocImpossibleToDisable {
        try {
            String cmd;
            //Pull down Wifi interface
            cmd = "ip link set " + Settings.NW_IF + " down";
            Utils.rootExec(cmd);

            //Set wifi managed mode
            cmd = "iwconfig " + Settings.NW_IF + " mode managed";
            Utils.rootExec(cmd);

            //Pull up wifi interface
            cmd = "ip link set " + Settings.NW_IF + " up";
            Utils.rootExec(cmd);

            //Controls to check that the interface is not on ad-hoc mode
            cmd = "iwconfig" + Settings.NW_IF;
            ArrayList<String> results = Utils.exec(cmd);
            if (containsSubstring(results, "Mode:Ad-Hoc")) {
                throw new WifiAdhocImpossibleToDisable("Impossible to disable Wifi Ad-Hoc");
            }

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("stopWifiAdhoc()", (msg != null)? msg : "Impossible to disable Wifi Ad-Hoc");
            throw new WifiAdhocImpossibleToDisable("Impossible to disable Wifi Ad-Hoc");
        }
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
