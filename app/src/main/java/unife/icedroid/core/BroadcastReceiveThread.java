package unife.icedroid.core;

import android.util.Log;
import android.content.Context;
import unife.icedroid.utils.Settings;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class BroadcastReceiveThread implements Runnable {
    private static final String TAG = "BroadcastReceiveThread";
    private static final boolean DEBUG = true;

    private Settings s;
    private DatagramSocket socket;
    private Context context;
    private MessageDispatcherThread messageDispatcherThread;

    public BroadcastReceiveThread(Settings s, Context context, DatagramSocket socket) {
        this.s = s;
        this.context = context;
        this.socket = socket;
        messageDispatcherThread = new MessageDispatcherThread(s, context);
        messageDispatcherThread.start();
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                DatagramPacket packet;
                byte[] data;
                while (true) {
                    data = new byte[s.getMessageSize()];
                    packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    //Filter out packets sent from this host
                    if (!packet.getAddress().getHostAddress().equals(s.getHostIP())) {
                        messageDispatcherThread.add(packet);
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error in BroadcastReceiveThread");
            }
        }
        messageDispatcherThread.interrupt();
    }
}
