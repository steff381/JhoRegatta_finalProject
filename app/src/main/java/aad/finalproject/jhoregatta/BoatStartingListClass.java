package aad.finalproject.jhoregatta;

import org.joda.time.DateTime;

import java.util.ArrayList;

/*
This class handles the boatclass' that are selected for use in the regatta timer.
 */
public class BoatStartingListClass {
    public static ArrayList<BoatClass> BOAT_CLASS_START_ARRAY = new ArrayList<>();

    //blank out the start times for each class. This is called when a total reset is requested
    public static void resetAllClassStartTimes() {
        for (BoatClass b : BOAT_CLASS_START_ARRAY) {
            b.setStartTime(null); //blank out the start time
        }
    }

    //individually add boatclasses to the array list based on the color of the boatclass
    public static void addToBoatClassStartArray(String boatColor) {
        BoatClass bc = new BoatClass(boatColor);
        BOAT_CLASS_START_ARRAY.add(bc);
    }

    // get the first finish time for the class color
    public static DateTime getClassFinishTime(String classColor) {
        // run through the array for the start time.
        for (BoatClass bc : BOAT_CLASS_START_ARRAY) {
            //compare color to boat class's color
            if (bc.getBoatColor().equals(classColor)) {
                // return the class's start time
                return bc.getFirstFinishAsDateTime();
            }
        }
        return null;
    }
}
