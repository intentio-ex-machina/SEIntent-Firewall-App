package com.carteryagemann.seintentfirewall;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class IntentCheckerCameraPicker extends FirewallService.IntentChecker {

    private static final String TAG = "CameraPicker";

    /* GEOFENCE */
    // SciTech: 43.037541 lat, -76.130531 long
    private static final double MAX_LAT = 43.037534;
    private static final double MIN_LAT = 43.037518;
    private static final double MAX_LON = -76.130627;
    private static final double MIN_LON = -76.130579;

    private Location mLocation;

    IntentCheckerCameraPicker(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.v(TAG, mLocation.toString());
                mLocation = location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public Bundle checkIntent(Bundle data) {
        Intent intent = data.getParcelable("intent");
        if (intent == null) {
            Log.w(TAG, "Received bundle with no intent! Dropping.");
            return null;
        }

        ComponentName receiver = intent.getComponent();

        if (receiver != null && receiver.getPackageName().equals("com.android.camera2")) {
            if (mLocation == null) {
                intent.setComponent(new ComponentName("com.carteryagemann.corporatecamera",
                        "com.carteryagemann.corporatecamera.MainActivity"));
                data.putParcelable("intent", intent);
                return data;
            }
            if (mLocation.getLatitude() < MAX_LAT && mLocation.getLatitude() > MIN_LAT &&
                    mLocation.getLongitude() < MAX_LON && mLocation.getLongitude() > MIN_LON) {
                intent.setComponent(new ComponentName("com.carteryagemann.corporatecamera",
                        "com.carteryagemann.corporatecamera.MainActivity"));
                data.putParcelable("intent", intent);
                return data;
            }
            return data;
        } else {
            return data;
        }
    }

    @Override
    public String getName() {
        return "CameraPicker";
    }
}
