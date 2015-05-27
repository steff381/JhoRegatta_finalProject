package aad.finalproject.jhoregatta;

import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Dimmer extends MainActivity {
    private static final String LOGTAG = "Dimmer";

    private long dimDel;
    private Window w;
    private Handler h;
    private Runnable r;
    private WindowManager.LayoutParams lp;

    private float dimmedValue = 0.01f;
    private boolean isStarted = false;


    //Instance constructor method
    public Dimmer(Window window, long dimmerDelayMillis) {
        this.dimDel = dimmerDelayMillis;
        this.lp = window.getAttributes();
        this.w = window;
    }

    //start the dimmer activity
    public void start() {
        //if the activity hasn't started yet then proceed to start
        if (!isStarted) {
            isStarted = true;
            //force screen into a constant wakeful state
            w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setBrightness(false);
            h = new Handler(); // create a handler
            //make a runnable activity to schedule
            r = new Runnable() {
                @Override
                public void run() {
                    setBrightness(true); // reduce brightness
                }
            };
            h.postDelayed(r, dimDel); //schedule the dimmer to run after a delay
            Log.i(LOGTAG, "Starting Dimmer");
        }
    }

    //end the dimmer activity
    public void end() {
        //check if the event scheduled event is running
        if (isStarted) {
            Log.i(LOGTAG, "Ending Dimmer");
            isStarted = false; // set started property to false
            setBrightness(false); // set the screen brightness to max
            h.removeCallbacks(r); // clear the scheduled task
            //remove screen wakeful state
            w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //suspend the dimmer activity
    // screen stays on and bright
    public void suspend() {
        //check if the event scheduled event is running
        if (isStarted) {
            Log.i(LOGTAG, "Suspending Dimmer");
            isStarted = false; // set started property to false
            setBrightness(false); // set the screen brightness to max
            h.removeCallbacks(r); // clear the scheduled task
            //keep the screen on
            w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void setBrightness(boolean isDimmed) {
        if (isDimmed) {
            lp.screenBrightness = dimmedValue;// set the appropriate brightness level
        } else {
            //reset the illumination mode to auto brightness
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }
        w.setAttributes(lp); // assign brightness to the window.
    }

    public void resetDelay() {
        if (isStarted) {
            setBrightness(false); // set the screen brightness to max
            h.removeCallbacks(r); // clear the scheduled run task
            h.postDelayed(r, dimDel); // re-assign the scheduled run task
        }
    }


}
