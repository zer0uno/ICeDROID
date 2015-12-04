package unife.icedroid.core;

import android.content.Context;

import unife.icedroid.core.managers.ChannelListManager;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;
import unife.icedroid.core.routingalgorithms.SprayAndWaitThread;
import unife.icedroid.exceptions.WifiAdhocImpossibleToEnable;
import unife.icedroid.utils.Settings;

public class ICeDROID {
    private static ICeDROID instance;

    private Context context;
    private ChannelListManager channelListManager;
    private NeighborhoodManager neighborhoodManager;
    private MessageQueueManager messageQueueManager;
    private Thread routingThread;

    private ICeDROID(Context context) throws WifiAdhocImpossibleToEnable{
        this.context = context.getApplicationContext();
        if (Settings.getSettings(context) != null) {
            channelListManager = ChannelListManager.getChannelListManager();
            neighborhoodManager = NeighborhoodManager.getNeighborhoodManager();
            messageQueueManager = MessageQueueManager.getMessageQueueManager();
        } else {
            throw new WifiAdhocImpossibleToEnable();
        }
    }

    public static ICeDROID getInstance(Context context) {
        if (instance == null) {
            synchronized (ICeDROID.class) {
                if (instance == null) {
                    try {
                        instance = new ICeDROID(context);
                    } catch (WifiAdhocImpossibleToEnable ex) {
                        instance = null;
                    }
                }
            }
        }
        return instance;
    }

    public static synchronized ICeDROID getInstance() {
        return instance;
    }

    public void subscribe(String channel) {
        channelListManager.subscribe(channel);
    }

    public void send(ICeDROIDMessage message) {
        switch (Settings.getSettings().getRoutingAlgorithm()) {
            case SPRAY_AND_WAIT:
                boolean added = false;
                while (!added) {
                    try {
                        added = ((SprayAndWaitThread) routingThread).add(message);
                    } catch (Exception ex) {}

                    if (!added) {
                        routingThread = new SprayAndWaitThread();
                        routingThread.start();
                    }
                }
                break;
            default:
                break;
        }
    }
}
