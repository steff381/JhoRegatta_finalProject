package aad.finalproject.jhoregatta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.BoatListClass;
import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.SelectBoatAdapter;


public class SelectBoats extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: SelectBoats";

    // List stuff
    private ListView myList; // initialize the listview
    private SelectBoatAdapter objAdapter;

    private PowerManager.WakeLock wl; // wake lock instances

    public static final String SOURCE_BUNDLE_KEY = "SOURCE";

    private String source;

    CheckBox selectBoatCkBox; // create an accessible instance of the checkbox widget

    // data base stuff
    private BoatDataSource boatDataSource; // call the boat datasource
    private ResultDataSource resultDataSource; // instance of results daasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_boats);

        //get power manager instances
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire(); // keep the activity awake
        Log.i(LOG, "WAKELOCK: Acquired Initial ");

        source = getIntent().getExtras().getString(SOURCE_BUNDLE_KEY);

        //Build database
        boatDataSource = new BoatDataSource(this);
        resultDataSource = new ResultDataSource(this);
        boatDataSource.open(); // open a writable version of the db
        resultDataSource.open(); // open a writable version of the db

        String where = null;
        if (source.equals("RM")) {
            where = showOnlyUnselectedBoats(GlobalContent.resultList); //get only unselected boats
            changeInterface();
        } else if (source.equals("SCD")) {
            where = GlobalContent.globalWhere; //set to standard where clause
        } else {
            Log.i(LOG, "ERROR WITH WHERE CLAUSE");
        }

        //get a list of all the boats in the selected classes
        String orderByClause = DBAdapter.KEY_BOAT_NAME;
        ArrayList<Boat> allTheBoats = boatDataSource
                .getAllBoatsArrayList(where, orderByClause, null);

        // set the lv to the current listview
        myList = (ListView) findViewById(R.id.lvSelectBoatList);
        // set the list to accept multiple selections
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectBoatCkBox = (CheckBox) findViewById(R.id.ckboxSelectBoatCheck);


        /// wire the custom view adapter to the view
        Log.i(LOG, "Setting obj Adapter");

        //set the listview adapter
        objAdapter = new SelectBoatAdapter(this, R.layout.activity_list_template_select_boats,
                allTheBoats, where);

    }

    private void changeInterface() {
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

        // check if user selected at least 1 boat
        if (BoatListClass.selectedBoatsList.size() > 0) {
            // enter selected boats into the results table
            resultDataSource.create();
            //if accessed by the select class distance activity
            if (source.equals("SCD")) {
                // open the timer
                Intent intent = new Intent(this, RegattaTimer.class);
                startActivity(intent);
                Log.i(LOG, " Navigate to Timer");
            //if accessed by the results menu
            } else if(source.equals("RM")) {
                // finish activity thus returning to results menu
                Log.i(LOG, " Return to Results Menu");
                // grab the start times for each class and apply it to the datasource.
                for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                    resultDataSource.updateClassStartTime(GlobalContent.getRaceRowID(),
                            bc.getBoatColor(), bc.getStartTime(), bc.getClassDistance());
                }
                finish(); // close activity

            }
            Toast.makeText(this, "Adding " + BoatListClass.selectedBoatsList.size() + " boats " +
                    "to the results table", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You must select at least 1 boat"
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
        wl.acquire(); // resume wakelock status
        Log.i(LOG, "WAKELOCK: Acquired");
        boatDataSource.open(); // open the datasource
        populateListView(); // refresh the listview
        // sync up array list with db
        objAdapter.syncArrayListWithSql(boatDataSource);

    }

    @Override
    protected void onPause() {
        super.onPause();
        wl.release(); // suspend wakelock mode for this screen
        Log.i(LOG, "WAKELOCK: Released");
        boatDataSource.close(); // close datasource to prevent leakage
    }


    // wire the data from the sql table to the list view
    public void populateListView() {
        Log.i(LOG, "Wiring ObjAdapter to list");
        myList.setAdapter(objAdapter);

    }

    // create a where clause without boats that have been selected
    private String showOnlyUnselectedBoats(List<Result> resultsList) {
        StringBuilder sb = new StringBuilder();
        sb.append(GlobalContent.globalWhere); // grab current where clause
        sb.append(" AND " + DBAdapter.KEY_BOAT_NAME + " NOT IN("); // add new exclusion language
        boolean firstResult = true; // set the first statement bool to true
        for (Result r : resultsList) {
            if (firstResult) {
                sb.append("\"" + r.getBoatName() + "\""); // add first record to the list
                firstResult = false; // no longer first statement so false
            } else {
                sb.append(",\"" + r.getBoatName() + "\""); // add subsequent records to the list
            }
        }
        sb.append(")"); // end list with paren.
        String newWhere = sb.toString(); // send whare to string
        Log.i(LOG, "New where is " + newWhere);

        return newWhere;
    }
}
