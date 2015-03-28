package aad.finalproject.jhoregatta;

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


public class BoatAddForm extends BoatMenu {

    private static String LOG;

    Spinner boatClassSpn;
    EditText boatName;
    EditText boatSailNum;
    EditText boatPHRF;

    Button createBoat;
    Button updateBoat;
    Button deleteBoat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_add_form);


        createBoat = (Button) findViewById(R.id.btn_add_current_boat);
        updateBoat = (Button) findViewById(R.id.btn_update_current_boat);
        deleteBoat = (Button) findViewById(R.id.btn_delete_current_boat);

        if (childActivityTypeSwitcher.equals("CREATE")) {
            createBoat.setVisibility(View.VISIBLE);
            updateBoat.setVisibility(View.GONE);
            deleteBoat.setVisibility(View.GONE);
            LOG = "Boat ADD form";
        } else if (childActivityTypeSwitcher.equals("EDIT")) {
            createBoat.setVisibility(View.GONE);
            updateBoat.setVisibility(View.VISIBLE);
            deleteBoat.setVisibility(View.VISIBLE);
            LOG = "Boat UPDATE form";

        }

        boatClassSpn = (Spinner) findViewById(R.id.spn_BoatClass);
        boatName = (EditText) findViewById(R.id.txt_inpt_BoatName);
        boatSailNum = (EditText) findViewById(R.id.txt_inpt_SailNum);
        boatPHRF = (EditText) findViewById(R.id.txt_inpt_PHRF);

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
        finish();
    }

    public void onClickAddBoat(View view){

        String strBoatClassSpn = boatClassSpn.getSelectedItem().toString();
        String strBoatName = boatName.getText().toString();
        String strBoatSailNum = boatSailNum.getText().toString();
        String strBoatPHRF = boatPHRF.getText().toString();

        if (strBoatName.length() != 0
                && strBoatPHRF.length() != 0
                && strBoatSailNum.length() != 0) {
            if(!strBoatClassSpn.equals("Please Select a Class")){

                Boat newBoat = new Boat();
                newBoat.setBoatClass(strBoatClassSpn);
                newBoat.setBoatName(strBoatName);
                newBoat.setBoatSailNum(strBoatSailNum);
                newBoat.setBoatPHRF(Integer.parseInt(strBoatPHRF));
                boatDataSource.create(newBoat);

                Log.i(LOG,"Validated add entry" );
                this.finish();
            } else {
                Toast.makeText(getApplicationContext(),"Please select a Boat Class",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG,"No class selected" );
            }
        } else {
            Toast.makeText(BoatAddForm.this,"All fields are required", Toast.LENGTH_LONG).show();
            Log.i(LOG,"Missing fields" );
        }
    }

    private void UpdateBoat(long id) {
        Cursor cursor = boatDataSource.getRow(id); // create cursor

        //pass values from data entry fields and spinner to String variables
        String strBoatClassSpn = boatClassSpn.getSelectedItem().toString();
        String strBoatName = boatName.getText().toString();
        String strBoatSailNum = boatSailNum.getText().toString();
        String strBoatPHRF = boatPHRF.getText().toString();

        // Ensure fields are not null or class a class has been selected prior to data input
        if (strBoatName.length() != 0
                && strBoatPHRF.length() != 0
                && strBoatSailNum.length() != 0) {
            if (!strBoatClassSpn.equals("Please Select a Class")) {
                if (cursor.moveToFirst()) { // checks if the id supplied leads to actual entry
                    boatDataSource.update(id, strBoatClassSpn, strBoatName, strBoatSailNum,
                            Integer.parseInt(strBoatPHRF));
                    Log.i(LOG, "Validated UPDATE entry");
                    this.finish(); // close out of the current activity. Back to boatmenu
                } else {
                    Toast.makeText(getApplicationContext(), "Cursor error, bad ID",
                            Toast.LENGTH_LONG).show();
                    Log.i(LOG, "CURSOR ERROR>> BAD ID");
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please select a Boat Class",
                        Toast.LENGTH_LONG).show();
                Log.i(LOG, "No class selected");
            }
        } else {
            Toast.makeText(BoatAddForm.this, "All fields are required", Toast.LENGTH_LONG).show();
            Log.i(LOG, "Missing fields");

        }
        cursor.close();
    }

    public void onClickUpdateBoat(View view){
        UpdateBoat(rowID);
    }

}
