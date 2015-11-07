package unife.icedroid.services;

import android.app.IntentService;
import android.content.Intent;
import unife.icedroid.core.Constants;

import unife.icedroid.core.RegularMessage;

public class ApplevDisseminationChannelService extends IntentService {
    private static final String TAG = "AppDissChannelService";

    public static final double CACHING_PROBABILITY = 0.1;
    public static final double FORWARD_PROBABILITY = 0.3;

    public ApplevDisseminationChannelService() {
        super(TAG);
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RegularMessage regularMessage = (RegularMessage) intent.getSerializableExtra(Constants.
                                                                                EXTRA_ADC_MESSAGE);


    }

}
