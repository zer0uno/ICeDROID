package unife.icedroid.core;

import android.util.Log;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.utils.Settings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class BroadcastSendThread implements Runnable {
    private static final String TAG = "BroadcastSendThread";
    private static final boolean DEBUG = true;

    private Settings s;
    private MessageQueueManager messageQueueManager;
    private DatagramSocket socket;


    public BroadcastSendThread(Settings s) {
        this.s = s;
        messageQueueManager = MessageQueueManager.getMessageQueueManager();
        if (s != null) {
            try {
                InetAddress localHost = InetAddress.getByName(s.getHostIP());
                socket = new DatagramSocket(0, localHost);
                socket.setBroadcast(true);
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Socket error");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                InetAddress broadcastAddress = InetAddress.
                                                        getByName(s.getNetworkBroadcastAddress());
                Message message = null;
                byte[] data = null;
                ByteArrayOutputStream byteArrayInputStream;
                ObjectOutputStream objectOutputStream;
                DatagramPacket packet = null;

                while (true) {
                    if (DEBUG) Log.i(TAG, "Waiting for a message to send...");
                    message = messageQueueManager.getMessageToSend();

                    //Need to ger a byte representation of the message
                    byteArrayInputStream = new ByteArrayOutputStream(s.getMessageSize());
                    try {
                        objectOutputStream = new ObjectOutputStream(byteArrayInputStream);
                        objectOutputStream.writeObject(message);
                        data = byteArrayInputStream.toByteArray();
                    } catch (IOException ex) {
                        data = null;
                        String msg = ex.getMessage();
                        if (DEBUG) Log.e(TAG, (msg != null) ? msg :
                                            "Impossible to convert to byte: " + message);
                    }

                    Thread.sleep(6000);
                    if (data != null) {
                        packet = new DatagramPacket(data, data.length,
                                                            broadcastAddress, s.getReceivePort());
                        socket.send(packet);
                        if (DEBUG) Log.i(TAG, "Message sent: " + message);
                    }
                }
            } catch (Exception ex) {
                socket.close();
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Closing BroadcastReceiveThread");
            }
        }
    }
}
