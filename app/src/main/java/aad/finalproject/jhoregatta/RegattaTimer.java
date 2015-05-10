package aad.finalproject.jhoregatta;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import aad.finalproject.db.ResultDataSource;

import static aad.finalproject.jhoregatta.R.id.imgNextFlag;


public class RegattaTimer extends MainActivity {

    private static String LOGTAG = "LogTag: TimeTracker "; // default log tag

    // create an instance of the result table datasource
    private ResultDataSource resultDataSource;

    private PowerManager.WakeLock wl; // wake lock instance

    private FrameLayout keepScreenOnDummyLayout;

    //get the shared preffs
    private SharedPreferences sharedPreferences;

    //var that indicates that the timer has finished all timing activities
    protected static boolean TIMER_FINISHED = false;

    //sound stuff
    private MediaPlayer hornMid;
    private ToneGenerator toneGenerator;
    private int toneType = ToneGenerator.TONE_CDMA_ABBR_ALERT;
    private int durationMs = 250;


    // timing variables
    CountDownTimerPausable myCountDownTimer;

    public static Button startResume;
    private TextView txtCountDown; // accessible instance of countdown
    private int flagToDisplay; // which flag case to implement

    //text options for start resume
    String startText = "Start";

    // create array list instances
    private ArrayList<LinearLayout> visibleClasses = new ArrayList<>();
    private ArrayList<FrameLayout> colorBlockFLs = new ArrayList<>();
    private ArrayList<FrameLayout> contentBlockLFs = new ArrayList<>();
    public ArrayList<TextView> currentCaseClassStartTime = new ArrayList<>();
    private ArrayList<TextView> boatClassNames = new ArrayList<>();
    private ArrayList<Button> classRecallButtonArrayList = new ArrayList<>();

    // initialize containers
    private LinearLayout linlayClassContainer;
    private ImageView currentClassImage;
    private ImageView currentFlagImage;
    private ImageView nextFlagImage;

    //used for testing the app. mins and seconds will be
    // adjusted to meet timer needs on final version

    private long totalTime = 0;

    private int numberOfSelectedBoatClasses; // initialize the number of selected Classes variable
    private int currentPosition = 0;

