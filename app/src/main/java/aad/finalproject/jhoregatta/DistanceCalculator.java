package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import aad.finalproject.db.DistanceCalculatorAdapter;


public class DistanceCalculator extends MainActivity {

    private static final String LOGTAG = "Distance Calculator";

    private DistanceCalculatorAdapter distanceCalculatorAdapter;
    private ArrayList<Coordinates> coordinatesArrayList;
    private ListView myList;


    String distanceNM; // display text variable
    //array navigation
    private int arrayPosition = -1;

    //calculatable variables
    private double latS, lonS;
    private int latD, latM, lonD, lonM;
    private String latDir, lonDir;
    private double distanceDouble;

    //widgets
    private EditText latDegrees, latMinutes, latSeconds;
    private EditText lonDegrees, lonMinutes, lonSeconds;
    private ToggleButton latDirection, lonDirection;
    private TextView distanceDisplay;

    private Button addEdit, delete, done;

    private CheckBox willSaveDegMin;
    // specify the numeric boundaries for the degrees, minutes and seconds
    private boolean isValidFields() {
        if (setValuesToVars()) {
            if (latD > 180 || latD < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Latitude Degrees",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (latM > 59 || latM < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Latitude Minutes",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (latS > 59.9999999 || latS < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Latitude Seconds",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (lonD > 180 || lonD < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Longitude Degrees",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (lonM > 59 || lonM < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Longitude Minutes",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (lonS > 59.9999999 || lonS < 0) {
                Toast.makeText(this, "INVALID ENTRY: Check your Longitude Seconds",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            return false;
        }

        return true;

    }


    //set the values in each text box to a variable, on error the box must be empty.
    private boolean setValuesToVars() {
        try {
            latD = Integer.parseInt(latDegrees.getText().toString());
            latM = Integer.parseInt(latMinutes.getText().toString());
            latS = Double.parseDouble(latSeconds.getText().toString());
            latDir = latDirection.getText().toString().trim();

            lonD = Integer.parseInt(lonDegrees.getText().toString());
            lonM = Integer.parseInt(lonMinutes.getText().toString());
            lonS = Double.parseDouble(lonSeconds.getText().toString());
            lonDir = lonDirection.getText().toString().trim();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //clear the edit text boxes after an event
    private void clearValues() {
        if (willSaveDegMin.isChecked()) {
            latSeconds.setText(null);
            lonSeconds.setText(null);
            arrayPosition = -1;
        } else {
            latDegrees.setText(null);
            latMinutes.setText(null);
            latSeconds.setText(null);
            latDirection.setChecked(false);
            lonDegrees.setText(null);
            lonMinutes.setText(null);
            lonSeconds.setText(null);
            lonDirection.setChecked(false);
            arrayPosition = -1;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_calculator);

        coordinatesArrayList = new ArrayList<>();

        //wire buttons
        addEdit = (Button) findViewById(R.id.btn_dc_addEdit);
        delete = (Button) findViewById(R.id.btn_dc_delete);
        done = (Button) findViewById(R.id.btn_dc_done);

        delete.setEnabled(false); // set to disabled initially.

        //wire distance display header
        distanceDisplay = (TextView) findViewById(R.id.txt_dc_calculated_distance);

        //wire listview
        myList = (ListView) findViewById(R.id.lv_distcalc);
        // lat buttons and tog
        latDegrees = (EditText) findViewById(R.id.txt_dc_lat_degrees);
        latMinutes = (EditText) findViewById(R.id.txt_dc_lat_minutes);
        latSeconds = (EditText) findViewById(R.id.txt_dc_lat_seconds);
        latDirection = (ToggleButton) findViewById(R.id.tog_dc_lat_direction);
        // long buttons and tog
        lonDegrees = (EditText) findViewById(R.id.txt_dc_lon_degrees);
        lonMinutes = (EditText) findViewById(R.id.txt_dc_lon_minutes);
        lonSeconds = (EditText) findViewById(R.id.txt_dc_lon_seconds);
        lonDirection = (ToggleButton) findViewById(R.id.tog_dc_lon_direction);

        //wire checkbox
        willSaveDegMin = (CheckBox) findViewById(R.id.ckbx_will_save_deg_min);


        // instance of the custom adapter
        distanceCalculatorAdapter = new DistanceCalculatorAdapter(this,
                R.layout.activity_list_template_distance_calculator, coordinatesArrayList);


        //Listening section

        // the list view listners
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrayPosition = position; // set the arrayposition equal to the position

                //change buttons for edit mode.
                addEdit.setText("Update"); //change the add button to say Update
                delete.setEnabled(true); // enable the delete button

                //convert strings to string array of each value
                String[] lat = coordinatesArrayList.get(position).getLatitudeString();
                String[] lon = coordinatesArrayList.get(position).getLongitudeString();

                //pass each value into the lat editboxes and toggle
                latDegrees.setText(lat[0].trim());
                latMinutes.setText(lat[1].trim());
                latSeconds.setText(lat[2].trim());
                if (lat[3].trim().equals("N")) {
                    latDirection.setChecked(false);
                } else {
                    latDirection.setChecked(true);
                }

                //pass each value into the lon editboxes and toggle
                lonDegrees.setText(lon[0].trim());
                lonMinutes.setText(lon[1].trim());
                lonSeconds.setText(lon[2].trim());
                if (lon[3].trim().equals("W")) {
                    lonDirection.setChecked(false);
                } else {
                    lonDirection.setChecked(true);
                }

            }
        });

        // done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RaceAddForm.isActiveRaceAddForm = true) {
                    // grab the current distance
                    RaceAddForm.raceDistance.setText(String.valueOf(distanceDouble));
                    finish(); // close out of the activity
                }
            }
        });


