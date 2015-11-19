package unife.icedroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import unife.icedroid.core.managers.SubscriptionListManager;

public class CreateChatActivity extends AppCompatActivity {
    private final static String TAG = "CreateChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chat_activity);
    }

    public void subscribeToGroup(View v) {
        String channel = ((EditText) findViewById(R.id.channel)).getText().toString();
        String group = ((EditText) findViewById(R.id.group_name)).getText().toString();
        SubscriptionListManager.getSubscriptionListManager().subscribe(channel, group);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHANNEL, channel);
        intent.putExtra(Constants.EXTRA_GROUP_NAME, group);
        startActivity(intent);
    }
}
