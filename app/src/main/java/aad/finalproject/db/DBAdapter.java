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
    private static final int DB_VERSION = 2;

    // db name
    public static final String DB_NAME = "jhoRegattaDatabase.db";

    // table names
    public static final String TABLE_BOATS = "boats";
    public static final String TABLE_SELECT_BOATS = "selectboats";
    public static final String TABLE_RACES = "races";
    public static final String TABLE_RESULTS = "results";
    public static final String TABLE_SERIES = "series";
    public static final String TABLE_SCORES = "scores";

    // common column names
    public static final String KEY_ID = "_id";
    public static final String KEY_CREATED_AT = "created_at";

    // Foreign Id columns
    public static final String KEY_RACE_ID = "race_id";
    public static final String KEY_BOAT_ID = "boat_id";
    public static final String KEY_SERIES_ID = "series_id"; // NEW IN V 2
    public static final String KEY_SCORES_ID = "scores_id"; // NEW IN V 2

    //BOATS table
    public static final String KEY_BOAT_NAME = "boat_name";
    public static final String KEY_BOAT_SAIL_NUM = "sail_num";
    public static final String KEY_BOAT_CLASS = "boat_class";
    public static final String KEY_BOAT_PHRF = "phrf";
    public static final String KEY_BOAT_SELECTED = "isSelected";
    public static final String KEY_BOAT_VISIBLE = "visible";

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

    public static final String KEY_RESULTS_CLASS_START = "class_start_time";
    public static final String KEY_RESULTS_FINISH_TIME = "boat_finish_time";
    public static final String KEY_RESULTS_DURATION = "elapsed_time";
    public static final String KEY_RESULTS_ADJ_DURATION = "adj_duration";
    public static final String KEY_RESULTS_PENALTY = "penalty_percent";
    public static final String KEY_RESULTS_NOTE = "note";
    public static final String KEY_RESULTS_PLACE = "place";
    public static final String KEY_RESULTS_RACE_POINTS = "points"; // NEW IN V 2 TODO add to DS and CREATE_TABLE string and ALL_FIELDS string
    public static final String KEY_RESULTS_NOT_FINISHED = "notFinished";
    public static final String KEY_RESULTS_VISIBLE = "visible";
    public static final String KEY_RESULTS_MANUAL_ENTRY = "elapsed_isedited";
//    public static final String KEY_RESULTS_CLS_FIN_ORD = "order_finished"; // NEW IN V 2

    // SERIES TABLE TODO build DS and class
    public static final String KEY_SERIES_NAME = "series_name"; // NEW IN V 2
    public static final String KEY_SERIES_SCOREKEEPER = "scorekeeper";// NEW IN V 2
    public static final String KEY_SERIES_SCOREKEEPER_EMAIL = "scorekpr_email";// NEW IN V 2
    public static final String KEY_SERIES_PR_RACE_OFF = "pr_race_ofcr";// NEW IN V 2
    public static final String KEY_SERIES_PR_RACE_OFF_EMAIL = "pr_race_ofcr_email";// NEW IN V 2
    public static final String KEY_SERIES_VISIBLE = "visible";// NEW IN V 2

    // SCORES TABLE TODO build DS and class

    public static final String KEY_SCORES_TTL_POINTS = "total_points";// NEW IN V 2
    public static final String KEY_SCORES_ATT_RACE_CNT = "attended_races";// NEW IN V 2
    public static final String KEY_SCORES_TTL_RACE_CNT = "total_races";// NEW IN V 2
    public static final String KEY_SCORES_VISIBLE = "visible";// NEW IN V 2




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

    //SELECT BOATS table create
    public static final String CREATE_TABLE_SELECT_BOATS = "CREATE TABLE " + TABLE_SELECT_BOATS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BOAT_ID + " INTEGER NOT NULL, "
            + KEY_BOAT_NAME + " TEXT NOT NULL,"
            + KEY_BOAT_SAIL_NUM + " TEXT NOT NULL,"
            + KEY_BOAT_CLASS + " TEXT NOT NULL,"
            + KEY_BOAT_PHRF + " INTEGER NOT NULL,"
            + KEY_BOAT_VISIBLE + " INTEGER NOT NULL,"
            + KEY_BOAT_SELECTED + " INTEGER NOT NULL"
            + ")";

    //races table create
    private static final String CREATE_TABLE_RACES = "CREATE TABLE " + TABLE_RACES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//            + KEY_SERIES_ID +  " INTEGER NOT NULL," v2
