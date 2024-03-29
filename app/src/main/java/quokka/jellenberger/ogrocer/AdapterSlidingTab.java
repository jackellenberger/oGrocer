package quokka.jellenberger.ogrocer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class AdapterSlidingTab extends FragmentStatePagerAdapter {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when AdapterSlidingTab is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the AdapterSlidingTab is created
    FragmentManager _fm;
    // Build a Constructor and assign the passed Values to appropriate values in the class
    public AdapterSlidingTab(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        _fm = fm;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment tabContent;
        if (position == 0) {
            tabContent = new ShoppingCartTabContent();
            tabContent = ShoppingCartTabContent.newInstance(position);
        }
        else {
            tabContent = new SavedCartTabContent();
            tabContent = SavedCartTabContent.newInstance(position);
        }

        return tabContent;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}