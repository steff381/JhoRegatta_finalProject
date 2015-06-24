package aad.finalproject.jhoregatta;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Preferences extends MainActivity implements TimePickerDialog.Communicator {

    //array lists that handle the duration textviews and the time they hold
    private ArrayList<TextView> durationTextViews;
    private ArrayList<Long> milliseconds;

    //Create a single string to call the preferences
    public static final String PREFS = "TimePrefs";

    //this is the item in the seconds and durationTextViews arrays that will be changed.
    private int arrayPositionToModify = 0; // declare and set initially to 0

    //button wigets
    private Button setAll;
    private Button cancelChanges;


    //create shared preferences and an editor
    private SharedPreferences timePreferences;
    private SharedPreferences.Editor editor;

    // Load the pref Keys into array list
    public static final String INITIAL_DELAY = "initialDelay";
    public static final String CLASS_UP = "classUp";
    public static final String CLASS_UP_PREP_UP = "classUpPrepUp";
    public static final String CLASS_UP_PREP_DOWN = "classUpPrepDown";
    public static final String POST_RECALL_DELAY = "postRecallDelay";
    public static final String RACE_TIME_LIMIT = "raceTimeLimit";
    public static final String FINISH_TIME_LIMIT = "finishTimeLimit";
    public static final String DIMMER_DELAY = "dimmerDelay";

    // Load the pref Keys into array list
    public static final String NAME_INITIAL_DELAY = "Initial Delay";
    public static final String NAME_CLASS_UP = "Class Flag Up";
    public static final String NAME_CLASS_UP_PREP_UP = "Class and Prep Up";
    public static final String NAME_CLASS_UP_PREP_DOWN = "Class Up Prep Down";
    public static final String NAME_POST_RECALL_DELAY = "Post-recall Delay";
    public static final String NAME_RACE_TIME_LIMIT = "Race Time Limit";
    public static final String NAME_FINISH_TIME_LIMIT = "Post First Finish Limit";
    public static final String NAME_DIMMER_DELAY = "Dimmer Delay";

    class Preference {
        public int min, max, seconds;
        long milliseconds;
        TextView textView;
        String prefKey, prefName;

        public Preference(TextView textView, String prefKey, String prefName, int defaultTimeSeconds, int max, int min) {
            this.min = min;
            this.max = max;
            this.seconds = defaultTimeSeconds;
            this.textView = textView;
            this.prefKey = prefKey;
            this.prefName = prefName;
        }
    }

    class PreferenceRestrictions {
        public int min, max;

        public PreferenceRestrictions(int max, int min) {
            this.min = min;
            this.max = max;

        }
    }

//    private ArrayList<Preference> setPrefList() {
//
//        int flagMinSeconds = 1;
//        int maxTimeSeconds = 356400;
//        ArrayList<Preference> preferences = new ArrayList<>();
//
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_initial_startDelay),
//                INITIAL_DELAY, NAME_INITIAL_DELAY, 0, maxTimeSeconds, 0));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_ClassUp),
//                CLASS_UP, NAME_CLASS_UP, 60, maxTimeSeconds, flagMinSeconds));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_ClassAndPrepUp),
//                CLASS_UP_PREP_UP, NAME_CLASS_UP_PREP_UP, 180, maxTimeSeconds, flagMinSeconds));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_classUpPrepDown),
//                CLASS_UP_PREP_DOWN, NAME_CLASS_UP_PREP_DOWN, 60, maxTimeSeconds, flagMinSeconds));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_postRecallDelay),
//                POST_RECALL_DELAY, NAME_POST_RECALL_DELAY, 0, maxTimeSeconds, 0));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_race_time_limit),
//                RACE_TIME_LIMIT, NAME_RACE_TIME_LIMIT, 7200, maxTimeSeconds, 1800));
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_finish_time_limit),
//                FINISH_TIME_LIMIT, NAME_FINISH_TIME_LIMIT,1800,);
//        preferences.add(new Preference((TextView) findViewById(R.id.txt_prefs_dimmer_delay));
//    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // hide the return/up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //get the shared prefereces file
        timePreferences = getSharedPreferences(PREFS, 0);
        editor = timePreferences.edit();


        // wire up the arrays
        ArrayList<Button> setButtons = new ArrayList<>();
        durationTextViews = new ArrayList<>();
        milliseconds = new ArrayList<>();
        final ArrayList<String> prefKey = new ArrayList<>();
        ArrayList<Integer> defaultPrefSeconds = new ArrayList<>();
        final ArrayList<PreferenceRestrictions> preferenceRestrictionsArrayList = new ArrayList<>();


        // Load up the arrays

        // Load the pref Keys into array list
        prefKey.add(INITIAL_DELAY);
        prefKey.add(CLASS_UP);
        prefKey.add(CLASS_UP_PREP_UP);
        prefKey.add(CLASS_UP_PREP_DOWN);
        prefKey.add(POST_RECALL_DELAY);
        prefKey.add(RACE_TIME_LIMIT);
        prefKey.add(FINISH_TIME_LIMIT);
        prefKey.add(DIMMER_DELAY);

        //Load default times in seconds
        defaultPrefSeconds.add(0);
        defaultPrefSeconds.add(60);
        defaultPrefSeconds.add(180);
        defaultPrefSeconds.add(60);
        defaultPrefSeconds.add(0);
        defaultPrefSeconds.add(7200); // 2 hours
        defaultPrefSeconds.add(1800); // 30 mins
        defaultPrefSeconds.add(90); // 1.5 mins

        int flagMinSeconds = 1;
        int maxTimeSeconds = 356400;
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, 0));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, flagMinSeconds));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, flagMinSeconds));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, flagMinSeconds));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, 0));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(maxTimeSeconds, timePreferences
                .getInt(FINISH_TIME_LIMIT, defaultPrefSeconds.get(6))));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(timePreferences
                .getInt(RACE_TIME_LIMIT, defaultPrefSeconds.get(5)), 1));
        preferenceRestrictionsArrayList.add(new PreferenceRestrictions(300, 30));

        //wire up textviews
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_initial_startDelay));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_ClassUp));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_ClassAndPrepUp));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_classUpPrepDown));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_postRecallDelay));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_race_time_limit));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_finish_time_limit));
        durationTextViews.add((TextView) findViewById(R.id.txt_prefs_dimmer_delay));

        //set teh seconds to the time preference
        for (int i = 0; i < defaultPrefSeconds.size(); i++) {
            milliseconds.add((long) (1000 * timePreferences.getInt(prefKey.get(i),
                    defaultPrefSeconds.get(i))));
        }

        //assign values to the textviews
        for (int i = 0; i < durationTextViews.size(); i++) {
            // convert to readable format and apply to text box
            durationTextViews.get(i).setText(GlobalContent.convertMillisToFormattedTime(
                    milliseconds.get(i), 1));
        }

        //set the onclick event for each text view
        for (int i = 0; i < durationTextViews.size(); i++) {
            final int innerI = i; // set the increment to a number the inner class can see.
            durationTextViews.get(i).setClickable(true);
            durationTextViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayPositionToModify = innerI; // set the modify position to that of the button
                    Bundle bundle = new Bundle();  // make a new bundle
                    // check if the millisecond arry is  0
                    if (milliseconds.get(innerI) != 0) {
                        // set the bundle to the seconds used in elapsed time format
                        bundle.putString("Value1", GlobalContent
                                .convertMillisToFormattedTime(milliseconds.get(innerI), 0));
                    } else { // handle if is 0
                        bundle.putString("Value1", "00:00:00");
                    }
                    bundle.putString("prefKey", prefKey.get(innerI));
                    bundle.putInt("min", preferenceRestrictionsArrayList.get(innerI).min);
                    bundle.putInt("max", preferenceRestrictionsArrayList.get(innerI).max);
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
                if (true) {
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

            }
        });

        //wire the cancel button
        cancelChanges = (Button) findViewById(R.id.btn_prefs_cancelChanges);

        // simply close the activity if the user doesn't want to commit the changes.
        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        } else if (id == R.id.action_ddms) {
            GlobalContent.DDMS(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogMessage(long message) {
        //set the new seconds for the chosen preference
        milliseconds.set(arrayPositionToModify, message);
        //format the seconds to a duration.
        String duration = GlobalContent.convertMillisToFormattedTime(message, 1);
        // set the text box to the duration specified
        durationTextViews.get(arrayPositionToModify).setText(duration);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
