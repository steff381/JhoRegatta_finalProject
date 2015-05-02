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
            Result result = new Result(); // create result
            //load result with data from all over
            ContentValues values = new ContentValues();
            values.put(DBAdapter.KEY_RACE_ID, GlobalContent.activeRace.getId());
            values.put(DBAdapter.KEY_BOAT_ID, b.getId());
            values.put(DBAdapter.KEY_RESULTS_PENALTY, 0);
            values.put(DBAdapter.KEY_RESULTS_NOT_FINISHED, 0);
            values.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, 0);
            values.put(DBAdapter.KEY_BOAT_NAME, b.getBoatName());
            values.put(DBAdapter.KEY_BOAT_SAIL_NUM, b.getBoatSailNum());
            values.put(DBAdapter.KEY_BOAT_CLASS, b.getBoatClass());
            values.put(DBAdapter.KEY_BOAT_PHRF, b.getBoatPHRF());
            values.put(DBAdapter.KEY_RACE_DISTANCE, GlobalContent.activeRace.getDistance());
//            values.put(DBAdapter.KEY_RACE_DISTANCE, GlobalContent.activeRace.getDistance());
            //TODO find some way to change distance by Distance value in BoatClass
            values.put(DBAdapter.KEY_RACE_NAME, GlobalContent.activeRace.getName());
            values.put(DBAdapter.KEY_RACE_DATE, GlobalContent.activeRace.getDate());
            values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
            values.put(DBAdapter.KEY_RACE_VISIBLE, 1);
            long insertId = db.insert(DBAdapter.TABLE_RESULTS, null, values);
            result.setResultsId(insertId); // insert data
            Log.i(LOG, " Added result ID: " + result.getResultsId() + " Boat name: " +
                    b.getBoatName() + " Race name: " + GlobalContent.activeRace.getName());
        }

    }

    //get a list of all results in the results table.
    public List<Result> getAllResults(String where, String orderBy, String having ){
        List<Result> results = new ArrayList<>();


        Cursor cursor = db.query(DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        // if the cursor isn't empty then build the results.
        if (cursor.getCount() > 0) while (cursor.moveToNext()) {
            Result result = new Result();
            // IDs
            result.setResultsId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ID)));
            result.setResultsBoatId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_BOAT_ID)));
            result.setResultsRaceId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_RACE_ID)));
            //results
            result.setResultsClassStartTime(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_CLASS_START)));
            result.setResultsBoatFinishTime(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_FINISH_TIME)));
            result.setResultsDuration(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_DURATION)));
            result.setResultsAdjDuration(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_ADJ_DURATION)));
            result.setResultsPenalty(cursor.getDouble(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_PENALTY)));
            result.setResultsNote(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_NOTE)));
            result.setResultsPlace(cursor.getInt(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_PLACE)));
            result.setResultsVisible(cursor.getInt(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_VISIBLE)));
            result.setResultsNotFinished(cursor.getInt(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_NOT_FINISHED)));
            result.setResultsManualEntry(cursor.getInt(cursor.getColumnIndex(
                    DBAdapter.KEY_RESULTS_MANUAL_ENTRY)));
            //boats
            result.setBoatName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
            result.setBoatSailNum(cursor.getString(cursor.getColumnIndex(
                    DBAdapter.KEY_BOAT_SAIL_NUM)));
            result.setBoatClass(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
            result.setBoatPHRF(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
            //races
            result.setRaceDistance(cursor.getDouble(cursor.getColumnIndex(
                    DBAdapter.KEY_RACE_DISTANCE)));
            result.setRaceName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RACE_NAME)));
            result.setRaceDate(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_RACE_DATE)));
            results.add(result); // add all to the results instance
            Log.i(LOG, "adding: " + result.getBoatName()
                    + " resultId: " + result.getResultsId());
        }
        cursor.close(); //close cursor to preserve resources
        return results;
    }



    // add the finish time to the results list for the individual result
    // for the row buttons in the Results Activity
    public boolean updateSingleFinishTime(long resultId, String timeFormatted) {
        String where = DBAdapter.KEY_ID + " = " + resultId; // select what item to update
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.put(DBAdapter.KEY_RESULTS_FINISH_TIME, String.valueOf(timeFormatted));

        // return update the table through the return statement if not 0
        return db.update(DBAdapter.TABLE_RESULTS, newValues, where, null) != 0;

    }

    //clear the fields of result time, duration and adjusted duration
    public boolean clearStartAndDurations(long resultId) {
        String value = null;
        String where = DBAdapter.KEY_ID + " = " + resultId; // select what item to update
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.put(DBAdapter.KEY_RESULTS_FINISH_TIME, value);
        newValues.put(DBAdapter.KEY_RESULTS_DURATION, value);
        newValues.put(DBAdapter.KEY_RESULTS_ADJ_DURATION, value);

        // return update the table through the return statement if not 0
        return db.update(DBAdapter.TABLE_RESULTS, newValues, where, null) != 0;
    }


    // add the finish time to the results list for the chosen class
    public void updateClassStartTime(long raceId, String boatClass, String timeFormatted) {
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.put(DBAdapter.KEY_RESULTS_CLASS_START, timeFormatted);
        // enter update statement in sql
        db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS + " SET " + DBAdapter.KEY_RESULTS_CLASS_START
                + "='" + timeFormatted + "' WHERE "  + DBAdapter.KEY_RACE_ID + " = " + raceId
                + " AND " + DBAdapter.KEY_BOAT_CLASS + " = '" + boatClass + "';");

    }
    // add the finish time to the results list for the chosen class
    public void updateClassStartTime(long raceId, String boatClass, String timeFormatted,
                                     double distance) {
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.put(DBAdapter.KEY_RESULTS_CLASS_START, timeFormatted);
        newValues.put(DBAdapter.KEY_RACE_DISTANCE, distance);
        // enter update statement in sql
        db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS
                + " SET " + DBAdapter.KEY_RESULTS_CLASS_START + "='" + timeFormatted + "' , "
                + DBAdapter.KEY_RACE_DISTANCE + "=" + distance
                + " WHERE " + DBAdapter.KEY_RACE_ID + " = " + raceId + " AND "
                + DBAdapter.KEY_BOAT_CLASS + " = '" + boatClass + "';");

    }

    // clear the finish time for all results in the given race.
    //
    public boolean clearRaceTimesDurations(long raceId) {
        String where = DBAdapter.KEY_RACE_ID + " = " + raceId; // select what race to affect
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.putNull(DBAdapter.KEY_RESULTS_CLASS_START);
        newValues.putNull(DBAdapter.KEY_RESULTS_FINISH_TIME);
        newValues.putNull(DBAdapter.KEY_RESULTS_DURATION);
        newValues.putNull(DBAdapter.KEY_RESULTS_ADJ_DURATION);

        // return update the table through the return statement
        return db.update(DBAdapter.TABLE_RESULTS, newValues, where, null) != 0;

    }

