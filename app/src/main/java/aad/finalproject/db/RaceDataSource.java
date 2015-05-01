package aad.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Daniel on 3/29/2015.
 */
public class RaceDataSource {

    // log cat tagging
    private static final String LOG = "LogTag: RaceDataSource";

    //get db helper and the database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;


    public RaceDataSource(Context context) {
        //Build database
        dbHelper = new DBAdapter(context);
        db = dbHelper.getWritableDatabase();
    }

    // open the datasource
    public void open() {
        db = dbHelper.getWritableDatabase();
        Log.i(LOG, "Opened data source");
    }

    // close data source
    public void close() {
        dbHelper.close();
        Log.i(LOG, "Closed data source");
    }

    public Race create(Race race) {
        // content values statement to package sql data
        ContentValues values = new ContentValues();
        values.put(DBAdapter.KEY_RACE_NAME, race.getName());
        values.put(DBAdapter.KEY_RACE_DATE, race.getDate());
        values.put(DBAdapter.KEY_RACE_DISTANCE, 0);
        values.put(DBAdapter.KEY_RACE_CLASS_BLUE, race.isClsBlue());
        values.put(DBAdapter.KEY_RACE_CLASS_GREEN, race.isClsGreen());
        values.put(DBAdapter.KEY_RACE_CLASS_PURPLE, race.isClsPurple());
        values.put(DBAdapter.KEY_RACE_CLASS_YELLOW, race.isClsYellow());
        values.put(DBAdapter.KEY_RACE_CLASS_RED, race.isClsRed());
        values.put(DBAdapter.KEY_RACE_CLASS_TBD, race.isCls_TBD_());
        values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
        values.put(DBAdapter.KEY_RACE_VISIBLE, 1);
        long insertId = db.insert(DBAdapter.TABLE_RACES, null, values);
        race.setId(insertId);

        return race;
    }

    //get a sql cursor for each race. Used to display the races in the listview
    public Cursor getAllRacesCursor(String where, String orderBy, String groupBy){

        Cursor cursor = db.query(DBAdapter.TABLE_RACES, DBAdapter.RACES_ALL_FIELDS, where,
                null, groupBy, null, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst(); //if we get results, move to the first one
        }

        return cursor; //return the cursor
    }

    //get the last inserted race id. THis will be the active race.
    public long getLastInsertedRowID() {
        String query = "SELECT MAX(" + DBAdapter.KEY_ID + ") from " + DBAdapter.TABLE_RACES;
        Cursor c; //create a cursor
        c = ((db.rawQuery(query, null)));// run query
        if (c != null && c.moveToFirst()) {
            return c.getLong(0); //return the id from column 0
        } else {
            return -1; // error
        }

    }

    //update that race information
    //includes distance but shouldn't
    public boolean update(long id, String raceName, String raceDate, Double raceDistance, int clsBlue,
                          int clsGreen, int clsPurple, int clsYellow, int clsRed, int cls_TBD_) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues(); //create a content value
        //load up alues
        newValues.put(DBAdapter.KEY_RACE_NAME, raceName);
        newValues.put(DBAdapter.KEY_RACE_DATE, raceDate);
        newValues.put(DBAdapter.KEY_RACE_DISTANCE, raceDistance );
        newValues.put(DBAdapter.KEY_RACE_CLASS_BLUE, clsBlue);
        newValues.put(DBAdapter.KEY_RACE_CLASS_GREEN, clsGreen);
        newValues.put(DBAdapter.KEY_RACE_CLASS_PURPLE, clsPurple);
        newValues.put(DBAdapter.KEY_RACE_CLASS_YELLOW, clsYellow);
        newValues.put(DBAdapter.KEY_RACE_CLASS_RED, clsRed);
        newValues.put(DBAdapter.KEY_RACE_CLASS_TBD, cls_TBD_);
        //run the update function using the loaded values
        return db.update(DBAdapter.TABLE_RACES, newValues, whereClause, null) != 0;

    }

    // no distance version of the update.
    public boolean update(long id, String raceName, String raceDate, int clsBlue,
                          int clsGreen, int clsPurple, int clsYellow, int clsRed, int cls_TBD_) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();//create a content value
        //load up alues
        newValues.put(DBAdapter.KEY_RACE_NAME, raceName);
        newValues.put(DBAdapter.KEY_RACE_DATE, raceDate);
        newValues.put(DBAdapter.KEY_RACE_DISTANCE, 0);
        newValues.put(DBAdapter.KEY_RACE_CLASS_BLUE, clsBlue);
        newValues.put(DBAdapter.KEY_RACE_CLASS_GREEN, clsGreen);
        newValues.put(DBAdapter.KEY_RACE_CLASS_PURPLE, clsPurple);
        newValues.put(DBAdapter.KEY_RACE_CLASS_YELLOW, clsYellow);
        newValues.put(DBAdapter.KEY_RACE_CLASS_RED, clsRed);
        newValues.put(DBAdapter.KEY_RACE_CLASS_TBD, cls_TBD_);
        //run the update function using the loaded values
        return db.update(DBAdapter.TABLE_RACES, newValues, whereClause, null) != 0;

    }

    //if user "deletes" a race, make it invisible but keep the data
    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id; //select based on ID
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RACE_VISIBLE, 0); //set the visibility field to 0
        return db.update(DBAdapter.TABLE_RACES, newValues, whereClause, null) != 0;
    }

    //get a sql cursor with the results of a single row
    public Cursor getRow(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id; //select the right row
        Cursor cursor = db.query(true, DBAdapter.TABLE_RACES, DBAdapter.RACES_ALL_FIELDS, whereClause,
                null, null, null, null, null); //assign the data to a cursor
        if (cursor != null) {
            cursor.moveToFirst(); // goto first result
        }
        return cursor; //send back the cursor
    }
}
