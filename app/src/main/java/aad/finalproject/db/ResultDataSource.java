package aad.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.jhoregatta.GlobalContent;

/**
 * Created by Daniel on 4/11/2015.
 */
public class ResultDataSource {
    // log cat tagging
    public static String LOG = "LogTag: ResultsDataSource";

    //get db helper and the database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;


    public ResultDataSource(Context context) {
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

    public void create() {

        //put the values into the results table for each boat
        for (Boat b : BoatListClass.selectedBoatsList) {
            Result result = new Result();
            ContentValues values = new ContentValues();
            values.put(DBAdapter.KEY_RACE_ID, GlobalContent.activeRace.getId());
            values.put(DBAdapter.KEY_BOAT_ID, b.getId());
//            values.put(DBAdapter.KEY_RESULTS_DURATION, result.getResultsDuration());
//            values.put(DBAdapter.KEY_RESULTS_ADJ_DURATION, result.getResultsAdjDuration());
            values.put(DBAdapter.KEY_RESULTS_PENALTY, 0);
//            values.put(DBAdapter.KEY_RESULTS_NOTE, result.getResultsNote());
//            values.put(DBAdapter.KEY_RESULTS_PLACE, result.getResultsPenalty());
            values.put(DBAdapter.KEY_RESULTS_NOT_FINISHED, 0);
            values.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, 0);
            values.put(DBAdapter.KEY_BOAT_NAME, b.getBoatName());
            values.put(DBAdapter.KEY_BOAT_SAIL_NUM, b.getBoatSailNum());
            values.put(DBAdapter.KEY_BOAT_CLASS, b.getBoatClass());
            values.put(DBAdapter.KEY_BOAT_PHRF, b.getBoatPHRF());
            values.put(DBAdapter.KEY_RACE_DISTANCE, GlobalContent.activeRace.getDistance());
            values.put(DBAdapter.KEY_RACE_NAME, GlobalContent.activeRace.getName());
            values.put(DBAdapter.KEY_RACE_DATE, GlobalContent.activeRace.getDate());
            values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
            values.put(DBAdapter.KEY_RACE_VISIBLE, 1);
            long insertId = db.insert(DBAdapter.TABLE_RESULTS, null, values);
            result.setResultsId(insertId);
            Log.i(LOG, " Added result ID: " + result.getResultsId() + " Boat name: " + b.getBoatName() + " Race name: " + GlobalContent.activeRace.getName());
        }

    }

    //get a list of all results in the results table.
    public List<Result> getAllResults(String where, String orderBy, String having ){
        List<Result> results = new ArrayList<>();


        Cursor cursor = db.query(DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Result result = new Result();
                // IDs
                result.setResultsId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ID)));
                result.setResultsBoatId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_BOAT_ID)));
                result.setResultsRaceId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_RACE_ID)));
                //results
                result.setResultsDuration(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_DURATION)));
                result.setResultsAdjDuration(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_ADJ_DURATION)));
                result.setResultsPenalty(cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_PENALTY)));
                result.setResultsNote(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_NOTE)));
                result.setResultsPlace(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_PLACE)));
                result.setResultsVisible(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_VISIBLE)));
                result.setResultsNotFinished(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_NOT_FINISHED)));
                result.setResultsManualEntry(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_RESULTS_MANUAL_ENTRY)));
                //boats
                result.setBoatName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
                result.setBoatSailNum(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
                result.setBoatClass(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
                result.setBoatPHRF(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
                //races
                result.setRaceDistance(cursor.getDouble(cursor.getColumnIndex(DBAdapter.KEY_RACE_DISTANCE)));
                result.setRaceName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RACE_NAME)));
                result.setRaceDate(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RACE_DATE)));
                results.add(result); // add all to the results instance
            }
        }
        cursor.close();
        return results;
    }




    // return a cursor with all rows of data
    public Cursor getAllResultsCursor(String where, String orderBy, String groupBy){

        Cursor cursor = db.query(DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS, where,
                null, null, null, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public boolean update(long id, String duration, String adjDuration, Double penalty,
                          String note, int place, int notFinished, int manualEntry) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RESULTS_DURATION, duration);
        newValues.put(DBAdapter.KEY_RESULTS_ADJ_DURATION, adjDuration);
        newValues.put(DBAdapter.KEY_RESULTS_PENALTY, penalty );
        newValues.put(DBAdapter.KEY_RESULTS_NOTE, note);
        newValues.put(DBAdapter.KEY_RESULTS_PLACE, place);
        newValues.put(DBAdapter.KEY_RESULTS_NOT_FINISHED, notFinished);
        newValues.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, manualEntry);

        return db.update(DBAdapter.TABLE_RESULTS, newValues, whereClause, null) != 0;

    }

    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RESULTS_VISIBLE, 0);
        return db.update(DBAdapter.TABLE_RESULTS, newValues, whereClause, null) != 0;
    }

    public Cursor getRow(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        Cursor cursor = db.query(true, DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS, whereClause,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
