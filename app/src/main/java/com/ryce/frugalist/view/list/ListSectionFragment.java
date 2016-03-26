package com.ryce.frugalist.view.list;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.Settings;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.LocationHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.util.Utils;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter.ListSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tony on 2016-02-06.
 *
 * Fragment containing a list section
 */
public class ListSectionFragment extends Fragment implements LocationHelper.LocationConnectionListener {

    private static final String TAG = ListSectionFragment.class.getSimpleName();

    /** Enum of listing types */
    public enum ListingType {
        EMPTY(-1), DEAL(0), FREEBIE(1);
        int val;
        ListingType(int val) { this.val = val; }
        public int toInteger() { return val; }
    }

    /** The fragment argument representing the section number for this fragment */
    public static final String ARG_SECTION_NUMBER = "section_number";

    ListSection mListSection;
    RecyclerView mRecyclerView;
    ListSectionRecyclerAdapter mListAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressDialog mProgressDialog;

    /**
     * Do not call this
     */
    public ListSectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListSectionFragment newInstance(int sectionNumber) {
        ListSectionFragment fragment = new ListSectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

        // get swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshContainer);

        // get section number
        int listSection = getArguments().getInt(ARG_SECTION_NUMBER);
        mListSection = ListSection.values()[listSection];

        // init recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mainListView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, false, true));

        // init recycler view adapter
        mListAdapter = new ListSectionRecyclerAdapter(getActivity(), new ArrayList<AbstractListing>(), ListingType.DEAL, mListSection);
        mRecyclerView.setAdapter(mListAdapter);

        // init progress dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_loading));

        // TODO: find a good way to handle this...
        if (!Utils.isConnected(getContext())) {
            Log.i(TAG, "No internet!");
            return rootView;
        }

        // determine the List section type and fetch data accordingly
        if (mListSection == ListSection.NEARBY) {

            // set refresh listener
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeFetchDealList();
                }
            });

            // need to wait for location helper to connect before fetching nearby deals...
            LocationHelper.getInstance().listenToLocation(this);

        } else if (mListSection == ListSection.POSTED) {

            // set refresh listener
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeFetchPostedDeals();
                }
            });

            // fetch posted deals
            mProgressDialog.show();
            executeFetchPostedDeals();

        } else if (mListSection == ListSection.SAVED) {

            // set refresh listener
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeFetchBookmarks();
                }
            });

            // fetch bookmarks
            mProgressDialog.show();
            executeFetchBookmarks();
        }

        return rootView;
    }

    /**
     * Wait for connection to location client before fetching list
     * @param location
     */
    @Override
    public void onLocationConnectionReady(Location location) {

        // Fetch nearby list when location data is ready
        if (mListSection == ListSection.NEARBY) {

            mProgressDialog.show();
            executeFetchDealList();

        }
    }

    /**************************************************
     * List fetch methods
     **************************************************/

    /**
     * Fetch bookmarked deals
     */
    private void executeFetchBookmarks() {
        FrugalistServiceHelper.doGetBookmarksList(mFrugalistDealListCallback,
                UserHelper.getCurrentUser(getContext()).getId());
    }

    /**
     * Fetch user's posted deals
     */
    private void executeFetchPostedDeals() {
        FrugalistServiceHelper.doGetListByAuthor(mFrugalistDealListCallback,
                UserHelper.getCurrentUser(getContext()).getId());
    }

    /**
     * Fetch deal list
     */
    private void executeFetchDealList() {

        // get current location
        Location loc = LocationHelper.getInstance().getLastLocation();

        // get search radius
        Settings settings = UserHelper.getUserSettings(getContext());

        // if location not available, just get all deals
        if (loc != null) {
            FrugalistServiceHelper.doGetNearbyDealList(mFrugalistDealListCallback,
                    (float) loc.getLatitude(), (float) loc.getLongitude(),
                    settings.getSearchRadius(), settings.getRatingThreshold());
        } else {
            Log.i(TAG, "Location not available");
            FrugalistServiceHelper.doGetDealList(mFrugalistDealListCallback,
                    settings.getRatingThreshold());
        }
    }

    /**
     * Called after deal list response arrives
     * @param dealList
     */
    private void onDealListFetchComplete(FrugalistResponse.DealList dealList) {
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
                onDealListFetchComplete(deals);

            } else {
                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {/* not handling */}
            }

            // finish refreshing
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<FrugalistResponse.DealList> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            // finish refreshing
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
