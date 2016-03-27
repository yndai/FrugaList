package com.ryce.frugalist.view.search;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.Settings;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.LocationHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.util.Utils;
import com.ryce.frugalist.view.list.DividerItemDecoration;
import com.ryce.frugalist.view.list.ListSectionFragment;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter;
import com.ryce.frugalist.view.list.ListSectionRecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Roger_Wang on 2016-03-14.
 */
public class SearchListingActivity extends AppCompatActivity
        implements LocationHelper.LocationConnectionListener {

    private static final String TAG = SearchListingActivity.class.getSimpleName();

    /** Mapping of spinner position to sort code */
    private static final int[] SORT_TYPE = {0, 1, 2};

    /** Enum of search types */
    private enum SearchType {
        PRODUCT(0), STORE(1);
        public int value;
        private SearchType(int value) {this.value = value;}
    }

    Spinner mSortSpinner;
    Spinner mTypeSpinner;
    EditText mInputSearch;
    RecyclerView mRecyclerView;
    Button mSearchButton;
    ListSectionRecyclerAdapter mListAdapter;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_listing);

        mInputSearch = (EditText) findViewById(R.id.inputSearch);
        mSortSpinner = (Spinner) findViewById(R.id.inputSortSpinner);
        mTypeSpinner = (Spinner) findViewById(R.id.inputTypeSpinner);
        mSearchButton = (Button) findViewById(R.id.searchButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.searchListView);

        // init progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_loading));

        // init recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null, false, true));

        // init recycler view adapter with empty list
        mListAdapter = new ListSectionRecyclerAdapter(this, new ArrayList<AbstractListing>(),
                ListSectionFragment.ListingType.DEAL, ListSectionPagerAdapter.ListSection.SEARCH);
        mRecyclerView.setAdapter(mListAdapter);

        // init spinner adapters
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.searchSortListArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.searchTypeListArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);

        // listener for search button
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifySearchTermOk()) {
                    executeSearch();
                }
            }
        });

        // wait until location information is ready
        mProgressDialog.show();
        LocationHelper.getInstance().listenToLocation(this);

    }

    @Override
    public void onLocationConnectionReady(Location location) {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // connect Google Location client
        LocationHelper.getInstance().connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disconnect Google Location client
        LocationHelper.getInstance().disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // when menu back button pressed, just finish()
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**********************************************************************
     * Frugalist Deal Search
     **********************************************************************/

    /**
     * Verify search term is OK
     * @return
     */
    private boolean verifySearchTermOk() {
        if (mInputSearch.getText().toString().isEmpty()) {
            Utils.showAlertDialog(this, "Enter a search term!");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Do deal search
     */
    private void executeSearch() {

        String searchTerm = mInputSearch.getText().toString();
        Location location = LocationHelper.getInstance().getLastLocation();
        Settings settings = UserHelper.getUserSettings(this);
        int sortType = SORT_TYPE[mSortSpinner.getSelectedItemPosition()];

        // if we don't have location, just stop
        if (location == null) {
            Snackbar.make(findViewById(android.R.id.content), "Location not available!",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        // check which type was selected & call API
        if (mTypeSpinner.getSelectedItemPosition() == SearchType.PRODUCT.value) {

            FrugalistServiceHelper.doGetListByProduct(mFrugalistDealListCallback, searchTerm,
                    (float) location.getLatitude(), (float) location.getLongitude(),
                    settings.getSearchRadius(), sortType, settings.getRatingThreshold());

        } else if (mTypeSpinner.getSelectedItemPosition() == SearchType.STORE.value) {

            FrugalistServiceHelper.doGetListByStore(mFrugalistDealListCallback, searchTerm,
                    (float) location.getLatitude(), (float) location.getLongitude(),
                    settings.getSearchRadius(), sortType, settings.getRatingThreshold());
        }
    }

    /**
     * Called after deal search completes
     * @param dealList
     */
    private void onDealSearchComplete(FrugalistResponse.DealList dealList) {
        // convert response Deal items to view model Deal list
        List<AbstractListing> newDealList = Deal.getDealListFromResponseList(dealList);

        // replace data in recycler view
        mListAdapter.replaceData(newDealList);
    }

    /** callback for deal list */
    Callback<FrugalistResponse.DealList> mFrugalistDealListCallback = new Callback<FrugalistResponse.DealList>() {

        @Override
        public void onResponse(
                Call<FrugalistResponse.DealList> call,
                Response<FrugalistResponse.DealList> response
        ) {
            if (response.isSuccess()) {

                FrugalistResponse.DealList deals = response.body();
                Log.i(TAG, deals.toString());
                onDealSearchComplete(deals);

            } else {
                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {/* not handling */}
            }

            mProgressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<FrugalistResponse.DealList> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mProgressDialog.dismiss();
        }

    };

}
