package unife.icedroid.services;

import android.app.IntentService;
import android.content.Intent;
import unife.icedroid.core.NeighborInfo;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.managers.*;
import unife.icedroid.utils.Settings;
import java.util.ArrayList;
import java.util.Random;

public class ApplevDisseminationChannelService extends IntentService {
    private static final String TAG = "AppDissChannelService";
    private static final boolean DEBUG = true;

    public static final String EXTRA_ADC_MESSAGE = "unife.icedroid.ADC_MESSAGE";
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
        RegularMessage regularMessage =
                                (RegularMessage) intent.getSerializableExtra(EXTRA_ADC_MESSAGE);

        //There's a new regular message, first it must be decided whether to cache or not
        //and following whether to forward it or not
        if (regularMessage != null) {
            //This host's messages
            if (regularMessage.getHostID().equals(Settings.getSettings().getHostID())) {
                switch (Settings.getSettings().getRoutingAlgorithm()) {
                    case SPRAY_AND_WAIT:
                        messageQueueManager.removeMessageFromForwardingMessages(regularMessage);
                        messageQueueManager.removeMessageFromCachedMessages(regularMessage);
                        forwardMessage(regularMessage, true);
                        messageQueueManager.addToCache(regularMessage);
                        break;
                    default:
                        break;
                }

            } else {
                //Other hosts' messages
                boolean toCache = true;

                if (!messageQueueManager.isCached(regularMessage) &&
                        !messageQueueManager.isDiscarded(regularMessage)) {

                    if (subscriptionListManager.isSubscribedToMessage(regularMessage)) {
                        ChatsManager.saveMessageInConversation(getFilesDir().getAbsolutePath(),
                                regularMessage);
                    } else if (!subscriptionListManager.isSubscribedToChannel(regularMessage)) {
                        switch (Settings.getSettings().getRoutingAlgorithm()) {
                            case SPRAY_AND_WAIT:
                                Integer L = regularMessage.getProperty("L");
                                if (L == null || L <= 0) {
                                    Random random = new Random(System.currentTimeMillis());
                                    if (random.nextDouble() > CACHING_PROBABILITY) {
                                        toCache = false;
                                        messageQueueManager.addToDiscarded(regularMessage);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    if (toCache) {
                        messageQueueManager.addToCache(regularMessage);
                        forwardMessage(regularMessage, false);
                    }
                }
            }

        }
        //There's a new neighbor, so it must be checked which messages should be forwarded
        // and which not
        else {
            boolean newNeighbor =
                            intent.getBooleanExtra(NeighborInfo.EXTRA_NEW_NEIGHBOR, false);

            if(newNeighbor) {
                messageQueueManager.removeRegularMessagesFromForwardingMessages();
                ArrayList<RegularMessage> cachedMessages = messageQueueManager.getCachedMessages();

                synchronized (cachedMessages) {
                    for (RegularMessage msg : cachedMessages) {
                        forwardMessage(msg, false);
                    }
                }
            }
        }

    }

    private void forwardMessage(RegularMessage msg, boolean thisHostMessage) {
        boolean send = false;
        switch (Settings.getSettings().getRoutingAlgorithm()) {
            case SPRAY_AND_WAIT:
                //If the message is new from this host
                if (thisHostMessage) {
                    if (neighborhoodManager.isThereNeighborInterestedToMessage(msg)) {
                        send = true;
                    } else if (neighborhoodManager.isThereNeighborSubscribedToChannel(msg)) {
                        send = true;
                    } else if (neighborhoodManager.isThereNeighborWithoutThisMessage(msg)) {
                        Random random = new Random(System.currentTimeMillis());
                        if (random.nextDouble() <= FORWARD_PROBABILITY) {
                            send = true;
                        }
                    }
                } else {
                    //If we are not in the spraying phase or the message is not new,
                    //then according to the Spray and Wait Algorithm the message must be
                    //delivered only if there is a direct interested neighbor.
                    if (neighborhoodManager.isThereNeighborInterestedToMessage(msg)) {
                        send = true;
                    }
                }
                break;
            default:
                /** Here to handle more routing algorithms' philosophies **/
                break;
        }

        if (send) {
            messageQueueManager.addToForwardingMessages(msg);
        }

    }
}