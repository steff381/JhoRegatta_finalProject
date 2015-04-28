package aad.finalproject.jhoregatta;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

import aad.finalproject.db.Race;
import aad.finalproject.db.ResultsAdapter;

/**
 * Created by Daniel on 4/14/2015.
 */
public class GlobalContent {

    private static final String LOGTAG = "Logtag: " + Thread.currentThread()
            .getStackTrace()[2].getClassName(); // log tag for records

    public static Race activeRace;
    public static ResultsAdapter activeResultsAdapter = null;

    private static String BoatFormAccessMode;
    private static String RaceFormAccessMode;
    private static String ResultsFormAccessMode;

    private static long boatRowID;
    private static long raceRowID;
    private static long resultsRowID;

    public static String modeAdd = "ADD";
    public static String modeEdit = "EDIT";

    private static int secondsUntilOrangeFlagUp;
    private static int secondsUntilOrangeFlagDown;
    private static int secondsUntilClassFlagUp;
    private static int secondsUntilPrepFlagUp;
    private static int secondsUntilPrepFlagDown;
    private static int secondsUntilClassFlagDown;
    private static int secondsUntilRestartFromRecall;

    // Joda time formatters. Used by all applications for consistancy of date time formats
    private static DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("hh:mm:ss a");
    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static String globalWhere; // where clause to be used by multiple interfaces

    // Time handling methods
    // from string to local time
    public static DateTime toDateTime(String time) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm:ss a");
        return formatter.parseDateTime(time);
    }

    // from string to local date
    public static LocalDate toLocalDate(String date) {
        Log.i(LOGTAG, "Converting time: " + date + " to LocalTime: " + LocalDate.parse(date));
        return LocalDate.parse(date);
    }

    // from local time to string format
    public static String dateTimeToString(DateTime dateTime) {
        return dateTime.toString(timeFormatter);
    }

    // get elapsed time in milliseconds
    public static String convertMillisToElapsedTime(long millis) {
        //format as string
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
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
        h = 3600000 * Long.parseLong(splitTime[0]);
        m = 60000 * Long.parseLong(splitTime[1]);
        s = 1000 * Long.parseLong(splitTime[2]);

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
        //format as string
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    //calculate PHRF and Penalty adjustment
    public static String calculateAdjDuration(int PHRF, long durationInMillis, double penalty,
                                              double distance, double correctionFactor) {
        String result = null;
        penalty = penalty/100;
        //calculate Time Allowance in Milliseconds
        long taInMillis = (long) ((distance * PHRF * correctionFactor / 60) * 60 * 1000);
        Log.i(LOGTAG, "TA in Millis " + taInMillis);
        // Calculate the duration less the time allowance
        long tmpAdjDuration = durationInMillis - taInMillis;
        Log.i(LOGTAG, "AdjDuration pre penalty " + tmpAdjDuration);
        //apply any penalty
        long adjDurationWPenalty = (long)(tmpAdjDuration - (tmpAdjDuration * penalty));
        Log.i(LOGTAG, "AdjDuration with penalty " + tmpAdjDuration);

        //convert result to formatted duration

        result = convertMillisToElapsedTime(adjDurationWPenalty);
        Log.i(LOGTAG, "AdjDuration with penalty formatted " + result);

        return result;
    }

    //Calculate penalty

    // Time tracker content

    // Tracker getters and setters
    public static int getSecondsUntilOrangeFlagUp() {
        return secondsUntilOrangeFlagUp;
    }

    public static void setSecondsUntilOrangeFlagUp(int secondsUntilOrangeFlagUp) {
        GlobalContent.secondsUntilOrangeFlagUp = secondsUntilOrangeFlagUp;
    }

    public static int getSecondsUntilOrangeFlagDown() {
        return secondsUntilOrangeFlagDown;
    }

    public static void setSecondsUntilOrangeFlagDown(int secondsUntilOrangeFlagDown) {
        GlobalContent.secondsUntilOrangeFlagDown = secondsUntilOrangeFlagDown;
    }

    public static int getSecondsUntilClassFlagUp() {
        return secondsUntilClassFlagUp;
    }

    public static void setSecondsUntilClassFlagUp(int secondsUntilClassFlagUp) {
        GlobalContent.secondsUntilClassFlagUp = secondsUntilClassFlagUp;
    }

    public static int getSecondsUntilPrepFlagUp() {
        return secondsUntilPrepFlagUp;
    }

    public static void setSecondsUntilPrepFlagUp(int secondsUntilPrepFlagUp) {
        GlobalContent.secondsUntilPrepFlagUp = secondsUntilPrepFlagUp;
    }

    public static int getSecondsUntilPrepFlagDown() {
        return secondsUntilPrepFlagDown;
    }

    public static void setSecondsUntilPrepFlagDown(int secondsUntilPrepFlagDown) {
        GlobalContent.secondsUntilPrepFlagDown = secondsUntilPrepFlagDown;
    }

    public static int getSecondsUntilClassFlagDown() {
        return secondsUntilClassFlagDown;
    }

    public static void setSecondsUntilClassFlagDown(int secondsUntilClassFlagDown) {
        GlobalContent.secondsUntilClassFlagDown = secondsUntilClassFlagDown;
    }

// Boat form content

    // Boat Form getters setters and Clear
    public static String getBoatFormAccessMode() {
        return BoatFormAccessMode;
    }

    public static void setBoatFormAccessMode(boolean isEditMode) {
        if(isEditMode){
            BoatFormAccessMode = modeEdit;
        } else {
            BoatFormAccessMode = modeAdd;
        }

    }

    public static void clearBoatFormAccessMode() {
        BoatFormAccessMode = null;
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

    public static void setRaceFormAccessMode(boolean isEditMode) {
        if(isEditMode){
            RaceFormAccessMode = modeEdit;
        } else {
            RaceFormAccessMode = modeAdd;
        }
    }

    public static void clearRaceFormAccessMode() {
        RaceFormAccessMode = null;
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
        if(isEditMode){
            ResultsFormAccessMode = modeEdit;
        } else {
            ResultsFormAccessMode = modeAdd;
        }
    }

    public static void clearResultsFormAccessMode() {
        ResultsFormAccessMode = null;
    }

    public static long getResultsRowID() {
        return resultsRowID;
    }

    public static void setResultsRowID(long resultsRowID) {
        GlobalContent.resultsRowID = resultsRowID;
    }

    public static int getSecondsUntilRestartFromRecall() {
        return secondsUntilRestartFromRecall;
    }

    public static void setSecondsUntilRestartFromRecall(int secondsUntilRestartFromRecall) {
        GlobalContent.secondsUntilRestartFromRecall = secondsUntilRestartFromRecall;
    }

    public static void setActiveRace(Race activeRace, long id) {
        GlobalContent.activeRace = activeRace;
        activeRace.setId(id);
    }
}
