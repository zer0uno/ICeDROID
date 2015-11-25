package unife.icedroid.core;

import java.util.HashMap;
import java.util.Map;

public class RegularMessage extends Message {
    public static final String REGULAR_MESSAGE = "regularMessage";
    public final static String EXTRA_REGULAR_MESSAGE = "unife.icedroid.REGULAR_MESSAGE";

    private Subscription subscription;
    private String contentData;
    private Map<String, Integer> properties;

    public RegularMessage(Subscription sbsc, String data) {
        super();
        typeOfMessage = REGULAR_MESSAGE;
        ttl = 60*1000;
        priority = NO_PRIORITY_LEVEL;
        contentData = data;
        subscription = sbsc;
        properties = new HashMap<>(0);
        setSize();
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public String getContentData() {
        return contentData;
    }

    public Integer getProperty(String key) {
        return properties.get(key);
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void setContentData(String data) {
        contentData = data;
    }

    public void setProperty(String key, Integer value) {
        properties.put(key, value);
    }
}
