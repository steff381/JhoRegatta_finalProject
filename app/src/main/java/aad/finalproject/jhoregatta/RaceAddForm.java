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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.Race;
import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;


public class RaceAddForm extends Form {

    //logging header
    private String LOG = this.getClass().getSimpleName() + " - MODE:";

    //bundle key used for communications
    public static final String DISTANCE_KEY = "setDistanceOnly";

    public static boolean isBoatClassUpdate;  // if the update is boat update

    // create empty instances for widgets
    private EditText raceTitle;
    private EditText raceDateMM;
    private EditText raceDateDD;
    private EditText raceDateYYYY;

    // linear layout
    LinearLayout linlayResultsButtons;


    //checkboxes for class selection
    private CheckBox raceClassless;
    private CheckBox raceClassBlue;
    private CheckBox raceClassPurple;
    private CheckBox raceClassYellow;
    private CheckBox raceClassGreen;
    private CheckBox raceClass_TBD_;


    //array list of checkboxes
    private ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();

    //array list of boat names in text boxes
    private ArrayList<TextView> boatClassHolderArrayList = new ArrayList<>();

    // create create field data containers
    private String strraceTitle;
    private String strraceDate;
    private int intRaceClassless;
    private int intRaceClassBlue;
    private int intRaceClassPurple;
    private int intRaceClassYellow;
    private int intRaceClassGreen;
    private int intRaceClass_TBD_;

    // initialize the race data source object
    RaceDataSource raceDataSource; // create instance of the race data source
    ResultDataSource resultDataSource; // create instance of the results data source.
    Cursor updateRowFromCursor; //create a cursor to hold single row of data for updates/edits

    // initialize button widgets
    private Button create;
    private Button update;
    private Button delete;
    private Button setDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_add_form);
        Log.i(LOG, "Form opened");


        //open a Writable instance of the RaceDataSource
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();
        //open the results data source
        resultDataSource = new ResultDataSource(this);
        resultDataSource.open();

        isBoatClassUpdate = false; // opening up the menu for the first time makes false

        BoatStartingListClass.BOAT_CLASS_START_ARRAY.clear(); // empty the array of all data

        wireWidgetsAndLoadArrays();// set up the instances with data


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
        Button modifyResults = (Button) findViewById(R.id.btn_raf_modify_results);
        setDistance = (Button) findViewById(R.id.btn_setDistance);
//        Button emailResults = (Button) findViewById(R.id.btn_raf_email_results);

        //wire linear layout
        linlayResultsButtons = (LinearLayout) findViewById(R.id.linlay_raf_results_buttons);

        //assign an onclickk listener to the get results button
        modifyResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalContent.setResultsFormAccessMode(true); //set the access mode variable.
                Intent intent = new Intent(v.getContext(), ResultsMenu.class);
                startActivity(intent);

            }
        });


        setDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cleare the boat class array
                BoatStartingListClass.BOAT_CLASS_START_ARRAY.clear();

                //load the array up with the selected boats.
                if (raceClassless.isChecked()) {
                    //add the class to the class array
                    BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                            BoatClass(raceClassless.getText().toString().trim()));
                } else {
                    if (raceClassBlue.isChecked()) {
                        //add the class to the class array
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                                BoatClass(raceClassBlue.getText().toString().trim()));
                    }
                    if (raceClassPurple.isChecked()) {
                        //add the class to the class array
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                                BoatClass(raceClassPurple.getText().toString().trim()));
                    }
                    if (raceClassYellow.isChecked()) {
                        //add the class to the class array
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                                BoatClass(raceClassYellow.getText().toString().trim()));
                    }
                    if (raceClassGreen.isChecked()) {
                        //add the class to the class array
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                                BoatClass(raceClassGreen.getText().toString().trim()));
                    }
                    if (raceClass_TBD_.isChecked()) {
                        //add the class to the class array
                        BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                                BoatClass(raceClass_TBD_.getText().toString().trim()));
                    }
                }

                if (BoatStartingListClass.BOAT_CLASS_START_ARRAY.size() > 0) {
                    // open the select class distance
                    Intent intent = new Intent(getApplicationContext(), SelectClassDistance.class);
                    Bundle b = new Bundle();
                    b.putBoolean(DISTANCE_KEY, true);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });

        // set the buttons for edit mode.
        if (GlobalContent.modeEdit.equals(GlobalContent.getRaceFormAccessMode())) {

            //if EDIT mode is active initialize cursor fields
            updateRowFromCursor = raceDataSource.getRow(GlobalContent.getRaceRowID());

            //show the proper buttons
            create.setVisibility(View.GONE);
            linlayResultsButtons.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);

            checkForDistance();


            //make sure the editable fields and boxes are disabled
            setEnabledOfEditables(false);

