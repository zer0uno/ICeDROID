package unife.icedroid.core;

import java.util.ArrayList;

import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.SubscriptionListManager;

public class HelloMessage extends Message {
    public static final String HELLO_MESSAGE = "helloMessage";

    private ArrayList<Subscription> hostSubscriptions;
    private ArrayList<RegularMessage> cachedMessages;

    public HelloMessage() {
        typeOfMessage = HELLO_MESSAGE;
        ttl = INFINITE_TTL;
        priority = MAX_PRIORITY_LEVEL;
        hostSubscriptions = SubscriptionListManager.getSubscriptionListManager().
                                                                            getSubscriptionsList();
        cachedMessages = MessageQueueManager.getMessageQueueManager().getCachedMessages();
    }


    public ArrayList<Subscription> getHostSubscriptions() {
        return hostSubscriptions;
    }

    public ArrayList<RegularMessage> getCachedMessages() {
        return cachedMessages;
    }

    public void setHostSubscriptions(ArrayList<Subscription> subscriptions) {
        hostSubscriptions = subscriptions;
    }

    public void setCachedMessages(ArrayList<RegularMessage> messages) {
        cachedMessages = messages;
    }

}