package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.DBAdapter;


public class BoatAddForm extends Form {

    private static String LOG = "No mode: "; // create a log tag that changes based on activity mode


    // initialize instances of text and spinner widgets
    private EditText boatName;
    private EditText boatSailNum;
    private EditText boatPHRF;

    // radio group and buttons
    private RadioGroup rbgrpBoatClass;
    private RadioButton rb_single;

    //create a bundle instance to help pass vairables between activities
    Bundle extras;

    // initialize strings for use by other methods
    private String strBoatName;
    private String strBoatSailNum;
    private String strBoatPHRF;

    Cursor updateRowFromCursor; // create a cursor to hold single row info for updates

    //Create initialize the boatDataSource class
    private BoatDataSource boatDataSource;
    private String strBoatClassColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_add_form);

        boatDataSource = new BoatDataSource(this); // open writable database
        boatDataSource.open();



        //load up text and spinner instances with their corresponding widgets
//        boatClassSpn = (Spinner) findViewById(R.id.spn_BoatClass);
        boatName = (EditText) findViewById(R.id.txt_inpt_BoatName);
        boatSailNum = (EditText) findViewById(R.id.txt_inpt_SailNum);
        boatPHRF = (EditText) findViewById(R.id.txt_inpt_PHRF);

        //load up buttons with widget instances
        Button create = (Button) findViewById(R.id.btn_add_current_boat);
        Button update = (Button) findViewById(R.id.btn_update_current_boat);
        Button delete = (Button) findViewById(R.id.btn_delete_current_boat);

        // get the radio button group
        rbgrpBoatClass = (RadioGroup) findViewById(R.id.rbgrp_baf_boat_class);

        //set radio button to null initially
        rb_single = null;




        //change the button's shown based on if form is in create or edit mode.
        // instructions for ADD mode
        if (GlobalContent.modeAdd.equals(GlobalContent.getBoatFormAccessMode())) {
            create.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            LOG = "Boat ADD form";

            // instructions for EDIT mode
        } else if (GlobalContent.modeEdit.equals(GlobalContent.getBoatFormAccessMode())) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            boatDataSource.getRow(GlobalContent.getBoatRowID());
            LOG = "Boat EDIT form";

            Log.i(LOG, " Row id = " + GlobalContent.getBoatRowID());

            // if in update mode assign cursor to initialized cursor field
            updateRowFromCursor = boatDataSource.getRow(GlobalContent.getBoatRowID());

            // populate the form with data for the row selected by the user in the boatMenu

            String boatClassFromSQL = updateRowFromCursor.getString(updateRowFromCursor
                    .getColumnIndex(DBAdapter.KEY_BOAT_CLASS));

            switch (boatClassFromSQL) {
                case "Yellow":
                    rb_single = (RadioButton) findViewById(R.id.rb_baf_yellow);
                    break;
                case "Blue":
                    rb_single = (RadioButton) findViewById(R.id.rb_baf_blue);
                    break;
                case "Green":
                    rb_single = (RadioButton) findViewById(R.id.rb_baf_green);
                    break;
                case "Purple":
                    rb_single = (RadioButton) findViewById(R.id.rb_baf_purple);
                    break;
                case "_TBD_":
                    rb_single = (RadioButton) findViewById(R.id.rb_baf__TBD_);
                    break;
                default:
                    Log.i(LOG, "Boat Class edit mode switch case defaulted to no option");
                    break;
            }
            if (rb_single != null) {
                rb_single.setChecked(true);
            }

            boatName.setText(updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(
                    DBAdapter.KEY_BOAT_NAME)));
            boatSailNum.setText(updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(
                    DBAdapter.KEY_BOAT_SAIL_NUM)));
            boatPHRF.setText(updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(
                    DBAdapter.KEY_BOAT_PHRF)));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_boat_add_form, menu);
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
        endBoatActivity(); // Exit the activity
    }

    @Override
    public void onClickDelete(View view) {
        delete(); // call delete on the id provided by the onclick listener

    }

    // create a new boat entry in SQLite db
    @Override
    public void onClickCreate(View view) {
        //check if form is valid
        if (validateDataEntryFields()) {
            Boat newBoat = new Boat(); // create new boat instance

            //assign text data to the new boat instance
            newBoat.setBoatClass(strBoatClassColor);
            newBoat.setBoatName(strBoatName);
            newBoat.setBoatSailNum(strBoatSailNum);
            newBoat.setBoatPHRF(Integer.parseInt(strBoatPHRF));
            newBoat.setBoatVisible(1); // visible should always be 1 initially
            boatDataSource.create(newBoat); // push the loaded boat instance to the SQL table
            Log.i(LOG, "Validated add entry");
            endBoatActivity(); // Exit the activity
        }
    }

    @Override
    protected void setTempDataFields() {
        //get the selected text value from the radio button
        int selected = rbgrpBoatClass.getCheckedRadioButtonId();
        rb_single = (RadioButton) findViewById(selected);
        //pass values from data entry fields and spinner to String variables
        try {
            strBoatClassColor = rb_single.getText().toString();
        } catch (Exception e) {
            //will be caught by validator
        }
        strBoatName = boatName.getText().toString();
        strBoatSailNum = boatSailNum.getText().toString();
        strBoatPHRF = boatPHRF.getText().toString();
    }

    @Override
    protected boolean validateDataEntryFields(){
        setTempDataFields(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        //check for commas
        if (GlobalContent.checkForCommas(getApplicationContext(),strBoatName, strBoatSailNum)){
            return false;
        }

        Log.i(LOG, "rb Single = " + (rb_single == null) + " Text is " + strBoatClassColor);
        if (strBoatName.length() == 0
                || strBoatPHRF.length() == 0
                || strBoatSailNum.length() == 0
                || strBoatClassColor.length() == 0) {
            Toast.makeText(getApplicationContext(), "All fields are required",
                    Toast.LENGTH_LONG).show();
            return false; // not valid
        }

       return true;
    }

    @Override
    protected void update(long id) {
        Cursor cursor = boatDataSource.getRow(id); // create cursor
        if (validateDataEntryFields()) {
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                boatDataSource.update(id, strBoatClassColor, strBoatName, strBoatSailNum,
                        Integer.parseInt(strBoatPHRF));
                Log.i(LOG, "Validated UPDATE entry");

            } else {
                Toast.makeText(getApplicationContext(), "Cursor error, bad ID",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG, "CURSOR ERROR>> BAD ID");
            }
            cursor.close();
        }
    }

    @Override
    public void onClickUpdate(View view){
        update(GlobalContent.getBoatRowID());
        endBoatActivity(); // Exit the activity
    }


    @Override
    protected void delete() {
        Cursor cursor = boatDataSource.getRow(GlobalContent.getBoatRowID());
        if (validateDataEntryFields()) {

            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!!!! CANNOT UNDO A DELETION!");
                builder.setMessage("Warning:\n" +
                        "You are about to delete this boat. You cannot undo this event");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boatDataSource.psudoDelete(GlobalContent.getBoatRowID());
                        Log.i(LOG, "Validated PsudoDeleted entry");

                        dialog.dismiss();
                        endBoatActivity(); // Exit the activity
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
        }
    }

    protected void endBoatActivity() {
        try {
            boatDataSource.close();
        } catch (Exception e) {
            Log.i(LOG, "Data source close caused error");
        }
        this.finish();
    }

}
