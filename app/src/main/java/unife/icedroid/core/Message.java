package unife.icedroid.core;

public class Message extends MessageIdentity implements TypeOfMessage {
    //campo statico che ricorda l'id, problema se l'app viene riavviata, da dove riprendere?

    /** Type of message (Hello or Regular) */
    private String typeOfMsg;
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


    public Message(Subscription sub, int msg) {
        super(sub, msg);
    }

    public String getTypeOfMsg() {
        return typeOfMsg;
    }

    public MessageIdentity getMessageIdentity() {

        return new MessageIdentity(getSubscription(), getMsgID());

    }

}
