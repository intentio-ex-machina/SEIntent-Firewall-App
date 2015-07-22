package com.carteryagemann.seintentfirewall;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * The FirewallService is the core of the application. This is the service which the Android intent
 * firewall will bind to and feed intents to. Upon receiving an intent, the FirewallService will
 * inform the Android intent firewall if the intent should be allowed or blocked.
 */
public class FirewallService extends Service {

    protected final static String TAG = "SEIntentFirewall";

    private final static int CHECK_INTENT = 1;
    protected final static int GET_STATS  = 2;

    private static int connectedClients = 0;
    private static int allowedCount = 0;
    private static int blockedCount = 0;

    protected final static String EXTRA_IS_ENABLED = "EXTRA_IS_ENABLED";
    protected final static String EXTRA_ALLOWED_COUNT = "EXTRA_CONNECTED_ALLOWED_COUNT";
    protected final static String EXTRA_BLOCKED_COUNT = "EXTRA_BLOCKED_COUNT";
    protected final static String EXTRA_POLICY = "EXTRA_POLICY";

    final Messenger mMessenger = new Messenger(new ServiceHandler());

    /**
     * The main handler for the firewall service. The intent firewall will deliver messages to this
     * handler.
     */
    private final static class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CHECK_INTENT:
                    //TODO
                    break;
                case GET_STATS:
                    gatherStats(msg.replyTo);
                    break;
            }
        }
    }

    private static void gatherStats(Messenger replyTo) {
        if (replyTo == null) return;

        Bundle replyData = new Bundle();
        if (connectedClients > 1) {
            replyData.putBoolean(EXTRA_IS_ENABLED, true);
        } else {
            replyData.putBoolean(EXTRA_IS_ENABLED, false);
        }
        replyData.putString(EXTRA_POLICY, "Default");
        replyData.putInt(EXTRA_ALLOWED_COUNT, allowedCount);
        replyData.putInt(EXTRA_BLOCKED_COUNT, blockedCount);

        Message reply = Message.obtain(null, GET_STATS);
        reply.setData(replyData);

        try {
            replyTo.send(reply);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to reply to get stats request.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Received bind request.");
        connectedClients++;
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        connectedClients--;
        return super.onUnbind(intent);
    }
}
