package unife.icedroid.services;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.BroadcastSendThread;
import unife.icedroid.utils.Settings;

public class BroadcastSendService extends Service {
    private static final String TAG = "BroadcastSendService";
    private static final boolean DEBUG = true;

    private Thread sendThread;


    @Override
    public void onCreate() {
        try {
            sendThread = new Thread(new BroadcastSendThread(Settings.getSettings()));
            sendThread.start();
            if (DEBUG) Log.i(TAG, "BroadcastSendThread started");
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "onCreate(): An error occurred");
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        sendThread.interrupt();
        if (DEBUG) Log.i(TAG, "BroadcastSendService destroyed");
    }
}
