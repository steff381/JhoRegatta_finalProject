package aad.finalproject.jhoregatta;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import aad.finalproject.db.Result;
import aad.finalproject.db.ResultDataSource;


public class ClassFinishesDialog extends DialogFragment {

    private static final String LOGTAG = "ClassFinishesDialog";

    private ResultDataSource resultDataSource; // access data source

    private TextView sailingCt, finishedCt, allBoatCt;

    // array lists for text boxes
    private ArrayList<TextView> classArrayList;
    private ArrayList<TextView> finishArrayList;
    private ArrayList<TextView> callAtArrayList;
    private ArrayList<TextView> sailFinArrayList;
    private SharedPreferences sp;

    private View view; // view to use with getviewbyId.

    public ClassFinishesDialog() {
    }

    @Override
    public void onDestroy() {
        super.onPause();
        resultDataSource.close(); // close data source at the end
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_class_finishes_dialog, container, false);
        // check to see if the dialog attached to the activity properly
        if (getActivity() != null) {
            //create a data source connection
            resultDataSource = new ResultDataSource(getActivity());
            resultDataSource.open();
            sp = getActivity().getSharedPreferences(Preferences.PREFS, 0);
            getDialog().setTitle("Race Stats"); // name the dialog
            setCancelable(false);
        }

        wireWidgets(); // wire the array lists to the text views

        // get counts of boats
        int stillSailingInt = resultDataSource.countActivelyRacingBoats();
        int finishedBoatsInt = resultDataSource.countFinishedBoats();
        int totalCt = resultDataSource.countAllBoats();

        // assign counts to text views
        sailingCt.setText(stillSailingInt + "");
        finishedCt.setText(finishedBoatsInt + "");
        allBoatCt.setText(totalCt + "");

        for (int i = 0; i < BoatStartingListClass.BOAT_CLASS_START_ARRAY.size(); i++) {
            // get the boats class's color from the starting list
            String bc = BoatStartingListClass.BOAT_CLASS_START_ARRAY.get(i).getBoatColor();
            // grab the result
            Result r = resultDataSource.getFirstClassResult(GlobalContent.getRaceRowID(),bc);

            int stillSailingCtByClass = resultDataSource.countActivelyRacingBoatsByClass(bc);
            int finishedBoatsCtByClass = resultDataSource.countFinishedBoatsByClass(bc);
            String callAtStringTime;
            String finishTime = null;
            if (r != null) {

                Log.i(LOGTAG, "\n      r.class    " + r.getBoatClass()
                                + "\n       r.Boat    " + r.getBoatName()
                                + "\n      r.start    " + r.getResultsClassStartTime()
                                + "\n     r.finish    " + r.getResultsBoatFinishTime()
                                + "\n   r.duration    " + r.getResultsDuration()
                );

                try {
                    // get the first finish time of the boat in the given class. Then add n minutes.
                    // this will be the time at which all boats in that class must have finished the race
                    DateTime callAtDateTime = GlobalContent.toDateTime(r.getResultsBoatFinishTime())
                    .plusSeconds(sp.getInt(Preferences.FINISH_TIME_LIMIT, 1800));
                    //convert the date time into a string to make it easier to enter into textviews
                    callAtStringTime = GlobalContent.dateTimeToString(callAtDateTime);
                    finishTime = r.getResultsBoatFinishTime();
                } catch (Exception e) {
                    callAtStringTime = "ERROR";
                    Log.i(LOGTAG, "ERROR getting DATETIME string; finish time = "
                            + r.getResultsBoatFinishTime());
                }

                //assign values from result
                finishArrayList.get(i).setText(finishTime);
                callAtArrayList.get(i).setText(callAtStringTime);
            } else {
                Log.i(LOGTAG, bc + " is Null");
                // if no boats from the class have finished display this result
            }

            // display the class and boats starting and finished for the given class
            classArrayList.get(i).setText(bc);
            sailFinArrayList.get(i).setText(stillSailingCtByClass + " / " + finishedBoatsCtByClass);

            //make column visible
            classArrayList.get(i).setVisibility(View.VISIBLE);
            finishArrayList.get(i).setVisibility(View.VISIBLE);
            callAtArrayList.get(i).setVisibility(View.VISIBLE);
            sailFinArrayList.get(i).setVisibility(View.VISIBLE);

        }

        // Inflate the layout for this fragment
        return view;
    }

    // wire all widgets to the xml elements
    private void wireWidgets() {

        sailingCt = (TextView) view.findViewById(R.id.txt_fcfd_sailingCt);
        finishedCt = (TextView) view.findViewById(R.id.txt_fcfd_finishedCt);
        allBoatCt = (TextView) view.findViewById(R.id.txt_fcfd_totalCt);

        sailFinArrayList = new ArrayList<>();
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin1));
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin2));
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin3));
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin4));
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin5));
        sailFinArrayList.add((TextView) view.findViewById(R.id.txt_cfd_sailFin6));

        classArrayList = new ArrayList<>();
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class1));
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class2));
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class3));
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class4));
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class5));
        classArrayList.add((TextView) view.findViewById(R.id.txt_cfd_class6));

        finishArrayList = new ArrayList<>();
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin1));
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin2));
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin3));
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin4));
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin5));
        finishArrayList.add((TextView) view.findViewById(R.id.txt_cfd_firstFin6));

        callAtArrayList = new ArrayList<>();
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt1));
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt2));
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt3));
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt4));
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt5));
        callAtArrayList.add((TextView) view.findViewById(R.id.txt_cfd_callAt6));

        Button btn = (Button) view.findViewById(R.id.btn_cfd_return);

        //assign click event to return button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
