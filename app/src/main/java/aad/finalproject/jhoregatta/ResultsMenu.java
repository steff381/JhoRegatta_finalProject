package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.DatabaseWriter;
import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.ResultsAdapter;


public class ResultsMenu extends MainActivity implements ProofOfIntentDialog.ProofOfIntentCommunicator{
    private static final String LOGTAG = "Logtag: ResultsMenu";

    // sql elements for selecting boats
    private String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
            + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
    private String orderBy = DBAdapter.KEY_BOAT_NAME;

    //Message telling user what is wrong with the form.
    private String validatorMessage;

    // Button Text Constants
    private static final String FINAL_BUTTON_TEXT = "E-MAIL RESULTS";
    private static final String FINAL_BUTTON_TEXT_ACTIVE = "E-MAIL RESULTS";
    private static final String BACK_BUTTON_TEXT = "Race Menu";
    private static final String BACK_BUTTON_TEXT_ACTIVE = "Time Tracker";


    //wake lock variable
    PowerManager.WakeLock wl;

    //instance of data source
    private RaceDataSource raceDataSource;
    private ResultDataSource resultDataSource;

    private List<Result> results;

    //Listview widgets and objects
    public static ListView myList;

    //make a package accessible race button that can be modified by other activities.
    protected static Button exitRace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_menu);

        //Keep awake
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        //wire data source and open
        raceDataSource = new RaceDataSource(this);
        resultDataSource = new ResultDataSource(this);
        raceDataSource.open();
        resultDataSource.open();

        results = getAllSQLResultResults(resultDataSource); // get list of all results

        //instantiate the custom results adapter
        GlobalContent.activeResultsAdapter = new ResultsAdapter(getApplicationContext(),
                results, resultDataSource);

        // wire widgets
        myList = (ListView) findViewById(R.id.lvResultList);

        //wire button
        Button returnToTimeTracker = (Button) findViewById(R.id.btn_nav_TimeTracker);
        final Button finishAndSendResults = (Button) findViewById(R.id.btn_rm_csv_export);
        exitRace = (Button) findViewById(R.id.btn_rm_exit);


            ////////Exit race button functions

        //set methods for the exit button
        exitRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOGTAG, " exit button clicked");

                if (!validateResultsTable()) {
                    // build dialog box for confirmation
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Exit Confirmation");
                    alertDialog.setMessage("WARNING: You have not completed the race or vital " +
                            "information is missing.\n\nWould you like to EXIT anyway?");
                    alertDialog.setCancelable(false);

                    // User chooses confirm
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //if yes
                            // create fragment manager and dialog fragment
                            FragmentManager fm = getFragmentManager();
                            ProofOfIntentDialog poid = new ProofOfIntentDialog();

                            poid.show(fm, "Confirm Intent to Exit");
                        }
                    });

                    //User choose no
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.show();// show the error message to the user
                } else {




//                if (validateResultsTable()) {
                    exitResultsMenu();
//                } else {
//                    // show the error message to the user
//                    Toast.makeText(ResultsMenu.this, validatorMessage, Toast.LENGTH_LONG).show();
                }



            }
        });


        // close finish line and navigate to back to time tracker
        returnToTimeTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close databases to conserve resources
                resultDataSource.close();
                raceDataSource.close();
                finish(); //finish the activity

            }
        });

            ////////Finializer button functions
        finishAndSendResults.setText(FINAL_BUTTON_TEXT_ACTIVE);
        // finalize the result table and
        // Export CSV and send via email
        finishAndSendResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateResultsTable()) {

                    //if no distance was set by the user show the warning dialog
                    if (calculateDistance() == 0) {
                        // build dialog box for confirmation
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                        alertDialog.setTitle("Missing Distance");
                        alertDialog.setMessage("This race does not have any distances.\n\n" +
                                "Would you like to send the results anyway?");
                        alertDialog.setCancelable(false);

                        // User chooses confirm
                        alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { //if yes
                                sendResultTableByEmail(); // send the finalized results by email
                            }
                        });

                        //User choose no
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        alertDialog.show();// show the error message to the user
                    } else {
                        sendResultTableByEmail(); // send the finalized results by email
                    }
                } else {
                    Toast.makeText(ResultsMenu.this, validatorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        //set the visability of buttons depending on access mode
        if (GlobalContent.getResultsFormAccessMode().equals(GlobalContent.modeEdit)) {
            //change the name of the back to tracker button
            returnToTimeTracker.setText(BACK_BUTTON_TEXT);
            //change the name of the finalizer button
            finishAndSendResults.setText(FINAL_BUTTON_TEXT);


        } else {//change the name of the back to tracker button
            //set the text for the tracker button
            returnToTimeTracker.setText(BACK_BUTTON_TEXT_ACTIVE);

        }


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

                TextView idTv = (TextView) view.findViewById(R.id.txt_hd_results_ID);
                long myId = Long.parseLong(idTv.getText().toString());//get the id value from the view

                GlobalContent.setResultsRowID(myId); // grab the result id so the editor can use it
                //open up the edit formm activity
                Intent intent = new Intent(view.getContext(), ResultsEditor.class);
                startActivity(intent);
                return true;
            }
        });


    }

    private double calculateDistance() {
        //tally up the distance
        double distance = 0;
        for (Result r : results) {
            Log.i(LOGTAG, " class: " + r.getBoatClass() + " boat: " + r.getBoatName() + " distance: " + r.getRaceDistance());
            distance += r.getRaceDistance();
        }
        return distance;
    }

    private void exitResultsMenu() {
        try {
            wl.release();
        } catch (Exception e) {
            e.printStackTrace();
            //do nothing else. it must already be closed.
        }
        GlobalContent.finalDataClear(); //clear all the data
        //goto main menu
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //Check if the results table is complete,
    private boolean validateResultsTable() {
        // grab the data from the sql table
        List<Result> results = getAllSQLResultResults(resultDataSource);
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


    private void sendResultTableByEmail() {
        //create a file name for the csv file
        String fileName = "Regatta_num_" + GlobalContent.getRaceRowID() + ".csv";

        //write the database to a csv file.
        DatabaseWriter.exportDatabase(fileName, resultDataSource);


        //get the URI of the file just created
        Uri uri = Uri.fromFile(new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + fileName));

        Log.i(LOGTAG, " URI: " + uri.toString());

        //Create a new email and
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regatta Results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "These are the results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);//send file by email

        //dialog that asks the user to choose their mailing program preference
        startActivity(Intent.createChooser(emailIntent, "Choose your Email App:"));
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
//            case R.id.action_ddms:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOGTAG, " onResume Now");
        raceDataSource.open(); // reopen the db
        resultDataSource.open(); // reopen the db
        wl.acquire(); //acquire the wake lock
        //risky method
        try {
            //sync the list in the adapter with data from SQL
            GlobalContent.activeResultsAdapter
                    .syncArrayListWithSql(getAllSQLResultResults(resultDataSource));
        } catch (Exception e) {
            Log.i(LOGTAG, "Caught error");
            e.printStackTrace();
        }
        myList.invalidate(); // force a refresh of the list view
        populateListView(); // refresh listview


    }

    public static List<Result> getAllSQLResultResults(ResultDataSource resultDataSource) {

        String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
                + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        String orderBy = DBAdapter.KEY_BOAT_NAME;

        return resultDataSource.getAllResults(where, orderBy, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOGTAG, " onPause NOW");
        wl.release(); //release wake lock
        raceDataSource.close(); // close db to reduce data leak
        resultDataSource.close(); // close db to reduce data leak
    }


    // populate the list using the adapter
    public void populateListView() {
        Log.i(LOGTAG, " Setting custom resultsAdapter to the listview");
        myList.setAdapter(GlobalContent.activeResultsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        //if time tracker finished then enable the exit button else set to disabled
//        if (RegattaTimer.TIMER_FINISHED) {
//            exitRace.setEnabled(true);
//        } else {
//            exitRace.setEnabled(false);
//        }
        // set the activity alive status to true as the thing is on
        GlobalContent.RESULT_MENU_ALIVE = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        //set the activity alive status to false
        GlobalContent.RESULT_MENU_ALIVE = false;
    }

    @Override
    public void onFragmentInteraction(String message) {
        if (message.equals("exit")) {
            exitResultsMenu();
        }
    }
}
