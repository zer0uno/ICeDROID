package unife.icedroid.services;

import java.util.*;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import unife.icedroid.core.*;
import unife.icedroid.core.managers.MessageQueueManager;
import unife.icedroid.core.managers.NeighborhoodManager;

public class HelloMessageService extends IntentService{
    private static final String TAG = "HelloMessageService";

    private MessageQueueManager messageQueueManager;
    private Timer helloMessageTimer;

    public HelloMessageService() {
        super(TAG);

        messageQueueManager = MessageQueueManager.getMessageQueueManager();

        helloMessageTimer = new Timer();
        helloMessageTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                HelloMessage helloMessage = new HelloMessage();
                messageQueueManager.addToForwardingMessages(helloMessage);
            }

        }, new Date(System.currentTimeMillis()), 25*1000);

        Log.i(TAG, "HelloMessageTimer ON");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HelloMessage helloMessage = (HelloMessage) intent.getSerializableExtra(Constants.
                                                                                EXTRA_HELLO_MESSAGE);

        String hostID = helloMessage.getHostID();
        String hostUsername = helloMessage.getHostUsername();
        Date lastTimeSeen = helloMessage.getReceptionTime();
        ArrayList<Subscription> hostSubscription = helloMessage.getHostSubscriptions();
        ArrayList<RegularMessage> cachedMessages = helloMessage.getCachedMessages();
        NeighborInfo neighbor = new NeighborInfo(hostID, hostUsername, lastTimeSeen,
                                                 hostSubscription, cachedMessages);
        boolean newNeighbor = NeighborhoodManager.getNeighborhoodManager().add(neighbor);

        //If there is a new neighbor then there's need to recalculate forwarding messages
        if (newNeighbor) {
            intent = new Intent(this, ApplevDisseminationChannelService.class);
            intent.putExtra(Constants.EXTRA_NEW_NEIGHBOR, true);
            startService(intent);
        }
        /**
         * TODO
         * Bisogna anche capire se smettere di trasmettere quando si è capito che tutti i vicini hanno ricevuto il messaggio e quindi se è necessario
         * che l'aggiunta di un hellomessage scateni un controllo sulla tabella delle decisioni
        */
    }

    public void onDestroy() {
        helloMessageTimer.cancel();
        super.onDestroy();
    }
}
