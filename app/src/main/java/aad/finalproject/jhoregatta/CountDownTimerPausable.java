package aad.finalproject.jhoregatta;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

/*
This is a drop in code from Github that is just a pausable version of the countdown timer interface.
 */
public abstract class CountDownTimerPausable {

    private static String LOGTAG = "LogTag: CountDownTimerPausable: ";
    //timer elements
    long millisInFuture = 0;
    long countDownInterval = 0;
    long millisRemaining =  0;

    //instance of the countdown timer
    CountDownTimer countDownTimer = null;

    //status of the timer variable
    boolean isPaused = true; //initially true, not sure why

    public CountDownTimerPausable(long millisInFuture, long countDownInterval) {
        super();
        //create a special instance of the timer with new variables
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.millisRemaining = this.millisInFuture;

    }

    //default constructor fromm super class
    private void createCountDownTimer(){
        countDownTimer = new CountDownTimer(millisRemaining,countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                CountDownTimerPausable.this.onTick(millisUntilFinished);

            }

            @Override
            public void onFinish() {
                CountDownTimerPausable.this.onFinish();

            }
        };
    }
    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);
    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();
    /**
     * Cancel the countdown.
     */
    public final void cancel(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
            startResumeButtonMethod();

        }
        this.millisRemaining = 0;
    }
    /**
     * Start or Resume the countdown.
     * @return CountDownTimerPausable current instance
     */
    public synchronized final CountDownTimerPausable start(){
        if(isPaused){
            //build and start the timer.
            createCountDownTimer();
            countDownTimer.start();
            isPaused = false;
            //when the timer is resumed or started change text back to "Start" and hide it
            Log.i(LOGTAG, "Timer Starting");
            //switch the text of the button in the regatta timer to "Start'
            RegattaTimer.startResume.setText("Start");
            RegattaTimer.startResume.setVisibility(View.INVISIBLE);
        } else{
            //not sure why this is left blank
        }
        return this;
    }
    /**
     * Pauses the CountDownTimerPausable, so it could be resumed(start)
     * later from the same point where it was paused.
     */
    public void pause()throws IllegalStateException{
        if(!isPaused){
            countDownTimer.cancel();
            Log.i(LOGTAG, "Timer Pausing");
            startResumeButtonMethod(); //swtich from sstart to resume button

        } else{
            throw new IllegalStateException("CountDownTimerPausable is already in pause state," +
                    " start counter before pausing it.");
        }
        isPaused = true;
    }

    private void startResumeButtonMethod() {
        // make the start resume button visible and change the text to "Resume"
        RegattaTimer.startResume.setVisibility(View.VISIBLE);
        RegattaTimer.startResume.setText("Resume");
    }

    public boolean isPaused() {
        return isPaused; //tell the state of the timer.
    }
}
