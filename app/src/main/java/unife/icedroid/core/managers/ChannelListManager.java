package unife.icedroid.core.managers;

import android.content.Context;
import android.util.Log;
import unife.icedroid.core.ICeDROIDMessage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChannelListManager {
    private static final String TAG = "ChannelListManager";
    private static final boolean DEBUG = true;

    private static final String channelsFileName = "channels";
    private volatile static ChannelListManager instance = null;

    private ArrayList<String> channelList;
    private Context context;


    private ChannelListManager(Context context) {
        this.context = context.getApplicationContext();
        channelList = new ArrayList<>(0);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                               context.openFileInput(channelsFileName)));

            String channel;
            while ((channel = br.readLine()) != null) {
                channelList.add(channel);
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error loading channel list");
        }
    }

    public static ChannelListManager getSubscriptionListManager(Context context) {
        if (instance == null) {
            synchronized (ChannelListManager.class) {
                if (instance == null) {
                    instance = new ChannelListManager(context);
                }
            }
        }
        return instance;
    }

    public static ChannelListManager getChannelListManager() {
        return instance;
    }

    public synchronized void subscribe(String channel) {
        if (!channelList.contains(channel)) {
            channelList.add(channel);
            try {
                FileOutputStream fos = context.openFileOutput(channelsFileName,
                        Context.MODE_PRIVATE | Context.MODE_APPEND);
                fos.write((channel + "\n").getBytes());
                fos.close();
                if (DEBUG) Log.i(TAG, "Attachment to channel: " + channel);
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error sticking to channel");
            }
        }
    }

    public synchronized ArrayList<String> getChannelList() {
        return new ArrayList<>(channelList);
    }

    public synchronized boolean isSubscribedToChannel(ICeDROIDMessage msg) {
        for (String c : channelList) {
            if (c.equals(msg.getChannel())) {
                return true;
            }
        }
        return false;
    }

}
