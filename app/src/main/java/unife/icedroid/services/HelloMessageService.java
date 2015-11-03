package unife.icedroid.services;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.IntentService;
import android.content.Intent;
import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.MessageQueueManager;

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

    }

    public void onDestroy() {
        helloMessageTimer.cancel();
        super.onDestroy();
    }
}
