package aad.finalproject.jhoregatta;

import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.ResultDataSource;


public class ResultsEditor extends MainActivity  implements TimePickerDialog.Communicator {

    private static final String LOGTAG = "Logtag: ResultsEditor"; // logtag component
    //widget instances
    private Button update, cancel, setElapsedTime;
    private CheckBox didNotFinish, manualEntryMode;
    private EditText penalty, notes;
    private TextView boatName, sailNum, boatClass, phrf, distance, elapsedTime, adjDuration,
            finishTime, classStart, notesCharRemaining;
    private Dimmer dimmer;


    /////////DATABASE SECTION////////////

    //cursor that holds all the data from the sql table.
    private Cursor updateFieldsFromCursor;

    // main linear layout
    private LinearLayout myLayout;

    //datasource instance
    private ResultDataSource resultDataSource;
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
        resultDataSource.open();

        //get the result from the sql table.
        updateFieldsFromCursor = resultDataSource.getRow(GlobalContent.getResultsRowID());

        //assing linear layout
        myLayout = (LinearLayout) findViewById(R.id.linlay_rf_mainLayout);

        wireWidgets(); //assign layout elements to variables


       /*
       //assign Listeners
        */

        //manual entry mode checkbox
        manualEntryMode.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
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
                    if (finishTime.getText().toString().length() > 0) {
                        long milliDuration = GlobalContent.getDurationInMillis(classStart.getText().toString(),
                                finishTime.getText().toString());
                        // convert to readable format
                        String newDuration = GlobalContent.convertMillisToFormattedTime(milliDuration, 0);
                        elapsedTime.setText(newDuration); //set the calculated time
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

        penalty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // if elapsed time is entered manually, calculate the adjusted time
        elapsedTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if the there is text in the elapsed time and manual entry is checked
                if (elapsedTime.length() > 0 && manualEntryMode.isChecked()) {
                    penalty.clearFocus(); // clear focus to prevent errors
                    //calculate the adjusted duration.
                    calculateAdjustedDuration();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (elapsedTime.length() == 0) {
//                    //if the text box is empty set to null
//                    elapsedTime.setText("");
//                }
            }
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
                if (!GlobalContent.checkForCommas(v.getContext(), notes.getText().toString())) {
                    if (manualEntryMode.isChecked() && elapsedTime.length() == 0) {
                        Toast.makeText(v.getContext(),
                                "\"Manual Entry\" is selected. You MUST set an Elapsed Time",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // grab values from text/edit fields
                        String elapsedTimeUD = elapsedTime.getText().toString();
                        int penaltyUD;
                        //if penalty is null or bad format then make it 0
                        try {
                            penaltyUD = Integer.parseInt(penalty.getText().toString());
                        } catch (NumberFormatException e) {
                            penaltyUD = 0;
                        }
                        String notesUD =  notes.getText().toString();
                        int didNotFinishUD = (didNotFinish.isChecked()) ? 1 : 0;
                        int manualEntryUD = (manualEntryMode.isChecked()) ? 1 : 0;

                        // call datasource update function using captured values.
                        resultDataSource.update(
                                GlobalContent.getResultsRowID(),
                                elapsedTimeUD,
                                penaltyUD,
                                notesUD,
                                didNotFinishUD,
                                manualEntryUD);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override // show the max number chars less the number of chars in the field
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesCharRemaining.setText(String.valueOf(totalChars - notes.length()));
            }
        });

        ///////////////initial widget states//////////////////////////////////////////

        getValuesFromCursor(); //grab the values from SQL

        //set elapsed time button is disabled until manual entry checkbox is clicked
        if (!manualEntryMode.isChecked()) {
            setElapsedTime.setEnabled(false);
            Log.i(LOGTAG, "CHECKBOX -------!!!--------- DISABLED @@");
        }

        notesCharRemaining.setText(String.valueOf(totalChars)); // initial count of characters
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
        boatName.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
        sailNum.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
        boatClass.setText(updateFieldsFromCursor.getString(updateFieldsFromCursor
                .getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
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
        update = (Button)findViewById(R.id.btn_rf_update_result);
        cancel = (Button)findViewById(R.id.btn_rf_cancel_result);
        setElapsedTime = (Button)findViewById(R.id.btn_rf_setElapsedTime);

        //checkboxes
        didNotFinish = (CheckBox)findViewById(R.id.ckbx_rf_DNF);
        manualEntryMode = (CheckBox)findViewById(R.id.ckbx_rf_manualEntryMode);

        //TextViews
        boatName = (TextView) findViewById(R.id.txt_rf_BoatName); // header/title
        sailNum = (TextView) findViewById(R.id.txt_rf_sailNum); // boat detail
        boatClass = (TextView) findViewById(R.id.txt_rf_BoatClass); // boat detail
        phrf = (TextView) findViewById(R.id.txt_rf_PHRF); // boat detail
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
        dimmer.start(); // start/resume dimmer timer
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOGTAG, " onPause NOW");
        dimmer.end(); // release dimmer
        resultDataSource.close(); // close db to reduce data leak
    }

    @Override
    public void onDialogMessage(long message) {
            // set the elapsed time to the data from teh dialog
            elapsedTime.setText(GlobalContent.convertMillisToFormattedTime(message,0));
    }
}
