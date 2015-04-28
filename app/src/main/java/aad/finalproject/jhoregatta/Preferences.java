package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Preferences extends ActionBarActivity {

//    public static TextView polymorphicTextView; //TODO use this later

    //for testing
    EditText initialDelay, classUp, classUpPrepUp, classUpPrepDown, postRecallDelay;
    Button setAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        initialDelay = (EditText) findViewById(R.id.txt_prefs_INT_initialDelay);
        classUp = (EditText) findViewById(R.id.txt_prefs_INT_classUp);
        classUpPrepUp = (EditText) findViewById(R.id.txt_prefs_INT_classUpPrepUp);
        classUpPrepDown = (EditText) findViewById(R.id.txt_prefs_INT_classUpPrepDown);
        postRecallDelay = (EditText) findViewById(R.id.txt_prefs_INT_afterRecall);

        try {
            initialDelay.setText(GlobalContent.secondsUntilClassFlagUp);
            classUp.setText(GlobalContent.secondsUntilPrepFlagUp);
            classUpPrepUp.setText(GlobalContent.secondsUntilPrepFlagDown);
            classUpPrepDown.setText(GlobalContent.secondsUntilClassFlagDown);
            postRecallDelay.setText(GlobalContent.secondsUntilRestartFromRecall);
        } catch (Exception e) {
            Toast.makeText(this, " assigning values to text fields error", Toast.LENGTH_LONG).show();
        }

        setAll = (Button) findViewById(R.id.btn_prefs_set_all);
        setAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GlobalContent.secondsUntilClassFlagUp =
                            Integer.parseInt(initialDelay.getText().toString());
                    GlobalContent.secondsUntilPrepFlagUp =
                            Integer.parseInt(classUp.getText().toString());
                    GlobalContent.secondsUntilPrepFlagDown =
                            Integer.parseInt(classUpPrepUp.getText().toString());
                    GlobalContent.secondsUntilClassFlagDown =
                            Integer.parseInt(classUpPrepDown.getText().toString());
                    GlobalContent.secondsUntilRestartFromRecall =
                            Integer.parseInt(postRecallDelay.getText().toString());
                    Toast.makeText(v.getContext(), " Values Assigned", Toast.LENGTH_LONG).show();
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(v.getContext(), " Parsing ints error", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
        return true;
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
