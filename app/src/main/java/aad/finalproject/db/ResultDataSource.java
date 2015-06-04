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

/*
This class handles the connection between the app and the database for the results
 */
public class ResultDataSource {
    // log cat tagging
    public static String LOG = "ResultsDataSource";

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

    }

    public void insertResult(Boat b) {

        Result result = new Result();
        //load result with data from all over
        ContentValues values = new ContentValues();
        values.put(DBAdapter.KEY_RACE_ID, GlobalContent.activeRace.getId());
        values.put(DBAdapter.KEY_BOAT_ID, b.getBoatId());
        values.put(DBAdapter.KEY_RESULTS_PENALTY, 0);
        values.put(DBAdapter.KEY_RESULTS_NOT_FINISHED, 0);
        values.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, 0);
        values.putNull(DBAdapter.KEY_RESULTS_CLASS_START);
        values.putNull(DBAdapter.KEY_RESULTS_FINISH_TIME);
        values.putNull(DBAdapter.KEY_RESULTS_DURATION);
        values.putNull(DBAdapter.KEY_RESULTS_ADJ_DURATION);
        values.putNull(DBAdapter.KEY_RESULTS_NOTE);
        values.put(DBAdapter.KEY_RESULTS_PLACE, 0);
        values.put(DBAdapter.KEY_BOAT_NAME, b.getBoatName());
        values.put(DBAdapter.KEY_BOAT_SAIL_NUM, b.getBoatSailNum());
        values.put(DBAdapter.KEY_BOAT_CLASS, b.getBoatClass());
        values.put(DBAdapter.KEY_BOAT_PHRF, b.getBoatPHRF());
        values.put(DBAdapter.KEY_RACE_DISTANCE, GlobalContent.activeRace.getDistance());
        values.put(DBAdapter.KEY_RACE_NAME, GlobalContent.activeRace.getName());
        values.put(DBAdapter.KEY_RACE_DATE, GlobalContent.activeRace.getDate());
        values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
        values.put(DBAdapter.KEY_RACE_VISIBLE, 1);
        //insert the data into the sql db
        long insertId = db.insert(DBAdapter.TABLE_RESULTS, null, values);
        result.setResultsId(insertId); // insert data
        Log.i(LOG, " Added result ID: " + result.getResultsId() + " Boat name: " +
                b.getBoatName() + " Race name: " + GlobalContent.activeRace.getName());
    }

    public void deleteResult(long raceId, long boatId) {
        db.execSQL("DELETE FROM " + DBAdapter.TABLE_RESULTS + " WHERE " + DBAdapter.KEY_RACE_ID
                + " = " + raceId + " AND " + DBAdapter.KEY_BOAT_ID + " = " + boatId);
        Log.i(LOG, "Deleting Race & Boat id = " + raceId + "/" + boatId);
    }

    public void deleteRace(long raceId) {
        db.execSQL("DELETE FROM " + DBAdapter.TABLE_RESULTS + " WHERE " + DBAdapter.KEY_RACE_ID
                + " = " + raceId);
        Log.i(LOG, "Deleting Race id = " + raceId);
    }

    //get a list of all results in the results table.
    public List<Result> getAllResults(String where, String orderBy, String having ){
        List<Result> results = new ArrayList<>();

        //load cursor with the data from sqlite
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
        String where = DBAdapter.KEY_ID + " = " + resultId; // select what item to update
        ContentValues newValues = new ContentValues(); // create a content values instance
        //Put the new values into the contentValues variable
        newValues.putNull(DBAdapter.KEY_RESULTS_FINISH_TIME);
        newValues.putNull(DBAdapter.KEY_RESULTS_DURATION);
        newValues.putNull(DBAdapter.KEY_RESULTS_ADJ_DURATION);
        newValues.put(DBAdapter.KEY_RESULTS_MANUAL_ENTRY, 0); // set manual entry to non-manual (0)

        // return update the table through the return statement if not 0
        return db.update(DBAdapter.TABLE_RESULTS, newValues, where, null) != 0;
    }


    // add the finish time to the results list for the chosen class
    public void updateClassStartTime(long raceId, String boatClass, String timeFormatted,
                                     double distance) {
        //create stringified versions of the values for use in SQL statements
        String strBoatClass = GlobalContent.singleQuotify(boatClass);
        String strTimeFormatted = GlobalContent.singleQuotify(timeFormatted);
        // enter update statement in sql
        //if the boat or boats aren't classless then execute this update
        if (!boatClass.equals("Classless")) {
            db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS
                    + " SET " + DBAdapter.KEY_RESULTS_CLASS_START + "=" + strTimeFormatted + " , "
                    + DBAdapter.KEY_RACE_DISTANCE + "=" + distance
                    + " WHERE " + DBAdapter.KEY_RACE_ID + " = " + raceId + " AND "
                    + DBAdapter.KEY_BOAT_CLASS + " = " + strBoatClass + ";");
        } else {
            //if boats are classless then use this update.
            db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS
                    + " SET " + DBAdapter.KEY_RESULTS_CLASS_START + "=" + strTimeFormatted + " , "
                    + DBAdapter.KEY_RACE_DISTANCE + "=" + distance
                    + " WHERE " + DBAdapter.KEY_RACE_ID + " = " + raceId + ";");
        }

    }

    // add the finish time to the results list for the chosen class
    public void updateClassDistances(long raceId, String boatClass, double distance) {

        //create stringified values for sql tatements
        // enter update statement in sql
        //if the boat or boats arent classless then execute this update
        if (!boatClass.equals("Classless")) {
            db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS
                    + " SET " + DBAdapter.KEY_RACE_DISTANCE + "=" + distance
                    + " WHERE " + DBAdapter.KEY_RACE_ID + " = " + raceId + " AND "
                    + DBAdapter.KEY_BOAT_CLASS + " = " +
                    GlobalContent.singleQuotify(boatClass) + ";");
        } else {
            //if boats are classless then use this update.
            db.execSQL("UPDATE " + DBAdapter.TABLE_RESULTS
                    + " SET " + DBAdapter.KEY_RACE_DISTANCE + "=" + distance
                    + " WHERE " + DBAdapter.KEY_RACE_ID + " = " + raceId + ";");
        }

    }

    // clear the finish time for all results in the given race.
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

    // clear the finish time for all results in the given race.
    public void clearSingleClassStartTimesAndDurations(long raceId, String className) {
        // select what race to affect
        String where;
        //specify class only if the boats have a class.
        if (!className.equals("Classless")) {
            where = " WHERE "  + DBAdapter.KEY_RACE_ID + " = " + raceId
                    + " AND " + DBAdapter.KEY_BOAT_CLASS + " = " +
                    GlobalContent.singleQuotify(className) + ";";
        } else {
            //when race is classless then do not specify a class.
            where = " WHERE "  + DBAdapter.KEY_RACE_ID + " = " + raceId + ";";
        }
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

        raceID = GlobalContent.getRaceRowID();
        Log.i(LOG, " runCalculations: raceID = " + raceID);
        //create where statements with the right value and ID
        where = DBAdapter.KEY_RACE_ID + " = " + raceID
                + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";

        //create a results array list with all the results
        List<Result> resultArrayList = getAllResults(where, null, null);
        for (Result r : resultArrayList) {
            Log.i(LOG, " runCalculations: resultArrayList> Boat Name: " + r.boatName);
        }
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

                    Log.i(LOG, " runCalculations: newDuration: " + newDuration);
                    // enter elapsed time into the database
                    db.execSQL("UPDATE " + table + " SET " + durationColumn + " = " + GlobalContent
                            .singleQuotify(newDuration)
                            + " WHERE _id = " + resultId + ";");
                    //calculate adjusted duration
                    String adjDuration = GlobalContent.calculateAdjDuration(phrf, milliDuration,
                            penalty, distance, 1);
                    // enter adjusted duration
                    db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = " +
                            GlobalContent.singleQuotify(adjDuration) + " WHERE _id = "
                            + resultId + ";");

                } else if (r.getResultsManualEntry() == 1 && r.getResultsDuration() != null) {
                    //get the elapsed time in millis from the manually entered duration.
                    long manualMilliDuration = GlobalContent.getDurationInMillis(r.getResultsDuration());
                    //calculate adjusted duration
                    String adjDuration = GlobalContent.calculateAdjDuration(phrf, manualMilliDuration,
                            penalty, distance, 1);
                    // enter adjusted duration
                    db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = " +
                            GlobalContent.singleQuotify(adjDuration) + " WHERE _id = " + resultId + ";");
                }
            }
        }
        // check to see if the right data is present
    }//run calculations on the data table
    public void runSingleCalculation(long resultId) {

        //create a result cursor to get data from
        Cursor result = getRow(resultId);

        //make shortened variables for convineince
        String table = DBAdapter.TABLE_RESULTS;
        String durationColumn = DBAdapter.KEY_RESULTS_DURATION;
        String adjDurationColumn = DBAdapter.KEY_RESULTS_ADJ_DURATION;

        int phrf = result.getInt(result.getColumnIndex(DBAdapter.KEY_BOAT_PHRF));
        double penalty = result.getDouble(result.getColumnIndex(DBAdapter.KEY_RESULTS_PENALTY));
        double distance = result.getDouble(result.getColumnIndex(DBAdapter.KEY_RACE_DISTANCE));
        int notFinished = result.getInt(result.getColumnIndex(DBAdapter
                .KEY_RESULTS_NOT_FINISHED));
        int manualEntry = result.getInt(result.getColumnIndex(DBAdapter
                .KEY_RESULTS_MANUAL_ENTRY));
        String classStartTime = result.getString(result.getColumnIndex(DBAdapter
                .KEY_RESULTS_CLASS_START));
        String boatFinishTime = result.getString(result.getColumnIndex(DBAdapter
                .KEY_RESULTS_FINISH_TIME));
        String duration = result.getString(result.getColumnIndex(DBAdapter
                .KEY_RESULTS_DURATION));

        // if the boat is both visible and not manual entry mode. Then next
        if (notFinished == 0) {
            //if there is a start and a finish time calculate duration.
            Log.i(LOG, "manualEntry " + manualEntry + "clasStart " + classStartTime + "boatFin " + boatFinishTime);
            if (manualEntry == 0 && classStartTime.length() > 1 && boatFinishTime.length() > 1) {
                //calculate elapsed time
                long milliDuration = GlobalContent.getDurationInMillis(classStartTime,
                        boatFinishTime);
                // convert to readable format
                String newDuration = GlobalContent.convertMillisToFormattedTime(milliDuration, 0);

                Log.i(LOG, " runCalculations: newDuration: " + newDuration);
                // enter elapsed time into the database
                db.execSQL("UPDATE " + table + " SET " + durationColumn + " = " + GlobalContent
                        .singleQuotify(newDuration)
                        + " WHERE _id = " + resultId + ";");
                //calculate adjusted duration
                String adjDuration = GlobalContent.calculateAdjDuration(phrf, milliDuration,
                        penalty, distance, 1);
                // enter adjusted duration
                db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = " +
                        GlobalContent.singleQuotify(adjDuration) + " WHERE _id = " + resultId + ";");

            } else if (manualEntry == 1 && duration.length() > 1) {
                //get the elapsed time in millis from the manually entered duration.
                long manualMilliDuration = GlobalContent.getDurationInMillis(duration);
                //calculate adjusted duration
                String adjDuration = GlobalContent.calculateAdjDuration(phrf, manualMilliDuration,
                        penalty, distance, 1);
                // enter adjusted duration
                db.execSQL("UPDATE " + table + " SET " + adjDurationColumn + " = " +
                        GlobalContent.singleQuotify(adjDuration) + " WHERE _id = " + resultId + ";");
            }
        }
        // check to see if the right data is present
    }

    //get a list of all results in the results table.
    public Result getFirstClassResult(long raceId, String boatClass){

        // fields to select and filter by
        String bClass = DBAdapter.KEY_BOAT_CLASS;
        String resultId = DBAdapter.KEY_ID;
        String bName = DBAdapter.KEY_BOAT_NAME;
        String bStart = DBAdapter.KEY_RESULTS_CLASS_START;
        String bFinish = DBAdapter.KEY_RESULTS_FINISH_TIME;
        String bDuration = DBAdapter.KEY_RESULTS_DURATION;
        String bDnf = DBAdapter.KEY_RESULTS_NOT_FINISHED;
        String bVis = DBAdapter.KEY_RESULTS_VISIBLE;

        // ID
        String rId = DBAdapter.KEY_RACE_ID;

        // table
        String bTable = DBAdapter.TABLE_RESULTS;
        String sqlQuery;

        // create a sql statement
        if (!boatClass.equals("Classless")) {
            //write a query that only looks at a specific class
            sqlQuery = "SELECT " + resultId + "," + bName + "," + bClass + "," + bStart + "," + bFinish +
                        "," + bDuration
                    + " FROM " + bTable
                    + " WHERE " + rId + " = " + raceId + " AND " + bDnf + " = 0 AND " + bVis +
                        " = 1 AND " + bClass + " = " + GlobalContent.singleQuotify(boatClass) +
                        " AND " + bDuration + " IS NOT NULL "
                    + " ORDER BY " + bDuration
                    + " LIMIT 1;";

        } else {
            //write a query that doesn't exclude boats by class
            sqlQuery = "SELECT " + resultId + "," + bName + "," + bClass + "," + bStart + "," + bFinish +
                    "," + bDuration
                    + " FROM " + bTable
                    + " WHERE " + rId + " = " + raceId + " AND " + bDnf + " = 0 AND " + bVis +
                        " = 1 AND " + bDuration + " IS NOT NULL "
                    + " ORDER BY " + bDuration
                    + " LIMIT 1;";
        }
        //load cursor with the data from sqlite
        Cursor cursor;
        Log.i(LOG, "SQL Query Used: " + sqlQuery);
        cursor = db.rawQuery(sqlQuery,  null);
        cursor.moveToNext();

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");

        cursor.moveToFirst();
        Result result = new Result();
        // check if the cursor contains any values.
        if (cursor.getCount() > 0) {
            // add the requested fields to the result
            result.setResultsId(cursor.getLong(cursor.getColumnIndex(resultId)));
            result.setResultsClassStartTime(cursor.getString(cursor.getColumnIndex(bStart)));
            result.setResultsBoatFinishTime(cursor.getString(cursor.getColumnIndex(bFinish)));
            result.setResultsDuration(cursor.getString(cursor.getColumnIndex(bDuration)));
            result.setBoatName(cursor.getString(cursor.getColumnIndex(bName)));
            result.setBoatClass(cursor.getString(cursor.getColumnIndex(bClass)));
            cursor.close(); //close cursor to preserve resources
            return result;
        } else {
            cursor.close();
            return null; // just return a null version of the result
        }

    }

}
