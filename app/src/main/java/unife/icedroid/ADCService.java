package unife.icedroid;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import unife.icedroid.core.MessageDispatcher;
import unife.icedroid.core.SubscriptionListManager;

public class ADCService extends Service{
    private HandlerThread thread;
    private ADCHandler handler;

    private final class ADCHandler extends Handler {
        public ADCHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            unife.icedroid.core.Message ADCmsg = (unife.icedroid.core.Message) msg.obj;
            SubscriptionListManager subscriptionListManager = SubscriptionListManager.getSubscriptionListManager();

            if (subscriptionListManager.isSubscribedTo(ADCmsg.getSubscription())) {
                //
            } else if (subscriptionListManager.belongsToThisChannel(ADCmsg.getSubscription().getADChannel())) {

            } else {

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
        Message msg = handler.obtainMessage();
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
        thread.quit();
    }
}
