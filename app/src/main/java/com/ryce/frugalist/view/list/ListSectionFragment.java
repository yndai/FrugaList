package com.ryce.frugalist.view.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter.ListSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2016-02-06.
 *
 * Fragment containing a list section
 */
public class ListSectionFragment extends Fragment {

    public static final int THUMBNAIL_HEIGHT = 80;
    public static final int THUMBNAIL_WIDTH = 80;

    /**
     * Enum of listing types
     */
    public enum ListingType {
        NONE(-1), DEAL(0), FREEBIE(1);
        int val;
        ListingType(int val) { this.val = val; }
        public int toInteger() { return val; }
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListSection mListSection;
    private ListSectionRecyclerAdapter mListAdapter;

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
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.mainListView);

        // get section number
        int listSection = getArguments().getInt(ARG_SECTION_NUMBER);
        mListSection = ListSection.values()[listSection];

        // TODO: only display for nearby for now...
        if (listSection == ListSection.NEARBY.toInteger()) {

            // use a linear layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);


            List<AbstractListing> items = new ArrayList<AbstractListing>(MockDatastore.getInstance().getDeals().values());

            mListAdapter = new ListSectionRecyclerAdapter(getContext(), items, ListingType.DEAL, mListSection);
            MockDatastore.getInstance().addDealsListener(mListAdapter);
            recyclerView.setAdapter(mListAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, false, true));

            return rootView;

        } else if (listSection == ListSection.SAVED.toInteger()) {

            // use a linear layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);


            List<AbstractListing> items = new ArrayList<AbstractListing>(MockDatastore.getInstance().getBookmarks().values());

            mListAdapter = new ListSectionRecyclerAdapter(getContext(), items, ListingType.DEAL, mListSection);
            MockDatastore.getInstance().addBookmarksListener(mListAdapter);
            recyclerView.setAdapter(mListAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, false, true));

            return rootView;

        } else {
            return rootView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListSection == ListSection.NEARBY) {
            MockDatastore.getInstance().removeDealsListener(mListAdapter);
        } else if (mListSection == ListSection.SAVED) {
            MockDatastore.getInstance().removeBookmarksListener(mListAdapter);
        }
    }
}
