package aad.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 3/28/2015.
 */
public class BoatDataSource {

    // log cat tagging
    private static final String LOG = "LogTag: BoatDataSource";

    //get db helper and the database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    private static final String[] ALL_BOAT_COLUMNS = {
            DBAdapter.KEY_ID,
            DBAdapter.KEY_BOAT_CLASS,
            DBAdapter.KEY_BOAT_NAME,
            DBAdapter.KEY_BOAT_SAIL_NUM,
            DBAdapter.KEY_BOAT_PHRF,
            DBAdapter.KEY_BOAT_VISIBLE
    };

    public BoatDataSource(Context context) {
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

    public Boat create(Boat boat) {
        // content values statement to package sql data
        ContentValues values = new ContentValues();
        values.put(DBAdapter.KEY_BOAT_NAME, boat.getBoatName());
        values.put(DBAdapter.KEY_BOAT_SAIL_NUM, boat.getBoatSailNum());
        values.put(DBAdapter.KEY_BOAT_CLASS, boat.getBoatClass());
        values.put(DBAdapter.KEY_BOAT_PHRF, boat.getBoatPHRF());
        values.put(DBAdapter.KEY_BOAT_VISIBLE, 1);
        long insertId = db.insert(DBAdapter.TABLE_BOATS, null, values);
        boat.setId(insertId);

        return boat;
    }

    public List<Boat> getAllBoats(String where, String orderBy, String having ){
        List<Boat> boats = new ArrayList<>();


        Cursor cursor = db.query(DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Boat boat = new Boat();
                boat.setId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ID)));
                boat.setBoatClass(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
                boat.setBoatName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
                boat.setBoatSailNum(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
                boat.setBoatPHRF(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
                boats.add(boat);
            }
        }
        cursor.close();
        return boats;
    }

    public Cursor getAllBoatsCursor(String where, String orderBy, String having){
        String sortBy = DBAdapter.KEY_BOAT_CLASS;


        Cursor cursor = db.query(DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, where,
                null, null, null, sortBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public boolean update(long id, String boatClass, String boatName, String boatSailNum,
                          int boatPHRF) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_BOAT_CLASS, boatClass);
        newValues.put(DBAdapter.KEY_BOAT_NAME, boatName);
        newValues.put(DBAdapter.KEY_BOAT_SAIL_NUM, boatSailNum);
        newValues.put(DBAdapter.KEY_BOAT_PHRF, boatPHRF);
        return db.update(DBAdapter.TABLE_BOATS, newValues, whereClause, null) != 0;

    }

    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_BOAT_VISIBLE, 0);
        return db.update(DBAdapter.TABLE_BOATS, newValues, whereClause, null) != 0;
    }

       public Cursor getRow(long id) {
           String whereClause = DBAdapter.KEY_ID + " = " + id;
           Cursor cursor = db.query(true, DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, whereClause,
                   null, null, null, null, null);
           if (cursor != null) {
               cursor.moveToFirst();
           }
           return cursor;
       }

    public static int getClassColorPosition(String classColor) {
        int colorPosition;
        switch (classColor) {
            case "Red":
                colorPosition = 1;
                break;
            case "Purple":
                colorPosition = 2;
                break;
            case "Yellow":
                colorPosition = 3;
                break;
            case "_TBD_":
                colorPosition = 4;
                break;
            case "Blue":
                colorPosition = 5;
                break;
            case "Green":
                colorPosition = 6;
                break;
            default:
                Log.i(LOG, "No color position found");
                colorPosition = 0;
                break;
        }

        return colorPosition;
    }


}
