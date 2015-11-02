package unife.icedroid.core;

import java.net.DatagramPacket;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import unife.icedroid.ADCService;

/**
 * TODO
*/
public class MessageDispatcher {
    private static final String TAG = "MessageDispatcher";

    public static final String EXTRA_ADC_MESSAGE = "unife.icedroid.ADC_MESSAGE";

    public static void deliver(Context context, DatagramPacket packet) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        try {
            ObjectInputStream rawMessage = new ObjectInputStream(byteArrayInputStream);
            Message message = (Message) rawMessage.readObject();

            if (message.getTypeOfMsg().equals("regular")) {
                Intent intent = new Intent(context, ADCService.class);
                intent.putExtra(EXTRA_ADC_MESSAGE, message);
                context.startService(intent);
            } else {
                //Chiamare HelloMessageService
            }

        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "deliver(): An error occurred");
        }
    }
}
