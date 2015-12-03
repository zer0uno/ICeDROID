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


    public BroadcastSendThread(Settings s) {
        messageQueueManager = MessageQueueManager.getMessageQueueManager();
        if (s != null) {
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
    }

    @Override
    public void run() {
        int counter = 0;
        if (!Thread.interrupted()) {
            try {
                InetAddress broadcastAddress = InetAddress.getByName(networkBroadcastAddress);
                BaseMessage message;
                byte[] data;
                ByteArrayOutputStream byteArrayOutputStream;
                ObjectOutputStream objectOutputStream;
                DatagramPacket packet;

                while (true) {
                    message = messageQueueManager.getMessageToSend();

                    //Need to ger a byte representation of the message
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(message);
                        data = byteArrayOutputStream.toByteArray();
                    } catch (IOException ex) {
                        data = null;
                        String msg = ex.getMessage();
                        if (DEBUG) Log.e(TAG, (msg != null) ? msg :
                                            "Impossible to convert to byte: " + message);
                    }

                    if (data != null) {
                        packet = new DatagramPacket(data, data.length, broadcastAddress, recvPort);
                        socket.send(packet);
                        counter++;
                        Log.i(TAG, "Message sent " + counter + ": " + message);
                    }
                }
            } catch (Exception ex) {
                socket.close();
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Closing BroadcastReceiveThread");
            }
        }

        if (DEBUG) Log.e(TAG, "SEND thread is deading");
    }
}
