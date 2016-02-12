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
    private MessageDispatcher messageDispatcher;

    public BroadcastReceiveThread(Settings s, Context context, DatagramSocket socket) {
        this.s = s;
        this.context = context;
        this.socket = socket;
        messageDispatcher = new MessageDispatcher(s, context);
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                byte[] data = new byte[s.getMessageSize()];
                DatagramPacket packet;
                while (true) {
                    packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    //Filter out packets sent from this host
                    if (!packet.getAddress().getHostAddress().equals(s.getHostIP())) {
                        messageDispatcher.dispatch(packet);
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error in BroadcastReceiveThread");
            }
        }
    }
}
