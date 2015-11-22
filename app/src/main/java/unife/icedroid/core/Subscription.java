package unife.icedroid.core;

import java.io.Serializable;

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

    public boolean equals(Subscription subscription) {
        return (channelID.equals(subscription.channelID) &&
                groupName.equals(subscription.groupName));
    }

    @Override
    public String toString() {
        return channelID + ":" + groupName;
    }

}
