package aad.finalproject.db;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/11/2015.
 */
public class BoatListClass {
    private static String LOGTAG = BoatListClass.class.getSimpleName() + ": ";

    public static final ArrayList<Boat> boatList = new ArrayList<Boat>();
    public static ArrayList<Boat> selectedBoatsList = new ArrayList<Boat>();

    public static void clearSelectedBoatsList() {
        selectedBoatsList.clear();
    }

    public static void setSelectedBoats() {
        Log.i(LOGTAG, "Setting selected boats\n");

        for (int i = 0; i < boatList.size(); i++) {
            Log.i(LOGTAG, " Boat list entry is now " + boatList.get(i).getBoatName()
                    + " and it isSelected = "
                    + boatList.get(i).isSelected());
            Boat boat = boatList.get(i);
            if (boat.isSelected()) {
                selectedBoatsList.add(boat);
                Log.i(LOGTAG, "Adding " + boat.getBoatName() + " to the selected boat list");
            }
        }
        Log.i(LOGTAG, "# of Selected Boats is " + selectedBoatsList.size());

    }

    public static void getSelectedBoats() {
        // For testing purposes!!!
        int k = 1;
        for (Boat boat : selectedBoatsList) {
            Log.i(LOGTAG, " SelectedBoatList item " + k + " is " + boat.getBoatName());
            k++;
        }
    }


}
