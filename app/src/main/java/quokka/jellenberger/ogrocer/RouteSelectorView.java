package quokka.jellenberger.ogrocer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jacka on 2/9/2016.
 */
public class RouteSelectorView extends AppCompatActivity {

    //APP BAR
    private Toolbar _toolbar;

    //CARD RECYCLER
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String[] mIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            mIngredients = (String[]) extras.get("ingredients");
        }
        setContentView(R.layout.route_selector_frag_layout);
        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _toolbar.findViewById(R.id.add_receipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rsf = new Intent(v.getContext(), ReceiptInputFragment.class);
                v.getContext().startActivity(rsf);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.routes_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String[] RouteDescriptions = {"Cheapest","Closest","Highest Rated", "Custom"};
        int[] RouteMaps = {R.drawable.multi_map, R.drawable.short_map, R.drawable.long_map, R.drawable.medium_map};
        mAdapter = new RouteRecyclerAdapter(RouteDescriptions, RouteMaps, mIngredients);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onResume(){
        super.onResume();
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
