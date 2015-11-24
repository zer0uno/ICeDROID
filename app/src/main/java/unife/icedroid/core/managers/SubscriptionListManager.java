package unife.icedroid.core.managers;

import android.content.Context;
import android.util.Log;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.Subscription;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SubscriptionListManager {
    /**
     * TODO
     * Ritornare sempre delle copie
    */
    private static final String TAG = "SubscriptionListManager";
    private static final boolean DEBUG = true;

    private static final String subscriptionsFileName = "subscriptions";
    private volatile static SubscriptionListManager instance = null;

    private ArrayList<Subscription> subscriptionsList;
    private Context context;


    private SubscriptionListManager(Context context) {
        this.context = context.getApplicationContext();
        subscriptionsList = new ArrayList<>(0);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                               context.openFileInput(subscriptionsFileName)));

            Subscription subscription;
            String subscriptionLine;
            while ((subscriptionLine = br.readLine()) != null) {
                String[] channelAndGroup = subscriptionLine.split(":");
                subscription = new Subscription(channelAndGroup[0], channelAndGroup[1]);
                subscriptionsList.add(subscription);
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error loading subscriptions list");
        }
    }

    public static SubscriptionListManager getSubscriptionListManager(Context context) {
        if (instance == null) {
            synchronized (SubscriptionListManager.class) {
                if (instance == null) {
                    instance = new SubscriptionListManager(context);
                }
            }
        }
        return instance;
    }

    public static SubscriptionListManager getSubscriptionListManager() {
        return instance;
    }

    public synchronized void subscribe(Subscription subscription) {
        if (!subscriptionsList.contains(subscription)) {
            subscriptionsList.add(subscription);
            try {
                FileOutputStream fos = context.openFileOutput(subscriptionsFileName,
                        Context.MODE_PRIVATE | Context.MODE_APPEND);
                fos.write((subscription.toString() + "\n").getBytes());
                fos.close();
                fos = context.openFileOutput(subscription.toString(), Context.MODE_PRIVATE);
                fos.close();
                if (DEBUG) Log.i(TAG, "Subscribing to: " + subscription.toString());
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error subscribing");
            }
        }
    }

    public synchronized ArrayList<Subscription> getSubscriptionsList() {
        return subscriptionsList;
    }

    public synchronized boolean isSubscribedToMessage(RegularMessage msg) {
        return subscriptionsList.contains(msg.getSubscription());
    }

    public synchronized boolean isSubscribedToChannel(RegularMessage msg) {
        for (Subscription sub : subscriptionsList) {
            if (sub.getChannelID().equals(msg.getSubscription().getChannelID())) {
                return true;
            }
        }
        return false;
    }

}
