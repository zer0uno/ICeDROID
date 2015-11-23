package unife.icedroid.core.managers;

import android.util.Log;
import java.io.FileOutputStream;

import unife.icedroid.core.RegularMessage;

public class ChatsManager {
    private static final String TAG = "ChatsManager";
    private static final boolean DEBUG = true;

    public static synchronized void saveMessageInConversation(String directory,
                                                              RegularMessage message) {
        String data = "[" + message.getReceptionTime().toString() + "] " +
                        message.getHostID() + ": " +
                        message.getContentData() + "\n";

        String path = directory + "/" + message.getSubscription().toString();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, true);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
            if (DEBUG) Log.i(TAG,"Message saved: " + data);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (DEBUG) Log.e(TAG, (msg != null) ? msg : "Error writing to file" );
        }
    }
}
