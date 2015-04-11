package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.Race;
import aad.finalproject.db.RaceDataSource;


public class RaceAddForm extends Form  {

    private static String LOG; // create a log tag that changes based on activity mode


    // create empty instances for widgets
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

    // create create field data containers
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


    //Time handleing variables
    private Calendar calendar;
    private int year, month, day;



    Bundle extras;
    // initialize the race data source object
    RaceDataSource raceDataSource;
    Cursor updateRowFromCursor; //create a cursor to hold single row of data for updates/edits

    // initialize button widgets
    Button create;
    Button update;
    Button delete;

    // date formatter instance
    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_add_form);
        Log.i("Race add edit form ","Form opened" );

        //open an Writable instance of the RaceDataSource
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();

        extras = getIntent().getExtras(); // get the data from the intent for use by this activity


        //load up text and other fields into widgets
        raceTitle = (EditText) findViewById(R.id.txt_inpt_RaceTitle);
        raceDistance = (EditText) findViewById(R.id.txt_inpt_RaceDistance);
        raceClassRed = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassRed);
        raceClassBlue = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassBlue);
        raceClassPurple = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassPurple);
        raceClassGreen = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassGreen);
        raceClassYellow = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassYellow);
        raceClass_TBD_ = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClass_TBD_);

        // special properties for the text box with date data.
        raceDateMM = (EditText) findViewById(R.id.txt_inpt_RaceDateMM);
        raceDateDD =  (EditText) findViewById(R.id.txt_inpt_RaceDateDD);
        raceDateYYYY =  (EditText) findViewById(R.id.txt_inpt_RaceDateYYYY);

            // wire button widgets to button instances
        create = (Button) findViewById(R.id.btn_add_race);
        update = (Button) findViewById(R.id.btn_update_race);
        delete = (Button) findViewById(R.id.btn_delete_race);



        if (extras.getString(RaceMenu.ACCESS_METHOD_KEY).equals("EDIT")) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);


            LOG = "Race EDIT form";
            raceDataSource.getRow(Form.getROW_ID());

            //if EDIT mode is active initialize cursor fields
            updateRowFromCursor = raceDataSource.getRow(getROW_ID());

            //populate the edit form fields with data from selected race
            raceTitle.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_NAME)));
            raceDistance.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_DISTANCE)));

            String[] dateTemp = updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(DBAdapter.KEY_RACE_DATE)).split("/");
            raceDateMM.setText(dateTemp[0]);
            raceDateDD.setText(dateTemp[1]);
            raceDateYYYY.setText(dateTemp[2]);

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
        } else if (extras.getString(RaceMenu.ACCESS_METHOD_KEY).equals("CREATE")) {
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
    public void onClickCancel(View view) {
        super.onClickCancel(view);
        raceDataSource.close();
    }

    @Override
    public void onClickCreate(View view) {
        setTempDataFields();
        if (validateDataEntryFields()) {

            Race newRace = new Race(); // creae a new race instance

            // load new race instance with data from validated fields
            newRace.setName(strraceTitle);
            newRace.setDate(strraceDate);
            newRace.setDistance(Double.parseDouble(strraceDistance));
            newRace.setClsRed(intRaceClassRed);
            newRace.setClsBlue(intRaceClassBlue);
            newRace.setClsPurple(intRaceClassPurple);
            newRace.setClsYellow(intRaceClassYellow);
            newRace.setClsGreen(intRaceClassGreen);
            newRace.setCls_TBD_(intRaceClass_TBD_);
            raceDataSource.create(newRace); //assign the race class to the db


            // open the select boats list
            Intent intent = new Intent(this, SelectBoats.class);
            startActivity(intent);
            Log.i("BoatAddForm ", "Validated entry added");
        }

    }
    @Override
    public void onClickUpdate(View view){
        update(getROW_ID());

    }
    @Override
    protected void update(long id) {
        Cursor cursor = raceDataSource.getRow(id); // create cursor
        setTempDataFields();
        if (validateDataEntryFields()) {
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                raceDataSource.update(id, strraceTitle, strraceDate, Double.parseDouble(strraceDistance),
                        intRaceClassBlue, intRaceClassGreen, intRaceClassPurple,intRaceClassYellow,
                        intRaceClassRed, intRaceClass_TBD_);
                Log.i(LOG, "Validated UPDATE entry");
                this.finish();

            } else {
                Toast.makeText(getApplicationContext(), "Cursor error, bad ID",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG, "CURSOR ERROR>> BAD ID");
            }
            cursor.close();
        }

    }
    @Override
    public void onClickDelete(View view) {
        delete();
    }

    @Override
    void delete() {
        Cursor cursor = raceDataSource.getRow(getROW_ID());
        if (validateDataEntryFields()) {

            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!!!! CANNOT UNDO A DELETION!");
                builder.setMessage("Warning:\n" +
                        "You are about to delete this boat. You cannot undo this event");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        raceDataSource.psudoDelete(getROW_ID());
                        Log.i(LOG, "Validated PsudoDeleted entry");

                        dialog.dismiss();
                        finish(); // close out of the current activity. Back to boatmenu
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Toast.makeText(getApplicationContext(), "Cursor error, bad ID",
                        Toast.LENGTH_LONG).show();
//                cursor.close();
                Log.i(LOG, "CURSOR ERROR>> BAD ID");
            }
        }
    }

    @Override
    protected boolean validateDataEntryFields(){
        boolean isValid; // declare return value
        setTempDataFields(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        if (    !raceTitle.getText().toString().equals("") &&
                !raceDateMM.getText().toString().equals("") &&
                !raceDateDD.getText().toString().equals("") &&
                !raceDateYYYY.getText().toString().equals("") &&
                !raceDistance.getText().toString().equals("")){
            isValid = true;
        } else {
            Toast.makeText(this,"All fields are required",
                    Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if( (isValid) &&
            (raceClassRed.isChecked() ||
             raceClassBlue.isChecked() ||
             raceClassPurple.isChecked() ||
             raceClassYellow.isChecked() ||
             raceClassGreen.isChecked() ||
             raceClass_TBD_.isChecked()
        )) {
            isValid = true;
        } else {
            Toast.makeText(this,"You must select at least 1 boat class",
                    Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if( isValid && Double.parseDouble(strraceDistance) > 0  ){
            isValid = true;
        } else {
            Toast.makeText(this,"The race distance must be greater than 0",
                    Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    @Override
    protected void setTempDataFields() {
        //pass values from data entry fields and spinner to String variables
        strraceTitle = raceTitle.getText().toString();
        strraceDateMM = Integer.parseInt(raceDateMM.getText().toString());
        strraceDateDD = Integer.parseInt(raceDateDD.getText().toString());
        strraceDateYYYY = Integer.parseInt(raceDateYYYY.getText().toString());
        strraceDate = strraceDateMM + "/" + strraceDateDD + "/" + strraceDateYYYY;
        strraceDistance = raceDistance.getText().toString();
        intRaceClassRed = (raceClassRed.isChecked()) ? 1 : 0;
        intRaceClassBlue = (raceClassBlue.isChecked()) ? 1 : 0;
        intRaceClassPurple = (raceClassPurple.isChecked()) ? 1 : 0;
        intRaceClassYellow = (raceClassYellow.isChecked()) ? 1 : 0;
        intRaceClassGreen = (raceClassGreen.isChecked()) ? 1 : 0;
        intRaceClass_TBD_ = (raceClass_TBD_.isChecked()) ? 1 : 0;

    }



}
