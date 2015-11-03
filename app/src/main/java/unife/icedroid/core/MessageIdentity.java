package unife.icedroid.core;

public class MessageIdentity {

    private Subscription subscription;
    private int msgID;

    public MessageIdentity() {}

    public MessageIdentity(Subscription subscription, int msgID) {

        this.subscription.copy(subscription);
        this.msgID = msgID;

    }

    public Subscription getSubscription() {

        Subscription newSubscription = new Subscription();
        newSubscription.copy(subscription);
        return newSubscription;

    }

    public int getMsgID() {
        return msgID;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = new Subscription();
        this.subscription.copy(subscription);
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public boolean equals(MessageIdentity msgIdentity) {
        return subscription.equals(msgIdentity.subscription) && msgID == msgIdentity.msgID;
    }

}
