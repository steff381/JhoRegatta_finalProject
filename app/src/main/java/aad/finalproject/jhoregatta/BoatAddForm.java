package aad.finalproject.jhoregatta;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class BoatAddForm extends BoatMenu {

    Spinner boatClassSpn;
    EditText boatName;
    EditText boatSailNum;
    EditText boatPHRF;

    String strBoatClassSpn;
    String strBoatName;
    String strBoatSailNum;
    String strBoatPHRF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_add_form);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_boat_add_form, menu);
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

    @Override
    public void onClickCancel(View view){
        finish();
    }

    public void onClickAddBoat(View view){

        boatClassSpn = (Spinner) findViewById(R.id.spn_BoatClass);
        boatName = (EditText) findViewById(R.id.txt_inpt_BoatName);
        boatSailNum = (EditText) findViewById(R.id.txt_inpt_SailNum);
        boatPHRF = (EditText) findViewById(R.id.txt_inpt_PHRF);

        strBoatClassSpn = boatClassSpn.getSelectedItem().toString();
        strBoatName = boatName.getText().toString();
        strBoatSailNum = boatSailNum.getText().toString();
        strBoatPHRF = boatPHRF.getText().toString();

        if (strBoatName.length() != 0
                && strBoatPHRF.length() != 0
                && strBoatSailNum.length() != 0) {
            if(!strBoatClassSpn.equals("Please Select a Class")){

                //TODO Place insert into SQLite database BOATS statement here
                Log.i("BoatAddForm ","Validated add entry" );
                this.finish();
            } else {
                Toast.makeText(getApplicationContext(),"Please select a Boat Class",
                        Toast.LENGTH_LONG).show();
                Log.i("BoatAddForm ","No class selected" );
            }
        } else {
            Toast.makeText(BoatAddForm.this,"All fields are required", Toast.LENGTH_LONG).show();
            Log.i("BoatAddForm ","Missing fields" );
        }

    }
}