//            + KEY_SERIES_NAME +  " TEXT NOT NULL," v2
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
            + KEY_BOAT_SELECTED + " INTEGER,"
            + KEY_CREATED_AT + " TEXT"
            + ")";

    // Results table create
    private static final String CREATE_TABLE_RESULTS = "CREATE TABLE " + TABLE_RESULTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RACE_ID + " INTEGER,"
            + KEY_BOAT_ID + " INTEGER,"
//            + KEY_SERIES_ID + " INTEGER," // New v2
            + KEY_RESULTS_CLASS_START + " TEXT,"
            + KEY_RESULTS_FINISH_TIME + " TEXT,"
            + KEY_RESULTS_DURATION + " TEXT,"
            + KEY_RESULTS_ADJ_DURATION + " TEXT,"
            + KEY_RESULTS_PENALTY + " REAL,"
            + KEY_RESULTS_NOTE + " TEXT,"
            + KEY_RESULTS_PLACE + " INTEGER,"
            + KEY_RESULTS_VISIBLE + " INTEGER NOT NULL,"
            + KEY_RESULTS_NOT_FINISHED + " INTEGER,"
            + KEY_RESULTS_MANUAL_ENTRY + " INTEGER,"
//            + KEY_RESULTS_RACE_POINTS + " DOUBLE," /// New V2
            + KEY_BOAT_NAME + " TEXT,"
            + KEY_BOAT_SAIL_NUM + " TEXT,"
            + KEY_BOAT_CLASS + " TEXT,"
            + KEY_BOAT_PHRF + " INTEGER,"
            + KEY_RACE_DISTANCE + " DOUBLE,"
            + KEY_RACE_NAME + " TEXT,"
