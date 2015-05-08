package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.BoatListClass;
import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.SelectBoatAdapter;


public class SelectBoats extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: SelectBoats";

    // List stuff
    private ListView myList; // initialize the listview
    private SelectBoatAdapter objAdapter;


    CheckBox selectBoatCkBox; // create an accessible instance of the checkbox widget

    // data base stuff
    private BoatDataSource boatDataSource; // call the boat datasource
    private ResultDataSource resultDataSource; // instance of results daasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_boats);

        //Build database
        boatDataSource = new BoatDataSource(this);
        resultDataSource = new ResultDataSource(this);
        boatDataSource.open(); // open a writable version of the db
        resultDataSource.open(); // open a writable version of the db

        // edit the where statement in sql to only select the chosen boat classes
        appendWhereClause();

        //get a list of all the boats in the selected classes
        String orderByClause = DBAdapter.KEY_BOAT_CLASS + ", "
                + DBAdapter.KEY_BOAT_NAME;
        ArrayList<Boat> allTheBoats = boatDataSource
                .getAllBoatsArrayList(GlobalContent.globalWhere, orderByClause, null);

        // set the lv to the current listview
        myList = (ListView) findViewById(R.id.lvSelectBoatList);
        // set the list to accept multiple selections
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectBoatCkBox = (CheckBox) findViewById(R.id.ckboxSelectBoatCheck);


        /// wire the custom view adapter to the view
        Log.i(LOG, "Setting obj Adapter");

        //set the listview adapter
        objAdapter = new SelectBoatAdapter(this, R.layout.activity_list_template_select_boats,
                allTheBoats, GlobalContent.globalWhere);




    }

    // build a sql query that includes only the classes chosen by the user in the prvious form.
    private void appendWhereClause() {
        //check if the boat class in the array is the "Classless" class
        for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
            Log.i(LOG, b.getBoatColor());
        }
        if (!BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(0).getBoatColor().equals("Classless")) {
            StringBuilder sb = new StringBuilder();
            sb.append(DBAdapter.KEY_BOAT_VISIBLE + " = 1"); // grab the original statement and append
            // it to the new one
            sb.append(" AND " + DBAdapter.KEY_BOAT_CLASS + " in(");
            for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                sb.append("\"" + bc.getBoatColor() + "\"");
                sb.append(", ");
            }
            String substring = sb.substring(0, sb.length() - 2); // chop off the last comma.
            substring += ")"; // include the last brace
            GlobalContent.globalWhere = substring; //assign the where clause to the global where
            Log.i(LOG, substring);
        } else {
            //if the only boat class in the list is the classless class choose all boats.
            GlobalContent.globalWhere = DBAdapter.KEY_BOAT_VISIBLE + " = 1";
        }
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

    //navigate user to the timer.
    public void onClickTimer(View view) {

        BoatListClass.selectedBoatsList.clear(); // clear data from the boat list.

        //If the user selected the boat add it to the list.
        for (Boat b : objAdapter.boatArrayList) {
            if (b.isSelected()) {
                BoatListClass.selectedBoatsList.add(b);
            }

        }

        int listSize = BoatListClass.selectedBoatsList.size();
        // check if user selected at least 1 boat
        if (listSize > 0) {
            // enter selected boats into the results table
            resultDataSource.create();
            // open the timer
            Intent intent = new Intent(this, RegattaTimer.class);
            startActivity(intent);
            Log.i(LOG, " Navigate to Timer");
            Toast.makeText(this, "Adding " + listSize + " boats to the results table"
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You must select at least 1 boat from each class"
                    , Toast.LENGTH_LONG).show();
        }

    }

    public void onClickBack(View view) {
        boatDataSource.close(); // close datasource to prevent leakage
        Log.i(LOG, " Close SelectBoats");
        finish(); // close out the current view
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatDataSource.open(); // open the datasource
        appendWhereClause(); // refresh the where clause
        populateListView(); // refresh the listview
        objAdapter.syncArrayListWithSql(boatDataSource);

    }

    @Override
    protected void onPause() {
        super.onPause();
        boatDataSource.close(); // close datasource to prevent leakage
    }


    // wire the data from the sql table to the list view
    public void populateListView() {
        Log.i(LOG, "Wiring ObjAdapter to list");
        myList.setAdapter(objAdapter);

    }
}
