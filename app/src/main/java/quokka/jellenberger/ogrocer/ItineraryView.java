package quokka.jellenberger.ogrocer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
    private ItineraryView _activityContect;

    private Button _mUpdatePricesButton;

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

        _activityContect = (ItineraryView) this;

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

        final List<ItineraryObject> ItineraryList = new ArrayList<ItineraryObject>();
        int index = 0;
        ItineraryList.add(index++, new ItineraryObject("Destination","Alice's Grocery",R.drawable.ic_action_location, -1,"Alice's Grocery"));
        ItineraryList.add(index++, new ItineraryObject("Direction","Turn Left on Woodlawn Dr",R.drawable.ic_action_arrow_left,1,"Alice's Grocery"));
        ItineraryList.add(index++, new ItineraryObject("Direction","Turn Right onto 55th St",R.drawable.ic_action_arrow_right,2,"Alice's Grocery"));
        ItineraryList.add(index++, new ItineraryObject("Direction","Destination on Right",R.drawable.ic_store,3,"Alice's Grocery"));
        int ingredientCounter = 0;
        for (String ingredient : mIngredients){
            if (ingredientCounter > 2)
                break;
            ItineraryList.add(index++, new ItineraryObject("Ingredient",ingredient,false,ingredientCounter++,"Alice's Grocery"));
        }
        if (mIngredients.length > ingredientCounter) {
            ItineraryList.add(index++, new ItineraryObject("Destination", "Bob's Deli", R.drawable.ic_action_location, -1, "Bob's Deli"));
            ItineraryList.add(index++, new ItineraryObject("Direction", "Continue on 55th St for 1 mi", R.drawable.ic_action_arrow_up, 1, "Bob's Deli"));
            ItineraryList.add(index++, new ItineraryObject("Direction", "Destination on Left", R.drawable.ic_store, 3, "Bob's Deli"));
            for (int i = ingredientCounter; i < mIngredients.length; i++) {
                ItineraryList.add(index++, new ItineraryObject("Ingredient", mIngredients[i], false, ingredientCounter++, "Alice's Grocery"));
            }
        }

        mAdapter = new ItineraryRecyclerAdapter(ItineraryList.toArray(new ItineraryObject[ItineraryList.size()]));
        mRecyclerView.setAdapter(mAdapter);

        _mUpdatePricesButton = (Button) findViewById(R.id.price_update_button);
        _mUpdatePricesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rsf = new Intent(v.getContext(), ReceiptInputFragment.class);
                rsf.putParcelableArrayListExtra("itineraryObjects", (ArrayList<? extends Parcelable>) ItineraryList);
                v.getContext().startActivity(rsf);
            }
        });
    }
}
