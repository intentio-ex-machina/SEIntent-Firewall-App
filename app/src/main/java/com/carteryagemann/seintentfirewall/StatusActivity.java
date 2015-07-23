package com.carteryagemann.seintentfirewall;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This is the main activity which will be launched when a user starts the app. It shows some basic
 * status information.
 */
public class StatusActivity extends AppCompatActivity {

    private static final int SERVICE_POLLING_RATE = 2000;
    private static final int SEND_GET_STATS  = 1;

    Handler mInternalHandler;
    Messenger mMessenger = new Messenger(new StatusHandler());
    Messenger mFirewallService;
    FirewallConnection mFirewallConnection;

    private boolean debugEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        // Bind to firewall service
        mFirewallConnection = new FirewallConnection();
        bindService(new Intent(this, FirewallService.class),
                mFirewallConnection,
                Service.BIND_AUTO_CREATE);
        mInternalHandler = new InternalHandler();
        // Set up policy spinner
        Spinner spinner = (Spinner) findViewById(R.id.policySpinner);
        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.policy_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInternalHandler != null) mInternalHandler.removeMessages(SEND_GET_STATS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInternalHandler != null) mInternalHandler.sendEmptyMessage(SEND_GET_STATS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mFirewallConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.enable_debug:
                if (!debugEnabled) {
                    Button testButton = (Button) findViewById(R.id.fireTestButton);
                    if (testButton != null) testButton.setVisibility(View.VISIBLE);
                    Spinner policySpinner = (Spinner) findViewById(R.id.policySpinner);
                    if (policySpinner != null) policySpinner.setVisibility(View.VISIBLE);
                    Button policyButton = (Button) findViewById(R.id.policyButton);
                    if (policyButton != null) policyButton.setVisibility(View.VISIBLE);
                } else {
                    Button testButton = (Button) findViewById(R.id.fireTestButton);
                    if (testButton != null) testButton.setVisibility(View.INVISIBLE);
                    Spinner policySpinner = (Spinner) findViewById(R.id.policySpinner);
                    if (policySpinner != null) policySpinner.setVisibility(View.INVISIBLE);
                    Button policyButton = (Button) findViewById(R.id.policyButton);
                    if (policyButton != null) policyButton.setVisibility(View.INVISIBLE);
                }
                debugEnabled = !debugEnabled;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void fireTestIntent(View view) {
        Message msg = Message.obtain(null, FirewallService.CHECK_INTENT);
        Bundle data = new Bundle();
        Intent emptyIntent = new Intent();
        data.putParcelable("INTENT", emptyIntent);
        msg.setData(data);
        msg.replyTo = mMessenger;
        try {
            mFirewallService.send(msg);
        } catch (RemoteException e) {
            Log.w(FirewallService.TAG, "Failed to send test check intent.");
        }
    }

    public void debugLoadPolicy(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.policySpinner);
        if (spinner == null) return;
        int pos = spinner.getSelectedItemPosition();
        // Instruct firewall service to load new policy
        if (mFirewallService == null) return;
        Message msg = Message.obtain(null, FirewallService.LOAD_POLICY);
        msg.arg1 = pos;
        try {
            mFirewallService.send(msg);
        } catch (RemoteException e) {
            Log.w(FirewallService.TAG, "Failed to send load policy message to intent firewall");
        }
    }

    private void unpackStats(Bundle data) {
        if (data == null) return;

        String policy = data.getString(FirewallService.EXTRA_POLICY, "Unknown");
        int allowCount = data.getInt(FirewallService.EXTRA_ALLOWED_COUNT, 0);
        int blockCount = data.getInt(FirewallService.EXTRA_BLOCKED_COUNT, 0);

        TextView enabledView = (TextView) findViewById(R.id.statusValue);
        TextView policyView = (TextView) findViewById(R.id.policyValue);
        TextView allowView = (TextView) findViewById(R.id.allowValue);
        TextView blockView = (TextView) findViewById(R.id.blockValue);

        if (enabledView != null) {
            if (mFirewallService != null) {
                enabledView.setText("Running");
            } else {
                enabledView.setText("Stopped");
            }
        }
        if (policyView != null) policyView.setText(policy);
        if (allowView != null) allowView.setText(Integer.toString(allowCount));
        if (blockView != null) blockView.setText(Integer.toString(blockCount));
    }

    private class StatusHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FirewallService.GET_STATS:
                    unpackStats(msg.getData());
                    break;
            }
        }
    }

    private class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_GET_STATS:
                    if (mFirewallService == null) break;
                    try {
                        Message request = Message.obtain(null, FirewallService.GET_STATS);
                        request.replyTo = mMessenger;
                        mFirewallService.send(request);
                        mInternalHandler.sendEmptyMessageDelayed(SEND_GET_STATS, SERVICE_POLLING_RATE);
                    } catch (RemoteException e) {
                        Log.w(FirewallService.TAG, "Failed to send message to firewall service.");
                    }
                    break;
            }
        }
    }

    private class FirewallConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFirewallService = new Messenger(iBinder);
            // Periodically get stats from firewall service
            mInternalHandler.sendEmptyMessageDelayed(SEND_GET_STATS, SERVICE_POLLING_RATE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFirewallService = null;
        }
    }
}
