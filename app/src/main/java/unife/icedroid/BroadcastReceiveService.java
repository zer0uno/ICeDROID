package unife.icedroid;

import java.net.DatagramSocket;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.BroadcastReceiveThread;

public class BroadcastReceiveService extends Service {
    private static final String TAG = "BroadcastReceiveService";

    private DatagramSocket socket;
    private Thread recvThread;

    @Override
    public void onCreate() {
        try {
            socket = new DatagramSocket();
            recvThread = new Thread(new BroadcastReceiveThread(getApplicationContext(), socket));
            recvThread.start();
            Log.i(TAG, "BroadcastReceiveThread started");
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
        socket.close();
        try {
            recvThread.join();
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "onDestroy(): An error occurred");
        }
        Log.i(TAG, "BroadcastReceiveService destroyed");
    }
}
