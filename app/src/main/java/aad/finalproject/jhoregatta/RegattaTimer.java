package aad.finalproject.jhoregatta;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import aad.finalproject.db.ResultDataSource;

import static aad.finalproject.jhoregatta.R.id.imgNextFlag;


public class RegattaTimer extends MainActivity {

    private static String LOGTAG = "LogTag: TimeTracker "; // default log tag

    public static String CDT_STARTING = " Timer STARTING ";
    public static String CDT_PAUSING = " Timer PAUSING ";
    public static String CDT_RESUMING = " Timer RESUMING ";
    public static String CDT_STOPPING = " Timer STOPPING ";
    public static String CDT_CALLING = " Timer CALLING ";
    public static String CDT_X = " XXX ";
    public static String CDT_Z = " ZZZ ";
    public static String CDT_XZ = " ZZZ XXX ";

    // create an instance of the result table datasource
    ResultDataSource resultDataSource;


    // media elements
    AssetFileDescriptor afd;
    MediaPlayer player;

    // timing variables
//    Timer myCountDownTimer;
    CountDownTimerPausable myCountDownTimer;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat timeFormat; // standard time format
    String timeString;

    private Button masterStart; // start button instance
    public static Button masterPause; // pause button instance
    private TextView txtCountDown; // accessible instance of countdown
    private int flagToDisplay; // which flag case to implement

    // create array list instances
    private ArrayList<LinearLayout> visibleClasses = new ArrayList<>();
    private ArrayList<FrameLayout> colorBlockFLs = new ArrayList<>();
    private ArrayList<FrameLayout> contentBlockLFs = new ArrayList<>();
    public ArrayList<TextView> currentCaseClassStartTime = new ArrayList<>();
    private ArrayList<TextView> boatClassNames = new ArrayList<>();
    private ArrayList<Button> classRecallButtonArrayList = new ArrayList<>();

    // initialize containers
    LinearLayout linlayClassContainer;
    TableRow currentClassColor;
    ImageView currentFlagImage;
    ImageView nextFlagImage;

    //used for testing the app. mins and seconds will be
    // adjusted to meet timer needs on final version

    int totalTime = 0;

