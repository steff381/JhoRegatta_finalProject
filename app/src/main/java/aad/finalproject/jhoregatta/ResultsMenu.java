package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import aad.finalproject.db.RaceDataSource;
import aad.finalproject.db.ResultDataSource;


public class ResultsMenu extends ActionBarActivity {
    private static final String LOGTAG = "Logtag: " + Thread.currentThread()
            .getStackTrace()[2].getClassName(); // log tag for records

    //instance of data source
    RaceDataSource raceDataSource;
    ResultDataSource resultDataSource;

    // make a listview instance
    ListView lvResultList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_menu);

        //wire data source and open
        raceDataSource = new RaceDataSource(this);
        resultDataSource = new ResultDataSource(this);
        raceDataSource.open();
        resultDataSource.open();

        // wire list view
        lvResultList = (ListView) findViewById(R.id.lvResultList);

        //set onclick listening for listview
        lvResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalContent.setResultsRowID(id);
                Intent intent = new Intent(view.getContext(), ResultsEditForm.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results_menu, menu);
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
