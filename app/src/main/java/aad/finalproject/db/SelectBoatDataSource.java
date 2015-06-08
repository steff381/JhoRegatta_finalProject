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
 * Created by Daniel on 5/22/2015.
 */
public class SelectBoatDataSource {


    // log cat tagging
    private static final String LOG = "SelectBoatDataSource";

    //get db helper and the database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    //create a string array of all the boats
    private static final String[] ALL_SELECT_BOAT_COLUMNS = {
            DBAdapter.KEY_ID,
            DBAdapter.KEY_BOAT_ID,
            DBAdapter.KEY_BOAT_CLASS,
            DBAdapter.KEY_BOAT_NAME,
            DBAdapter.KEY_BOAT_SAIL_NUM,
            DBAdapter.KEY_BOAT_PHRF,
            DBAdapter.KEY_BOAT_VISIBLE,
            DBAdapter.KEY_BOAT_SELECTED
    };

    public SelectBoatDataSource(Context context) {
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
        values.put(DBAdapter.KEY_BOAT_ID, boat.getId());
        values.put(DBAdapter.KEY_BOAT_NAME, boat.getBoatName());
        values.put(DBAdapter.KEY_BOAT_SAIL_NUM, boat.getBoatSailNum());
        values.put(DBAdapter.KEY_BOAT_CLASS, boat.getBoatClass());
        values.put(DBAdapter.KEY_BOAT_PHRF, boat.getBoatPHRF());
        values.put(DBAdapter.KEY_BOAT_VISIBLE, 1);
        values.put(DBAdapter.KEY_BOAT_SELECTED, 0);
        long insertId = db.insert(DBAdapter.TABLE_SELECT_BOATS, null, values);
        boat.setId(insertId);

        return boat;
    }

    public void buildBoatList(ArrayList<Boat> boats) {
        // content values statement to package sql data

        for (Boat boat : boats) {
            ContentValues values = new ContentValues();
            values.put(DBAdapter.KEY_BOAT_ID, boat.getId());
            values.put(DBAdapter.KEY_BOAT_NAME, boat.getBoatName());
            values.put(DBAdapter.KEY_BOAT_SAIL_NUM, boat.getBoatSailNum());
            values.put(DBAdapter.KEY_BOAT_CLASS, boat.getBoatClass());
            values.put(DBAdapter.KEY_BOAT_PHRF, boat.getBoatPHRF());
            values.put(DBAdapter.KEY_BOAT_VISIBLE, 1);
            values.put(DBAdapter.KEY_BOAT_SELECTED, 0);
            db.insert(DBAdapter.TABLE_SELECT_BOATS, null, values);

        }
    }

    public void addSingleBoat(Boat boat) {
        // content values statement to package sql data

        ContentValues values = new ContentValues();
        values.put(DBAdapter.KEY_BOAT_ID, boat.getId());
        values.put(DBAdapter.KEY_BOAT_NAME, boat.getBoatName());
        values.put(DBAdapter.KEY_BOAT_SAIL_NUM, boat.getBoatSailNum());
        values.put(DBAdapter.KEY_BOAT_CLASS, boat.getBoatClass());
        values.put(DBAdapter.KEY_BOAT_PHRF, boat.getBoatPHRF());
        values.put(DBAdapter.KEY_BOAT_VISIBLE, 1);
        values.put(DBAdapter.KEY_BOAT_SELECTED, 0);

        // add the boat to the table
        db.insert(DBAdapter.TABLE_SELECT_BOATS, null, values);


    }

