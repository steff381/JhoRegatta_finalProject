package aad.finalproject.db;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/11/2015.
 */
public class BoatListClass {
    public static final ArrayList<Boat> boatList = new ArrayList<Boat>();
    public static final ArrayList<Boat> selectedBoatsList = new ArrayList<Boat>();

    public static void clearSelectedBoatsList() {
        selectedBoatsList.clear();
    }

    public static void setSelectedBoats() {
        for (Boat boat : BoatListClass.boatList) {
            if (boat.isSelected()) {
                BoatListClass.selectedBoatsList.add(boat);
            }
        }
    }


}
