package aad.finalproject.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.jhoregatta.BoatClass;
import aad.finalproject.jhoregatta.BoatStartingListClass;
import aad.finalproject.jhoregatta.GlobalContent;
import aad.finalproject.jhoregatta.Preferences;
import aad.finalproject.jhoregatta.R;
import aad.finalproject.jhoregatta.ResultsEditor;
import aad.finalproject.jhoregatta.ResultsMenu;

/*
Handles the list view of results in the results menu
 */
public class ResultsAdapter extends BaseAdapter {
    private static String LOGTAG = "ResultAdapter";
    Context mContext; // add context
    LayoutInflater inflater; // instance of inflater
    ResultDataSource resultDataSource;
    private SharedPreferences sp;
    // lists of result
    public ArrayList<Result> arraylist;
    public Handler handler;


    // instance constructor
    public ResultsAdapter(Context context, List<Result> mainDataList,
                          ResultDataSource resultDataSource) {
        this.resultDataSource = resultDataSource;
        mContext = context;
        sp = context.getSharedPreferences(Preferences.PREFS, 0);
        inflater = LayoutInflater.from(mContext);
        // create a copy of the main list so we don't end up damagaing the original list
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(mainDataList);
        handler = new Handler();
    }

    // class that holds the pertinant data relating to the first boat of a particular class that
    // crossed the finish line. Boats that do fail to cross the finish line 30 mins after the first
    // boat are disqualified

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View view, final ViewGroup parent) {

        if (view == null) {
            // build inflater to create a new row for each row in the Results table
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.activity_list_template_results, parent, false);
        }

        boolean hasExhaustedTimeLimit = false;

        // create an result that will hold the data for the row item.
        final Result result = arraylist.get(index);

        // instance of the finish button
        final Button btnFinish = (Button) view.findViewById(R.id.btn_boat_finished);

        // instance of the finish button
        final Button btnReset = (Button) view.findViewById(R.id.btn_boat_reset_finished);

        // wire text views and set the associated text to them.
        final TextView tv1 = (TextView) view.findViewById(R.id.txt_hd_results_ID);
        tv1.setText(result.getResultsId() + "");
        final TextView tv2 = (TextView) view.findViewById(R.id.txt_hd_results_race_id);
        tv2.setText(result.getResultsRaceId() + "");
        final TextView tv3 = (TextView) view.findViewById(R.id.txt_hd_results_boat_id);
        tv3.setText(result.getResultsBoatId() + "");
        final TextView tv4 = (TextView) view.findViewById(R.id.txt_hd_results_Visible);
        tv4.setText(result.getResultsVisible() + "");
        final TextView tv5 = (TextView) view.findViewById(R.id.txt_hd_results_Name);
        tv5.setText(result.getBoatName() + "");
        final TextView tv6 = (TextView) view.findViewById(R.id.txt_hd_results_Class);
        tv6.setText(result.getBoatClass() + "");
        final TextView tv7 = (TextView) view.findViewById(R.id.txt_hd_results_SailNum);
        tv7.setText(result.getBoatSailNum() + "");
        final TextView tv8 = (TextView) view.findViewById(R.id.txt_hd_finish_time);
        //set the boat finish time
        if (result.getResultsBoatFinishTime() != null) {
            tv8.setText(result.getResultsBoatFinishTime() + "");
        } else {
            tv8.setText("");
        }

        final View finalView = view;

        // add the text views the list
        final ArrayList<TextView> textViews = new ArrayList<TextView>() {{
            add(tv1);
            add(tv2);
            add(tv3);
            add(tv4);
            add(tv5);
            add(tv6);
            add(tv7);
            add(tv8);
        }};

        // programmatically change text style.
        for (TextView t : textViews) {
            t.setTextColor(Color.parseColor("#000000")); // make the text black
            t.setTypeface(Typeface.DEFAULT_BOLD); // make text bold
        }

        Result r = arraylist.get(index);


        // on refresh of list.
        // If there is a finish time present then make the row red
        if (r.getResultsNotFinished() == 1) {//if the boat didn't finish the race
            //hide the finish button and show the reset button instead.
            btnFinish.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.GONE);
            //set the row color to black
            view.setBackgroundColor(view.getResources().getColor(R.color.black));
            //set the text of each text box white
            for (TextView t : textViews) {
                t.setTextColor(Color.parseColor("#ffffff")); // make the text white
            }
            tv8.setText("DNF");


        } else if (r.getResultsBoatFinishTime() != null) {

            //hide the finish button and show the reset button instead.
            btnFinish.setVisibility(View.GONE);
            btnReset.setVisibility(View.VISIBLE);
            // set the text of the reset button. Click count to reset is 3
            btnReset.setText("   Reset [3]   ");
            if (!checkAgainstTimeLimit(r) || result.getBoatName().equals(ResultsEditor.PLACEHOLDER_BOAT_NAME)) {

                //set the row color to red
                view.setBackgroundColor(view.getResources().getColor(R.color.red06));
            } else {
                //set the row color to light blue
                view.setBackgroundColor(view.getResources().getColor(R.color.blue08));
                Log.i(LOGTAG, "COLOR IF: Fin Time != null " + r.getBoatName());
            }
            //set the text of each text box white
            for (TextView t : textViews) {
                t.setTextColor(Color.parseColor("#ffffff")); // make the text white
            }

        } else {
            Log.i(LOGTAG, "checkAgainstTimeLimit = " + !checkAgainstTimeLimit(r));
            if (!checkAgainstTimeLimit(r)) {
                // change the row color blank
                view.setBackgroundColor(Color.parseColor("#00000000"));
            } else {
                //set the row color to light blue
                view.setBackgroundColor(view.getResources().getColor(R.color.blue11));
                Log.i(LOGTAG, "COLOR IF: All Else " + r.getBoatName());
            }
            //show the finish button and hide the reset button instead.
            btnFinish.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.GONE);
            //set the text of each text box white
            for (TextView t : textViews) {
                t.setTextColor(Color.parseColor("#000000")); // make the text white
            }
        }

        // set the function of each finish button
        btnFinish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check if the class start time contains either a string or a null
                if (result.getResultsClassStartTime() != null) {
                    DateTime capturedTime = DateTime.now();// capture the current time
                    long resultId = result.getResultsId();

                    //format time as string for SQL
                    String timeFormatted = GlobalContent.dateTimeToString(capturedTime);

                    resultDataSource.updateSingleFinishTime(resultId, timeFormatted);

                    //set the result entry's finish time to the same.
                    result.setResultsBoatFinishTime(timeFormatted);

                    //hide the finish button and show the reset button instead.
                    btnFinish.setVisibility(View.GONE);
                    btnReset.setVisibility(View.VISIBLE);

                    // set the text of the reset button. Click count to reset is 3
                    btnReset.setText("   Reset [3]   ");


                    //set the text of each text box white
                    for (TextView t : textViews) {
                        t.setTextColor(Color.parseColor("#ffffff")); // make the text white
                    }

                    // get the start time for the class
                    String startTime = result.getResultsClassStartTime();

                    //get the finish time ""
                    GlobalContent.getElapsedTime(startTime, timeFormatted);


                    //run table calculations to derive duration and adjusted duration
                    resultDataSource.runSingleCalculation(resultId);

                    syncArrayListWithSql(); // sync up results with sql

                    //refresh the viewed data
                    notifyDataSetChanged();
                    updateFirstFinishers();
                } else {
                    Toast.makeText(v.getContext(), "This boat's Class hasn't started yet. \n" +
                                    "Wait for Time Tracker to finish starting the Class.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // resets the finish time to 0 after 3 clicks
        btnReset.setOnClickListener(new View.OnClickListener() {
            int counter = 3; // initial click count

            @Override
            public void onClick(View v) {
                String message; // message to pass to the user
                if (counter == 1) { // check how many clicks are left, if 1 then...
                    //blank out database entry
                    resultDataSource.clearStartAndDurations(result.getResultsId());

                    //reset the result finish order to 999
                    result.setResultsOrderFinished(999);

                    //blank out result object fnish time entry
                    result.setResultsBoatFinishTime(null);

                    //hide reset button and show finish button
                    btnReset.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.VISIBLE);
                    counter = 3; // reset counter

                    // check each boat class for a first finisher.
                    for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                        Log.i(LOGTAG, bc.getBoatColor() + " classes first finish id: " +
                                bc.getFirstFinishBoatId() + " current Result: " +
                                result.getBoatName() + " id: " + result.getResultsBoatId());
                        // If the current boat is first finisher
                        if (bc.getFirstFinishBoatId() == result.getResultsBoatId()) {
                            Log.i(LOGTAG, "Boat ID " + result.getBoatName() + " is first finisher. " +
                                    "Resetting " + result.getBoatClass() + " first finish time " +
                                    "from " + result.getResultsBoatFinishTime() + " to NULL");
                            bc.resetFirstFinish(); // reset first finisher for given class.
                            Log.i(LOGTAG, "firstFinisherTime for class " + bc.getBoatColor() + " is now " + bc.getFirstFinishAsDateTime());
                        }
                    }
                    //output message
                    message = result.getBoatName() + "'s finish time is now RESET";

                    //set the row color to transparent
                    finalView.setBackgroundColor(Color.parseColor("#00000000"));

                    // make each text box text black
                    for (TextView t : textViews) {
                        t.setTextColor(Color.parseColor("#000000")); // make the text black
                    }

                    syncArrayListWithSql();// sync up array list with SQL
                    notifyDataSetChanged(); // update the data
                } else {
                    counter--; // decrement the click counter
                    //state how many clicks are left in the toast menu
                    message = "To reset, click \"Reset\" " + counter + " more times";
                    //change the reset button text to indicate how many clicks are left.
                    btnReset.setText("   Reset [" + counter + "]   ");
                }
                Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    // check to see if the boat has not exceeded any time limitations
    private boolean checkAgainstTimeLimit(Result result) {
        DateTime classFinishTime = BoatStartingListClass.getClassFinishTime(result.getBoatClass());
        Log.i(LOGTAG, "checkAgainstTimeLimit: ClassFinishTime =  " + classFinishTime);
        String now = GlobalContent.dateTimeToString(DateTime.now());// convert to string to remove date portion
        String boatFinishTimeStr = result.getResultsBoatFinishTime();

        DateTime nowDT = GlobalContent.toDateTime(now);

        // check if first finish time was recorded
        if (classFinishTime != null) {
//            Log.i(LOGTAG, "checkAgainstTimeLimit> ClassFinishTime not null");
            DateTime callAtThisTime = classFinishTime.plusSeconds(sp.getInt("finishTimeLimit", 1800));
            // check if finish time was recorded
            if (boatFinishTimeStr != null) {
//                Log.i(LOGTAG, "checkAgainstTimeLimit> boatFinishTimeStr not null");
                DateTime boatFinishTime = GlobalContent.toDateTime(boatFinishTimeStr);
                //check the boats finish time against the time limit
                if (boatFinishTime.isAfter(callAtThisTime)) {
//                    Log.i(LOGTAG, "checkAgainstTimeLimit> boatFinishTime.isAfter(callAtThisTime)");
                    return true;
                }
            } else {
                // check current time against class' first finish time
                if (nowDT.isAfter(callAtThisTime)) {
                    return true;
                }
            }
        }
        return false;

    }

    public void updateFirstFinishers() {
        // check if the race is alive and running
        if (GlobalContent.activeRace != null) {
            // set first finish time
            for (BoatClass bc : BoatStartingListClass.BOAT_CLASS_START_ARRAY) {
                // if the boat class in the list matches the current boat's color
                Result r = resultDataSource.getFirstClassResult(GlobalContent.getRaceRowID(),
                        bc.getBoatColor());
                if (r != null) {
                    // see if there is any need to update the first finishers.
                    if (bc.checkAndSetFirstFinish(r.getResultsBoatFinishTime(), r.getResultsBoatId())) {
                        Log.i(LOGTAG, "updateFirstFinishers: Class: " + bc.getBoatColor());
                        long timeLimitMillis = sp.getInt("finishTimeLimit", 1800) * 1000;
                        long bufferMillis = 700;

                        if (bc.callAtRunnable != null) {
                            // if a runnable already exists, remove it
                            handler.removeCallbacks(bc.callAtRunnable);
                            Log.i(LOGTAG, "updateFirstFinishers > removing callback for Class: " + bc.getBoatColor());
                        }
                        final BoatClass fbc = bc;
                        // create new event to be scheduled
                        bc.callAtRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // refresh the view
                                try {
                                    if (resultDataSource.isOpen()) {
                                        Log.i(LOGTAG, "callatrunnable called and resultDataSource is OPEN");
                                        syncArrayListWithSql();
                                        ResultsMenu.myList.invalidateViews();
                                        notifyDataSetChanged();
                                    } else {
                                        Log.i(LOGTAG, "callatrunnable called and resultDataSource is CLOSED");
                                        resultDataSource.open();
                                        syncArrayListWithSql();
                                        ResultsMenu.myList.invalidateViews();
                                        notifyDataSetChanged();
                                        resultDataSource.close();
                                    }
                                } catch (Exception e) {
                                    Log.i(LOGTAG, "bc.callAtRunnable ERROR >ResultDataSource Closed");
                                }
                                Log.i(LOGTAG, "Runnable Called: " + fbc.getBoatColor());
                            }
                        };
                        Log.i(LOGTAG, "PostDealying Runnable " + bc.getBoatColor() + " seconds to refresh: " + ((timeLimitMillis + bufferMillis)/1000));
                        handler.postDelayed(bc.callAtRunnable, (timeLimitMillis + bufferMillis));
                    }
                }

            }
        }
    }


    // sync the list in the ResultsAdapter with what is in the Results SQL table.
    public void syncArrayListWithSql() {
        //get the results from sql
        List<Result> tempResultFromSql = ResultsMenu.getAllSQLResultResults(resultDataSource);
        // Make sure the data coming from sql isn't blank. Otherwise throw error
        if (tempResultFromSql.size() > 0) {
            this.arraylist.clear(); //empty out the array list
            this.arraylist.addAll(tempResultFromSql);// add the temp sql stuff to the array list
        } else {
            throw new NullPointerException("Data in tempResultFromSql is empty");
        }
        notifyDataSetChanged(); // force refresh of the listview
    }

}
