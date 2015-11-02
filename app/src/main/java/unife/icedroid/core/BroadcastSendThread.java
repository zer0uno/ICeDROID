package unife.icedroid.core;

import android.util.Log;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import unife.icedroid.utils.Settings;

public class BroadcastSendThread implements Runnable {

    @Override
    public void run() {
        try {
            InetAddress localHost = InetAddress.getByName(Settings.HOST_IP);
            DatagramSocket socket = new DatagramSocket(0, localHost);
            socket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName(Settings.NW_BROADCAST_ADDRESS);
            byte[] data = null;
            DatagramPacket packet = null;

            while(true) {
                //data = devo prelevare il dato da inviare da qualche coda...
                packet = new DatagramPacket(data, data.length, broadcastAddress, Settings.RECV_PORT);
                socket.send(packet);
            }
            //vedere come chiudere le socket e come dire al thread di spegnersi
        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e("BroadcastReceiveThread", (msg != null) ? msg : "Error in BroadcastReceiveThread");
            //Vedere cosa fare, come comunicare l'errore;
        }
    }
}
