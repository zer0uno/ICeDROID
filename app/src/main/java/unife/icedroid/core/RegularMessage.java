package unife.icedroid.core;

public class RegularMessage extends Message {
    public static final String REGULAR_MESSAGE = "regularMessage";
    public final static String EXTRA_REGULAR_MESSAGE = "unife.icedroid.REGULAR_MESSAGE";

    private Subscription subscription;
    private String contentData;

    public RegularMessage(Subscription sbsc, String data) {
        super();
        typeOfMessage = REGULAR_MESSAGE;
        ttl = INFINITE_TTL;
        priority = NO_PRIORITY_LEVEL;
        contentData = data;
        subscription = sbsc;
        size = contentData.getBytes().length;
    }

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
