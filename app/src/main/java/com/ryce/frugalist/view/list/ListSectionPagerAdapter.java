package com.ryce.frugalist.view.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Tony on 2016-02-06.
 *
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the list sections.
 */
public class ListSectionPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Enum of section types
     */
    public enum ListSection {
        NEARBY(0), POSTED(1), SAVED(2);
        int val;
        ListSection(int val) { this.val = val; }
        public int toInteger() { return val; }
    }

    public ListSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Called to instantiate the fragment for the given page.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        int sectionNumber = ListSection.values()[position].toInteger();
        return ListSectionFragment.newInstance(sectionNumber);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Near Me";
//            case 1:
//                return "Freebies";
            case 1:
                return "Posted";
            case 2:
                return "Saved";
        }
        return null;
    }
}
