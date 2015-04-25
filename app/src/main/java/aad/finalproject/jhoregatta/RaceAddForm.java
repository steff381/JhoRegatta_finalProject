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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.Race;
import aad.finalproject.db.RaceDataSource;


public class RaceAddForm extends Form {

    //logging header
    private String LOG = this.getClass().getSimpleName() + " - MODE:";

    public static boolean isBoatClassUpdate;  // if the update is boat update

    // create empty instances for widgets
    EditText raceTitle;
    EditText raceDateMM;
    EditText raceDateDD;
    EditText raceDateYYYY;
    EditText raceDistance;

    //checkboxes for class selection
    CheckBox raceClassRed;
    CheckBox raceClassBlue;
    CheckBox raceClassPurple;
    CheckBox raceClassYellow;
    CheckBox raceClassGreen;
    CheckBox raceClass_TBD_;

    ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();

    // textviews for showing start order
    TextView raceClass1Holder;
    TextView raceClass2Holder;
    TextView raceClass3Holder;
    TextView raceClass4Holder;
    TextView raceClass5Holder;
    TextView raceClass6Holder;

    ArrayList<TextView> boatClassHolderArrayList = new ArrayList<>();

    // create create field data containers
    String strraceTitle;
    String strraceDate;
    int strraceDateYYYY;
    int strraceDateDD;
    int strraceDateMM;
    String strraceDistance;
    int intRaceClassRed;
    int intRaceClassBlue;
    int intRaceClassPurple;
    int intRaceClassYellow;
    int intRaceClassGreen;
    int intRaceClass_TBD_;

    Bundle extras;// grab the saved instance of the bundle
    // initialize the race data source object
    RaceDataSource raceDataSource; // create instance of the race data source
    Cursor updateRowFromCursor; //create a cursor to hold single row of data for updates/edits

    // initialize button widgets
    Button create;
    Button update;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_add_form);
        Log.i(LOG, "Form opened");


        //open a Writable instance of the RaceDataSource
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();

        isBoatClassUpdate = false; // opening up the menu for the first time makes false

        BoatStartingListClass.clearBoatClassStartArray(); // empty the array of all data

        //load up text and other fields into widgets
        raceTitle = (EditText) findViewById(R.id.txt_inpt_RaceTitle);
        raceDistance = (EditText) findViewById(R.id.txt_inpt_RaceDistance);
        raceClassRed = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassRed);
        raceClassBlue = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassBlue);
        raceClassPurple = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassPurple);
        raceClassGreen = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassGreen);
        raceClassYellow = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassYellow);
        raceClass_TBD_ = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClass_TBD_);

        // add each checkbox to the check box array
        checkBoxArrayList.add(raceClassRed);
        checkBoxArrayList.add(raceClassBlue);
        checkBoxArrayList.add(raceClassPurple);
        checkBoxArrayList.add(raceClassGreen);
        checkBoxArrayList.add(raceClassYellow);
        checkBoxArrayList.add(raceClass_TBD_);

        // wire textviews to textview instances
        raceClass1Holder = (TextView) findViewById(R.id.txt_class_1_holder);
        raceClass2Holder = (TextView) findViewById(R.id.txt_class_2_holder);
        raceClass3Holder = (TextView) findViewById(R.id.txt_class_3_holder);
        raceClass4Holder = (TextView) findViewById(R.id.txt_class_4_holder);
        raceClass5Holder = (TextView) findViewById(R.id.txt_class_5_holder);
        raceClass6Holder = (TextView) findViewById(R.id.txt_class_6_holder);

        //build array list of raceclass holder textviews
        boatClassHolderArrayList.add(raceClass1Holder);
        boatClassHolderArrayList.add(raceClass2Holder);
        boatClassHolderArrayList.add(raceClass3Holder);
        boatClassHolderArrayList.add(raceClass4Holder);
        boatClassHolderArrayList.add(raceClass5Holder);
        boatClassHolderArrayList.add(raceClass6Holder);

        // set the oncheck listeners for each of the check boxes
        setCheckboxListeners();

        // special properties for the text box with date data.
        raceDateMM = (EditText) findViewById(R.id.txt_inpt_RaceDateMM);
        raceDateDD = (EditText) findViewById(R.id.txt_inpt_RaceDateDD);
        raceDateYYYY = (EditText) findViewById(R.id.txt_inpt_RaceDateYYYY);

        // wire button widgets to button instances
        create = (Button) findViewById(R.id.btn_add_race);
        update = (Button) findViewById(R.id.btn_update_race);
        delete = (Button) findViewById(R.id.btn_delete_race);

        // set the buttons for edit mode.
        if (GlobalContent.modeEdit.equals(GlobalContent.getRaceFormAccessMode())) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);

            //make the starting order text views invisible during edit mode.
            findViewById(R.id.linlay_class_start_order_textviews).setVisibility(View.INVISIBLE);


            LOG += " Race EDIT form"; // change the edit mode status for log cat
