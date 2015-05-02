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

import aad.finalproject.db.Boat;
import aad.finalproject.db.BoatDataSource;
import aad.finalproject.db.DBAdapter;


public class BoatMenu extends MainActivity {

    // log cat tagging
    private static final String LOG = "LogTag: BoatMenu";

    // parameters for methods using sql quiery parameters
    private String whereClauseIsVisible = DBAdapter.KEY_BOAT_VISIBLE + " = 1";
    private String orderByClause = DBAdapter.KEY_BOAT_CLASS + ", "
            + DBAdapter.KEY_BOAT_NAME + " DESC";
    private String havingClause = null;

    ListView myList; // initialize the listview

    public static String ACCESS_METHOD_KEY = "Key";

    BoatDataSource boatDataSource; // call the boat datasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_menu);

        //Build database
        boatDataSource = new BoatDataSource(this); // open writable version of DB
        boatDataSource.open();

        myList = (ListView) findViewById(R.id.lvBoatList); // set the lv to the current listview

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG, "Row id " + id);
                GlobalContent.setBoatRowID(id); // set the boat row id for the boat edit form
                GlobalContent.setBoatFormAccessMode(true); // edit mode is on
                Intent gotoBoatForm = new Intent(view.getContext(), BoatAddForm.class);
                startActivity(gotoBoatForm);
            }
        });

        // get a cursor for all the boats in the list
        Cursor boats = boatDataSource.getAllBoatsCursor(whereClauseIsVisible,
                orderByClause, havingClause);

        //TODO: FOR TESTING ONLY, REMOVE IF STATEMENT PRIOR TO COMPLETION
        if (boats.getCount()>0) {
            populateListView();
        } else {
            createData();
            populateListView();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_boat_menu, menu);
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

    public void navigateToAddBoatForm(View view) {
        //set the access mode to create
        GlobalContent.setBoatFormAccessMode(false);
        Intent intent = new Intent(this, BoatAddForm.class);
        startActivity(intent);
    }

    public void navigateToMainMenu(View view){
        Intent intent = new Intent(this,MainActivity.class); // open main menu and nav to
        startActivity(intent);
        endBoatActivity(); // close db and exit activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatDataSource.open(); // reopen the datasource
        populateListView(); // refresh the listview

    }

    @Override
    protected void onPause() {
        super.onPause();
        //close data source on pause
        boatDataSource.close();
    }


    // create lorim ipsum data
    public void createData() {
        String[][] boatString = {
                {	"_TBD_",	"Striptease",	"4808*",	"0"	},
                {	"_TBD_",	"Blondie",	"579",	"0"	},
                {	"_TBD_",	"Wei??? Wai???",	"None",	"0"	},
                {	"_TBD_",	"2",	"4204",	"168"	},
                {	"_TBD_",	"Srange Crew",	"None",	"168"	},
                {	"_TBD_",	"151",	"151",	"168"	},
                {	"_TBD_",	"Danger Zone",	"4808*",	"168"	},
                {	"_TBD_",	"Insatiable",	"4808*",	"168"	},
                {	"Blue",	"Northern Lights",	"111",	"0"	},
                {	"Blue",	"Tweedy Bird",	"30714",	"0"	},
                {	"Blue",	"Falcon",	"6019",	"0"	},
                {	"Blue",	"Polar Bear",	"50868",	"102"	},
                {	"Blue",	"Raptor",	"220*",	"102"	},
                {	"Blue",	"Death Whoosh",	"None",	"102"	},
                {	"Blue",	"Straw Man",	"81",	"105"	},
                {	"Blue",	"Flight Risk",	"2209",	"114"	},
                {	"Blue",	"Captin Cool",	"12174",	"114"	},
                {	"Blue",	"Indigo",	"40628",	"120"	},
                {	"Blue",	"Ragnarok",	"31506",	"123"	},
                {	"Blue",	"Swedish Magic",	"50955",	"138"	},
                {	"Green",	"Moon River",	"32635",	"0"	},
                {	"Green",	"Hobbs",	"None",	"0"	},
                {	"Green",	"Sixx",	"6",	"51"	},
                {	"Green",	"Arbitrage",	"18550",	"63"	},
                {	"Green",	"Papa Gaucho",	"40205",	"72"	},
                {	"Green",	"Mustang Sally Two",	"36000",	"81"	},
                {	"Green",	"Kicks",	"50296",	"81"	},
                {	"Green",	"Radio Flyer",	"40410",	"81"	},
                {	"Purple",	"Satori",	"None",	"0"	},
                {	"Purple",	"Key of Sea",	"539",	"168"	},
                {	"Purple",	"Impetuous",	"527",	"177"	},
                {	"Purple",	"Skeptikatt",	"540",	"180"	},
                {	"Purple",	"Neverland",	"16982",	"180"	},
                {	"Purple",	"Big Bird",	"__",	"183"	},
                {	"Purple",	"Lunasa",	"56358",	"183"	},
                {	"Purple",	"Bigger Juan",	"12",	"189"	},
                {	"Purple",	"Taku",	"2432",	"240"	},
                {	"Yellow",	"Wind Walker",	"53",	"0"	},
                {	"Yellow",	"Assassin",	"None",	"0"	},
                {	"Yellow",	"Golden Draft",	"32419",	"174"	},
                {	"Yellow",	"Stellar Jay",	"1090",	"180"	},
                {	"Yellow",	"Whisky Jack",	"17",	"180"	},
                {	"Yellow",	"Loki",	"2578",	"183"	},
                {	"Yellow",	"Whirlwind",	"569",	"216"	}
        };


        Boat boat = new Boat();
        for (int i = 0; i < boatString.length; i++) {

            //add each boat to the listview
            boat.setBoatClass(boatString[i][0]);
            boat.setBoatName(boatString[i][1]);
            boat.setBoatSailNum(boatString[i][2]);
            boat.setBoatPHRF(Integer.parseInt(boatString[i][3]));
            boat = boatDataSource.create(boat);
            Log.i(LOG,"Boat created with the id of: " + boat.getId() );
        }
    }

    public void populateListView(){

        Cursor cursor = boatDataSource.getAllBoatsCursor(whereClauseIsVisible, orderByClause, havingClause);
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

        //creaet an assign simple cursor adapter for the listview
        SimpleCursorAdapter myCursorAdaptor;
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_list_template_boats, cursor, fromFieldNames, toViewIDs,0);
        myList.setAdapter(myCursorAdaptor);
    }

    // close everythig out
    protected void endBoatActivity() {
        try {
            boatDataSource.close();
        } catch (Exception e) {
            Log.i(LOG, "Data source close caused error");
        }
        this.finish();
    }

}
