package aad.finalproject.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import aad.finalproject.jhoregatta.Coordinates;
import aad.finalproject.jhoregatta.R;


public class DistanceCalculatorAdapter extends ArrayAdapter<Coordinates> {


    //create some instances
    private Context context;
    private ArrayList<Coordinates> coordinates;

    // instance constructor
    public DistanceCalculatorAdapter(Context context, int textViewResourceId,
                                     ArrayList<Coordinates> coordinates) {
        super(context, textViewResourceId, coordinates);

        //assign values
        this.context = context;
        this.coordinates = new ArrayList<>();
        this.coordinates.addAll(coordinates);
    }

    //create a new view holder class for th text view instnaces
    private class ViewHolder {

        TextView lat;
        TextView lon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null; // make null


        //if the convertView is null
        if (convertView == null) {
            // make and inflate the inflator
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.activity_list_template_distance_calculator,null);

            //wire the holder to the textviews in the list template
            holder = new ViewHolder();
            holder.lat = (TextView) convertView.findViewById(R.id.txt_dc_list_latitude);
            holder.lon = (TextView) convertView.findViewById(R.id.txt_dc_list_longitude);

            // set the tags for the holder
            convertView.setTag(holder);
            convertView.setTag(R.id.txt_dc_list_latitude, holder.lat);
            convertView.setTag(R.id.txt_dc_list_longitude, holder.lon);


        } else {
            // if convert view exists just assign holder to the constructed convert view
            holder = (ViewHolder) convertView.getTag();
        }


        Coordinates coordinate = coordinates.get(position);// create a coordinate to edit
        //get string arrays with the stored coordinates in string form
        String[] latStr = coordinate.getLatitudeString();
        String[] lonStr = coordinate.getLongitudeString();
        // display the numbers in the right format
        holder.lat.setText(latStr[0] + "° "
                + latStr[1] + "\' "
                + latStr[2] + "\" "
                + latStr[3]);
        holder.lon.setText(
                lonStr[0] + "° "
                + lonStr[1] + "\' "
                + lonStr[2] + "\" "
                + lonStr[3]);

        return convertView;
    }

    // sync the list in the listview with the list in the adapter
    public void updateList(ArrayList<Coordinates> updateList) {
        this.coordinates = updateList;
        notifyDataSetChanged();
    }

}