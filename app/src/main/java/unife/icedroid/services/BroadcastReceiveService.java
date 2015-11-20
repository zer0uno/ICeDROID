package unife.icedroid.services;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.BroadcastReceiveThread;
import unife.icedroid.utils.Settings;

public class BroadcastReceiveService extends Service {
    private static final String TAG = "BroadcastReceiveService";

    private DatagramSocket socket;
    private Thread recvThread;

    @Override
    public void onCreate() {
        /**
         * TODO
         * Trovare un modo per segnalare che è impossibile avviare il servizio di send e che quindi
         * l'applicazione va chiusa
         */
        try {
            InetAddress broadcastWildcard = InetAddress.getByName("0.0.0.0");
            socket = new DatagramSocket(Settings.RECV_PORT, broadcastWildcard);
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
        recvThread.interrupt();
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