//            //make the starting order text views invisible during edit mode.
            findViewById(R.id.linlay_raf_startOrder).setVisibility(View.INVISIBLE);

            LOG += " Race EDIT form"; // change the edit mode status for log cat

            //populate the edit form fields with data from selected race
            raceTitle.setText(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_NAME)));
            // set the text in the date fields from a split of the text version of the date
            String[] dateTemp = updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_DATE)).split("/");
            raceDateMM.setText(dateTemp[0]);
            raceDateDD.setText(dateTemp[1]);
            raceDateYYYY.setText(dateTemp[2]);

            //check if the variable in SQLite is 1 or 0, if one set the checkbox to checked
            if (Integer.parseInt(updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_RACE_CLASS_RED))) == 1) {
                raceClassless.setChecked(true);
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
            linlayResultsButtons.setVisibility(View.GONE);
            //make the starting order text views visible during add mode.
//            findViewById(R.id.linlay_class_start_order_textviews).setVisibility(View.VISIBLE);

            Log.i(LOG, "create mode buttons activated");
        } else {
            Log.i(LOG, "NO RACE CHILD ACIVITY TYPE ERROR!!!!");

        }
    }

    private void checkForDistance() {

        //get a list of all the results that match the curret race id.
        List<Result> results = resultDataSource.getAllResults(DBAdapter.KEY_RACE_ID + "= "
                + GlobalContent.getRaceRowID(), null, null);

        //tally up the distance
        double distance = 0;
        for (Result r : results) {
            Log.i(LOG, " class: " + r.getBoatClass() + " boat: " + r.getBoatName() + " distance: " + r.getRaceDistance());
            distance += r.getRaceDistance();
        }
        Log.i(LOG, "Distance = " + distance);
        //if the distance is 0 then the distance wasn't set.
        //show or hide the "set distance" button depending on the situation.
        if (distance == 0) {
            setDistance.setVisibility(View.VISIBLE);
        } else {
            setDistance.setVisibility(View.GONE);
        }
    }


    private void wireWidgetsAndLoadArrays() {
        //load up text and other fields into widgets
        raceTitle = (EditText) findViewById(R.id.txt_inpt_RaceTitle);
        raceClassless = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassless);
        raceClassBlue = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassBlue);
        raceClassPurple = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassPurple);
        raceClassGreen = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassGreen);
        raceClassYellow = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClassYellow);
        raceClass_TBD_ = (CheckBox) findViewById(R.id.ckbx_inpt_RaceClass_TBD_);

        // add each checkbox to the check box array
        checkBoxArrayList.add(raceClassless);
        checkBoxArrayList.add(raceClassBlue);
        checkBoxArrayList.add(raceClassPurple);
        checkBoxArrayList.add(raceClassGreen);
        checkBoxArrayList.add(raceClassYellow);
        checkBoxArrayList.add(raceClass_TBD_);

        // wire textviews to textview instances
        TextView raceClass1Holder = (TextView) findViewById(R.id.txt_class_1_holder);
        TextView raceClass2Holder = (TextView) findViewById(R.id.txt_class_2_holder);
        TextView raceClass3Holder = (TextView) findViewById(R.id.txt_class_3_holder);
        TextView raceClass4Holder = (TextView) findViewById(R.id.txt_class_4_holder);
        TextView raceClass5Holder = (TextView) findViewById(R.id.txt_class_5_holder);
        TextView raceClass6Holder = (TextView) findViewById(R.id.txt_class_6_holder);

        //build array list of raceclass holder textviews
        boatClassHolderArrayList.add(raceClass1Holder);
        boatClassHolderArrayList.add(raceClass2Holder);
        boatClassHolderArrayList.add(raceClass3Holder);
        boatClassHolderArrayList.add(raceClass4Holder);
        boatClassHolderArrayList.add(raceClass5Holder);
        boatClassHolderArrayList.add(raceClass6Holder);
    }


    // create method to assign checkbox listeners to the class check boxes
    public void setCheckboxListeners() {

        for (int i = 1; i < checkBoxArrayList.size(); i++) {
            String boatCheckBoxName = checkBoxArrayList.get(i).getText().toString().trim();
            final String BCBN = boatCheckBoxName;
            Log.i(LOG, " Adding Listener to " + boatCheckBoxName);
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
        }

        //set a special re assignment for the first class
        checkBoxArrayList.get(0).setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //clear the boat class array of all other boats
                    BoatStartingListClass.BOAT_CLASS_START_ARRAY.clear();
                    //if the classless cb is check then uncheck al other boxes and disable them
                    for (int i = 1; i < checkBoxArrayList.size(); i++) {
                        checkBoxArrayList.get(i).setChecked(false);
                        checkBoxArrayList.get(i).setEnabled(false);
                    }
                    //add the classless class to the class array
                    BoatStartingListClass.BOAT_CLASS_START_ARRAY.add(new
                            BoatClass(checkBoxArrayList.get(0).getText().toString().trim()));
                } else {
                    //clear the boat class array of all other boats
                    BoatStartingListClass.BOAT_CLASS_START_ARRAY.clear();
                    for (int i = 1; i < checkBoxArrayList.size(); i++) {
                        checkBoxArrayList.get(i).setEnabled(true);
                    }
                }
                bindTextViewWithClassColors(); // update the class order text views.
            }
        });

        //set the listener for the first check box
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
        endActivity(); /// just end the actiity
    }

    @Override
    public void onClickCreate(View view) {
        setTempDataFields(); // load data from text fields
        //check if fields are valid
        if (validateDataEntryFields()) {

            Race newRace = new Race(); // create a new race instance

            // load new race instance with data from validated fields
            newRace.setName(strraceTitle);
            newRace.setDate(strraceDate);
            newRace.setClsRed(intRaceClassless);
            newRace.setClsBlue(intRaceClassBlue);
            newRace.setClsPurple(intRaceClassPurple);
            newRace.setClsYellow(intRaceClassYellow);
            newRace.setClsGreen(intRaceClassGreen);
            newRace.setCls_TBD_(intRaceClass_TBD_);
            newRace.hasDistance = false; // created race has no distance. set in next activity
            raceDataSource.create(newRace); //assign the race class to the db

            // grab the newly created RaceID and set it to the global content for use by
            // the results table
            long id = raceDataSource.getLastInsertedRowID();
            GlobalContent.setRaceRowID(id); // set the id of global content
            GlobalContent.setActiveRace(newRace, id); // set the active race to the new race
            Log.i(LOG, "Last inserted Id: " + id + " || Race name: " + newRace.getName());


            isBoatClassUpdate = true; // after the create, update goes to select boats

            appendWhereClause(); // create a where clause for select boats

            // open the select class distance
            Intent intent = new Intent(this, SelectClassDistance.class);
            Bundle b = new Bundle();
            b.putBoolean(DISTANCE_KEY, false);
            intent.putExtras(b);
            startActivity(intent);

            // remove the ADD functions and replace with "Update" functions
            create.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            Log.i("BoatAddForm ", "Validated entry added");
        }

    }


    @Override
    public void onClickUpdate(View view) {
        update(GlobalContent.getRaceRowID()); // update the data in the race sql table

    }

    @Override
    protected void update(long id) {
        Cursor cursor = raceDataSource.getRow(id); // create cursor
        setTempDataFields();
        if (validateDataEntryFields()) {
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                raceDataSource.update(id,
                        strraceTitle,
                        strraceDate,
                        intRaceClassBlue,
                        intRaceClassGreen,
                        intRaceClassPurple,
                        intRaceClassYellow,
                        intRaceClassless,
                        intRaceClass_TBD_);
                Log.i(LOG, "Validated UPDATE entry");
                // open the select boats list
                appendWhereClause(); // create a where clause for select boats
                Intent intent = new Intent(this, SelectClassDistance.class);
                Bundle b = new Bundle();
                b.putBoolean(DISTANCE_KEY, false);
                intent.putExtras(b);
                startActivity(intent);
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
            Log.i(LOG, "CURSOR ERROR>> BAD ID");
        }

        cursor.close();
    }

    @Override
    protected boolean validateDataEntryFields() {
        setTempDataFields(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        if (raceTitle.getText().toString().equals("") ||
                raceDateMM.getText().toString().equals("") ||
                raceDateDD.getText().toString().equals("") ||
                raceDateYYYY.getText().toString().equals("")) {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        //make sure at least 1 class is selected
        if (
                (!raceClassless.isChecked() &&
                        !raceClassBlue.isChecked() &&
                        !raceClassPurple.isChecked() &&
                        !raceClassYellow.isChecked() &&
                        !raceClassGreen.isChecked() &&
                        !raceClass_TBD_.isChecked()
                )) {
            Toast.makeText(this, "You must select at least 1 boat class",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // ensure proper formatting on the day month and year edit Texts
        if (Integer.parseInt(raceDateMM.getText().toString()) > 0
                && Integer.parseInt(raceDateMM.getText().toString()) <= 12
                && Integer.parseInt(raceDateDD.getText().toString()) > 0
                && Integer.parseInt(raceDateDD.getText().toString()) <= 31
                && Integer.parseInt(raceDateYYYY.getText().toString()) > 999
                && Integer.parseInt(raceDateYYYY.getText().toString()) < 2081) {
            //then all is well and good.
        } else {
            Toast.makeText(this, " Check your date format. \n mm / dd / yyyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        //check if any commas are in fields used when writing to the sql CSV file
        return !GlobalContent.checkForCommas(this, raceTitle.getText().toString());
    }

    @Override
    protected void setTempDataFields() {
        //pass values from data entry fields and spinner to String variables
        try {
            strraceTitle = raceTitle.getText().toString();
            int strraceDateMM = Integer.parseInt(raceDateMM.getText().toString());
            int strraceDateDD = Integer.parseInt(raceDateDD.getText().toString());
            int strraceDateYYYY = Integer.parseInt(raceDateYYYY.getText().toString());
            strraceDate = strraceDateMM + "/" + strraceDateDD + "/" + strraceDateYYYY;
            intRaceClassless = (raceClassless.isChecked()) ? 1 : 0;
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
    public void onClickToday(View view) {
        DateTime date = new DateTime();
        date.toLocalDate();
        raceDateMM.setText(date.getMonthOfYear() + "");
        raceDateDD.setText(date.getDayOfMonth() + "");
        raceDateYYYY.setText(date.getYear() + "");

    }

    // generic end of activty
    protected void endActivity() {

        if (resultDataSource != null) {
            resultDataSource.close();
        }
        if (raceDataSource != null) {
            raceDataSource.close();
        }
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBoatClassUpdate) {
            // if accessing form from distance then show update button otherwise do nothing
            update.setVisibility(View.VISIBLE);
            // hide the results buttons too
            linlayResultsButtons.setVisibility(View.GONE);
        }
        checkForDistance(); // check to see if the distance was entered into the database.
        resultDataSource.open();
        raceDataSource.open();

    }


    //either disable or enable the editable fields and checkboxes in the form.
    private void setEnabledOfEditables(boolean isEnabled) {
        //set all editable fields as either enabled or disabled.
        raceTitle.setEnabled(isEnabled);
        raceDateMM.setEnabled(isEnabled);
        raceDateDD.setEnabled(isEnabled);
        raceDateYYYY.setEnabled(isEnabled);

        raceClassless.setEnabled(isEnabled);
        raceClassBlue.setEnabled(isEnabled);
        raceClassPurple.setEnabled(isEnabled);
        raceClassYellow.setEnabled(isEnabled);
        raceClassGreen.setEnabled(isEnabled);
        raceClass_TBD_.setEnabled(isEnabled);

        findViewById(R.id.btnToday).setEnabled(isEnabled);

    }

    // build a sql query that includes only the classes chosen by the user in the prvious form.
    private void appendWhereClause() {
        for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
            Log.i(LOG, b.getBoatColor());
        }
        //check if the boat class in the array is the "Classless" class
        if (!BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(0).getBoatColor().equals("Classless")) {
            StringBuilder sb = new StringBuilder();
            sb.append(DBAdapter.KEY_BOAT_VISIBLE + " = 1"); // grab the original statement and append
            // it to the new one
            sb.append(" AND " + DBAdapter.KEY_BOAT_CLASS + " in(");
            for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                sb.append("\"" + bc.getBoatColor() + "\"");
                sb.append(", ");
            }
            String substring = sb.substring(0, sb.length() - 2); // chop off the last comma.
            substring += ")"; // include the last brace
            GlobalContent.globalWhere = substring; //assign the where clause to the global where
            Log.i(LOG, substring);
        } else {
            //if the only boat class in the list is the classless class choose all boats.
            GlobalContent.globalWhere = DBAdapter.KEY_BOAT_VISIBLE + " = 1";
        }
    }
}
