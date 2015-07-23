package com.carteryagemann.seintentfirewall;

import android.os.Bundle;

/**
 * A very simple intent checker which blocks every packet it checks.
 */
public class IntentCheckerBlockAll extends FirewallService.IntentChecker {

    @Override
    public Bundle checkIntent(Bundle data) {
        return null;
    }

    @Override
    public String getName() {
        return "BlockAll";
    }
}