        addEdit.setText("Add");
        /// add and edit button
        addEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidFields()) {
                    if (arrayPosition == -1 ) { // if nothing is selected then add
                        Coordinates c = new Coordinates(); // create new coordinate
                        //set the coordinates to the values in the text boxes
                        c.setLatitudeString(latD, latM, latS, latDir);
                        c.setLongitudeString(lonD, lonM, lonS, lonDir);
                        coordinatesArrayList.add(c); // add the coordinate to the list
                    } else { // update the currently selected row
                        coordinatesArrayList.get(arrayPosition)
                                .setLatitudeString(latD, latM, latS, latDir);
                        coordinatesArrayList.get(arrayPosition)
                                .setLongitudeString(lonD, lonM, lonS, lonDir);
                        delete.setEnabled(false);
                        addEdit.setText("Add");
                    }
                    populateListView(); // refresh the listview
                    clearValues(); // clear the text fields that the user wants emptied
                    // calculate and store the distance
                    distanceDouble = calculateDistance();
                    // update display
                    distanceDisplay.setText("Distance: " + distanceDouble + " nm");

                }
            }
        });

        //delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the array position is -1 (no currently selected row) then remove from array
                if (arrayPosition > -1) {
                    coordinatesArrayList.remove(arrayPosition);
                    clearValues(); // clear values
                    populateListView(); // refresh the list
                    delete.setEnabled(false); // disable the delete button
                    addEdit.setText("Add");
                    // calculate and store the distance
                    distanceDouble = calculateDistance();
                    // update display
                    distanceDisplay.setText("Distance: " + distanceDouble + " nm");
                } else {

                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_distance_calculator, menu);
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

    //sync the array lists from this activity and the adapter and update.
    private void populateListView() {
        distanceCalculatorAdapter.updateList(coordinatesArrayList);
        myList.setAdapter(distanceCalculatorAdapter);
    }

    private double calculateDistance() {
        double totalDistance = 0;
        double er = 3440.065; // earths radius in nautical miles.
        // array must have more than one coordinate
        Log.i(LOGTAG, " array size is " + coordinatesArrayList.size());

        if (coordinatesArrayList.size()> 1) {

            for (int i = 0 ; i+1 < coordinatesArrayList.size(); i++) {
                Coordinates c1 = coordinatesArrayList.get(i);
                Coordinates c2 = coordinatesArrayList.get(i + 1);
                Log.i(LOGTAG, " Lat/ lon of c1 > i " + i + ": " + c1.latitudeString + " / " + c1.longitudeString);
                Log.i(LOGTAG, " Lat/ lon of c2 > i " + i + ": " + c2.latitudeString + " / " + c2.longitudeString);
                double lat1 = c1.getLatitudeDouble();
                double lat2 = c2.getLatitudeDouble();
                double lon1 = c1.getLongitudeDouble();
                double lon2 = c2.getLongitudeDouble();

                double dLat = lat2 - lat1;
                double dLon = lon2 - lon1;

                double a = Math.pow((Math.sin(dLat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2)
                        * Math.pow((Math.sin(dLon / 2)), 2);

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                Log.i(LOGTAG, "Distance of " + i + ": " + (er * c));

                totalDistance += er * c;
                Log.i(LOGTAG, "Total Distance of " + i + " is : " + totalDistance);
             }
        }
        return Math.round(totalDistance * 10000.0)/10000.0;
    }
}
