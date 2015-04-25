package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import aad.finalproject.db.AndroidDatabaseManager;
import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.ResultsAdapter;


public class ResultsMenu extends ActionBarActivity {
    private static final String LOGTAG = "Logtag: " + Thread.currentThread()
            .getStackTrace()[2].getClass().getSimpleName(); // log tag for records

    // sql elements for selecting boats
    private String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.activeRace.getId()
            + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
    private String orderBy = DBAdapter.KEY_BOAT_CLASS + ", "
            + DBAdapter.KEY_BOAT_NAME + " DESC";

    //instance of data source
    RaceDataSource raceDataSource;
    ResultDataSource resultDataSource;

    // Listview widgets and objects
    ListView myList;
    ResultsAdapter resultsAdapter;

    // make button instance for capturing finish time
    Button returnToTimeTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_menu);

        //wire data source and open
        raceDataSource = new RaceDataSource(this);
        resultDataSource = new ResultDataSource(this);
        raceDataSource.open();
        resultDataSource.open();

        //instantiate the custom results adapter
        resultsAdapter = new ResultsAdapter(this, resultDataSource.getAllResults(where, orderBy,
                null), resultDataSource);

        // wire widgets
        myList = (ListView) findViewById(R.id.lvResultList);

        //wire button
        returnToTimeTracker = (Button) findViewById(R.id.btn_nav_TimeTracker);

        // close finish line and navigate to back to time tracker
        returnToTimeTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close databases to conserve resources
                resultDataSource.close();
                raceDataSource.close();
                finish();

            }
        });



        //set onclick listening for listview
        //make it long click to prevent accidental clicking
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * Callback method to be invoked when an item in this view has been
             * clicked and held.
             * Implementers can call getItemAtPosition(position) if they need to access
             * the data associated with the selected item.
             *
             * @param parent   The AbsListView where the click happened
             * @param view     The view within the AbsListView that was clicked
             * @param position The position of the view in the list
             * @param id       The row id of the item that was clicked
             * @return true if the callback consumed the long click, false otherwise
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalContent.setResultsRowID(id);
                Intent intent = new Intent(view.getContext(), ResultsEditForm.class);
                startActivity(intent);
                return true;
            }
        });
    }


    // prevent the user from using the devices back button
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_ddms:
                onActionClickDDMS(); //TODO: Get rid of me
                return true;
//            case R.id.action_settings:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onActionClickDDMS(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOGTAG, " onResume Now");
        raceDataSource.open(); // reopen the db
        resultDataSource.open(); // reopen the db
        populateListView(); // refresh listview

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOGTAG, " onPause NOW");
        raceDataSource.close(); // close db to reduce data leak
        resultDataSource.close(); // close db to reduce data leak
    }


    // populate the list using the adapter
    public void populateListView() {
        Log.i(LOGTAG, " Setting custom resultsAdapter to the listview");
        myList.setAdapter(resultsAdapter);
    }



}
