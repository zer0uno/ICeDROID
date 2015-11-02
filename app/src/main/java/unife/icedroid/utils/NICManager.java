package unife.icedroid.utils;

import android.util.Log;
import unife.icedroid.exceptions.WifiAdhocImpossibleToEnable;
import unife.icedroid.exceptions.WifiAdhocImpossibleToDisable;

public class NICManager {

    /**
     * TO-DO
    */
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

            //TO-DO aggiungere un controllo, se effettivamente l'interfaccia è in ad-hoc, per esempio con una grep

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("startWifiAdhoc()", (msg != null)? msg : "Impossible to enable Wifi Ad-Hoc");
            throw new WifiAdhocImpossibleToEnable("Impossible to enable Wifi Ad-Hoc");
        }
    }

    /**
     * TO-DO
    */
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

            //TO-DO aggiungere un controllo, se effettivamente l'interfaccia è tolta da ad-hoc, per esempio con una grep

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("startWifiAdhoc()", (msg != null)? msg : "Impossible to disable Wifi Ad-Hoc");
            throw new WifiAdhocImpossibleToDisable("Impossible to disable Wifi Ad-Hoc");
        }
    }
}
