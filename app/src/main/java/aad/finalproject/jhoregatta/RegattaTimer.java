package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static aad.finalproject.jhoregatta.R.id.imgNextFlag;


public class RegattaTimer extends SelectBoats {

    static String LOGTAG = "LogTag: TimeTracker "; // default log tag
    Button masterStart; // start button instance
    TextView txtCountDown; // accessible instance of countdown
    TextView displayMasterStartTime; //create instance of the displayed master start time
//    ImageView currentFlag;
    boolean isPrepTime = true;
    int classToDisplay = 0;
    int flagToDisplay = 0;
    int minThree = 0;
    int secThree = 5;
    int minOne = 0;
    int secOne = 2;
//    int flagToDisplayTIME;
//    ImageView currentClass;
//    TableRow currentClassTblRow;

    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
    boolean isStartButton; // If the button currently displayed is the Master Start button
    int maxClasses = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regatta_timer);

        displayMasterStartTime = (TextView) findViewById(R.id.txtMasterStartTime); // get textview instance
        displayMasterStartTime.setText("START TIME"); // initial value of clock display

        masterStart = (Button) findViewById(R.id.btnMasterStart); // create master start button
        masterStart.setText("Start"); // set initial text to "Start"
        txtCountDown = (TextView) findViewById(R.id.txtCountDown); // create instance of the countdown
        isStartButton = true; // set oncreate value to true

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regatta_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void raceFlagSwitcher() {
        ImageView currentFlagImage = (ImageView) findViewById(R.id.imgCurrentFlag); // wire to xml
        ImageView nextFlagImage = (ImageView) findViewById(imgNextFlag); // wire flag to xml
        Log.i(LOGTAG, " Switchcase raceFlagSwitcher SWITCHCASE " + flagToDisplay);
        switch (flagToDisplay) {
            case 0: // for 0 case show no current flag and display the class up as the next flag
                currentFlagImage.setBackgroundResource(R.drawable.nullflag);
                nextFlagImage.setBackgroundResource(R.drawable.classup);
                flagToDisplay++;
                myCountdownMethod(0,minOne,secOne);
                break;
            case 1:
                currentFlagImage.setBackgroundResource(R.drawable.classup);
                nextFlagImage.setBackgroundResource(R.drawable.prepup);
                flagToDisplay++;
                myCountdownMethod(0,minOne,secOne);
                break;
            case 2:
                currentFlagImage.setBackgroundResource(R.drawable.prepup);
                nextFlagImage.setBackgroundResource(R.drawable.prepdown);
                flagToDisplay++;
                myCountdownMethod(0,minThree,secThree);
                break;
            case 3:
                currentFlagImage.setBackgroundResource(R.drawable.prepdown);
                nextFlagImage.setBackgroundResource(R.drawable.classdown);
                flagToDisplay++;
                myCountdownMethod(0,minOne,secOne);
                break;
            case 4:
                currentFlagImage.setBackgroundResource(R.drawable.classdown);
                currentFlagImage.setBackgroundResource(R.drawable.classup);
                flagToDisplay++;
                myCountdownMethod(0,minOne,secOne);
                break;
            case 555:
                currentFlagImage.setBackgroundResource(R.drawable.nullflag);
                currentFlagImage.setBackgroundResource(R.drawable.nullflag);
            default:
                Log.i(LOGTAG, " Switchcase raceFlagSwitcher ended in default");

                break;

        }
    }


    private void setRaceStartByClass() {
        TableRow currentClassColor = (TableRow) findViewById(R.id.tblrowCurrentClass);
        TextView currentCaseClassStartTime;
        Calendar cal = Calendar.getInstance();
        String timeFormatted = timeFormat.format(cal.getTime());
        Log.i(LOGTAG, " Switchcase setRaceStartByClass SWITCHCASE " +classToDisplay);
        switch (classToDisplay) {

            case 0:

                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegPURPLE));
                classToDisplay++;
                break;
            case 1:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass1StartTime);
                currentCaseClassStartTime.setText(timeFormatted);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegBLUE));
                classToDisplay++;
                break;
            case 2:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass2StartTime);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegRED));
                currentCaseClassStartTime.setText(timeFormatted);
                classToDisplay++;
                break;
            case 3:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass3StartTime);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegGREEN));
                currentCaseClassStartTime.setText(timeFormatted);
                classToDisplay++;
                break;
            case 4:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass4StartTime);
                currentCaseClassStartTime.setText(timeFormatted);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegYELLOW));
                classToDisplay++;
                break;
            case 5:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass5StartTime);
                currentCaseClassStartTime.setText(timeFormatted);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.Reg_TBD_));
                classToDisplay++;
                break;
            case 6:
                currentCaseClassStartTime = (TextView) findViewById(R.id.txtClass6StartTime);
                currentCaseClassStartTime.setText(timeFormatted);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.white));
                classToDisplay = 999999;
                break;
            default:
                Log.i(LOGTAG, " Switchcase setRaceStartByClass ended in default");
                break;
        }
    }


    // method to switch between timer start button and pause button
    private void masterStartButtonSwitcher() {
        if (isStartButton) {
            masterStart.setText("Pause"); // change text of button
            isStartButton = false; // change status of button to NOT start
            Log.i(LOGTAG, "isStartButton = " + isStartButton);
        } else if (!isStartButton) {
            masterStart.setText("Start");// change text of button
            isStartButton = true; // change status of button to is start
            Log.i(LOGTAG, "isStartButton = " + isStartButton);
        }
    }

    private void myCountdownMethod(int hours, int minutes, int seconds) {
        //convert hours mins and seconds to milliseconds
        final int milliHours = hours*3600000;
        final int milliMinutes = minutes*60000;
        final int milliSeconds = seconds*1000;
        // add all milliseconds up
        int time = milliHours + milliMinutes + milliSeconds;

        new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //convert miliseconds into hh:mm:ss
                String timeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                txtCountDown.setText(timeString); // Update text to display current time remaining
//                Log.i(LOGTAG, "CountDownTimer: OnTick " + timeString);
            }

            @Override
            public void onFinish() {
                Log.i(LOGTAG, "CountDownTimer: flagToDisplay is: " + flagToDisplay);
                Log.i(LOGTAG, "CountDownTimer: classToDisplay is: " + classToDisplay);
                if (isPrepTime) {
                    TableRow currentClassColor = (TableRow) findViewById(R.id.tblrowCurrentClass);
                    currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegORANGE));
