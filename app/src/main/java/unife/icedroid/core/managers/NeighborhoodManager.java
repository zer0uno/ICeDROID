package unife.icedroid.core.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import unife.icedroid.core.NeighborInfo;

public class NeighborhoodManager {

    private volatile static NeighborhoodManager instance;
    private static long ttlOfNeighbor = 35*1000;

    private ArrayList<NeighborInfo> neighborsList;
    private Timer neighborhoodManagerTimer;


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

        return newNeighbor;
    }

    public synchronized void remove(NeighborInfo neighbor) {
        neighborsList.remove(neighbor);
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
