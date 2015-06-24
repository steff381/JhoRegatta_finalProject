package aad.finalproject.jhoregatta;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


public class TimePickerDialog extends DialogFragment implements View.OnClickListener {

    private static final String LOGTAG = "TimePickerDialog";

    // instances of number pickers
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;

    private TextView minTime;
    private TextView maxTime;

    private String pref;
    private int min;
    private int max;

    private int hrNewVal;
    private int mnNewVal;
    private int seNewVal;

    private boolean isPrefDialog = true;

    // create a new communicator interface to communicate between activity and dialog
    private Communicator communicator;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments(); // get the arguments from the bundle
        String time = bundle.getString("Value1"); // get the specific argument
        pref = bundle.getString("prefKey"); // get the specific argument
        min = bundle.getInt("min");
        max = bundle.getInt("max");

        View rootView = inflater.inflate(R.layout.fragment_time_picker_dialog, container,
                false);
        setCancelable(false); // disable cancallability
        getDialog().setTitle("HH:MM:SS"); // name the dialog

        if (pref == null) {
            isPrefDialog = false;
        }
        // wire widgets
        Button setTime = (Button) rootView.findViewById(R.id.btn_tpd_set_time);
        Button cancel = (Button) rootView.findViewById(R.id.btn_tpd_cancel);

        // wire the number pickers
        hourPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_hour);
        minutePicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_minute);
        secondPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_second);

        //wire textviews
        minTime = (TextView) rootView.findViewById(R.id.txt_ftpd_min_time);
        maxTime = (TextView) rootView.findViewById(R.id.txt_ftpd_max_time);

        if (isPrefDialog) {
            // enter the min and max times into the text views for user to see
            minTime.setText(GlobalContent.convertMillisToFormattedTime((min * 1000), 1));
            maxTime.setText(GlobalContent.convertMillisToFormattedTime((max * 1000), 1));
        } else {
            // if the dialog is not being accessed by the preferences activity, hide min/max times
            minTime.setVisibility(View.GONE);
            maxTime.setVisibility(View.GONE);
        }

        // split the time from the bundle into a string array
        String[] splitTime = time.split(":");

        //assign the onclick listeners
        setTime.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // set numeric boundaries for number pickers
        hourPicker.setMaxValue(99);
        hourPicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);

        //set the values in the number pickers based on the bundle data passed in
        hourPicker.setValue(Integer.parseInt(splitTime[0]));
        minutePicker.setValue(Integer.parseInt(splitTime[1]));
        secondPicker.setValue(Integer.parseInt(splitTime[2]));

        hrNewVal = hourPicker.getValue();
        mnNewVal = minutePicker.getValue();
        seNewVal = secondPicker.getValue();

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hrNewVal = newVal;
                checkMinMaxLimits();
            }
        });

        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mnNewVal = newVal;
                checkMinMaxLimits();

            }
        });

        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                seNewVal = newVal;
                checkMinMaxLimits();
            }
        });


        return rootView;
    }

    private void checkMinMaxLimits() {
        if (isPrefDialog) {

            int total = (hrNewVal * 3600  + mnNewVal * 60 + seNewVal);

            if (total < min) {
                Log.i(LOGTAG, "Under Min: " + min + " Total: " + total);
                minTime.setBackgroundColor(getResources().getColor(R.color.red10));
                maxTime.setBackgroundColor(Color.parseColor("#00000000"));
            } else if (total > max) {
                Log.i(LOGTAG, "Over Max: " + max + " Total: " + total);

                maxTime.setBackgroundColor(getResources().getColor(R.color.red10));
                minTime.setBackgroundColor(Color.parseColor("#00000000"));
            } else {
                Log.i(LOGTAG, "All Good,  Under MAX: " + max + "Over Min: " + min + " Total: " + total);
                maxTime.setBackgroundColor(Color.parseColor("#00000000"));
                minTime.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tpd_set_time) {

            //clear focus in case the user hasn't clicked away from the current text box
            hourPicker.clearFocus();
            minutePicker.clearFocus();
            secondPicker.clearFocus();

            // convert integers to seconds of hours minutes and seconds
            long hour = 3600000 * hourPicker.getValue();
            long minute = 60000 * minutePicker.getValue();
            long second = 1000 * secondPicker.getValue();

            //add all seconds up to a total time
            long totalMillis = hour + minute + second;

            if (!isPrefDialog || isValid(totalMillis)) {
                //pass the duration/elapsed time as a string back to the activity
                communicator.onDialogMessage(totalMillis);
                dismiss(); // dismiss the dialog
            } else {
                long totalSeconds = totalMillis /1000;
                if (totalSeconds < min) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Warning, time must be greater than or equal to "
                                    + GlobalContent.convertMillisToFormattedTime((min * 1000), 1)
                            , Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Warning, time must be less than " + GlobalContent
                                    .convertMillisToFormattedTime((max * 1000), 1), Toast.LENGTH_LONG)
                                .show();

                }

            }

        } else {
            dismiss(); // just close the dialog
        }

    }

    private boolean isValid(long totalMillis) {
        long totalSeconds = totalMillis / 1000;
        return !(totalSeconds < min || totalSeconds > max);
    }

    //create an interface to handle messages to the activity
    interface Communicator {
        public void onDialogMessage(long message);
    }
}
