package unife.icedroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RoutingService extends Service {

    @Override
    public void onCreate() {
        /**
         * TODO
         * Caricare il corretto algoritmo di routing
         */

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

    }
}
