package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;

import java.util.Calendar;


public class RegattaTimer extends SelectBoats {

    public Button masterStart;
    public Button masterPause;
    public TextClock masterTimeClock;
    public Calendar masterStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regatta_timer);

        masterStart = (Button) findViewById(R.id.btnMasterStart);
        masterPause = (Button) findViewById(R.id.btnMasterPause);
        masterPause.setVisibility(View.INVISIBLE);
    }


    public void startPauseSwitch() {
        masterStart.setVisibility(View.INVISIBLE);
        masterPause.setVisibility(View.VISIBLE);

    }

    public void pauseResumeSwitch() {
        masterPause.setVisibility(View.INVISIBLE);
        masterStart.setVisibility(View.VISIBLE);

    }

    public void onClickMasterStart(View view) {
        startPauseSwitch();
        Calendar cal = Calendar.getInstance();
        Log.i("LogTag: TimeTracker ", "Calendar's toString is " + cal);


    }


    public void onClickMasterPause(View view) {
        pauseResumeSwitch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regatta_timer, menu);
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
}
