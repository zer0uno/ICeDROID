package unife.icedroid.core.managers;

import java.util.ArrayList;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.MessageIdentity;

public class MessageQueueManager {

    private volatile static MessageQueueManager instance;

    public static double CACHING_PROBABILITY = 0.1;
    public static double FORWARD_PROBABILITY = 0.3;

    private ArrayList<MessageIdentity> cachedMessages;
    private ArrayList<Message> incomingMessagesDecisionTable;
    private String forwordingDecisionTable;


    private MessageQueueManager() {
        cachedMessages = new ArrayList<MessageIdentity>(0);
        incomingMessagesDecisionTable = new ArrayList<Message>(0);
    }

    public static MessageQueueManager getMessageQueueManager() {

        if (instance == null) {
            synchronized (MessageQueueManager.class) {
                if (instance == null) {
                    instance = new MessageQueueManager();
                }
            }
        }
        return instance;

    }

    public ArrayList<MessageIdentity> getCachedMessages() {
        return cachedMessages;
    }

    public void addToCache(Message msg) {
        //Da modificare, aggiungere secondo certe politiche
        cachedMessages.add(msg.getMessageIdentity());
    }

    public boolean isCached(Message msg) {
        return cachedMessages.contains(msg);
    }

    public boolean isAlreadyDecided(Message msg) {
        return incomingMessagesDecisionTable.contains(msg);
    }

    public void send(HelloMessage helloMessage) {

    }

    public void send(Message message) {

    }

    public void eraseForwardingDecisionTable() {

    }
}
