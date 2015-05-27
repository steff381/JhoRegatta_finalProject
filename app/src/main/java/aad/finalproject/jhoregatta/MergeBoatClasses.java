package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;

import aad.finalproject.db.ResultDataSource;
import aad.finalproject.db.SelectBoatDataSource;


public class MergeBoatClasses extends MainActivity {

    private static final String LOGTAG = "MergeBoatClasses";

    // widgets
    private Button btnMerge, btnReset, btnCancel;
    private Spinner spnSuperClass;
    private ArrayList<CheckBox> cbxArray;

    //arrays and adapters
    private ArrayList<String> boatClassArrayList;
    private ArrayList<String> selectedBoatClassArrayList;
    private String mergingClassName;


    //data sources
    private SelectBoatDataSource selectBoatDatasource;
    private ResultDataSource resultDataSource;

    //
    private final static String DEFAULT_SPINNER_TEXT = "Select Class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_boat_classes);

        //access databases
        selectBoatDatasource = new SelectBoatDataSource(this);
        selectBoatDatasource.open();
        resultDataSource = new ResultDataSource(this);
        resultDataSource.open();

        wireWidgets();//wire widget

        setWidgetListeners(); //add the user interaction listeners to widgets

        setCheckBoxVisibility(false); // make all switches invisible at start

        ///Preserve the unmerged list for use in reset/ un-merge
        // check for existing list of unmerged boats in global content
        if (GlobalContent.unmergedBoats.size() == 0) {
            // add boat classes to global content
            GlobalContent.unmergedBoats = selectBoatDatasource.getAllSelectBoatsArrayList(null,
                    null, null); //build list
        }

        selectedBoatClassArrayList = new ArrayList<>();
        boatClassArrayList = new ArrayList<>();

        boatClassArrayList.add(DEFAULT_SPINNER_TEXT); // add the default option to the list
        //Query the current table for a list of all boat classes present
        boatClassArrayList.addAll(selectBoatDatasource.getAllBoatClasses());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                boatClassArrayList);
        //change text size of spinner and spinner items.
        spnSuperClass.setAdapter(spinnerAdapter); //apply adapter to spinner
    }

    private void setWidgetListeners() {

        // SPINNER SECTION ===========================================

        // listener for spinner
        spnSuperClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerSelection = spnSuperClass.getSelectedItem().toString(); // get selection
                // apply the names of the boat classes in the table to the checkboxes and make visible
                Log.i(LOGTAG, "Spinner item: " + spinnerSelection);
                prepareCheckBoxes(boatClassArrayList, spinnerSelection);
                deselectCheckboxes(); //blank the boxes out.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {} // do nothing
        });

        // CHECKBOX SECTION ===========================================

        // wire the checkbox listeners
        for (CheckBox cb : cbxArray) {
            mergingClassName = cb.getText().toString(); // get the current class name
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //check the status of the checkbox
                    if (isChecked) {
                        //add the name to the array list
                        selectedBoatClassArrayList.add(buttonView.getText().toString());
                    } else {
                        //add the name to the array list
                        selectedBoatClassArrayList.remove(buttonView.getText().toString());
                    }
                }

            });
        }

        // BUTTON SECTION ===========================================

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // close activity
            }
        });

    }

    private void deselectCheckboxes() {
        for (CheckBox c : cbxArray) {
            c.setChecked(false); //deselect
        }
    }

    private void prepareCheckBoxes(ArrayList<String> boatClassArrayList, String spinnerSelection) {
        //hide or re-hide the current boat class checkboxes
        setCheckBoxVisibility(false);

        int i = 0;
        for (String s : boatClassArrayList) {
            // check if the current array item is the default text or the current selection.
            // if so, skip to next
            if (s.equals(DEFAULT_SPINNER_TEXT) || s.equals(spinnerSelection)) {
                Log.i(LOGTAG, s + " text value not set");

            } else {
                Log.i(LOGTAG, "Spinner value = " + s + " i = " + i + " Spinner Selection: " + spinnerSelection);

                cbxArray.get(i).setText(s); // set the name to the class name
                cbxArray.get(i).setVisibility(View.VISIBLE); // set the visibility to true
                i++;
            }
        }
    }

    //change visibility of check boxes
    private void setCheckBoxVisibility(boolean isVisible) {

        if (isVisible) { //show boxes
            for (CheckBox cbx : cbxArray) {
                cbx.setVisibility(View.VISIBLE);
            }
        } else { // hide boxes
            for (CheckBox cbx : cbxArray) {
                cbx.setVisibility(View.GONE);
            }
        }
    }

    //get the ids for the xml elements and wire them to java classes
    private void wireWidgets() {

        //spinner
        spnSuperClass = (Spinner) findViewById(R.id.spn_mbc_superClass);

        //checkbox array list
        cbxArray = new ArrayList<>();
        cbxArray.add((CheckBox) findViewById(R.id.cbx_mbc_class1));
        cbxArray.add((CheckBox) findViewById(R.id.cbx_mbc_class2));
        cbxArray.add((CheckBox) findViewById(R.id.cbx_mbc_class3));
        cbxArray.add((CheckBox) findViewById(R.id.cbx_mbc_class4));
        cbxArray.add((CheckBox) findViewById(R.id.cbx_mbc_class5));

        //buttons
        btnMerge = (Button) findViewById(R.id.btn_mbc_merge);
        btnReset = (Button) findViewById(R.id.btn_mbc_reset);
        btnCancel = (Button) findViewById(R.id.btn_mbc_cancel);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merge_boat_classes, menu);
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
    protected void onPause() {
        super.onPause();
        resultDataSource.close();
        selectBoatDatasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultDataSource.open();
        selectBoatDatasource.open();
    }
}
