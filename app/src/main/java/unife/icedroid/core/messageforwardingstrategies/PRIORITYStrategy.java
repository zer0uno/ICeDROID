package unife.icedroid.core.messageforwardingstrategies;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import unife.icedroid.core.RegularMessage;

import java.util.ArrayList;

public class PRIORITYStrategy extends MessageForwardingStrategy {

    @Override
    public void add(ArrayList<Message> list, Message msg, int indexForwardingMessages) {
        if (msg.getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
            int helloMessageIndex = getHelloMessageIndex(list);
            if (helloMessageIndex != -1) {
                list.add(helloMessageIndex, msg);
                list.remove(helloMessageIndex + 1);
                return;
            }
        } else {
            RegularMessage regularMessage = (RegularMessage) msg;
            if (regularMessage.getSubscription().getChannelID().equals("Sport")) {
                if (indexForwardingMessages >= list.size()) {
                    indexForwardingMessages = 0;
                }
                int i;
                for (i = indexForwardingMessages; i < list.size(); i++) {
                    Message m = list.get(i);
                    if (m.getTypeOfMessage().equals(RegularMessage.REGULAR_MESSAGE)) {
                        RegularMessage rm = (RegularMessage) m;
                        if (!rm.getSubscription().getChannelID().equals("Sport")) {
                            break;
                        }
                    }
                }
                if (i < list.size()) {
                    list.add(i, regularMessage);
                    return;
                }
            }
        }
        list.add(msg);
    }

    private int getHelloMessageIndex(ArrayList<Message> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
                return i;
            }
        }
        return -1;
    }
}
