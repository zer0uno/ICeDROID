package unife.icedroid.services;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.IntentService;
import android.content.Intent;
import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.MessageDispatcher;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;

public class HelloMessageService extends IntentService{
    private static final String TAG = "HelloMessageService";

    private Timer helloMessageTimer;

    public HelloMessageService() {
        super(TAG);

        helloMessageTimer = new Timer();
        helloMessageTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                HelloMessage helloMessage = new HelloMessage();
                MessageQueueManager.getMessageQueueManager().send(helloMessage);
            }

        }, new Date(System.currentTimeMillis()), 25*1000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HelloMessage helloMessage = (HelloMessage) intent.getSerializableExtra(MessageDispatcher.
                                                                                EXTRA_HELLOMESSAGE);
        boolean newNeighbor = NeighborhoodManager.getNeighborhoodManager().add(helloMessage.
                                                                                    getHostInfo());
        if (newNeighbor) {
            MessageQueueManager.getMessageQueueManager().eraseForwardingDecisionTable();
        }
    }

    public void onDestroy() {
        helloMessageTimer.cancel();
        super.onDestroy();
    }
}
