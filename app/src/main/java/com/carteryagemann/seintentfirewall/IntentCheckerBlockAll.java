package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * A very simple intent checker which blocks every packet it checks.
 */
public class IntentCheckerBlockAll extends FirewallService.IntentChecker {

    private static final String TAG = "BlockAll";

    @Override
    public Bundle checkIntent(Bundle data) {
        // It would be very bad to block our own app, so we'll allow intents to us and block
        // everything else.
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();
        // Block this intent if the receiver is the browser
        if (receiver.getPackageName().equals("com.carteryagemann.seintentfirewall")) {
            return data;
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return "BlockAll";
    }
}
