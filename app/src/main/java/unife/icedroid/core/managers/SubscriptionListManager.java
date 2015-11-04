package unife.icedroid.core.managers;

import java.util.ArrayList;

import unife.icedroid.core.Subscription;

/**
 *  TODO
*/
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

    public ArrayList<Subscription> getSubscriptionsList() {
        ArrayList<Subscription> newSubscriptionsList = new ArrayList<Subscription>();
        for (Subscription sub : subscriptionsList) {
            Subscription newSub = new Subscription(sub);
            newSubscriptionsList.add(newSub);
        }
        return newSubscriptionsList;
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
        ArrayList<String> channels = new ArrayList<String>(0);
        for (Subscription sub : subscriptionsList) {
            if (!channels.contains(sub.getADChannel())) {
                channels.add(sub.getADChannel());
            }
        }
        return channels;
    }
}
