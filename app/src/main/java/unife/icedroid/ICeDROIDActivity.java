package unife.icedroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import unife.icedroid.core.Subscription;
import unife.icedroid.core.managers.SubscriptionListManager;
import unife.icedroid.utils.Settings;

public class ICeDROIDActivity extends AppCompatActivity {
    private static final String TAG = "ICeDROIDActivity";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.icedroid_activity);
        //Setting action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Settings s = Settings.getSettings(this);
        if (s == null) finish();
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

        ArrayList<Subscription> subscriptionsList = SubscriptionListManager.
                                            getSubscriptionListManager().getSubscriptionsList();
        ArrayAdapter<Subscription> adapter = new ArrayAdapter<>(this,
                                                    android.R.layout.simple_list_item_1,
                                                    subscriptionsList);

        ListView listView = (ListView) findViewById(R.id.subscritions_list);
        listView.setAdapter(adapter);
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
        if (DEBUG) Log.i(TAG, "ICeDROIDActivity destroyed");
    }
}