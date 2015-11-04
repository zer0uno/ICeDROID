package unife.icedroid.core;

import java.util.ArrayList;

public class HostInfo {

    private String hostID;
    private ArrayList<Subscription> subscriptionsList;
    private ArrayList<MessageIdentity> cachedMessages;


    public HostInfo(String hostID, ArrayList<Subscription> subscriptionsList, ArrayList<MessageIdentity> cachedMessages) {
        this.hostID = hostID;
        this.subscriptionsList = subscriptionsList;
        this.cachedMessages = cachedMessages;
    }

    public HostInfo(HostInfo hostInfo) {
        this.hostID = hostInfo.hostID;
        this.subscriptionsList = hostInfo.getSubscriptionsList();
        this.cachedMessages = hostInfo.getCachedMessages();
    }

    public String getHostID() {
        return hostID;
    }

    public ArrayList<Subscription> getSubscriptionsList() {
        ArrayList<Subscription> newSubscriptionsList = new ArrayList<Subscription>();
        for (Subscription sub : subscriptionsList) {
            Subscription newSub = new Subscription(sub);
            newSubscriptionsList.add(newSub);
        }
        return newSubscriptionsList;
    }

    public ArrayList<MessageIdentity> getCachedMessages() {
        ArrayList<MessageIdentity> newCachedMessages = new ArrayList<MessageIdentity>();
        for (MessageIdentity cachedMsg : cachedMessages) {
            MessageIdentity newCachedMsg = new MessageIdentity(cachedMsg);
            newCachedMessages.add(newCachedMsg);
        }
        return newCachedMessages;
    }

    public void setSubscriptionsList(ArrayList<Subscription> subList) {
        this.subscriptionsList = subList;
    }

    public void setCachedMessages(ArrayList<MessageIdentity> cachedMessages) {
        this.cachedMessages = cachedMessages;
    }

    public boolean equals(HostInfo o) {
        return hostID == o.hostID;
    }

}
