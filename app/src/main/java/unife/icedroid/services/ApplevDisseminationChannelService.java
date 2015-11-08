package unife.icedroid.services;

import java.util.ArrayList;
import java.util.Random;
import android.app.IntentService;
import android.content.Intent;
import unife.icedroid.core.Constants;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.managers.*;

public class ApplevDisseminationChannelService extends IntentService {
    private static final String TAG = "AppDissChannelService";

    public static final double CACHING_PROBABILITY = 0.1;
    public static final double FORWARD_PROBABILITY = 0.3;

    private MessageQueueManager messageQueueManager;
    private SubscriptionListManager subscriptionListManager;
    private NeighborhoodManager neighborhoodManager;


    public ApplevDisseminationChannelService() {
        super(TAG);
        setIntentRedelivery(true);

        messageQueueManager = MessageQueueManager.getMessageQueueManager();
        subscriptionListManager = SubscriptionListManager.getSubscriptionListManager();
        neighborhoodManager = NeighborhoodManager.getNeighborhoodManager();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RegularMessage regularMessage = (RegularMessage) intent.getSerializableExtra(Constants.
                                                                                EXTRA_ADC_MESSAGE);

        //There's a new regular message, first it must be decided whether to cache or not
        //and following whether to forward it or not
        if (regularMessage != null) {
            boolean toCache = true;

            if (! messageQueueManager.isCached(regularMessage) &&
                ! messageQueueManager.isDiscarded(regularMessage)) {

                if (subscriptionListManager.isSubscribedToMessage(regularMessage)) {
                    /**
                     * TODO
                     * inviare il messaggio al componente per la visualizzazione dei nuovi
                     * messaggi, il quale dovrà controllare che il messaggio non sia quello mio che
                     * ho creato prima
                    */
                } else if (! subscriptionListManager.isSubscribedToChannel(regularMessage)) {
                    Random random = new Random(System.currentTimeMillis());
                    if (random.nextDouble() > CACHING_PROBABILITY) {
                        toCache = false;
                        messageQueueManager.addToDiscarded(regularMessage);
                    }
                }

                if (toCache) {
                    messageQueueManager.addToCache(regularMessage);
                    forwardMessage(regularMessage);
                }

            }

        }
        //There's a new neighbor, so it must be checked which messages should be forwarded
        // and which not
        else {
            boolean newNeighbor = intent.getBooleanExtra(Constants.EXTRA_NEW_NEIGHBOR, false);

            if(newNeighbor) {
                messageQueueManager.removeRegularMessagesFromForwardingMessages();
                ArrayList<RegularMessage> cachedMessages = messageQueueManager.getCachedMessages();

                synchronized (cachedMessages) {
                    for (RegularMessage msg : cachedMessages) {
                        forwardMessage(msg);
                    }
                }
            }
        }

    }

    private void forwardMessage(RegularMessage msg) {
        boolean send = false;

        if (neighborhoodManager.isThereNeighborInterestedToMessage(msg)) {
            send = true;
        } else if (neighborhoodManager.isThereNeighborSubscribedToChannel(
                                                    msg.getSubscription().getChannelID())) {
            send = true;
        } else {
            Random random = new Random(System.currentTimeMillis());
            if (random.nextDouble() <= FORWARD_PROBABILITY) {
                send = true;
            }
        }

        if (send) {
            messageQueueManager.addToForwardingMessages(msg);
        }

    }

}