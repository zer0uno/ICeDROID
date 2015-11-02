package unife.icedroid;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import unife.icedroid.utils.NICManager;

public class ICeDROIDActivity extends Activity{
    private static final String TAG = "ICeDROIDActivity";

    private WifiManager wifimanager;
    private WifiManager.WifiLock wifilock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icedroid_activity);

        wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifilock = wifimanager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WifiLock");
        wifilock.acquire();
        wifimanager.setWifiEnabled(true);

        try {
            NICManager.startWifiAdhoc();

        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onCreate(): An error occurred");
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            NICManager.stopWifiAdhoc();
            wifilock.release();
        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onDestroy(): An error occurred");
        }
    }
}
