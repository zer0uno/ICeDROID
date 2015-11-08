package unife.icedroid.core;

import java.util.ArrayList;

public class HelloMessage extends Message {
    public static final String HELLO_MESSAGE = "helloMessage";

    private ArrayList<Subscription> hostSubscriptions;
    private ArrayList<RegularMessage> cachedMessages;


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