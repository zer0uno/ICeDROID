package unife.icedroid.core;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import android.util.Log;
import android.content.Context;
import unife.icedroid.utils.Settings;

public class BroadcastReceiveThread implements Runnable {
    private static final String TAG = "BroadcastReceiveThread";

    private DatagramSocket socket;
    private Context context;

    public BroadcastReceiveThread(Context context, DatagramSocket socket) {
        /**
         * TODO
         * Trovare un modo più carino nel caso ci sia un errone nella creazione della socket
         * per esempio riprovanto più volte dopo aver chiamato una sleep e provando al max TOT volte
         */
        this.context = context;
        this.socket = socket;
        try {
            InetAddress broadcastWildcard = InetAddress.getByName("0.0.0.0");
            InetSocketAddress addr = new InetSocketAddress(broadcastWildcard, Settings.RECV_PORT);
            this.socket.bind(addr);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "Socket error");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                Log.i(TAG, "UDP Socket is up");
                DatagramPacket packet = null;
                byte[] data = null;
                while (true) {
                    data = new byte[Settings.MSG_SIZE];
                    packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    MessageDispatcher.deliver(context, packet);
                    Log.i(TAG, "Received: " + packet.toString());
                }

            } catch (Exception ex) {
                String msg = ex.getMessage();
                Log.e(TAG, (msg != null) ? msg : "Error in BroadcastReceiveThread");
            }
        }
    }
}
