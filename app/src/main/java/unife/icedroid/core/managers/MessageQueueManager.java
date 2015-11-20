package unife.icedroid.core.managers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import android.util.Log;
import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.utils.Settings;

public class MessageQueueManager {
    private final static String TAG = "MessageQueueManager";

    private volatile static MessageQueueManager instance;

    private ArrayList<RegularMessage> cachedMessages;
    private ArrayList<RegularMessage> discardedMessages;
    private ArrayList<Message> forwardingMessages;
    private Timer cachedMessagesTimer;
    private Timer discardedMessagesTimer;

    private static int indexForwardingMessages;


    private MessageQueueManager() {
        cachedMessages = new ArrayList<>(0);
        discardedMessages = new ArrayList<>(0);
        forwardingMessages = new ArrayList<>(0);
        indexForwardingMessages = 0;

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
        /**
         * TODO
         * Aggiungere politiche di caching
        */
        if (!isExpired(msg)) {
            synchronized (cachedMessages) {
                cachedMessages.add(msg);
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
            //Hello Messages have the highest priority.
            //There can't be two hello messages in the forwarding queue, so it must be checked
            //if there is one, if there is it must be removed and substituted.
            if (msg.getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
                removeHelloMessageFromForwardingMessages();
                if (indexForwardingMessages >= forwardingMessages.size()) {
                    forwardingMessages.add(0, msg);
                } else {
                    forwardingMessages.add(indexForwardingMessages, msg);
                }

            } else {
                /**
                 * TODO
                 * Implementare delle politiche di forwarding
                */
                forwardingMessages.add(msg);
            }
            forwardingMessages.notifyAll();
        }
    }

    public ArrayList<RegularMessage> getCachedMessages() {
        return cachedMessages;
    }

    public void removeFromQueue(ArrayList<RegularMessage> queue, RegularMessage msg) {
        synchronized (queue) {
            queue.remove(msg);
        }
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

    public void removeHelloMessageFromForwardingMessages() {
        synchronized (forwardingMessages) {
            for (int i = 0; i < forwardingMessages.size(); i++) {
                if (forwardingMessages.get(i).getTypeOfMessage().equals(
                                                                    HelloMessage.HELLO_MESSAGE)) {
                    forwardingMessages.remove(i);
                    break;
                }
            }
        }
    }

    public byte[] getMessageToSend() throws InterruptedException {
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
                }
                indexForwardingMessages++;
            }
        }
        byte[] messageToArray = null;
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(Settings.MSG_SIZE);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayInputStream);
            objectOutputStream.writeObject(message);
            messageToArray = byteArrayInputStream.toByteArray();
        } catch (IOException ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg != null) ? msg : "Impossible to convert to byte: " + message);
        }

        return messageToArray;
    }

    private boolean isExpired(Message msg) {
        if (msg.getTtl() != Message.INFINITE_TTL) {
            if (msg.getCreationTime().getTime() + msg.getTtl() >= System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

}