    //allowance for time drift
    private double drift = 0.0001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regatta_timer);

        //Keep awake during activity
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        //get teh size of the window
        double screenSize = GlobalContent.getScreenSize(this);


        //switch to media volume control vs notification volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        sharedPreferences = getSharedPreferences(Preferences.PREFS, 0);// grab the shared prefs

        //wire and open the result datasource
        resultDataSource = new ResultDataSource(this);
        resultDataSource.open();

        wireWidgetsAndAddToArrayLists(); // call the wiring method
        enabledStatusSwitcherRecallButtons(false); // disable all recall buttons

        //set the SP of the timer programatically based on the size of the device's screen
        if (screenSize < 5.5) {
            txtCountDown.setTextSize(45f);
        } else {
            txtCountDown.setTextSize(65f);

        }
        // Media play section
        hornMid = new MediaPlayer();
        int volume = 100;
        int streamType = AudioManager.STREAM_MUSIC;
        toneGenerator = new ToneGenerator(streamType, volume);

        // load up horn mid
        try {
            AssetFileDescriptor afd = getAssets().openFd("Horn_Blast.wav");
            //set the players data source elements
            hornMid.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            hornMid.prepare();
        } catch (IOException e) {
            e.printStackTrace(); //could be tricky!
        }


        //handle the start resume functions
        startResume = (Button) findViewById(R.id.btn_tt_start_resume); // wire up the button
        startResume.setText(startText); //set the text to "Start" initially

        //set onclick listener for start resume button
        startResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (startResume.getText().toString()) {
                    case "Start":
                        masterTimerEventHandler(); // start the timer event!
                        break;
                    case "Resume":
                        //get the integer of the number of seconds to delay
                        int postRecallDelay = sharedPreferences.getInt("postRecallDelay",45);
                        //if the value is 0 then create a 50millisecond delay.
                        if (postRecallDelay > 0) {
                            // start new timer with given time limit
                            myCountdownMethod(0, 0, sharedPreferences.getInt("postRecallDelay", 45),
                                    " Resume Case ");
                        } else {
                            //create a timer without a post recall delay (well a 50 ms delay)
                            myCountdownMethod(0, 0, drift," Resume Case ");
                        }
                        break;
                }
            }
        });


        // event sequence variable
        flagToDisplay = 0; // set initial value

        txtCountDown.setText("0s");

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
        currentClassImage = (ImageView) findViewById(R.id.imgCurrentClass);
        keepScreenOnDummyLayout = (FrameLayout) findViewById(R.id.frmlay_dummy_layout_keepScreenOn);

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
            final int arrayPosition = i; // set the position the class holds in the classes displayed
            final Context context = this; //grab context for use in inner methods
            final BoatClass boatClassInstance = BoatStartingListClass
                    .BOAT_CLASS_START_ARRAY.get(i);// grab boat class color for inner methods
            String startTimeInstance = boatClassInstance.getStartTime(); // grab the start time if any
            if (startTimeInstance != null) {
                //if a start time exists put it in the text box
                currentCaseClassStartTime.get(i).setText(startTimeInstance);
            } else {
                currentCaseClassStartTime.get(i).setText("");
            }
            Log.i(LOGTAG, "Color at " + i + " is " + boatClassInstance.getBoatColor());
            // set the onclick listener for each button in the array
            classRecallButtonArrayList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override // create an alert dialog to confirm user wants to recall the boat class
                public void onClick(View v) {

//-------------------------------BEGIN ALERT DIALOG-------------------------------------------------
                    //create a dialogue to confirm user wants to recall
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Confirm Class Recall of Boat Class: "
                            + boatClassInstance.getBoatColor().toUpperCase());
                    // tell the user what will happen if they confirm the recall
                    String alertMessage = "Recall Class: "
                            + boatClassInstance.getBoatColor().toUpperCase()
                            + "\nNOTE: After confirmation you a countdown of " +
                            GlobalContent.convertMillisToFormattedTime((long) (sharedPreferences
                                    .getInt("postRecallDelay", 45) * 1000), 0) +
                            " and the class time will be reset.";


                    alertDialog.setMessage(alertMessage);
                    alertDialog.setCancelable(false);
  //---------------------------BEGIN ON CONFIRMATION FUNCTION ------------------------------------
                    //User selects Confirm
                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            myCountDownTimer.cancel(); // cancel the timer completely
                            timeTrackerIsFinished(false); // disable exit button/ unfinish tracker

                            //clear out the start time from the current boat class isntance
                            clearUnstartedBoatTimes(arrayPosition);

                            //disable all recall buttons for classes after current position
                            for (int i = (arrayPosition + 1); i < numberOfSelectedBoatClasses; i++) {
                                classRecallButtonArrayList.get(i).setEnabled(false);
                                Log.i(LOGTAG, "Disabling recall for all classes beyond position" +
                                        arrayPosition);
                            }

                            txtCountDown.setText(""); // blank out the counter

                            Log.i(LOGTAG, "BCROCL : arrayposition: " + arrayPosition);


                            //set current position to the array position
                            currentPosition = arrayPosition;
                            flagToDisplay = 0; //reset flag sequence to position 0 IT WILL SWITCH

                            // make current flag AND current class color boxes invisible until new
                            // Flag switcher will make them visible again
                            currentFlagImage.setVisibility(View.INVISIBLE);
                            currentClassImage.setVisibility(View.INVISIBLE);
                            // show the next flag container
                            nextFlagImage.setVisibility(View.VISIBLE);
                            // set the next flag to class up
                            nextFlagImage.setImageResource(BoatStartingListClass
                                    .BOAT_CLASS_START_ARRAY
                                    .get(arrayPosition).getImage());


                            // store color of current class
                            String color = boatClassInstance.getBoatColor();

                            //clear the times and durations for the given class.
                            resultDataSource.clearSingleClassStartTimesAndDurations(GlobalContent
                                    .getResultsRowID(), color);

                            //rebuild the tables
                            classTableRowBuilder(colorBlockFLs, contentBlockLFs, boatClassNames);
                            linlayClassContainer.invalidate(); // force the view to refresh

                            Log.i(LOGTAG, "Try invalidating static myList");
                            refreshResultsList(); //self explainitory

                        }
                    });
    //---------------------------BEGIN NO FUNCTION -------------------------------------------------
                    // User selects NO
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           //Dialog will dismiss automatically
                        }
                    });

                    alertDialog.show();
                }
            });
        }
