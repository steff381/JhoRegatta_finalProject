package aad.finalproject.jhoregatta;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.ResultDataSource;


public class ResultsEditForm extends Form implements TimePickerDialog.Communicator{

    private Button btnFinishTime;
    static final int dialog_id = 0;
    int hour, minute, second;
    private TextView time;
    private String timeInitialElapsed;

    ResultDataSource resultDataSource;
    RaceDataSource raceDataSource;

    // instances of buttons
    Button update;
    Button setTime;
    Button cancel;
    CheckBox isEditedElapsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_edit_form);

        resultDataSource = new ResultDataSource(this);
        resultDataSource.open();
        raceDataSource = new RaceDataSource(this);
        raceDataSource.open();

        time = (TextView) findViewById(R.id.txt_JODADURATION);
        setTime = (Button) findViewById(R.id.btn_setText);

        timeInitialElapsed = time.getText().toString();


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

    public void showDialog(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Time", time.getText().toString());
        FragmentManager manager = getFragmentManager();
        TimePickerDialog picker = new TimePickerDialog();
        picker.setArguments(bundle);

        picker.show(manager, "HH:MM:SS");

    }

    @Override
    public void onDialogMessage(String message) {
        //get the message and initial time in millis
        long timeInitialMillis = GlobalContent.getDurationInMillis(timeInitialElapsed);
        long timeMessageMillis = GlobalContent.getDurationInMillis(message);

        // check to see if the time value actually changed
        if (timeInitialMillis != timeMessageMillis) {
            time.setText(message); // set text to the new time
            isEditedElapsed.setChecked(true);
        } else {
            time.setText(timeInitialElapsed); // make sure the value remains unchanged
            isEditedElapsed.setChecked(false);
        }

    }

}
