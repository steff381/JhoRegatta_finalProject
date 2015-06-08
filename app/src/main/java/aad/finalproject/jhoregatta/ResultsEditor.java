package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.SelectBoatDataSource;


public class ResultsEditor extends MainActivity  implements TimePickerDialog.Communicator {

    private static final String LOGTAG = "ResultsEditor"; // logtag component
    //widget instances
    private Button update, cancel, delete, setElapsedTime;
    private CheckBox didNotFinish, manualEntryMode, editBoatDetails;
    private Spinner boatNameSpn, boatClassSpn;
    private EditText penalty, notes, sailNum, phrf;
    private TextView distance, elapsedTime, adjDuration,
            finishTime, classStart, notesCharRemaining;
    private Dimmer dimmer;

    public static final String PLACEHOLDER = "[Select Boat]"; // text for placeholder

    // create a result that captures the current result's information
    private Result originalResult;
    private Result preUpdateResult;

    // list of all the boat results that have no recorded finish time.
    private List<Result> currentlyRacingResults;
    private ArrayList<String> currentlyRacingClasses;

    // array adapters

    private ArrayAdapter<String> boatNameArrayAdapter;
    private ArrayAdapter<String> boatClassArrayAdapter;
    // get the current race id
    private long resultId;

    // make a variable that tells if the boat details have been changed
    private boolean boatHasChanged = false;

    /////////DATABASE SECTION////////////

    //cursor that holds all the data from the sql table.
    private Cursor updateFieldsFromCursor;

    // main linear layout
    private LinearLayout myLayout;

    //datasource instance
    private ResultDataSource resultDataSource;
    private SelectBoatDataSource selectBoatDataSource;


    private int totalChars = 256; //set max char width of notes EditText box


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_editor);

        //Keep awake during activity
        dimmer = new Dimmer(getWindow(), GlobalContent.dimmerDelay);
        dimmer.start();

        Log.i(LOGTAG, " Result row is " + GlobalContent.getResultsRowID());
        //assign editable datasource
        resultDataSource = new ResultDataSource(this);
        selectBoatDataSource = new SelectBoatDataSource(this);
        selectBoatDataSource.open();
        resultDataSource.open();

        //get the current race id
        resultId = GlobalContent.getResultsRowID();



        originalResult = resultDataSource.getResultById(resultId); // store current result data


        preUpdateResult = originalResult; // start the initial pre update result to the original.

        //initialize the list of racers still racing
        currentlyRacingResults = new ArrayList<>();

        // unfinished results "Where" clause
        String whereResults = DBAdapter.KEY_RACE_ID + "=" + GlobalContent.getRaceRowID() +
                " AND " + DBAdapter.KEY_RESULTS_FINISH_TIME + " is null AND " +
                DBAdapter.KEY_RESULTS_VISIBLE + " = 1 AND " + DBAdapter.KEY_RESULTS_CLASS_START +
                " is NOT NULL";

        //order by name
        String orderByResults = DBAdapter.KEY_BOAT_NAME;

        //add the current result to the list first so it stays at position 0
        currentlyRacingResults.add(originalResult);

        // get list of all racers still racing from results table
        currentlyRacingResults.addAll(resultDataSource.getAllResults(whereResults, orderByResults, null));

        //get the result from the sql table.
        updateFieldsFromCursor = resultDataSource.getRow(resultId);


        //assing linear layout
        myLayout = (LinearLayout) findViewById(R.id.linlay_rf_mainLayout);

        wireWidgets(); //assign layout elements to variables


       //assign Listeners
        wireListeners();

        getValuesFromCursor(); //grab the values from SQL
        ///////////////initial widget states//////////////////////////////////////////
        setupSpinners();

        // if the boat is a place holder then allow editing by default
        if (boatNameSpn.getSelectedItem().toString().equals(PLACEHOLDER)) {
            editBoatDetails.setChecked(true);
            // enable drop down
            boatNameSpn.setClickable(true);
        }

        //set elapsed time button to disabled until manual entry checkbox is clicked
        if (!manualEntryMode.isChecked()) {
            setElapsedTime.setEnabled(false);
            Log.i(LOGTAG, "CHECKBOX -------!!!--------- DISABLED @@");
        }

        notesCharRemaining.setText(String.valueOf(totalChars)); // initial count of characters

