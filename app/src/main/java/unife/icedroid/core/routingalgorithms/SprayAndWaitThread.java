package unife.icedroid.core.routingalgorithms;

import android.app.Service;
import android.content.Intent;
import unife.icedroid.core.Message;
import unife.icedroid.core.NeighborInfo;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;
import unife.icedroid.services.ApplevDisseminationChannelService;
import java.util.ArrayList;

public class SprayAndWaitThread implements Runnable {

    private Service service;
    private int startID;
    private RegularMessage message;

    public SprayAndWaitThread(Service service, int startID, RegularMessage msg) {
        this.service = service;
        this.startID = startID;
        message = msg;
    }

    @Override
    public void run() {
        NeighborhoodManager neighborhoodManager = NeighborhoodManager.getNeighborhoodManager();
        float numberOfNeighbors = neighborhoodManager.getNumberOfNeighbors();
        int L = 1; /** L può anche essere determinato in modo dinamico */
        Intent intent = new Intent(service, ApplevDisseminationChannelService.class);

        if (numberOfNeighbors == 0) {
            //If there is no neighbor just cache the message
            intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE, message);
            service.startService(intent);
        }

        ArrayList<NeighborInfo> ackList = new ArrayList<>(0);
        long lastUpdate = 0;

        while (L > 0 && !isExpired(message)) {

            for (NeighborInfo neighbor : neighborhoodManager.
                    whoHasThisMessageButNotInterested(message)) {
                if (!ackList.contains(neighbor)) {
                    L = (int) Math.ceil(L / 2);
                    ackList.add(neighbor);
                    if (L <= 0) {
                        break;
                    }
                }
            }
            if (L > 0) {
                if (neighborhoodManager.isThereNeighborInterestedToMessage(message)) {
                    message.setProperty("L", 0);
                    intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE, message);
                    service.startService(intent);
                } else {
                    if (neighborhoodManager.
                            isThereNeighborNotInterestedToMessageAndNotCached(message)) {
                        message.setProperty("L", L);
                        intent.putExtra(ApplevDisseminationChannelService.EXTRA_ADC_MESSAGE,
                                                                                        message);
                        service.startService(intent);
                    }
                }
                neighborhoodManager.isThereAnUpdate(lastUpdate);
                lastUpdate = neighborhoodManager.getLastUpdate();
            }
        }

        message.setProperty("L", 0);
        MessageQueueManager messageQueueManager = MessageQueueManager.getMessageQueueManager();
        messageQueueManager.removeMessageFromForwardingMessages(message);
        messageQueueManager.removeMessageFromCachedMessages(message);
        messageQueueManager.addToCache(message);

        service.stopSelf(startID);
    }

    private boolean isExpired(Message msg) {
        if (msg.getTtl() != Message.INFINITE_TTL) {
            if (msg.getCreationTime().getTime() + msg.getTtl() >= System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }
}