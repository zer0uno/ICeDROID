package unife.icedroid.core.managers;

import unife.icedroid.core.NeighborInfo;
import unife.icedroid.core.ICeDROIDMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NeighborhoodManager {
    private static final String TAG = "NeighborhoodManager";

    private volatile static NeighborhoodManager instance;
    private static long ttlOfNeighbor = 15*1000;

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

    public boolean add(NeighborInfo neighbor) {
        synchronized (neighborsList) {
            boolean newNeighbor = false;
            int index = isNeighborPresent(neighbor);

            if (index != -1) {
                neighborsList.remove(index);
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
    }

    public synchronized void remove(NeighborInfo neighbor) {
        if (System.currentTimeMillis() > neighbor.getLastTimeSeen().getTime() + ttlOfNeighbor) {
            neighborsList.remove(neighbor);
        }
    }

    public synchronized int getNumberOfNeighbors() {
        return neighborsList.size();
    }

    public synchronized boolean isThereNeighborNotInterestedToMessageAndNotCached(
                                                                            ICeDROIDMessage msg) {
        String channel = msg.getChannel();
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostChannels().contains(channel) &&
                !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isThereNeighborSubscribedToChannel(ICeDROIDMessage msg) {
        String channel = msg.getChannel();
        for (NeighborInfo neighbor : neighborsList) {
            if (neighbor.getHostChannels().contains(channel) &&
                    !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isThereNeighborWithoutThisMessage(ICeDROIDMessage msg) {
        String channel = msg.getChannel();
        //Is there a neighbor that isn't interested to this message, doesn't belong
        //to the same message channel and hasn't the message in its own cache?
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostChannels().contains(channel) &&
                    !neighbor.getCachedMessages().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ArrayList<NeighborInfo> whoHasThisMessageButNotInterested(
                                                                            ICeDROIDMessage msg) {
        ArrayList<NeighborInfo> neighbors = new ArrayList<>(0);
        String channel = msg.getChannel();
        for (NeighborInfo neighbor : neighborsList) {
            if (!neighbor.getHostChannels().contains(channel)) {
                if (neighbor.getCachedMessages().contains(msg)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    public long isThereAnUpdate(long time) {
        synchronized (neighborsList) {
            try {
                while (time == lastUpdate) {
                    neighborsList.wait();
                }
            } catch (Exception ex) {}
            return lastUpdate;
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