package unife.icedroid.core.messageforwardingstrategies;

import unife.icedroid.core.Message;
import unife.icedroid.utils.Settings;
import java.util.ArrayList;

public abstract class MessageForwardingStrategy {

    public static MessageForwardingStrategy newInstance(Settings s) {
        switch (s.getForwardingStrategy()) {
            case FIFO:
                return new FIFOStrategy();
            case PRIORITY:
                return new PRIORITYStrategy();
            default:
                return new FIFOStrategy();
        }
    }

    public abstract void add(ArrayList<Message> list, Message msg, int index);
}