//-------------------------------END ALERT DIALOG-------------------------------------------------
    }

    private void timeTrackerIsFinished(boolean isFinished) {

        // set the status of the time tracker
        TIMER_FINISHED = isFinished;

        //check if the results menu activity is open
        if (GlobalContent.RESULT_MENU_ALIVE) {
            // if the reset button is pushed then the exit button on the
            // results menu should not be accessible so hide it.
            ResultsMenu.exitRace.setEnabled(isFinished);
        }
    }

    //sync up states and data sets with the results list
    private void refreshResultsList() {
        //risky because the results adapter and list may not be open yet.
        if (GlobalContent.activeResultsAdapter != null) {
            try {
                //sync the results adapter with data from the SQL table.
                GlobalContent.activeResultsAdapter
                        .syncArrayListWithSql(ResultsMenu
                                .getAllSQLResultResults(resultDataSource));
                ResultsMenu.myList.invalidate(); // force refresh the list
            } catch (Exception e) {
                Log.i(LOGTAG, " GlobalContent.activeResultsAdapter SYNC fired but caused an exception!!!!!!!!!!!");
            }
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
        switch (item.getItemId()) {
            case R.id.action_ddms:
                onActionClickDDMS();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, Preferences.class);
                startActivity(intent);
                return true;
            case R.id.action_reset:
                verifyIntentToResetMasterTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wl.acquire(); //acquire wake lock

    }

    @Override
    protected void onPause() {
        super.onPause();
        wl.release(); // let go of wake lock
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // modify the appearance of the activty by adding or removing each of the boat class panes.
    // Only boat classes that the user wishes to include will be visible
    private void classTableRowBuilder(ArrayList<FrameLayout> colorFL,
                                      ArrayList<FrameLayout> contentFL,
                                      ArrayList<TextView> boatClassNameTV) {
        for (int i = 0; i < BoatStartingListClass.BOAT_CLASS_START_ARRAY.size(); i++) {
            // create an instance of the boat class
            BoatClass bc = BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(i);
            String time = bc.getStartTime();
            colorFL.get(i).setBackgroundColor(bc.getClassColorSolid()); // assign color to pane
            contentFL.get(i).setBackgroundColor(bc.getClassColorLite()); // assign lite color to pane
            boatClassNameTV.get(i).setText(bc.getBoatColor()); // assign the name as text in the textview
            visibleClasses.get(i).setVisibility(View.VISIBLE); // make the pane visible.
            //check if a time is present. if set the current class start time to blank
            if (time != null) {
                currentCaseClassStartTime.get(i).setText(time);
            } else {
                currentCaseClassStartTime.get(i).setText("");
            }
        }

    }

    //controls which flags are to be displayed on the right hand side of the activity
    private void raceFlagSwitcher(int flagToDisplay){
        String caseString;
        if(this.flagToDisplay == flagToDisplay){
            caseString = " CASE: [" + this.flagToDisplay + "] currentPosition: [" +
                    "" + this.currentPosition + "]";
        } else {
            caseString = "WARNING WARNING!!!\n\nthis.flagToDisplay and the raceFlagSwitcher " +
                    "local flagToDisplay do not match!";
        }
        //each case sets the Current flag and the Next flag, increments the counter for the next run
        // then calls the countdown method with the appropriate time interval for the flag set
        switch (flagToDisplay) {
            case -1: // for -1 case show no current flag and display the class up as the next flag
                Log.i(LOGTAG, caseString);
                resetFlagAndClassHolders(); // reset all the flags and holders
                // start new timer with given time limit
                myCountdownMethod(0, 0, 3," Case -1: current Pos: " + currentPosition);

                break;
            case 0: // for 0 case show no current flag and display the class up as the next flag
                Log.i(LOGTAG, caseString);
//                keep the button disabled until the case 1
                classRecallButtonArrayList.get(currentPosition).setEnabled(true);
                currentFlagImage.setVisibility(View.INVISIBLE); // no current flag so invis

                nextFlagImage.setVisibility(View.VISIBLE); // show the next flag holder
                nextFlagImage.setImageResource(R.drawable.class_up); // put flag in next flag

                //get the initial start time as an int
                int initialDelay = sharedPreferences.getInt("initialDelay", 15);

                //Because this value cannot actually be 0 then add a delay in the timer of 50 milliseconds
                if (initialDelay > 0) {
                    // start new timer with given time limit
                    myCountdownMethod(0, 0, sharedPreferences.getInt("initialDelay", 15),
                            " Case 0: current Pos: " + currentPosition);
                } else {
                    myCountdownMethod(0, 0, drift," Case 0: current Pos: " + currentPosition);

                }

                break;
            case 1:
                Log.i(LOGTAG, caseString);
                // enable the recall button for the active class
                classRecallButtonArrayList.get(currentPosition).setEnabled(true);
                // make sure the flags are visible
                currentClassImage.setVisibility(View.VISIBLE);
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                //change the current class color to the currentPosition's class color
                currentClassImage.setImageResource(BoatStartingListClass
                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getImage());
                currentFlagImage.setImageResource(R.drawable.class_up); // up for 1 min
                nextFlagImage.setImageResource(R.drawable.class_up_prep_up);
                // start new timer with given time limit
                myCountdownMethod(0, 0, sharedPreferences.getInt("classUp",65),
                        " Case 1: current Pos: " + currentPosition);
                break;
            case 2:
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                Log.i(LOGTAG, caseString);currentClassImage.setImageResource(BoatStartingListClass
                    .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getImage());
                currentFlagImage.setImageResource(R.drawable.class_up_prep_up); // up for 3 mins
                nextFlagImage.setImageResource(R.drawable.class_up_prep_down);

                // start new timer with given time limit
                myCountdownMethod(0, 0, sharedPreferences.getInt("classUpPrepUp",185),
                        " Case 2: current Pos: " + currentPosition);
                break;
            case 3:
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);
                //change the background color to the color of the next boat
                currentClassImage.setImageResource(BoatStartingListClass
                        .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getImage());
                currentFlagImage.setImageResource(R.drawable.class_up_prep_down); // up for 1
                //Check if there are more boat classes to time
                if ((currentPosition + 1) <= (numberOfSelectedBoatClasses - 1)) {
                    nextFlagImage.setImageResource(BoatStartingListClass.BOAT_CLASS_START_ARRAY
                            .get(this.currentPosition + 1).getImage()); // show the next class color
                } else {
                    nextFlagImage.setImageResource(R.drawable.no_flags);
                }
                // start new timer with given time limit
                myCountdownMethod(0, 0,sharedPreferences.getInt("classUpPrepDown",65),
                        " Case 3: current Pos: " + currentPosition);
                break;
            case 4:

                //get current date time
                DateTime localTime = DateTime.now();
                String now = GlobalContent.dateTimeToString(localTime);

                // write the current time to the current time text box
                currentCaseClassStartTime.get(this.currentPosition)
                        .setText(now);

                //instance of current boat class
                BoatClass currentBoatClass = BoatStartingListClass.BOAT_CLASS_START_ARRAY
                        .get(currentPosition);


                // send the class's start time and class distance to the sql database
                resultDataSource.updateClassStartTime(GlobalContent.getRaceRowID(),
                        currentBoatClass.getBoatColor(), now, currentBoatClass.getClassDistance());
                Log.i(LOGTAG, "Distance Version of updateClassStartTime Used. Distance is " +
                        currentBoatClass.getClassDistance());

                // write the current time to the BoatClass variable for storage
                BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(currentPosition)
                        .setStartTime(now);
                // show the flag holders
                currentFlagImage.setVisibility(View.VISIBLE);
                nextFlagImage.setVisibility(View.VISIBLE);

                this.flagToDisplay = 1; // set flag back to 1
                this.currentPosition++; // increment the current position
                // if the current position exceeds or equals the number of boat classes reset
                if ((this.currentPosition) < numberOfSelectedBoatClasses) {
                    raceFlagSwitcher(this.flagToDisplay); // run the flag sequence from 1
                    //change the class color to the color of the next boat class in the starting lineup
                    currentClassImage.setImageResource(BoatStartingListClass
                            .BOAT_CLASS_START_ARRAY.get(this.currentPosition).getImage());
                } else { // the time tracker is done
                    //exit the switch case
                    resetFlagAndClassHolders(); // clear out the data in flag and class holders
                    currentFlagImage.setVisibility(View.VISIBLE);
                    currentFlagImage.setImageResource(R.drawable.no_flags);
                    txtCountDown.setText(""); // reset the countdown display's time to 0
                    //check if the results menu is active
                    timeTrackerIsFinished(true); // enable the result menu exit button
                }
                refreshResultsList(); // force a repaint of the results list.
                break;
            default:
                Log.i(LOGTAG, " Switchcase raceFlagSwitcher ended in default");
                break;
        }
    }

    private void resetFlagAndClassHolders() {
        // make flag boxes invisible to start
        currentClassImage.setVisibility(View.INVISIBLE);
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


    private void myCountdownMethod(int hours, int minutes, double seconds, String caller) {
        //convert hours mins and seconds to milliseconds
        long milliHours = hours * 3600000;
        long milliMinutes = minutes * 60000;
        final long milliSeconds = (long) (seconds * 1000);

        //keep the screen on by allowing the dummy frame layout to be displayed
        keepScreenOnDummyLayout.setKeepScreenOn(true);

        // add all milliseconds up
        totalTime = milliHours + milliMinutes + milliSeconds;

        Log.i(LOGTAG, " CountdownTimer Called by: " + caller);
        myCountDownTimer = new CountDownTimerPausable(totalTime, 500) {
            /**
             * This method is called periodically with the interval set as the delay between subsequent calls.
             */
            @Override
            public void onTick(long millisUntilFinished) {
                // set the string to the top of the current second
                String timeString = bestTickTockTime(millisUntilFinished+1000);
                txtCountDown.setText(timeString); // Update text to display current time remaining
                // play sound every second for the last 10 seconds.
                if (millisUntilFinished < 10001 & (millisUntilFinished %1000 > 501)) {
                    toneGenerator.startTone(toneType, durationMs);
                }
            }

            @Override
            public void onFinish() {
                hornMid.start(); // play horn sound to inform user that the state is switching
                flagToDisplay++; // add 1 to the flag sequence. Moves the case to next flag

                keepScreenOnDummyLayout.setKeepScreenOn(false); // allow screen to dim again.
                raceFlagSwitcher(flagToDisplay); // restart switch case using next flag
            }
        }.start();
    }

    // change the display format based on if amount of time to track.
    public String bestTickTockTime(long millisUntilFinished) {
        String timeString;
        if (millisUntilFinished >= 3600000) {
            timeString = String.format("%2dh %2dm %02ds",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) %
                            TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) %
                            TimeUnit.MINUTES.toSeconds(1));
        } else if (millisUntilFinished >= 60000) {
            timeString = String.format("%2dm %02ds",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) %
                            TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) %
                            TimeUnit.MINUTES.toSeconds(1));
        } else {
            timeString = String.format("%2ds",
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) %
                            TimeUnit.MINUTES.toSeconds(1));
        }
        return timeString;
    }


    // starts everything off
    private void masterTimerEventHandler() {

        Log.i(LOGTAG, " Master Time Event Handler Activated");
        raceFlagSwitcher(this.flagToDisplay); // start from default position
        buildClassRecallOnClickListeners(); // rebuild the onclick listeners.
    }


    //function for finishline button
    public void onClickFinishLine(View view) {
        Log.i(LOGTAG, "Opening finishLine activity");
        GlobalContent.setResultsFormAccessMode(false); //set access mode to "add" mode
        Intent intent = new Intent(this, ResultsMenu.class);
        startActivity(intent);
    }

    private void verifyIntentToResetMasterTime() {
        // build dialog box for confirmation
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Complete Reset/Recall Confirmation");
        alertDialog.setMessage("Perform a Complete Reset?\n" +
                "This will reset all timers.");
        alertDialog.setCancelable(false);

        // User chooses confirm
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //if yes
                Log.i(LOGTAG, " Complete reset dialog result is CONFIRMED");

                resetFlagAndClassHolders();//blank out all stored times
                myCountDownTimer.cancel(); // cancel the timing event

                startResume.setText("Start"); //change text to start

                timeTrackerIsFinished(false); // disable exit button

                //clear start times from boat class array
                for (TextView tv : currentCaseClassStartTime) {
                    tv.setText(null);
                }
                //remove any saved start times for all classes
                resultDataSource.clearRaceTimesDurations(GlobalContent.getRaceRowID());
                refreshResultsList();

                //set all variables to initial positions
                flagToDisplay = 0; // set flag to 0
                currentPosition = 0; // position 0
                BoatStartingListClass.resetAllClassStartTimes(); // clear all start times

                txtCountDown.setText(""); // set counter to 0
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

    //clear the start times from each class after the class being recalled.
    private void clearUnstartedBoatTimes(int arrayPosition) {
        for (int i = arrayPosition; i < BoatStartingListClass.BOAT_CLASS_START_ARRAY.size(); i++) {
            BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(i).setStartTime(null);
        }
    }

}



