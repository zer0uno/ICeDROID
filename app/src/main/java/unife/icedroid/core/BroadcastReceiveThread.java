package unife.icedroid.core;

import android.util.Log;
import android.content.Context;
import unife.icedroid.utils.Settings;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class BroadcastReceiveThread implements Runnable {
    private static final String TAG = "BroadcastReceiveThread";
    private static final boolean DEBUG = true;

    private Settings s;
    private DatagramSocket socket;
    private Context context;
    private ArrayList<DatagramPacket> packets;
    private Thread messageDispatcherThread;

    public BroadcastReceiveThread(Settings s, Context context, DatagramSocket socket) {
        this.s = s;
        this.context = context;
        this.socket = socket;
        this.packets = new ArrayList<>(0);
        messageDispatcherThread = new Thread(new MessageDispatcher(context, packets));
        messageDispatcherThread.start();
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                if (DEBUG) Log.i(TAG, "UDP Socket is up to receive!");
                DatagramPacket packet;
                byte[] data;
                while (true) {
                    data = new byte[s.getMessageSize()];
                    packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    synchronized (packets) {
                        if (!packet.getAddress().getHostAddress().equals(s.getHostIP())) {
                            packets.add(packet);
                            packets.notifyAll();
                        }
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
