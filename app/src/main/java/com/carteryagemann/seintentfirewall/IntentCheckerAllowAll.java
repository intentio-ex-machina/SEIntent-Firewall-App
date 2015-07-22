package com.carteryagemann.seintentfirewall;

import android.os.Bundle;

/**
 * A very simple intent checker which allows every packet it checks.
 */
public class IntentCheckerAllowAll extends FirewallService.IntentChecker {

    @Override
    public Bundle checkIntent(Bundle data) {
        return data;
    }
}
