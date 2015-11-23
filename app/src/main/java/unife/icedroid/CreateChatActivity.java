package unife.icedroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import unife.icedroid.core.Subscription;
import unife.icedroid.core.managers.SubscriptionListManager;
import unife.icedroid.utils.Settings;

public class CreateChatActivity extends AppCompatActivity {
    private final static String TAG = "CreateChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chat_activity);

        Settings s = Settings.getSettings(this);
        if (s == null) finish();
    }

    public void subscribeToGroup(View v) {
        String channelID = ((EditText) findViewById(R.id.channel)).getText().toString();
        String groupName = ((EditText) findViewById(R.id.group_name)).getText().toString();
        Subscription subscription = new Subscription(channelID, groupName);
        SubscriptionListManager.getSubscriptionListManager().subscribe(subscription);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_SUBSCRIPTION, subscription);
        startActivity(intent);
    }
}
