package com.carteryagemann.seintentfirewall;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * The FirewallService is the core of the application. This is the service which the Android intent
 * firewall will bind to and feed intents to. Upon receiving an intent, the FirewallService will
 * inform the Android intent firewall if the intent should be allowed or blocked.
 */
public class FirewallService extends Service {

    private final static String TAG = "SEIntentFirewall";

    private final static int CHECK_INTENT = 1;

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
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Received bind request.");
        return mMessenger.getBinder();
    }
}
