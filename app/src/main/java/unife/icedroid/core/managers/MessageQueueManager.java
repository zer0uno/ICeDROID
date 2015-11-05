package unife.icedroid.core.managers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.MessageIdentity;

public class MessageQueueManager {

    private volatile static MessageQueueManager instance;

    private ArrayList<Message> cachedMessages;
    private ArrayList<MessageIdentity> discardedMessages;
    private ArrayList<Message> forwardingMessages;

    private Timer cachedMessagesTimer;
    private Timer discardedMessagesTimer;


    private MessageQueueManager() {
        cachedMessages = new ArrayList<Message>(0);
        discardedMessages = new ArrayList<MessageIdentity>(0);
        forwardingMessages = new ArrayList<Message>(0);

        cachedMessagesTimer = new Timer();
        discardedMessagesTimer = new Timer();
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

    public synchronized ArrayList<MessageIdentity> getCachedMessagesIdentities() {
        ArrayList<MessageIdentity> messagesIdentities = new ArrayList<MessageIdentity>(0);
        for (Message msg : cachedMessages) {
            messagesIdentities.add(msg.getMessageIdentity());
        }
        return messagesIdentities;
    }

    public synchronized void addToCache(final Message msg) {
        //Devo aggiornare la lista dei messaggi da inviare?
        //Da modificare, aggiungere secondo certe politiche
        cachedMessages.add(msg);

        Date expirationTime = new Date(msg.getTimeCreated() + msg.getTtl());
        cachedMessagesTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                removeCachedMessage(msg);
            }

        }, expirationTime);
    }

    public synchronized void addToDiscarded(final Message msg) {
        discardedMessages.add(msg.getMessageIdentity());

        Date expirationTime = new Date(msg.getTimeCreated() + msg.getTtl());
        discardedMessagesTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                removeDiscardedMessage(msg);
            }

        }, expirationTime);
    }

    public synchronized boolean isCached(Message msg) {
        return cachedMessages.contains(msg);
    }

    public synchronized boolean isDiscarded(Message msg) {
        return discardedMessages.contains(msg.getMessageIdentity());
    }

    public synchronized void send(HelloMessage helloMessage) {

    }

    public synchronized void send(Message message) {

    }

    public synchronized void removeCachedMessage(Message msg) {
        cachedMessages.remove(msg);
    }

    public synchronized void removeDiscardedMessage(Message msg) {
        discardedMessages.remove(msg.getMessageIdentity());
    }

    public synchronized void eraseForwardingMessages() {
        forwardingMessages = new ArrayList<Message>(0);
    }
}
