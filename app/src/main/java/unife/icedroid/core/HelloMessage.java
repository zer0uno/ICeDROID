package unife.icedroid.core;

import java.io.Serializable;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.SubscriptionListManager;
import unife.icedroid.utils.Settings;

public class HelloMessage implements TypeOfMessage, Serializable {

    private String typeOfMsg;
    private HostInfo hostInfo;


    public HelloMessage() {
        typeOfMsg = "hello";
        hostInfo = new HostInfo(Settings.HOST_IP,
                                SubscriptionListManager.getSubscriptionListManager().
                                                                            getSubscriptionsList(),
                                MessageQueueManager.getMessageQueueManager().getCachedMessages());
    }

    public String getTypeOfMsg() {
        return typeOfMsg;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

}