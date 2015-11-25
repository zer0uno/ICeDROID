package unife.icedroid.core.messagecachingstrategies;

import unife.icedroid.core.Message;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.utils.Settings;
import java.util.ArrayList;

public abstract class MessageCachingStrategy {

    public static MessageCachingStrategy newInstance(Settings s) {
        int cacheSize = s.getCacheSize()*1000000;
        switch (s.getCachingStrategy()) {
            case FIFO:
                return new FIFOStrategy(cacheSize);
            case RANDOM:
                return new RANDOMStrategy(cacheSize);
            default:
                return new FIFOStrategy(cacheSize);
        }
    }

    public abstract void add(ArrayList<RegularMessage> list, RegularMessage msg);

    protected int getListSize(ArrayList<? extends Message> list) {
        int size = 0;

        for (Message m : list) {
            size += m.getSize();
        }
        return size;
    }
}
