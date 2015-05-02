package aad.finalproject.jhoregatta;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;


public class TimePickerDialog extends DialogFragment implements View.OnClickListener {

    private static final String LOGTAG = "Logtag: TimePickerDail";

    // instances of number pickers
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;


    // create a new communicator interface to communicate between activity and dialog
    Communicator communicator;
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
        View rootView = inflater.inflate(R.layout.fragment_time_picker_dialog, container,
                false);
        setCancelable(false); // disable cancallability
        getDialog().setTitle("HH:MM:SS"); // name the dialog

        // wire widgets
        Button setTime = (Button) rootView.findViewById(R.id.btn_tpd_set_time);
        Button cancel = (Button) rootView.findViewById(R.id.btn_tpd_cancel);

        // wire the number pickers
        hourPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_hour);
        minutePicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_minute);
        secondPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_second);

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

        return rootView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tpd_set_time) {
            hourPicker.clearFocus();
            minutePicker.clearFocus();
            secondPicker.clearFocus();
            // convert integers to milliseconds of hours minutes and seconds
            long hour = 3600000 * hourPicker.getValue();
            long minute = 60000 * minutePicker.getValue();
            long second = 1000 * secondPicker.getValue();

            //add all milliseconds up to a total time
            long totalMillis = hour + minute + second;

            //pass the duration/elapsed time as a string back to the activity
            communicator.onDialogMessage(totalMillis);
            dismiss(); // dismiss the dialog
        } else {
            dismiss(); // just close the dialog
        }

    }

    //create an interface to handle messages to the activity
    interface Communicator {
        public void onDialogMessage(long message);
    }
}
