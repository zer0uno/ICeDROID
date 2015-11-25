package unife.icedroid.core.managers;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.utils.Settings;
import unife.icedroid.core.messagecachingstrategies.MessageCachingStrategy;
import unife.icedroid.core.messageforwardingstrategies.MessageForwardingStrategy;
import java.util.*;

public class MessageQueueManager {
    /**
     * TODO
     * Ritornare sempre delle copie
     */
    private static final String TAG = "MessageQueueManager";
    private static final boolean DEBUG = true;

    private volatile static MessageQueueManager instance;

    private ArrayList<RegularMessage> cachedMessages;
    private ArrayList<RegularMessage> discardedMessages;
    private ArrayList<Message> forwardingMessages;
    private Timer cachedMessagesTimer;
    private Timer discardedMessagesTimer;
    private MessageCachingStrategy cachingStrategy;
    private MessageForwardingStrategy forwardingStrategy;

    private static int indexForwardingMessages;


    private MessageQueueManager() {
        cachedMessages = new ArrayList<>(0);
        discardedMessages = new ArrayList<>(0);
        forwardingMessages = new ArrayList<>(0);
        indexForwardingMessages = 0;

        cachedMessagesTimer = new Timer();
        discardedMessagesTimer = new Timer();

        cachingStrategy = MessageCachingStrategy.newInstance(Settings.getSettings());
        forwardingStrategy = MessageForwardingStrategy.newInstance(Settings.getSettings());
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

    public boolean isCached(RegularMessage msg) {
        synchronized (cachedMessages) {
            return cachedMessages.contains(msg);
        }
    }

    public
    boolean isDiscarded(RegularMessage msg) {
        synchronized (discardedMessages) {
            return discardedMessages.contains(msg);
        }
    }

    public void addToCache(final RegularMessage msg) {
        if (!isExpired(msg)) {
            synchronized (cachedMessages) {
                cachingStrategy.add(cachedMessages, msg);
            }

            //If the msg TTL isn't infinite then set a timer to delete it.
            if (msg.getTtl() != Message.INFINITE_TTL) {
                cachedMessagesTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        removeFromQueue(cachedMessages, msg);
                    }

                }, new Date(msg.getCreationTime().getTime() + msg.getTtl()));
            }
        }
    }

    public void addToDiscarded(final RegularMessage msg) {
        if (!isExpired(msg)) {

            msg.setContentData(null);
            synchronized (discardedMessages) {
                discardedMessages.add(msg);
            }

            //If the msg TTL isn't infinite then set a timer to delete it.
            if (msg.getTtl() != Message.INFINITE_TTL) {
                discardedMessagesTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        removeFromQueue(discardedMessages, msg);
                    }

                }, new Date(msg.getCreationTime().getTime() + msg.getTtl()));
            }
        }
    }

    public void addToForwardingMessages(Message msg) {
        synchronized (forwardingMessages) {
            forwardingStrategy.add(forwardingMessages, msg, indexForwardingMessages);
            forwardingMessages.notifyAll();
        }
    }

    public ArrayList<RegularMessage> getCachedMessages() {
        synchronized (cachedMessages) {
            return new ArrayList(cachedMessages);
        }
    }

    public ArrayList<RegularMessage> getDiscardedMessages() {
        synchronized (discardedMessages) {
            return new ArrayList<>(discardedMessages);
        }
    }

    public ArrayList<RegularMessage> getCachedAndDiscardedMessages() {
        synchronized (cachedMessages) {
            synchronized (discardedMessages) {
                return joinArrayLists(cachedMessages, discardedMessages);
            }
        }
    }

    public void removeFromQueue(ArrayList<?> queue, Message msg) {
        synchronized (queue) {
            queue.remove(msg);
        }
    }

    public void removeMessageFromCachedMessages(RegularMessage msg) {
        removeFromQueue(cachedMessages, msg);
    }

    public void removeMessageFromForwardingMessages(Message msg) {
       removeFromQueue(forwardingMessages, msg);
    }

    public void removeRegularMessagesFromForwardingMessages() {
        synchronized (forwardingMessages) {
            Message helloMessage = null;
            //Save the hello message, if it is present
            for (Message msg : forwardingMessages) {
                if (msg.getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
                    helloMessage = msg;
                    break;
                }
            }

            forwardingMessages = new ArrayList<>(0);
            //If there was an hello message then re-insert it
            if (helloMessage != null) {
                forwardingMessages.add(helloMessage);
            }
        }
    }

    public Message getMessageToSend() throws InterruptedException {
        Settings s = Settings.getSettings();
        Message message = null;
        synchronized (forwardingMessages) {
            while (message == null) {
                while (forwardingMessages.size() == 0) {
                    try {
                        forwardingMessages.wait();
                    } catch (InterruptedException ex) {
                        throw ex;
                    }
                }

                if (indexForwardingMessages >= forwardingMessages.size()) {
                    indexForwardingMessages = 0;
                }

                message = forwardingMessages.get(indexForwardingMessages);

                if (isExpired(message)) {
                    forwardingMessages.remove(indexForwardingMessages);
                    message = null;
                } else {
                    indexForwardingMessages++;
                }
            }
        }
        return message;
    }

    private boolean isExpired(Message msg) {
        if (msg.getTtl() != Message.INFINITE_TTL) {
            if (msg.getCreationTime().getTime() + msg.getTtl() < System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

    private <T> ArrayList<T> joinArrayLists(ArrayList<T> listOne, ArrayList<T> listTwo) {
        ArrayList<T> jointList = new ArrayList<>(listOne);
        for (T item : listTwo) {
            jointList.add(item);
        }
        return jointList;
    }

}