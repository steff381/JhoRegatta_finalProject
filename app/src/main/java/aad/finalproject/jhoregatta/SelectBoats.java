package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatAdapter;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.BoatListClass;
import aad.finalproject.db.DBAdapter;


public class SelectBoats extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: SelectBoats";

    // List stuff
    ListView myList; // initialize the listview
    BoatAdapter objAdapter; // initialize custom adapter

    // parameters for methods using sql quiery parameters
    private String whereClauseIsVisible = DBAdapter.KEY_BOAT_VISIBLE + " = 1";
    private String orderByClause = DBAdapter.KEY_BOAT_CLASS + ", "
            + DBAdapter.KEY_BOAT_NAME + " DESC";
    private String havingClause = null;
    CheckBox selectBoatCkBox;

    // data base stuff
    BoatDataSource boatDataSource; // call the boat datasource


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_boats);

        // edit the where statement in sql to only select the chosen boat classes
        appendWhereClause();

        //Build database
        boatDataSource = new BoatDataSource(this);
        boatDataSource.open(); // open a writable version of the db

        // set the lv to the current listview
        myList = (ListView) findViewById(R.id.lvSelectBoatList);
        // set the list to accept multiple selections
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectBoatCkBox = (CheckBox) findViewById(R.id.ckboxSelectBoatCheck);

        BoatListClass.clearSelectedBoatsList();
        addBoatsToBoatListClass(); // generate the boat list

        Log.i(LOG, "Setting obj Adapter");
        objAdapter = new BoatAdapter(this, boatDataSource
                .getAllBoats(whereClauseIsVisible, orderByClause, havingClause));


    }

    private void appendWhereClause() {
        StringBuilder sb = new StringBuilder();
        sb.append(whereClauseIsVisible);
        sb.append(" AND " + DBAdapter.KEY_BOAT_CLASS + " in(");
        for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
            sb.append("\"" + bc.getBoatColor() + "\"");
            sb.append(", ");
        }
        String substring = sb.substring(0, sb.length() - 2);
        substring += ")";
        whereClauseIsVisible = substring;
        Log.i(LOG, substring);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_boats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void onClickTimer(View view) {

        Log.i(LOG, "Boat list sie " + BoatListClass.boatList.size());
        for (int i = 0; i < BoatListClass.boatList.size(); i++) {
            Log.i(LOG, "Get checked item positions iteratior " + myList.getCheckedItemPositions());
        }
        BoatListClass.setSelectedBoats();
        BoatListClass.getSelectedBoats();
        Intent intent = new Intent(this, RegattaTimer.class);
        startActivity(intent);

        Log.i(LOG, " Navigate to Timer");
    }

    public void onClickBack(View view) {
        boatDataSource.close();
        Log.i(LOG, " Close SelectBoats");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatDataSource.open();
        populateListView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        boatDataSource.close();
    }

    private void addBoatsToBoatListClass() {

        try {
            Cursor boatCursor = boatDataSource.getAllBoatsCursor(whereClauseIsVisible, null, null);

            try {
                BoatListClass.clearSelectedBoatsList();
            } catch (Exception e) {
                Log.i(LOG, " exception when attempting to Clear Boat List");
            }
            int k =0;


            while (boatCursor.moveToNext()) {
                String id = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_ID));
                String bClass = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS));
                String name = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME));
                String sail = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM));
                String phrf = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF));
                String visible = boatCursor
                        .getString(boatCursor.getColumnIndex(DBAdapter.KEY_BOAT_VISIBLE));

                Boat boat = new Boat();

                boat.setId(Long.parseLong(id));
                boat.setBoatClass(bClass);
                boat.setBoatName(name);
                boat.setBoatSailNum(sail);
                boat.setBoatPHRF(Integer.parseInt(phrf));
                boat.setBoatVisible((Integer.parseInt(visible)));

                BoatListClass.boatList.add(boat);

            }
            boatCursor.close();

            Collections.sort(BoatListClass.boatList,
                    new Comparator<Boat>() {
                        @Override
                        public int compare(Boat lhs,
                                           Boat rhs) {
                            return (lhs.getId() + "").compareTo((
                                    rhs.getId() + ""));
                        }
                    });
            populateListView();
            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {

                    Boat boat = BoatListClass.boatList
                            .get(position);
                    if (boat.isSelected()) {
                        Log.i(LOG, "Deselecting " + boat.getBoatName());
                        boatDataSource.updateChecked(boat.getId(),!boat.isSelected());
                        boat.setSelected(false);
                        selectBoatCkBox.setChecked(false);
                    } else {
                        boatDataSource.updateChecked(boat.getId(), boat.isSelected());
                        Log.i(LOG, "Selecting " + boat.getBoatName());
                        boat.setSelected(true);
                        selectBoatCkBox.setChecked(true);
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // wire the data from the sql table to the list view
    public void populateListView() {
        Log.i(LOG, "Wiring ObjAdapter to list");
        myList.setAdapter(objAdapter);

    }
}
