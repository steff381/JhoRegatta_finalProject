package aad.finalproject.jhoregatta;

import android.os.CountDownTimer;
import android.util.Log;

public abstract class CountDownTimerPausable {

    private static String LOGTAG = "LogTag: CountDownTimerPausable: ";
    long millisInFuture = 0;
    long countDownInterval = 0;
    long millisRemaining =  0;

    CountDownTimer countDownTimer = null;

    boolean isPaused = true;

    public CountDownTimerPausable(long millisInFuture, long countDownInterval) {
        super();
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.millisRemaining = this.millisInFuture;

    }


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
                Log.i(LOGTAG,  Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + " XXX RRR ---------------- onFinish Counter# " + this.hashCode());

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
            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " XXX RRR ---------------- Cancelled Counter# "  + this.hashCode());
        }
        this.millisRemaining = 0;
    }
    /**
     * Start or Resume the countdown.
     * @return CountDownTimerPausable current instance
     */
    public synchronized final CountDownTimerPausable start(){
        if(isPaused){
            createCountDownTimer();
            countDownTimer.start();
            isPaused = false;
            RegattaTimer.masterPause.setText("Pause");
            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + " XXX RRR ---------------- Resuming Counter# "  + this.hashCode());
        } else{
            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + " XXX RRR ---------------- Starting Counter# "  + this.hashCode());
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
            RegattaTimer.masterPause.setText("Resume");
            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " XXX RRR ---------------- Paused Counter# "  + this.hashCode());

        } else{
            throw new IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.");
        }
        isPaused = true;
    }
    public boolean isPaused() {
        return isPaused;
    }
}
