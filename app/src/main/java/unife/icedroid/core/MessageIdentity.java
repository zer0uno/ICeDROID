package unife.icedroid.core;

import java.io.Serializable;

public class MessageIdentity implements Serializable {

    private Subscription subscription;
    private int msgID;


    public MessageIdentity(Subscription subscription, int msgID) {
        this.subscription = subscription;
        this.msgID = msgID;
    }

    public MessageIdentity(MessageIdentity msgIdentity) {
        this.subscription = new Subscription(msgIdentity.subscription);
        this.msgID = msgIdentity.msgID;
    }

    public Subscription getSubscription() {
        Subscription newSubscription = new Subscription(subscription);
        return newSubscription;
    }

    public int getMsgID() {
        return msgID;
    }

}
