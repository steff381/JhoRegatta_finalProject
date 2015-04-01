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


public class BoatAddForm extends BoatMenu {

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

    // initialize strings for use by other methods
    String strBoatClassSpn;
    String strBoatName;
    String strBoatSailNum;
    String strBoatPHRF;

    Cursor updateRowFromCursor; // create a cursor to hold single row info for updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_add_form);

//        boatDataSource.open();

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
        if ("CREATE".equals(BoatMenu.CHILD_ACTIVITY_TYPE_SWITCHER)) {
            create.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            LOG = "Boat ADD form";

            // instructions for EDIT mode
        } else if (BoatMenu.CHILD_ACTIVITY_TYPE_SWITCHER.equals("EDIT")) {
            create.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            boatDataSource.getRow(BoatMenu.ROW_ID);
            LOG = "Boat UPDATE form";

            // if in update mode assign cursor to initialized cursor field
            updateRowFromCursor = boatDataSource.getRow(BoatMenu.ROW_ID);

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
//        boatDataSource.close();
        finish();
    }

    // create a new boat entry in SQLite db
    public void onClickAddBoat(View view) {
        //check if form is valid
        if (validateForm()) {
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

    private void loadTextFieldsToStrings() {
        //pass values from data entry fields and spinner to String variables
        strBoatClassSpn = boatClassSpn.getSelectedItem().toString();
        strBoatName = boatName.getText().toString();
        strBoatSailNum = boatSailNum.getText().toString();
        strBoatPHRF = boatPHRF.getText().toString();
    }

    private boolean validateForm(){
        boolean isValid; // declare return vallue
        loadTextFieldsToStrings(); // repopulate text strings to field values
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


    private void UpdateBoat(long id) {
        Cursor cursor = boatDataSource.getRow(id); // create cursor
        if (validateForm()) {
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

    public void onClickUpdateBoat(View view){
        UpdateBoat(ROW_ID);
//        boatDataSource.close();
        this.finish(); // close out of the current activity. Back to boatmenu
    }

    long accessibleID; //TODO move to declarations

    private void delete(long id) {
        accessibleID = id;
        Cursor cursor = boatDataSource.getRow(id);
        if (validateForm()) {

            if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!!!! CANNOT UNDO A DELETION!");
                builder.setMessage("Warning:\n" +
                        "You are about to delete this boat. You cannot undo this event");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boatDataSource.psudoDelete(accessibleID);
                        Log.i(LOG, "Validated UPDATE entry");

                        dialog.dismiss();
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
    }

    public void onClickDelete(View view) {
//        delete(Long.parseLong(updateRowFromCursor.getString(updateRowFromCursor.getColumnIndex(
//                DBAdapter.KEY_ID))));
        delete(ROW_ID);
//        boatDataSource.close();
        finish(); // close out of the current activity. Back to boatmenu
    }
}
