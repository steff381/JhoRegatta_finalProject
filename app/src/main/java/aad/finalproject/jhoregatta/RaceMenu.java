package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class RaceMenu extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_menu);

        Log.i("Race Menu ", " onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race_menu, menu);
        Log.i("Race Menu ", " onCreate Options Menu");
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

    public void navigateToAddRaceForm(View view){
        Intent intent = new Intent(this,RaceAddForm.class);
        startActivity(intent);
        Log.i("Race Menu ", " Nav to Race Form");
    }

    public void navigateToMainMenu(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        Log.i("Race Menu ", " Nav to Main Menu");
    }
}
