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

import java.util.LinkedList;
import java.util.List;

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
     */
    public static synchronized void initInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new LocationHelper(context);
        }
    }

    /**
     * Note: may return null if initInstance has not been called...
     * @return
     */
    public static synchronized LocationHelper getInstance() {
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
     * Add listener to listen to on connected, note: may still return null location if
     * permissions are not set
     * @param listener
     */
    public void listenToLocation(LocationReadyListener listener) {
        if (mGoogleApiWrapper.mLocation != null) {
            // if location ready, just immediately call the listener
            listener.onLocationReady(mGoogleApiWrapper.mLocation);
        } else {
            // else queue up the listener
            mGoogleApiWrapper.addListener(listener);
        }
    }

    /**
     * Interface for listening to location ready
     */
    public interface LocationReadyListener {

        void onLocationReady(Location location);

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
        List<LocationReadyListener> listeners = new LinkedList<>();

        public GoogleApiWrapper(Context context) {
            mContext = context;
            buildGoogleApiClient(context);
        }

        public GoogleApiClient getGoogleApiClient() {
            return this.mGoogleApiClient;
        }

        public void addListener(LocationReadyListener listener) {
            listeners.add(listener);
        }

        public void notifyListeners(Location location) {

            // notify all listeners
            for (LocationReadyListener listener : listeners) {
                listener.onLocationReady(location);
            }

            // clear out listeners
            listeners.clear();

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
                Log.i(TAG, "onConnected: connected");
            } else {
                Log.i(TAG, "onConnected: No permission for location!");
            }

            notifyListeners(mLocation);
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
