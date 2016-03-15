package com.ryce.frugalist.view.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Tony on 2016-02-06.
 *
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the list sections.
 */
public class ListSectionPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Enum of section types
     */
    public enum ListSection {
        NEARBY(0), FREEBIES(1), POSTED(2), SAVED(3);
        int val;
        ListSection(int val) { this.val = val; }
        public int toInteger() { return val; }
    }

    public ListSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        int sectionNumber = ListSection.values()[position].toInteger();
        return ListSectionFragment.newInstance(sectionNumber);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Near Me";
            case 1:
                return "Freebies";
            case 2:
                return "Posted";
            case 3:
                return "Saved";
        }
        return null;
    }
}
