package quokka.jellenberger.ogrocer;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jacka on 2/9/2016.
 */

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder> {
    private String[] mRouteTypes;
    private int[] mRouteDrawables;
    private String[] mIngredients;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RouteRecyclerAdapter(String[] routeNames, int[] routeDrawables, String[] ingredients)
    {
        mRouteTypes = routeNames;
        mRouteDrawables = routeDrawables;
        mIngredients = ingredients;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
        // Create new views (invoked by the layout manager)
    @Override
    public RouteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              final int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card_recycler_item, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rsf = new Intent(v.getContext(), ItineraryView.class);
                rsf.putExtra("ingredients", mIngredients);
                rsf.putExtra("itineraryMap", mRouteDrawables[viewType]);
                v.getContext().startActivity(rsf);
            }
        });
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mCardView.findViewById(R.id.map_frame_text)).setText(mRouteTypes[position]);
        ((ImageView) holder.mCardView.findViewById(R.id.map_frame_image)).setImageResource(mRouteDrawables[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRouteTypes.length;
    }
}