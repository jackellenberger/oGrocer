package quokka.jellenberger.ogrocer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jacka on 2/19/2016.
 */

public class ItineraryRecyclerAdapter extends RecyclerView.Adapter<ItineraryRecyclerAdapter.ViewHolder> {

    ItineraryObject[] mItineraryList;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (mItineraryList[position].type == "Destination")
            return 0;
        else if (mItineraryList[position].type == "Direction")
            return 1;
        else //if (mItineraryList[position] == "Ingredient")
            return 2;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItineraryRecyclerAdapter(ItineraryObject[] ItineraryList)
    {
        mItineraryList = ItineraryList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItineraryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        Log.d("viewtype",String.valueOf(viewType));
        View v;
        if (viewType < 2)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_recycler_item_direction, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_recycler_item_ingredient, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView) holder.mView.findViewById(R.id.recycler_item_text)).setText(mItineraryList[position].innerString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItineraryList.length;
    }
}