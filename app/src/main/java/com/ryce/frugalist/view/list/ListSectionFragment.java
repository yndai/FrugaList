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
import com.ryce.frugalist.model.Deal;

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
     * Enum of section types
     */
    public enum ListSections {
        NEARBY(0), HOTTEST(1), FREEBIE(2);
        int val;
        ListSections(int val) { this.val = val; }
        public int toInteger() { return val; }
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";


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

    public static List<AbstractListing> items = new ArrayList<AbstractListing>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.mainListView);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // make this a class level var
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        String test_apple = "http://imgur.com/2dozFb1.jpg";
        String test_cheese = "http://imgur.com/G0f4Lbb.jpg";
        String test_peach = "http://imgur.com/M5b16xH.jpg";

        if (items.isEmpty()) {
            // TEST DATA
            // TODO: should move data to model module somehow so all activities can access
            Deal deal = new Deal(test_peach, "2.99", "Peachy", 5, "lb", "Zehr's");
            items.add(deal);

            deal = new Deal(test_apple, "0.99", "Apple", 7, "lb", "ValuMart");
            items.add(deal);

            deal = new Deal(test_cheese, "3.99", "Cheese", 9, "lb", "Sobey's");
            items.add(deal);

            deal = new Deal(test_apple, "0.89", "Apples", 1, "lb", "Zehr's");
            items.add(deal);

            deal = new Deal(test_apple, "1.99", "Appless", -6, "lb", "Metro");
            items.add(deal);

            deal = new Deal(test_cheese, "6.99", "Cheese", -5, "lb", "Sobey's");
            items.add(deal);

            deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's");
            items.add(deal);

            deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's");
            items.add(deal);
        }

        recyclerView.setAdapter(new ListSectionRecyclerAdapter(items, ListingType.DEAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, false, true));

        return rootView;
    }



}