    //get a list of all the boats
    public List<Boat> getAllSelectBoats(String where, String orderBy, String having) {
        List<Boat> boats = new ArrayList<>();


        Cursor cursor = db.query(DBAdapter.TABLE_SELECT_BOATS, ALL_SELECT_BOAT_COLUMNS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Boat boat = new Boat();
                boat.setId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ID)));
                boat.setBoatId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_BOAT_ID)));
                boat.setBoatClass(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
                boat.setBoatName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
                boat.setBoatSailNum(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
                boat.setBoatPHRF(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
                boat.setIsSelected(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SELECTED)));
                boats.add(boat);
            }
        }
        cursor.close();
        return boats;
    }

    //get a list of all the boat classes present in the current list of boats
    public ArrayList<String> getAllBoatClasses() {
        ArrayList<String> strBoatClasses = new ArrayList<>();
        String[] columns = {DBAdapter.KEY_BOAT_CLASS};
        Cursor c = db.query(true, DBAdapter.TABLE_SELECT_BOATS, columns, null, null, null,
                null, null, null);
        Log.i(LOG, "Returned " + c.getCount() + " classes");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                //add boat class to the output arraylist
                strBoatClasses.add(c.getString(c.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
            }
        }
        c.close();
        return strBoatClasses;
    }


    //get an array list of all the boats in the sql table
    public ArrayList<Boat> getAllSelectBoatsArrayList(String where, String orderBy, String having) {
        ArrayList<Boat> boats = new ArrayList<>();

        // build a cursor that holds the sql data
        Cursor cursor = db.query(DBAdapter.TABLE_SELECT_BOATS, ALL_SELECT_BOAT_COLUMNS, where,
                null, null, having, orderBy);

        Log.i(LOG, "Returned " + cursor.getCount() + " Rows");
        //load the cursor with data
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Boat boat = new Boat();
                boat.setId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ID)));
                boat.setBoatId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_BOAT_ID)));
                Log.i(LOG, "Key id " + boat.getId() + " Boat id " + boat.getBoatId());
                boat.setBoatClass(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_CLASS)));
                boat.setBoatName(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_NAME)));
                boat.setBoatSailNum(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SAIL_NUM)));
                boat.setBoatPHRF(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_PHRF)));
                boat.setIsSelected(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_BOAT_SELECTED)));
                boats.add(boat);
            }
        }
        cursor.close();
        return boats;
    }

    //update the selection status in the sql table
    public void setSelected(long selectedId, boolean isSelected) {
        //create content value to hold data
        ContentValues contentValues = new ContentValues();
        if (isSelected) { // if true then mark selected = 1
            contentValues.put(DBAdapter.KEY_BOAT_SELECTED, 1);
        } else { // else 0
            contentValues.put(DBAdapter.KEY_BOAT_SELECTED, 0);
        }
        //commit changes to the table.
        db.update(DBAdapter.TABLE_SELECT_BOATS, contentValues,
                DBAdapter.KEY_ID + "= " + selectedId, null);
    }

    //update the selection status in the sql table
    public void setSelectedByBoatId(long boatId, boolean isSelected) {
        //create content value to hold data
        ContentValues contentValues = new ContentValues();
        if (isSelected) { // if true then mark selected = 1
            contentValues.put(DBAdapter.KEY_BOAT_SELECTED, 1);
        } else { // else 0
            contentValues.put(DBAdapter.KEY_BOAT_SELECTED, 0);
        }
        //commit changes to the table.
        db.update(DBAdapter.TABLE_SELECT_BOATS, contentValues,
                DBAdapter.KEY_BOAT_ID + "= " + boatId, null);
    }

    // merge classes of one type into a different class
    public void mergeClasses(String parentClass, List<String> mergingClasses) {
        // define variables to shorten things up
        parentClass = GlobalContent.singleQuotify(parentClass); // quoted version of string
        String colBoatClass = DBAdapter.KEY_BOAT_CLASS;
        String tblTable = DBAdapter.TABLE_SELECT_BOATS;

        // change each one of the elements in the list to the parent class
        for (String boatClass : mergingClasses) {
            //creaet a quoted version of the string
            boatClass = GlobalContent.singleQuotify(boatClass);
            // commit changes to the sql table
            db.execSQL("UPDATE " + tblTable + " SET " + colBoatClass + " = " + parentClass
                    + " WHERE " + colBoatClass + " = " + boatClass);

            Log.i(LOG, " Changed " + boatClass + " to " + parentClass);
        }
    }

    //remove all rows from select boats table.
    public boolean deleteAllRows() {
        Log.i(LOG, "Deleting all rows");
        return db.delete(DBAdapter.TABLE_SELECT_BOATS, null, null) != 0;
    }


    public void rebuildTable() {
        Log.i(LOG, "Dropping SelectBoats Sql table");
        //delete the table entirely
        db.execSQL("DROP TABLE IF EXISTS " + DBAdapter.TABLE_SELECT_BOATS);

        Log.i(LOG, "Rebuilding SelectBoats Sql Table");
        //create the table again
        db.execSQL(DBAdapter.CREATE_TABLE_SELECT_BOATS);
    }

    //get a cursor with a single row of data
    public Cursor getRow(long boatId) {
        String whereClause = DBAdapter.KEY_BOAT_ID + " = " + boatId;
        // create a cursor with a query
        Cursor cursor = db.query(true, DBAdapter.TABLE_SELECT_BOATS, ALL_SELECT_BOAT_COLUMNS,
                whereClause, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst(); // move to first record of the cursor
        }
        return cursor;
    }
}