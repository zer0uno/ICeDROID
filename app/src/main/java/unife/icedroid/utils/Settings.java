package unife.icedroid.utils;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.content.Context;
import android.content.res.Resources;
import android.content.Intent;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.widget.Toast;
import unife.icedroid.R;
import unife.icedroid.core.managers.SubscriptionListManager;
import unife.icedroid.exceptions.ImpossibleToGetIPAddress;
import unife.icedroid.services.BroadcastReceiveService;
import unife.icedroid.services.BroadcastSendService;
import unife.icedroid.services.HelloMessageService;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;

public class Settings {
    /**
     * TODO
     * Attenzione, che qualcuno possa avere una versione vecchia dei settings,
     * mai memorizzare l'oggetto ritornato, cioè mai fare Settings s = Settings.getSettings(),
     * ma sempre utilizzare solo Settings.getSettings()
    */
    private static final String TAG = "Settings";
    private static final boolean DEBUG = true;
    public static enum RoutingAlgorithm {SPRAY_AND_WAIT}

    private volatile static Settings instance;

    private String networkInterface;
    private String networkESSID;
    private String networkChannel;
    private String hostID;
    private String hostIP;
    private String networkMask;
    private String networkBroadcastAddress;
    private int receivePort;
    private int messageSize;
    private RoutingAlgorithm routingAlgorithm;

    private Context context;
    private WifiManager wifiManager;
    private WifiManager.WifiLock wifiLock;

    private Settings(Context context) throws Exception {
        this.context = context;
        Resources resources = context.getResources();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                resources.openRawResource(R.raw.settings)));
        String[] setting;
        String line;
        String settingName;
        while ((line = br.readLine()) != null) {
            setting = line.split(" ");
            if (setting.length > 0) {
                settingName = setting[0];

                switch (settingName) {
                    case "NetworkInterface":
                        networkInterface = setting[2];
                        break;
                    case "ESSID":
                        networkESSID = setting[2];
                        break;
                    case "NetworkChannel":
                        networkChannel = setting[2];
                        break;
                    case "HostID":
                        if (!setting[2].equals("null")) {
                            hostID = setting[2];
                        } else {
                            hostID = null;
                        }
                        break;
                    case "HostIP":
                        if (!setting[2].equals("null")) {
                            hostIP = setting[2];
                        } else {
                            hostIP = null;
                        }
                        break;
                    case "NetworkMask":
                        networkMask = "/" + setting[2];
                        break;
                    case "BroadcastAddress":
                        networkBroadcastAddress = setting[2];
                        break;
                    case "ReceivePort":
                        receivePort = Integer.parseInt(setting[2]);
                        break;
                    case "MsgSize":
                        messageSize = Integer.parseInt(setting[2]);
                        break;
                    case "RoutingAlgorithm":
                        if (setting[2].equals("SprayAndWait")) {
                            routingAlgorithm = RoutingAlgorithm.SPRAY_AND_WAIT;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void enableWifiAdhoc() throws Exception {
        /**************************/
        /** Enabling wifi ad-hoc **/
        /**************************/
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (hostID == null) hostID = wifiManager.getConnectionInfo().getMacAddress();
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WifiLock");
        wifiLock.acquire();
        wifiManager.setWifiEnabled(true);

        //Waiting for wifi to be ready
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            Thread.sleep(1000);
        }

        /**
         *  TODO
         *  Trovare un modo per evitare di dover aspettare che il wifi si attivi, vedere anche con
         *  attempts di 10, attenzione però al fatto "link già esistente"
        */

        NICManager.startWifiAdhoc(this);

        //UI changes must run on the UI main thread
        Handler UIhandler = new Handler(context.getMainLooper());
        UIhandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.AdHoc_enabled, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startServices() throws Exception {
        /**************************************/
        /** Setting up subscriptions Manager **/
        /**************************************/
        SubscriptionListManager.getSubscriptionListManager(context);

        /*******************************************/
        /** Starting various services for the app **/
        /*******************************************/
        Intent intent;
        //BroadcastReceiveService
        intent = new Intent(context, BroadcastReceiveService.class);
        context.startService(intent);
        if (!isServiceRunning(BroadcastReceiveService.class)) {
            throw new Exception("BroadcastReceiveServiceNotRunning");
        }
        //BroadcastSendService
        intent = new Intent(context, BroadcastSendService.class);
        context.startService(intent);
        if (!isServiceRunning(BroadcastSendService.class)) {
            throw new Exception("BroadcastSendServiceNotRunning");
        }
        //HelloMessageService
        intent = new Intent(context, HelloMessageService.class);
        context.startService(intent);
        if (!isServiceRunning(HelloMessageService.class)) {
            throw new Exception("HelloMessageServiceNotRunning");
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)
                                            context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Settings getSettings(Context context) {
        if (instance == null) {
            synchronized (Settings.class) {
                if (instance == null) {
                    try {
                        instance = new Settings(context);
                        instance.enableWifiAdhoc();
                        instance.startServices();
                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        if (DEBUG) Log.e(TAG, "Error loading settings!");
                        if (instance != null) instance.close();
                    }
                }
            }
        }
        return instance;
    }

    public static Settings getSettings() {
        synchronized (Settings.class) {
            return instance;
        }
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public String getNetworkESSID() {
        return networkESSID;
    }

    public String getNetworkChannel() {
        return networkChannel;
    }

    public String getHostID() {
        return hostID;
    }

    public String getHostIP() throws ImpossibleToGetIPAddress {
        if (hostIP == null) {
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
                    cmd = "arping -I " + networkInterface + " -D -c " + numOfPacks + " " + address;

                    results = Utils.rootExec(cmd);
                    if (results.remove(results.size() - 1).contains("Received 0 reply")) {
                        found = true;
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Impossible to get and address");
                throw new ImpossibleToGetIPAddress("Impossible to get and address");
            }

            if (DEBUG) Log.i(TAG, "Ip address set: " + address);
            hostIP = address;
        }
        return hostIP;
    }

    public String getNetworkMask() {
        return networkMask;
    }

    public String getNetworkBroadcastAddress() {
        return networkBroadcastAddress;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public RoutingAlgorithm getRoutingAlgorithm() {
        return routingAlgorithm;
    }

    public void close() {
        try {
            instance = null;

            NICManager.stopWifiAdhoc(this);

            wifiLock.release();
            wifiManager.setWifiEnabled(false);

            Intent intent;
            //BroadcastReceiveService
            intent = new Intent(context, BroadcastReceiveService.class);
            context.stopService(intent);
            //BroadcastSendService
            intent = new Intent(context, BroadcastSendService.class);
            context.stopService(intent);
            //HelloMessageService
            intent = new Intent(context, HelloMessageService.class);
            context.stopService(intent);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "close(): Closing error!");
        }
    }
}