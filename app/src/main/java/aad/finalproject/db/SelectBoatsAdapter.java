package aad.finalproject.db;

import android.content.Context;
import android.graphics.Color;
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

public class SelectBoatsAdapter extends ArrayAdapter<Boat> {
    private static String LOGTAG = "Logtag: SelectBoatAdapter";
    private ResultDataSource resultDataSource;
    private SelectBoatDataSource selectBoatDataSource;
    private Context context; // make an accessible field for context
    private String where;
    private long currentRaceId;

    public ArrayList<Boat> boatArrayList;// list of all boats in the selected classes
    // instance constructor
    public SelectBoatsAdapter(Context context, int textViewResourceId,
                             ArrayList<Boat> boatArrayList, String where, ResultDataSource rds,
                             SelectBoatDataSource sbd,  long currentRaceId) {
        super(context, textViewResourceId, boatArrayList);
        this.context = context; // pass context to the class field
        // make a new array list and place all boats inside.
        this.boatArrayList = new ArrayList<>();
        this.boatArrayList.addAll(boatArrayList);
        this.where = where;
        this.selectBoatDataSource = sbd;
        this.resultDataSource = rds;
        this.currentRaceId = currentRaceId;
    }

    @Override
    public int getCount() {
        return boatArrayList.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        //if the view is null
        if (view == null) {
            // make and inflate the inflator
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(R.layout.activity_list_template_select_boats, parent, false);
        }
        //create finalized boat
        final Boat boat = boatArrayList.get(position);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.ckboxSelectBoatCheck);
        boolean isSelected = boat.getIsSelected() == 1; // check if boat is selected
        checkBox.setChecked(isSelected);
        final TextView id = (TextView) view.findViewById(R.id.txt_hd_sb_ID);
        id.setText(String.valueOf(boat.getBoatId()));
        final TextView boatPHRF = (TextView) view.findViewById(R.id.txt_hd_sb_PHRF);
        boatPHRF.setText(boat.getBoatPHRF() + "");
        final TextView boatVisible = (TextView) view.findViewById(R.id.txt_hd_sb_Visible);
        boatVisible.setText(boat.getBoatVisible() + "");
        final TextView boatClass = (TextView) view.findViewById(R.id.txt_hd_sb_Class);
        boatClass.setText(boat.getBoatClass());
        final TextView boatName = (TextView) view.findViewById(R.id.txt_hd_sb_Name);
        boatName.setText(boat.getBoatName());
        final TextView boatSailNum = (TextView) view.findViewById(R.id.txt_hd_sb_SailNum);
        boatSailNum.setText(boat.getBoatSailNum());
        
        checkBox.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = checkBox.isChecked();
                //check for checkbox status
                if (isChecked) {
                    boat.setIsSelected(1);
                    selectBoatDataSource.setSelected(boat.getId(), true); // if checked set to checked
                    resultDataSource.insertResult(boat); // add boat to results table
                    Log.i(LOGTAG, boat.getBoatName() + " was selected and selection is now " + boat.getIsSelected() + " boat id ");
                } else {
                    boat.setIsSelected(0);
                    selectBoatDataSource.setSelected(boat.getId(), false); //if unchecked, set to unchecked
                    // remove from results table
                    resultDataSource.deleteResult(currentRaceId, boat.getBoatId());
                    Log.i(LOGTAG, boat.getBoatName() + " was DESELECTED and selection is now " + boat.getIsSelected());
                }
                notifyDataSetChanged();
            }
        });

        //if the checkbox is checked change the view to blue
        if (boat.getIsSelected() == 1) {
            ///view becomes blue
            view.setBackgroundColor(view.getResources().getColor(R.color.blue05));
            //text becomes white.
            boatClass.setTextColor(Color.parseColor("#ffffff"));
            boatName.setTextColor(Color.parseColor("#ffffff"));
            boatSailNum.setTextColor(Color.parseColor("#ffffff"));
        } else {
            //if it isn't checked change it to regular.
            view.setBackgroundColor((Color.parseColor("#00000000")));
            //text becomes black.
            boatClass.setTextColor(Color.parseColor("#000000"));
            boatName.setTextColor(Color.parseColor("#000000"));
            boatSailNum.setTextColor(Color.parseColor("#000000"));
        }
        return view;
    }

    // sync the list in the ResultsAdapter with what is in the Results SQL table.
    public void syncArrayListWithSql() {
        //create statement strings
        String orderBy = DBAdapter.KEY_BOAT_NAME;
        // create a temporary placeholder for the data from SQL
        List<Boat> tempResultFromSql;
        tempResultFromSql = selectBoatDataSource.getAllSelectBoats(where, orderBy, null);
        // Make sure the data coming from sql isn't blank. Otherwise throw error
        if (tempResultFromSql.size() > 0) {
            //clear the list
            this.boatArrayList.clear();
            //repopulate the list with data from SQL
            this.boatArrayList.addAll(tempResultFromSql);
        }
        notifyDataSetChanged(); //force a repaint of the listview
    }
}