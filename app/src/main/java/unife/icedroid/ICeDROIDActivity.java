package unife.icedroid;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import unife.icedroid.utils.NICManager;

public class ICeDROIDActivity extends AppCompatActivity {
    private static final String TAG = "ICeDROIDActivity";

    private WifiManager wifimanager;
    private WifiManager.WifiLock wifilock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icedroid_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifilock = wifimanager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WifiLock");
        wifilock.acquire();
        wifimanager.setWifiEnabled(true);

        try {
            NICManager.startWifiAdhoc();

        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onCreate(): An error occurred");
            finish();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_chat:
                Intent intent = new Intent(this, CreateChatActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

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
        /*try {
            NICManager.stopWifiAdhoc();
            wifilock.release();
        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onDestroy(): An error occurred");
        }
        Log.i(TAG, "ICeDROIDActivity destroyed");*/
    }
}