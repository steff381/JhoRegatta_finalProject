package aad.finalproject.jhoregatta;

import android.content.SharedPreferences;
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

    public static final String PREFS = "TimePrefs";

    //for testing
    EditText initialDelay, classUp, classUpPrepUp, classUpPrepDown, postRecallDelay;
    Button setAll;

    //create vars to hold the values from the text boxes in integer format
    private int initialDelayVar, classUpVar, classUpPrepUpVar,
            classUpPrepDownVar, postRecallDelayVar;

    //create shared preferences and an editor
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        sharedPreferences = getSharedPreferences(PREFS, 0);
        editor = sharedPreferences.edit();

        //wire up the text boxes
        initialDelay = (EditText) findViewById(R.id.txt_prefs_INT_initialDelay);
        classUp = (EditText) findViewById(R.id.txt_prefs_INT_classUp);
        classUpPrepUp = (EditText) findViewById(R.id.txt_prefs_INT_classUpPrepUp);
        classUpPrepDown = (EditText) findViewById(R.id.txt_prefs_INT_classUpPrepDown);
        postRecallDelay = (EditText) findViewById(R.id.txt_prefs_INT_afterRecall);

        try {
            // load the text boxes with the stored values
            initialDelay.setText(String.valueOf(sharedPreferences.getInt("initialDelay", 15)));
            classUp.setText(String.valueOf(sharedPreferences.getInt("classUp", 65)));
            classUpPrepUp.setText(String.valueOf(sharedPreferences.getInt("classUpPrepUp", 185)));
            classUpPrepDown.setText(String.valueOf(sharedPreferences.getInt("classUpPrepDown", 65)));
            postRecallDelay.setText(String.valueOf(sharedPreferences.getInt("postRecallDelay", 365)));
        } catch (Exception e) {
            Toast.makeText(this, " assigning values to text fields error", Toast.LENGTH_LONG).show();
        }

        setAll = (Button) findViewById(R.id.btn_prefs_set_all);
        setAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // put the variable versions of the text values into containers
                    initialDelayVar = Integer.parseInt(initialDelay.getText().toString());
                    classUpVar = Integer.parseInt(classUp.getText().toString());
                    classUpPrepUpVar = Integer.parseInt(classUpPrepUp.getText().toString());
                    classUpPrepDownVar = Integer.parseInt(classUpPrepDown.getText().toString());
                    postRecallDelayVar = Integer.parseInt(postRecallDelay.getText().toString());


                    // place the new values into the shared preferences editor
                    editor.putInt("initialDelay", initialDelayVar);
                    editor.putInt("classUp", classUpVar);
                    editor.putInt("classUpPrepUp", classUpPrepUpVar);
                    editor.putInt("classUpPrepDown", classUpPrepDownVar);
                    editor.putInt("postRecallDelay", postRecallDelayVar);

                    editor.apply(); //commit the changes


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
