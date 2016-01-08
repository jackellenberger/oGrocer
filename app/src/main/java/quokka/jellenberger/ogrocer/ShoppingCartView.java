package quokka.jellenberger.ogrocer;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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

/**
 * Created by jellenberger on 12/15/15.
 */
public class ShoppingCartView extends AppCompatActivity
{

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";


    //APP BAR
    private Toolbar _toolbar;

    //TABS
    ViewPager _pager;
    AdapterSlidingTab _adapter;
    SlidingTabLayout _tabs;
    CharSequence _titles[] = {"Cart", "Saved"};
    int _numtabs = _titles.length;

    AbstractExpandableDataProvider _dataProviders[] = {null,null};


    //DRAWER
    DrawerLayout _DrawerLayout;
    ActionBarDrawerToggle _DrawerToggle;
    RecyclerView _DrawerRecycler;
    RecyclerView.Adapter _DrawerRecyclerAdapter;
    RecyclerView.LayoutManager _DrawerRecyclerLayout;
    String _DrawerStrings[] = {"Cart","History","Recipes","Stores","My Contributions","Settings"};
    int _DrawerIcons[] = {R.drawable.ic_action_cart, R.drawable.ic_action_clock,R.drawable.ic_action_book,R.drawable.ic_action_location,R.drawable.ic_action_users,R.drawable.ic_action_settings};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_view_layout);

        //>> APPBAR
        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        //<< APPBAR

        //>> TABS
        _adapter =  new AdapterSlidingTab(getSupportFragmentManager(),_titles,_numtabs);
        _pager = (ViewPager) findViewById(R.id.cart_tab_pager);
        _pager.setAdapter(_adapter);
        _tabs = (SlidingTabLayout) findViewById(R.id.cart_tab_bar);
        _tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        _tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentColor);
            }
        });
        _tabs.setViewPager(_pager);
        //<< TABS

        //>> NAVIGATION DRAWER
        _DrawerRecycler = (RecyclerView) findViewById(R.id.left_RecyclerView);
        _DrawerRecycler.setHasFixedSize(true);
        _DrawerRecyclerAdapter = new AdapterNavigationDrawer(_DrawerStrings, _DrawerIcons);
        _DrawerRecyclerLayout = new LinearLayoutManager(this);
        _DrawerRecycler.setAdapter(_DrawerRecyclerAdapter);
        _DrawerRecycler.setLayoutManager(_DrawerRecyclerLayout);
        _DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _DrawerToggle = new ActionBarDrawerToggle(this,_DrawerLayout,_toolbar,R.string.drawer_open,R.string.drawer_close){
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
        _DrawerLayout.setDrawerListener(_DrawerToggle);
        _DrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                _DrawerToggle.syncState();
            }
        });
        //<< NAVIGATION DRAWER

    }
    public int getCurrentTab()
    {
        return _pager.getCurrentItem();
    }

    public void onGroupItemRemoved(int groupPosition) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_group_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a child item is removed
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemRemoved(int groupPosition, int childPosition) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_child_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a group item is pinned
     *
     * @param groupPosition The position of the group item within data set
     */
    public void onGroupItemPinned(int groupPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, RecyclerView.NO_POSITION);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    /**
     * This method will be called when a child item is pinned
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemPinned(int groupPosition, int childPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, childPosition);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
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
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractExpandableDataProvider.ChildData data = getDataProvider().getChildItem(groupPosition, childPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((ShoppingCartTabContent) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    private void onItemUndoActionClicked() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        final long result = getDataProvider().undoLastRemoval();

        if (result == RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION) {
            return;
        }

        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(result);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(result);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            ((ShoppingCartTabContent) fragment).notifyGroupItemRestored(groupPosition);
        } else {
            // child item
            ((ShoppingCartTabContent) fragment).notifyChildItemRestored(groupPosition, childPosition);
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
        //TODO: make this actually work?
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ShoppingCartTabContent) fragment).getDataProvider();
    }

    public void registerDataProvider(int index, AbstractExpandableDataProvider dp){
        _dataProviders[index] = dp;
    }

}
//TODO: app crashes when rapidly clicking buttons that move or remove recycler items
//TODO: Update on swipe left group action to do what the plus button currently does