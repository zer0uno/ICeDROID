package unife.icedroid.core;

import java.io.Serializable;

public class Message implements TypeOfMessage, Serializable {
    //campo statico che ricorda l'id, problema se l'app viene riavviata, da dove riprendere?

    /** Type of message (Hello or Regular) */
    private final String typeOfMsg = "regular";
    /** Message Identity */
    private MessageIdentity identity;
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

    public MessageIdentity getMessageIdentity() {
        return identity;
    }

    public Subscription getSubscription() {
        return identity.getSubscription();
    }

    public boolean equals(Message msg) {
        return identity.equals(msg.identity);
    }

}
