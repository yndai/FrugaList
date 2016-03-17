package com.ryce.frugalist.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * A helper for retrieving location information
 *
 * Created by Tony on 2016-03-16.
 */
public class LocationHelper {

    private static LocationHelper ourInstance;
    private static Context mContext;
    private GoogleApiWrapper mGoogleApiWrapper;

    // disallow instantiation
    private LocationHelper(Context context) {
        // initialize and builder google api client
        mGoogleApiWrapper = new GoogleApiWrapper(context);
    }

    /**
     * Note: we need context to verify location permissions when first initialized
     * @param context
     * @return
     */
    public static synchronized LocationHelper getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new LocationHelper(context);
        }
        return ourInstance;
    }

    public boolean isConnected() {
        return mGoogleApiWrapper.isConnected();
    }
    public void connect() {
        mGoogleApiWrapper.connect();
    }
    public void disconnect() {
        mGoogleApiWrapper.disconnect();
    }
    public Location getLastLocation() {
        return mGoogleApiWrapper.mLocation;
    }

    /**
     * Inner class to wrap the Google API Client connection
     */
    private class GoogleApiWrapper implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private final String TAG = GoogleApiWrapper.class.getSimpleName();

        Context mContext;
        GoogleApiClient mGoogleApiClient;
        Location mLocation;

        public GoogleApiWrapper(Context context) {
            mContext = context;
            buildGoogleApiClient(context);
        }

        public GoogleApiClient getGoogleApiClient() {
            return this.mGoogleApiClient;
        }

        public void connect() {
            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }

        public void disconnect() {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        public boolean isConnected() {
            if (mGoogleApiClient != null) {
                return mGoogleApiClient.isConnected();
            } else {
                return false;
            }
        }

        private void buildGoogleApiClient(Context context) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        @Override
        public void onConnected(Bundle bundle) {

            // check if we have permission to access location
            int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED) {
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else {
                Log.i(TAG, "onConnected: No permission for location!");
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "onConnectionSuspended");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, "onConnectionFailed: " + connectionResult.toString());
        }

    }
}
