package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.joda.time.DateTime;

import java.io.File;

import aad.finalproject.db.DBAdapter;
import aad.finalproject.db.DatabaseWriter;
import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.ResultDataSource;


public class RaceMenu extends MainActivity {

    // log cat tagging
    private static final String LOG = "RaceMenu";

    private ListView myListRace; // initialize the listview


    private RaceDataSource raceDataSource; // call the race datasource
    private ResultDataSource resultDataSource; // call the race datasource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_menu);

        //Build database
        raceDataSource = new RaceDataSource(this);
        resultDataSource = new ResultDataSource(this);
        raceDataSource.open();
        resultDataSource.open();

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
        } else if (id == R.id.action_send_all_races) {
            sendAllRaces();
        }
        return super.onOptionsItemSelected(item);
    }

    private String whiteSpacer(int spaces) {
        String spaceString = "&nbsp;";
        for (int i = 1; i < spaces; i++) {
            spaceString += "&nbsp;";
        }
        return spaceString;
    }

    private void sendAllRaces() {
        DateTime dtNow = DateTime.now();
        //create a file name for the csv file
        String fileName = "All Races as of " + dtNow.getMonthOfYear() + "." + dtNow.getDayOfMonth()
                + "." + dtNow.getYear() + ".csv";
        String fileNameNoFormat = "All Races as of " + dtNow.getMonthOfYear() + "." + dtNow.getDayOfMonth()
                + "." + dtNow.getYear();

        //write the database to a csv file.
        DatabaseWriter.exportDatabase(fileName, resultDataSource, true);

        //get the URI of the file just created
        Uri uri = Uri.fromFile(new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + fileName));

        Log.i(LOG, " URI: " + uri.toString());

        //Create a new email and
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regatta Results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "These are the results for " + fileName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<html><body>")
                .append("<div><font face=\"monospace\",\"monospace\">These are the results for: ").append(fileNameNoFormat).append("<br/>")
                .append("<br/>")
                .append("<strong>Column Name</strong>").append(whiteSpacer(20)).append("<strong>Description</strong><br/>")
                .append("-----------------------------------<br/>")
                .append("<strong>Race Name</strong>").append(whiteSpacer(22)).append("The name given to the race by the time keeper<br/>")
                .append("<strong>Date</strong>").append(whiteSpacer(27)).append("Date when the race took place<br/>")
                .append("<strong>Distance</strong>").append(whiteSpacer(23)).append("Total Distnace (nm) of the course for this class<br/>")
                .append("<strong>Fleet Color</strong>").append(whiteSpacer(20)).append("The color assigned to the fleet <br/>")
                .append("<strong>Boat Name</strong>").append(whiteSpacer(22)).append("The name of the boat as it appears in the DYC race registration<br/>")
                .append("<strong>Elapsed Time</strong>").append(whiteSpacer(19)).append("The time duration between the boat fleet's start time and the time it took for the boat to cross the finish line.<br/>")
                .append("<strong>Adj Elapsed Time</strong>").append(whiteSpacer(15)).append("The time duration adjusted for PHRF, Distance (nm), and any penalties incurred.<br/>")
                .append("<strong>Penalty Percent</strong>").append(whiteSpacer(16)).append("The percentage of time that will be added as a penalty for boats. E.g. a 1 hour of Elapsed Time with a 10% penalty will be 1 hour and 6 mins.<br/>")
                .append("<strong>Notes</strong>").append(whiteSpacer(26)).append("Any notes made by the time keeper<br/>")
                .append("<strong>DNF (1/0)</strong>").append(whiteSpacer(22)).append("Did Not Finish indicator. (1) True. (0) False <br/>")
                .append("<strong>PHRF rating</strong>").append(whiteSpacer(20)).append("The Performance Handicap Racing Fleet rating assigned to a boat<br/>")
                .append("<strong>Sail Number</strong>").append(whiteSpacer(20)).append("The number registered as the sail number <br/>")
                .append("<strong>Elapsed Time Set Manually(1/0)</strong>").append(whiteSpacer(1)).append("Indicates if the Elapsed Time was overridden by the Time Keeper. (1) True. (0) False <br/>")
                .append("<strong>Class Flag Up At:</strong>").append(whiteSpacer(14)).append("The exact time at which the race began for this boat's fleet color<br/>")
                .append("<strong>Boat Finished At:</strong>").append(whiteSpacer(14)).append("The exact time at which the boat crossed the finish line.")
                .append("</font face></div></body></html>").toString()));
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);//send file by email

        //dialog that asks the user to choose their mailing program preference
        startActivity(Intent.createChooser(emailIntent, "Choose your Email App:"));

    }

    // open the race editor/adder activity
    public void navigateToAddRaceForm(View view){
        Intent intent = new Intent(this,RaceAddForm.class);
        GlobalContent.setRaceFormAccessMode(false);  //set access mode to false for add mode
        startActivity(intent);
    }


    public void navigateRaceBack(View view){
        endActivity(); // exit activity and close db
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, " onResume Now");
        raceDataSource.open(); // reopen the db
        resultDataSource.open();
        populateListView(); // refresh listview

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, " onPause NOW");
        raceDataSource.close(); // close db to reduce data leak
        resultDataSource.close();
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
