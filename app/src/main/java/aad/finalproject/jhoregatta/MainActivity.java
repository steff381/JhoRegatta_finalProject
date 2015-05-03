package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import aad.finalproject.db.AndroidDatabaseManager;


public class MainActivity extends ActionBarActivity {

    // set up buttons for click assignment
    Button testButton;
    Button calculator;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // wire buttons
        testButton = (Button)findViewById(R.id.btn_ma_test);
        calculator = (Button) findViewById(R.id.btn_nav_Calculator);


        // assign click listeners
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DistanceCalculator.class);
                startActivity(intent);
            }
        });

        // assign click listeners
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_ddms:
                onActionClickDDMS();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, Preferences.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    //go to the boats menu
    public void navigateToBoatMenu(View view){
        Intent intent = new Intent(this,BoatMenu.class);
        startActivity(intent);

        Log.i("Main Menu ", " GotoBoat ");
    }

    //go to the race menu

    public void navigateToRaceMenu(View view){
        Intent intent = new Intent(this, RaceMenu.class);
        startActivity(intent);
        Log.i("Main Menu ", " GotoRace ");
    }

    // send the user to the preferences screen
    public void navigateToPreferences(View view){
        Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);



        Log.i("Main Menu ", " goto Preferences ");
    }

    // TODO get rid of DDMS
    public void onActionClickDDMS(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

}
