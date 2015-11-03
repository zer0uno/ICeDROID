package unife.icedroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import unife.icedroid.utils.Settings;

public class HelloMessage implements TypeOfMessage, Serializable{

    /** Type of message (hello or regular) */
    private final String typeOfMsg = "hello";

    /** Local host IDentifier */
    private final String hostID = Settings.HOST_IP;

    /** Subscriptions List of the local host */
    private ArrayList<Subscription> subscriptionsList;

    /** Cached messages of the local host */
    private ArrayList<MessageIdentity> cachedMessages;

    public HelloMessage() {
        subscriptionsList = SubscriptionListManager.getSubscriptionListManager().getSubscriptionsList();
        cachedMessages = MessageQueueManager.getMessageQueueManager().getCachedMessages();
    }

    public String getTypeOfMsg() {
        return typeOfMsg;
    }
}
