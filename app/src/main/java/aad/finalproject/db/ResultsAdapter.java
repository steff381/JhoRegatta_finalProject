package aad.finalproject.db;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import java.util.Locale;

import aad.finalproject.jhoregatta.GlobalContent;
import aad.finalproject.jhoregatta.R;

/**
 * Created by Daniel on 4/20/2015.
 */
public class ResultsAdapter extends BaseAdapter {
    private static String LOGTAG = "Logtag: " + Thread.currentThread()
            .getStackTrace()[2].getClass().getSimpleName(); // log tag for records
    Context mContext; // add context
    LayoutInflater inflater; // instance of inflater
    ResultDataSource resultDataSource;
    // lists of result
    private List<Result> mainDataList = null;
    public static ArrayList<Result> arraylist;

    // instance constructor
    public ResultsAdapter(Context context, List<Result> mainDataList,
                          ResultDataSource resultDataSource) {
        this.mainDataList= mainDataList;
        this.resultDataSource = resultDataSource;
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        // create a copy of the main list so we don't end up damagaing the original list
        ResultsAdapter.arraylist = new ArrayList<>();
        ResultsAdapter.arraylist.addAll(mainDataList);

    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return arraylist.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return arraylist.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param index    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */

    @Override
    public View getView( int index, View view, final ViewGroup parent) {

        if (view == null) {
            // build inflater to create a new row for each row in the Results table
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.activity_list_template_results, parent, false);
        }



        Log.i("Results Adapter ", "mainDataList is of size " + arraylist.size() + " index is " + index);
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
        if (r.getResultsBoatFinishTime() != null) {

            //hide the finish button and show the reset button instead.
            btnFinish.setVisibility(View.GONE);
            btnReset.setVisibility(View.VISIBLE);
            // set the text of the reset button. Click count to reset is 3
            btnReset.setText("   Reset [3]   ");
            //set the row color to red
            view.setBackgroundColor(view.getResources().getColor(R.color.red06));
            //set the text of each text box white
            for (TextView t : textViews) {
                t.setTextColor(Color.parseColor("#ffffff")); // make the text white
            }
        } else {
            // change the row color blank
            view.setBackgroundColor(Color.parseColor("#00000000"));
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
                    if (result.getResultsClassStartTime() != null) {
                        DateTime dateTime = DateTime.now();// capture the current time

                        //format time as string for SQL
                        String timeFormatted = GlobalContent.dateTimeToString(dateTime);

                        //update the result's finish time.
                        resultDataSource.updateSingleFinishTime(result.getResultsId(), timeFormatted);

                        //run table calculations to derive duration and adjusted duration
                        resultDataSource.runCalculations();

                        //set the result entry's finish time to the same.
                        result.setResultsBoatFinishTime(timeFormatted);
                        Log.i(LOGTAG, "Finish time for " + result.getBoatName() + " is: " + dateTime + " formatted: " + timeFormatted);

                        //hide the finish button and show the reset button instead.
                        btnFinish.setVisibility(View.GONE);
                        btnReset.setVisibility(View.VISIBLE);

                        // set the text of the reset button. Click count to reset is 3
                        btnReset.setText("   Reset [3]   ");

                        //set the row color to red
                        finalView.setBackgroundColor(v.getResources().getColor(R.color.red06));

                        //set the text of each text box white
                        for (TextView t : textViews) {
                            t.setTextColor(Color.parseColor("#ffffff")); // make the text white
                        }

                        // get the start time for the class
                        String startTime = result.getResultsClassStartTime();

                       //get the finish time ""
                        GlobalContent.getElapsedTime(startTime, timeFormatted);
                        notifyDataSetChanged();
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
//                    resultDataSource.updateSingleFinishTime(result.getResultsId(), null);
                    resultDataSource.clearStartAndDurations(result.getResultsId());


                    //blank out result object fnish time entry
                    result.setResultsBoatFinishTime(null);

                    //hide reset button and show finish button
                    btnReset.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.VISIBLE);
                    counter = 3; // reset counter

                    //output message
                    message = result.getBoatName() + "'s finish time is now RESET";

                    //set the row color to transparent
                    finalView.setBackgroundColor(Color.parseColor("#00000000"));

                    // make each text box text black
                    for (TextView t : textViews) {
                        t.setTextColor(Color.parseColor("#000000")); // make the text black
                    }
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

    // sync the list in the ResultsAdapter with what is in the Results SQL table.
    public static void syncArrayListWithSql(ResultDataSource resultDataSource) {
        //create statement strings
        String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.activeRace.getId()
                + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        String orderBy = DBAdapter.KEY_BOAT_CLASS + ", "
                + DBAdapter.KEY_BOAT_NAME + " DESC";
        // create a temporary placeholder for the data from SQL
        List<Result> tempResultFromSql;
        tempResultFromSql = resultDataSource.getAllResults(where, orderBy,
                null);
        //TODO For testing
        for (Result r : tempResultFromSql) {
            Log.i(LOGTAG, "boat " + r.getBoatName());
        }
        // Make sure the data coming from sql isn't blank. Otherwise throw error
        if (tempResultFromSql.size() > 0) {
            ResultsAdapter.arraylist.clear(); // clear the current list
            ResultsAdapter.arraylist.addAll(tempResultFromSql);
        } else {
            throw new NullPointerException("Data in tempResultFromSql is empty");
        }
    }

    //filter results based on text entered into text box in the Results menu
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        try {
            mainDataList.clear();
        } catch (Exception e) {
            Log.i(LOGTAG, "Exception =------------------------------------------------------------");
        }
        if (charText.length() == 0) {
            mainDataList.addAll(arraylist);
        } else {
            for (Result result : arraylist) {
                if (result.getBoatName().toLowerCase(Locale.getDefault()).contains(charText) ||
                    result.getBoatSailNum().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mainDataList.add(result);
                }
            }
        }
        notifyDataSetChanged();
    }
}
