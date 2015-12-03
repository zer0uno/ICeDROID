package unife.icedroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class NeighborInfo implements Serializable {
    public static final String EXTRA_NEIGHBOR = "unife.icedroid.NEIGHBOR";
    public static final String EXTRA_NEW_NEIGHBOR = "unife.icedroid.NEW_NEIGHBOR";
    public static final String EXTRA_NEIGHBOR_UPDATE = "unife.icedroid.NEIGHBOR_UPDATE";

    private String hostID;
    private String hostUsername;
    private Date lastTimeSeen;
    private ArrayList<String> hostChannels;
    private ArrayList<ICeDROIDMessage> cachedMessages;

    public NeighborInfo(String id,
                        String username,
                        Date time,
                        ArrayList<String> channels,
                        ArrayList<ICeDROIDMessage> messages) {
        hostID = id;
        hostUsername = username;
        lastTimeSeen = time;
        hostChannels = new ArrayList<>(channels);
        cachedMessages = new ArrayList<>(messages);
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

    public ArrayList<String> getHostChannels() {
        return new ArrayList<>(hostChannels);
    }

    public ArrayList<ICeDROIDMessage> getCachedMessages() {
        return new ArrayList<>(cachedMessages);
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

    public void setHostChannels(ArrayList<String> channels) {
        hostChannels = new ArrayList<>(channels);
    }

    public void setCachedMessages(ArrayList<ICeDROIDMessage> messages) {
        cachedMessages = new ArrayList<>(messages);
    }

    public void copyFromNeighbor(NeighborInfo neighbor) {
        hostUsername = neighbor.hostUsername;
        lastTimeSeen = neighbor.lastTimeSeen;
        hostChannels = neighbor.hostChannels;
        cachedMessages = neighbor.cachedMessages;
    }

    @Override
    public boolean equals(Object object) {
        NeighborInfo nb = (NeighborInfo) object;
        return hostID.equals(nb.hostID);
    }

}
