package quokka.jellenberger.ogrocer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacka on 2/19/2016.
 */
public class ItineraryView extends AppCompatActivity {
    String[] mIngredients;
    int mItineraryMap;
    //APP BAR
    private Toolbar _toolbar;

    //CARD RECYCLER
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            mIngredients = (String[]) extras.get("ingredients");
            mItineraryMap = (int) extras.get("itineraryMap");
        }
        setContentView(R.layout.itinerary_layout);

        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((ImageView) findViewById(R.id.map_frame)).setImageResource(mItineraryMap);

        mRecyclerView = (RecyclerView) findViewById(R.id.directions_items_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<ItineraryObject> ItineraryList = new ArrayList<ItineraryObject>();
        int index = 0;
        ItineraryList.add(index++, new ItineraryObject("Destination","Alice's Grocery",R.drawable.ic_store, -1));
        ItineraryList.add(index++, new ItineraryObject("Direction","Turn Left",R.drawable.ic_directions,1));
        ItineraryList.add(index++, new ItineraryObject("Direction","Turn Right",R.drawable.ic_directions,2));
        ItineraryList.add(index++, new ItineraryObject("Direction","Destination on Right",R.drawable.ic_directions,3));
        for (String ingredient : mIngredients){
            ItineraryList.add(index++, new ItineraryObject("Ingredient",ingredient,R.drawable.ic_action_cart,-1));
        }
        mAdapter = new ItineraryRecyclerAdapter(ItineraryList.toArray(new ItineraryObject[ItineraryList.size()]));
        mRecyclerView.setAdapter(mAdapter);
    }
}