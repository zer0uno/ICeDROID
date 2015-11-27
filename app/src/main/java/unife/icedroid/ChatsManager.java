package unife.icedroid;

import android.content.Context;
import android.util.Log;
import unife.icedroid.core.ICeDROIDMessage;
import unife.icedroid.services.ApplevDisseminationChannelService.OnMessageReceiveListener;
import java.io.FileOutputStream;

public class ChatsManager implements OnMessageReceiveListener {
    private static final String TAG = "ChatsManager";
    private static final boolean DEBUG = true;

    private static volatile ChatsManager instance;

    private Context context;

    private ChatsManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ChatsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ChatsManager.class) {
                if (instance == null) {
                    instance = new ChatsManager(context);
                }
            }
        }
        return instance;
    }

    public static synchronized ChatsManager getInstance() {
        return instance;
    }

    public synchronized void saveMessageInConversation(TxtMessage message) {
        String data = "[" + message.getReceptionTime().toString() + "] " +
                        message.getHostID() + ": " +
                        message.getContentData() + "\n";

        Subscription subscription = new Subscription(message.getChannel(), message.getGroup());
        String path = context.getFilesDir().getAbsolutePath() + "/" + subscription.toString();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, true);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
            if (DEBUG) Log.i(TAG,"Message saved: " + data);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error writing to file" );
        }
    }

    public void receive (ICeDROIDMessage message) {
        try {
            TxtMessage txt = (TxtMessage) message;
            if (SubscriptionListManager.getSubscriptionListManager().isSubscribedToMessage(txt)) {
                saveMessageInConversation(txt);
            }
        } catch (Exception ex) {}
    }
}
