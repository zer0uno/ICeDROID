package unife.icedroid.utils;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import android.util.Log;
import unife.icedroid.exceptions.ImpossibleToGetIPAddress;
import unife.icedroid.exceptions.ImpossibleToGetMacAddress;

public class Settings {
    //Network Inferface
    public static String NW_IF = "wlan0"; //chiamerà un metodo per trovare wlan0
    //Network Channel
    public static String NW_CHANNEL = "1"; //semmai permetterò all'utente di scegliere il canale
    //Network ESSID
    public static final String NW_ESSID = "ICEDROID_NETWORK";
    //Host IDentifier
    public static String HOST_ID;
    //Local IP address
    public static String HOST_IP = null;
    //Network Mask
    public static final String NW_MASK = "/16";
    //Network Broadcast Address
    public static final String NW_BROADCAST_ADDRESS = "192.168.255.255";
    //UDP Receive Port
    public static final int RECV_PORT = 49152;
    //Message size
    public static final int MSG_SIZE = 2048;


    public static String getIPAddress() throws ImpossibleToGetIPAddress {
        Random randGen = new Random(System.currentTimeMillis());
        ArrayList<String> results = null;
        int numOfPacks = 2;
        String address = null;
        int addrC;
        int addrD;
        String cmd = null;
        boolean found = false;

        try {
            while (!found) {
                addrC = randGen.nextInt(254) + 1;
                addrD = randGen.nextInt(254) + 1;
                address = "192.168." + addrC + "." + addrD;
                cmd = "arping -I " + NW_IF + " -D -c " + numOfPacks + " " + address;

                results = Utils.rootExec(cmd);
                if (results.remove(results.size() - 1).contains("Received 0 reply")) {
                    found = true;
                }
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("getIPAddress()", (msg != null)? msg : "Impossible to get and address");
            throw new ImpossibleToGetIPAddress("Impossible to get and address");
        }

        Log.i("IP address set", address);
        HOST_IP = address;
        return address;
    }

    public static void setHostId(String id) {
        HOST_ID = id;
    }

    /**
     * UNUSED
    */
    public static String getMacAddress() throws ImpossibleToGetMacAddress {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface interf; interfaces.hasMoreElements();) {
                interf = interfaces.nextElement();
                if (interf.getName().equals(NW_IF)) {
                    String mac = "";
                    for (int i = 0; i < 6; i++) {
                        mac += Integer.toHexString(interf.getHardwareAddress()[i]);
                        if (i < 5) {
                            mac += ":";
                        }
                    }
                    return mac;
                }

            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("getMacAddress", (msg != null) ? msg : "Impossible to get MAC address");
            throw new ImpossibleToGetMacAddress("Impossible to get MAC address");
        }
        return null;
    }
}