//            raceDataSource.getRow(GlobalContent.getRaceRowID()); // get the id from global contetn

            //if EDIT mode is active initialize cursor fields
            updateRowFromCursor = raceDataSource.getRow(GlobalContent.getRaceRowID());

            //populate the edit form fields with data from selected race
            raceTitle.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_NAME)));
            raceDistance.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_DISTANCE)));
            // set the text in the date fields from a split of the text version of the date
            String[] dateTemp = updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_DATE)).split("/");
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
            // set up button configuration for when user is creating a new race
        } else if (GlobalContent.modeAdd.equals(GlobalContent.getRaceFormAccessMode())) {

            LOG += " Race ADD form"; // change status for log cat

            create.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);

            //make the starting order text views visible during add mode.
            findViewById(R.id.linlay_class_start_order_textviews).setVisibility(View.VISIBLE);

            for (TextView tv : boatClassHolderArrayList) {
                Log.i(LOG, tv.getText().toString());

            }

            Log.i(LOG, "create mode buttons activated");
        } else {
            Log.i(LOG, "NO RACE CHILD ACIVITY TYPE ERROR!!!!");

        }

    }

    // set the onclick listners for each of the check boxes
    String boatCheckBoxName;


    // create method to assign checkbox listeners to the class check boxes
    public void setCheckboxListeners() {

        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            boatCheckBoxName = checkBoxArrayList.get(i).getText().toString().trim();
            final String BCBN = boatCheckBoxName;
            Log.i(LOG, " Adding Listener to " + boatCheckBoxName );
            checkBoxArrayList.get(i).setOnCheckedChangeListener(new CompoundButton.
                    OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // when it is checked for the boat class
                    if (isChecked) { // if the checkbox is checked
                        BoatStartingListClass.addToBoatClassStartArray(BCBN); // Add item to the arary

                    } else { // if the check box ISN'T checked.
                        BoatClass tmpBC = null;
                        //find the correct color in the boat class start array for removal
                        for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                            if (bc.getBoatColor().equals(BCBN)) {
                                tmpBC = bc;
                            }
                        }
                        // remove the boat class from the array when user deselects it.
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.remove(tmpBC);
                    }
                    bindTextViewWithClassColors(); // update the class order text views.
                }
            });
            boatCheckBoxName = null;

        }
    }


    //Shows the user the order of boat classes in text views. User can change them by
    // selecting checkboxes in the order they wish to start each class in the race

    protected void bindTextViewWithClassColors() {
        int i = 0;

        for (TextView tv : boatClassHolderArrayList) {
            try {
                //create boat class instance for each iteration
                BoatClass bc = BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(i);
                // set the text in the textview to the appropriate value.
                boatClassHolderArrayList.get(i).setText((i + 1) + " " + bc.getBoatColor());
            } catch (Exception e) {
                tv.setText((i + 1) + ""); // clear any data from holders with no classes
            }
            i++;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race_add_form, menu);
        Log.i("BoatAddForm ", "OnCreateOptionsMenu");
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
        endActivity();
    }

    @Override
    public void onClickCreate(View view) {
        setTempDataFields(); // load data from text fields
        if (validateDataEntryFields()) {

            Race newRace = new Race(); // creae a new race instance

            // load new race instance with data from validated fields
            newRace.setName(strraceTitle);
            newRace.setDate(strraceDate);
            try {
                newRace.setDistance(Double.parseDouble(strraceDistance));
            } catch (NumberFormatException e) {
                // do nothing. will be cought by validator
            }
            newRace.setClsRed(intRaceClassRed);
            newRace.setClsBlue(intRaceClassBlue);
            newRace.setClsPurple(intRaceClassPurple);
            newRace.setClsYellow(intRaceClassYellow);
            newRace.setClsGreen(intRaceClassGreen);
            newRace.setCls_TBD_(intRaceClass_TBD_);
            raceDataSource.create(newRace); //assign the race class to the db

            // grab the newly created RaceID and set it to the global content for use by
            // the results table
            long id = raceDataSource.getLastInsertedRowID();
            GlobalContent.setRaceRowID(id); // set the id of global content
            GlobalContent.setActiveRace(newRace,id); // set the active race to the new race
            Log.i(LOG, "Last inserted Id: " + id + " || Race name: " + newRace.getName());


            isBoatClassUpdate = true; // after the create, update goes to select boats
            // open the select boats list
            Intent intent = new Intent(this, SelectBoats.class);
            startActivity(intent);
            // remove the ADD functions and replace with "Update" functions
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            Log.i("BoatAddForm ", "Validated entry added");
        }

    }

