package unife.icedroid.core.managers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.RegularMessage;

public class MessageQueueManager {

    private volatile static MessageQueueManager instance;

    private ArrayList<RegularMessage> cachedMessages;
    private ArrayList<RegularMessage> discardedMessages;
    private ArrayList<RegularMessage> forwardingMessages;
    private int index;

    private Timer cachedMessagesTimer;
    private Timer discardedMessagesTimer;


    private MessageQueueManager() {
        cachedMessages = new ArrayList<>(0);
        discardedMessages = new ArrayList<>(0);
        forwardingMessages = new ArrayList<>(0);
        index = 0;

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
        forwardingMessages.add(0, helloMessage);
    }

    public synchronized void send(Message message) {
        //aggiungere il messaggio nella coda secondo determinate politiche
        //FIFO
        forwardingMessages.add(message);
    }

    public synchronized void updateForwardingMessages() {
        //ricontrollare per tutti i messaggi se dagli hellomessagge risulta che tutti ce l'hanno
    }

    public synchronized TypeOfMessage getMessage() {
        TypeOfMessage message;
        if (index < forwardingMessages.size()) {
            message = forwardingMessages.get(index);
            index++;
        } else {
            message = forwardingMessages.get(0);
            index = 1;
        }
        return message;
    }

    public synchronized void removeCachedMessage(Message msg) {
        cachedMessages.remove(msg);

        for (int i=0; i<forwardingMessages.size(); i++) {
            if (forwardingMessages.get(i).getTypeOfMsg() == msg.getTypeOfMsg()) {
                Message m = (Message) forwardingMessages.get(i);
                if (msg.equals(m)) {
                    forwardingMessages.remove(i);
                    break;
                }
            }
        }
    }

    public synchronized void removeDiscardedMessage(Message msg) {
        discardedMessages.remove(msg.getMessageIdentity());
    }

    public synchronized void eraseForwardingMessages() {
        forwardingMessages = new ArrayList<TypeOfMessage>(0);
    }
}