//    // clear the finish time for all results in the given race.
    public void clearSingleClassStartTimesAndDurations(long raceId, String className) {
        // select what race to affect
        String where = " WHERE "  + DBAdapter.KEY_RACE_ID + " = " + raceId
                + " AND " + DBAdapter.KEY_BOAT_CLASS + " = '" + className + "';";
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.putNull(DBAdapter.KEY_RESULTS_CLASS_START);
        newValues.putNull(DBAdapter.KEY_RESULTS_FINISH_TIME);
        newValues.putNull(DBAdapter.KEY_RESULTS_DURATION);
        newValues.putNull(DBAdapter.KEY_RESULTS_ADJ_DURATION);

        String set = " SET " + DBAdapter.KEY_RESULTS_CLASS_START + "=" + null + ", " +
                DBAdapter.KEY_RESULTS_FINISH_TIME + "=" + null + ", " +
                DBAdapter.KEY_RESULTS_DURATION + "=" + null + ", " +
                DBAdapter.KEY_RESULTS_ADJ_DURATION + "=" + null;

                db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS + set + where);

    }

    //update sql with the data provided
    public boolean update(long id,
                          String duration,
                          int penalty,
                          String note,
                          int notFinished,
                          int manualEntry) {
        String whereClause = DBAdapter.KEY_ID + " = " + id; // select what item to update
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.put(DBAdapter.KEY_RESULTS_DURATION, duration);
        newValues.put(DBAdapter.KEY_RESULTS_PENALTY, penalty);
        newValues.put(DBAdapter.KEY_RESULTS_NOTE, note);
        newValues.put(DBAdapter.KEY_RESULTS_NOT_FINISHED, notFinished);
        newValues.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, manualEntry);

        // return update the table through the return statement
        return db.update(DBAdapter.TABLE_RESULTS, newValues, whereClause, null) != 0;

    }

    // FOR FUTURE EXPANSION. May need to delete results
    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RESULTS_VISIBLE, 0);
        return db.update(DBAdapter.TABLE_RESULTS, newValues, whereClause, null) != 0;
    }

    //get single row of data
    public Cursor getRow(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id; //select the row id
        //create a cursor
        Cursor cursor = db.query(true, DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS,
                whereClause,
                null, null, null, null, null);
        if (cursor == null) {
            Log.i(LOG, "Cursor is Empty");
        }
        if (cursor != null) {
            cursor.moveToFirst(); // move to first record
        }
        return cursor; //send the cursor
    }

    //run calculations on the data table
    public void runCalculations() {
        // create a where statement to select the correct data
        String where; //statement holder
        long raceID; // id holder
        //check if there is an active race, if not use the race id that was set previously

        if (GlobalContent.activeRace != null) {
            raceID =  GlobalContent.activeRace.getId();
        } else {
            raceID = GlobalContent.getResultsRowID();
        }
        //create where statements with the right value and ID
        where = DBAdapter.KEY_RACE_ID + " = " + raceID
                + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";

        //create a results array list with all the results
        List<Result> resultArrayList = getAllResults(where, null, null);
        //make shortened variables for convineince
        String table = DBAdapter.TABLE_RESULTS;
        String durationColumn = DBAdapter.KEY_RESULTS_DURATION;
        String adjDurationColumn = DBAdapter.KEY_RESULTS_ADJ_DURATION;


        //run calcs on the array list
        for (Result r : resultArrayList) {
            Log.i(LOG, " Boat: " + r.getBoatName() + " " + r.boatClass + " finish time: "
                    + r.getResultsBoatFinishTime());
            long resultId = r.getResultsId(); // get the result id
            int phrf = r.getBoatPHRF();
            double penalty = r.getResultsPenalty();
            double distance = r.getRaceDistance();
            // if the boat is both visible and not manual entry mode. Then next
            if (r.getResultsNotFinished() == 0 && r.getResultsVisible() == 1) {
                //if there is a start and a finish time calculate duration.
                if (r.getResultsManualEntry() == 0 && r.getResultsClassStartTime() != null &&
                        r.getResultsBoatFinishTime() != null) {
                    //calculate elapsed time
                    long milliDuration = GlobalContent.getDurationInMillis(r.getResultsClassStartTime(),
                            r.getResultsBoatFinishTime());
                    // convert to readable format
                    String newDuration = GlobalContent.convertMillisToFormattedTime(milliDuration,0);
                    // enter elapsed time into the database
                    db.execSQL("UPDATE " + table + " SET " + durationColumn + " = '" + newDuration
                            + "' WHERE _id = " + resultId + ";");
                    //calculate adjusted duration
                    String adjDuration = GlobalContent.calculateAdjDuration(phrf, milliDuration,
                            penalty, distance, 1);
                    // enter adjusted duration
                    db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = '" +
                            adjDuration + "' WHERE _id = " + resultId + ";");

                } else if (r.getResultsManualEntry() == 1 && r.getResultsDuration() != null) {
                    //get the elapsed time in millis from the manually entered duration.
                    long manualMilliDuration = GlobalContent.getDurationInMillis(r.getResultsDuration());
                    //calculate adjusted duration
                    String adjDuration = GlobalContent.calculateAdjDuration(phrf, manualMilliDuration,
                            penalty, distance, 1);
                    // enter adjusted duration
                    db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = '" +
                            adjDuration + "' WHERE _id = " + resultId + ";");
                }
            }
        }
        // check to see if the right data is present


    }
}
