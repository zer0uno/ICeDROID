package unife.icedroid.core;

import java.net.DatagramPacket;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import unife.icedroid.services.ADCService;
import unife.icedroid.services.HelloMessageService;

public class MessageDispatcher {

    private static final String TAG = "MessageDispatcher";
    public static final String EXTRA_ADC_MESSAGE = "unife.icedroid.ADC_MESSAGE";
    public static final String EXTRA_HELLOMESSAGE = "unife.icedroid.HELLOMESSAGE";


    public static void deliver(Context context, DatagramPacket packet) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());

        try {
            ObjectInputStream rawMessage = new ObjectInputStream(byteArrayInputStream);
            TypeOfMessage message = (TypeOfMessage) rawMessage.readObject();

            Intent intent;

            if (message.getTypeOfMsg().equals("regular")) {
                Message regularMessage = (Message) message;
                intent = new Intent(context, ADCService.class);
                intent.putExtra(EXTRA_ADC_MESSAGE, regularMessage);
            } else {
                HelloMessage helloMessage = (HelloMessage) message;
                intent = new Intent(context, HelloMessageService.class);
                intent.putExtra(EXTRA_HELLOMESSAGE, helloMessage);
            }

            context.startService(intent);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null)? msg : "deliver(): An error occurred");
        }

    }

}
