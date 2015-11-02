package unife.icedroid.core;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import android.util.Log;
import android.content.Context;
import unife.icedroid.utils.Settings;

/**
 *  TODO
*/
public class BroadcastReceiveThread implements Runnable {
    private static final String TAG = "BroadcastReceiveThread";

    private DatagramSocket socket;
    private Context context;

    public BroadcastReceiveThread(Context context, DatagramSocket socket) {
        this.context = context;
        this.socket = socket;
        try {
            InetAddress broadcastWildcard = InetAddress.getByName("0.0.0.0");
            InetSocketAddress addr = new InetSocketAddress(broadcastWildcard, Settings.RECV_PORT);
            this.socket.bind(addr);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "Socket error");
        }
    }

    @Override
    public void run() {
        try {
            Log.i("BroadcastReceiveThread", "UDP Socket is up");
            byte[] data = null;
            while (true) {
                data = new byte[Settings.MSG_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                MessageDispatcher.deliver(context, packet);
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e("BroadcastReceiveThread", (msg != null)? msg : "Error in BroadcastReceiveThread");
        }
    }
}
