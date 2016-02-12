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

public class MessageDispatcher {
    private static final String TAG = "MessageDispatcher";
    private static final boolean DEBUG = true;

    private Settings s;
    private Context context;
    private ArrayList<DatagramPacket> packets;
    private ApplevDisseminationChannelService ADCThread;

    public MessageDispatcher(Settings s, Context context) {
        this.s = s;
        this.context = context;
        this.packets = new ArrayList<>(0);
        ADCThread = Settings.getSettings().getADCThread();
    }


    public void dispatch(DatagramPacket packet) {
        //DatagramPacket packet;
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream rawMessage;
        BaseMessage baseMessage;
        Intent intent;

            try {
                byteArrayInputStream = new ByteArrayInputStream(packet.getData());
                rawMessage = new ObjectInputStream(byteArrayInputStream);
                baseMessage = (BaseMessage) rawMessage.readObject();
                byteArrayInputStream.close();
                rawMessage.close();

                //Filter out messages generated from this host
                if (!baseMessage.getHostID().equals(s.getHostID())) {
                    //Set message reception time
                    baseMessage.setReceptionTime(new Date(System.currentTimeMillis()));

                    if (baseMessage.getTypeOfMessage().equals(ICeDROIDMessage.ICEDROID_MESSAGE)) {
                        intent = new Intent();
                        intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE,
                                                                                    baseMessage);
                        ADCThread.add(intent);
                    } else {
                        intent = new Intent(context, HelloMessageService.class);
                        intent.putExtra(HelloMessage.EXTRA_HELLO_MESSAGE, baseMessage);
                        context.startService(intent);
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "deliver(): An error occurred");
            }
        }
    }