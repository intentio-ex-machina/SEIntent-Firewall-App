package com.carteryagemann.seintentfirewall;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class IntentCheckerBanditBuster extends FirewallService.IntentChecker {

    private static final String TAG = "BanditBuster";
    private static final int THRESHOLD = 10;

    private int BULLET_COUNT = 0;
    private Context mContext;
    private ActivityManager mAms;

    IntentCheckerBanditBuster(Context context) {
        mContext = context;
        if (mContext != null)
            mAms = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }

        // Get sender information
        String callerPackage = data.getString("callingPackage");
        if (callerPackage == null) {
            Log.v(TAG, "Intent has no sender information, skipping.");
            return data;
        }

        // We're tracking a specific package because this is a proof-of-concept
        if (!callerPackage.equalsIgnoreCase("com.carteryagemann.boondocksaint")) {
            return data;
        }

        // We've got the right sender, handle it
        BULLET_COUNT++;
        if (BULLET_COUNT == THRESHOLD) {
            CharSequence text = "Intent DoS Detected! Taking defensive action.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(mContext, text, duration);
            toast.show();
        }

        if (BULLET_COUNT > THRESHOLD) {
            if (mAms != null)
                mAms.killBackgroundProcesses("com.carteryagemann.boondocksaint");
            return null;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "BanditBuster";
    }
}
