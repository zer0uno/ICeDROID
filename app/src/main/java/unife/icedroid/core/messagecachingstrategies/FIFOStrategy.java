package unife.icedroid.core.messagecachingstrategies;

import unife.icedroid.core.RegularMessage;
import java.util.ArrayList;

public class FIFOStrategy extends MessageCachingStrategy {

    //In bytes
    private int cacheSize;

    FIFOStrategy(int size) {
        cacheSize = size;
    }

    @Override
    public void add(ArrayList<RegularMessage> list, RegularMessage msg) {
        int msgSize = msg.getSize();
        while (getListSize(list) + msgSize > cacheSize) {
            list.remove(0);
        }
        list.add(msg);
    }
}
