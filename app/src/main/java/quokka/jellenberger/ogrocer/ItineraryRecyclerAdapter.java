package quokka.jellenberger.ogrocer;

import android.content.res.ColorStateList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItineraryObject thisObject = mItineraryList[position];
        ((TextView) holder.mView.findViewById(R.id.recycler_item_text)).setText(thisObject.innerString);
        if (thisObject.type == "Destination"){
            ((ImageView) holder.mView.findViewById(R.id.direction_icon)).setImageResource(thisObject.drawableResourceID);
            holder.mView.findViewById(R.id.direction_icon).setBackgroundResource(R.drawable.circle_green);
        }
        else if (thisObject.type == "Direction"){
            ((ImageView) holder.mView.findViewById(R.id.direction_icon)).setImageResource(thisObject.drawableResourceID);
        }
        else if (thisObject.type == "Ingredient"){
            ((CheckBox) holder.mView.findViewById(R.id.ingredient_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        ((View) buttonView.getParent()).setBackgroundColor(buttonView.getContext().getResources().getColor(R.color.accentColorReallyLight));
                        mItineraryList[position].checked=true;
                    }
                    else{
                        ((View) buttonView.getParent()).setBackgroundColor(buttonView.getContext().getResources().getColor(R.color.grey50));
                        mItineraryList[position].checked=false;
                    }
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItineraryList.length;
    }
}