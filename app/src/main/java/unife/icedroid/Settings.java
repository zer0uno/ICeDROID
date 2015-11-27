package unife.icedroid;

import android.content.Context;
import android.util.Log;
import unife.icedroid.core.ICeDROID;
import unife.icedroid.services.ApplevDisseminationChannelService.OnMessageReceiveListener;

public class Settings {
    private static final String TAG = "Application Settings";
    private static final boolean DEBUG = true;

    private volatile static Settings instance;

    private ChatsManager chatsManager;

    private Settings(Context context) {
        ICeDROID.getInstance(context);
        SubscriptionListManager.getSubscriptionListManager(context);
        chatsManager = ChatsManager.getInstance(context);
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

    public OnMessageReceiveListener getListener() {
        return chatsManager;
    }
}
