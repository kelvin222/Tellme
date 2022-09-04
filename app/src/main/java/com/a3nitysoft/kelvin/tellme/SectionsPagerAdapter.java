package com.a3nitysoft.kelvin.tellme;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                ReviewFragment reviewFragment = new ReviewFragment();
                return reviewFragment;

            case 1:
                TaggedFragment taggedFragment = new TaggedFragment();
                return taggedFragment;

            case 2:
                NotifFragment notifFragment = new NotifFragment();
                return  notifFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "REVIEWS";

            case 1:
                return "TAGGED REVIEWS";

            case 2:
                return "NOTIFICATIONS";

            default:
                return null;
        }

    }

}
