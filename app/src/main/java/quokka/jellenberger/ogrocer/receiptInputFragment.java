package quokka.jellenberger.ogrocer;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jacka on 2/24/2016.
 */
public class ReceiptInputFragment extends AppCompatActivity {

    private ReceiptInputFragment _activityContect;
    private Toolbar _toolbar;

    private RecyclerView mLocationCardRecycler;
    private FloatingActionButton mFAB;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<ItineraryObject> ItineraryObjects;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        //if (extras != null) {
            ItineraryObjects = getIntent().getParcelableArrayListExtra("itineraryObjects");
        //}
        setContentView(R.layout.receipt_input_layout);

        _activityContect = (ReceiptInputFragment) this;

        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLocationCardRecycler = (RecyclerView) findViewById(R.id.receipt_location_recylcer);
        mLayoutManager = new LinearLayoutManager(this);
        mLocationCardRecycler.setLayoutManager(mLayoutManager);

        Set<String> locations = new HashSet<>();
        for (ItineraryObject obj : ItineraryObjects){
            locations.add(obj.store);
        }

        mAdapter = new ReceiptInputLocationAdapter(ItineraryObjects,locations);
        mLocationCardRecycler.setAdapter(mAdapter);


        mFAB = (FloatingActionButton) findViewById(R.id.receipt_add_item);
        mFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accentColor)));
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }
}
