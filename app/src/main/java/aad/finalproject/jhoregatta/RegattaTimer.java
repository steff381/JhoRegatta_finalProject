package aad.finalproject.jhoregatta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static aad.finalproject.jhoregatta.R.id.imgNextFlag;


public class RegattaTimer extends MainActivity {

    private static String LOGTAG = "LogTag: TimeTracker "; // default log tag
    private Button masterStart; // start button instance
    private TextView txtCountDown; // accessible instance of countdown
    private TextView displayMasterStartTime; //create instance of the displayed master start time
    private boolean isPrepTime;
    private int classToDisplay;
    private int flagToDisplay;

    private ArrayList<LinearLayout> visibleClasses = new ArrayList<>();
    private ArrayList<FrameLayout> colorBlockFLs = new ArrayList<>();
    private ArrayList<FrameLayout> contentBlockLFs = new ArrayList<>();
    private ArrayList<ClassDisplayProperties> classDisplayProperties = new ArrayList<>();
    private ArrayList<TextView> boatClassNames= new ArrayList<>();


    //used for testing the app. mins and seconds will be adjusted to meet timer needs on final version
    //TODO: Remove references to these variables with actual times.
    int minThree = 0;
    int secThree = 5;
    int minOne = 0;
    int secOne = 2;

    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a"); // standard time format
    boolean isStartButton; // If the button currently displayed is the Master Start button
    int maxClasses; // initialize the maxClasses variable

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
        isPrepTime = true; // preptime is true when class activity begins
        classToDisplay = 0;
        flagToDisplay = 0;



        // load up linear layouts for manipulation

        visibleClasses.add((LinearLayout)findViewById(R.id.linlay1));
        visibleClasses.add((LinearLayout)findViewById(R.id.linlay2));
        visibleClasses.add((LinearLayout)findViewById(R.id.linlay3));
        visibleClasses.add((LinearLayout)findViewById(R.id.linlay4));
        visibleClasses.add((LinearLayout)findViewById(R.id.linlay5));
        visibleClasses.add((LinearLayout)findViewById(R.id.linlay6));

        //set default visability to invisible
        for (int i = 0; i < visibleClasses.size(); i++ ) {
            visibleClasses.get(i).setVisibility(View.INVISIBLE);
        }

        //load up table layouts
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame1));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame2));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame3));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame4));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame5));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame6));

        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame1));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame2));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame3));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame4));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame5));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame6));

        boatClassNames.add((TextView) findViewById(R.id.txtClassName1));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName2));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName3));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName4));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName5));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName6));

        testCreateClassDisplayProperties();
        classTableRowBuilder(colorBlockFLs, contentBlockLFs, boatClassNames, classDisplayProperties);
        //TODO: Programmatically change the max number of classes based on count of included classes
        maxClasses = classDisplayProperties.size(); // set the number of classes that the user wishes to have in the timer
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

    private void testCreateClassDisplayProperties(){
        classDisplayProperties.add(new ClassDisplayProperties("_TBD_"));
        classDisplayProperties.add(new ClassDisplayProperties("Yellow"));
        classDisplayProperties.add(new ClassDisplayProperties("Green"));
//        classDisplayProperties.add(new ClassDisplayProperties("Red"));
//        classDisplayProperties.add(new ClassDisplayProperties("Blue"));
        classDisplayProperties.add(new ClassDisplayProperties("Purple"));
    }

    private void classTableRowBuilder(ArrayList<FrameLayout> colorFL, ArrayList<FrameLayout> contentFL,
                                      ArrayList<TextView> boatClassNameTV,
                                      ArrayList<ClassDisplayProperties> cdp) {
        for (int i = 0; i < cdp.size(); i++ ) {
            Log.i(LOGTAG, " For loop # " + i);
            colorFL.get(i).setBackgroundColor(Color.argb(cdp.get(i).aBlk, cdp.get(i).r, cdp.get(i).g, cdp.get(i).b));
            contentFL.get(i).setBackgroundColor(Color.argb(cdp.get(i).aCon, cdp.get(i).r, cdp.get(i).g, cdp.get(i).b));
            boatClassNameTV.get(i).setText(cdp.get(i).name);
            visibleClasses.get(i).setVisibility(View.VISIBLE);
        }

    }


    // method that changes the displayed flag on the GUI
    private void raceFlagSwitcher() {
        ImageView currentFlagImage = (ImageView) findViewById(R.id.imgCurrentFlag); // wire to xml
        ImageView nextFlagImage = (ImageView) findViewById(imgNextFlag); // wire flag to xml
        Log.i(LOGTAG, " Switchcase raceFlagSwitcher SWITCHCASE " + flagToDisplay);
        //each case sets the Current flag and the Next flag, increments the counter for the next run
        // then calls the countdown method with the appropriate time interval for the flag set
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
                myCountdownMethod(0,minOne,secOne);
                break;
            case 3:
                currentFlagImage.setBackgroundResource(R.drawable.prepdown);
                nextFlagImage.setBackgroundResource(R.drawable.classdown);
                flagToDisplay++;
                myCountdownMethod(0,minOne,secOne);
                break;
            case 4:
                currentFlagImage.setBackgroundResource(R.drawable.classdown);
                nextFlagImage.setBackgroundResource(R.drawable.classup);
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
        Log.i(LOGTAG, " Switchcase setRaceStartByClass SWITCHCASE " + classToDisplay);
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
        int milliHours = hours*3600000;
        int milliMinutes = minutes*60000;
        int milliSeconds = seconds*1000;
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
            Calendar cal = Calendar.getInstance();
            masterStartButtonSwitcher();
            String timeFormatted = timeFormat.format(cal.getTime());
            displayMasterStartTime.setText(timeFormatted);
            Log.i(LOGTAG, "Current time is " + timeFormatted);
        } else {
            //TODO: Add pause functionality
            verifyIntentToResetMasterTime();
        }
    }

    private void verifyIntentToResetMasterTime() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to reset?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flagToDisplay=0;
                classToDisplay=0;
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
