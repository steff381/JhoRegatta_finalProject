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

    private static final String LOGTAG = "Logtag: TimePickerDialog";

    // instances of number pickers
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;

    // holders for time elements
    private long hour, minute, second;
    private String[] splitTime;

    // buttons for the dialog
    private Button setTime;
    private Button cancel;

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
        String time = bundle.getString("Time"); // get the specific argument
        View rootView = inflater.inflate(R.layout.fragment_time_picker_dialog, container,
                false);
        setCancelable(false); // disable cancallability
        getDialog().setTitle("HH:MM:SS"); // name the dialog

        // wire widgets
        setTime = (Button) rootView.findViewById(R.id.btn_tpd_set_time);
        cancel = (Button) rootView.findViewById(R.id.btn_tpd_cancel);

        // wire the number pickers
        hourPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_hour);
        minutePicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_minute);
        secondPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_second);

        // split the time from the bundle into a string array
        splitTime = time.split(":");

        //assign the onclick listeners
        setTime.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // set numeric boundaries for number pickers
        hourPicker.setMaxValue(12);
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
            // convert integers to milliseconds of hours minutes and seconds
            hour = 3600000 * hourPicker.getValue();
            minute = 60000 * minutePicker.getValue();
            second = 1000 * secondPicker.getValue();

            //add all milliseconds up to a total time
            long totalMillis = hour + minute + second;

            //pass the duration/elapsed time as a string back to the activity
            communicator.onDialogMessage(GlobalContent.convertMillisToElapsedTime(totalMillis));
            dismiss(); // dismiss the dialog
        } else
        {
            dismiss(); // just close the dialog
        }

    }

    //create an interface to handle interactivity communication
    interface Communicator {
        public void onDialogMessage(String message);
    }
}
