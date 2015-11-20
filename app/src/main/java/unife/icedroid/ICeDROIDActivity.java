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
import android.widget.Toast;

import unife.icedroid.services.ApplevDisseminationChannelService;
import unife.icedroid.services.BroadcastReceiveService;
import unife.icedroid.services.BroadcastSendService;
import unife.icedroid.services.HelloMessageService;
import unife.icedroid.utils.NICManager;
import unife.icedroid.utils.Settings;

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

        //Enable Wifi Ad-Hoc
        wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Settings.setHostId(wifimanager.getConnectionInfo().getMacAddress());
        wifilock = wifimanager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WifiLock");
        wifilock.acquire();
        wifimanager.setWifiEnabled(true);
        try {
            NICManager.startWifiAdhoc();
            Toast.makeText(this, R.string.AdHoc_enabled, Toast.LENGTH_LONG).show();

            //Activation of various needed services
            Intent intent;
            //BroadcastReceiveService
            intent = new Intent(this, BroadcastReceiveService.class);
            startService(intent);
            //BroadcastSendService
            intent = new Intent(this, BroadcastSendService.class);
            startService(intent);
            //HelloMessageService
            //intent = new Intent(this, HelloMessageService.class);
            //startService(intent);
            //ApplevDisseminationChannelService
            intent = new Intent(this, ApplevDisseminationChannelService.class);
            startService(intent);

        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onCreate(): An error occurred");
            finish();
        }
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
        try {
            NICManager.stopWifiAdhoc();
            wifilock.release();

            //Destruction of all needed services
            Intent intent;
            //BroadcastReceiveService
            intent = new Intent(this, BroadcastReceiveService.class);
            stopService(intent);
            //BroadcastSendService
            intent = new Intent(this, BroadcastSendService.class);
            stopService(intent);
            //HelloMessageService
            intent = new Intent(this, HelloMessageService.class);
            stopService(intent);
        } catch(Exception ex) {
            String msg = ex.getMessage();
            Log.e(TAG, (msg!=null)? msg : "onDestroy(): An error occurred");
        }
        Log.i(TAG, "ICeDROIDActivity destroyed");
    }
}