package unife.icedroid.core;

import java.util.ArrayList;
import java.util.Date;

public class NeighborInfo {

    private String hostID;
    private String hostUsername;
    private Date lastTimeSeen;
    private ArrayList<Subscription> hostSubscriptions;
    private ArrayList<RegularMessage> cachedMessages;

    public NeighborInfo(String id,
                        String username,
                        Date time,
                        ArrayList<Subscription> subscriptions,
                        ArrayList<RegularMessage> messages) {
        hostID = id;
        hostUsername = username;
        lastTimeSeen = time;
        hostSubscriptions = subscriptions;
        cachedMessages = messages;
    }

    public String getHostID() {
        return hostID;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public Date getLastTimeSeen() {
        return lastTimeSeen;
    }

    public ArrayList<Subscription> getHostSubscriptions() {
        return hostSubscriptions;
    }

    public ArrayList<RegularMessage> getCachedMessages() {
        return cachedMessages;
    }

    public void setHostID(String id) {
        hostID = id;
    }

    public void setHostUsername(String username) {
        hostUsername = username;
    }

    public void setLastTimeSeen(Date time) {
        lastTimeSeen = time;
    }

    public void setHostSubscriptions(ArrayList<Subscription> subscriptions) {
        hostSubscriptions = subscriptions;
    }

    public void setCachedMessages(ArrayList<RegularMessage> messages) {
        cachedMessages = messages;
    }

    public boolean equals(NeighborInfo nb) {
        return hostID == nb.hostID;
    }

}
