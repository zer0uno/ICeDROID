package unife.icedroid.core.managers;

import unife.icedroid.core.NeighborInfo;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.Subscription;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NeighborhoodManager {
    /**
     * TODO
     * Ritornare sempre delle copie
     */
    private volatile static NeighborhoodManager instance;
    private static long ttlOfNeighbor = 35*1000;

    private ArrayList<NeighborInfo> neighborsList;
    private Timer neighborhoodManagerTimer;
    private long lastUpdate;


    private NeighborhoodManager() {
        neighborsList = new ArrayList<>(0);
        neighborhoodManagerTimer = new Timer();
    }

    public static NeighborhoodManager getNeighborhoodManager() {
        if (instance == null) {
            synchronized (NeighborhoodManager.class) {
                if (instance == null) {
                    instance = new NeighborhoodManager();
                }
            }
        }
        return instance;
    }

    public synchronized boolean add(NeighborInfo neighbor) {
        boolean newNeighbor = false;
        int index = isNeighborPresent(neighbor);

        if (index != -1) {
            neighborsList.add(index, neighbor);
        } else {
            neighborsList.add(neighbor);
            newNeighbor = true;
        }

        NeighborRemoveTask task = new NeighborRemoveTask(this, neighbor);
        Date expirationTime = new Date(neighbor.getLastTimeSeen().getTime() + ttlOfNeighbor);
        neighborhoodManagerTimer.schedule(task, expirationTime);

        lastUpdate = System.currentTimeMillis();
        //Notify that an update was done
        neighborsList.notifyAll();

        return newNeighbor;
    }

    public synchronized void remove(NeighborInfo neighbor) {
        neighborsList.remove(neighbor);
    }

    public synchronized int getNumberOfNeighbors() {
        return neighborsList.size();
    }

    public synchronized boolean isThereNeighborInterestedToMessage(RegularMessage msg) {
        Subscription subscription = msg.getSubscription();
        for (NeighborInfo neighbor : neighborsList) {
            //The neighbor must be subscribed to the same subscription and must not have
            //the message in its own cache
            if (neighbor.getHostSubscriptions().contains(subscription) &&
                !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isThereNeighborNotInterestedToMessageAndNotCached(
                                                                            RegularMessage msg) {
        Subscription subscription = msg.getSubscription();
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostSubscriptions().contains(subscription) &&
                !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isThereNeighborSubscribedToChannel(RegularMessage msg) {
        String channel = msg.getSubscription().getChannelID();
        for (NeighborInfo neighbor : neighborsList) {
            if (neighbor.getHostChannels().contains(channel) &&
                    !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isThereNeighborWithoutThisMessage(RegularMessage msg) {
        Subscription subscription = msg.getSubscription();
        String channel = subscription.getChannelID();
        //Is there a neighbor that isn't interested to this message, doesn't belong
        //to the same message channel and hasn't the message in its own cache?
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostSubscriptions().contains(subscription) &&
                !neighbor.getHostChannels().contains(channel) &&
                !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ArrayList<NeighborInfo> whoHasThisMessageButNotInterested(
                                                                            RegularMessage msg) {
        ArrayList<NeighborInfo> neighbors = new ArrayList<>(0);
        Subscription subscription = msg.getSubscription();
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostSubscriptions().contains(subscription)) {
                if (neighbor.getCachedMessages().contains(msg)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    public void isThereAnUpdate(long time) {
        synchronized (neighborsList) {
            try {
                while (time == lastUpdate) {
                    neighborsList.wait();
                }
            } catch (Exception ex) {}
        }
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    private int isNeighborPresent(NeighborInfo neighbor) {
        for (int i = 0; i < neighborsList.size(); i++) {
            if (neighbor.equals(neighborsList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private class NeighborRemoveTask extends TimerTask {

        private NeighborhoodManager neighborhoodManager;
        private NeighborInfo neighbor;

        public NeighborRemoveTask(NeighborhoodManager neighborhoodManager,
                                  NeighborInfo neighbor) {
            this.neighborhoodManager = neighborhoodManager;
            this.neighbor = neighbor;
        }

        @Override
        public void run() {
            neighborhoodManager.remove(neighbor);
        }

    }

}