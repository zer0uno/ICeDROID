package unife.icedroid.core;

public class RegularMessage extends Message {
    public static final String REGULAR_MESSAGE = "regularMessage";

    private Subscription subscription;
    private String contentData;

    public Subscription getSubscription() {
        return subscription;
    }

    public String getContentData() {
        return contentData;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void setContentData(String data) {
        contentData = data;
    }

    public boolean equals(RegularMessage msg) {
        return (super.equals(msg) && subscription.equals(msg.subscription));

    }

}
