package unife.icedroid.core.messageforwardingstrategies;

import unife.icedroid.core.HelloMessage;
import unife.icedroid.core.Message;
import java.util.ArrayList;

public class FIFOStrategy extends MessageForwardingStrategy {

    @Override
    public void add(ArrayList<Message> list, Message msg, int index) {
        if (msg.getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
            removeHelloMessage(list);
        }
        list.add(msg);
    }

    private void removeHelloMessage(ArrayList<Message> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTypeOfMessage().equals(HelloMessage.HELLO_MESSAGE)) {
                    list.remove(i);
                    break;
                }
            }
    }
}
