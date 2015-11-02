package unife.icedroid.core;

import java.util.ArrayList;

/**
 *  TODO
*/
public class SubscriptionListManager {
    private volatile static SubscriptionListManager instance = null;

    private ArrayList<Subscription> subscriptionsList;


    private SubscriptionListManager() {
        subscriptionsList = new ArrayList<Subscription>();
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

    public void add(Subscription subscription) {
        subscriptionsList.add(subscription);
    }

    public boolean isSubscribedTo(Subscription subscription) {
        return subscriptionsList.contains(subscription);
    }

    public boolean belongsToThisChannel(String ADCchannel) {
        return getSubscriptionsADChannels().contains(ADCchannel) ? true : false;
    }

    private ArrayList<String> getSubscriptionsADChannels() {
        ArrayList<String> channels = new ArrayList<String>();
        for (Subscription sub : subscriptionsList) {
            if (!channels.contains(sub.getADChannel())) {
                channels.add(sub.getADChannel());
            }
        }
        return channels;
    }
}
