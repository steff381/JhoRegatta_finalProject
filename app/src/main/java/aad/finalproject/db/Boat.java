package aad.finalproject.db;

/**
 * Created by Daniel on 3/28/2015.
 */
public class Boat {

    long id;
    String boatName;
    String boatSailNum;
    String boatClass;
    int boatPHRF;
    int boatVisible = 1;
    private boolean selected;
    String boatCreateDate = DBAdapter.getDateTime();

    public int getBoatVisible() {
        return boatVisible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getBoatCreateDate() {
        return boatCreateDate;
    }

    public String getBoatSailNum() {
        return boatSailNum;
    }

    public void setBoatSailNum(String boatSailNum) {
        this.boatSailNum = boatSailNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public String getBoatClass() {
        return boatClass;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public int getBoatPHRF() {
        return boatPHRF;
    }

    public void setBoatPHRF(int boatPHRF) {
        this.boatPHRF = boatPHRF;
    }

    public int isBoatVisible() {
        return boatVisible;
    }

    public void setBoatVisible(int boatVisible) {
        this.boatVisible = boatVisible;
    }






}
