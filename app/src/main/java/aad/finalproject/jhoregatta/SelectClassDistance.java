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


public class SelectClassDistance extends MainActivity {

    private static final String LOGTAG = "SelectClassDistance";
    // build array lists of widgets
    public static ArrayList<TextView> classColor;
    public static ArrayList<EditText> classDistance;
    public static ArrayList<Button> btnCalcDistance;
    private ArrayList<LinearLayout> classLinearLayouts;

    private Button btnGoToSelectBoats;


    int c; // counter accessible in annonymous inner class
    public static boolean isActiveSCD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class_distance);

        //TODO For testing. If no boats in the array then add these 3
        if (BoatStartingListClass.BOAT_CLASS_START_ARRAY.size() == 0) {
            BoatStartingListClass.addToBoatClassStartArray("Red");
            BoatStartingListClass.addToBoatClassStartArray("Blue");
            BoatStartingListClass.addToBoatClassStartArray("Purple");
        }

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

        // grab the button instance
        btnGoToSelectBoats = (Button) findViewById(R.id.btn_scd_gotoSelectBoats);

        //set the button function
        btnGoToSelectBoats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = 0; // counter
                String color; // class color holder
                try {
                    // pass the distance to the Class's distace field
                    for (BoatClass b : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                        //get the distance from the text field
                        double dist = Double.parseDouble(classDistance.get(c).getText().toString());
                        b.setClassDistance(dist); // set the distance to the boat class
                        Log.i(LOGTAG, " distance for class " + b.getBoatColor() + " is " +
                                b.getClassDistance());
                        c++; // increment the counter
                    }
                    //start the select boats activity
                    Intent intent = new Intent(v.getContext(), SelectBoats.class);
                    startActivity(intent);
                } catch (Exception e) {
                    //grab the color of the class that caused the error
                    color =  BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(c).getBoatColor();
                    //inform user of the error
                    Toast.makeText(v.getContext(), "Missing or invalid entry on class: " + color,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupDisplayAndWidgets() {
        c = 0;
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

    //change activie status when activity starts and stops
    @Override
    public void onStart() {
        super.onStart();
        isActiveSCD = true;
    }

    // set the active setting to false
    @Override
    public void onStop() {
        super.onStop();
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
