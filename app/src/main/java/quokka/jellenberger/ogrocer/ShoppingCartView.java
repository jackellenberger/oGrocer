package quokka.jellenberger.ogrocer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jellenberger on 12/15/15.
 */
public class ShoppingCartView extends AppCompatActivity
{

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";


    //Local item database
    public ItemDatabase mCartItemDB;

    //APP BAR
    private Toolbar _toolbar;

    //TABS
    ViewPager mViewPager;
    AdapterSlidingTab mSlidingTabAdapter;
    SlidingTabLayout mSlidingTabLayout;
    CharSequence _titles[] = {"Cart", "Saved"};
    int _numtabs = _titles.length;

    AbstractExpandableDataProvider mDataProviders[] = {null,null};


    //DRAWER
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    RecyclerView mDrawerRecycler;
    RecyclerView.Adapter mDrawerRecyclerAdapter;
    RecyclerView.LayoutManager mDrawerRecyclerLayout;
    String _DrawerStrings[] = {"Cart","History","Recipes","Stores","My Contributions","Settings"};
    int _DrawerIcons[] = {R.drawable.ic_action_cart, R.drawable.ic_action_clock,R.drawable.ic_action_book,R.drawable.ic_action_location,R.drawable.ic_action_users,R.drawable.ic_action_settings};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_view_layout);

        //>> ITEM DATABASE
        long itemCount = 1;
        deleteDatabase(ItemDatabaseHelper.DATABASE_NAME);
        mCartItemDB = new ItemDatabase(this, "cartDB");
        try {
            mCartItemDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCartItemDB.addItem(new ItemInfo(itemCount++,"Bread", Arrays.asList(2.11d,3.00d,2.30d), Arrays.asList("Jewel","Target","Costco"),0));
        mCartItemDB.addItem(new ItemInfo(itemCount++,"Apple Sauce", Arrays.asList(3.44d,4.80d,3.75d), Arrays.asList("Jewel","Target","Costco"),0));
        mCartItemDB.addItem(new ItemInfo(itemCount++,"Oatmeal", Arrays.asList(2.64d,3.89d,3.20d), Arrays.asList("Jewel","Target","Costco"),0));


        mCartItemDB.addItem(new ItemInfo(itemCount++,"Kiwis", Arrays.asList(2.11d,3.00d,2.30d), Arrays.asList("Jewel","Target","Costco"),1));
        mCartItemDB.addItem(new ItemInfo(itemCount++,"Ketchup", Arrays.asList(3.44d,4.80d,3.75d), Arrays.asList("Jewel","Target","Costco"),1));
        mCartItemDB.addItem(new ItemInfo(itemCount++,"Watermelon", Arrays.asList(2.64d,3.89d,3.20d), Arrays.asList("Jewel","Target","Costco"),1));

        //<< ITEM DATABASE

        //>> APPBAR
        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        _toolbar.findViewById(R.id.add_receipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rsf = new Intent(v.getContext(), ReceiptInputFragment.class);
                v.getContext().startActivity(rsf);
            }
        });
        //<< APPBAR

        //>> TABS
        mSlidingTabAdapter =  new AdapterSlidingTab(getSupportFragmentManager(),_titles,_numtabs);
        mViewPager = (ViewPager) findViewById(R.id.cart_tab_pager);
        mViewPager.setAdapter(mSlidingTabAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.cart_tab_bar);
        mSlidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primaryColorReallyLight);
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
        //<< TABS

        //>> NAVIGATION DRAWER
        mDrawerRecycler = (RecyclerView) findViewById(R.id.left_RecyclerView);
        mDrawerRecycler.setHasFixedSize(true);
        mDrawerRecyclerAdapter = new AdapterNavigationDrawer(_DrawerStrings, _DrawerIcons);
        mDrawerRecyclerLayout = new LinearLayoutManager(this);
        mDrawerRecycler.setAdapter(mDrawerRecyclerAdapter);
        mDrawerRecycler.setLayoutManager(mDrawerRecyclerLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,_toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //invalidateOptionsMenu();
            }
            @Override
            public void onDrawerSlide(View drawerView,float slideOffset){
                super.onDrawerSlide(drawerView,slideOffset);
                //this might be useful for something? Allows you to emulate things in relation
                // to the drawer sliding in and out

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        //<< NAVIGATION DRAWER

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("didContribute")) {
            if ((boolean)extras.get("didContribute"))
                Snackbar.make(findViewById(R.id.main_content),"Thank you for contributing! Your reputation has increased",Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(findViewById(R.id.main_content),"You have chosen not to contribute, so your Reputation has decreased",Snackbar.LENGTH_LONG).show();

        }

    }



    // UTILITIES //
    public int getCurrentTab()
    {
        return mViewPager.getCurrentItem();
    }

    public void openFragment(final Fragment fragment) {
        Log.d("openFragment","opening Fragment");
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        ft.replace(R.id.main_content, fragment).addToBackStack(null).commit();
        //TODO: make proper fragment handling mechanic. pushing and popping off the FragMan is poor practice and doesn't support nesting
    }

    public void onGroupItemSwipedOut(int tabID, int groupPosition) {
        String movedText = (tabID == 0) ? " saved for later" : " added to cart";
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                this.mDataProviders[tabID].getGroupItem(groupPosition).getText() + movedText,
                Snackbar.LENGTH_LONG);

        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }
    public void onGroupItemDeleted(int tabID, int groupPosition) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                this.mDataProviders[tabID].getGroupItem(groupPosition).getText() + " deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    public void onGroupItemClicked(int groupPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractExpandableDataProvider.GroupData data = getDataProvider().getGroupItem(groupPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((ShoppingCartTabContent) fragment).notifyGroupItemChanged(groupPosition);
        }
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        Log.d("Child Item Clicked", "I guess");

    }

    private void onItemUndoActionClicked() {
        final long result = getDataProvider().undoLastRemoval();
        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(result);

        if (result == RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION) {
            return;
        }
        if (getCurrentTab() == 0) {
            ((ShoppingCartTabContent)mSlidingTabAdapter.getItem(0)).notifyGroupItemRestored(groupPosition);
        }
        else{
            ((SavedCartTabContent)mSlidingTabAdapter.getItem(1)).notifyGroupItemRestored(groupPosition);
        }
    }

    // implements ExpandableItemPinnedMessageDialogFragment.EventListener
    public void onNotifyExpandableItemPinnedDialogDismissed(int groupPosition, int childPosition, boolean ok) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            getDataProvider().getGroupItem(groupPosition).setPinned(ok);
            ((ShoppingCartTabContent) fragment).notifyGroupItemChanged(groupPosition);
        } else {
            // child item
            getDataProvider().getChildItem(groupPosition, childPosition).setPinned(ok);
            ((ShoppingCartTabContent) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    public AbstractExpandableDataProvider getDataProvider() {
        //TODO: this is hacky and will not work if the user is swiping between tabs real fast
        return mDataProviders[getCurrentTab()];
    }

    public void registerDataProvider(int index, AbstractExpandableDataProvider dp){
        mDataProviders[index] = dp;
    }

}
//TODO: app crashes when rapidly clicking buttons that move or remove recycler items
