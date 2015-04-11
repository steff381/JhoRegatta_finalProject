package aad.finalproject.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daniel on 3/27/2015.
 */
public class DBAdapter extends SQLiteOpenHelper{

    // log cat tagging
    private static final String LOG = "LogTag: DBAdapter";

    // db version
    private static final int DB_VERSION = 1;

    // db name
    public static final String DB_NAME = "jhoRegattaDatabase.db";

    // table names
    public static final String TABLE_BOATS = "boats";
    public static final String TABLE_RACES = "races";
    public static final String TABLE_RESULTS = "results";

    // common column names
    public static final String KEY_ID = "_id";
    public static final String KEY_CREATED_AT = "created_at";

    //BOATS table
    public static final String KEY_BOAT_NAME = "boat_name";
    public static final String KEY_BOAT_SAIL_NUM = "sail_num";
    public static final String KEY_BOAT_CLASS = "boat_class";
    public static final String KEY_BOAT_PHRF = "phrf";
    public static final String KEY_BOAT_VISIBLE = "visible";

    public static final String[] BOATS_ALL_FIELDS = new String[]{
            KEY_ID,
            KEY_BOAT_NAME,
            KEY_BOAT_SAIL_NUM,
            KEY_BOAT_CLASS,
            KEY_BOAT_PHRF,
            KEY_BOAT_VISIBLE
    };

    //RACES table
    public static final String KEY_RACE_NAME = "race_name";
    public static final String KEY_RACE_DATE = "date";
    public static final String KEY_RACE_DISTANCE = "distance";
    public static final String KEY_RACE_CLASS_BLUE = "cl_blue";
    public static final String KEY_RACE_CLASS_GREEN = "cl_green";
    public static final String KEY_RACE_CLASS_PURPLE = "cl_purple";
    public static final String KEY_RACE_CLASS_YELLOW = "cl_yellow";
    public static final String KEY_RACE_CLASS_RED = "cl_red";
    public static final String KEY_RACE_CLASS_TBD = "cl_tbd";
    public static final String KEY_RACE_VISIBLE = "visible";

    public static final String[] RACES_ALL_FIELDS = new String[]{
            KEY_ID,
            KEY_RACE_NAME,
            KEY_RACE_DATE,
            KEY_RACE_DISTANCE,
            KEY_RACE_CLASS_BLUE,
            KEY_RACE_CLASS_GREEN,
            KEY_RACE_CLASS_PURPLE,
            KEY_RACE_CLASS_YELLOW,
            KEY_RACE_CLASS_RED,
            KEY_RACE_CLASS_TBD,
            KEY_RACE_VISIBLE
    };


    //RESULTS table
    public static final String KEY_RACE_ID = "race_id";
    public static final String KEY_BOAT_ID = "boat_id";
    public static final String KEY_RESULTS_DURATION = "duration";
    public static final String KEY_RESULTS_ADJ_DURATION = "adj_duration";
    public static final String KEY_RESULTS_PENALTY = "penalty";
    public static final String KEY_RESULTS_NOTE = "note";
    public static final String KEY_RESULTS_PLACE = "place";
    public static final String KEY_RESULTS_VISIBLE = "visible";


    // build table statements

    //boats table create
    private static final String CREATE_TABLE_BOATS = "CREATE TABLE " + TABLE_BOATS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BOAT_NAME + " TEXT NOT NULL,"
            + KEY_BOAT_SAIL_NUM + " TEXT NOT NULL,"
            + KEY_BOAT_CLASS + " TEXT NOT NULL,"
            + KEY_BOAT_PHRF + " INTEGER NOT NULL,"
            + KEY_BOAT_VISIBLE + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " INTEGER NOT NULL"
            + ")";

    //races table create
    private static final String CREATE_TABLE_RACES = "CREATE TABLE " + TABLE_RACES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RACE_NAME + " TEXT NOT NULL,"
            + KEY_RACE_DATE + " TEXT NOT NULL,"
            + KEY_RACE_DISTANCE + " DOUBLE NOT NULL,"
            + KEY_RACE_CLASS_BLUE + " INTEGER NOT NULL,"
            + KEY_RACE_CLASS_GREEN + " INTEGER NOT NULL,"
            + KEY_RACE_CLASS_PURPLE + " INTEGER NOT NULL,"
            + KEY_RACE_CLASS_YELLOW + " INTEGER NOT NULL,"
            + KEY_RACE_CLASS_RED + " INTEGER NOT NULL,"
            + KEY_RACE_CLASS_TBD + " INTEGER NOT NULL,"
            + KEY_RACE_VISIBLE + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " TEXT"
            + ")";

    private static final String CREATE_TABLE_RESULTS = "CREATE TABLE " + TABLE_RESULTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RACE_ID + " INTEGER,"
            + KEY_BOAT_ID + " INTEGER,"
            + KEY_RACE_NAME + " TEXT,"
            + KEY_RACE_DATE + " TEXT,"
            + KEY_RACE_DISTANCE + " DOUBLE,"
            + KEY_BOAT_NAME + " TEXT,"
            + KEY_BOAT_SAIL_NUM + " TEXT,"
            + KEY_BOAT_CLASS + " TEXT,"
            + KEY_BOAT_PHRF + " INTEGER,"
            + KEY_RESULTS_DURATION + " TEXT,"
            + KEY_RESULTS_ADJ_DURATION + " TEXT,"
            + KEY_RESULTS_PENALTY + " REAL,"
            + KEY_RESULTS_NOTE + " TEXT,"
            + KEY_RESULTS_PLACE + " INTEGER,"
            + KEY_RESULTS_VISIBLE + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " TEXT"
            + ")";

    // call all results
    public static final String[] RESULTS_ALL_FIELDS = new String[] {
            KEY_ID,
            KEY_RACE_ID,
            KEY_BOAT_ID,
            KEY_RACE_NAME,
            KEY_RACE_DATE,
            KEY_RACE_DISTANCE,
            KEY_BOAT_NAME,
            KEY_BOAT_SAIL_NUM,
            KEY_BOAT_CLASS,
            KEY_BOAT_PHRF,
            KEY_RESULTS_DURATION,
            KEY_RESULTS_ADJ_DURATION,
            KEY_RESULTS_PENALTY,
            KEY_RESULTS_NOTE,
            KEY_RESULTS_PLACE,
            KEY_RESULTS_VISIBLE
    } ;
    public DBAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i(LOG, "------------Database Created");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create required tables and log result
        db.execSQL(CREATE_TABLE_RACES);
        Log.i(LOG, "------------Table " + TABLE_RACES + " Created");
        db.execSQL(CREATE_TABLE_BOATS);
        Log.i(LOG, "------------Table " + TABLE_BOATS + " Created");
        db.execSQL(CREATE_TABLE_RESULTS);
        Log.i(LOG, "------------Table " + TABLE_RESULTS + " Created");
    }

    // when db is updated rebuild the db with new version details
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // perform on upgrade of old tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);

        //remake the tables
        onCreate(db);
    }

    /**
     * get datetime
     * */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }




///////////////////////////BEGIN DB MANAGER SECTION
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
/////////////////////END DB MANAGER SECTION
}
