package quokka.jellenberger.ogrocer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by jacka on 2/24/2016.
 */
public class ReceiptInputItemAdapter extends RecyclerView.Adapter<ReceiptInputItemAdapter.ViewHolder> {

    public List<ItineraryObject> mItems;
    ReceiptInputItemAdapter mAdapter;

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
    public ReceiptInputItemAdapter(List<ItineraryObject> items)
    {
        mItems = items;
        mAdapter = this;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReceiptInputItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_input_location_item_recylcer_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((CheckBox) holder.mView.findViewById(R.id.item_checkbox)).setChecked(mItems.get(position).checked);
        ((TextView) holder.mView.findViewById(R.id.ingredient_text)).setText(mItems.get(position).innerString);
        Spinner unitSpinner = (Spinner) holder.mView.findViewById(R.id.amount_type_spinner);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView)parentView.getChildAt(0)).setTextColor(parentView.getContext().getResources().getColor(R.color.grey700));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {            }
        });
        holder.mView.findViewById(R.id.item_recycler_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.mItems.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
