package aad.finalproject.jhoregatta;

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
}
