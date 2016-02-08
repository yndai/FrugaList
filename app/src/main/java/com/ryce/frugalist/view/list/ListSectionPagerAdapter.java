package com.ryce.frugalist.view.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Tony on 2016-02-06.
 *
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the list sections.
 */
public class ListSectionPagerAdapter extends FragmentPagerAdapter {

    public ListSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ListSectionFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Near Me";
            case 1:
                return "Hottest";
            case 2:
                return "Freebies";
        }
        return null;
    }
}
