package com.ryce.frugalist.view.list;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.ryce.frugalist.R;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.util.LocationHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.create.CreateListingActivity;
import com.ryce.frugalist.view.login.LoginActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainListActivity extends AppCompatActivity {

    private static final String TAG = MainListActivity.class.getSimpleName();

    // for requesting permissions
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String[] PERMISSIONS = {
            //Manifest.permission.ACCESS_FINE_LOCATION, // just get coarse location for now
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /** the FragmentStatePagerAdapter */
    private ListSectionPagerAdapter mSectionsPagerAdapter;



    // callback for deal list
    Callback<FrugalistResponse.DealList> mFrugalistDealListCallback = new Callback<FrugalistResponse.DealList>() {
        @Override
        public void onResponse(
                Call<FrugalistResponse.DealList> call,
                Response<FrugalistResponse.DealList> response
        ) {

            if (response.isSuccess()) {

                FrugalistResponse.DealList deals = response.body();
                Log.i(TAG, deals.toString());

            } else {

                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {
                    // not handling
                }
            }
        }

        @Override
        public void onFailure(Call<FrugalistResponse.DealList> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    };

    // callback for deal
    Callback<FrugalistResponse.Deal> mFrugalistDealCallback = new Callback<FrugalistResponse.Deal>() {
        @Override
        public void onResponse(
                Call<FrugalistResponse.Deal> call,
                Response<FrugalistResponse.Deal> response
        ) {

            if (response.isSuccess()) {

                FrugalistResponse.Deal deal = response.body();
                Log.i(TAG, deal.toString());

            } else {

                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {
                    // not handling
                }
            }
        }

        @Override
        public void onFailure(Call<FrugalistResponse.Deal> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    };

    /** The {@link ViewPager} that will host the section contents. */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize facebook SDK
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // immediately go to login if not logged in
        if (UserHelper.getCurrentUser(this) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // initialize location helper
        LocationHelper.initInstance(this);

        // try to get permissions for location and file read/write
        requestPermissions();

        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        mSectionsPagerAdapter = new ListSectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set up tabs widget with pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // init FAB for creating a new deal
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (LocationHelper.getInstance(MainListActivity.this).isConnected()) {
//                    Location loc = LocationHelper.getInstance(MainListActivity.this).getLastLocation();
//                    FrugalistServiceHelper.getInstance().doGetNearbyDealList(
//                            mFrugalistDealListCallback, (float) loc.getLatitude(), (float) loc.getLongitude(), 5);
//                }
//
//                FrugalistServiceHelper.getInstance().doGetDealById(mFrugalistDealCallback, 5981343255101440L);

                Context context = view.getContext();
                Intent intent = new Intent(context, CreateListingActivity.class);
                context.startActivity(intent);
                Log.i(TAG, LocationHelper.getInstance().getLastLocation().toString());
            }
        });

    }

    /**
     * Make sure we have permissions we need, ask user if needed
     */
    private void requestPermissions() {
        // check if we have location permission
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (location != PackageManager.PERMISSION_GRANTED || write != PackageManager.PERMISSION_GRANTED) {

            // ask user for permissions
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    PERMISSIONS_REQUEST
            );

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // connect Google Location client if we have location permission, otherwise, wait for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationHelper.getInstance().connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // connect Google Location client
        LocationHelper.getInstance().disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_logout){
            UserHelper.userLogout(MainListActivity.this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length >= 3
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                // granted, connect Google Location client
                LocationHelper.getInstance().connect();

            } else {

                // boo, close the activity
                finish();
            }
        }
    }

}
