package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import aad.finalproject.db.ResultDataSource;


public class SelectClassDistance extends MainActivity {

    private static final String LOGTAG = "SelectClassDistance";
    // build array lists of widgets


    public static ArrayList<TextView> classColor;
    public static ArrayList<EditText> classDistance;
    public static ArrayList<Button> btnCalcDistance;
    private ArrayList<LinearLayout> classLinearLayouts;
    private Bundle b; // new bundle instance

    public static boolean isActiveSCD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class_distance);

        //get bundled info from race add form.
        final boolean setDistanceOnly = getIntent().getExtras().getBoolean(RaceAddForm.DISTANCE_KEY);

        //set the bundle variable
        b = new Bundle();
        b.putString(SelectBoats.SOURCE_BUNDLE_KEY, "SCD");

        isActiveSCD = true;

        // create instances of the arraylists
        classColor = new ArrayList<>();
        classDistance = new ArrayList<>();
        btnCalcDistance = new ArrayList<>();
        classLinearLayouts = new ArrayList<>();

        //assign values to the array lists
        createWidgetArrays();

        // initially hide all the layouts
        for (LinearLayout ll : classLinearLayouts) {
            ll.setVisibility(View.GONE);
        }

        //prepare the display elements and widgets and load with correct functions / data
        setupDisplayAndWidgets();

        // Wire buttons
        Button btnSetDistance = (Button) findViewById(R.id.btn_scd_gotoSelectBoats);
        Button btnBack = (Button) findViewById(R.id.btn_scd_back);
        Button btnDistanceUnknown = (Button) findViewById(R.id.btn_scd_distanceUnknown);

        //if the purpose is to set the distance of an existing race then do not show "Unknown"
        if (setDistanceOnly) {
            btnDistanceUnknown.setVisibility(View.GONE);
        }



        //set the button function
        btnSetDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (validateEntries()) {
                int c = 0; // counter
                // pass the distance to the Class's distance field
                for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                    //get the distance from the text field
                    double dist = Double.parseDouble(classDistance.get(c).getText().toString());
                    b.setClassDistance(dist); // set the distance to the boat class
                    Log.i(LOGTAG, " distance for class " + b.getBoatColor() + " is " +
                            b.getClassDistance());
                    c++; // increment the counter
                }
                // if the intent is to set the distance and move on to time tracking
                if (!setDistanceOnly) {
                    //active race now has distances for each class
                    GlobalContent.activeRace.hasDistance = true;
                    //start the select boats activity
                    Intent intent = new Intent(v.getContext(), SelectBoats.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    //if the intent is to set the distance for an existing race.
                } else {
                    // open the results database
                    ResultDataSource rds = new ResultDataSource(getApplicationContext());
                    rds.open();

                    //set the distance for each class in the correct race.
                    for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                        rds.updateClassDistances(GlobalContent.getRaceRowID(), b.getBoatColor(),
                                b.getClassDistance());
                    }
                    rds.runCalculations(); // calculate and enter adjusted duration.
                    //return to the previous activity.
                    finish();
                }
            }
            }
        });

        //set back button functions
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // close activity
            }
        });

        // if user does not know the distance yet.
        btnDistanceUnknown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set all class distances to 0
                for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                    bc.setClassDistance(0);
                }
                //active race has no distance
                GlobalContent.activeRace.hasDistance = false;
                //start the select boats activity
                Intent intent = new Intent(v.getContext(), SelectBoats.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // check to see if the entries are valid.
    private boolean validateEntries() {
        int c = 0; //counter
        for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
            //if it's null then it will cause error and return false
            try {
                double d = Double.parseDouble(classDistance.get(c).getText().toString());
                // if it's 0 or less then it will return false
                if (d <= 0) {
                    Toast.makeText(this, "Distance must be greater than 0", Toast
                            .LENGTH_LONG).show();
                    return false;
                }
            } catch (Exception e1) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
                return false;
            }
            c++;
        }
        return true;
    }

    private void setupDisplayAndWidgets() {
        int c = 0;
        //load instances
        for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
            final int counter = c;
            // set the layout to be visible!
            classLinearLayouts.get(counter).setVisibility(View.VISIBLE);
            // set the displayed class color name
            classColor.get(counter).setText(b.getBoatColor());
            // set each button's function
            btnCalcDistance.get(counter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DistanceCalculator.class);
                    startActivity(intent);
                    // tell the distance calculator which edit text to use
                    DistanceCalculator.editTextArrayLocation = counter;
                }
            });
            c++; //increment counter
        }
    }


    // set the active setting to false
    @Override
    public void onDestroy() {
        super.onDestroy();
        isActiveSCD = false;
    }

    private void createWidgetArrays() {

//        //add linear layouts
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class1));
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class2));
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class3));
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class4));
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class5));
        classLinearLayouts.add((LinearLayout)findViewById(R.id.linlay_class6));


        // add text views to the array
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class1));
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class2));
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class3));
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class4));
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class5));
        classColor.add((TextView) findViewById(R.id.txt_scd_color_class6));

        // add edit texts to the array
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class1));
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class2));
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class3));
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class4));
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class5));
        classDistance.add((EditText) findViewById(R.id.txt_scd_distance_class6));

        // add buttons to array list
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class1_calculate));
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class2_calculate));
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class3_calculate));
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class4_calculate));
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class5_calculate));
        btnCalcDistance.add((Button) findViewById(R.id.btn_scd_class6_calculate));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_class_distance, menu);
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
