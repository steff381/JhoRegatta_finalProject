package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aad.finalproject.db.AndroidDatabaseManager;


public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void navigateToBoatMenu(View view){
        Intent intent = new Intent(this,BoatMenu.class);
        startActivity(intent);

        Log.i("Main Menu ", " GotoBoat ");
    }


    public void navigateToRaceMenu(View view){
        Intent intent = new Intent(this, RaceMenu.class);
        startActivity(intent);
        Log.i("Main Menu ", " GotoRace ");
    }


    public void navigateToPreferences(View view){
//        Intent intent = new Intent(this, SelectBoats.class); //TODO: wire to settings activity
//        startActivity(intent);


        Log.i("Main Menu ", " goto Preferences ");
    }

    public void onActionClickDDMS(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

}
