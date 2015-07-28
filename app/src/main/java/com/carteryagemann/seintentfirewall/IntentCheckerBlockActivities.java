package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * An intent checker which will block activities and allow everything else.
 */
public class IntentCheckerBlockActivities extends FirewallService.IntentChecker {

    private static final String TAG = "BlockActivities";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();

        // It would be very bad to block our own app, so we'll allow intents to us no matter what.
        if (receiver.getPackageName().equals("com.carteryagemann.seintentfirewall")) return data;

        // Block service intents and allow everything else.
        int intentType = data.getInt("intentType", -1);
        if (intentType == 0) { // TYPE_ACTIVITY
            return null;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "BlockActivities";
    }
}
