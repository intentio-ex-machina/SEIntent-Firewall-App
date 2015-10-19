package com.carteryagemann.seintentfirewall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class IntentCheckerSafeFile extends FirewallService.IntentChecker {

    private static final String TAG = "SafeFile";

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }

        // If the URI is for the malicious file, we'll change it to instead point at the benign one
        if (intent.getData() != null) {
            String filePath = intent.getData().toString();
            Log.v(TAG, filePath);
            if (filePath != null && filePath.contains("malicious_file.png")) {
                filePath = filePath.replace("malicious_file.png", "benign_file.png");
                intent.setData(Uri.parse(filePath));
                data.putParcelable("intent", intent);
            }
        }
        return data;
    }

    @Override
    public String getName() {
        return "SafeFile";
    }
}
