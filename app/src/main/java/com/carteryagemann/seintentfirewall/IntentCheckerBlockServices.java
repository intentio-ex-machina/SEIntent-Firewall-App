package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * An intent checker which will block services (bind, start, stop) and allow everything else.
 */
public class IntentCheckerBlockServices extends FirewallService.IntentChecker {

    private static final String TAG = "BlockServices";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();

        // It would be very bad to block our own app, so we'll allow intents to us no matter what.
        if (receiver != null && receiver.getPackageName().equals("com.carteryagemann.seintentfirewall"))
            return data;

        // Block service intents and allow everything else.
        int intentType = data.getInt("intentType", -1);
        if (intentType == 2) { // TYPE_SERVICE
            return null;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "BlockServices";
    }
}
