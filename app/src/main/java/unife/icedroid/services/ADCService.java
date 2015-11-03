package unife.icedroid.services;

import java.util.Random;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Looper;
import android.os.Handler;
import unife.icedroid.core.MessageDispatcher;
import unife.icedroid.core.MessageQueueManager;
import unife.icedroid.core.SubscriptionListManager;
import unife.icedroid.core.Message;

public class ADCService extends Service{
    private HandlerThread thread;
    private ADCHandler handler;

    private final class ADCHandler extends Handler {
        public ADCHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(android.os.Message msg) {
            Message ADCmessage = (Message) msg.obj;
            SubscriptionListManager subscriptionListManager = SubscriptionListManager.getSubscriptionListManager();
            MessageQueueManager messageQueueManager = MessageQueueManager.getMessageQueueManager();

            if (!messageQueueManager.isCached(ADCmessage) && !messageQueueManager.isAlreadyDecided(ADCmessage)) {
                if (subscriptionListManager.isSubscribedTo(ADCmessage.getSubscription())) {
                    //
                    messageQueueManager.addToCache(ADCmessage);
                } else if (subscriptionListManager.belongsToThisChannel(ADCmessage.getSubscription().getADChannel())) {
                    messageQueueManager.addToCache(ADCmessage);
                } else {
                    Random random = new Random(System.currentTimeMillis());
                    if (random.nextDouble() <= MessageQueueManager.CACHING_PROBABILITY) {
                        messageQueueManager.addToCache(ADCmessage);
                    }
                }
            }

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        thread = new HandlerThread("ADCThreadStart", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper ADClooper = thread.getLooper();
        handler = new ADCHandler(ADClooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        android.os.Message msg = handler.obtainMessage();
        msg.obj = intent.getSerializableExtra(MessageDispatcher.EXTRA_ADC_MESSAGE);
        msg.arg1 = startID;
        handler.sendMessage(msg);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (thread != null) {
            thread.quit();
        }
    }
}
