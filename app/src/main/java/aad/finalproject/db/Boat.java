package aad.finalproject.db;

/*
 class containing all data that is manipulated for teh boat lass
 */
public class Boat {

    long id;
    long boatId;
    String boatName;
    String boatSailNum;
    String boatClass;
    int boatPHRF;
    int boatVisible;
    private boolean selected = false;
    int isSelected;

    public int getBoatVisible() {
        return boatVisible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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

    public void setBoatVisible(int boatVisible) {
        this.boatVisible = boatVisible;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public long getBoatId() {
        return boatId;
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }
}
