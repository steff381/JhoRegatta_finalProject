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
import android.widget.Spinner;
import android.widget.Toast;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.DBAdapter;


public class BoatAddForm extends Form {

    private static String LOG; // create a log tag that changes based on activity mode


    // initialize instances of text and spinner widgets
    Spinner boatClassSpn;
    EditText boatName;
    EditText boatSailNum;
    EditText boatPHRF;

    // initialize button widgets
    Button create;
    Button update;
    Button delete;

    //create a bundle instance to help pass vairables between activities
    Bundle extras;

    // initialize strings for use by other methods
    String strBoatClassSpn;
    String strBoatName;
    String strBoatSailNum;
    String strBoatPHRF;

    Cursor updateRowFromCursor; // create a cursor to hold single row info for updates

    //Create initialize the boatDataSource class
    BoatDataSource boatDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_add_form);

        extras = getIntent().getExtras();

        boatDataSource = new BoatDataSource(this);
        boatDataSource.open();

        //load up text and spinner instances with their corresponding widgets
        boatClassSpn = (Spinner) findViewById(R.id.spn_BoatClass);
        boatName = (EditText) findViewById(R.id.txt_inpt_BoatName);
        boatSailNum = (EditText) findViewById(R.id.txt_inpt_SailNum);
        boatPHRF = (EditText) findViewById(R.id.txt_inpt_PHRF);

        //load up buttons with widget instances
        create = (Button) findViewById(R.id.btn_add_current_boat);
        update = (Button) findViewById(R.id.btn_update_current_boat);
        delete = (Button) findViewById(R.id.btn_delete_current_boat);

        //change the button's shown based on if form is in create or edit mode.
        // instructions for CREATE mode
        if ("CREATE".equals(extras.getString(BoatMenu.ACCESS_METHOD_KEY))) {
            create.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            LOG = "Boat ADD form";

            // instructions for EDIT mode
        } else if (extras.getString(BoatMenu.ACCESS_METHOD_KEY).equals("EDIT")) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            boatDataSource.getRow(getROW_ID());
            LOG = "Boat UPDATE form";

            Log.i(LOG, " Row id = " + getROW_ID());

            // if in update mode assign cursor to initialized cursor field
            updateRowFromCursor = boatDataSource.getRow(getROW_ID());

            // populate the form with data for the row selected by the user in the boatMenu
            boatClassSpn.setSelection(BoatDataSource.getClassColorPosition(
                    updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(
                            DBAdapter.KEY_BOAT_CLASS))));
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
        boatDataSource.close();
        finish();
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
            newBoat.setBoatClass(strBoatClassSpn);
            newBoat.setBoatName(strBoatName);
            newBoat.setBoatSailNum(strBoatSailNum);
            newBoat.setBoatPHRF(Integer.parseInt(strBoatPHRF));
            boatDataSource.create(newBoat); // push the loaded boat instance to the SQL table
            Log.i(LOG, "Validated add entry");
//            boatDataSource.close();
            this.finish(); // close out activity
        }
    }

    @Override
    protected void setTempDataFields() {
        //pass values from data entry fields and spinner to String variables
        strBoatClassSpn = boatClassSpn.getSelectedItem().toString();
        strBoatName = boatName.getText().toString();
        strBoatSailNum = boatSailNum.getText().toString();
        strBoatPHRF = boatPHRF.getText().toString();
    }

    @Override
    protected boolean validateDataEntryFields(){
        boolean isValid; // declare return vallue
        setTempDataFields(); // repopulate text strings to field values
        // Ensure fields are not null or class a class has been selected prior to data input
        if (strBoatName.length() != 0
                && strBoatPHRF.length() != 0
                && strBoatSailNum.length() != 0) {
            if (!strBoatClassSpn.equals("Please Select a Class")) {
                isValid = true; // change to valid
            } else {
                Toast.makeText(getApplicationContext(), "Please select a Boat Class",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG, "No class selected");
                isValid = false; // not valid
            }
        } else {
            Toast.makeText(BoatAddForm.this, "All fields are required", Toast.LENGTH_LONG).show();
            Log.i(LOG, "Missing fields");
            isValid = false; // not valid
        }
       return isValid;
    }

    @Override
    protected void update(long id) {
        Cursor cursor = boatDataSource.getRow(id); // create cursor
        if (validateDataEntryFields()) {
            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                boatDataSource.update(id, strBoatClassSpn, strBoatName, strBoatSailNum,
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
        update(getROW_ID());
        this.finish(); // close out of the current activity. Back to boatmenu
    }


    @Override
    protected void delete() {
        Cursor cursor = boatDataSource.getRow(getROW_ID());
        if (validateDataEntryFields()) {

            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!!!! CANNOT UNDO A DELETION!");
                builder.setMessage("Warning:\n" +
                        "You are about to delete this boat. You cannot undo this event");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boatDataSource.psudoDelete(getROW_ID());
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


}
