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
        values.put(DBAdapter.KEY_RACE_DISTANCE, race.getDistance());
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

    public Cursor getAllRacesCursor(String where, String orderBy, String groupBy){

        Cursor cursor = db.query(DBAdapter.TABLE_RACES, DBAdapter.RACES_ALL_FIELDS, where,
                null, groupBy, null, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public long getLastInsertedRowID() {
        String query = "SELECT MAX(" + DBAdapter.KEY_ID + ") from " + DBAdapter.TABLE_RACES;
        Cursor c;
        c = ((db.rawQuery(query, null)));
        if (c != null && c.moveToFirst()) {
            return c.getLong(0);
        } else {
            return -1;
        }

    }

    public boolean update(long id, String raceName, String raceDate, Double raceDistance, int clsBlue,
                          int clsGreen, int clsPurple, int clsYellow, int clsRed, int cls_TBD_) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RACE_NAME, raceName);
        newValues.put(DBAdapter.KEY_RACE_DATE, raceDate);
        newValues.put(DBAdapter.KEY_RACE_DISTANCE, raceDistance );
        newValues.put(DBAdapter.KEY_RACE_CLASS_BLUE, clsBlue);
        newValues.put(DBAdapter.KEY_RACE_CLASS_GREEN, clsGreen);
        newValues.put(DBAdapter.KEY_RACE_CLASS_PURPLE, clsPurple);
        newValues.put(DBAdapter.KEY_RACE_CLASS_YELLOW, clsYellow);
        newValues.put(DBAdapter.KEY_RACE_CLASS_RED, clsRed);
        newValues.put(DBAdapter.KEY_RACE_CLASS_TBD, cls_TBD_);
        return db.update(DBAdapter.TABLE_RACES, newValues, whereClause, null) != 0;

    }

    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_RACE_VISIBLE, 0);
        return db.update(DBAdapter.TABLE_RACES, newValues, whereClause, null) != 0;
    }

    public Cursor getRow(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        Cursor cursor = db.query(true, DBAdapter.TABLE_RACES, DBAdapter.RACES_ALL_FIELDS, whereClause,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
