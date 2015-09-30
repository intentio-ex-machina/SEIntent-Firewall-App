package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * An intent checker which redirects intents which are heading to the browser application.
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
        // Redirect this intent if the receiver is the browser
        if (receiver != null && receiver.getPackageName().equals("com.android.browser")) {
            intent.setComponent(new ComponentName("com.carteryagemann.seintentfirewall",
                    "com.carteryagemann.seintentfirewall.BlockActivity"));
            data.putParcelable("intent", intent);
            return data;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "BlockBrowser";
    }
}
