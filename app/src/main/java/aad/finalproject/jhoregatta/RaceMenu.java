package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.RaceDataSource;


public class RaceMenu extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: RaceMenu";

    private ListView myListRace; // initialize the listview


    private RaceDataSource raceDataSource; // call the race datasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_menu);

        //Build database
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();

        myListRace = (ListView) findViewById(R.id.lvRaceList); // set the lv to the current listview

        //set the onclick listener for when user selects an item from the list
        myListRace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalContent.setRaceRowID(id); //grab race to get data for
                GlobalContent.setRaceFormAccessMode(true); // open form in edit mode
                Intent navigateToAddRaceForm = new Intent(view.getContext(), RaceAddForm.class);
                startActivity(navigateToAddRaceForm);


            }
        });
        //get a sql cursor of all the races in the database
        String whereClauseIsVisible = DBAdapter.KEY_RACE_VISIBLE + " = 1";
        String orderByClause = DBAdapter.KEY_RACE_DATE + " ASC";
        Cursor races = raceDataSource.getAllRacesCursor(whereClauseIsVisible,
                orderByClause, null);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race_menu, menu);
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

    // open the race editor/adder activity
    public void navigateToAddRaceForm(View view){
        Intent intent = new Intent(this,RaceAddForm.class);
        GlobalContent.setRaceFormAccessMode(false);  //set access mode to false for add mode
        startActivity(intent);
    }


    public void navigateRaceBack(View view){
//TODO Trash
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);
        endActivity(); // exit activity and close db
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, " onResume Now");
        raceDataSource.open(); // reopen the db
        populateListView(); // refresh listview

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, " onPause NOW");
        raceDataSource.close(); // close db to reduce data leak
    }

    // add the data from sql to the listview
    public void populateListView(){

        Cursor cursor = raceDataSource.getAllRacesCursor("visible = 1", null, null);
        //arrays to work with adapter
        String[] fromFieldNames = new String[] { //names of columns
                DBAdapter.KEY_ID,
                DBAdapter.KEY_RACE_NAME,
                DBAdapter.KEY_RACE_DATE,
                DBAdapter.KEY_RACE_DISTANCE,
                DBAdapter.KEY_RACE_CLASS_BLUE,
                DBAdapter.KEY_RACE_CLASS_GREEN,
                DBAdapter.KEY_RACE_CLASS_PURPLE,
                DBAdapter.KEY_RACE_CLASS_YELLOW,
                DBAdapter.KEY_RACE_CLASS_RED,
                DBAdapter.KEY_RACE_CLASS_TBD
        };
        // ids of txt fields to populate
        int[] toViewIDs = new int[] {
                R.id.txt_raceId,
                R.id.txt_raceName,
                R.id.txt_raceDate,
                R.id.txt_raceDistance,
                R.id.txt_raceBlue,
                R.id.txt_raceGreen,
                R.id.txt_racePurple,
                R.id.txt_raceYellow,
                R.id.txt_raceRed,
                R.id.txt_race_TBD_
        };

        SimpleCursorAdapter myCursorAdaptor; // create an adapter
        // Wire the template and to the sql table
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_list_template_races, cursor, fromFieldNames, toViewIDs,0);
        myListRace.setAdapter(myCursorAdaptor); // wire the adapter to the listview
    }

    // end of activity actions
    protected void endActivity() {
        try {
            raceDataSource.close();
        } catch (Exception e) {
            Log.i(LOG, "Data source close threw error");
        }
        this.finish();
    }
}
