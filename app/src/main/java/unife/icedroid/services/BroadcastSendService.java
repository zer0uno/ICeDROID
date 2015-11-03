package unife.icedroid.services;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import unife.icedroid.core.BroadcastSendThread;

public class BroadcastSendService extends Service {

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Thread send_thread = new Thread(new BroadcastSendThread());
        send_thread.start();

        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
