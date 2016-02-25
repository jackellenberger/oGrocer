package quokka.jellenberger.ogrocer;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by jacka on 2/24/2016.
 */
public class ReceiptInputLocationAdapter extends RecyclerView.Adapter<ReceiptInputLocationAdapter.ViewHolder> {

    List<ItineraryObject> mItineraryObjects;
    String[] mLocationSet;
    HashMap<String, List<ItineraryObject>> mItemsByLocation;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptInputLocationAdapter(List<ItineraryObject> itineraryObjects, Set<String> locations)
    {
        mItineraryObjects = itineraryObjects;
        mLocationSet = locations.toArray(new String[locations.size()]);
        mItemsByLocation = new HashMap<>();

        for (ItineraryObject obj : mItineraryObjects){
            String key = obj.store;
            if (!mItemsByLocation.containsKey(key)) {
                List<ItineraryObject> starterList = new ArrayList<>();
                mItemsByLocation.put(key, starterList);
            }
            if (obj.type.equals("Ingredient"))
                mItemsByLocation.get(key).add(obj);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReceiptInputLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_input_location_card, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mView.findViewById(R.id.location_name)).setText(mLocationSet[position]);
        ((ImageView) holder.mView.findViewById(R.id.map_frame_image)).setImageResource(R.drawable.medium_map);

        RecyclerView itemRecycler = (RecyclerView) holder.mView.findViewById(R.id.receipt_item_per_location_recylcer);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(holder.mView.getContext());
        RecyclerView.Adapter itemAdapter = new ReceiptInputItemAdapter(mItemsByLocation.get(mLocationSet[position]));
        itemRecycler.setLayoutManager(lm);
        itemRecycler.setAdapter(itemAdapter);
        ViewGroup.LayoutParams adjustedHeightParams = itemRecycler.getLayoutParams();
        //adjustedHeightParams.height = R.dimen.cart_recycler_item_height;
        itemRecycler.setLayoutParams(adjustedHeightParams);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLocationSet.length;
    }
}
