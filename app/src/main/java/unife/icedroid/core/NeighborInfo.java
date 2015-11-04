package unife.icedroid.core;

public class NeighborInfo extends HostInfo {

    private long lastTimeSeen;

    public NeighborInfo(HostInfo hostInfo) {
        super(hostInfo);
        setLastTimeSeen();
    }

    public NeighborInfo(NeighborInfo neighborInfo) {
        super(neighborInfo);
        lastTimeSeen = neighborInfo.lastTimeSeen;
    }

    public long getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen() {
        lastTimeSeen = System.currentTimeMillis();
    }

    public boolean equals(NeighborInfo o) {
        return super.equals(o) && (lastTimeSeen == o.lastTimeSeen);
    }

}
