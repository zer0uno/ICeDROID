package unife.icedroid.core;

import java.io.Serializable;

public class Message implements Serializable {
    //campo statico che ricorda l'id, problema se l'app viene riavviata, da dove riprendere?

    /** Type of message (Hello or Regular) */
    private String typeOfMsg;
    /** Message subscription membership */
    private Subscription subscription;
    /** Message IDentifier */
    private int msgID;
    /** Time when the message was created */
    private double timeCreated;
    /** Time to live of the message */
    private double ttl;
    /** Message priority */
    private int priority;
    /** The entire message size, including header and data, in bytes */
    private int size;
    /** The message data */
    private String data;


    public String getTypeOfMsg() {
        return typeOfMsg;
    }

    public void setTypeOfMsg(String type) {
        this.typeOfMsg = type;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription.copy(subscription);
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public boolean equals(Message msg) {
        return (this.subscription.equals(msg.subscription) && this.msgID == msg.getMsgID()) ? true : false;
    }

}
