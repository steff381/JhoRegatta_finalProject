package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import aad.finalproject.db.SelectBoatDataSource;
import aad.finalproject.db.SelectBoatsAdapter;


public class SelectBoats extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: SelectBoats";

    // List stuff
    private ListView myList; // initialize the listview
    private SelectBoatsAdapter objAdapter;

    private Dimmer dimmer;

    public static final String SOURCE_BUNDLE_KEY = "SOURCE";

    private String source;

    CheckBox selectBoatCkBox; // create an accessible instance of the checkbox widget
    private Button btnRegTimer;

    // data base stuff
    private BoatDataSource boatDataSource; // call the boat datasource
    private ResultDataSource resultDataSource; // instance of results daasource
    private SelectBoatDataSource selectBoatDataSource; // instance of select boats daasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_boats);

        //create a dimmer class object that will schedule dimming after a period of time
        dimmer = new Dimmer(getWindow(), GlobalContent.dimmerDelay);
        dimmer.start();

        source = getIntent().getExtras().getString(SOURCE_BUNDLE_KEY);

        //Build database
        boatDataSource = new BoatDataSource(this);
        resultDataSource = new ResultDataSource(this);
        selectBoatDataSource = new SelectBoatDataSource(this);
        boatDataSource.open(); // open a writable version of the db
        resultDataSource.open(); // open a writable version of the db
        selectBoatDataSource.open(); // open a writable version of the db

        btnRegTimer = (Button) findViewById(R.id.btn_sb_toTimer);

        // set the lv to the current listview
        myList = (ListView) findViewById(R.id.lvSelectBoatList);
        // set the list to accept multiple selections
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectBoatCkBox = (CheckBox) findViewById(R.id.ckboxSelectBoatCheck);

        populateListView();
    }

    private void populateListView() {
        String where = null;
        ArrayList<Boat> selectedBoatsList = new ArrayList<>();

        switch (source) {
            case "RM":
                btnRegTimer.setVisibility(View.GONE); // hide the to timer button
                //get only unselected boats
                where = GlobalContent.globalWhere + " AND " + DBAdapter.KEY_BOAT_SELECTED + " = 0";

                Log.i(LOG, " RM > GlobalWhere = " + GlobalContent.globalWhere + " || Where = " + where);

                selectedBoatsList.clear(); // make sure list is empty
                break;
            case "SCD":
                //Clear the current race from the results table.
                resultDataSource.deleteRace(GlobalContent.activeRace.getId());
                Log.i(LOG, "Source is SCD. Removing any existing race from Results Table");

                where = GlobalContent.globalWhere; //set to standard where clause

                //Where statement for selectedBoatsList
                String selectedBoatsListWhere = GlobalContent.globalWhere + " AND "
                        + DBAdapter.KEY_BOAT_SELECTED + " = 1";

                //get a list of boats marked as "Selected = 1"
                selectedBoatsList = selectBoatDataSource.getAllSelectBoatsArrayList(
                        selectedBoatsListWhere, null, null);
                Log.i(LOG, " SCD > GlobalWhere = " + GlobalContent.globalWhere + " || Where = " + where);

                //clear the current select boats table
                selectBoatDataSource.rebuildTable();

                //populate select boats table from the boat table
                selectBoatDataSource.buildBoatList(boatDataSource.getAllBoatsArrayList(
                        GlobalContent.globalWhere, DBAdapter.KEY_BOAT_NAME, null));
                break;
            default:
                Log.i(LOG, "ERROR WITH WHERE CLAUSE");
                break;
        }

        //// make sure the selected boats from the previous list
        //// match the selections in the new list (when applicable)
        // edit the selected status to true for the boats in the new selected boat list that
        // were selected in the previous version of the list
        for (Boat b : selectedBoatsList) {
            selectBoatDataSource.setSelectedByBoatId(b.getBoatId(), true);
            resultDataSource.insertResult(b); // insert the selected boat into results table
            Log.i(LOG, "Sync SELECTED:  Boat " + b.getBoatName() + " Selected" );
        }

        // get all the boats that are in the selected boats table
        ArrayList<Boat> allTheBoats = selectBoatDataSource.getAllSelectBoatsArrayList(
                where, null, null);

        /// wire the custom view adapter to the view
        Log.i(LOG, "Setting obj Adapter");

        //set the listview adapter
        objAdapter = new SelectBoatsAdapter(this, R.layout.activity_list_template_select_boats,
                allTheBoats, where, resultDataSource, selectBoatDataSource,
                GlobalContent.getRaceRowID());
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

        switch (item.getItemId()) {
            case R.id.action_merge_classes:
                Intent intent = new Intent(this, MergeBoatClasses.class);
                startActivity(intent);
                return true;
            case R.id.action_ddms:
                GlobalContent.DDMS(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //navigate user to the timer.
    public void onClickTimer(View view) {

        BoatListClass.selectedBoatsList.clear(); // clear data from the boat list.

        // check if user selected at least 1 boat
        if (isValid()) {
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
        } else {
            Toast.makeText(this, "You must select at least 1 boat"
                    , Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        List<Result> validResults = resultDataSource.getAllResults(
                DBAdapter.KEY_RACE_ID + "= " + GlobalContent.getRaceRowID(),
                 null, null);
        //check the soruce,
        if (source.equals("RM")) {
            return true; //  if it was from results menu then no validation required
        } else if (validResults.size() > 0) { //check if user selected at least one boat
            return true;
        }
        return false;
    }

    public void onClickBack(View view) {
        Log.i(LOG, " Close SelectBoats");
        finish(); // close out the current view
    }

    @Override
    public void onUserInteraction() {
        dimmer.resetDelay(); // reschedule dimmer task
    }

    @Override
    protected void onResume() {
        super.onResume();
        dimmer.start();// start or resume the dimmer
        boatDataSource.open(); // open the datasource
        selectBoatDataSource.open(); // open the datasource
        resultDataSource.open(); // open the datasource
        populateListView();// refresh the listview
        myList.setAdapter(objAdapter); // refresh the listview
        // sync up array list with db
        objAdapter.syncArrayListWithSql();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dimmer.end();// end dimmer activity
        boatDataSource.close(); // close datasource to prevent leakage
        selectBoatDataSource.close(); // open the datasource
        resultDataSource.close(); // close datasource to prevent leakage
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
