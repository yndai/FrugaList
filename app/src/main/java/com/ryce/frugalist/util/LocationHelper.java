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
public class LocationHelper implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationHelper.class.getSimpleName();

    private static LocationHelper ourInstance;

    private Context mContext;

    // disallow instantiation
    private LocationHelper(Context context) {
        this.mContext = context;
        // init Google client
        buildGoogleApiClient(context);
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

    /**************************************
     * Location connection listening
     **************************************/

    List<LocationConnectionListener> listeners = new LinkedList<>();

    /**
     * Interface for listening to location API connected
     */
    public interface LocationConnectionListener {

        void onLocationConnectionReady(Location location);

    }

    /**
     * Add listener to listen to on connected, note: may still return null location if
     * permissions are not set
     * @param listener
     */
    public void listenToLocation(LocationConnectionListener listener) {
        if (isConnected()) {
            // if connection ready, just immediately call the listener
            listener.onLocationConnectionReady(getLastLocation());
        } else {
            // else queue up the listener
            listeners.add(listener);
        }
    }

    /**
     * Notify listeners that we are connected
     * @param location
     */
    private void notifyListeners(Location location) {

        // notify all listeners
        for (LocationConnectionListener listener : listeners) {
            listener.onLocationConnectionReady(location);
        }

        // clear out listeners
        listeners.clear();
    }

    /**************************************
     * Google API Client methods
     **************************************/

    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    /**
     * Connect to Google API
     */
    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.i(TAG, "connect()");
        }
    }

    /**
     * Disconnect from Google API
     */
    public void disconnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "disconnect()");
        }
    }

    /**
     * True if connected to Google API
     * @return
     */
    public boolean isConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }

    /**
     * Get last location, may be null
     * @return
     */
    public Location getLastLocation() {
        if (isConnected()) {
            try {
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e){/* Not handling here */}
        }
        return mLocation;
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

    /**
     * Do not call this directly
     * @param bundle
     */
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

    /**
     * Do not call this directly
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    /**
     * Do not call this directly
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

}
