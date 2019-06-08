package com.tsf.model.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tsf.model.ViewDetails.Education;
import com.tsf.model.ViewDetails.Personal;
import com.tsf.model.ViewDetails.Profession;

public class TabAdapter extends FragmentPagerAdapter {

    private int totalTabs;

    public TabAdapter( FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return new Personal();
            case 1:
                return new Education();
            case 2:
                return new Profession();
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
