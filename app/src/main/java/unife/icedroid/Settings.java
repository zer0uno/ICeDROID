package unife.icedroid;

import android.content.Context;
import android.util.Log;
import unife.icedroid.core.ICeDROID;
import unife.icedroid.exceptions.WifiAdhocImpossibleToEnable;
import unife.icedroid.services.ApplevDisseminationChannelService.OnMessageReceiveListener;

public class Settings {
    private static final String TAG = "Application Settings";
    private static final boolean DEBUG = true;

    private volatile static Settings instance;

    private static ChatsManager chatsManager;

    private Settings(Context context) throws WifiAdhocImpossibleToEnable{
        chatsManager = ChatsManager.getInstance(context);
        if (ICeDROID.getInstance(context) != null) {
            SubscriptionListManager.getSubscriptionListManager(context);
        } else {
            throw new WifiAdhocImpossibleToEnable();
        }
    }

    public static Settings getSettings(Context context) {
        if (instance == null) {
            synchronized (Settings.class) {
                if (instance == null) {
                    try {
                        instance = new Settings(context);
                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error loading settings!");
                        instance = null;
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

    public static OnMessageReceiveListener getListener() {
        return chatsManager;
    }
}
