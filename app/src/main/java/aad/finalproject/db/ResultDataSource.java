package aad.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public Result create(Result result) {
        // content values statement to package sql data
        ContentValues values = new ContentValues();

        values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
        values.put(DBAdapter.KEY_RACE_VISIBLE, 1);
        long insertId = db.insert(DBAdapter.TABLE_RESULTS, null, values);
        result.setResultsId(insertId);

        return result;
    }

    public Cursor getAllResultsCursor(String where, String orderBy, String groupBy){

        Cursor cursor = db.query(DBAdapter.TABLE_RESULTS, DBAdapter.RESULTS_ALL_FIELDS, where,
                null, groupBy, null, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public boolean update(long id, String duration, String adjDuration, Double penalty,
                          String note, int place) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
//        newValues.put(DBAdapter.KEY_, );
//        newValues.put(DBAdapter.KEY_, );
//        newValues.put(DBAdapter.KEY_, );
//        newValues.put(DBAdapter.KEY_, );
//        newValues.put(DBAdapter.KEY_, );
//        newValues.put(DBAdapter.KEY_, );
        newValues.put(DBAdapter.KEY_RESULTS_DURATION, duration);
        newValues.put(DBAdapter.KEY_RESULTS_ADJ_DURATION, adjDuration);
        newValues.put(DBAdapter.KEY_RESULTS_PENALTY, penalty );
        newValues.put(DBAdapter.KEY_RESULTS_NOTE, note);
        newValues.put(DBAdapter.KEY_RESULTS_PLACE, place);
        return db.update(DBAdapter.TABLE_RESULTS, newValues, whereClause, null) != 0;

    }

    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RACE_VISIBLE, 0);
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
