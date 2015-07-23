package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * A very simple intent checker which allows every packet it checks.
 */
public class IntentCheckerBlockBrowser extends FirewallService.IntentChecker {

    private static final String TAG = "BlockBrowser";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();
        // Block this intent if the receiver is the browser
        if (receiver.getPackageName().equals("com.android.browser")) {
            return null;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "BlockBrowser";
    }
}
