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

    private MessageQueueManager messageQueueManager;
    private DatagramSocket socket;
    private String networkBroadcastAddress;
    private int recvPort;


    public BroadcastSendThread() {
        Settings s = Settings.getSettings();
        messageQueueManager = MessageQueueManager.getMessageQueueManager();
        try {
            InetAddress localHost = InetAddress.getByName(s.getHostIP());
            socket = new DatagramSocket(0, localHost);
            socket.setBroadcast(true);
            networkBroadcastAddress = s.getNetworkBroadcastAddress();
            recvPort = s.getReceivePort();
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Socket error");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        if (!Thread.interrupted()) {
            try {
                InetAddress broadcastAddress = InetAddress.getByName(networkBroadcastAddress);
                BaseMessage baseMessage;
                byte[] data;
                ByteArrayOutputStream byteArrayOutputStream;
                ObjectOutputStream objectOutputStream;
                DatagramPacket packet;

                while (true) {
                    baseMessage = messageQueueManager.getMessageToSend();

                    //Need to get a byte representation of the message
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(baseMessage);
                        data = byteArrayOutputStream.toByteArray();
                    } catch (IOException ex) {
                        data = null;
                        String msg = ex.getMessage();
                        if (DEBUG) Log.e(TAG, (msg != null) ? msg :
                                            "Impossible to convert to byte: " + baseMessage);
                    }

                    if (data != null) {
                        packet = new DatagramPacket(data, data.length, broadcastAddress, recvPort);
                        socket.send(packet);
                        Log.i(TAG, "Message sent: " + baseMessage);
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