//            + KEY_RESULTS_CLS_FIN_ORD + " INTEGER," // New v2
//            + KEY_SERIES_NAME + " TEXT," // New V2
            + KEY_RACE_DATE + " TEXT,"
            + KEY_CREATED_AT + " TEXT"
            + ")";


    //boats table create
    private static final String CREATE_TABLE_SERIES = "CREATE TABLE " + TABLE_SERIES + "("
            + KEY_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SERIES_NAME +  " TEXT NOT NULL,"
            + KEY_SERIES_SCOREKEEPER +  " TEXT NOT NULL,"
            + KEY_SERIES_SCOREKEEPER_EMAIL +  " TEXT NOT NULL,"
            + KEY_SERIES_PR_RACE_OFF +  " TEXT NOT NULL,"
            + KEY_SERIES_PR_RACE_OFF_EMAIL +  " TEXT NOT NULL,"
            + KEY_SERIES_VISIBLE +  " INTEGER NOT NULL,"
            + KEY_CREATED_AT +  " TEXT"
            + ")";


    //boats table create
    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_SCORES + "("
            + KEY_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BOAT_ID +  " INTEGER NOT NULL,"
            + KEY_SERIES_ID +  " INTEGER NOT NULL,"
            + KEY_SERIES_NAME +  " TEXT NOT NULL,"
            + KEY_BOAT_NAME +  " TEXT NOT NULL,"
            + KEY_BOAT_CLASS +  " TEXT NOT NULL,"
            + KEY_SCORES_TTL_POINTS +  " DOUBLE NOT NULL,"
            + KEY_SCORES_ATT_RACE_CNT +  " INTEGER NOT NULL,"
            + KEY_SCORES_TTL_RACE_CNT +  " INTEGER NOT NULL,"
            + KEY_SCORES_VISIBLE +  " INTEGER NOT NULL,"
            + KEY_CREATED_AT +  " TEXT"
            + ")";

    // call all results
    public static final String[] RESULTS_ALL_FIELDS = new String[] {
            KEY_ID,
            KEY_RACE_ID,
            KEY_BOAT_ID,
//            KEY_SERIES_ID, // V2
            KEY_RESULTS_CLASS_START,
            KEY_RESULTS_FINISH_TIME,
            KEY_RESULTS_DURATION,
            KEY_RESULTS_ADJ_DURATION,
            KEY_RESULTS_PENALTY,
            KEY_RESULTS_NOTE,
            KEY_RESULTS_PLACE,
//            KEY_RESULTS_RACE_POINTS, // v2
            KEY_RESULTS_NOT_FINISHED,
            KEY_RESULTS_MANUAL_ENTRY,
            KEY_RESULTS_VISIBLE,
            KEY_BOAT_NAME,
            KEY_BOAT_SAIL_NUM,
            KEY_BOAT_CLASS,
            KEY_BOAT_PHRF,
            KEY_RACE_NAME,
//            KEY_RESULTS_CLS_FIN_ORD, // v2
//            KEY_SERIES_NAME, // v2
            KEY_RACE_DATE,
            KEY_RACE_DISTANCE
    } ;


    //array with all fields for the race in it
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
    //array with all fields for the race in it
    public static final String[] SCORES_ALL_FIELDS = new String[]{
            KEY_ID,
            KEY_BOAT_ID,
            KEY_SERIES_ID,
            KEY_SERIES_NAME,
            KEY_BOAT_NAME,
            KEY_BOAT_CLASS,
            KEY_SCORES_TTL_POINTS,
            KEY_SCORES_ATT_RACE_CNT,
            KEY_SCORES_TTL_RACE_CNT,
            KEY_SCORES_VISIBLE,
            KEY_CREATED_AT

    };
    //array with all fields for the race in it
    public static final String[] SERIES_ALL_FIELDS = new String[]{
            KEY_ID,
            KEY_SERIES_NAME,
            KEY_SERIES_SCOREKEEPER,
            KEY_SERIES_SCOREKEEPER_EMAIL,
            KEY_SERIES_PR_RACE_OFF,
            KEY_SERIES_PR_RACE_OFF_EMAIL,
            KEY_SERIES_VISIBLE,
            KEY_CREATED_AT

    };

    //todo make all field string for SERIES TABLE
    //todo make all field string for SCORES TABLE

    // instances constructor for the DBAdapter class
    public DBAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i(LOG, "------------Database Created");

    }

    // oncreate write to log cat
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create required tables and log result
        db.execSQL(CREATE_TABLE_RACES);
        Log.i(LOG, "------------Table " + TABLE_RACES + " Created");
        db.execSQL(CREATE_TABLE_BOATS);
        Log.i(LOG, "------------Table " + TABLE_BOATS + " Created");
        db.execSQL(CREATE_TABLE_SELECT_BOATS);
        Log.i(LOG, "------------Table " + TABLE_SELECT_BOATS + " Created");
        db.execSQL(CREATE_TABLE_RESULTS);
        Log.i(LOG, "------------Table " + TABLE_RESULTS + " Created");
        //todo add create statement for SERIES TABLE
        //todo add create statement for SCORES TABLE
    }

    // when db is updated rebuild the db with new version details
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // perform on upgrade of old tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECT_BOATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        //todo add drop statement for SERIES TABLE
        //todo add drop statement for SCORES TABLE

        //remake the tables
        onCreate(db);
    }


    /**
     * Create a time stamp for each entry into the database for tracking
     * */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss", Locale.getDefault());
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
            alc.set(1, Cursor2);
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
