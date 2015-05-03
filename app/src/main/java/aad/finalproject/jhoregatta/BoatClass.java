package aad.finalproject.jhoregatta;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Daniel on 4/12/2015.
 */
public class BoatClass {

    private String startTime;
    private double classDistance;
    private String boatColor;
    private int classColorSolid;
    private int classColorLite;
    private int image;

    public int getImage() {
        return image;
    }


    public String getBoatColor() {
        return boatColor;
    }



    // instance with color only
    public BoatClass(String color) {
        setClassColor(color);
    }



    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public double getClassDistance() {
        return classDistance;
    }

    public void setClassDistance(double classDistance) {
        this.classDistance = classDistance;
    }

    public int getClassColorSolid() {
        return classColorSolid;
    }

    public void setClassColor(String classColor) {
        switch (classColor) {
            case "Classless":
                boatColor = "Classless";
                this.classColorSolid = Color.rgb(255, 0, 0);
                this.classColorLite = Color.argb(60, 255, 0, 0);
                image = R.drawable.red_class;
                break;
            case "Blue":
                boatColor = "Blue";
                this.classColorSolid = Color.rgb(30, 30, 160);
                this.classColorLite = Color.argb(60, 30, 30, 160);
                image = R.drawable.blue_class;
                break;
            case "Green":
                boatColor = "Green";
                this.classColorSolid = Color.rgb(17, 190, 0);
                this.classColorLite = Color.argb(60, 17, 190, 0);
                image = R.drawable.green_class;
                break;
            case "Purple":
                boatColor = "Purple";
                this.classColorSolid = Color.rgb(160, 0, 255);
                this.classColorLite = Color.argb(60, 160, 0, 255);
                image = R.drawable.purple_class;
                break;
            case "Yellow":
                boatColor = "Yellow";
                this.classColorSolid = Color.rgb(235, 215, 0);
                this.classColorLite = Color.argb(60, 235, 215, 0);
                image = R.drawable.yellow_class;
                break;
            case "_TBD_":
                boatColor = "_TBD_";
                this.classColorSolid = Color.rgb(255, 0, 255);
                this.classColorLite = Color.argb(60, 255, 0, 255);
                image = R.drawable.tbd_class;
                break;
            default:
                Log.i("ClassDisplayProperties ", "SWITCHCASE FAILED!");
        }
    }

    public int getClassColorLite() {
        return classColorLite;
    }


}