//                    setRaceStartByClass();
                    isPrepTime = false;
                    myCountdownMethod(0,0,secOne);
                    Log.i(LOGTAG, "CountDownTimer: OnFinish: isPrepTime");
                } else {

                    if (classToDisplay > maxClasses) {
                        //basically stop doing stuff
                        Log.i(LOGTAG, "CountDownTimer: OnFinish: Stopped everything");
                    } else if (flagToDisplay >= 5) {
                        setRaceStartByClass(); // change the class flag and add start time to boat class display
                        flagToDisplay = 1; // reset the race flag to 1 (Class flag up)
                        raceFlagSwitcher(); // use the race flag switch case
                        Log.i(LOGTAG, "CountDownTimer: OnFinish: Reset flagToDisplay at 1");
                    } else {
                        raceFlagSwitcher(); // use the race flag switch case
                    }

                    Log.i(LOGTAG, "CountDownTimer: OnFinish");
                }
            }
        }.start();
    }



    public void onClickMasterStart(View view) {
        myCountdownMethod(0,0,2);

        if (isStartButton) {
            flagToDisplay=0;
            classToDisplay=0;
            Calendar cal = Calendar.getInstance();
            masterStartButtonSwitcher();
            String timeFormatted = timeFormat.format(cal.getTime());
            Log.i(LOGTAG, "Current time is " + timeFormatted);
            displayMasterStartTime.setText(timeFormatted);
//        Calendar cal = Calendar.getInstance();
//        Log.i(LOGTAG, "Calendar's toString is " + cal);
        } else {
            //TODO: Add pause functionality
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Are you sure you want to reset?");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    masterStartButtonSwitcher();
                }
            });

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.show();
        }
    }
}
