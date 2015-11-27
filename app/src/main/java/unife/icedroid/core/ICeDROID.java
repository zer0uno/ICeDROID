package unife.icedroid.core;

import android.content.Context;
import android.content.Intent;
import unife.icedroid.core.managers.ChannelListManager;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;
import unife.icedroid.services.RoutingService;
import unife.icedroid.utils.Settings;

public class ICeDROID {
    private static ICeDROID instance;

    private Context context;
    private ChannelListManager channelListManager;
    private NeighborhoodManager neighborhoodManager;
    private MessageQueueManager messageQueueManager;

    private ICeDROID(Context context) {
        this.context = context.getApplicationContext();
        Settings.getSettings(context);
        channelListManager = ChannelListManager.getChannelListManager();
        neighborhoodManager = NeighborhoodManager.getNeighborhoodManager();
        messageQueueManager = MessageQueueManager.getMessageQueueManager();
    }

    public static ICeDROID getInstance(Context context) {
        if (instance == null) {
            synchronized (ICeDROID.class) {
                if (instance == null) {
                    instance = new ICeDROID(context);
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
        Intent intent = new Intent(context, RoutingService.class);
        intent.putExtra(RoutingService.EXTRA_NEW_MESSAGE, message);
        context.startService(intent);
    }
}
