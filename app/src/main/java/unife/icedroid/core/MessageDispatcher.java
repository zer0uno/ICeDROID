package unife.icedroid.core;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import unife.icedroid.services.ApplevDisseminationChannelService;
import unife.icedroid.services.HelloMessageService;
import unife.icedroid.utils.Settings;
import java.net.DatagramPacket;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;

public class MessageDispatcher extends Thread {
    private static final String TAG = "MessageDispatcher";
    private static final boolean DEBUG = true;
    private static final Settings s = Settings.getSettings();

    private Context context;
    private ArrayList<DatagramPacket> packets;
    private ApplevDisseminationChannelService ADCThread;

    public MessageDispatcher(Context context) {
        this.context = context;
        this.packets = new ArrayList<>(0);
        ADCThread = Settings.getSettings().getADCThread();
    }


    public void run() {
        DatagramPacket packet;
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream rawMessage;
        BaseMessage message;
        String messageSource;

        while (!Thread.interrupted()) {

            synchronized (packets) {
                while (packets.size() == 0) {
                    try {
                        packets.wait();
                    } catch (Exception ex) {
                    }
                }
                packet = packets.get(0);
                packets.remove(0);
            }

            try {
                byteArrayInputStream = new ByteArrayInputStream(packet.getData());
                rawMessage = new ObjectInputStream(byteArrayInputStream);
                message = (BaseMessage) rawMessage.readObject();

                messageSource = message.getHostID();

                if (!messageSource.equals(s.getHostID())) {
                    Log.i(TAG, "Received message: " + message);
                    //Set message reception time
                    message.setReceptionTime(new Date(System.currentTimeMillis()));

                    Intent intent;
                    if (message.getTypeOfMessage().equals(ICeDROIDMessage.ICEDROID_MESSAGE)) {
                        intent = new Intent(context, ApplevDisseminationChannelService.class);
                        intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE,
                                                                                        message);
                        ADCThread.add(intent);
                    } else {
                        intent = new Intent(context, HelloMessageService.class);
                        intent.putExtra(HelloMessage.EXTRA_HELLO_MESSAGE, message);
                        context.startService(intent);
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "deliver(): An error occurred");
            }
        }
    }

    public void add(DatagramPacket packet) {
        synchronized (packets) {
            packets.add(packet);
            packets.notifyAll();
        }
    }
}