    boolean isStartButton; // If the button currently displayed is the Master Start button
    int numberOfSelectedBoatClasses; // initialize the number of selected Classes variable
    int currentPosition = 0;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regatta_timer);

        // TODO: FOR TESTING ONLY, DEFAULTS SHOULD BE SET IN PROPERTIES MENU
        GlobalContent.setSecondsUntilOrangeFlagUp(5); // 15 seconds *Warn user to prepare orange flag
        GlobalContent.setSecondsUntilOrangeFlagDown(10); // Displayed for 1 min *Instruction: Warn boats of 6 mins to race start
        GlobalContent.setSecondsUntilClassFlagUp(5); //
        GlobalContent.setSecondsUntilPrepFlagUp(5);
        GlobalContent.setSecondsUntilPrepFlagDown(15);
        GlobalContent.setSecondsUntilClassFlagDown(5);

        //wire and open the result datasource
        resultDataSource = new ResultDataSource(this);
        resultDataSource.open();

        wireWidgetsAndAddToArrayLists(); // call the wiring method
        enabledStatusSwitcherRecallButtons(false); // disable all recall buttons

        //assign data to time handling variables
        timeFormat = new SimpleDateFormat("h:mm:ss a");
        timeString = timeFormat.format(cal.getTime());

        // Media play section
        player = new MediaPlayer();

        try {
            afd = getAssets().openFd("Horn_Blast.wav"); // get audio from assets
            //set the players data source elements
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        masterStart = (Button) findViewById(R.id.btnMasterStart); // create master start button
        masterStart.setText("Start"); // set initial text to "Start"
        masterPause = (Button) findViewById(R.id.btnMasterPause); // pause button wired
        // pause button only visible when timer is running.
        masterPause.setVisibility(View.INVISIBLE); // hide pause button

        // start/general recall button state variables
        isStartButton = true; // set oncreate value to true

        // event sequence variable
        flagToDisplay = -1; // set initial value



        //set default visability of each class containing linear layout to invisible. They will
        // be made visible contingent on whether or not they contain an active boat class.
        for (int i = 0; i < visibleClasses.size(); i++) {
            visibleClasses.get(i).setVisibility(View.INVISIBLE);
        }
        //Buttons that handle the class recall need to be assigned periodically to
        //maintain alignment with the array lists
        buildClassRecallOnClickListeners(); // initial assignment of listeners to recall buttons

        classTableRowBuilder(colorBlockFLs, contentBlockLFs, boatClassNames);
        // set the number of classes that the user wishes to have in the timer
        numberOfSelectedBoatClasses = BoatStartingListClass.BOAT_CLASS_START_ARRAY.size();
    }

    // prevent the user from using the devices back button
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void wireWidgetsAndAddToArrayLists() {

        linlayClassContainer = (LinearLayout) findViewById(R.id.linlay_class_container);
        currentFlagImage = (ImageView) findViewById(R.id.imgCurrentFlag); // wire to xml
        nextFlagImage = (ImageView) findViewById(imgNextFlag); // wire flag to xml
        currentClassColor = (TableRow) findViewById(R.id.tblrowCurrentClass);

        // get text view instance for the start time label
        txtCountDown = (TextView) findViewById(R.id.txtCountDown); // create instance of the countdown

        // wire and add individual class recall buttons
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall1));
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall2));
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall3));
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall4));
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall5));
        classRecallButtonArrayList.add((Button) findViewById(R.id.btnClassRecall6));

        // load up linear layouts for manipulation
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay1));
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay2));
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay3));
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay4));
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay5));
        visibleClasses.add((LinearLayout) findViewById(R.id.linlay6));

        //load up table layouts
        // wire and add frame layouts that need solid colors to the color block array
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame1));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame2));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame3));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame4));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame5));
        colorBlockFLs.add((FrameLayout) findViewById(R.id.frmlayColorFrame6));

        // wire and add frame layouts that need lite colors to the lite color block array
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame1));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame2));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame3));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame4));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame5));
        contentBlockLFs.add((FrameLayout) findViewById(R.id.frmlayContentFrame6));

        // wire and add boat class color names for each of the text views
        boatClassNames.add((TextView) findViewById(R.id.txtClassName1));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName2));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName3));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName4));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName5));
        boatClassNames.add((TextView) findViewById(R.id.txtClassName6));

        // wire and add the text boxes that hold the start time for each class.
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass1StartTime));
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass2StartTime));
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass3StartTime));
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass4StartTime));
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass5StartTime));
        currentCaseClassStartTime.add((TextView) findViewById(R.id.txtClass6StartTime));

    }

    // programmatically assign onclick listeners
    // This depends on alignment of boat class start array and each individual class display pane
    private void buildClassRecallOnClickListeners() {
        for (int i = 0; i < BoatStartingListClass.BOAT_CLASS_START_ARRAY.size(); i++) {
            final int arrayPosition = i;
            final Context context = this; //grab context for use in inner methods
            final BoatClass boatClassInstance = BoatStartingListClass
                    .BOAT_CLASS_START_ARRAY.get(i);// grab boat class color for inner methods
            Time startTimeInstance = boatClassInstance.getStartTime(); // grab the start time if any
            if (startTimeInstance != null) {
                Log.i(LOGTAG, " BCROCL: Found a time in Boat Class: " + boatClassInstance.getBoatColor() + " and the time is: " + startTimeInstance);
                //if a start time exists put it in the text box
                currentCaseClassStartTime.get(i).setText(timeFormat.format(startTimeInstance));
            } else {
                Log.i(LOGTAG, " BCROCL: No time in Boat Class: " + boatClassInstance.getBoatColor() + " getStartTime returns: " + startTimeInstance);
                currentCaseClassStartTime.get(i).setText("");
            }
            Log.i(LOGTAG, "Color at " + i + " is " + boatClassInstance.getBoatColor());
            // set the onclick listener for each button in the array
            classRecallButtonArrayList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override // create an alert dialog to confirm user wants to recall the boat class
                public void onClick(View v) {
                    if (!myCountDownTimer.isPaused()) { // prevent pausing a paused timer
                        myCountDownTimer.pause(); // pause the timer.
                        Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_PAUSING  );
                    } else {
                        Log.i(LOGTAG, "248 OnClick is DID NOT PAUSE timer XX ");
                    }
//                    tempCurrentPosition = arrayPosition;
                    Log.i(LOGTAG, "RecallListener OnClick: arrayPosition is " + arrayPosition + " Color is " + boatClassInstance.getBoatColor() + " " + myCountDownTimer.isPaused());

                    //create a dialogue to confirm user wants to recall
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Confirm Class Recall of Boat Class: "
                            + boatClassInstance.getBoatColor().toUpperCase());
                    // tell the user what will happen if they confirm the recall
                    String alertMessage = "Recall Class: "
                            + boatClassInstance.getBoatColor().toUpperCase()
                            + "\nNOTE: After confirmation the class will move "
                            + "to last position and its time will be reset.";
                    if (currentPosition != arrayPosition) {
                        alertMessage += "\n\nWARNING: You will have 10 seconds after recall "
                                + "before the next class begins.";
                    }

                    alertDialog.setMessage(alertMessage);
                    alertDialog.setCancelable(false);

                    //User selects Confirm
                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(LOGTAG, " Confirmed: > OnClick event triggered for "  + boatClassInstance.getBoatColor());

                            masterPause.setVisibility(View.VISIBLE); // make pause button visible if it isn't

                            //clear out the start time from the current boat class isntance
                            boatClassInstance.setStartTime(null);


                            Log.i(LOGTAG, "BCROCL : arrayposition: " + arrayPosition);

                            // if the current position is the same as the postion of the boat class in the array
                            if (currentPosition == arrayPosition) {

                                myCountDownTimer.cancel(); // exit the current timer method
                                Log.i(LOGTAG, "BCROCL : " + Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_STOPPING);

                                flagToDisplay = 0; //reset flag sequence to position 0
                                // restart using new current position which restarts the timer

                                myCountdownMethod(0, 0, 10); // grace period before restart of event
                                Log.i(LOGTAG, "BCROCL : " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_XZ + CDT_CALLING);
                                // make flag boxes invisible until new variables are set using flag switch
                                currentFlagImage.setVisibility(View.INVISIBLE);
                                // show the next flag container
                                nextFlagImage.setVisibility(View.VISIBLE);
                                // set the next flag to class up
                                nextFlagImage.setImageResource(R.drawable.class_up);
                                // Change the color to the next color class color in the array.
                                    // get the index of the current boat class in the array

                                //If the array has more classes in it after current position
                                //get the color of the next class and set it to the current class color
                                if (arrayPosition < (numberOfSelectedBoatClasses - 1)) {
                                    //get the color of the class to be displayed
                                    currentClassColor.setBackgroundColor(BoatStartingListClass
                                            .BOAT_CLASS_START_ARRAY.get((arrayPosition + 1))
                                            .getClassColorSolid());
                                } // if it is the last

                            } else if(currentPosition < arrayPosition) {
                                // simply resume the timer.
                                myCountDownTimer.start();
                            } else {// check if the flag sequence finished.

                                if (currentPosition >= (numberOfSelectedBoatClasses - 1)) {
                                    Log.i(LOGTAG, "BCROCL : Current pos > = Num of selected " +
                                            "boats. Resetting ");
                                    // set the position back by one
                                    currentPosition = (numberOfSelectedBoatClasses - 1);
//                                    currentPosition = (numberOfSelectedBoatClasses - 1);
                                    flagToDisplay = 0; //reset flag sequence to position 0
                                    try {
                                        myCountDownTimer.cancel(); // exit the timer
                                        Log.i(LOGTAG, "BCROCL : " + Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_STOPPING);
                                    } catch (Exception e) {
                                        Log.i(LOGTAG, "BCROCL: End of array. myCountDownTimer" +
                                                ".cancel() raised an exception");
                                    }
                                    // make flag boxes invisible until new variables are set using flag switch
                                    currentFlagImage.setVisibility(View.INVISIBLE);
                                    // show the next flag container
                                    nextFlagImage.setVisibility(View.VISIBLE);
                                    // set the next flag to class up
                                    nextFlagImage.setImageResource(R.drawable.class_up);
                                    // restart using new current position which restarts the timer
                                    myCountdownMethod(0, 0, 10); // grace period before restart of event

                                    Log.i(LOGTAG, "BCROCL : " + Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_CALLING);
                                } else { // if flag sequence didn't finsih just continue on
                                    //resume the countdown from it's paused location -1
                                    currentPosition--;
                                    myCountDownTimer.start(); //resume the timer

                                    Log.i(LOGTAG, "BCROCL : 346 Countdown Timer resumed XX");
                                }
                            }
                            //clear the start time if any.
                            boatClassInstance.setStartTime(null);
                            // move boat class to last position
                            BoatStartingListClass.moveBoatToLast(boatClassInstance);
                            // reassign onclick listener to ensure arraylist alignment
                            buildClassRecallOnClickListeners();
                            //Redraw the activities to reflect the change class order
                            classTableRowBuilder(colorBlockFLs, contentBlockLFs, boatClassNames);
                            linlayClassContainer.invalidate(); // force the view to refresh
                        }
                    });

                    // User selects NO
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myCountDownTimer.start();
                            Log.i(LOGTAG, "BCROCL : " + Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_RESUMING);

                            Log.i(LOGTAG, "Recall class " + boatClassInstance.getBoatColor()
                                    + ". User selected \"NO : Do not recall the class\"");
                        }
                    });

                    alertDialog.show();
                }
            });
        }
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



    // modify the appearance of the activty by adding or removing each of the boat class panes.
    // Only boat classes that the user wishes to include will be visible
    private void classTableRowBuilder(ArrayList<FrameLayout> colorFL,
                                      ArrayList<FrameLayout> contentFL,
                                      ArrayList<TextView> boatClassNameTV) {
        for (int i = 0; i < BoatStartingListClass.BOAT_CLASS_START_ARRAY.size(); i++) {
            // create an instance of the boat class
            BoatClass bc = BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(i);
            Time t = bc.getStartTime();
            colorFL.get(i).setBackgroundColor(bc.getClassColorSolid()); // assign color to pane
            contentFL.get(i).setBackgroundColor(bc.getClassColorLite()); // assign lite color to pane
            boatClassNameTV.get(i).setText(bc.getBoatColor()); // assign the name as text in the textview
            visibleClasses.get(i).setVisibility(View.VISIBLE); // make the pane visible.
            if (t != null) {
                currentCaseClassStartTime.get(i).setText(timeFormat.format(t));
            } else {
                currentCaseClassStartTime.get(i).setText("");
            }
        }

    }


    // method that changes the displayed flag on the GUI


    // time vars for testing
    int t1 = 6;
    int t2 = 3;
    int t3 = 5;
    int t0 = 0;
    int tempTime = 0;


    private void raceFlagSwitcher(int flagToDisplay){
        String caseString;
        if(this.flagToDisplay == flagToDisplay){
            caseString = " CASE: [" + this.flagToDisplay + "] currentPosition: [" + this.currentPosition + "]";
        } else {
            caseString = "WARNING WARNING!!!\n\nthis.flagToDisplay and the raceFlagSwitcher local flagToDisplay do not match!";
        }
        //each case sets the Current flag and the Next flag, increments the counter for the next run
        // then calls the countdown method with the appropriate time interval for the flag set
        switch (flagToDisplay) {
            case -1: // for -1 case show no current flag and display the class up as the next flag
                tempTime = 3;
                Log.i(LOGTAG, caseString);
                resetFlagAndClassHolders();


                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit

                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
//                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": >> Line:" + Thread.currentThread().getStackTrace()[2].getLineNumber()  + " Countdown Timer CREATED XX Case " + flagToDisplay + " " + Thread.currentThread().getStackTrace()[2].getMethodName());
                break;
            case 0: // for 0 case show no current flag and display the class up as the next flag
                tempTime = 3;
                Log.i(LOGTAG, caseString);
                currentClassColor.setBackgroundColor(getResources().getColor(R.color.RegORANGE));
                currentFlagImage.setVisibility(View.INVISIBLE); // no current flag so invis
                nextFlagImage.setVisibility(View.VISIBLE); // show the next flag holder
                nextFlagImage.setImageResource(R.drawable.class_up); // put flag in next flag


                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit
                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
//                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[1]  + " " + RegattaTimer.getLineNumber() + " Countdown Timer CREATED XX Case " + flagToDisplay);
                break;
            case 1:
                tempTime = 3;
                Log.i(LOGTAG, caseString);
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                currentClassColor.setBackgroundColor(BoatStartingListClass
                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getClassColorSolid());
                currentFlagImage.setImageResource(R.drawable.class_up); // up for 1 min
                nextFlagImage.setImageResource(R.drawable.class_up_prep_up);


                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit
                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
                break;
            case 2:
                tempTime = t2;
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                Log.i(LOGTAG, caseString);
                currentClassColor.setBackgroundColor(BoatStartingListClass
                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getClassColorSolid());
                currentFlagImage.setImageResource(R.drawable.class_up_prep_up); // up for 3 mins
                nextFlagImage.setImageResource(R.drawable.class_up_prep_down);


                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit
                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
                break;
            case 3:
                tempTime = t2;
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                Log.i(LOGTAG, caseString);
                currentClassColor.setBackgroundColor(BoatStartingListClass
                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getClassColorSolid());
                currentFlagImage.setImageResource(R.drawable.class_up_prep_down); // up for 1
                //Check if there are more boat classes to time
                if ((currentPosition + 1) <= (numberOfSelectedBoatClasses - 1)) {
                    nextFlagImage.setImageResource(BoatStartingListClass.BOAT_CLASS_START_ARRAY
                            .get(this.currentPosition + 1).getImage());
                } else {
                    nextFlagImage.setImageResource(R.drawable.no_flags);
                }

                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit
                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
                break;
            case 4:
//                flagToDisplay++;
//
////                tempTime = t0;
////                currentFlagImage.setVisibility(View.VISIBLE);
////                nextFlagImage.setVisibility(View.VISIBLE);
////                Log.i(LOGTAG, caseString);
////                currentClassColor.setBackgroundColor(BoatStartingListClass
////                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getClassColorSolid());
////                currentFlagImage.setImageResource(R.drawable.classdown);
////                nextFlagImage.setImageResource(R.drawable.classup);
////
////
////                myCountdownMethod(0, 0, tempTime);// start new timer with given time limit
////                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_CALLING  + CDT_XZ + " Case " + flagToDisplay);
//                break;
//            case 5:
                Time now = new Time(new Date().getTime()); // get the current time

                // write the current time to the current time text box
                currentCaseClassStartTime.get(this.currentPosition)
                        .setText(timeFormat.format(new Date()));

                // write the current time to the BoatClass variable for storage
                BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(currentPosition)
                        .setStartTime(now);
                Log.i(LOGTAG, "Case 5 date for " + BoatStartingListClass.BOAT_CLASS_START_ARRAY
                        .get(currentPosition).getBoatColor() + " is " + new Date());
                // show the flag holders
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);

                Log.i(LOGTAG, caseString);

                this.flagToDisplay = 1; // set flag back to 1
                this.currentPosition++; // increment the current position
                // if the current position exceeds or equals the number of boat classes reset
                if ((this.currentPosition) < numberOfSelectedBoatClasses) {
                    raceFlagSwitcher(this.flagToDisplay); // run the flag sequence from 1
                    //change the class color to the color of the next boat class in the starting lineup
                    currentClassColor.setBackgroundColor(BoatStartingListClass
                            .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getClassColorSolid());
                } else {
                    //exit the switch case
                    resetFlagAndClassHolders(); // clear out the data in flag and class holders
                    currentFlagImage.setVisibility(View.VISIBLE);
                    currentFlagImage.setImageResource(R.drawable.no_flags);
                    masterPause.setVisibility(View.INVISIBLE); // hide the pause button
                    txtCountDown.setText("00:00:00"); // reset the countdown display's time to 0
                }
                break;
            default:
                Log.i(LOGTAG, " Switchcase raceFlagSwitcher ended in default");
                break;
        }
    }

    private void resetFlagAndClassHolders() {
        // make flag boxes invisible to start
        currentClassColor.setBackgroundColor(0);
        currentFlagImage.setVisibility(View.INVISIBLE);
        nextFlagImage.setVisibility(View.INVISIBLE);
    }


    // activate or deactivate the recall buttons base
    private void enabledStatusSwitcherRecallButtons(boolean isEnabled) {
        for (Button b : classRecallButtonArrayList) {
            b.setEnabled(isEnabled); // apply state to each button instance
            b.setClickable(isEnabled);// remove clickability too
        }
    }



    private void myCountdownMethod(int hours, int minutes, int seconds) {
        //convert hours mins and seconds to milliseconds
        int milliHours = hours * 3600000;
        int milliMinutes = minutes * 60000;
        int milliSeconds = seconds * 1000;
        // add all milliseconds up
        totalTime = milliHours + milliMinutes + milliSeconds;
//        Log.i(LOGTAG, "RRR CREATING NEW TIMER!!!!! XXX");
        myCountDownTimer = new CountDownTimerPausable(totalTime, 1001) {
            /**
             * This method is called periodically with the interval set as the delay between subsequent calls.
             */
            @Override
            public void onTick(long millisUntilFinished) {
                //convert milliseconds into hh:mm:ss
                String timeString = String.format("%02d:%02d:%02d", TimeUnit
                                .MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) %
                                TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) %
                                TimeUnit.MINUTES.toSeconds(1));
                txtCountDown.setText(timeString); // Update text to display current time remaining

            }

            @Override
            public void onFinish() {
                player.start(); // play horn sound to inform user that the state is switching
                flagToDisplay++; // add 1 to the flag sequence. Moves the case to next flag

                raceFlagSwitcher(flagToDisplay); // restart switch case using next flag
            }
        }.start();
    }

    // starts everything off
    private void masterTimerEventHandler() {

        Log.i(LOGTAG, " Master Time Event Handler Activated");
        raceFlagSwitcher(this.flagToDisplay);
        buildClassRecallOnClickListeners(); // rebuild the onclick listeners.
    }

    // method to switch between timer start button and pause button
    private void masterStartButtonSwitcher() {
        if (isStartButton) {
            masterStart.setText("Gen. Recall"); // change text of button
            isStartButton = false; // change status of button to NOT start
            Log.i(LOGTAG, "isStartButton = True");
        } else {
            masterStart.setText("Start");// change text of button
            isStartButton = true; // change status of button to is start
            Log.i(LOGTAG, "isStartButton = False");
        }
    }

    //function for finishline button
    public void onClickFinishLine(View view) {
        resultDataSource.create();
        Intent intent = new Intent(this, ResultsMenu.class);
        startActivity(intent);
    }

    //function for the pause button
    public void onClickMasterPause(View view) throws InterruptedException {
        if(!myCountDownTimer.isPaused()){
            myCountDownTimer.pause();

            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_PAUSING);
        } else {
            myCountDownTimer.start();

            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName()  + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber()  + CDT_XZ + CDT_RESUMING);
        }



    }


    public void onClickMasterStart(View view) throws InterruptedException {

        if (isStartButton) {
            enabledStatusSwitcherRecallButtons(true); // enable recall buttons
            masterPause.setVisibility(View.VISIBLE); // make visible when timer started
            masterStartButtonSwitcher();
            masterTimerEventHandler();
        } else {
            verifyIntentToResetMasterTime(); // confirm choice to gen recall
        }
    }

    private void verifyIntentToResetMasterTime() {

        if (!myCountDownTimer.isPaused()) {
            myCountDownTimer.pause(); // pause the currently running timer
            Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_XZ + CDT_PAUSING);
        }
        // build dialog box for confirmation
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("General Recall Confirmation");
        alertDialog.setMessage("perform a GENERAL RECALL?\n" +
                "This will reset all timers.");
        alertDialog.setCancelable(false);

        // User chooses confirm
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //if yes
                Log.i(LOGTAG, " general recall dialog result is CONFIRMED");
                //blank out all stored times
                resetFlagAndClassHolders();
                myCountDownTimer.cancel(); // cancel the timing event
                Log.i(LOGTAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": Line#: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + CDT_XZ + CDT_STOPPING);
                masterPause.setVisibility(View.INVISIBLE);// hide pause button to prevent errors
                for (TextView tv : currentCaseClassStartTime) {
                    tv.setText(null);
                }
                //TODO: Add function to update Results Table by blanking out start times for all classes
                //set all variables to initial positions
                flagToDisplay = -1;
                currentPosition = 0;
                BoatStartingListClass.resetAllBoatStartTimes(); // clear all start times
                masterStartButtonSwitcher(); // change the main button back to start
                txtCountDown.setText("00:00:00"); // set counter to 0
                enabledStatusSwitcherRecallButtons(false); // disable recall buttons
            }
        });

        //User choose no
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(LOGTAG, " general recall dialog result is NO");
            }
        });

        alertDialog.show();
    }

}



