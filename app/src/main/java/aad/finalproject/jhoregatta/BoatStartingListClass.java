package aad.finalproject.jhoregatta;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/13/2015.
 */
public class BoatStartingListClass {
    public static ArrayList<BoatClass> BOAT_CLASS_START_ARRAY = new ArrayList<>();


    public static void addAllToBoatClassStartArray() {

    }

    public static void addToBoatClassStartArray(BoatClass boatClass, int startOrder) {
        BOAT_CLASS_START_ARRAY.add(startOrder, boatClass);
    }

    public static void addToBoatClassStartArray(String boatColor) {
        BoatClass bc = new BoatClass(boatColor);
        BOAT_CLASS_START_ARRAY.add(bc);
    }

    public static void removeBoatFromBoatClassStartArray(int boatIndexLocation) {
        BOAT_CLASS_START_ARRAY.remove(boatIndexLocation);
    }

    public static void clearBoatClassStartArray() {
        BOAT_CLASS_START_ARRAY.clear();
    }

    public static void moveBoatToLast(int currentLocationIndex) {
        BoatClass tmpBoatClass;
        tmpBoatClass = BOAT_CLASS_START_ARRAY.get(currentLocationIndex);
        BOAT_CLASS_START_ARRAY.remove(currentLocationIndex);
        BOAT_CLASS_START_ARRAY.add(tmpBoatClass);
    }


}
