package unife.icedroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ICeDROIDActivity extends AppCompatActivity {
    private static final String TAG = "ICeDROIDActivity";
    private static final boolean DEBUG = true;

    private ArrayAdapter<Subscription> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.icedroid_activity);
        //Setting action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Settings s = Settings.getSettings(this);
        if (s == null) finish();
        else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    SubscriptionListManager.getSubscriptionListManager().getSubscriptionsList());

            ListView listView = (ListView) findViewById(R.id.subscritions_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Subscription subscription = adapter.getItem(position);
                    Intent intent = new Intent(ICeDROIDActivity.this, ChatActivity.class);
                    intent.putExtra(Subscription.EXTRA_SUBSCRIPTION, subscription);
                    startActivity(intent);
                }

            });
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

        for (Subscription s : SubscriptionListManager.getSubscriptionListManager().
                                                                        getSubscriptionsList()) {
            if (adapter.getPosition(s) == -1) {
                adapter.add(s);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.i(TAG, "ICeDROIDActivity destroyed");
    }
}