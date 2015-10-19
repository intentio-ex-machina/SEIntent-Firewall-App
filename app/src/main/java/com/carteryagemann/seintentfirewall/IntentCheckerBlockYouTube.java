package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IntentCheckerBlockYouTube extends FirewallService.IntentChecker {

    private static final String TAG = "BlockYouTube";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }
        ComponentName receiver = intent.getComponent();
        // Redirect this intent if the receiver is the browser
        if (receiver != null && receiver.getPackageName().contains("youtube")) {
            intent.setComponent(new ComponentName("com.carteryagemann.seintentfirewall",
                    "com.carteryagemann.seintentfirewall.BlockActivity"));
            data.putParcelable("intent", intent);
            return data;
        }

        // Scrub youtube data
        if (intent.getDataString() != null && intent.getDataString().contains("youtu")) {
            Intent redirect = new Intent();
            redirect.setComponent(new ComponentName("com.carteryagemann.seintentfirewall",
                    "com.carteryagemann.seintentfirewall.BlockActivity"));
            data.putParcelable("intent", redirect);
            return data;
        }

        return data;
    }

    @Override
    public String getName() {
        return "BlockYouTube";
    }
}
