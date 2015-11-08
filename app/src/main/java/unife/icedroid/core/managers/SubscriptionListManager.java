package unife.icedroid.core.managers;

import java.util.ArrayList;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.Subscription;

public class SubscriptionListManager {

    private volatile static SubscriptionListManager instance = null;

    private ArrayList<Subscription> subscriptionsList;


    private SubscriptionListManager() {
        subscriptionsList = new ArrayList<Subscription>(0);
    }

    public static SubscriptionListManager getSubscriptionListManager() {
        if (instance == null) {
            synchronized (SubscriptionListManager.class) {
                if (instance == null) {
                    instance = new SubscriptionListManager();
                }
            }
        }
        return instance;
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
