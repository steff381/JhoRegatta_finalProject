package aad.finalproject.jhoregatta;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Preferences extends ActionBarActivity implements TimePickerDialog.Communicator{

    private ArrayList<TextView> durationTextViews;
    private ArrayList<Long> milliseconds;


    public static final String PREFS = "TimePrefs";

    private int arrayPositionToModify = 0; // declare and set initially to 0

    //button wigets
    Button setAll;

    //create shared preferences and an editor
    SharedPreferences timePreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //get the shared prefereces file
        timePreferences = getSharedPreferences(PREFS, 0);
        editor = timePreferences.edit();

        // wire up the arrays
        ArrayList<Button> setButtons = new ArrayList<>();
        durationTextViews = new ArrayList<>();
        milliseconds = new ArrayList<>();
        final ArrayList<String> prefKey = new ArrayList<>();
        ArrayList<Integer> defaultPrefSeconds = new ArrayList<>();

        // Load up the arrays

        // Load the pref Keys
        prefKey.add("initialDelay");
        prefKey.add("classUp");
        prefKey.add("classUpPrepUp");
        prefKey.add("classUpPrepDown");
        prefKey.add("postRecallDelay");

        //Load default seconds
        defaultPrefSeconds.add(15);
        defaultPrefSeconds.add(65);
        defaultPrefSeconds.add(185);
        defaultPrefSeconds.add(65);
        defaultPrefSeconds.add(365);

        //wire up textviews
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_initial_startDelay));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_ClassUp));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_ClassAndPrepUp));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_classUpPrepDown));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_postRecallDelay));

        //wire the set buttons
        setButtons.add((Button) findViewById(R.id.btn_prefs_set_pos0));
        setButtons.add((Button) findViewById(R.id.btn_prefs_set_pos1));
        setButtons.add((Button) findViewById(R.id.btn_prefs_set_pos2));
        setButtons.add((Button) findViewById(R.id.btn_prefs_set_pos3));
        setButtons.add((Button) findViewById(R.id.btn_prefs_set_pos4));


        //set teh milliseconds to the time preference
        milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(0),
                defaultPrefSeconds.get(0))));
        milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(1),
                defaultPrefSeconds.get(1))));
        milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(2),
                defaultPrefSeconds.get(2))));
        milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(3),
                defaultPrefSeconds.get(3))));
        milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(4),
                defaultPrefSeconds.get(4))));


        //assign values to the textviews
        for (int i = 0; i < durationTextViews.size(); i++) {
            // convert to readable format and apply to text box
            durationTextViews.get(i).setText(GlobalContent.convertMillisToFormattedTime(
                    milliseconds.get(i), 1));
        }

        //set the onclick event for each button
        for (int i = 0; i < setButtons.size(); i++) {
            final int innerI = i; // set the increment to a number the inner class can see.
            setButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayPositionToModify = innerI; // set the modify position to that of the button
                    Bundle bundle = new Bundle();  // make a new bundle
                    // check if the millisecond arry is  0
                    if (milliseconds.get(innerI) != 0) {
                        // set the bundle to the milliseconds used in elapsed time format
                        bundle.putString("Value1", GlobalContent
                                .convertMillisToFormattedTime(milliseconds.get(innerI), 0));
                    } else { // handle if is 0
                        bundle.putString("Value1", "00:00:00");
                    }

                    //set up the time picker fragment and set the bundle
                    FragmentManager manager = getFragmentManager();
                    TimePickerDialog picker = new TimePickerDialog();
                    picker.setArguments(bundle);

                    //open the time picker
                    picker.show(manager, "HH:MM:SS");
                }
            });
        }


        //wire the set all button
        setAll = (Button) findViewById(R.id.btn_prefs_set_all);

        // when user clicks save
        setAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // grab each milli value and assign it to the appropriate preference
                for (int i = 0; i < prefKey.size(); i++) {
                    editor.putInt(prefKey.get(i), (int) (milliseconds.get(i) / 1000));
                    Log.i("Prefs", "Pref: " + prefKey.get(i) + " is " + GlobalContent
                            .convertMillisToFormattedTime(milliseconds.get(i), 1));
                }
                editor.apply(); //commit the changes
                Toast.makeText(v.getContext(), " Values Assigned", Toast.LENGTH_LONG).show();
                finish();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
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
    public void onDialogMessage(long message) {
        //set the new milliseconds for the chosen preference
        milliseconds.set(arrayPositionToModify, message);
        //format the milliseconds to a duration.
        String duration = GlobalContent.convertMillisToFormattedTime(message, 1);
        // set the text box to the duration specified
        durationTextViews.get(arrayPositionToModify).setText(duration);
    }
}
