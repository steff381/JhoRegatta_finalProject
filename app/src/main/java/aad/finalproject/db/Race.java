package aad.finalproject.db;

/*
This class defines the fields of a race
 */
public class Race {

    long id;
    String name;
    String date;
    double distance;
    int clsBlue;
    int clsGreen;
    int clsPurple;
    int clsYellow;
    int clsRed;
    int cls_TBD_;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int isClsBlue() {
        return clsBlue;
    }

    public void setClsBlue(int clsBlue) {
        this.clsBlue = clsBlue;
    }

    public int isClsGreen() {
        return clsGreen;
    }

    public void setClsGreen(int clsGreen) {
        this.clsGreen = clsGreen;
    }

    public int isClsPurple() {
        return clsPurple;
    }

    public void setClsPurple(int clsPurple) {
        this.clsPurple = clsPurple;
    }

    public int isClsYellow() {
        return clsYellow;
    }

    public void setClsYellow(int clsYellow) {
        this.clsYellow = clsYellow;
    }

    public int isClsRed() {
        return clsRed;
    }

    public void setClsRed(int clsRed) {
        this.clsRed = clsRed;
    }

    public int isCls_TBD_() {
        return cls_TBD_;
    }

    public void setCls_TBD_(int cls_TBD_) {
        this.cls_TBD_ = cls_TBD_;
    }



}
