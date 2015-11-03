package unife.icedroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import unife.icedroid.utils.Settings;

public class HelloMessage implements TypeOfMessage, Serializable{

    /** Type of message (hello or regular) */
    private String typeOfMsg;
    /** Local host IDentifier */
    private String hostID;
    /** Subscriptions List of the local host */
    private ArrayList<Subscription> subscriptionsList;
    /** Cached messages of the local host */
    private ArrayList<MessageIdentity> cachedMessages;


    public HelloMessage() {

        typeOfMsg = "hello";
        hostID = Settings.HOST_IP;
        subscriptionsList = SubscriptionListManager.getSubscriptionListManager().getSubscriptionsList();
        cachedMessages = MessageQueueManager.getMessageQueueManager().getCachedMessages();

    }


    public String getTypeOfMsg() {
        return typeOfMsg;
    }
}
