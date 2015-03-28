package aad.finalproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
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
            KEY_BOAT_PHRF
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



    //RESULTS table
    public static final String KEY_RACES_ID = "race_id";
    public static final String KEY_BOATS_ID = "boat_id";
    public static final String KEY_RESULTS_DURATION = "duration";
    public static final String KEY_RESULTS_ADJ_DURATION = "adj_duration";
    public static final String KEY_RESULTS_PENALTY = "penalty";
    public static final String KEY_RESULTS_NOTE = "note";
    public static final String KEY_RESULTS_PLACE = "place";
    public static final String KEY_RESULTS_VISIBLE = "visible";

    // call all results
    public static final String[] RACES_ALL_FIELDS = new String[] {
            KEY_RACES_ID,
            KEY_BOATS_ID,
            KEY_RESULTS_DURATION,
            KEY_RESULTS_ADJ_DURATION,
            KEY_RESULTS_PENALTY,
            KEY_RESULTS_NOTE,
            KEY_RESULTS_PLACE
    } ;


    // build table statements

    //boats table create
    private static final String CREATE_TABLE_BOATS = "CREATE TABLE " + TABLE_BOATS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BOAT_NAME + " TEXT NOT NULL,"
            + KEY_BOAT_SAIL_NUM + " TEXT NOT NULL,"
            + KEY_BOAT_CLASS + " TEXT NOT NULL,"
            + KEY_BOAT_PHRF + " INTEGER NOT NULL,"
            + KEY_BOAT_VISIBLE + " INTEGER NOT NULL"
            + ")";

    //races table create
    private static final String CREATE_TABLE_RACES = "CREATE TABLE " + TABLE_RACES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RACE_NAME + " TEXT NOT NULL,"
            + KEY_RACE_DATE + " TEXT NOT NULL,"
            + KEY_RACE_DISTANCE + " DOUBLE NOT NULL,"
            + KEY_RACE_CLASS_BLUE + " BOOLEAN NOT NULL,"
            + KEY_RACE_CLASS_GREEN + " BOOLEAN NOT NULL,"
            + KEY_RACE_CLASS_PURPLE + " BOOLEAN NOT NULL,"
            + KEY_RACE_CLASS_YELLOW + " BOOLEAN NOT NULL,"
            + KEY_RACE_CLASS_RED + " BOOLEAN NOT NULL,"
            + KEY_RACE_CLASS_TBD + " BOOLEAN NOT NULL,"
            + KEY_RACE_VISIBLE + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " TEXT"
            + ")";

    private static final String CREATE_TABLE_RESULTS = "CREATE TABLE " + TABLE_RESULTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RACES_ID + " INTEGER,"
            + KEY_BOATS_ID + " INTEGER,"
            + KEY_RESULTS_DURATION + " TEXT,"
            + KEY_RESULTS_ADJ_DURATION + " TEXT,"
            + KEY_RESULTS_PENALTY + " REAL,"
            + KEY_RESULTS_NOTE + " TEXT,"
            + KEY_RESULTS_PLACE + " INTEGER,"
            + KEY_RESULTS_VISIBLE + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " TEXT"
            + ")";

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
}
