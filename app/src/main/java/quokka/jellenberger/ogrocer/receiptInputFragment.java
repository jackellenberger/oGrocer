package quokka.jellenberger.ogrocer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
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
    TextView mFinishedButton;

    Set<String> mLocations;
    HashMap<String, List<ItineraryObject>> mItemsByLocation;

    List<ItineraryObject> ItineraryObjects;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ItineraryObjects = getIntent().getParcelableArrayListExtra("itineraryObjects");
        }
        else
            ItineraryObjects = new ArrayList<>();

        setContentView(R.layout.receipt_input_layout);

        _activityContect = (ReceiptInputFragment) this;

        _toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(_toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLocationCardRecycler = (RecyclerView) findViewById(R.id.receipt_location_recylcer);
        mLayoutManager = new LinearLayoutManager(this);
        mLocationCardRecycler.setLayoutManager(mLayoutManager);

        mLocations = new HashSet<>();
        for (ItineraryObject obj : ItineraryObjects){
            mLocations.add(obj.store);
        }

        mItemsByLocation = new HashMap<>();
        for (ItineraryObject obj : ItineraryObjects){
            String key = obj.store;
            if (!mItemsByLocation.containsKey(key)) {
                List<ItineraryObject> starterList = new ArrayList<>();
                mItemsByLocation.put(key, starterList);
            }
            if (obj.type.equals("Ingredient"))
                mItemsByLocation.get(key).add(obj);
        }

        mAdapter = new ReceiptInputLocationAdapter(mItemsByLocation);
        mLocationCardRecycler.setAdapter(mAdapter);


        mFAB = (FloatingActionButton) findViewById(R.id.receipt_add_item);
        mFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accentColor)));
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiptInputLocationAdapter) mAdapter).addEmptyLocation();
                mAdapter.notifyDataSetChanged();
            }
        });

        mFinishedButton = (TextView) findViewById(R.id.finished_button);
        mFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i < mLocationCardRecycler.getChildCount(); i++) {
                    RecyclerView locsItemRecycler = ((ReceiptInputLocationAdapter.ViewHolder) mLocationCardRecycler.findViewHolderForAdapterPosition(i)).mRecyclerView;
                    for (int j = 0; j < locsItemRecycler.getChildCount(); j++){
                        View itemView = ((ReceiptInputItemAdapter.ViewHolder) locsItemRecycler.findViewHolderForAdapterPosition(j)).mView;
                        boolean isChecked = ((CheckBox) itemView.findViewById(R.id.item_checkbox)).isChecked();
                        String itemName = ((EditText) itemView.findViewById(R.id.ingredient_text)).getText().toString();
                        double itemPrice = Double.parseDouble(((EditText) itemView.findViewById(R.id.price_input)).getText().toString());
                        double itemQuantity = Double.parseDouble(((EditText) itemView.findViewById(R.id.amount_input)).getText().toString());
                        String itemUnit = ((Spinner) itemView.findViewById(R.id.amount_type_spinner)).getSelectedItem().toString();

                        if (isChecked && (itemPrice <= 0.0 || itemQuantity <= 0.0)){
                            itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.primaryColorLight));
                            mFinishedButton.setText("Please Enter Price and Quantity To Continue");
                            mFinishedButton.setBackgroundColor(v.getContext().getResources().getColor(R.color.primaryColorDark));
                            return;
                        }
                    }

                }
                Intent rsf = new Intent(v.getContext(), ShoppingCartView.class);
                rsf.putExtra("didContribute", true);
                v.getContext().startActivity(rsf);
            }
        });
    }
}
