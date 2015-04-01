package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import aad.finalproject.db.DBAdapter;


public class RaceAddForm extends RaceMenu {

    private static String LOG; // create a log tag that changes based on activity mode



    EditText raceTitle;
    EditText raceDateMM;
    EditText raceDateDD;
    EditText raceDateYYYY;
    EditText raceDistance;
    CheckBox raceClassRed;
    CheckBox raceClassBlue;
    CheckBox raceClassPurple;
    CheckBox raceClassYellow;
    CheckBox raceClassGreen;
    CheckBox raceClass_TBD_;

    String strraceTitle;
    String strraceDate;
    int strraceDateYYYY;
    int strraceDateDD;
    int strraceDateMM;
    String strraceDistance;
    double dblRaceDistance;
    int intRaceClassRed;
    int intRaceClassBlue;
    int intRaceClassPurple;
    int intRaceClassYellow;
    int intRaceClassGreen;
    int intRaceClass_TBD_;


    Cursor updateRowFromCursor; //create a cursor to hold single row of data for updates/edits

    // initialize button widgets
    Button create;
    Button update;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_add_form);
        Log.i("Race add edit form ","Form opened" );

        //load up text and other fields into widgets
        raceTitle = (EditText) findViewById(R.id.txt_inpt_RaceTitle);
        raceDateMM = (EditText) findViewById(R.id.txt_inpt_RaceDateMM);
        raceDateDD =  (EditText) findViewById(R.id.txt_inpt_RaceDateDD);
        raceDateYYYY =  (EditText) findViewById(R.id.txt_inpt_RaceDateYYYY);
        raceDistance = (EditText) findViewById(R.id.txt_inpt_RaceDistance);
        raceClassRed = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassRed);
        raceClassBlue = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassBlue);
        raceClassPurple = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassPurple);
        raceClassGreen = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassGreen);
        raceClassYellow = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassYellow);
        raceClass_TBD_ = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClass_TBD_);

        // wire button widgets to button instances
        create = (Button) findViewById(R.id.btn_add_race);
        update = (Button) findViewById(R.id.btn_update_race);
        delete = (Button) findViewById(R.id.btn_delete_race);

        if (RaceMenu.CHILD_ACTIVITY_TYPE_SWITCHER.equals("EDIT")) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            LOG = "Race EDIT form";
            raceDataSource.getRow(RaceMenu.ROW_ID);

            //if EDIT mode is active initialize cursor fields
            updateRowFromCursor = raceDataSource.getRow(ROW_ID);

            //populate the edit form fields with data from selected race
            raceTitle.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_NAME)));
            raceDistance.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_DISTANCE)));


            //check if the variable in SQLite is 1 or 0, if one set the checkbox to checked
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_RED))) == 1) {
                raceClassRed.setChecked(true);
            }
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_BLUE))) == 1) {
                raceClassBlue.setChecked(true);
            }
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_YELLOW))) == 1) {
                raceClassYellow.setChecked(true);
            }
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_GREEN))) == 1) {
                raceClassGreen.setChecked(true);
            }
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_PURPLE))) == 1) {
                raceClassPurple.setChecked(true);
            }
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_TBD))) == 1) {
                raceClass_TBD_.setChecked(true);
            }

            Log.i(LOG, "Edit mode buttons activated");
        } else if (RaceMenu.CHILD_ACTIVITY_TYPE_SWITCHER.equals("CREATE")) {
            create.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            LOG = "Race CREATE form";
            Log.i(LOG, "create mode buttons activated");
        } else {
            Log.i(LOG, "NO RACE CHILD ACIVITY TYPE ERROR!!!!");

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race_add_form, menu);
        Log.i("BoatAddForm ","OnCreateOptionsMenu" );
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
    public void onClickCancel(View view){
        finish();
        Log.i("BoatAddForm ","Clicked Cancel" );
    }

    public void onClickUpdate(View view) {

    }

    public void onClickAddRace(View view) {
        loadTextFieldsToStrings();
        if (validateForm()) {
            //TODO Place insert into SQLite database BOATS statement here
            Intent intent = new Intent(this, SelectBoats.class);
            startActivity(intent);
            Log.i("BoatAddForm ", "Validated entry added");
        }
//
//
//        if (raceDateYYYY.getText().toString().length() != 0
//                && raceDateDD.getText().toString().length() != 0
//                && raceDateMM.getText().toString().length() != 0) {
//            strraceDateMM = Integer.valueOf(raceDateMM.getText().toString());
//            strraceDateDD = Integer.valueOf(raceDateDD.getText().toString());
//            strraceDateYYYY = Integer.valueOf(raceDateYYYY.getText().toString());
//            isEmptyDate = false;
//        } else {
//            isEmptyDate = true;
//        }
//
//        // create a string in the format of a date from the values provided
//        strraceDate = strraceDateMM + "/" + strraceDateDD + "/" + strraceDateYYYY;
//
//        strraceTitle = raceTitle.getText().toString();
//        strraceDistance = raceDistance.getText().toString();
//
//        //check to see if at least one class is selected
//        if (raceClass_TBD_.isChecked() || raceClassYellow.isChecked()
//                || raceClassGreen.isChecked() || raceClassPurple.isChecked()
//                || raceClassBlue.isChecked() || raceClassRed.isChecked()) {
//            Log.i("BoatAddForm ", "Passed RaceClass checked Check");
//            // Check to make sure text fields are not blank
//            if (strraceTitle.length() != 0
//                    && strraceDistance.length() != 0
//                    && !isEmptyDate) {
//                Log.i("BoatAddForm ", "Passed Empty Field Check");
//                //check to see if the date is formatted properly
//                if (strraceDateMM <= 12 && strraceDateDD <= 31 && strraceDateYYYY >= 1984) {
//
//                    Toast.makeText(getApplicationContext(), "Validation True",
//                            Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(this, SelectBoats.class);
//                    startActivity(intent);
//                    Log.i("BoatAddForm ", "Validated entry added");
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "Please correct the date format",
//                            Toast.LENGTH_LONG).show();
//                    Log.i("BoatAddForm ", "Invalid Date fields");
//                }
//            } else {
//                Toast.makeText(RaceAddForm.this, "All text fields are required",
//                        Toast.LENGTH_LONG).show();
//                Log.i("BoatAddForm ", "Missing text fields");
//            }
//
//        } else {
//            Toast.makeText(getApplicationContext(), "You must select at least 1 class",
//                    Toast.LENGTH_LONG).show();
//            Log.i("BoatAddForm ", "Invalid checkbox fields");
//        }
    }
    private void UpdateRace(long id) {
        Cursor cursor = raceDataSource.getRow(id); // create cursor
        loadTextFieldsToStrings();
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                raceDataSource.update(id, strraceTitle, strraceDate, dblRaceDistance,
                        intRaceClassBlue, intRaceClassGreen, intRaceClassPurple,intRaceClassYellow,
                        intRaceClassRed, intRaceClass_TBD_);
                Log.i(LOG, "Validated UPDATE entry");

            } else {
                Toast.makeText(getApplicationContext(), "Cursor error, bad ID",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG, "CURSOR ERROR>> BAD ID");
            }
            cursor.close();

    }
    private boolean validateForm(){
        boolean isValid; // declare return vallue
        loadTextFieldsToStrings(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        if (
                !raceTitle.getText().toString().equals("") &&
                !raceDateMM.getText().toString().equals("") &&
                !raceDateDD.getText().toString().equals("") &&
                !raceDateYYYY.getText().toString().equals("") &&
                !raceDistance.getText().toString().equals("") && (
                        raceClassRed.isChecked() ||
                        raceClassBlue.isChecked() ||
                        raceClassPurple.isChecked() ||
                        raceClassYellow.isChecked() ||
                        raceClassGreen.isChecked() ||
                        raceClass_TBD_.isChecked()
                        )) {
            isValid = true;

        } else {
            isValid = false;
            Toast.makeText(this,"All fields are require and you must choose at least one class",
                    Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    private void loadTextFieldsToStrings() {
        //pass values from data entry fields and spinner to String variables
        strraceTitle = raceTitle.getText().toString();
        strraceDateMM = Integer.parseInt(raceDateMM.getText().toString());
        strraceDateDD = Integer.parseInt(raceDateDD.getText().toString());
        strraceDateYYYY = Integer.parseInt(raceDateYYYY.getText().toString());
        strraceDate = strraceDateMM + "/" + strraceDateDD + "/" + strraceDateYYYY;
        strraceDistance = raceDistance.getText().toString();
        dblRaceDistance = Double.parseDouble(raceDistance.getText().toString());
        intRaceClassRed = (raceClassRed.isChecked()) ? 1 : 0;
        intRaceClassBlue = (raceClassBlue.isChecked()) ? 1 : 0;
        intRaceClassPurple = (raceClassPurple.isChecked()) ? 1 : 0;
        intRaceClassYellow = (raceClassYellow.isChecked()) ? 1 : 0;
        intRaceClassGreen = (raceClassGreen.isChecked()) ? 1 : 0;
        intRaceClass_TBD_ = (raceClass_TBD_.isChecked()) ? 1 : 0;

    }

}
