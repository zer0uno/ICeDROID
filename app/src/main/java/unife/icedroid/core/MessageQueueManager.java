package unife.icedroid.core;

import java.util.ArrayList;

public class MessageQueueManager {

    private volatile static MessageQueueManager instance;

    public static double CACHING_PROBABILITY = 0.1;

    public static double FORWARD_PROBABILITY = 0.3;


    private ArrayList<Message> cachedMessage;

    private ArrayList<Message> incomingMessagesDecisionTable;


    private MessageQueueManager() {
        cachedMessage = new ArrayList<Message>();
        incomingMessagesDecisionTable = new ArrayList<Message>();
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

    public void addToCache(Message msg) {
        //Da modificare, aggiungere secondo certe politiche
        cachedMessage.add(msg);
    }

    public boolean isCached(Message msg) {
        return cachedMessage.contains(msg);
    }

    public boolean isAlreadyDecided(Message msg) {
        return incomingMessagesDecisionTable.contains(msg);
    }
}
