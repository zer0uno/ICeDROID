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
import java.util.Date;

public class MessageDispatcher {
    private static final String TAG = "MessageDispatcher";
    private static final boolean DEBUG = true;


    public static void deliver(Context context, DatagramPacket packet) {
        Settings s = Settings.getSettings();

        if (s != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());

            try {
                ObjectInputStream rawMessage = new ObjectInputStream(byteArrayInputStream);
                Message message = (Message) rawMessage.readObject();

                //Broadcast messages are also delivered to whom generated the message
                if (!message.getHostID().equals(s.getHostID())) {
                    if (DEBUG) Log.i(TAG, "Received a message" + message);
                    //Set message reception time
                    message.setReceptionTime(new Date(System.currentTimeMillis()));

                    Intent intent;
                    if (message.getTypeOfMessage().equals("regular")) {
                        intent = new Intent(context, ApplevDisseminationChannelService.class);
                        intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE,
                                        message);
                    } else {
                        intent = new Intent(context, HelloMessageService.class);
                        intent.putExtra(HelloMessage.EXTRA_HELLO_MESSAGE, message);
                    }
                    context.startService(intent);
                }

            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "deliver(): An error occurred");
            }
        }
    }
}
