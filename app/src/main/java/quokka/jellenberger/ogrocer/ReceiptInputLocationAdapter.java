package quokka.jellenberger.ogrocer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jacka on 2/24/2016.
 */
public class ReceiptInputLocationAdapter extends RecyclerView.Adapter<ReceiptInputLocationAdapter.ViewHolder> {

    String[] mLocationSet;
    HashMap<String, List<ItineraryObject>> mItemsByLocation;
    RecyclerView mItemRecycler;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public RecyclerView mRecyclerView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptInputLocationAdapter(HashMap<String, List<ItineraryObject>> itemsByLocation)
    {
        mItemsByLocation = itemsByLocation;
        mLocationSet = mItemsByLocation.keySet().toArray(new String[]{});
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

        mItemRecycler = (RecyclerView) holder.mView.findViewById(R.id.receipt_item_per_location_recylcer);
        VariableLinearLayoutManager vlm = new VariableLinearLayoutManager(holder.mView.getContext(),RecyclerView.VERTICAL,false);
        final RecyclerView.Adapter itemAdapter = new ReceiptInputItemAdapter(mItemsByLocation.get(mLocationSet[position]));
        mItemRecycler.setLayoutManager(vlm);
        mItemRecycler.setAdapter(itemAdapter);
        holder.mRecyclerView = mItemRecycler;

        View addMoreItemsToLocationButton = holder.mView.findViewById(R.id.add_item_to_location_holder);
        addMoreItemsToLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiptInputItemAdapter) itemAdapter).mItems.add(new ItineraryObject("New Item"));
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLocationSet.length;
    }

    public void addEmptyLocation(){
        List<ItineraryObject> emptyItems = new ArrayList<ItineraryObject>();
        mItemsByLocation.put("Choose Location",emptyItems);
        mLocationSet = mItemsByLocation.keySet().toArray(new String[]{});
    }
}
