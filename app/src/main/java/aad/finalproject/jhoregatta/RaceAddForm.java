package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class RaceAddForm extends RaceMenu {

    EditText raceName;
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
    Boolean isEmptyDate;

    String strraceName;
    String strraceDate;
    int strraceDateYYYY;
    int strraceDateDD;
    int strraceDateMM;
    String strraceDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_add_form);
        Log.i("BoatAddForm ","Form opened" );
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

    public void onClickAddRace(View view){


        raceName = (EditText) findViewById(R.id.txt_inpt_RaceTitle);
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

        //Turn text fields into numeric for validation

        if (       raceDateYYYY.getText().toString().length() !=0
                && raceDateDD.getText().toString().length() != 0
                && raceDateMM.getText().toString().length() != 0) {
            strraceDateMM = Integer.valueOf(raceDateMM.getText().toString());
            strraceDateDD = Integer.valueOf(raceDateDD.getText().toString());
            strraceDateYYYY = Integer.valueOf(raceDateYYYY.getText().toString());
            isEmptyDate = false;
        } else {
            isEmptyDate = true;
        }

        // create a string in the format of a date from the values provided
        strraceDate = strraceDateMM + "/" + strraceDateDD + "/" + strraceDateYYYY;

        strraceName = raceName.getText().toString();
        strraceDistance = raceDistance.getText().toString();

        //check to see if at least one class is selected
        if (raceClass_TBD_.isChecked() || raceClassYellow.isChecked()
                || raceClassGreen.isChecked() || raceClassPurple.isChecked()
                || raceClassBlue.isChecked() || raceClassRed.isChecked()) {
            Log.i("BoatAddForm ","Passed RaceClass checked Check" );
            // Check to make sure text fields are not blank
            if(        strraceName.length() != 0
                    && strraceDistance.length() != 0
                    && !isEmptyDate) {
                Log.i("BoatAddForm ","Passed Empty Field Check" );
                //check to see if the date is formatted properly
                if (strraceDateMM <= 12 && strraceDateDD <= 31 && strraceDateYYYY >= 1984) {
                    //TODO Place insert into SQLite database BOATS statement here
                    Toast.makeText(getApplicationContext(), "Validation True",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, SelectBoats.class);
                    startActivity(intent);
                    Log.i("BoatAddForm ", "Validated entry added");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please correct the date format",
                            Toast.LENGTH_LONG).show();
                    Log.i("BoatAddForm ","Invalid Date fields" );
                }
            } else {
//                Toast.makeText(RaceAddForm.this,"All text fields are required",
//                        Toast.LENGTH_LONG).show();
                Log.i("BoatAddForm ","Missing text fields" );
            }

        } else {
            Toast.makeText(getApplicationContext(), "You must select at least 1 class",
                    Toast.LENGTH_LONG).show();
            Log.i("BoatAddForm ","Invalid checkbox fields" );
        }
    }

    public void navigateToSelectBoats(View view){
        Intent intent = new Intent(this, SelectBoats.class);
        startActivity(intent);
    }

}
