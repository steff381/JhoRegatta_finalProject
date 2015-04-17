package aad.finalproject.db;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/11/2015.
 */
public class BoatListClass {
    private static String LOGTAG = BoatListClass.class.getSimpleName() + ": ";

    public static final ArrayList<Boat> boatList = new ArrayList<Boat>(); // all boats in list
    public static ArrayList<Boat> selectedBoatsList = new ArrayList<Boat>(); // only the selected boats

    //clear the selected boats list array
    public static void clearSelectedBoatsList() {
        selectedBoatsList.clear();
    }

    // parse through all boat list for boats that are selected and add them to selected boat list
    public static void setSelectedBoats() {
        Log.i(LOGTAG, "Setting selected boats\n");

        for (int i = 0; i < boatList.size(); i++) {
            //TODO: testing, print out the current boat
            Log.i(LOGTAG, " Boat list entry is now " + boatList.get(i).getBoatName()
                    + " and it isSelected = "
                    + boatList.get(i).isSelected());
            Boat boat = boatList.get(i); // create an instance of the current boat
            if (boat.isSelected()) { // when the boat is selected add to the list
                selectedBoatsList.add(boat);
                //TODO testing, to show that the boat is being added to the list
                Log.i(LOGTAG, "Adding " + boat.getBoatName() + " to the selected boat list");
            }
        }
        // TODO: testing confirm the size of the final list
        Log.i(LOGTAG, "# of Selected Boats is " + selectedBoatsList.size()); // output the size

    }

    //TODO: Get rid of testing method
    public static void getSelectedBoats() {
        // For testing purposes!!!
        int k = 1;
        for (Boat boat : selectedBoatsList) {
            Log.i(LOGTAG, " SelectedBoatList item " + k + " is " + boat.getBoatName());
            k++;
        }
    }


}
