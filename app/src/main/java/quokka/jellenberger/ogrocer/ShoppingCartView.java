package quokka.jellenberger.ogrocer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by jellenberger on 12/15/15.
 */
public class ShoppingCartView extends AppCompatActivity
{
    //APP BAR
    private Toolbar _toolbar;

    //TABS
    ViewPager _pager;
    AdapterSlidingTab _adapter;
    SlidingTabLayout _tabs;
    CharSequence _titles[] = {"Cart", "Saved"};
    int _numtabs = _titles.length;

    //DRAWER
    DrawerLayout _DrawerLayout;
    ActionBarDrawerToggle _DrawerToggle;
    RecyclerView _DrawerRecycler;
    RecyclerView.Adapter _DrawerRecyclerAdapter;
    RecyclerView.LayoutManager _DrawerRecyclerLayout;
    String _DrawerStrings[] = {"Item 1","Item 2"};
    int _DrawerIcons[] = {R.mipmap.ic_launcher, R.mipmap.ic_launcher};

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

}
