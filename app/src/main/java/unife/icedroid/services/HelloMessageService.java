package unife.icedroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import unife.icedroid.core.*;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;
import java.util.*;

public class HelloMessageService extends Service {
    private static final String TAG = "HelloMessageService";
    private static final boolean DEBUG = true;

    private MessageQueueManager messageQueueManager;
    private Timer helloMessageTimer;
    private HandlerThread thread;
    private HelloMessageHandler handler;

    private final class HelloMessageHandler extends Handler {

        public HelloMessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = (Intent) msg.obj;
            HelloMessage helloMessage =
                    (HelloMessage) intent.getSerializableExtra(HelloMessage.EXTRA_HELLO_MESSAGE);

            if (helloMessage != null) {
                String hostID = helloMessage.getHostID();
                String hostUsername = helloMessage.getHostUsername();
                Date lastTimeSeen = helloMessage.getReceptionTime();
                ArrayList<Subscription> hostSubscription = helloMessage.getHostSubscriptions();
                ArrayList<RegularMessage> cachedMessages = helloMessage.getCachedMessages();
                NeighborInfo neighbor = new NeighborInfo(hostID, hostUsername, lastTimeSeen,
                        hostSubscription, cachedMessages);
                boolean newNeighbor = NeighborhoodManager.getNeighborhoodManager().add(neighbor);

                //If there is a new neighbor then there's need to recalculate forwarding messages
                if (newNeighbor) {
                    intent = new Intent(HelloMessageService.this,
                                        ApplevDisseminationChannelService.class);
                    intent.putExtra(NeighborInfo.EXTRA_NEW_NEIGHBOR, true);
                    startService(intent);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        messageQueueManager = MessageQueueManager.getMessageQueueManager();

        helloMessageTimer = new Timer(TAG);
        helloMessageTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                HelloMessage helloMessage = new HelloMessage();
                messageQueueManager.addToForwardingMessages(helloMessage);
                Log.i(TAG, "HelloMessage added to ForwardingMessages");

            }

        }, new Date(System.currentTimeMillis()), 15*1000);

        if (DEBUG) Log.i(TAG, "HelloMessageTimer ON");

        thread = new HandlerThread("HelloMessageServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        handler = new HelloMessageHandler(thread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        android.os.Message msg = handler.obtainMessage();
        msg.obj = intent;
        handler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        if (DEBUG) Log.i(TAG, "Service destroyed");
        helloMessageTimer.cancel();
        super.onDestroy();
    }
}