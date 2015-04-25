package aad.finalproject.jhoregatta;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.joda.time.DateTime;


public class TimePickerDialog extends DialogFragment {

    private static final String LOGTAG = "Logtag: " + Thread.currentThread().getStackTrace()[2]
            .getClass().getSimpleName();

    private TextView textView;

    private NumberPicker hourPicker;
    private NumberPicker minuitePicker;
    private NumberPicker secondPicker;

    private Button setTime;
    private Button cancel;

    ///am pm toggle button
    private ToggleButton amPm;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_time_picker_dialog, container,
                false);

        getDialog().setTitle("HH:MM:SS");

        // wire widgets
        setTime = (Button) rootView.findViewById (R.id.btn_tpd_set_time);
        cancel = (Button) rootView.findViewById (R.id.btn_tpd_cancel);
        hourPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_hour);
        minuitePicker= (NumberPicker) rootView.findViewById(R.id.np_tpd_minute);
        secondPicker = (NumberPicker) rootView.findViewById(R.id.np_tpd_second);
        amPm = (ToggleButton) rootView.findViewById(R.id.tb_tpd_amPm);

        // set numeric boundaries for number pickers
        hourPicker.setMaxValue(12);
        hourPicker.setMinValue(0);
        minuitePicker.setMaxValue(59);
        minuitePicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);



        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime lt = GlobalContent.toDateTime(hourPicker.getValue() + ":"
                        + minuitePicker.getValue() + ":" + secondPicker.getValue());
                textView.setText(GlobalContent.dateTimeToString(lt));
                String timeText = textView.getText().toString();

                Log.i(LOGTAG, "time is: " + timeText);
            }
        });





        return rootView;
    }

    public void setTextView(TextView textView){
        this.textView = textView;
    }

    public void cancel(View view) {
        this.dismiss();
    }

}
