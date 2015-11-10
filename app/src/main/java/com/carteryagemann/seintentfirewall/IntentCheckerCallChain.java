package com.carteryagemann.seintentfirewall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IntentCheckerCallChain extends FirewallService.IntentChecker {

    private static final String TAG = "CallChain";

    private Map<String, ArrayList<String>> CHAINS = new HashMap<String, ArrayList<String>>();

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }

        // Get sender information
        String callerPackage = data.getString("callingPackage");
        if (callerPackage == null)
            return data;

        // Get receiver information
        if (intent.getComponent() == null)
            return data;
        String receiverPackage = intent.getComponent().getPackageName();
        if (receiverPackage == null)
            return data;

        // Look up the receiver and add the sender to their list of callers
        if (CHAINS.containsKey(receiverPackage)) {
            ArrayList<String> callerList = CHAINS.get(receiverPackage);
            if (!callerList.contains(callerPackage)) {
                callerList.add(callerPackage);
                CHAINS.put(receiverPackage, callerList);
            }
        } else {
            ArrayList<String> newList = new ArrayList<String>();
            newList.add(callerPackage);
            CHAINS.put(receiverPackage, newList);
        }

        // Print all the chains associated with this receiver
        ArrayList<String> allCallers = getAllCallers(receiverPackage, new ArrayList<String>());
        Log.i(TAG, chainsToString(receiverPackage, allCallers));

        return data;
    }

    /**
     * Generates a string containing the receiver and all the associated callers.
     * @param receiver The receiver.
     * @param callers A list of all the associated callers.
     * @return A String representation of the caller chains
     */
    private String chainsToString(String receiver, ArrayList<String> callers) {
        if (callers.isEmpty())
            return receiver;

        String output =  receiver + " <= {" + callers.get(0);
        for (int i = 1; i < callers.size(); i++)
            output += ", " + callers.get(i);
        output += "}";

        return output;
    }

    /**
     * Gets all the callers associated with a package name.
     * @param packageName The receiver's package name.
     * @param list The list to store the results in.
     * @return A list of all the associated callers.
     */
    private ArrayList<String> getAllCallers(String packageName, ArrayList<String> list) {
        if (CHAINS.containsKey(packageName)) {
            ArrayList<String> callerList = CHAINS.get(packageName);
            for (String caller : callerList) {
                if (!list.contains(caller)) {
                    list.add(caller);
                    list = getAllCallers(caller, list);
                }
            }
        }
        return list;
    }

    @Override
    public String getName() {
        return "CallChain";
    }
}
