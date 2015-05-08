package aad.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

///handles all the data access abilies of the boat SQL table
public class BoatDataSource {

    // log cat tagging
    private static final String LOG = "LogTag: BoatDataSource";

    //get db helper and the database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    //create a string array of all the boats
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
        values.put(DBAdapter.KEY_CREATED_AT, DBAdapter.getDateTime());
        values.put(DBAdapter.KEY_BOAT_VISIBLE, 1);
        long insertId = db.insert(DBAdapter.TABLE_BOATS, null, values);
        boat.setId(insertId);

        return boat;
    }

    //get a list of all the boats
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

    //get an array list of all the boats in the sql table
    public ArrayList<Boat> getAllBoatsArrayList(String where, String orderBy, String having ){
        ArrayList<Boat> boats = new ArrayList<>();

          // build a cursor that holds the sql data
        Cursor cursor = db.query(DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        //load the cursor with data
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

    //get a cursor alone from the sql table
    public Cursor getAllBoatsCursor(String where, String orderBy, String having){

        // grab a cursor laoded from db
        Cursor cursor = db.query(DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, where,
                null, null, null, orderBy);

        //if there is data move to the first row
        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    //change the data in the sql table using the values specified
    public boolean update(long id, String boatClass, String boatName, String boatSailNum,
                          int boatPHRF) {
        // send data to content views to be used as the updates
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues();
        newValues.put(DBAdapter.KEY_BOAT_CLASS, boatClass);
        newValues.put(DBAdapter.KEY_BOAT_NAME, boatName);
        newValues.put(DBAdapter.KEY_BOAT_SAIL_NUM, boatSailNum);
        newValues.put(DBAdapter.KEY_BOAT_PHRF, boatPHRF);
        return db.update(DBAdapter.TABLE_BOATS, newValues, whereClause, null) != 0;

    }

    //make a row of data disapear from the user's view
    public boolean psudoDelete(long id) {
        String whereClause = DBAdapter.KEY_ID + " = " + id;
        ContentValues newValues = new ContentValues(); // update from cursor
        newValues.put(DBAdapter.KEY_BOAT_VISIBLE, 0); // make the row invisible
        return db.update(DBAdapter.TABLE_BOATS, newValues, whereClause, null) != 0;
    }

    //get a cursor with a single row of data
   public Cursor getRow(long id) {
       String whereClause = DBAdapter.KEY_ID + " = " + id;
       // create a cursor with a query
       Cursor cursor = db.query(true, DBAdapter.TABLE_BOATS, ALL_BOAT_COLUMNS, whereClause,
               null, null, null, null, null);
       if (cursor != null) {
           cursor.moveToFirst(); // move to first record of the cursor
       }
       return cursor;
   }
}
