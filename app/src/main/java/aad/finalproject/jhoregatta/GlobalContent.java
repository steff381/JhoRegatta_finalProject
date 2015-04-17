package aad.finalproject.jhoregatta;

import android.graphics.Color;

/**
 * Created by Daniel on 4/14/2015.
 */
public class GlobalContent {
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

    public static int colorOrange = Color.rgb(255, 100, 0);


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
            RaceFormAccessMode = modeAdd;
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



}
