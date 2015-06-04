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
    private static final String LOG = "BoatMenu";

    // parameters for methods using sql quiery parameters
    private String whereClauseIsVisible = DBAdapter.KEY_BOAT_VISIBLE + " = 1";
    private String orderByClause = DBAdapter.KEY_BOAT_CLASS + ", "
            + DBAdapter.KEY_BOAT_NAME ;
    private String havingClause = null;

    private ListView myList; // initialize the listview

    public static String ACCESS_METHOD_KEY = "Key";

    private BoatDataSource boatDataSource; // call the boat datasource

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

        //TODO: Default boats
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

    public void navigateBack(View view){

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
        String[][] boatString =
                {
                        {	"Yellow"	,	"151"	            ,	"151"	    ,	"168"	},
                        {	"Yellow"	,	"2"	                ,	"None"	    ,	"168"	},
                        {	"Green"	    ,	"Arbitrage"	        ,	"18550"	    ,	"63"	},
                        {	"Yellow"	,	"Assassin"	        ,	"None"	    ,	"168"	},
                        {	"Purple"	,	"Big Bird"	        ,	"__"	    ,	"183"	},
                        {	"Purple"	,	"Bigger Juan"	    ,	"None"	    ,	"198"	},
                        {	"Yellow"	,	"Blondie"	        ,	"None"	    ,	"168"	},
                        {	"Blue"	    ,	"Captain Cool"	    ,	"12174"	    ,	"114"	},
                        {	"Yellow"	,	"Danger Zone"	    ,	"None"	    ,	"168"	},
                        {	"Blue"	    ,	"Death Whoosh"	    ,	"None"	    ,	"126"	},
                        {	"Blue"	    ,	"Falcon"	        ,	"6019"	    ,	"0"	    },
                        {	"Blue"	    ,	"Flight Risk"	    ,	"2209"	    ,	"114"	},
                        {	"Yellow"	,	"Golden Draft"	    ,	"32419"	    ,	"171"	},
                        {	"Green"	    ,	"Hobbs"	            ,	"USA 52"	,	"51"	},
                        {	"Purple"	,	"Impetuous"	        ,	"527"	    ,	"192"	},
                        {	"Blue"	    ,	"Indigo"	        ,	"40628"	    ,	"126"	},
                        {	"Yellow"	,	"Insatiable"	    ,	"US-4808"	,	"168"	},
                        {	"Blue"	    ,	"Jack-A-Roe"	    ,	"None"	    ,	"102"	},
                        {	"Purple"	,	"Key of Sea"	    ,	"539"	    ,	"180"	},
                        {	"Green"	    ,	"Kicks"	            ,	"50296"	    ,	"81"	},
                        {	"Yellow"	,	"Loki"	            ,	"2578"	    ,	"183"	},
                        {	"Purple"	,	"Lunasa"	        ,	"56358"	    ,	"201"	},
                        {	"Purple"	,	"Makena"	        ,	"None"	    ,	"138"	},
                        {	"Blue"	    ,	"Mayhem"	        ,	"None"	    ,	"102"	},
                        {	"Green"	    ,	"Moon River"	    ,	"32635"	    ,	"69"	},
                        {	"Green"	    ,	"Mustang Sally Two"	,	"36000"	    ,	"81"	},
                        {	"Purple"	,	"Neverland"	        ,	"None"	    ,	"195"	},
                        {	"Purple"	,	"Northern Lights"	,	"111"	    ,	"108"	},
                        {	"Green"	    ,	"Papa Gaucho"	    ,	"40205"	    ,	"69"	},
                        {	"Blue"	    ,	"Polar Bear"	    ,	"50868"	    ,	"102"	},
                        {	"Green"	    ,	"Radio Flyer"	    ,	"40410"	    ,	"87"	},
                        {	"Blue"	    ,	"Ragnarok"	        ,	"31506"	    ,	"123"	},
                        {	"Purple"	,	"Ramble On"	        ,	"None"	    ,	"192"	},
                        {	"Blue"	    ,	"Raptor"	        ,	"220*"	    ,	"102"	},
                        {	"Purple"	,	"Satori"	        ,	"None"	    ,	"174"	},
                        {	"Green"	    ,	"Sixx"	            ,	"6"	        ,	"54"	},
                        {	"Purple"	,	"Skeptikatt"	    ,	"None"	    ,	"189"	},
                        {	"Yellow"	,	"Srange Crew"	    ,	"None"	    ,	"168"	},
                        {	"Yellow"	,	"Stellar Jay"	    ,	"None"	    ,	"180"	},
                        {	"Blue"	    ,	"Straw Man"	        ,	"81"	    ,	"105"	},
                        {	"Yellow"	,	"Striptease"	    ,	"4808*"	    ,	"0"	    },
                        {	"Blue"	    ,	"Swedish Magic"	    ,	"50955"	    ,	"135"	},
                        {	"Purple"	,	"Taku"	            ,	"2432"	    ,	"249"	},
                        {	"Blue"	    ,	"Tweety"	        ,	"30714"	    ,	"102"	},
                        {	"Yellow"	,	"Wai"	            ,	"None"	    ,	"168"	},
                        {	"Yellow"	,	"Whirlwind"	        ,	"None"	    ,	"219"	},
                        {	"Yellow"	,	"Whisky Jack"	    ,	"None"	    ,	"180"	},
                        {	"Yellow"	,	"Wind Walker"	    ,	"53"	    ,	"0"	    },
                        {	"Blue"      ,	"Loki II"           , 	"None"      ,	"114"	}

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

    //set the adapter and refresh the listview
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
