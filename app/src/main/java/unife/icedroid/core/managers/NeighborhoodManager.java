package unife.icedroid.core.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import unife.icedroid.core.HostInfo;
import unife.icedroid.core.NeighborInfo;

public class NeighborhoodManager {

    private volatile static NeighborhoodManager instance;

    private Timer neighborhoodManagerTimer;
    private ArrayList<NeighborInfo> neighborsList;


    private NeighborhoodManager() {
        neighborhoodManagerTimer = new Timer();
        neighborsList = new ArrayList<NeighborInfo>(0);
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

    public synchronized boolean add(HostInfo hostInfo) {
        boolean newNeighbor = false;
        NeighborInfo neighborInfo = contains(hostInfo.getHostID());

        if (neighborInfo != null) {
            neighborInfo.setSubscriptionsList(hostInfo.getSubscriptionsList());
            neighborInfo.setCachedMessages(hostInfo.getCachedMessages());
            neighborInfo.setLastTimeSeen();
        } else {
            neighborInfo = new NeighborInfo(hostInfo);
            neighborsList.add(neighborInfo);
        }

        NeighborRemoveTask task = new NeighborRemoveTask(this, new NeighborInfo(neighborInfo));
        neighborhoodManagerTimer.schedule(task, new Date(neighborInfo.getLastTimeSeen() + 60 * 1000));

        return newNeighbor;
    }

    public synchronized void remove(NeighborInfo neighborInfo) {
        neighborsList.remove(neighborInfo);
    }

    private synchronized NeighborInfo contains(String hostID) {
        for (NeighborInfo nb : neighborsList) {
            if (nb.getHostID() == hostID) {
                return nb;
            }
        }
        return  null;
    }

    private class NeighborRemoveTask extends TimerTask {

        private NeighborhoodManager neighborhoodManager;
        private NeighborInfo neighborInfo;

        public NeighborRemoveTask(NeighborhoodManager neighborhoodManager,
                                                                        NeighborInfo neighborInfo) {
            this.neighborhoodManager = neighborhoodManager;
            this.neighborInfo = neighborInfo;
        }

        @Override
        public void run() {
            neighborhoodManager.remove(neighborInfo);
        }

    }

}
