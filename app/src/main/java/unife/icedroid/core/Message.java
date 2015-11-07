package unife.icedroid.core;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private static int counterOfmsgID;

    private String typeOfMessage;
    private String hostID;
    private String hostUsername;
    private int msgID;
    private Date creationTime;
    private Date receptionTime;
    private long ttl;
    private int size;


    public String getTypeOfMessage() {
        return typeOfMessage;
    }

    public String getHostID() {
        return hostID;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public int getMsgID() {
        return msgID;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getReceptionTime() {
        return receptionTime;
    }

    public long getTtl() {
        return ttl;
    }

    public int getSize() {
        return size;
    }

    public void setTypeOfMessage(String type) {
        typeOfMessage = type;
    }

    public void setHostID(String id) {
        hostID = id;
    }

    public void setHostUsername(String username) {
        hostUsername = username;
    }

    public void setMsgID() {
        msgID = counterOfmsgID;
        incrementCounterOfmsgID();
    }

    public void setCreationTime(Date time) {
        creationTime = time;
    }

    public void setReceptionTime(Date time) {
        receptionTime = time;
    }

    public void setTtl(long period) {
        ttl = period;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void resetCounterOfmsgID() {
        counterOfmsgID = 0;
    }

    private void incrementCounterOfmsgID() {
        if(counterOfmsgID == Integer.MAX_VALUE) {
            resetCounterOfmsgID();
        } else {
            counterOfmsgID++;
        }
    }

}
