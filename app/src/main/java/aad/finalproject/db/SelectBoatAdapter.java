package aad.finalproject.db;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.jhoregatta.R;

/**
 * Created by Daniel on 4/24/2015.
 */
public class SelectBoatAdapter extends ArrayAdapter<Boat> {
    private static String LOGTAG = "Logtag: " + Thread.currentThread().getStackTrace()[2]
            .getClass().getSimpleName();

    private Context context; // make an accessible field for context
    private String where;

    public ArrayList<Boat> boatArrayList;// list of all boats in the selected classes
    // instance constructor
    public SelectBoatAdapter(Context context, int textViewResourceId,
                             ArrayList<Boat> boatArrayList, String where) {
        super(context, textViewResourceId, boatArrayList);
        this.context = context; // pass context to the class field
        // make a new array list and place all boats inside.
        this.boatArrayList = new ArrayList<>();
        this.boatArrayList.addAll(boatArrayList);
        this.where = where;
    }
    // create a holder class for the variables
    private class ViewHolder{
        CheckBox checkBox;
        TextView id;
        TextView boatPHRF;
        TextView boatVisible;
        TextView boatClass;
        TextView boatName;
        TextView boatSailNum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder; // make null
        Log.i(LOGTAG, "convertView" + String.valueOf(position));


        //if the convertView is null
        if (convertView == null) {
            // make and inflate the inflator
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.activity_list_template_select_boats, null);

            //pass values into the holder instance
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.ckboxSelectBoatCheck);
            holder.id = (TextView) convertView.findViewById(R.id.txt_hd_sb_ID);
            holder.boatPHRF = (TextView) convertView.findViewById(R.id.txt_hd_sb_PHRF);
            holder.boatVisible = (TextView) convertView.findViewById(R.id.txt_hd_sb_Visible);
            holder.boatClass = (TextView) convertView.findViewById(R.id.txt_hd_sb_Class);
            holder.boatName = (TextView) convertView.findViewById(R.id.txt_hd_sb_Name);
            holder.boatSailNum = (TextView) convertView.findViewById(R.id.txt_hd_sb_SailNum);

            //pass values to the tag of the covert view
            convertView.setTag(holder);


            //create an onclick listener for each checkbox
            holder.checkBox.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CheckBox ocCheckBox = (CheckBox) view;
                    Boat ocBoat = (Boat) ocCheckBox.getTag();
                    ocBoat.setSelected(ocCheckBox.isChecked());
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // creat a boat and add the data to the holder using the boat
        Boat boat = boatArrayList.get(position);
        holder.id.setText(String.valueOf(boat.getId()));
        holder.boatPHRF.setText(boat.getBoatPHRF() + "");
        holder.boatVisible.setText(boat.getBoatVisible() + "");
        holder.boatClass.setText(boat.getBoatClass());
        holder.boatName.setText(boat.getBoatName());
        holder.boatSailNum.setText(boat.getBoatSailNum());
        holder.checkBox.setChecked(boat.isSelected());
        holder.checkBox.setTag(boat);

        return convertView;

    }

    // sync the list in the ResultsAdapter with what is in the Results SQL table.
    public void syncArrayListWithSql(BoatDataSource boatDataSource) {
//    public static void syncArrayListWithSql(ResultDataSource resultDataSource) {
        //create statement strings
        String orderBy = DBAdapter.KEY_BOAT_CLASS + ", "
                + DBAdapter.KEY_BOAT_NAME;
        // create a temporary placeholder for the data from SQL
        List<Boat> tempResultFromSql;
        tempResultFromSql = boatDataSource.getAllBoats(where, orderBy, null);
        //TODO For testing
        for (Boat b : tempResultFromSql) {
            Log.i(LOGTAG, "boat " + b.getBoatName());
        }
        // Make sure the data coming from sql isn't blank. Otherwise throw error
        if (tempResultFromSql.size() > 0) {

            this.boatArrayList.clear();

            this.boatArrayList.addAll(tempResultFromSql);

        }
        notifyDataSetChanged();
    }

}
