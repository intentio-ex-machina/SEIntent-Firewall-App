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
        // Check some extra fields as well
        try {
            String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (extraText != null && extraText.contains("malicious_file.png")) {
                extraText = extraText.replace("malicious_file.png", "benign_file.png");
                intent.putExtra(Intent.EXTRA_TEXT, extraText);
                data.putParcelable("intent", intent);
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not unmarshal extras bundle.");
        }
        try {
            String extraStream = intent.getStringExtra(Intent.EXTRA_STREAM);
            if (extraStream != null && extraStream.contains("malicious_file.png")) {
                extraStream = extraStream.replace("malicious_file.png", "benign_file.png");
                intent.putExtra(Intent.EXTRA_STREAM, extraStream);
                data.putParcelable("intent", intent);
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not unmarshal extras bundle.");
        }

        return data;
    }

    @Override
    public String getName() {
        return "SafeFile";
    }
}
