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
    private static final int DURATION = 15*1000;

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


            NeighborInfo neighbor = createNeighborInfo(helloMessage);
            boolean newNeighbor = NeighborhoodManager.getNeighborhoodManager().add(neighbor);

            intent = new Intent(HelloMessageService.this, ApplevDisseminationChannelService.class);
            intent.putExtra(HelloMessage.EXTRA_HELLO_MESSAGE, helloMessage);
            //If there is a new neighbor then there's need to recalculate forwarding messages
            if (newNeighbor) {
                intent.putExtra(NeighborInfo.EXTRA_NEW_NEIGHBOR, true);
            } else {
                intent.putExtra(NeighborInfo.EXTRA_NEIGHBOR_UPDATE, true);
            }
            startService(intent);
        }

        private NeighborInfo createNeighborInfo(HelloMessage helloMessage) {
            String hostID = helloMessage.getHostID();
            String hostUsername = helloMessage.getHostUsername();
            ArrayList<String> hostSubscription = helloMessage.getHostChannels();
            ArrayList<ICeDROIDMessage> cachedMessages = helloMessage.getCachedMessages();

            return new NeighborInfo(hostID, hostUsername, null,
                    hostSubscription, cachedMessages);
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
                Log.i(TAG, "HelloMessage added to ForwardingMessages, MsgID: " +
                                                                        helloMessage.getMsgID());

            }

        }, new Date(System.currentTimeMillis()), DURATION);

        thread = new HandlerThread("HelloMessageServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        handler = new HelloMessageHandler(thread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (intent.hasExtra(HelloMessage.EXTRA_HELLO_MESSAGE)) {
            android.os.Message msg = handler.obtainMessage();
            msg.obj = intent;
            handler.sendMessage(msg);
        }

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