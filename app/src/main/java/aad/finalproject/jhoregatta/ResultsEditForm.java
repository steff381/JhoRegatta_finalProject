package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.ResultDataSource;


public class ResultsEditForm extends Form {

    private Button btnFinishTime;
    static final int dialog_id = 0;
    int hour, minute, second;
    private TextView time;

    ResultDataSource resultDataSource;
    RaceDataSource raceDataSource;

    // instances of buttons
    Button update;
    Button setTime;
    Button cancel;

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
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDataSource.runCalculations();
            }
        });

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
}
