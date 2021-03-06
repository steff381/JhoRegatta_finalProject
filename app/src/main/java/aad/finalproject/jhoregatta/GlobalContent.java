package aad.finalproject.jhoregatta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import aad.finalproject.db.AndroidDatabaseManager;
import aad.finalproject.db.Boat;
import aad.finalproject.db.Race;
import aad.finalproject.db.Result;
import aad.finalproject.db.ResultsAdapter;

/*
This class contains data that can be accessed by any class in the project.
 */
public class GlobalContent {

    private static final String LOGTAG = "GlobalContent"; // log tag for records
    protected static boolean RESULT_MENU_ALIVE = false;

    //vital details used by the time tracker and results menu
    public static Race activeRace;
    public static ResultsAdapter activeResultsAdapter = null;

    public static AudioVoiceManager avm; // globally accessible audio manager

//    public static long dimmerDelay = ; // how long should screen remain bright before dimming

    //result list that can be accessed globally.
    public static List<Result> resultList;

    //preserved list of boat classes that haven't been merged
    public static ArrayList<Boat> unmergedBoats;

    //access mode holders
    private static String BoatFormAccessMode;
    private static String RaceFormAccessMode;
    private static String ResultsFormAccessMode;

    //storage for the ID's of each table
    private static long boatRowID;
    private static long raceRowID;
    public static String raceRowName;
    public static String raceRowDate;
    private static long resultsRowID;

    //form access mode options
    public static String modeAdd = "ADD";
    public static String modeEdit = "EDIT";


    // public alive statement
    public static boolean selectBoatListIsAlive = false;

    // Joda time formatters. Used by all applications for consistancy of date time formats
    private static DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("hh:mm:ss a");
    public static String globalWhere; // where clause to be used by multiple interfaces

    //clear out the common resources among activities
    public static void finalDataClear() {
        BoatFormAccessMode = null;
        RaceFormAccessMode = null;
        ResultsFormAccessMode = null;
        BoatStartingListClass.BOAT_CLASS_START_ARRAY.clear();
        globalWhere = null;
        try {
            avm.audioFullStop();
        } catch (Exception e) {
            Log.i(LOGTAG, "AVM ERROR. No instance recorded.");
        }
        unmergedBoats = null;
        activeRace = null;

    }

