package unife.icedroid.core;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import android.util.Log;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.utils.Settings;

public class BroadcastSendThread implements Runnable {
    private static final String TAG = "BroadcastSendThread";

    private MessageQueueManager messageQueueManager;
    private DatagramSocket socket;


    public BroadcastSendThread() {
        /**
         * TODO
         * Trovare un modo più carino nel caso ci sia un errore nella creazione della socket
         * per esempio riprovanto più volte dopo aver chiamato una sleep e provando al max TOT volte
        */
        messageQueueManager = MessageQueueManager.getMessageQueueManager();
        try {
            InetAddress localHost = InetAddress.getByName(Settings.HOST_IP);
            socket = new DatagramSocket(0, localHost);
            socket.setBroadcast(true);
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
                InetAddress broadcastAddress = InetAddress.getByName(Settings.NW_BROADCAST_ADDRESS);
                byte[] data = null;
                DatagramPacket packet = null;

                while (true) {
                    //Log.i(TAG, "Waiting for a message to send...");
                    data = messageQueueManager.getMessageToSend();
                    Thread.sleep(10000);
                    if (data != null) {
                        packet = new DatagramPacket(data, data.length, broadcastAddress, Settings.RECV_PORT);
                        socket.send(packet);
                        Log.i(TAG, "Message sent: " + data);
                    }
                }
            } catch (Exception ex) {
                socket.close();
                String msg = ex.getMessage();
                Log.e(TAG, (msg != null) ? msg : "Closing BroadcastReceiveThread");
            }
        }
    }
}
