package unife.icedroid.services;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.BroadcastReceiveThread;
import unife.icedroid.utils.Settings;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastReceiveService extends Service {
    private static final String TAG = "BroadcastReceiveService";
    private static final boolean DEBUG = true;

    private DatagramSocket socket;
    private Thread recvThread;

    @Override
    public void onCreate() {
        Settings s = Settings.getSettings();

        if (s != null) {
            try {
                InetAddress broadcastWildcard = InetAddress.getByName("0.0.0.0");
                socket = new DatagramSocket(s.getReceivePort(), broadcastWildcard);
                recvThread = new Thread(new BroadcastReceiveThread(
                                                            s, getApplicationContext(), socket));
                recvThread.start();
                if (DEBUG) Log.i(TAG, "BroadcastReceiveThread started");
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "onCreate(): An error occurred");
                stopSelf();
            }
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
        recvThread.interrupt();
        socket.close();
        try {
            recvThread.join();
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null)? msg : "onDestroy(): An error occurred");
        }
        if (DEBUG) Log.i(TAG, "BroadcastReceiveService destroyed");
    }
}
