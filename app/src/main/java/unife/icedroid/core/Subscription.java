package unife.icedroid.core;

import java.io.Serializable;
import java.util.Objects;

public class Subscription implements Serializable {

    private String channelID;
    private String groupName;

    public Subscription(String channel, String group) {
        channelID = channel;
        groupName = group;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public boolean equals(Object object) {
        Subscription subscription = (Subscription) object;
        return (channelID.equals(subscription.channelID) &&
                groupName.equals(subscription.groupName));
    }

    @Override
    public String toString() {
        return channelID + ":" + groupName;
    }

}
