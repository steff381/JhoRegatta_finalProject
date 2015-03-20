package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    public void navigateToSettings(View view){
        Intent intent = new Intent(this, MainActivity.class); //TODO: wire to settings activity
        startActivity(intent);


        Log.i("Main Menu ", " GotoSettings ");
    }

    public static boolean isNumeric(String str)
    {
        try
    {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public void onClickCancel(View view) {
        finish();
    }
}
