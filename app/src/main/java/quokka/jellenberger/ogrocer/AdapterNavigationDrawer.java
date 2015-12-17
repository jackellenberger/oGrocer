package quokka.jellenberger.ogrocer;

/**
 * Created by jellenberger on 5/10/15.
 */

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterNavigationDrawer extends RecyclerView.Adapter<AdapterNavigationDrawer.ViewHolder> {

    private String _DrawerStrings[]; // String Array to store the passed titles Value from MainActivity.java
    private int _Icons[];       // Int Array to store the passed icons resource value from MainActivity.java


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText);

            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(itemView.getContext(), R.style.TextHeader);
            } else {
                textView.setTextAppearance(R.style.TextHeader);
            }

            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
        }
    }

    AdapterNavigationDrawer(String Titles[], int Icons[]){ // MyAdapter Constructor with titles and icons parameter
        _DrawerStrings = Titles;
        _Icons = Icons;
    }

    @Override
    public AdapterNavigationDrawer.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_drawer_item,parent,false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(AdapterNavigationDrawer.ViewHolder holder, int position) {
        holder.textView.setText(_DrawerStrings[position]);
        holder.imageView.setImageResource(_Icons[position]);
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return _DrawerStrings.length;
    }
}