package com.ryce.frugalist.view.list;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.ryce.frugalist.R;
import com.ryce.frugalist.util.LocationHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.create.CreateListingActivity;
import com.ryce.frugalist.view.login.LoginActivity;
import com.ryce.frugalist.view.search.SearchListingActivity;
import com.ryce.frugalist.view.settings.SettingsActivity;

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
        //mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount() - 1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set up tabs widget with pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // init FAB for creating a new deal
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, CreateListingActivity.class);
                context.startActivity(intent);
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
    protected void onResume() {
        super.onResume();
        // connect Google Location client if we have location permission, otherwise, wait for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationHelper.getInstance().connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disconnect Google Location client
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

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_search) {

            Intent intent = new Intent(this, SearchListingActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_logout) {

            // show confirm dialog for logout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you wish to log out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // do logout
                            UserHelper.userLogout(MainListActivity.this);
                            Intent intent = new Intent(MainListActivity.this, LoginActivity.class);
                            startActivity(intent);
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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
