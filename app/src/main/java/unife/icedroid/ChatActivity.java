package unife.icedroid;

import java.util.ArrayList;
import java.util.Date;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.Subscription;
import unife.icedroid.services.ApplevDisseminationChannelService;
import unife.icedroid.utils.Settings;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";

    private String channelID;
    private String groupName;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Intent intent = getIntent();
        channelID = intent.getStringExtra(Constants.EXTRA_CHANNEL);
        groupName = intent.getStringExtra(Constants.EXTRA_GROUP_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(channelID + ": " + groupName);
        setSupportActionBar(toolbar);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(0));

        ListView listView = (ListView) findViewById(R.id.messages_list);
        listView.setAdapter(adapter);
    }

    public void sendMessage(View v) {
        String msg = ((EditText) findViewById(R.id.msg)).getText().toString();
        RegularMessage message = new RegularMessage();
        message.setSubscription(new Subscription(channelID, groupName));
        message.setContentData(msg);
        message.setHostID(Settings.HOST_IP);
        message.setHostUsername("antonio");
        message.setMsgID();
        message.setTtl(-1);
        message.setPriority(0);
        message.setSize(msg.getBytes().length);
        message.setCreationTime(new Date(System.currentTimeMillis()));
        message.setReceptionTime(new Date(System.currentTimeMillis()));

        Intent intent = new Intent(this, ApplevDisseminationChannelService.class);
        intent.putExtra(unife.icedroid.core.Constants.EXTRA_ADC_MESSAGE, message);
        startService(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        RegularMessage msg = (RegularMessage) intent.getSerializableExtra(RegularMessage.REGULAR_MESSAGE);
        String message = msg.getHostID() + "\n" + msg.getContentData();
        adapter.add(message);
    }
}
