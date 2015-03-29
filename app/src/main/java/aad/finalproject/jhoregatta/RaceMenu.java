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
import aad.finalproject.db.Race;
import aad.finalproject.db.RaceDataSource;


public class RaceMenu extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: RaceMenu";

    // parameters for methods using sql quiery parameters
    private String whereClauseIsVisible = null;
    private String orderByClause = null;
    private String havingClause = null;

    // tells all child activities how they should be displayed. i.e. edit vs add menu items
    public static String CHILD_ACTIVITY_TYPE_SWITCHER; // EDIT or CREATE

    public static long ROW_ID; // a public row id that passes info to other activities
    ListView myListRace; // initialize the listview


    RaceDataSource raceDataSource; // call the race datasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_menu);

        //Build database
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();

//        listViewItemClicked(); // tell activity to start listening for onClicks of list items
        myListRace = (ListView) findViewById(R.id.lvRaceList); // set the lv to the current listview

        myListRace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ROW_ID = id;
                CHILD_ACTIVITY_TYPE_SWITCHER = "EDIT";
                Intent navigateToAddRaceForm = new Intent(view.getContext(), RaceAddForm.class);
                startActivity(navigateToAddRaceForm);


            }
        });
        Cursor races = raceDataSource.getAllRacesCursor(whereClauseIsVisible,
                orderByClause, havingClause);

        //TODO: FOR TESTING ONLY, REMOVE IF STATEMENT PRIOR TO COMPLETION
        if (races.getCount()>0) {
            populateListView();
        } else {
            createData();
            populateListView();
        }
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

    public void navigateToAddRaceForm(View view){
        CHILD_ACTIVITY_TYPE_SWITCHER = "CREATE";
        Intent intent = new Intent(this,RaceAddForm.class);
        startActivity(intent);
    }

    public void navigateToMainMenu(View view){
        raceDataSource.close();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
        raceDataSource.open();

    }

    @Override
    protected void onPause() {
        super.onPause();
        raceDataSource.close();
    }

    public void createData() {
        String[][] raceString = new String[][] {
            {"Race 1","03/30/15",	"0.83",	"0",	"0",	"0",	"0",	"1",	"1",	"1"},
            {"Race 2","03/31/15",	"0.46",	"0",	"0",	"0",	"1",	"1",	"1",	"1"},
            {"Race 3","04/01/15",	"1.95",	"0",	"0",	"0",	"0",	"1",	"0",	"1"},
            {"Race 4","04/02/15",	"7.45",	"0",	"0",	"0",	"0",	"0",	"0",	"1"},
            {"Race 5","04/03/15",	"1.99",	"1",	"0",	"1",	"1",	"1",	"0",	"1"},
            {"Race 6","04/04/15",	"2.81",	"0",	"0",	"1",	"0",	"0",	"0",	"1"},
            {"Race 7","04/05/15",	"9.11",	"0",	"1",	"1",	"0",	"0",	"1",	"1"},
            {"Race 8","04/06/15",	"4.05",	"0",	"0",	"1",	"1",	"0",	"1",	"1"},
            {"Race 9","04/07/15",	"5.53",	"0",	"1",	"1",	"0",	"0",	"0",	"1"},
            {"Race 10","04/08/15",	"3.69",	"0",	"1",	"1",	"0",	"1",	"0",	"1"},
            {"Race 11","04/09/15",	"7.51",	"1",	"0",	"1",	"1",	"1",	"1",	"1"},
            {"Race 12","04/10/15",	"7.98",	"0",	"0",	"1",	"0",	"1",	"1",	"1"},
            {"Race 13","04/11/15",	"6.60",	"0",	"0",	"1",	"1",	"1",	"1",	"1"},
            {"Race 14","04/12/15",	"7.79",	"0",	"0",	"1",	"1",	"0",	"0",	"1"},
            {"Race 15","04/13/15",	"3.64",	"0",	"1",	"0",	"1",	"1",	"0",	"1"},
            {"Race 16","04/14/15",	"9.01",	"0",	"1",	"0",	"0",	"0",	"0",	"1"},
            {"Race 17","04/15/15",	"9.72",	"1",	"0",	"1",	"1",	"0",	"1",	"1"},
            {"Race 18","04/16/15",	"5.73",	"1",	"0",	"1",	"0",	"0",	"1",	"1"}
        };

        Race race = new Race();
        for (int i = 0; i < raceString.length; i++) {

            race.setName(raceString[i][0]);
            race.setDate(raceString[i][1]);
            race.setDistance(Double.parseDouble(raceString[i][2]));
            race.setClsBlue(Integer.parseInt(raceString[i][3]));
            race.setClsGreen(Integer.parseInt(raceString[i][5]));
            race.setClsPurple(Integer.parseInt(raceString[i][6]));
            race.setClsYellow(Integer.parseInt(raceString[i][7]));
            race.setClsRed(Integer.parseInt(raceString[i][8]));
            race.setCls_TBD_(Integer.parseInt(raceString[i][9]));
            race = raceDataSource.create(race);
            Log.i(LOG,"Race created with the id of: " + race.getId() );
        }
    }

    public void populateListView(){

        Cursor cursor = raceDataSource.getAllRacesCursor("visible = 1", orderByClause, havingClause);
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

        int[] toViewIDs = new int[] { // names of txt fields to populate
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

        SimpleCursorAdapter myCursorAdaptor;
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_list_template_races, cursor, fromFieldNames, toViewIDs,0);
        myListRace.setAdapter(myCursorAdaptor);
    }
}
