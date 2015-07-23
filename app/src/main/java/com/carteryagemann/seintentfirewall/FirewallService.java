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

    protected final static int CHECK_INTENT = 1;
    protected final static int GET_STATS    = 2;
    protected final static int LOAD_POLICY  = 3;

    private static int allowedCount = 0;
    private static int blockedCount = 0;

    protected final static String EXTRA_ALLOWED_COUNT = "EXTRA_CONNECTED_ALLOWED_COUNT";
    protected final static String EXTRA_BLOCKED_COUNT = "EXTRA_BLOCKED_COUNT";
    protected final static String EXTRA_POLICY = "EXTRA_POLICY";

    final Messenger mMessenger = new Messenger(new ServiceHandler());

    private IntentChecker mIntentChecker = new IntentCheckerAllowAll();

    /**
     * The main handler for the firewall service. The intent firewall will deliver messages to this
     * handler.
     */
    private final class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CHECK_INTENT:
                    Bundle data = msg.getData();
                    Bundle rData = null;
                    if (data != null && mIntentChecker != null)
                        rData = mIntentChecker.checkIntent(data);
                    if (rData != null && msg.replyTo != null) {
                        Message response = Message.obtain(null, CHECK_INTENT);
                        response.setData(rData);
                        try {
                            msg.replyTo.send(response);
                        } catch (RemoteException e) {
                            Log.w(TAG, "Failed to send intent to intent firewall.");
                        }
                        allowedCount++;
                    } else {
                        blockedCount++;
                    }
                    break;
                case GET_STATS:
                    gatherStats(msg.replyTo);
                    break;
                case LOAD_POLICY:
                    loadPolicy(msg.arg1);
            }
        }
    }

    private void loadPolicy(int option) {
        switch (option) {
            case 0: //AllowAll
                mIntentChecker = new IntentCheckerAllowAll();
                break;
            case 1: //BlockAll
                mIntentChecker = new IntentCheckerBlockAll();
                break;
            case 2: //BlockBrowser
                mIntentChecker = new IntentCheckerBlockBrowser();
                break;
            default:
                Log.w(TAG, "Unknown policy request: " + option);
        }
    }

    private void gatherStats(Messenger replyTo) {
        if (replyTo == null) return;

        Bundle replyData = new Bundle();
        replyData.putString(EXTRA_POLICY, mIntentChecker.getName());
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
        return mMessenger.getBinder();
    }

    /**
     * The base intent checker class from which all intent checkers will extend.
     *
     * Intent checkers should either reply with the bundle to be sent back to the intent firewall or
     * null if the enclosed intent should be blocked.
     */
    public static abstract class IntentChecker {
        public abstract Bundle checkIntent(Bundle data);
        public abstract String getName();
    }
}