    // Time handling methods
    // from string to local time
    public static DateTime toDateTime(String time) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm:ss a");
        return formatter.parseDateTime(time);
    }


    // from local time to string format
    public static String dateTimeToString(DateTime dateTime) {
        return dateTime.toString(timeFormatter);
    }

    // convert millis in long format and get elapsed time formatted
    public static String convertMillisToFormattedTime(long millis, int format) {

        // 0 is elapsed time format
        if (format == 0) {
            //format as string
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            // 1 is 00h 00m 00s format
        } else if (format == 1) {
            if (millis >= 3600000) { // format with hours
                return String.format("%2dh %2dm %2ds",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) %
                                TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) %
                                TimeUnit.MINUTES.toSeconds(1));
            } else if (millis >= 60000) { //format with mins
                return String.format("%2dm %2ds",
                        TimeUnit.MILLISECONDS.toMinutes(millis) %
                                TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) %
                                TimeUnit.MINUTES.toSeconds(1));
            } else { //format with just seconds
                return String.format("%2ds",
                        TimeUnit.MILLISECONDS.toSeconds(millis) %
                                TimeUnit.MINUTES.toSeconds(1));
            }

        } else {
            Log.i(LOGTAG, " Convert Millis to Formatted Time = Wrong format specified");
            return null;
        }
    }


    //elapsed time string to millis
    public static long getDurationInMillis(String startTimeString, String finishTimeString) {
        // convert string to date times
        DateTime startTime = toDateTime(startTimeString);
        DateTime finishTime = toDateTime(finishTimeString);
        // calculate difference in time
        return finishTime.getMillis() - startTime.getMillis();
    }

    //elapsed time string to millis
    public static long getDurationInMillis(String elapsedDuration) {
        String[] splitTime;
        splitTime = elapsedDuration.split(":"); // break up string
        long h, m, s;
        // convert hours mins seconds to millis
        try {
            h = 3600000 * Long.parseLong(splitTime[0]);
            m = 60000 * Long.parseLong(splitTime[1]);
            s = 1000 * Long.parseLong(splitTime[2]);
        } catch (NumberFormatException e) {
            Log.i(LOGTAG, "ERROR!!! > getDurationInMillis NUMBER FORMAT EXCEPTION");
            e.printStackTrace();
            return 0l; //if something went wrong then just return 0
        }

        // calculate the time
        return (h + m + s);
    }

    // get elapsed time using strings
    public static String getElapsedTime(String startTimeString, String finishTimeString) {
        // convert string to date times
        DateTime startTime = toDateTime(startTimeString);
        DateTime finishTime = toDateTime(finishTimeString);
        // calculate difference in time
        long millis = finishTime.getMillis() - startTime.getMillis();
        //format as string 00:00:00
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    // get elapsed time in seconds using DateTimes
    public static long getElapsedTime(DateTime startTimeString, DateTime finishTimeString) {

        return finishTimeString.getMillis() - startTimeString.getMillis();
    }

    //calculate PHRF and Penalty adjustment
    public static String calculateAdjDuration(int PHRF, long durationInMillis, double penalty,
                                              double distance, double correctionFactor) {
        String result;
        penalty = penalty / 100;
        //calculate Time Allowance in Milliseconds
        long taInMillis = (long) ((distance * PHRF * correctionFactor) * 1000);
        // Calculate the duration less the time allowance
        long tmpAdjDuration = durationInMillis - taInMillis;
        //apply any penalty
        long adjDurationWPenalty = (long) (tmpAdjDuration + (tmpAdjDuration * penalty));

        //convert result to formatted duration

        result = convertMillisToFormattedTime(adjDurationWPenalty, 0);

        return result;
    }

    //check for commas in chosen text fields. Used for validation because commas will mess up the
    // SQL table when converted to CSV
    public static boolean checkForCommas(Context context, String... textFields) {
        for (String textField : textFields) {
            if (textField.contains(",")) {
                Toast.makeText(context, "Error: Comma(s) found. You cannot use commas in text fields"
                        , Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }


// Boat form content

    // Boat Form getters setters and Clear
    public static String getBoatFormAccessMode() {
        return BoatFormAccessMode;
    }

    //used for establishing what kind of access the user should ahve to the data in the form
    public static void setBoatFormAccessMode(boolean isEditMode) {
        if (isEditMode) {
            BoatFormAccessMode = modeEdit;
        } else {
            BoatFormAccessMode = modeAdd;
        }

    }


    public static long getBoatRowID() {
        return boatRowID;
    }

    public static void setBoatRowID(long boatRowID) {
        GlobalContent.boatRowID = boatRowID;
    }


// Race Form content

    // Race Form getters setters and Clear
    public static String getRaceFormAccessMode() {
        return RaceFormAccessMode;
    }

    //set teh access mode for the race form.
    public static void setRaceFormAccessMode(boolean isEditMode) {
        if (isEditMode) {
            RaceFormAccessMode = modeEdit;
        } else {
            RaceFormAccessMode = modeAdd;
        }
    }


    public static long getRaceRowID() {
        return raceRowID;
    }

    public static void setRaceRowID(long raceRowID) {
        GlobalContent.raceRowID = raceRowID;
    }


    // Results Form getters setters and Clear
    public static String getResultsFormAccessMode() {
        return ResultsFormAccessMode;
    }

    public static void setResultsFormAccessMode(boolean isEditMode) {
        if (isEditMode) {
            ResultsFormAccessMode = modeEdit;
        } else {
            ResultsFormAccessMode = modeAdd;
        }
    }


    public static long getResultsRowID() {
        return resultsRowID;
    }

    public static void setResultsRowID(long resultsRowID) {
        GlobalContent.resultsRowID = resultsRowID;
    }

    //establishes the current active race instance
    public static void setActiveRace(Race activeRace, long id) {
        GlobalContent.activeRace = activeRace;
        activeRace.setId(id);
    }

    //calculate the size of the screen being used.
    public static double getScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        return Math.sqrt(x + y);
    }

    //open the data based manager
    public static void DDMS(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), AndroidDatabaseManager.class);
        context.startActivity(intent);
    }

    //change value into a format that can be used in execSQL statements.
    public static String singleQuotify(String value) {
        if (value != null) {
            return "\'" + value + "\'";
        } else {
            return null;
        }
    }

    //create logcat tag
    public static String logTag(Context context) {
        return context.getClass().getSimpleName();
    }
}
