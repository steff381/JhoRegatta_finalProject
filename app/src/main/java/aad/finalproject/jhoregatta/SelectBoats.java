package aad.finalproject.jhoregatta;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatAdapter;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.BoatListClass;
import aad.finalproject.db.DBAdapter;


public class SelectBoats extends MainActivity {




    // List stuff
    ListView myList; // initialize the listview
    Context context = null;
    BoatAdapter objAdapter;

    // log cat tagging
    private static final String LOG = "LogTag: SelectBoats";

    // parameters for methods using sql quiery parameters
    private String whereClauseIsVisible = "visible = 1";
    private String orderByClause = "boat_class, boat_name DESC";
    private String havingClause = null;
    CheckBox SelectBoatCkBox;


    final public static ArrayList<Boat> boatArrayList = new ArrayList<Boat>();

    // data base stuff
    BoatDataSource boatDataSource; // call the boat datasource


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_boats);

        //Build database
        boatDataSource = new BoatDataSource(this);
        boatDataSource.open(); // open a writable version of the db

        // set the lv to the current listview
        myList = (ListView) findViewById(R.id.lvSelectBoatList);
        // set the list to accept multiple selections
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

    }

    public void onClickCancel(View view) {
        boatDataSource.close();
        Intent intent = new Intent(this, RaceAddForm.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_boats, menu);
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

    public void onClickTimer(View view) {
        BoatListClass.setSelectedBoats();
        Intent intent = new Intent(this, RegattaTimer.class);
        startActivity(intent);

        Log.i(LOG, " Navigate to Timer");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatDataSource.open();
        populateListView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        boatDataSource.close();
    }

    public void populateListView(){

        Cursor cursor = boatDataSource.getAllBoatsCursor(whereClauseIsVisible, orderByClause,
                havingClause);
        //arrays to work with adapter
        String[] fromFieldNames = new String[] { //names of columns
                DBAdapter.KEY_ID,
                DBAdapter.KEY_BOAT_PHRF,
                DBAdapter.KEY_BOAT_VISIBLE,
                DBAdapter.KEY_BOAT_CLASS,
                DBAdapter.KEY_BOAT_NAME,
                DBAdapter.KEY_BOAT_SAIL_NUM};

        int[] toViewIDs = new int[] { // names of txt fields to populate
                R.id.txt_hd_ID,
                R.id.txt_hd_PHRF,
                R.id.txt_hd_Visible,
                R.id.txt_hd_Class,
                R.id.txt_hd_Name,
                R.id.txt_hd_SailNum};

        SimpleCursorAdapter myCursorAdaptor;
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_list_template_boats, cursor, fromFieldNames, toViewIDs,0);
        myList.setAdapter(myCursorAdaptor);
    }
}
