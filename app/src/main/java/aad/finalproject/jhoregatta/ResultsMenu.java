package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import aad.finalproject.db.AndroidDatabaseManager;
import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.DatabaseWriter;
import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.ResultsAdapter;


public class ResultsMenu extends ActionBarActivity {
    private static final String LOGTAG = "Logtag: " + Thread.currentThread()
            .getStackTrace()[2].getClass().getSimpleName(); // log tag for records

    // sql elements for selecting boats
    private String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.activeRace.getId()
            + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
    private String orderBy = DBAdapter.KEY_BOAT_CLASS + ", "
            + DBAdapter.KEY_BOAT_NAME;

    private String validatorMessage;

    //instance of data source
    RaceDataSource raceDataSource;
    ResultDataSource resultDataSource;

    // Listview widgets and objects
    ListView myList;
    ResultsAdapter resultsAdapter;

    // make button instance for capturing finish time
    Button returnToTimeTracker;

    //make a button that handles all finalization efforts
    Button finishAndSendResults;

    //make a button that closes out the program.
    Button exitRace;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_menu);

        //wire data source and open
        raceDataSource = new RaceDataSource(this);
        resultDataSource = new ResultDataSource(this);
        raceDataSource.open();
        resultDataSource.open();

        //instantiate the custom results adapter
        GlobalContent.activeResultsAdapter = new ResultsAdapter(getApplicationContext(), resultDataSource.getAllResults(where, orderBy,
                null), resultDataSource);

        // wire widgets
        myList = (ListView) findViewById(R.id.lvResultList);

        //wire button
        returnToTimeTracker = (Button) findViewById(R.id.btn_nav_TimeTracker);
        finishAndSendResults = (Button) findViewById(R.id.btn_rm_csv_export);
        exitRace =(Button) findViewById(R.id.btn_rm_exit);

////////Exit race button functions
        // initially set button to invisible
        // only shows up after database has been finalized
        exitRace.setVisibility(View.GONE);

        //set methods for the exit button
        exitRace.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOGTAG, " exit button clicked");
                //todo Create exit button functions
                // if the race has been finailized then
                // clear the global array adapter
                // clear the global race ID  and Active Race ID

                // Always do the following....
                // close the results menu
                // return to the main menu
            }
        });


////////Return to time tracker button functions
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

////////Finializer button functions
        finishAndSendResults.setText("Finalize");
        // finalize the result table and
        // Export CSV and send via email
        finishAndSendResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateResultsTable()){
                    // build dialog box for confirmation
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultsMenu.this);
                    alertDialog.setTitle("WARNING: Finalizing Result Tables");
                    alertDialog.setMessage("Do you want to end the race?\n" +
                            "Finalizing the race will disable all editing and send a copy of " +
                            "the database via email.\n\nAre you sure you wish to proceed?");
                    alertDialog.setCancelable(false);

                    // User chooses confirm
                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //if yes
                            finalizeActiveRace(); // call finalizer
                            sendResultTableByEmail(); // send the finalized results by email
                            exitRace.setVisibility(View.VISIBLE); // make the exit button visible
                        }
                    });

                    //User chose no
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(LOGTAG, " Finalize results?: NO");
                        }
                    });
                    alertDialog.show();
                } else {
                    // show the error message to the user
                    Toast.makeText(ResultsMenu.this, validatorMessage, Toast.LENGTH_LONG).show();
                }
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

    //Check if the results table is complete,
    private boolean validateResultsTable() {
        // grab the data from the sql table
        List<Result> results = resultDataSource.getAllResults(where, null, null);
        validatorMessage = null; // set validator message to null
        String boatName;
        boolean returnValue = true;
        // first check to see if all the data that is required is available
        for (Result r : results) {

            boatName = r.getBoatName(); // boat name for message
            String errorMessage = "ERROR on: " + boatName + ": \n>>>>>   "; // base message

            // check to see if the result has been psudo deleted (made invisible)
            if (r.getResultsVisible() == 1) {
                Log.i(LOGTAG, boatName + "is visible ");
                // check if class start time is empty
                if (r.getResultsClassStartTime() == null) {
                    Log.i(LOGTAG, boatName + " Missing Class Start Time");
                    validatorMessage = errorMessage + " Class Start time is missing. Allow Time " +
                            "Tracker to finish.";
                    return false;
                }

                Log.i(LOGTAG, boatName + " Class Start is GOOD! ");

                // check to see if did not finish was selected for the boat
                if (r.getResultsNotFinished() == 0) { //boat is not DNF

                    Log.i(LOGTAG, boatName + " is NOT DNF");
                    //check for a finish time.
                    if (r.getResultsBoatFinishTime() == null) {
                        Log.i(LOGTAG, boatName + " Result Finished is NULL ");
                        validatorMessage = errorMessage + "Finish Time was not recorded, if " +
                                "the boat did not finish click \"DNF\" in the boat editor";
                        return false;
                    }

                    Log.i(LOGTAG, boatName + " Finish Time is GOOD! ");

                    ///THIS SHOULD NOT EVER HAPPEN
                    // if the elapsed time is empty then tell user
                    if (r.getResultsDuration() == null) {
                        Log.i(LOGTAG, boatName + " elapsed is NULL ");
                        validatorMessage = errorMessage + " The elapsed time has not been recorded. " +
                                "Something is wrong.";
                        return false;
                    }

                    Log.i(LOGTAG, boatName + " Elapsed Time is GOOD! ");

                    // if the adjusted duration is empty then tell user
                    if (r.getResultsAdjDuration() == null) {
                        validatorMessage = errorMessage + " The adjusted duration has not been " +
                                "recorded. Something is wrong.";
                        Log.i(LOGTAG, boatName + "Adjusted Duration is null");
                        return false;
                    }
                    Log.i(LOGTAG, boatName + " Adjusted Duration is GOOD! ");

                } else {

                    Log.i(LOGTAG, boatName + " DID NOT FINISH");
                }
            } else {

                Log.i(LOGTAG, boatName + " is NOT visible");
            }

        }
        return true;
    }
    //TODO develop finilzer for the results table
    //perform calculations needed to finialize the race database.
    private void finalizeActiveRace() {
        // for each boat in the result list.
        // check if the boat is visible.
        // If the boat DNF then set place to [total boats] + 1
        // get the adjusted duration.
        // Then calculate place for the class????
        // Set the listview to uneditable to prevent any changes being made from now on.

    }

    private void sendResultTableByEmail() {
        //create a file name for the csv file
        String fileName ="Regatta_num_" + GlobalContent.getRaceRowID() + ".csv";

        //write the database to a csv file.
        DatabaseWriter.exportDatabase(fileName, resultDataSource);


        //get the URI of the file just created
        Uri uri = Uri.fromFile(new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + fileName));

        Log.i(LOGTAG, " URI: " + uri.toString());

//                Create a new email and
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regatta Results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "These are the results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);//send file by email

        //dialog that asks the user to choose their mailing program preference
        startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
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
        myList.setAdapter(GlobalContent.activeResultsAdapter);
    }



}