//TODO: For testing
    public void onClickGoodLife(View view) {
//        setTempDataFields(); // load data from text fields
        if (true) {

            Race newRace = new Race(); // creae a new race instance

            // load new race instance with data from validated fields
            newRace.setName("Race TEST");
            newRace.setDate("1/1/2005");
            try {
                newRace.setDistance(2.15);
            } catch (NumberFormatException e) {
                // do nothing. will be cought by validator
            }
            newRace.setClsRed(1);
            newRace.setClsBlue(1);
            newRace.setClsPurple(1);
            newRace.setClsYellow(1);
            newRace.setClsGreen(1);
            newRace.setCls_TBD_(0);
            raceDataSource.create(newRace); //assign the race class to the db


            long id = raceDataSource.getLastInsertedRowID();
            GlobalContent.setRaceRowID(id); // set the id of global content
            GlobalContent.setActiveRace(newRace,id); // set the active race to the new race
            Log.i(LOG, "Last inserted Id: " + id + " || Race name: " + newRace.getName());

            BoatStartingListClass.addToBoatClassStartArray("Red");
            BoatStartingListClass.addToBoatClassStartArray("Blue");
            BoatStartingListClass.addToBoatClassStartArray("Purple");
            BoatStartingListClass.addToBoatClassStartArray("Yellow");
            BoatStartingListClass.addToBoatClassStartArray("Green");

            isBoatClassUpdate = true; // after the create, update goes to select boats
            // open the select boats list
            Intent intent = new Intent(this, SelectBoats.class);
            startActivity(intent);

            // remove the ADD functions and replace with "Update" functions
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            Log.i("BoatAddForm ", "Validated entry added");

        }

    }

    @Override
    public void onClickUpdate(View view) {
        update(GlobalContent.getRaceRowID()); // update the data

    }

    @Override
    protected void update(long id) {
        Cursor cursor = raceDataSource.getRow(id); // create cursor
        setTempDataFields();
        if (validateDataEntryFields()) {
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                raceDataSource.update(id, strraceTitle, strraceDate, Double.parseDouble(strraceDistance),
                        intRaceClassBlue, intRaceClassGreen, intRaceClassPurple, intRaceClassYellow,
                        intRaceClassRed, intRaceClass_TBD_);
                Log.i(LOG, "Validated UPDATE entry");

                if (!isBoatClassUpdate) {
                    endActivity();
                } else {
                    // open the select boats list
                    Intent intent = new Intent(this, SelectBoats.class);
                    startActivity(intent);
                }

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
    } // simply delete the record

    @Override
    void delete() {
        Cursor cursor = raceDataSource.getRow(GlobalContent.getRaceRowID());


            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!!!! CANNOT UNDO A DELETION!");
                builder.setMessage("Warning:\n" +
                        "You are about to delete this boat. You cannot undo this event");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        raceDataSource.psudoDelete(GlobalContent.getRaceRowID());
                        Log.i(LOG, "Validated PsudoDeleted entry");

                        dialog.dismiss();
                        endActivity(); // close out of the current activity. Back to boatmenu
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

        cursor.close();
    }

    @Override
    protected boolean validateDataEntryFields() {
//        boolean isValid; // declare return value
        setTempDataFields(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        if (!raceTitle.getText().toString().equals("") &&
                !raceDateMM.getText().toString().equals("") &&
                !raceDateDD.getText().toString().equals("") &&
                !raceDateYYYY.getText().toString().equals("") &&
                !raceDistance.getText().toString().equals("")) {
//            isValid = true;
        } else {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (
                (raceClassRed.isChecked() ||
                        raceClassBlue.isChecked() ||
                        raceClassPurple.isChecked() ||
                        raceClassYellow.isChecked() ||
                        raceClassGreen.isChecked() ||
                        raceClass_TBD_.isChecked()
                )) {
//            isValid = true;
        } else {
            Toast.makeText(this, "You must select at least 1 boat class",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (Double.parseDouble(strraceDistance) <= 0) {
            Toast.makeText(this, "The race distance must be greater than 0",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (Integer.parseInt(raceDateMM.getText().toString()) > 0
                && Integer.parseInt(raceDateMM.getText().toString()) <= 12
                && Integer.parseInt(raceDateDD.getText().toString()) > 0
                && Integer.parseInt(raceDateDD.getText().toString()) <= 31
                && Integer.parseInt(raceDateYYYY.getText().toString()) > 999
                && Integer.parseInt(raceDateYYYY.getText().toString()) < 2081) {


        } else {
            Toast.makeText(this, " Check your date format. \n mm / dd / yyyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    @Override
    protected void setTempDataFields() {
        //pass values from data entry fields and spinner to String variables
        try {
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
        } catch (NumberFormatException e) {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_LONG).show();
        }

    }
    // user clicks today and today's date appears in the date picker
    public void onClickToday(View view){
        DateTime date = new DateTime();
        date.toLocalDate();
        raceDateMM.setText(date.getMonthOfYear() + "");
        raceDateDD.setText(date.getDayOfMonth() + "");
        raceDateYYYY.setText(date.getYear() + "");

    }
    // generic end of activty
    protected void endActivity() {
        try {
            raceDataSource.close();
        } catch (Exception e) {
            Log.i(LOG, "Data source close threw error");
        }
        this.finish();
    }


}
