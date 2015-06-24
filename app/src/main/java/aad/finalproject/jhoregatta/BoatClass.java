package aad.finalproject.jhoregatta;

import android.graphics.Color;
import android.util.Log;

import org.joda.time.DateTime;

/*
This class handles teh boatclass fields
It isn't associated with the DB only references by other classes.
 */
public class BoatClass {

    private static final String LOGTAG = "BoatClass";

    private String startTime;
    private String firstFinish;
    private double classDistance;
    private String boatColor;
    private int classColorSolid;
    private int classColorLite;
    private int image;
    public Runnable callAtRunnable;

    public DateTime getFirstFinishAsDateTime() {
        if (this.firstFinish == null) {
            return null;
        } else {
            return GlobalContent.toDateTime(firstFinish);
        }
    }

    public String getFirstFinishAsString() {
        return firstFinish;
    }

    // check if the time of first finish
    public boolean checkAndSetFirstFinish(String finishString) {
        // check for a first finish
        if (this.firstFinish == null && finishString != null) {
            this.firstFinish = finishString;
            Log.i(LOGTAG, "FIRST NULL CASE> Class: " + this.boatColor + ", Start Time is now: "
                    + this.firstFinish);
            return true;
            // check if finish string is not null
        } else if (finishString != null) {

            DateTime checkedFirstFinish = GlobalContent.toDateTime(finishString);
            DateTime currentFirstFinish = GlobalContent.toDateTime(this.firstFinish);
            // compare the current finish time on record with the new time.
            if (checkedFirstFinish.getMillis() < currentFirstFinish.getMillis()) {
                // if new time occured before current time, replace current time with new time
                this.firstFinish = GlobalContent.dateTimeToString(checkedFirstFinish);
                Log.i(LOGTAG, "COMPARED CASE> Class: " + this.boatColor + ", Start Time is now: "
                        + this.firstFinish);
                return true;
            } else {
                Log.i(LOGTAG, "NO CHANGE (COMPARED) CASE> Class: " + this.boatColor + ", Start Time is now: "
                        + this.firstFinish);
                return false;
            }
        } else {
            Log.i(LOGTAG, "NO CHANGE CASE> Class: " + this.boatColor + ", Start Time is now: "
                    + this.firstFinish);
            return false;
        }
    }

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

    //each boatclass has its own color and among other properties. choosing a class color
    // automatically assigns other boatclass characteristics
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
