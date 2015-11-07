package unife.icedroid.core;

import java.net.DatagramPacket;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.Date;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import unife.icedroid.services.ApplevDisseminationChannelService;
import unife.icedroid.services.HelloMessageService;

public class MessageDispatcher {
    private static final String TAG = "MessageDispatcher";


    public static void deliver(Context context, DatagramPacket packet) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());

        try {
            ObjectInputStream rawMessage = new ObjectInputStream(byteArrayInputStream);
            Message message = (Message) rawMessage.readObject();

            //Set message reception time
            message.setReceptionTime(new Date(System.currentTimeMillis()));

            Intent intent;

            if (message.getTypeOfMessage().equals("regular")) {
                intent = new Intent(context, ApplevDisseminationChannelService.class);
                intent.putExtra(Constants.EXTRA_ADC_MESSAGE, message);
            } else {
                intent = new Intent(context, HelloMessageService.class);
                intent.putExtra(Constants.EXTRA_HELLO_MESSAGE, message);
            }
            context.startService(intent);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "deliver(): An error occurred");
        }

    }

}
