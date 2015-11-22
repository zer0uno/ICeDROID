package unife.icedroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.os.FileObserver;
import android.util.Log;
import unife.icedroid.core.RegularMessage;
import unife.icedroid.core.Subscription;
import unife.icedroid.services.ApplevDisseminationChannelService;
import unife.icedroid.utils.Settings;

public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {
    private final static String TAG = "ChatActivity";

    private Subscription subscription;

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Intent intent = getIntent();
        subscription = (Subscription) intent.getSerializableExtra(Constants.EXTRA_SUBSCRIPTION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(subscription.toString());
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(0));
        listView = (ListView) findViewById(R.id.messages_list);
        listView.setAdapter(adapter);
    }

    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new MessagesLoader(this, subscription);
    }

    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        for (String msg : data) {
            adapter.add(msg);
        }
        listView.setSelection(adapter.getCount() - 1);
    }

    public void onLoaderReset(Loader<ArrayList<String>> loader) {}

    public void sendMessage(View v) {
        EditText editText = (EditText) findViewById(R.id.msg);
        String txt = editText.getText().toString();
        editText.setText(null);
        RegularMessage regularMessage = new RegularMessage(subscription, txt);

        Intent intent = new Intent(this, ApplevDisseminationChannelService.class);
        intent.putExtra(unife.icedroid.core.Constants.EXTRA_ADC_MESSAGE, regularMessage);
        startService(intent);
    }


    private static class MessagesLoader extends AsyncTaskLoader<ArrayList<String>> {

        private MessagesObserver observer;
        private ArrayList<String> messages;
        private Context context;
        private Subscription subscription;
        private String oldMsg;


        public MessagesLoader(Context context, Subscription subscription) {
            super(context);
            this.context = context.getApplicationContext();
            this.subscription = subscription;
            oldMsg = "";
        }

        @Override
        protected void onStartLoading() {
            if (messages != null) {
                deliverResult(messages);
            }

            if (observer == null) {
                observer = new MessagesObserver(context.getFilesDir().toString() +
                        "/" + subscription.toString(), this);
                observer.startWatching();
            }

            if (takeContentChanged() || messages == null) {
                forceLoad();
            }
        }

        @Override
        protected void onForceLoad() {
            super.onForceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            ArrayList<String> newMsg = new ArrayList<>(0);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                                                context.openFileInput(subscription.toString())));
                ArrayList<String> previousMessages = new ArrayList<>(0);
                String msg;
                while ((msg = br.readLine()) != null) {
                    previousMessages.add(msg);
                }
                if (previousMessages.size() > 0) {
                    newMsg.add(previousMessages.get(previousMessages.size()-1));
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                Log.e(TAG, (msg != null) ? msg : "Impossible to load messages");
            }
            return newMsg;
        }

        @Override
        public void deliverResult(ArrayList<String> data) {
            if (isReset()) {
                return;
            }

            messages = data;

            if (isStarted()) {
                super.deliverResult(data);
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();

            onStopLoading();

            if (messages != null) {
                messages = null;
            }

            if (observer != null) {
                observer.stopWatching();
                observer = null;
            }
        }
    }

    private static class MessagesObserver extends FileObserver {
        private MessagesLoader loader;

        public MessagesObserver(String path, MessagesLoader loader) {
            super(path, MODIFY);
            this.loader = loader;
        }

        public void onEvent(int event, String path) {
            Log.i(TAG, "Modification event");
            loader.onContentChanged();
        }
    }
}
