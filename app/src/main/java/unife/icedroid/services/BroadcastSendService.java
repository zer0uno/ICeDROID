package unife.icedroid.services;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.BroadcastSendThread;

public class BroadcastSendService extends Service {
    private static final String TAG = "BroadcastSendService";

    private Thread sendThread;


    @Override
    public void onCreate() {
        /**
         * TODO
         * Trovare un modo per segnalare che è impossibile avviare il servizio di send e che quindi
         * l'applcazione va chiusa
         */
        try {
            sendThread = new Thread(new BroadcastSendThread());
            sendThread.start();
            Log.i(TAG, "BroadcastSendThread started");
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null) ? msg : "onCreate(): An error occurred");
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
        Log.i(TAG, "BroadcastSendService destroyed");
    }
}