//        // check if the original result has a manually assigned duration
//        if (originalResult.getResultsManualEntry() == 1) {
//            // set the elapsed time and adjusted time to that in the original entry
//            manualEntryMode.setChecked(true);
//            elapsedTime.setText(originalResult.getResultsClassStartTime());
//            adjDuration.setText(originalResult.getResultsAdjDuration());
//        }
    }

    private void syncViewsToPreUpdate() {

        //KEY_ID,
        preUpdateResult.setResultsId(resultId);
        //KEY_RACE_ID,
//        preUpdateResult.setResultsRaceId(originalResult.getResultsRaceId());
        //KEY_RESULTS_VISIBLE,
//        preUpdateResult.setResultsVisible(1);
        //KEY_RACE_NAME,
//        preUpdateResult.setRaceName(originalResult.getRaceName());
        //KEY_RACE_DATE,
//        preUpdateResult.setRaceDate(originalResult.getRaceDate());
        //KEY_RESULTS_FINISH_TIME,
//        preUpdateResult.setResultsBoatFinishTime(originalResult.getResultsBoatFinishTime());

        //KEY_BOAT_ID, IS SET IN SPINNER onselect

//        preUpdateResult.setResultsBoatId(r.getResultsBoatId());
        //KEY_RESULTS_CLASS_START,
        preUpdateResult.setResultsClassStartTime(classStart.getText().toString());
//        classStart.setText(r.getResultsClassStartTime());
        //KEY_RESULTS_PENALTY,
        preUpdateResult.setResultsPenalty(Integer.parseInt(penalty.getText().toString()));
//        penalty.setText(r.getResultsPenalty());
        //KEY_RESULTS_NOTE,
        preUpdateResult.setResultsNote(notes.getText().toString());
//        notes.setText(r.getResultsNote());
        //KEY_RESULTS_NOT_FINISHED,
//        preUpdateResult.setResultsNotFinished((didNotFinish.isChecked()) ? 1 : 0);
//        if (r.getResultsNotFinished() == 1) {
//            didNotFinish.setChecked(false);
//        } else {
//            didNotFinish.setChecked(true);
//        }
        //KEY_RESULTS_MANUAL_ENTRY,
        preUpdateResult.setResultsManualEntry((manualEntryMode.isChecked()) ? 1 : 0);
//        if (r.getResultsNotFinished() == 1) {
//            manualEntryMode.setChecked(true);
//        } else {
//            manualEntryMode.setChecked(false);
//        }
        //KEY_BOAT_NAME,
        preUpdateResult.setBoatName(boatNameSpn.getSelectedItem().toString());
//        boatNameSpn.setSelection(boatNameArrayAdapter.getPosition(r.getBoatName()));
        //KEY_BOAT_SAIL_NUM,
        preUpdateResult.setBoatSailNum(sailNum.getText().toString());
//        sailNum.setText(r.getBoatSailNum());
        //KEY_BOAT_CLASS,
        preUpdateResult.setBoatClass(boatClassSpn.getSelectedItem().toString());
//        boatClassSpn.setSelection(boatClassArrayAdapter.getPosition(r.getBoatClass()));
        //KEY_BOAT_PHRF,
        preUpdateResult.setBoatPHRF(Integer.parseInt(phrf.getText().toString()));
//        phrf.setText(r.getBoatPHRF());
        //KEY_RACE_DISTANCE
        preUpdateResult.setRaceDistance(Double.parseDouble(distance.getText().toString()));
//        distance.setText(r.getRaceDistance() + "");
        //KEY_RESULTS_PLACE,
//        preUpdateResult.setResultsPlace(r.getResultsPlace());
        //Todo, add text view to hold race place
        //KEY_RESULTS_DURATION,
        preUpdateResult.setResultsDuration(elapsedTime.getText().toString());
        //KEY_RESULTS_ADJ_DURATION,
        preUpdateResult.setResultsAdjDuration(adjDuration.getText().toString());
    }

    private void wireListeners() {
        // allow or disallow editing of boat details.
        editBoatDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                penalty.clearFocus();
                if (isChecked) {
                    // allow user to edit boat details
                    boatClassSpn.setClickable(true);
                    phrf.setEnabled(true);
                    sailNum.setEnabled(true);
                    // check if an elapsed time has been recorded
                    if (elapsedTime.getText().length() > 4) {
                        // if elapsed time has been recorded then allow user to swap boats
                        boatNameSpn.setClickable(true);
                    }

                } else {
                    //disable all boat detail fields
                    boatClassSpn.setClickable(false);
                    boatNameSpn.setClickable(false);
                    phrf.setEnabled(false);
                    sailNum.setEnabled(false);

                    // return activity's values back to pre edited defaults.
                    boatClassSpn.setSelection(boatClassArrayAdapter.getPosition(
                            originalResult.getBoatClass()));
                    boatNameSpn.setSelection(boatNameArrayAdapter.getPosition(
                            originalResult.getBoatName()));
                    phrf.setText(originalResult.getBoatPHRF() + "");
                    sailNum.setText(originalResult.getBoatSailNum());
                    penalty.setText(originalResult.getResultsPenalty() + "");
                    notes.setText(originalResult.getResultsNote());
                    classStart.setText(originalResult.getResultsClassStartTime());
                    if (originalResult.getResultsNotFinished() == 1) {
                        didNotFinish.setSelected(true);
                    } else {
                        didNotFinish.setSelected(false);
                    }
                    if (originalResult.getResultsManualEntry() == 1) {
                        manualEntryMode.setSelected(true);
                    } else {
                        manualEntryMode.setSelected(false);
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pseudo delete the current record
                // build dialog box for confirmation
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Delete Confirmation");
                alertDialog.setMessage("Delete this result?\n" +
                        "WARNING!: You cannot undo this event.");
                alertDialog.setCancelable(false);

                // User chooses confirm
                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { //if yes
                        Log.i(LOGTAG, " Complete reset dialog result is CONFIRMED");
                        // delete the current result.
                        resultDataSource.pseudoDeleteResult(resultId);
                        //set selected status Not Selected in the select boats table
                        // so the user may re-add the boat later
                        selectBoatDataSource.setSelectedByBoatId(preUpdateResult.getResultsBoatId(), false);
                        finishResultEditor(); // close out of the activity
                    }
                });

                //User choose no
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(LOGTAG, " delete dialog result is NO");
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        //manual entry mode checkbox
        manualEntryMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                penalty.clearFocus();
                if (isChecked) {
                    //if checked allow user to enter custom elapsed time
                    setElapsedTime.setEnabled(true);
                    Log.i(LOGTAG, "CHECKBOX ENABLED ++");
                } else {
                    //if unchecked, disable the button.
                    setElapsedTime.setEnabled(false);
                    Log.i(LOGTAG, "CHECKBOX -------------------- DISABLED ++");
                    //recalculate the duration based on start and finish time

                    // check if the duration field is empty.
                    if (finishTime.getText().toString().length() > 4) {
                        long milliDuration = GlobalContent.getDurationInMillis(classStart.getText()
                                        .toString(),
                                finishTime.getText().toString());
                        // convert to readable format
                        String newDuration = GlobalContent.convertMillisToFormattedTime(
                                milliDuration, 0);
                        elapsedTime.setText(newDuration); //set the calculated time
                        preUpdateResult.setResultsDuration(newDuration);
                        calculateAdjustedDuration(); // recalculate adjusted duration
                    } else {
                        elapsedTime.setText(null); // no finish means no elapsed can be calculated
                    }
                }
            }
        });

        penalty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // if there is data in the penalty editor then recalculate the adjusted duration
                    if (penalty.getText().toString().length() == 0) {
                        penalty.setText("0");
                    }
                    calculateAdjustedDuration(); // calculate the adjusted duration
                }
            }
        });

        // if elapsed time is entered manually, calculate the adjusted time
        elapsedTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if the there is text in the elapsed time and manual entry is checked
                if (elapsedTime.length() > 0 && manualEntryMode.isChecked()) {
                    penalty.clearFocus(); // clear focus to prevent errors
                    // update the pre update result's elapsed time to match the edited time
                    preUpdateResult.setResultsDuration(elapsedTime.getText().toString());
                    // indicate that manual entry has been selected
                    preUpdateResult.setResultsManualEntry(1);
                    Log.i(LOGTAG, "elapsedTime textchangelistener ontextchanged elapsed time is "
                            + elapsedTime.getText().toString());
                    //calculate the adjusted duration.
                    calculateAdjustedDuration();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // open up time picker dialog
        setElapsedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a bundle for the time picker to use
                Bundle bundle = new Bundle();
                //check if the time requirement is met
                if (elapsedTime.getText().toString().length() > 0) {
                    bundle.putString("Value1", elapsedTime.getText().toString());
                } else {
                    bundle.putString("Value1", "00:00:00");
                }

                //set up time picker and ste the bundle
                FragmentManager manager = getFragmentManager();
                TimePickerDialog picker = new TimePickerDialog();
                picker.setArguments(bundle);

                //open the time picker
                picker.show(manager, "HH:MM:SS");
            }
        });

        //update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clear the focus from the penalty edit text
                penalty.clearFocus();
                //make sure there are no commas! bad for SQL table
                if (!GlobalContent.checkForCommas(v.getContext(), notes.getText().toString(),
                        sailNum.getText().toString())) {
                    if (manualEntryMode.isChecked() && elapsedTime.length() == 0) {
                        Toast.makeText(v.getContext(),
                                "\"Manual Entry\" is selected. You MUST set an Elapsed Time",
                                Toast.LENGTH_LONG).show();
                    } else {
                        syncViewsToPreUpdate();// make a result with all the visible data
                        if (boatHasChanged) {
                            // delete the unfinished boat that this result is now representing
                            resultDataSource.pseudoDeleteResult(preUpdateResult.getResultsRaceId()
                                    , preUpdateResult.getResultsBoatId());
                        }
                        // commit the changes in the activity to the results table
                        resultDataSource.update(preUpdateResult);

                        // re-run calculations to to incorporate changes to the result entry
                        resultDataSource.runCalculations();

                        //sync the list with the adapter.
                        finish();
                    }
                }
            }
        });


        // cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //notes remaining characters
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override // show the max number chars less the number of chars in the field
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesCharRemaining.setText(String.valueOf(totalChars - notes.length()));
            }
        });
    }

    private void finishResultEditor() {
        //close out of the activity.
        finish();
    }

    private void setupSpinners() {


        // get list of all current boat classes
        ArrayList<String> currentlyRacingClassesSPN = resultDataSource.getAllClasses();
        ArrayList<String> currentlyRacingResultsSPN = new ArrayList<>();

        // add the names of each race result to the spinner array list.
        for (Result r : currentlyRacingResults) {
            currentlyRacingResultsSPN.add(r.getBoatName());
            Log.i(LOGTAG, "adding " + r.getBoatName() + " to currentlyRacingResults Array List");
        }

        // add values to spinners

        // currently racing spinner adapter
        boatNameArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                currentlyRacingResultsSPN);

        // currently racing classes adapter
        boatClassArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                currentlyRacingClassesSPN);

        //set adapters
        boatNameSpn.setAdapter(boatNameArrayAdapter);
        boatClassSpn.setAdapter(boatClassArrayAdapter);

        boatNameSpn.setSelection(boatNameArrayAdapter.getPosition(updateFieldsFromCursor
                .getString(updateFieldsFromCursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME))));

        boatClassSpn.setSelection(boatClassArrayAdapter.getPosition(updateFieldsFromCursor
                .getString(updateFieldsFromCursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS))));

        ///// SPINNERs
        boatNameSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (editBoatDetails.isChecked()) {
                    Log.i(LOGTAG, "NameSpinner: editBoatDetails == true");
                    // get current selection
                    String spinnerSelection = boatNameSpn.getSelectedItem().toString();
                    // check if the selection has changed from original position
                    if (!spinnerSelection.equals(originalResult.getBoatName())) {
                        boatHasChanged = true; // indicate that the result's boat changed
                        Log.i(LOGTAG, "NameSpinner: calling boatNameSpn OnItemSelectedListener: Selected Item CHANGED");
                        //if changed, find result that matches new selection
                        for (Result r : currentlyRacingResults) {
                            //check if selected boat name matches result boat name
                            if (r.getBoatName().equals(spinnerSelection)) {
                                //set all boat details to match new selection
                                sailNum.setText(r.getBoatSailNum());
                                boatClassSpn.setSelection(boatClassArrayAdapter.getPosition(
                                        r.getBoatClass()));
                                Log.i(LOGTAG, "NameSpinner: CLASS SPINNER CHANGED ");
                                phrf.setText(r.getBoatPHRF() + "");
                                classStart.setText(r.getResultsClassStartTime()); // start time
                                boolean isManualEntry = false;
                                // check if manual entry was set for this boat.
                                if (r.getResultsManualEntry() == 1) {
                                    isManualEntry = true;
                                }
                                manualEntryMode.setChecked(isManualEntry);
                                penalty.setText(r.getResultsPenalty() + ""); // penalty
                                notes.setText(r.getResultsNote()); // notes
                                if (r.getResultsNotFinished() == 1) {
                                    didNotFinish.setSelected(true);
                                } else {
                                    didNotFinish.setSelected(false);
                                } // dnf status
                                preUpdateResult.setResultsBoatId(r.getResultsBoatId());

                            }
                        }
                    } else {
                        boatHasChanged = false; // indicate that the boat has not changed
                        Log.i(LOGTAG, "NameSpinner:: Selected Item " +
                                "DID NOT CHANGE");


                        // return their values back to pre edited defaults.
                        sailNum.setText(originalResult.getBoatSailNum());
                        boatClassSpn.setSelection(boatClassArrayAdapter.getPosition(
                                originalResult.getBoatClass()));
                        Log.i(LOGTAG, "NameSpinner: CLASS SPINNER NO CHANGE ");
                        phrf.setText(originalResult.getBoatPHRF() + "");
                        classStart.setText(originalResult.getResultsClassStartTime());
                        penalty.setText(originalResult.getResultsPenalty() + "");
                        notes.setText(originalResult.getResultsNote());
                        if (originalResult.getResultsNotFinished() == 1) {
                            didNotFinish.setSelected(true);
                        } else {
                            didNotFinish.setSelected(false);
                        }
                        //set the boat id back to the original boat id
                        preUpdateResult.setResultsBoatId(originalResult.getResultsBoatId());
                    }
                } else {
                    Log.i(LOGTAG, "NameSpinner: editBoatDetails == FALSE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        boatClassSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editBoatDetails.isChecked()) {
                    Log.i(LOGTAG, "BoatSpinner: editBoatDetails == true");
                    // get the selected item and save as string
                    String spinnerSelection = boatClassSpn.getSelectedItem().toString();
                    //find the classes start time from the collection of start times in class starting list
                    for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                        Log.i(LOGTAG, "BoatSpinner - BoatStartingListClass.BOAT_CLASS_START_ARRAY " + bc.getBoatColor());
                        if (bc.getBoatColor().equals(spinnerSelection)) {
                            // if the class colors match, change the start time to that of the color in
                            // the starting class array list
                            final String time = bc.getStartTime();
                            if (time != null) {
                                Log.i(LOGTAG, "BoatSpinner - Time not null [] BoatClass " + bc.getBoatColor()
                                        + " start Time is " + time);
                                // put the values into the text fields
                                classStart.setText(time);
                                distance.setText(bc.getClassDistance() + "");

                                // run preliminary calculations
                                String finish = finishTime.getText().toString();
                                int thisPhrf = Integer.parseInt(phrf.getText().toString());
                                double pen = Double.parseDouble(penalty.getText().toString());
                                double dist = Double.parseDouble(distance.getText().toString());
                                // check if a finish time has been recorded and if activity is loading
                                if (finish.length() > 4) {
                                    // check if manual entry button was selected
                                    if (manualEntryMode.isChecked()) {
                                        // turn off manual entry mode
                                        manualEntryMode.setChecked(false);
                                    }
                                    long durationLong = GlobalContent.getDurationInMillis(time, finish);
                                    String durStr = GlobalContent.convertMillisToFormattedTime(
                                            durationLong, 0);
                                    preUpdateResult.setResultsBoatFinishTime(durStr);
                                    elapsedTime.setText(durStr);
                                    //KEY_RESULTS_ADJ_DURATION,
                                    String adjDur = GlobalContent.calculateAdjDuration(
                                            thisPhrf,
                                            durationLong,
                                            pen,
                                            dist,
                                            1.0
                                    );
                                    adjDuration.setText(adjDur);

                                } else {
                                    // blank out the elapsed and adjusted durations
                                    elapsedTime.setText(null);
                                    adjDuration.setText(null);
                                }

                            }
                        }
                    }
                } else {

                    Log.i(LOGTAG, "BoatSpinner: editBoatDetails == FALSE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    //calculate the adjusted duration using the values in the text boxes
    private void calculateAdjustedDuration() {

        if (elapsedTime.getText().toString().length() != 0) {
            // calculate the adjusted duration
            adjDuration.setText(GlobalContent.calculateAdjDuration(
                    Integer.parseInt(phrf.getText().toString()),
                    GlobalContent.getDurationInMillis(elapsedTime.getText().toString()),
                    Integer.parseInt(penalty.getText().toString()),
                    Double.parseDouble(distance.getText().toString()),
                    1));
        }
    }

    // prevent the user from using the devices back button
    @Override
    public void onBackPressed() {
//        super.onBackPressed(); // prevent the back button from being used to go back
        myLayout.requestFocus(); //should effectivly hide the keyboard
    }

    private void getValuesFromCursor() {
        //assign Textviews from cursor
        sailNum.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
        phrf.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
        distance.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RACE_DISTANCE)));
        classStart.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_CLASS_START)));
        finishTime.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_FINISH_TIME)));
        elapsedTime.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_DURATION)));
        adjDuration.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_ADJ_DURATION)));

        //assign EditTexts from cursor
        penalty.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_PENALTY)));
        notes.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_NOTE)));

        //assign CheckBoxes from cursor
        if (Integer.parseInt(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_NOT_FINISHED))) == 1) {
            didNotFinish.setChecked(true);
        }

        if (Integer.parseInt(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_RESULTS_MANUAL_ENTRY))) == 1) {
            manualEntryMode.setChecked(true);
        }
    }

    private void wireWidgets() {
        //Buttons
        update = (Button) findViewById(R.id.btn_rf_update_result);
        cancel = (Button) findViewById(R.id.btn_rf_cancel_result);
        delete = (Button) findViewById(R.id.btn_rf_delete_result);
        setElapsedTime = (Button) findViewById(R.id.btn_rf_setElapsedTime);

        // spinners
        boatClassSpn = (Spinner) findViewById(R.id.spn_rf_boatClass);
        boatNameSpn = (Spinner) findViewById(R.id.spn_rf_boatName);

        //checkboxes
        didNotFinish = (CheckBox) findViewById(R.id.ckbx_rf_DNF);
        manualEntryMode = (CheckBox) findViewById(R.id.ckbx_rf_manualEntryMode);
        editBoatDetails = (CheckBox) findViewById(R.id.ckbx_rf_editBoatDetails); // new

        //TextViews
        distance = (TextView) findViewById(R.id.txt_rf_distance);
        classStart = (TextView) findViewById(R.id.txt_rf_classStart); // boat detail
        finishTime = (TextView) findViewById(R.id.txt_rf_boatFinishTime); // boat detail
        elapsedTime = (TextView) findViewById(R.id.txt_rf_ElapsedTime); // Calculated / Manual enter
        adjDuration = (TextView) findViewById(R.id.txt_rf_AdjDuration); // Calculated

        //special textview for calculating remaining characters
        notesCharRemaining = (TextView) findViewById(R.id.txt_rf_sub_notesRemainingChars);

        //Edit texts
        penalty = (EditText) findViewById(R.id.txt_rf_Penalty);
        notes = (EditText) findViewById(R.id.txt_rf_notes);
        sailNum = (EditText) findViewById(R.id.txt_rf_sailNum); // boat detail
        phrf = (EditText) findViewById(R.id.txt_rf_PHRF); // boat detail
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results_editor, menu);
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

    @Override
    public void onUserInteraction() {
        dimmer.resetDelay(); // reschedule dimmer task
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOGTAG, " onResume Now");
        resultDataSource.open(); // reopen the db
        selectBoatDataSource.open();
        dimmer.start(); // start/resume dimmer timer
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOGTAG, " onPause NOW");
        dimmer.end(); // release dimmer
        resultDataSource.close(); // close db to reduce data leak
        selectBoatDataSource.close();
    }

    @Override
    public void onDialogMessage(long message) {
            // set the elapsed time to the data from teh dialog
            elapsedTime.setText(GlobalContent.convertMillisToFormattedTime(message,0));
    }
}
