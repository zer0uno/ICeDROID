package unife.icedroid.core;

public class Subscription {

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

}
