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
    private String whereClauseIsVisible = "visible = 1";
    private String orderByClause = "boat_name DESC, boat_class";
    private String havingClause = null;

    // tells all child activities how they should be displayed. i.e. edit vs add menu items
    public static String CHILD_ACTIVITY_TYPE_SWITCHER; // EDIT or CREATE

    public static long ROW_ID; // a public row id that passes info to other activities
    ListView myList; // initialize the listview


    BoatDataSource boatDataSource; // call the boat datasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_menu);

        //Build database
        boatDataSource = new BoatDataSource(this);
        boatDataSource.open();

//        listViewItemClicked(); // tell activity to start listening for onClicks of list items
        myList = (ListView) findViewById(R.id.lvBoatList); // set the lv to the current listview

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ROW_ID = id;
                CHILD_ACTIVITY_TYPE_SWITCHER = "EDIT";
                Intent gotoBoatForm = new Intent(view.getContext(), BoatAddForm.class);
                startActivity(gotoBoatForm);


            }
        });
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
        CHILD_ACTIVITY_TYPE_SWITCHER = "CREATE";
        Intent intent = new Intent(this, BoatAddForm.class);
        startActivity(intent);
    }

    public void navigateToMainMenu(View view){
        boatDataSource.close();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
        boatDataSource.open();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        boatDataSource.close();
    }


    public void createData() {
        String[][] boatString = {
            {"Blue" , "J-Hoe" , "514473" , "89"},
            {"Blue" , "Grace's Secret" , "456915" , "193"},
            {"Green" , "Demonio de la Velocidad" , "467961" , "-300"},
            {"Green" , "Goin' Fishin'" , "744218" , "174"},
            {"_TBD_" , "Allecrity" , "501645" , "155"},
            {"_TBD_" , "PDQ" , "782342" , "163"},
            {"_TBD_" , "Sea Shell" , "463029" , "175"},
            {"Purple" , "Hellofa Ride" , "393032" , "100"},
            {"Purple" , "Smooth Sailin'" , "632606" , "68"},
            {"Red" , "Happy Tub" , "329123" , "115"},
            {"Red" , "Norther Express" , "744688" , "65"},
            {"Red" , "Speed Incarnate" , "551642" , "57"},
            {"Red" , "Spirit Bomber" , "895720" , "185"},
            {"Red" , "Sweet 16" , "383153" , "110"},
            {"Red" , "Time Bandit" , "762180" , "186"},
            {"Yellow" , "Katrina" , "941659" , "65"},
            {"Yellow" , "The Winner" , "794736" , "121"},
            {"Yellow" , "Spring" , "684919" , "165"}};

        Boat boat = new Boat();
        for (int i = 0; i < boatString.length; i++) {

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

        SimpleCursorAdapter myCursorAdaptor;
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_list_template_boats, cursor, fromFieldNames, toViewIDs,0);
        myList.setAdapter(myCursorAdaptor);
    }

}
