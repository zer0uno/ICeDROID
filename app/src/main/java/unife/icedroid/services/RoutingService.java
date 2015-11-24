package unife.icedroid.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.routingalgorithms.SprayAndWaitThread;
import unife.icedroid.utils.Settings;

public class RoutingService extends Service {
    private static final String TAG = "RoutingService";
    private static final boolean DEBUG = true;

    public static final String EXTRA_NEW_MESSAGE = "unife.icedroid.NEW_MESSAGE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        RegularMessage message = (RegularMessage) intent.getSerializableExtra(EXTRA_NEW_MESSAGE);
        startRoutingAlgorithm(this, startID, message);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        /**
         * TODO
         * devo richiare i thread?
        */
    }

    private void startRoutingAlgorithm(Service service, int startID, RegularMessage message) {
        Thread thread = null;
        switch (Settings.getSettings().getRoutingAlgorithm()) {
            case SPRAY_AND_WAIT:
                thread = new Thread(new SprayAndWaitThread(service, startID, message));
                if (DEBUG) Log.i(TAG, "SprayAndWaitThread started");
                break;
            default:
                break;
        }
        thread.start();
    }
}
